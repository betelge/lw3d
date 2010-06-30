package lw3d.renderer.managers;

import java.util.HashMap;
import java.util.Map;

import lw3d.renderer.Texture;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.SGISGenerateMipmap;

public class TextureManager {

	Map<Texture, Integer> textureHandles = new HashMap<Texture, Integer>();

	public int getTextureHandle(Texture texture) {
		if (tryToUpload(texture))
			return textureHandles.get(texture);
		else
			return 0;
	}

	private boolean tryToUpload(Texture texture) {
		if (textureHandles.containsKey(texture))
			return true;

		// Create a texture object
		int textureHandle = GL11.glGenTextures();
		GL11.glBindTexture(texture.getTextureType().getValue(), textureHandle);
		
		// Set both filters
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_MIN_FILTER, texture.getFilter().getValue());
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); // TODO: <- hardcoded linear

		// Set wrap modes
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_WRAP_S, texture.getWrapMode().getValue());
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_WRAP_T, texture.getWrapMode().getValue());
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL12.GL_TEXTURE_WRAP_R, texture.getWrapMode().getValue());
		
		if(texture.getMipmapLevel() != -1) {
			GL11.glTexParameterf(texture.getTextureType().getValue(), GL12.GL_TEXTURE_MIN_LOD, (float)texture.getMipmapLevel());
			GL11.glTexParameterf(texture.getTextureType().getValue(), GL12.GL_TEXTURE_MIN_LOD, (float)texture.getMipmapLevel());
		}

		// Upload data
		switch (texture.getTextureType()) {
		case TEXTURE_1D:
			GL11.glTexImage1D(texture.getTextureType().getValue(), 0, texture
					.getFormat().getInternalFormatValue(), texture.getWidth(),
					0, texture.getFormat().getExternalFormatValue(), texture
							.getTexelType().getValue(), texture
							.getTextureData());
			break;
		case TEXTURE_2D:
			GL11.glTexImage2D(texture.getTextureType().getValue(), 0, texture
					.getFormat().getInternalFormatValue(), texture.getWidth(),
					texture.getHeight(), 0, texture.getFormat()
							.getExternalFormatValue(), texture.getTexelType()
							.getValue(), texture.getTextureData());
			break;
		case TEXTURE_3D:
			GL12.glTexImage3D(texture.getTextureType().getValue(), 0, texture
					.getFormat().getInternalFormatValue(), texture.getWidth(),
					texture.getHeight(), texture.getDepth(), 0, texture.getFormat()
							.getExternalFormatValue(), texture.getTexelType()
							.getValue(), texture.getTextureData());
			break;
		default:
			return false;
		}
		
		// Generate mipmaps
		EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);

		textureHandles.put(texture, textureHandle);

		return true;
	}

}
