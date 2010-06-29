package lw3d.renderer;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public class Texture extends FBOAttachable{
	
	private ByteBuffer textureData;
	private final TextureType textureType;
	private final TexelType texelType;
	private final Filter filter; // TODO: Separate filters?
	private final WrapMode wrapMode; // TODO: Separate wrap modes?
	
	// -1 if unspecified
	private float mipmapLevel = -1;

	// width and height are in the super class
	private final int depth;

	public enum TextureType {
		TEXTURE_1D(GL11.GL_TEXTURE_1D), TEXTURE_2D(GL11.GL_TEXTURE_2D), TEXTURE_3D(
				GL12.GL_TEXTURE_3D);

		private int type;

		private TextureType(int type) {
			this.type = type;
		}

		public int getValue() {
			return type;
		}
	}

	public enum TexelType {
		FLOAT(GL11.GL_FLOAT), INT(GL11.GL_INT), UINT(GL11.GL_UNSIGNED_INT), BYTE(
				GL11.GL_BYTE), UBYTE(GL11.GL_UNSIGNED_BYTE), SHORT(
				GL11.GL_SHORT), USHORT(GL11.GL_UNSIGNED_SHORT);

		private int type;

		TexelType(int type) {
			this.type = type;
		}

		public int getValue() {
			return type;
		}
	}



	public enum Filter {
		NEAREST(GL11.GL_NEAREST), LINEAR(GL11.GL_LINEAR), NEAREST_MIPMAP_NEAREST(
				GL11.GL_NEAREST_MIPMAP_NEAREST), NEAREST_MIPMAP_LINEAR(
				GL11.GL_NEAREST_MIPMAP_LINEAR), LINEAR_MIPMAP_NEAREST(
				GL11.GL_LINEAR_MIPMAP_NEAREST), LINEAR_MIPMAP_LINEAR(
				GL11.GL_LINEAR_MIPMAP_LINEAR);

		private int filter;

		private Filter(int filter) {
			this.filter = filter;
		}

		public int getValue() {
			return filter;
		}
	}

	public enum WrapMode {
		CLAMP(GL11.GL_CLAMP), REPEAT(GL11.GL_REPEAT), CLAMP_TO_EDGE(
				GL12.GL_CLAMP_TO_EDGE), CLAMP_TO_BORDER(GL13.GL_CLAMP_TO_BORDER);

		private int mode;

		private WrapMode(int mode) {
			this.mode = mode;
		}

		public int getValue() {
			return mode;
		}
	}

	public Texture(ByteBuffer textureData, TextureType textureType, int width,
			int height, TexelType texelType, Format format, Filter filter,
			WrapMode wrapMode) {
		super(format, width, height);
		this.textureData = textureData;
		this.textureType = textureType;
		this.depth = 1;
		this.texelType = texelType;
		this.filter = filter;
		this.wrapMode = wrapMode;
	}

	public TextureType getTextureType() {
		return textureType;
	}

	public ByteBuffer getTextureData() {
		return textureData;
	}

	public int getDepth() {
		return depth;
	}

	public TexelType getTexelType() {
		return texelType;
	}

	public Filter getFilter() {
		return filter;
	}

	public WrapMode getWrapMode() {
		return wrapMode;
	}

	public float getMipmapLevel() {
		return mipmapLevel;
	}

	public void setMipmapLevel(float mipmapLevel) {
		this.mipmapLevel = mipmapLevel;
	}

}
