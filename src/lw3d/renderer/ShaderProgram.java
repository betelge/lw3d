package lw3d.renderer;

import java.util.Set;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.ARBVertexShader;

public class ShaderProgram {

	static public class Shader {
		final public String source;
		final public Type type;
		
		public Shader(Type type, String source) {
			this.type = type;
			this.source = source;
		}

		public enum Type {
			VERTEX(ARBVertexShader.GL_VERTEX_SHADER_ARB), FRAGMENT(
					ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
					
			private int type;
					
			Type(int type) {
				this.type = type;
			}
			
			public int getValue() {
				return type;
			}
		}
	}

	final Set<Shader> shaders;

	public ShaderProgram(Set<Shader> shaders) {
		this.shaders = shaders;
	}

	public Set<Shader> getShaders() {
		return shaders;
	}
}
