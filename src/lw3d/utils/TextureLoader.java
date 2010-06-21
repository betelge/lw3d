package lw3d.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import lw3d.renderer.Texture;

public class TextureLoader {

	static public Texture loadTexture(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		DataBuffer data = image.getData().getDataBuffer();

		int dataSize = data.getSize();

		ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * dataSize );
		for (int i = 0; i < dataSize; i++)
			buffer.put((byte) data.getElem(i));
		buffer.flip();

		Texture texture = new Texture(buffer, Texture.TextureType.TEXTURE_2D,
				image.getWidth(), image.getHeight(), Texture.TexelType.UBYTE,
				Texture.Format.GL_RGB8, Texture.Filter.LINEAR_MIPMAP_NEAREST,
				Texture.WrapMode.REPEAT);

		return texture;
	}
}
