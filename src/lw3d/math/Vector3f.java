package lw3d.math;

public class Vector3f {
	public float x, y, z;
	
	public Vector3f()
	{
		x = y = z = 0f;
	}
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f add(Vector3f vec) {
		return new Vector3f(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
	
	public Vector3f mult(float scalar) {
		return new Vector3f(scalar * this.x, scalar * this.y, scalar * this.z);
	}
}
