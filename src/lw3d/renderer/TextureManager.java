package lw3d.renderer;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

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

		// Turn on mipmap generation
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);

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
		default:
			return false;
		}

		// Set both filters
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_MIN_FILTER, texture.getFilter().getValue());
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); // TODO: <- hardcoded linear

		// Set both wrap modes
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_WRAP_S, texture.getWrapMode().getValue());
		GL11.glTexParameteri(texture.getTextureType().getValue(),
				GL11.GL_TEXTURE_WRAP_T, texture.getWrapMode().getValue());

		textureHandles.put(texture, textureHandle);

		return true;
	}

}
