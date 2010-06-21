package lw3d.renderer;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public class Texture {
	
	private String name;

	private ByteBuffer textureData;
	private final TextureType textureType;
	private final TexelType texelType;
	private final Format format;
	private final Filter filter; // TODO: Separate filters?
	private final WrapMode wrapMode; // TODO: Separate wrap modes?

	private final int width;
	private final int height;
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

	public enum Format {
		GL_RGB8(GL11.GL_RGB, GL11.GL_RGB8, 3), GL_RGBA4(GL11.GL_RGBA,
				GL11.GL_RGBA4, 2), GL_RGBA8(GL11.GL_RGBA, GL11.GL_RGBA8, 4), GL_RGBA16(
				GL11.GL_RGBA, GL11.GL_RGBA16, 8), GL_RGB5_A1(GL11.GL_RGBA,
				GL11.GL_RGB5_A1, 2), GL_RGBA32F(GL11.GL_RGBA,
				ARBTextureFloat.GL_RGB32F_ARB, 16), GL_DEPTH_COMPONENT(0,
				GL11.GL_DEPTH_COMPONENT, 2), GL_STENCIL_INDEX(0,
				GL11.GL_STENCIL_INDEX, 1);

		private int externalFormatValue;
		private int internalFormatValue;
		private int texelSizeValue;

		private Format(int externalFormatValue, int internalFormatValue,
				int texelSizeValue) {
			this.internalFormatValue = internalFormatValue;
			this.externalFormatValue = externalFormatValue;
			this.texelSizeValue = texelSizeValue;
		}

		public int getExternalFormatValue() {
			return externalFormatValue;
		}

		public int getInternalFormatValue() {
			return internalFormatValue;
		}

		public int getTexelSizeValue() {
			return texelSizeValue;
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
		this.textureData = textureData;
		this.textureType = textureType;
		this.width = width;
		this.height = height;
		this.depth = 1;
		this.texelType = texelType;
		this.format = format;
		this.filter = filter;
		this.wrapMode = wrapMode;
	}

	public TextureType getTextureType() {
		return textureType;
	}

	public ByteBuffer getTextureData() {
		return textureData;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	public TexelType getTexelType() {
		return texelType;
	}

	public Format getFormat() {
		return format;
	}

	public Filter getFilter() {
		return filter;
	}

	public WrapMode getWrapMode() {
		return wrapMode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
