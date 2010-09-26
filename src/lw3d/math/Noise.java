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
	

	public double getValueNormal(double x, double y, double z,
			double resolution, Vector3f normal) {
		return noise(x, y, z, normal);
	}
	
	public double noise(double x, double y, double z) {
		return this.noise(x, y, z, null);
	}

	public double noise(double x, double y, double z, Vector3f normal) {
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
		double modX = x - iX,
			modY = y - iY,
			modZ = z - iZ;
		
		// Gradients
		double gAx = gradients[iX][iY][iZ][0],
			gAy = gradients[iX][iY][iZ][1],
			gAz = gradients[iX][iY][iZ][2],
			
			gBx = gradients[iX][iY][(iZ+1)%8][0],
			gBy = gradients[iX][iY][(iZ+1)%8][1],
			gBz = gradients[iX][iY][(iZ+1)%8][2],
			
			gCx = gradients[iX][(iY+1)%8][iZ][0],
			gCy = gradients[iX][(iY+1)%8][iZ][1],
			gCz = gradients[iX][(iY+1)%8][iZ][2],
			
			gDx = gradients[iX][(iY+1)%8][(iZ+1)%8][0],
			gDy = gradients[iX][(iY+1)%8][(iZ+1)%8][1],
			gDz = gradients[iX][(iY+1)%8][(iZ+1)%8][2],
			
			gEx = gradients[(iX+1)%8][iY][iZ][0],
			gEy = gradients[(iX+1)%8][iY][iZ][1],
			gEz = gradients[(iX+1)%8][iY][iZ][2],
			
			gFx = gradients[(iX+1)%8][iY][(iZ+1)%8][0],
			gFy = gradients[(iX+1)%8][iY][(iZ+1)%8][1],
			gFz = gradients[(iX+1)%8][iY][(iZ+1)%8][2],
			
			gGx = gradients[(iX+1)%8][(iY+1)%8][iZ][0],
			gGy = gradients[(iX+1)%8][(iY+1)%8][iZ][1],
			gGz = gradients[(iX+1)%8][(iY+1)%8][iZ][2],
			
			gHx = gradients[(iX+1)%8][(iY+1)%8][(iZ+1)%8][0],
			gHy = gradients[(iX+1)%8][(iY+1)%8][(iZ+1)%8][1],
			gHz = gradients[(iX+1)%8][(iY+1)%8][(iZ+1)%8][2];

		// Dot products
		double vA = modX*gAx + modY*gAy + modZ*gAz,
			vB = modX*gBx + modY*gBy + (modZ-1)*gBz,
		
			vC = modX*gCx + (modY-1)*gCy + modZ*gCz,
			vD = modX*gDx + (modY-1)*gDy + (modZ-1)*gDz,
		
			vE = (modX-1)*gEx + modY*gEy + modZ*gEz,
			vF = (modX-1)*gFx + modY*gFy + (modZ-1)*gFz,
		
			vG = (modX-1)*gGx + (modY-1)*gGy + modZ*gGz,
			vH = (modX-1)*gHx + (modY-1)*gHy + (modZ-1)*gHz;
		
		// Change of coordinates
		double 	sX = spline5(modX),
			sY = spline5(modY),
			sZ = spline5(modZ);
		
		
		double k0 = vA, // 1
			k1 = vB - vA, // z
			k2 = vC - vA, // y
			k3 = vE - vA, // x
			k4 = vD - vC - vB + vA, // yz
			k5 = vF - vE - vB + vA, // xz
			k6 = vG - vE - vC + vA, // xy
			k7 = vH - vG - vF + vE - vD + vC + vB - vA; // xyz
		
		// Normal		
		if(normal != null) {
			
			// Spline derivatives
			double dsX = spline5derivative(modX),
				dsY = spline5derivative(modY),
				dsZ = spline5derivative(modZ);
			
			double dk0x = gAx,
				dk1x = gBx - gAx,
				dk2x = gCx - gAx,
				dk3x = gEx - gAx,
				dk4x = gDx - gCx - gBx + gAx,
				dk5x = gFx - gEx - gBx + gAx,
				dk6x = gGx - gEx - gCx + gAx,
				dk7x = gHx - gGx - gFx + gEx - gDx + gCx + gBx - gAx,
				
				dk0y = gAy,
				dk1y = gBy - gAy,
				dk2y = gCy - gAy,
				dk3y = gEy - gAy,
				dk4y = gDy - gCy - gBy + gAy,
				dk5y = gFy - gEy - gBy + gAy,
				dk6y = gGy - gEy - gCy + gAy,
				dk7y = gHy - gGy - gFy + gEy - gDy + gCy + gBy - gAy,
				
				dk0z = gAz,
				dk1z = gBz - gAz,
				dk2z = gCz - gAz,
				dk3z = gEz - gAz,
				dk4z = gDz - gCz - gBz + gAz,
				dk5z = gFz - gEz - gBz + gAz,
				dk6z = gGz - gEz - gCz + gAz,
				dk7z = gHz - gGz - gFz + gEz - gDz + gCz + gBz - gAz;
			
			normal.x = (float) (dk0x + dk1x*sZ + dk2x*sY + dk3x*sX
				+ dk4x*sY*sZ + dk5x*sX*sZ + dk6x*sX*sY + dk7x*sX*sY*sZ
				+ (k3 + k5*sZ + k6*sY + k7*sY*sZ) * dsX);
			
			normal.y = (float) (dk0y + dk1y*sZ + dk2y*sY + dk3y*sX
				+ dk4y*sY*sZ + dk5y*sX*sZ + dk6y*sX*sY + dk7y*sX*sY*sZ
				+ (k2 + k4*sZ + k6*sX + k7*sX*sZ) * dsY);
		
			normal.z = (float) (dk0z + dk1z*sZ + dk2z*sY + dk3z*sX
				+ dk4z*sY*sZ + dk5z*sX*sZ + dk6z*sX*sY + dk7z*sX*sY*sZ
				+ (k1 + k4*sY + k5*sX + k7*sX*sY) * dsZ);
		}
		
		// Value
	/*	double v00 = lin(vA, vB, sZ),
			v01 = lin(vC, vD, sZ),
			v10 = lin(vE, vF, sZ),
			v11 = lin(vG, vH, sZ);
		
		double v0 = lin(v00, v01, sY),
			v1 = lin(v10, v11, sY);

		return lin(v0, v1, sX);*/
		
		return k0 + k1*sZ + k2*sY + k3*sX
			+ k4*sY*sZ + k5*sX*sZ + k6*sX*sY
			+ k7*sX*sY*sZ;
	}
	
	private static double spline5(double x) {
		// 6x^5 - 15x^4 + 10x^3
		return x*x*x*(6*x*x - 15*x + 10);
	}
	
	private static double spline5derivative(double x) {
		// d/dx(6x^5 - 15x^4 + 10x^3) = 30x^4 - 60x^3 + 30x^2
		return x*x*(30*x*x - 60*x + 30);
	}
	
	private static double lin(double start, double end, double x) {
		return start + x * (end - start);
	}

}
