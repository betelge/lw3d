package lw3d.renderer.passes;

import lw3d.renderer.FBO;

import org.lwjgl.opengl.GL11;

public class ClearPass extends RenderPass {
	public final static int COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT;
	public final static int DEPTH_BUFFER_BIT = GL11.GL_DEPTH_BUFFER_BIT;
	public final static int ACCUM_BUFFER_BIT = GL11.GL_ACCUM_BUFFER_BIT;
	public final static int STENCIL_BUFFER_BIT = GL11.GL_STENCIL_BUFFER_BIT;
	
	private int bufferBits;
	
	public ClearPass(int bufferBits, FBO fbo) {
		this.setBufferBits(bufferBits);
		setFbo(fbo);
	}

	public void setBufferBits(int bufferBits) {
		this.bufferBits = bufferBits;
	}

	public int getBufferBits() {
		return bufferBits;
	}
}
