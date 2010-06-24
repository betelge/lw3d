package lw3d.renderer.passes;

import lw3d.renderer.FBO;

public class RenderPass {
	private FBO fbo = null;

	public void setFbo(FBO fbo) {
		this.fbo = fbo;
	}

	public FBO getFbo() {
		return fbo;
	}
}
