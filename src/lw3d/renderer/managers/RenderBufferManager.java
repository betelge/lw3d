package lw3d.renderer.managers;

import java.util.HashMap;
import java.util.Map;

import lw3d.renderer.RenderBuffer;

import org.lwjgl.opengl.EXTFramebufferObject;

public class RenderBufferManager {

	Map<RenderBuffer, Integer> renderBufferHandles = new HashMap<RenderBuffer, Integer>();

	public int getRenderBufferHandle(RenderBuffer renderBuffer) {
		if (tryToUpload(renderBuffer))
			return renderBufferHandles.get(renderBuffer);
		else
			return 0;
	}

	private boolean tryToUpload(RenderBuffer renderBuffer) {
		if (renderBufferHandles.containsKey(renderBuffer))
			return true;

		int handle = EXTFramebufferObject.glGenRenderbuffersEXT();
		EXTFramebufferObject.glBindRenderbufferEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, handle);

		// Allocate space
		EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, renderBuffer
						.getFormat().getInternalFormatValue(), renderBuffer
						.getWidth(), renderBuffer.getHeight());

		EXTFramebufferObject.glBindRenderbufferEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
		
		renderBufferHandles.put(renderBuffer, handle);
		return true;
	}

}
