package lw3d.renderer.managers;

import java.util.HashMap;
import java.util.Map;

import lw3d.renderer.FBO;
import lw3d.renderer.FBOAttachable;
import lw3d.renderer.RenderBuffer;
import lw3d.renderer.Texture;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public class FBOManager {

	Map<FBO, Integer> FBOHandles = new HashMap<FBO, Integer>();

	final TextureManager textureManager;
	final RenderBufferManager renderBufferManager;

	public FBOManager(TextureManager textureManager,
			RenderBufferManager renderBufferManager) {
		this.textureManager = textureManager;
		this.renderBufferManager = renderBufferManager;
	}

	public int getFBOHandle(FBO fbo) {
		//generateMipmaps(fbo);
		if (tryToUpload(fbo))
			return FBOHandles.get(fbo);
		else
			return 0;
	}

	private boolean tryToUpload(FBO fbo) {
		if (FBOHandles.containsKey(fbo))
			return true;

		int handle = EXTFramebufferObject.glGenFramebuffersEXT();
		EXTFramebufferObject.glBindFramebufferEXT(
				EXTFramebufferObject.GL_FRAMEBUFFER_EXT, handle);

		// TODO: Check if sizes match
		for (int i = 0; i < fbo.getAttachables().length && i <= 15; i++) {
			attach(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT + i, fbo
					.getAttachables()[i]);
		}
		if (fbo.getDepthBuffer() != null)
			attach(EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, fbo
					.getDepthBuffer());
		if (fbo.getStencilBuffer() != null)
			attach(EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, fbo
					.getStencilBuffer());

		FBOHandles.put(fbo, handle);

		return true;
	}

	private void attach(int attachPoint, FBOAttachable attachable) {
		if (attachable == null)
			EXTFramebufferObject.glFramebufferRenderbufferEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, attachPoint,
					EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_NONE);
		else if (attachable instanceof Texture) {
			EXTFramebufferObject.glFramebufferTexture2DEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, attachPoint,
					GL11.GL_TEXTURE_2D, textureManager
							.getTextureHandle((Texture) attachable), 0);

		} else if (attachable instanceof RenderBuffer) {
			EXTFramebufferObject.glFramebufferRenderbufferEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, attachPoint,
					EXTFramebufferObject.GL_RENDERBUFFER_EXT,
					renderBufferManager
							.getRenderBufferHandle((RenderBuffer) attachable));

		}
	}

	public void generateMipmaps(FBO fbo) {
		for (int i = 0; i < fbo.getAttachables().length && i <= 15; i++) {
			if (fbo.getAttachables()[i] instanceof Texture) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager
						.getTextureHandle((Texture) fbo.getAttachables()[i]));
				EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
			}
		}
	}

	public boolean isComplete(FBO fbo) {
		tryToUpload(fbo);
		return (EXTFramebufferObject.glCheckFramebufferStatusEXT(
						EXTFramebufferObject.GL_FRAMEBUFFER_EXT) == EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT);
	}
}
