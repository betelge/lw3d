package lw3d.renderer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Set;

import org.lwjgl.opengl.GL11;

public class Geometry {
	IntBuffer indices;
	Set<Attribute> attributes;
	
	enum Type {		
		BYTE(GL11.GL_BYTE), FLOAT(GL11.GL_FLOAT);
		
		int type;
		
		private Type(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}
	
	class Attribute {
		public String name;
		public Type type;
		public int size;
		public Buffer buffer;
		public boolean normalized;
	}
	
	public Geometry(IntBuffer indices, Set<Attribute> attributes) {
		this.indices = indices;
		this.attributes = attributes;
	}
	
	public IntBuffer getIndices() {
		return indices;
	}
	
	public Set<Attribute> getAttributes() {
		return attributes;
	}
}
