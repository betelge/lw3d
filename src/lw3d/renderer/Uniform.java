package lw3d.renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Uniform {

	private String name;
	private Type type = Type.NONE;
	private float[] floats = new float[4];
	boolean transpose;
	private FloatBuffer matrix;

	enum Type {
		FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, MATRIX2, MATRIX3, MATRIX4, NONE;
	}
	
	public Uniform(String name, boolean transpose, float[] matrix) {
		this.setName(name);
		this.transpose = transpose;
		set(transpose, matrix);
	}
	
	public void set(boolean transpose, float[] matrix) {
		if(this.matrix == null)
			this.matrix = BufferUtils.createFloatBuffer(matrix.length);
		
		this.matrix.put(matrix);
		this.matrix.flip();
		
		switch(matrix.length) {
		case 4:
			type = Type.MATRIX2;
			break;
		case 9:
			type = Type.MATRIX3;
			break;
		case 16:
			type = Type.MATRIX4;
			break;
		default :
			break;
		}
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
	
	public boolean isTranspose() {
		return transpose;
	}
	
	public FloatBuffer getMatrix() {
		return matrix;
	}

	public Type getType() {
		return type;
	}

}
