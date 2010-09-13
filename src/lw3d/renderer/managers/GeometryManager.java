package lw3d.renderer.managers;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lw3d.Lw3dModel.RendererMode;
import lw3d.renderer.Geometry;
import lw3d.renderer.Geometry.Attribute;
import lw3d.renderer.Geometry.Type;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

public class GeometryManager {

	final private RendererMode rendererMode;

	private int indexVBOHandle;
	private int dataVBOHandle;
	private int indexOffset = 0;
	private int dataOffset = 0;

	static public Geometry QUAD;

	public class GeometryInfo {
		// VAO handle
		public int VAO = 0;

		public int indexOffset;

		public int count;

		// TODO: fix mode enum
		/*
		 * public enum Mode { TRIANGLES(GL11.GL_TRIANGLES),
		 * QUADS(GL11.GL_QUADS); // ... }
		 */

		public String[] attributeNames;
	}

	Map<Geometry, GeometryInfo> geometryInfos = new HashMap<Geometry, GeometryInfo>();

	public GeometryManager(RendererMode rendererMode) {
		this.rendererMode = rendererMode;

		IntBuffer buff = BufferUtils.createIntBuffer(1);
		ARBVertexBufferObject.glGenBuffersARB(buff);
		indexVBOHandle = buff.get(0);
		ARBVertexBufferObject.glBindBufferARB(
				ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
				indexVBOHandle);
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
				1 * 1024 * 1024, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);

		ARBVertexBufferObject.glGenBuffersARB(buff);
		dataVBOHandle = buff.get(0);
		ARBVertexBufferObject.glBindBufferARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, dataVBOHandle);
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 4 * 1024 * 1024,
				ARBVertexBufferObject.GL_STATIC_DRAW_ARB);

		// Initialize the QUAD
		IntBuffer indices = BufferUtils.createIntBuffer(6);
		indices.put(0);
		indices.put(1);
		indices.put(2);
		indices.put(2);
		indices.put(3);
		indices.put(0);
		indices.flip();
		
		Geometry.Attribute at = new Geometry.Attribute();
		at.name = "position";
		at.size = 3;
		at.type = Geometry.Type.FLOAT;
		at.buffer = BufferUtils.createFloatBuffer(3 * 4);
		((FloatBuffer) at.buffer).put(-1);
		((FloatBuffer) at.buffer).put(-1);
		((FloatBuffer) at.buffer).put(0);

		((FloatBuffer) at.buffer).put(1);
		((FloatBuffer) at.buffer).put(-1);
		((FloatBuffer) at.buffer).put(0);

		((FloatBuffer) at.buffer).put(1);
		((FloatBuffer) at.buffer).put(1);
		((FloatBuffer) at.buffer).put(0);

		((FloatBuffer) at.buffer).put(-1);
		((FloatBuffer) at.buffer).put(1);
		((FloatBuffer) at.buffer).put(0);
		at.buffer.flip();
		
		Geometry.Attribute at2 = new Geometry.Attribute();
		at2.name = "textureCoord";
		at2.size = 2;
		at2.type = Geometry.Type.FLOAT;
		at2.buffer = BufferUtils.createFloatBuffer(2 * 4);
		((FloatBuffer) at2.buffer).put(0);
		((FloatBuffer) at2.buffer).put(0);

		((FloatBuffer) at2.buffer).put(1);
		((FloatBuffer) at2.buffer).put(0);

		((FloatBuffer) at2.buffer).put(1);
		((FloatBuffer) at2.buffer).put(1);

		((FloatBuffer) at2.buffer).put(0);
		((FloatBuffer) at2.buffer).put(1);
		at2.buffer.flip();
		
		List<Geometry.Attribute> lat = new ArrayList<Geometry.Attribute>();
		lat.add(at);
		lat.add(at2);
		QUAD = new Geometry(indices, lat);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		IntBuffer buffs = BufferUtils.createIntBuffer(2);
		buffs.put(indexVBOHandle);
		buffs.put(dataVBOHandle);
		buffs.flip();
		ARBVertexBufferObject.glDeleteBuffersARB(buffs);
	}

	public int getIndexVBOHandle(Geometry geometry) {
		if (tryToUpload(geometry))
			return /* geometryInfos.get(geometry). */indexVBOHandle;
		else
			return 0;
	}

	public int getDataVBOHandle(Geometry geometry) {
		if (tryToUpload(geometry))
			return /* geometryInfos.get(geometry). */dataVBOHandle;
		else
			return 0;
	}

	final public GeometryInfo getGeometryInfo(Geometry geometry) {
		if (tryToUpload(geometry))
			return geometryInfos.get(geometry);
		else
			return null;
	}

	public int getVAO(Geometry geometry) {
		if (tryToUpload(geometry))
			return geometryInfos.get(geometry).VAO;
		else
			return 0;
	}

	private boolean tryToUpload(Geometry geometry) {
		if (geometryInfos.containsKey(geometry))
			return true;
		else {
			GeometryInfo geometryInfo = new GeometryInfo();

			// Create and bind a VAO
			geometryInfo.VAO = ARBVertexArrayObject.glGenVertexArrays();
			ARBVertexArrayObject.glBindVertexArray(geometryInfo.VAO);

			// Bind VBOs to the VAO
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
					indexVBOHandle);
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, dataVBOHandle);

			// Upload index data to the index VBO
			ARBVertexBufferObject.glBufferSubDataARB(
					ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
					indexOffset, geometry.getIndices());

			// Set and update the index VBO offset
			geometryInfo.indexOffset = indexOffset;
			indexOffset += geometry.getIndices().capacity() * 4;

			geometryInfo.count = geometry.getIndices().capacity();

			geometryInfo.attributeNames = new String[geometry.getAttributes()
					.size()];

			// Iterate through the attributes
			Iterator<Geometry.Attribute> it = geometry.getAttributes()
					.iterator();
			int i = 0;
			Geometry.Attribute geometryAttribute;
			while (it.hasNext()) {
				geometryAttribute = it.next();

				// Upload attribute data to the data VBO
				switch (geometryAttribute.type) {
				case BYTE:
					ARBVertexBufferObject.glBufferSubDataARB(
							ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
							dataOffset, (ByteBuffer) geometryAttribute.buffer);
					break;
				case FLOAT:
					ARBVertexBufferObject.glBufferSubDataARB(
							ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
							dataOffset, (FloatBuffer) geometryAttribute.buffer);
					break;
				}

				// Bind the attribute to the VAO
				if (rendererMode == RendererMode.SHADERS) {
					ARBVertexShader.glEnableVertexAttribArrayARB(i);
					ARBVertexShader.glVertexAttribPointerARB(i,
							geometryAttribute.size, geometryAttribute.type
									.getType(), geometryAttribute.normalized,
							0, dataOffset);

				} else {
					if (geometryAttribute.name.equals("position")) {
						GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
						GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, dataOffset);
					}
					else if (geometryAttribute.name.equals("textureCoord")) {
						GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
						GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, dataOffset);
					}
					else if (geometryAttribute.name.equals("normal")) {
						GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
						GL11.glNormalPointer(GL11.GL_FLOAT, 0, dataOffset);
					}
					System.out.println(geometryAttribute.name);
				}

				geometryInfo.attributeNames[i] = geometryAttribute.name;

				i++;

				// Update the data VBO offset
				dataOffset += geometryAttribute.buffer.capacity() * 4;

			}

			geometryInfos.put(geometry, geometryInfo);

			// Unbind the VAO
			ARBVertexArrayObject.glBindVertexArray(0);

			return true;
		}
	}

	/*
	 * public ... getVertexAttributes(Geometry geometry) {
	 * 
	 * }
	 */

	public void register(Geometry geometry /* TODO: hints */) {

	}
}
