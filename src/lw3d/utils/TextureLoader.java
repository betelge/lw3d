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
	
	static public Texture loadTextureExceptionless(String filename) {
		try {
			return TextureLoader.loadTexture(filename);
		} catch (IOException e) {
			System.out.println("TODO: Add a default fall back texture.");
			e.printStackTrace();
			return null;
		}
	}

	static public Texture loadTexture(String filename) throws IOException {
		InputStream is = object.getClass().getResourceAsStream(filename);
		if(is == null)
			System.out.println("Cant't load texture: " + filename);
		BufferedImage image = ImageIO.read(is);
		DataBuffer data = image.getData().getDataBuffer();
		
		int dataSize = data.getSize();
		Texture.Format format;
		Texture.TexelType texelType;

		ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * dataSize );
		
		// Handle different formats
		switch(image.getType()) {
		case BufferedImage.TYPE_INT_RGB:
			System.out.println("TYPE_INT_RGB");
			for (int i = 0; i < dataSize; i++)
				buffer.put((byte) data.getElem(i));
			format = Format.GL_COMPRESSED_RGB;
			texelType = TexelType.UINT;
			break;
		case BufferedImage.TYPE_INT_BGR:
			System.out.println("TYPE_INT_BGR");
			for (int i = 0; i < dataSize; i += 3) {
				buffer.put((byte) data.getElem(i+2));
				buffer.put((byte) data.getElem(i+1));
				buffer.put((byte) data.getElem(i));
			}
			format = Format.GL_COMPRESSED_RGB;
			texelType = TexelType.UINT;
			break;
		case BufferedImage.TYPE_INT_ARGB:
			System.out.println("TYPE_INT_ARGB");
			for (int i = 0; i < dataSize; i++)
				buffer.put((byte) data.getElem(i));
			format = Format.GL_COMPRESSED_RGBA;
			texelType = TexelType.UINT;
			break;
		case BufferedImage.TYPE_INT_ARGB_PRE:
			System.out.println("TYPE_INT_ARGB_PRE");
			for (int i = 0; i < dataSize; i++)
				buffer.put((byte) data.getElem(i));
			format = Format.GL_COMPRESSED_RGBA;
			texelType = TexelType.UINT;
			break;
		case BufferedImage.TYPE_3BYTE_BGR:
			System.out.println("TYPE_3BYTE_BGR");
			for (int i = 0; i < dataSize; i += 3) {
				buffer.put((byte) data.getElem(i));
				buffer.put((byte) data.getElem(i+1));
				buffer.put((byte) data.getElem(i+2));
			}
			format = Format.GL_COMPRESSED_RGB;
			texelType = TexelType.UBYTE;
			break;
		case BufferedImage.TYPE_4BYTE_ABGR:
			System.out.println("TYPE_4BYTE_ABGR");
			for (int i = 0; i < dataSize; i += 4) {
				buffer.put((byte) data.getElem(i+3));
				buffer.put((byte) data.getElem(i+2));
				buffer.put((byte) data.getElem(i+1));
				buffer.put((byte) data.getElem(i));
			}
			format = Format.GL_COMPRESSED_RGBA;
			texelType = TexelType.UBYTE;
			break;
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			System.out.println("TYPE_4BYTE_ABGR_PRE");
			for (int i = 0; i < dataSize; i += 4) {
				buffer.put((byte) data.getElem(i+3));
				buffer.put((byte) data.getElem(i+2));
				buffer.put((byte) data.getElem(i+1));
				buffer.put((byte) data.getElem(i));
			}
			format = Format.GL_COMPRESSED_RGBA;
			texelType = TexelType.UBYTE;
			break;
		default:
			System.out.println("WARNING: Unknown texture format. Will use RGB.");
			for (int i = 0; i < dataSize; i++)
				buffer.put((byte) data.getElem(i));
			format = Format.GL_COMPRESSED_RGB;
			texelType = TexelType.UBYTE;
			break;				
		}

		buffer.flip();

		Texture texture = new Texture(buffer, Texture.TextureType.TEXTURE_2D,
				image.getWidth(), image.getHeight(), texelType,
				format, Texture.Filter.LINEAR_MIPMAP_LINEAR,
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
