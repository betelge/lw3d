package lw3d.renderer;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import lw3d.math.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexProgram;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;


public class Renderer {

	ContextCapabilities capabilities;
	
	GeometryManager geometryManager;

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
	}

	// TODO: Change argument to RenderPass(Node, FBO)
	public void render(Node rootNode, CameraNode cameraNode) {
		renderNodes.clear();

		ProcessNode(rootNode, cameraNode.getPosition().mult(-1f));

		Iterator<GeometryNode> it = renderNodes.iterator();
		while (it.hasNext()) {
			GeometryNode geometryNode = it.next();
			// TODO: position -> transform
			// TODO: absolutePosition -> get transform from array made by ProcessNode
			Vector3f position = geometryNode.getAbsolutePosition();
			
			Geometry geometry = geometryNode.getGeometry();
			
			/*
			 * 		TODO:	Use VAOs!
			 */
			
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB,
					geometryManager.getIndexVBOHandle(geometry));
			ARBVertexBufferObject.glBindBufferARB(
					ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
					geometryManager.getDataVBOHandle(geometry));
			
			
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
