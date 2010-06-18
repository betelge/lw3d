package lw3d.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.GL11;

public class GeometryManager {

	/*
	 * TODO: Use VAOs!
	 */

	private int indexVBOHandle;
	private int dataVBOHandle;
	private int indexOffset = 0;
	private int dataOffset = 0;

	/*
	 * public class Attribute { public String name; int size; public
	 * Geometry.Type type; public boolean normalized; public int stride; public
	 * int offset; }
	 */

	public class GeometryInfo {
		// VAO handle
		public int VAO = 0;

		public int indexOffset;

		public int count;
		
		// TODO: fix mode enum
		/*public enum Mode {
			TRIANGLES(GL11.GL_TRIANGLES), QUADS(GL11.GL_QUADS); // ...
		}*/

		// Set<Attribute> attributes = new HashSet<Attribute>();
	}

	Map<Geometry, GeometryInfo> geometryInfos = new HashMap<Geometry, GeometryInfo>();

	public GeometryManager() {
		IntBuffer buff = BufferUtils.createIntBuffer(1);
		ARBVertexBufferObject.glGenBuffersARB(buff);
		indexVBOHandle = buff.get(0);
		ARBVertexBufferObject.glBindBufferARB(
				ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
				indexVBOHandle);
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
				4 * 1024 * 1024, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);

		ARBVertexBufferObject.glGenBuffersARB(buff);
		dataVBOHandle = buff.get(0);
		ARBVertexBufferObject.glBindBufferARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, dataVBOHandle);
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 4 * 1024 * 1024,
				ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
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
			
			// TODO: Do this better!
			geometryInfo.count = 1;//geometry.getIndices().capacity() / 3;

			// Iterate through the attributes
			Iterator<Geometry.Attribute> it = geometry.getAttributes()
					.iterator();

			// Set<Attribute> attributes = new HashSet<Attribute>();

			int i = 0;
			Geometry.Attribute geometryAttribute;
			while (it.hasNext()) {
				geometryAttribute = it.next();
				/*
				 * Attribute attribute = new Attribute(); attribute.name =
				 * geometryAttribute.name; attribute.normalized =
				 * geometryAttribute.normalized; attribute.type =
				 * geometryAttribute.type; attribute.size =
				 * geometryAttribute.size;
				 */

				// Upload attribute data to the data VBO
				switch (geometryAttribute.type) {
				case BYTE:
					ARBVertexBufferObject.glBufferSubDataARB(
							ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
							dataOffset, (ByteBuffer) geometryAttribute.buffer);
					break;
				case FLOAT:
					ARBVertexBufferObject.glBufferSubDataARB(
							ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
							dataOffset, (FloatBuffer) geometryAttribute.buffer);
					break;
				}

				// Bind the attribute to the VAO
				ARBVertexProgram.glEnableVertexAttribArrayARB(i);
				ARBVertexProgram.glVertexAttribPointerARB(i,
						geometryAttribute.size, geometryAttribute.type
								.getType(), geometryAttribute.normalized, 0,
						dataOffset);
				i++;

				/*
				 * attribute.offset = dataOffset; attribute.stride = 0;
				 * 
				 * attributes.add(attribute);
				 */

				// Update the data VBO offset
				dataOffset += geometry.getIndices().capacity() * 4;

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
