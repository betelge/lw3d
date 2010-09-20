package lw3d.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import lw3d.math.Noise;
import lw3d.renderer.Texture;
import lw3d.renderer.FBOAttachable.Format;
import lw3d.renderer.Texture.Filter;
import lw3d.renderer.Texture.TexelType;
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
		int samples = 1;
		
		int length;
		switch(textureType) {
		case TEXTURE_1D:
			length = samples*size;
			break;
		case TEXTURE_2D:
			length = samples*size * samples*size;
			break;
		case TEXTURE_3D:
			samples = 3;
			length = samples*size * samples*size * samples*size;
			break;
		default:
				return null;
		}
		
		ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * length );
		
		switch(textureType) {
		case TEXTURE_3D:
			Noise rNoise = new Noise(seed),
				iNoise = new Noise(seed ^ 22l);
			
			for(float x = 1f/6; x < size; x += 1f/3)
				for(float y = 1f/6; y < size; y += 1f/3)
					for(float z = 1f/6; z < size; z += 1f/3) {
						buffer.putShort((short)(Short.MAX_VALUE * rNoise.noise(x,y,z)));
						buffer.putShort((short)(Short.MAX_VALUE * iNoise.noise(x,y,z)));
					}
			break;
		default:
				
				// vnoise
				Random rand = new Random(seed);
				byte[] array = new byte[4 * length];
				rand.nextBytes(array);
				buffer.put(array);
		}
		
		buffer.flip();
		
		return new Texture(buffer, textureType, size, size, size,
				TexelType.USHORT, Format.GL_LUMINANCE16_ALPHA16,
				Filter.LINEAR_MIPMAP_LINEAR, WrapMode.REPEAT);
	}
	
	public static void setObject(Object givenObject) {
		object = givenObject;
	}
}
