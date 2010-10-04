package lw3d.math;

public class Matrix4 {
	private float[] floats = new float[16];
	
	public Matrix4(float[] floats) {
		this.floats = floats;
	}
	
	public void setFloats(float[] floats) {
		this.floats = floats;
	}
	
	public float[] getFloats() {
		return floats;
	}
	
	public void setFloat(int index, float value) {
		if(index >= 0 && index < 16)
			floats[index] = value;
	}
	
	public float getFloat(int index) {
		if(index >= 0 && index < 16)
			return floats[index];
		else
			return 0;
	}
	
	public void multThis(Matrix4 m) {
		float[] newF = new float[16];
		float[] argF = m.getFloats();
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				newF[4*i+j] = floats[4*i]*argF[j] + floats[4*i+1]*argF[j+4]
				+ floats[4*i+2]*argF[j+2*4] + floats[4*i+3]*argF[j+3*4];
			}
		}
		
		floats = newF;
	}
	
	public Matrix4 mult(Matrix4 m) {
		Matrix4 newMatrix = new Matrix4(floats);
		newMatrix.multThis(m);
		return newMatrix;
	}
}
