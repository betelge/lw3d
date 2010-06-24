package lw3d.renderer;

public class Uniform {

	private String name;
	private Type type;
	private float[] floats = new float[4];

	enum Type {
		FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4;
		// TODO: Add matrices?
	}

	public Uniform(String name, float a, float b, float c, float d) {
		this.setName(name);
		set(a, b, c, d);

	}
	
	public Uniform(String name, float a, float b, float c) {
		this.setName(name);
		set(a, b, c);

	}
	
	public Uniform(String name, float a, float b) {
		this.setName(name);
		set(a, b);

	}
	
	public Uniform(String name, float a) {
		this.setName(name);
		set(a);

	}

	public void set(float a, float b, float c, float d) {
		type = Type.FLOAT4;
		floats[0] = a;
		floats[1] = b;
		floats[2] = c;
		floats[3] = d;
	}

	public void set(float a, float b, float c) {
		type = Type.FLOAT3;
		floats[0] = a;
		floats[1] = b;
		floats[2] = c;
	}
	
	public void set(float a, float b) {
		type = Type.FLOAT2;
		floats[0] = a;
		floats[1] = b;
	}
	
	public void set(float a) {
		type = Type.FLOAT;
		floats[0] = a;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public float[] getFloats() {
		return floats;
	}

	public Type getType() {
		return type;
	}

}
