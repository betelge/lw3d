package lw3d.renderer;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;

import lw3d.math.Vector3f;
import lw3d.renderer.GeometryManager.GeometryInfo;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class Renderer {

	ContextCapabilities capabilities;

	GeometryManager geometryManager;
	MaterialManager materialManager;

	// TODO: Create matrix class
	FloatBuffer transformMatrixBuffer;

	Set<GeometryNode> renderNodes = new HashSet<GeometryNode>();

	public Renderer() {
		// Create the display.
		try {
			Display.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		capabilities = GLContext.getCapabilities();

		// TODO: Optionally set (core) profile

		// Demand VBO support
		if (!capabilities.GL_ARB_vertex_buffer_object)
			return;

		// Demand shaders
		// TODO: allow fallback to fixed function?
		if (!capabilities.GL_ARB_shader_objects)
			return;

		if (capabilities.OpenGL30)
			System.out.println("OpenGL30");
		// TODO: Check for ARBVertexProgram instead
		else if (capabilities.OpenGL20)
			System.out.println("OpenGL20");
		else if (capabilities.OpenGL15)
			System.out.println("OpenGL15");
		else if (capabilities.OpenGL14)
			System.out.println("OpenGL14");
		else if (capabilities.OpenGL11)
			System.out.println("OpenGL11");

		geometryManager = new GeometryManager();
		materialManager = new MaterialManager();

		transformMatrixBuffer = BufferUtils.createFloatBuffer(16);
		for(int i = 0; i < 16; i++) {
			transformMatrixBuffer.put((float)i / 160f);
		}
		transformMatrixBuffer.flip();
	}

	// TODO: Change argument to RenderPass(Node, FBO)
	public void render(Node rootNode, CameraNode cameraNode) {
		renderNodes.clear();

		ProcessNode(rootNode, cameraNode.getPosition().mult(-1f));

		// Current objects. Used for performence.
		Geometry currentGeometry = null;
		int currentVAOhandle = -1;
		GeometryInfo geometryInfo = null;

		Iterator<GeometryNode> it = renderNodes.iterator();
		while (it.hasNext()) {
			GeometryNode geometryNode = it.next();
			// TODO: position -> transform
			// TODO: absolutePosition -> get transform from array made by
			// ProcessNode
			Vector3f position = geometryNode.getAbsolutePosition();

			Geometry geometry = geometryNode.getGeometry();

			if (currentGeometry != geometry) {

				geometryInfo = geometryManager.getGeometryInfo(geometry);

				if (currentVAOhandle != geometryInfo.VAO) {
					// Bind VAO
					ARBVertexArrayObject.glBindVertexArray(geometryInfo.VAO);
					currentVAOhandle = geometryInfo.VAO;
				}
			}

			// TODO: Set meterial ( shader, uniforms )
			int shaderProgram = materialManager
					.getShaderProgramHandle(geometryNode.getMaterial().shaderProgram);
			ARBShaderObjects.glUseProgramObjectARB(shaderProgram);

			// TODO: Set transformation uniform(s)
			int matrixLocation = ARBShaderObjects.glGetUniformLocationARB(
					shaderProgram, "transformMatrix");
			System.out.println("transformationMAtrix location: " + matrixLocation);
			ARBShaderObjects.glUniformMatrix4ARB(matrixLocation, false,
					transformMatrixBuffer);

			// Bind vertex attribute names
			ListIterator<Geometry.Attribute> lit = geometry.attributes
					.listIterator();
			int i = 0;
			while (lit.hasNext()) {
				ARBVertexShader.glBindAttribLocationARB(shaderProgram, i, lit
						.next().name);
			}

			GL11.glDrawElements(GL11.GL_TRIANGLES, geometryInfo.count,
					GL11.GL_UNSIGNED_INT, geometryInfo.indexOffset);
		}
	}

	private void ProcessNode(Node node, Vector3f position) {

		Vector3f currentPosition = position.add(node.getPosition());

		if (node instanceof GeometryNode) {
			renderNodes.add((GeometryNode) node);
		}

		Iterator<Node> it = node.getChildren().iterator();
		while (it.hasNext()) {
			ProcessNode(it.next(), currentPosition);
		}
	}
}
