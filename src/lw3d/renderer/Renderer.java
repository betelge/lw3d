package lw3d.renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lw3d.math.Transform;
import lw3d.renderer.GeometryManager.GeometryInfo;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class Renderer {

	ContextCapabilities capabilities;

	GeometryManager geometryManager;
	ShaderManager shaderManager;
	TextureManager textureManager;

	// TODO: Create matrix class?
	FloatBuffer modelViewMatrix;
	FloatBuffer perspectiveMatrix;
	FloatBuffer normalMatrix;

	List<GeometryNode> renderNodes = new ArrayList<GeometryNode>();
	List<Transform> renderTransforms = new ArrayList<Transform>();

	public Renderer(float fov, float zNear, float zFar) {
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
		shaderManager = new ShaderManager();
		textureManager = new TextureManager();

		// Initialize model-view matrix
		modelViewMatrix = BufferUtils.createFloatBuffer(16);
		
		// Initialize normal matrix
		normalMatrix = BufferUtils.createFloatBuffer(9);

		// Initialize perspecitve matrix
		perspectiveMatrix = BufferUtils.createFloatBuffer(16);
		float h = 1f / (float) Math.tan(fov * (float) Math.PI / 360f);
		float aspect = Display.getDisplayMode().getWidth()
				/ (float) Display.getDisplayMode().getHeight();
		perspectiveMatrix.put(h / aspect);
		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(0f);

		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(h);
		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(0f);

		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(0f);
		perspectiveMatrix.put((zNear + zFar) / (zNear - zFar));
		perspectiveMatrix.put(-1f);

		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(0f);
		perspectiveMatrix.put(2f * (zNear * zFar) / (zNear - zFar));
		perspectiveMatrix.put(0f);

		perspectiveMatrix.flip();
	}

	// TODO: Change argument to RenderPass(Node, FBO)
	public void render(Node rootNode, CameraNode cameraNode) {
		renderNodes.clear();
		renderTransforms.clear();

		ProcessNode(rootNode, cameraNode.getTransform().invert());

		// Current objects. Used for performance.
		Geometry currentGeometry = null;
		int currentVAOhandle = -1;
		GeometryInfo geometryInfo = null;
		
		// Clear color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		// Enable depth test
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		Iterator<GeometryNode> it = renderNodes.iterator();
		Iterator<Transform> tit = renderTransforms.iterator();
		while (it.hasNext() && tit.hasNext()) {
			GeometryNode geometryNode = it.next();
			Transform transform = tit.next();

			Geometry geometry = geometryNode.getGeometry();

			if (currentGeometry != geometry) {

				geometryInfo = geometryManager.getGeometryInfo(geometry);

				if (currentVAOhandle != geometryInfo.VAO) {
					// Bind VAO
					ARBVertexArrayObject.glBindVertexArray(geometryInfo.VAO);
					currentVAOhandle = geometryInfo.VAO;
				}
			}

			// Set shader
			int shaderProgram = shaderManager
					.getShaderProgramHandle(geometryNode.getMaterial()
							.getShaderProgram());
			ARBShaderObjects.glUseProgramObjectARB(shaderProgram);

			// Bind textures
			Texture[] textures = geometryNode.getMaterial().getTextures();
			if (textures != null) {

				for (int i = 0; i < textures.length; i++) {
					int textureLocation = ARBShaderObjects
							.glGetUniformLocationARB(shaderProgram, textures[i]
									.getName());
					GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
					GL11.glBindTexture(textures[i].getTextureType().getValue(),
							textureManager.getTextureHandle(textures[i]));
					ARBShaderObjects.glUniform1iARB(textureLocation, i);
				}
			}

			// Upload uniforms
			// TODO: check for changes instead?
			Uniform[] uniforms = geometryNode.getMaterial().getUniforms();
			if (uniforms != null) {
				for (int i = 0; i < uniforms.length; i++) {
					int uniformLocation = ARBShaderObjects
							.glGetUniformLocationARB(shaderProgram, uniforms[i]
									.getName());
					float[] floats = uniforms[i].getFloats();
					switch (uniforms[i].getType()) {
					case FLOAT:
						ARBShaderObjects.glUniform1fARB(uniformLocation,
								floats[0]);
						break;
					case FLOAT2:
						ARBShaderObjects.glUniform2fARB(uniformLocation,
								floats[0], floats[1]);
						break;
					case FLOAT3:
						ARBShaderObjects.glUniform3fARB(uniformLocation,
								floats[0], floats[1], floats[2]);
						break;
					case FLOAT4:
						ARBShaderObjects.glUniform4fARB(uniformLocation,
								floats[0], floats[1], floats[2], floats[3]);
						break;
					case INT:
						ARBShaderObjects.glUniform1iARB(uniformLocation,
								(int) floats[0]);
						break;
					case INT2:
						ARBShaderObjects.glUniform2iARB(uniformLocation,
								(int) floats[0], (int) floats[1]);
						break;
					case INT3:
						ARBShaderObjects.glUniform3iARB(uniformLocation,
								(int) floats[0], (int) floats[1],
								(int) floats[2]);
						break;
					case INT4:
						ARBShaderObjects.glUniform4iARB(uniformLocation,
								(int) floats[0], (int) floats[1],
								(int) floats[2], (int) floats[3]);
						break;
					default:
						break;
					}
				}
			}

			modelViewMatrix.clear();
			modelViewMatrix.put(transform.toMatrix4());
			modelViewMatrix.flip();
			int matrixLocation = ARBShaderObjects.glGetUniformLocationARB(
					shaderProgram, "modelViewMatrix");
			ARBShaderObjects.glUniformMatrix4ARB(matrixLocation, true,
					modelViewMatrix);
			matrixLocation = ARBShaderObjects.glGetUniformLocationARB(
					shaderProgram, "perspectiveMatrix");
			ARBShaderObjects.glUniformMatrix4ARB(matrixLocation, false,
					perspectiveMatrix);
			normalMatrix.clear();
			normalMatrix.put(transform.getRotation().toMatrix3());
			normalMatrix.flip();
			matrixLocation = ARBShaderObjects.glGetUniformLocationARB(
					shaderProgram, "normalMatrix");
			ARBShaderObjects.glUniformMatrix3ARB(matrixLocation, false,
					normalMatrix);

			// Bind vertex attributes to uniform names
			for(int i = 0; i < geometryInfo.attributeNames.length; i++) {
				ARBVertexShader.glBindAttribLocationARB(shaderProgram, i,
						geometryInfo.attributeNames[i]);
			}

			GL11.glDrawElements(GL11.GL_TRIANGLES, geometryInfo.count,
					GL11.GL_UNSIGNED_INT, geometryInfo.indexOffset);
		}
	}

	private void ProcessNode(Node node, Transform transform) {

		Transform currentTransform = transform.mult(node.getTransform());

		if (node instanceof GeometryNode) {
			renderNodes.add((GeometryNode) node);
			renderTransforms.add(currentTransform);
		}

		Iterator<Node> it = node.getChildren().iterator();
		while (it.hasNext()) {
			ProcessNode(it.next(), currentTransform);
		}
	}
}
