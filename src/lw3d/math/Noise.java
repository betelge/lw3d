package lw3d.math;

import java.util.Random;

public class Noise implements Procedural{

	// Gradients
	static float g[] = {

		  1,1,0,    -1,1,0,    1,-1,0,    -1,-1,0,

		  1,0,1,    -1,0,1,    1,0,-1,    -1,0,-1,

		  0,1,1,    0,-1,1,    0,1,-1,    0,-1,-1,

		  1,1,0,    0,-1,1,    -1,1,0,    0,-1,-1,

		};
	
	Double[][][][] gradients = new Double[8][8][8][3]; 

	public Noise(long seed) {
		Random rand = new Random(seed);
		
		// Generate gradients
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				for(int k = 0; k < 8; k++) {
					
					int gradIndex = (int)Math.round(15*rand.nextFloat());
					
					// Generate a random gradient
					for(int l = 0; l < 3; l++) {
						/*gradients[i][j][k][l] =
							rand.nextDouble();*/
						
						gradients[i][j][k][l] = (double)g[ 3 * gradIndex + l ];
					}
					
					// Calculate the inverse norm
				/*	Double invNorm = 1.0 / Math.sqrt(
						gradients[i][j][k][0] * gradients[i][j][k][0]
						+ gradients[i][j][k][1] * gradients[i][j][k][1]
						+ gradients[i][j][k][2] * gradients[i][j][k][2] );
					
					// Normalize the gradient
					for(int l = 0; l < 3; l++) {
						gradients[i][j][k][l] *= invNorm;
					}
					*/
				}
	}
	
	public double getValue(double x, double y, double z, double resoultion) {
		return this.noise(x, y, z);
	}
	
	public double noise(double x, double y, double z) {		
		// Modulate the coordinates to the domain
		x %= 8;
		y %= 8;
		z %= 8;
		
		if(x < 0) x += 8;
		if(y < 0) y += 8;
		if(z < 0) z += 8;
		
		// The integer part (index of first gradient)
		int iX = (int) Math.floor(x),
			iY = (int) Math.floor(y),
			iZ = (int) Math.floor(z);
		
		// Vector with the first gradient as origin
		double modX = x % 1,
			modY = y % 1,
			modZ = z % 1;
		
		if(x < 0) x += 1;
		if(y < 0) y += 1;
		if(z < 0) z += 1;
		
		// Dot products
		double v000 = modX*gradients[iX][iY][iZ][0] + modY*gradients[iX][iY][iZ][1] + modZ*gradients[iX][iY][iZ][2],
			v001 = modX*gradients[iX][iY][(iZ+1)%7][0] + modY*gradients[iX][iY][(iZ+1)%7][1] + (modZ-1)*gradients[iX][iY][(iZ+1)%7][2],
		
			v010 = modX*gradients[iX][(iY+1)%7][iZ][0] + (modY-1)*gradients[iX][(iY+1)%7][iZ][1] + modZ*gradients[iX][(iY+1)%7][iZ][2],
			v011 = modX*gradients[iX][(iY+1)%7][(iZ+1)%7][0] + (modY-1)*gradients[iX][(iY+1)%7][(iZ+1)%7][1] + (modZ-1)*gradients[iX][(iY+1)%7][(iZ+1)%7][2],
		
			v100 = (modX-1)*gradients[(iX+1)%7][iY][iZ][0] + modY*gradients[(iX+1)%7][iY][iZ][1] + modZ*gradients[(iX+1)%7][iY][iZ][2],
			v101 = (modX-1)*gradients[(iX+1)%7][iY][(iZ+1)%7][0] + modY*gradients[(iX+1)%7][iY][(iZ+1)%7][1] + (modZ-1)*gradients[(iX+1)%7][iY][(iZ+1)%7][2],
		
			v110 = (modX-1)*gradients[(iX+1)%7][(iY+1)%7][iZ][0] + (modY-1)*gradients[(iX+1)%7][(iY+1)%7][iZ][1] + modZ*gradients[(iX+1)%7][(iY+1)%7][iZ][2],
			v111 = (modX-1)*gradients[(iX+1)%7][(iY+1)%7][(iZ+1)%7][0] + (modY-1)*gradients[(iX+1)%7][(iY+1)%7][(iZ+1)%7][1] + (modZ-1)*gradients[(iX+1)%7][(iY+1)%7][(iZ+1)%7][2];
		
		// Change of coordinates
		double 	sX = spline5(modX),
			sY = spline5(modY),
			sZ = spline5(modZ);
		
		double v00 = lin(v000, v001, sZ),
			v01 = lin(v010, v011, sZ),
			v10 = lin(v100, v101, sZ),
			v11 = lin(v110, v111, sZ);
		
		double v0 = lin(v00, v01, sY),
			v1 = lin(v10, v11, sY);

		return lin(v0, v1, sX);
	}
	
	private static double spline5(double x) {
		// 6x^5 - 15x^4 + 10x^3
		return x*x*x*(6*x*x - 15*x + 10);
	}
	
	private static double lin(double start, double end, double x) {
		return start + x * (end - start);
	}
}
