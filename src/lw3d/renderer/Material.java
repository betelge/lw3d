package lw3d.renderer;

public class Material {
	
	private ShaderProgram shaderProgram;
	private Uniform[] uniforms;
	private Texture[] textures;
	
	public Material(ShaderProgram shaderProgram) {
		this.setShaderProgram(shaderProgram);
	}

	public void setShaderProgram(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
	}

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	public void setTextures(Texture[] textures) {
		this.textures = textures;
	}

	public Texture[] getTextures() {
		return textures;
	}

	public void setUniforms(Uniform[] uniforms) {
		this.uniforms = uniforms;
	}

	public Uniform[] getUniforms() {
		return uniforms;
	}

}
