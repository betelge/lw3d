package lw3d.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import lw3d.renderer.Texture;
import lw3d.renderer.FBOAttachable.Format;
import lw3d.renderer.Texture.Filter;
import lw3d.renderer.Texture.TexelType;
import lw3d.renderer.Texture.TextureType;
import lw3d.renderer.Texture.WrapMode;

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
				Texture.Format.GL_COMPRESSED_RGB, Texture.Filter.LINEAR_MIPMAP_NEAREST,
				Texture.WrapMode.REPEAT);

		return texture;
	}
	
	static public Texture generateNoiseTexture(Texture.TextureType textureType, int size, long seed) {
		int length;
		switch(textureType) {
		case TEXTURE_1D:
			length = size;
			break;
		case TEXTURE_2D:
			length = size * size;
			break;
		case TEXTURE_3D:
			length = size * size * size;
			break;
		default:
				return null;
		}
		
		ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * length );
		byte[] array = new byte[4 * length];
		
		Random rand = new Random(seed);
		rand.nextBytes(array);
		buffer.put(array);
		buffer.flip();
		
		return new Texture(buffer, textureType, size, size, size, TexelType.USHORT, Format.GL_LUMINANCE16_ALPHA16,
				Filter.LINEAR, WrapMode.REPEAT);
	}
	
	public static void setObject(Object givenObject) {
		object = givenObject;
	}
}
