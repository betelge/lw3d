package lw3d.renderer;

import java.util.HashMap;
import java.util.Map;

public class Material {

	private ShaderProgram shaderProgram;
	private Uniform[] uniforms;
	
	private Map<String, Texture> textures = new HashMap<String, Texture>();

	public Material(ShaderProgram shaderProgram) {
		this.setShaderProgram(shaderProgram);
	}

	public void setShaderProgram(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
	}

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	public void setTextures(Map<String, Texture> textures) {
		this.textures = textures;
	}

	public void addTexture(String name, Texture texture) {
		textures.put(name, texture);
	}

	public Map<String, Texture> getTextures() {
		return textures;
	}

	public void setUniforms(Uniform[] uniforms) {
		this.uniforms = uniforms;
	}

	public Uniform[] getUniforms() {
		return uniforms;
	}

	public void addUniform(Uniform uniform) {
		if (uniforms == null)
			uniforms = new Uniform[1];
		else {
			Uniform[] newUniforms = new Uniform[uniforms.length + 1];
			for (int i = 0; i < uniforms.length; i++)
				newUniforms[i] = uniforms[i];
			
			uniforms = newUniforms;
		}
		
		uniforms[uniforms.length - 1] = uniform;
	}
}
