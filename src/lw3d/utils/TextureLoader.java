package lw3d.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import lw3d.renderer.Texture;

public class TextureLoader {
	
	static private Object object = new Object();

	static public Texture loadTexture(String filename) throws IOException {
		InputStream is = object.getClass().getResourceAsStream(filename);
		if(is == null)
			System.out.println("Cant't load texture: " + filename);
		BufferedImage image = ImageIO.read(is);
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
	
	public static void setObject(Object givenObject) {
		object = givenObject;
	}
}
