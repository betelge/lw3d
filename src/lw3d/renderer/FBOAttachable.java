package lw3d.renderer;

import org.lwjgl.opengl.ARBDepthTexture;
import org.lwjgl.opengl.ARBTextureCompression;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;

public class FBOAttachable {
	private final int width;
	private final int height;
	private final Format format;

	public enum Format {
		GL_RGB8(GL11.GL_RGB, GL11.GL_RGB8, 3), GL_RGBA4(GL11.GL_RGBA,
				GL11.GL_RGBA4, 2), GL_RGBA8(GL11.GL_RGBA, GL11.GL_RGBA8, 4), GL_RGBA16(
				GL11.GL_RGBA, GL11.GL_RGBA16, 8), GL_RGB5_A1(GL11.GL_RGBA,
				GL11.GL_RGB5_A1, 2),
				
		GL_LUMINANCE16_ALPHA16(GL11.GL_LUMINANCE_ALPHA, GL11.GL_LUMINANCE16_ALPHA16, 4),

		GL_RGBA32F(GL11.GL_RGBA, ARBTextureFloat.GL_RGB32F_ARB, 16),

		GL_DEPTH_COMPONENT(0, GL11.GL_DEPTH_COMPONENT, 2), GL_STENCIL_INDEX(0,
				GL11.GL_STENCIL_INDEX, 1),
				
		GL_DEPTH_COMPONENT16(GL11.GL_DEPTH_COMPONENT, ARBDepthTexture.GL_DEPTH_COMPONENT16_ARB, 4),
		GL_DEPTH_COMPONENT24(GL11.GL_DEPTH_COMPONENT, ARBDepthTexture.GL_DEPTH_COMPONENT24_ARB, 4),
		GL_DEPTH_COMPONENT32(GL11.GL_DEPTH_COMPONENT, ARBDepthTexture.GL_DEPTH_COMPONENT32_ARB, 4),

		GL_COMPRESSED_RGB(GL11.GL_RGB, ARBTextureCompression.GL_COMPRESSED_RGB_ARB, 3),
		GL_COMPRESSED_RGBA(GL11.GL_RGBA, ARBTextureCompression.GL_COMPRESSED_RGBA_ARB, 4);

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
	};

	public FBOAttachable(Format format, int width, int height) {
		this.format = format;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Format getFormat() {
		return format;
	}
}
