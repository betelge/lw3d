package lw3d.renderer;

public class FBO {
	
	private FBOAttachable[] attachables;
	private RenderBuffer depthBuffer;
	private RenderBuffer stencilBuffer;
	
	final private int width, height;

	public FBO(FBOAttachable[] attachables, RenderBuffer depthBuffer, RenderBuffer stencilBuffer, int width, int height) {
		this.attachables = attachables;
		this.depthBuffer = depthBuffer;
		this.stencilBuffer = stencilBuffer;
		this.width = width;
		this.height = height;
	}
	
	public FBO(FBOAttachable[] attachables, RenderBuffer depthBuffer, int width, int height) {
		this(attachables, depthBuffer, null, width, height);
	}
	
	public FBO(FBOAttachable[] attachables, int width, int height) {
		this(attachables, null, null, width, height);
	}
	
	public FBO(FBOAttachable attachable, RenderBuffer depthBuffer, int width, int height) {
		this.attachables = new FBOAttachable[1];
		this.attachables[0] = attachable;
		this.depthBuffer = depthBuffer;
		this.stencilBuffer = null;
		this.width = width;
		this.height = height;
	}
	
	public FBO(FBOAttachable attachable, int width, int height) {
		this(attachable, null, width, height);
	}

	public FBOAttachable[] getAttachables() {
		return attachables;
	}

	public void setAttachables(FBOAttachable[] attachables) {
		this.attachables = attachables;
	}

	public RenderBuffer getDepthBuffer() {
		return depthBuffer;
	}

	public void setDepthBuffer(RenderBuffer depthBuffer) {
		this.depthBuffer = depthBuffer;
	}

	public RenderBuffer getStencilBuffer() {
		return stencilBuffer;
	}

	public void setStencilBuffer(RenderBuffer stencilBuffer) {
		this.stencilBuffer = stencilBuffer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
