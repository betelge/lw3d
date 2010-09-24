
package lw3d.math;


public class Vector3f
{
	public float x, y, z;

	public final static Vector3f UNIT_X = new Vector3f(1, 0, 0);
	public final static Vector3f UNIT_Y = new Vector3f(0, 1, 0);
	public final static Vector3f UNIT_Z = new Vector3f(0, 0, 1);

	public Vector3f()
	{
		x = y = z = 0;
	}

	public Vector3f(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(Vector3f vector)
	{
		x = vector.x;
		y = vector.y;
		z = vector.z;
	}

	public void addThis(float x, float y, float z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void addThis(Vector3f vector)
	{
		this.x += vector.x;
		this.y += vector.y;
		this.z += vector.z;
	}

	/**
	 * Scales vector before adding it to this.
	 * 
	 * @param vector
	 * @param scale
	 */
	public void addMultThis(Vector3f vector, float scale) {
		this.x += scale * vector.x;
		this.y += scale * vector.y;
		this.z += scale * vector.z;
	}

	public Vector3f add(float x, float y, float z)
	{
		return new Vector3f(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f add(Vector3f vector)
	{
		return new Vector3f(this.x + vector.x, this.y + vector.y, this.z
				+ vector.z);
	}

	public void subThis(float x, float y, float z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}

	public void subThis(Vector3f vector)
	{
		this.x -= vector.x;
		this.y -= vector.y;
		this.z -= vector.z;
	}

	public Vector3f sub(float x, float y, float z)
	{
		return new Vector3f(this.x - x, this.y - y, this.z - z);
	}

	public Vector3f sub(Vector3f vector)
	{
		return new Vector3f(this.x - vector.x, this.y - vector.y, this.z
				- vector.z);
	}

	public Vector3f cross(float x, float y, float z)
	{
		Vector3f result = new Vector3f(this);
		result.crossThis(x, y, z);
		return result;
	}

	public Vector3f cross(Vector3f vector)
	{
		Vector3f result = new Vector3f(this);
		result.crossThis(vector);
		return result;
	}

	public void crossThis(float x, float y, float z)
	{
		float tempX = this.y * z - this.z * y;
		float tempY = this.z * x - this.x * z;
		float tempZ = this.x * y - this.y * x;
		this.x = tempX;
		this.y = tempY;
		this.z = tempZ;
	}

	public void crossThis(Vector3f vector)
	{
		crossThis(vector.x, vector.y, vector.z);
	}

	public float dot(float x, float y, float z)
	{
		return this.x * x + this.y * y + this.z * z;
	}

	public float dot(Vector3f vector)
	{
		return this.x * vector.x + this.y * vector.y + this.z * vector.z;
	}

	public void multThis(float scalar)
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public Vector3f mult(float scalar)
	{
		return new Vector3f(x * scalar, y * scalar, z * scalar);
	}

	public Vector3f mult(Vector3f vector)
	{
		return new Vector3f(this.x * vector.x, this.y * vector.y, this.z
				* vector.z);
	}

	public void multThis(Vector3f vector)
	{
		this.x *= vector.x;
		this.y *= vector.y;
		this.z *= vector.z;
	}

	public void negateThis()
	{
		x = -x;
		y = -y;
		z = -z;
	}

	public Vector3f negate()
	{
		return new Vector3f(-x, -y, -z);
	}

	public void normalizeThis()
	{
		float length = getLength();

		if(length != 0)
		{
			multThis(1 / length);
		}
	}

	public Vector3f normalize()
	{
		Vector3f vector = new Vector3f(this);
		vector.normalizeThis();

		return vector;
	}

	public float getDistance(Vector3f vector)
	{
		float dx = x - vector.x;
		float dy = y - vector.y;
		float dz = z - vector.z;

		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public float getLength()
	{
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float getLengthSquared()
	{
		return x * x + y * y + z * z;
	}

	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vector3f vector)
	{
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public float getZ()
	{
		return z;
	}

	public void setZ(float z)
	{
		this.z = z;
	}

	public boolean equals(Object object)
	{
		if(this == object)
		{
			return true;
		}

		if(!(object instanceof Vector3f) || object == null)
		{
			return false;
		}

		return this.x == ((Vector3f) object).x && this.y == ((Vector3f) object).y
				&& this.z == ((Vector3f) object).z;
	}

	public int hashCode()
	{
		return Float.floatToIntBits(x) ^ Float.floatToIntBits(y)
				^ Float.floatToIntBits(z);
	}
	
	public String toString() {
		return "x=" + x + " y=" + y + " z=" + z;
	}
}
