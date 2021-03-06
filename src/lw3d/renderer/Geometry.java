package lw3d.renderer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class Geometry {
	static public Geometry QUAD;
	
	final IntBuffer indices;
	final List<Attribute> attributes;
	public enum PrimitiveType {
		POINTS(GL11.GL_POINTS), LINES(GL11.GL_LINES), LINE_STRIP(GL11.GL_LINE_STRIP),
		TRIANGLES(GL11.GL_TRIANGLES), TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP);
		
		int value;
		
		PrimitiveType(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	final PrimitiveType primitiveType;
	
	public enum Type {		
		BYTE(GL11.GL_BYTE), UBYTE(GL11.GL_UNSIGNED_BYTE), FLOAT(GL11.GL_FLOAT);
		
		int type;
		
		Type(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}
	
	static public class Attribute {
		public String name;
		public Type type;
		public int size;
		public Buffer buffer;
		public boolean normalized = false;
	}
	
	public Geometry(PrimitiveType primitiveType, IntBuffer indices, List<Attribute> attributes) {
		this.primitiveType = primitiveType;
		this.indices = indices;
		this.attributes = attributes;
	}
	
	public Geometry(IntBuffer indices, List<Attribute> attributes) {
		this(PrimitiveType.TRIANGLES, indices, attributes);
	}
	
	public IntBuffer getIndices() {
		return indices;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}
}
