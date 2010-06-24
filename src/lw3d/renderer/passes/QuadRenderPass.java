package lw3d.renderer.passes;

import lw3d.renderer.FBO;
import lw3d.renderer.Material;

public class QuadRenderPass extends RenderPass {
	private Material material;
		
	public QuadRenderPass(Material material) {
		this(material, null);
	}
	
	public QuadRenderPass(Material material, FBO fbo) {
		this.material = material;
		setFbo(fbo);
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
}
