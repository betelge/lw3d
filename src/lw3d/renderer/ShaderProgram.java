package lw3d.renderer;

import java.util.HashSet;
import java.util.Set;

import lw3d.utils.StringLoader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexShader;

public class ShaderProgram {
	
	static public ShaderProgram DEFAULT = new ShaderProgram(Shader.DEFAULT_VERTEX, Shader.DEFAULT_FRAGMENT);

	static public class Shader {
		
		static public Shader DEFAULT_VERTEX =
			new Shader(Type.VERTEX, StringLoader.loadStringExceptionless("/default.vertex"));
		
		static public Shader DEFAULT_FRAGMENT =
			new Shader(Type.FRAGMENT, StringLoader.loadStringExceptionless("/light.fragment")); 
		
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
	
	public ShaderProgram(Shader vertexShader, Shader fragmentShader) {
		assert(vertexShader.type == Shader.Type.VERTEX &&
				fragmentShader.type == Shader.Type.FRAGMENT);
		Set<Shader> shaders = new HashSet<Shader>();
		shaders.add(vertexShader);
		shaders.add(fragmentShader);
		this.shaders = shaders;
	}

	public ShaderProgram(Set<Shader> shaders) {
		this.shaders = shaders;
	}

	public Set<Shader> getShaders() {
		return shaders;
	}
}
