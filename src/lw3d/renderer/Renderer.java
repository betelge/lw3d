package lw3d.renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lw3d.math.Transform;
import lw3d.renderer.ShaderProgram.Shader;
import lw3d.renderer.managers.FBOManager;
import lw3d.renderer.managers.GeometryManager;
import lw3d.renderer.managers.RenderBufferManager;
import lw3d.renderer.managers.ShaderManager;
import lw3d.renderer.managers.TextureManager;
import lw3d.renderer.managers.GeometryManager.GeometryInfo;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class Renderer {

	ContextCapabilities capabilities;
	
	final public boolean isUseFixedVertexPipeline;

	GeometryManager geometryManager;
	ShaderManager shaderManager;
	TextureManager textureManager;
	RenderBufferManager renderBufferManager;
	FBOManager fboManager;

	// TODO: Create matrix class?
	FloatBuffer modelViewMatrix;
	FloatBuffer perspectiveMatrix;
	FloatBuffer normalMatrix;

	List<GeometryNode> renderNodes = new ArrayList<GeometryNode>();
	List<Transform> renderTransforms = new ArrayList<Transform>();

	// Function as a "backbuffer" for the procesnode to write to. Are then
	// swapped with the "front"
	List<GeometryNode> backRenderNodes = new ArrayList<GeometryNode>();
	List<Transform> backRenderTransforms = new ArrayList<Transform>();

	public Renderer(float fov, float zNear, float zFar, boolean isUseFixedVertexPipeline) {
		this.isUseFixedVertexPipeline = isUseFixedVertexPipeline;
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

		geometryManager = new GeometryManager(isUseFixedVertexPipeline);
		shaderManager = new ShaderManager(isUseFixedVertexPipeline);
		textureManager = new TextureManager();
		renderBufferManager = new RenderBufferManager();
		fboManager = new FBOManager(textureManager, renderBufferManager);

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

	public void renderQuad(Material material) {
		renderQuad(material, null);
	}

	public void renderQuad(Material material, FBO fbo) {

		// Bind FBO
		bindFBO(fbo);

		// Clear color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Bind quad VAO
		GeometryInfo geometryInfo = geometryManager
				.getGeometryInfo(GeometryManager.QUAD);
		ARBVertexArrayObject.glBindVertexArray(geometryInfo.VAO);

		// Set shader
		int shaderProgram = shaderManager.getShaderProgramHandle(material
				.getShaderProgram());
		ARBShaderObjects.glUseProgramObjectARB(shaderProgram);

		// Bind textures
		bindTextures(shaderProgram, material.getTextures());

		// Upload uniforms
		Uniform[] uniforms = material.getUniforms();
		uploadUniforms(shaderProgram, uniforms);

		// Bind vertex attributes to uniform names
		bindAttributes(shaderProgram, geometryInfo.attributeNames);

		// Draw
		GL11.glDrawElements(GL11.GL_QUADS, geometryInfo.count,
				GL11.GL_UNSIGNED_INT, geometryInfo.indexOffset);
		
		if(fbo != null) {
			EXTFramebufferObject.glBindFramebufferEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
			fboManager.generateMipmaps(fbo);
		}
		 
	}

	public void renderSceneNonOpenGL(Node rootNode, CameraNode cameraNode) {
		backRenderNodes.clear();
		backRenderTransforms.clear();

		ProcessNode(rootNode, cameraNode.getTransform().invert());

		List<GeometryNode> tempRenderNodes = backRenderNodes;
		List<Transform> tempRenderTransforms = backRenderTransforms;
		backRenderNodes = renderNodes;
		backRenderTransforms = renderTransforms;
		renderNodes = tempRenderNodes;
		renderTransforms = tempRenderTransforms;
	}

	public void renderScene(Node rootNode, CameraNode cameraNode) {
		renderScene(rootNode, cameraNode, null);
	}

	public void renderScene(Node rootNode, CameraNode cameraNode, FBO fbo) {
		
		renderSceneNonOpenGL(rootNode, cameraNode);

		// Current objects. Used for performance.
		Geometry oldGeometry = null;
		GeometryInfo oldGeometryInfo = null;
		Map<String, Texture> oldTextures = null;
		int shaderProgramHandle = 0;
		int oldShaderProgramHandle = 0;
		ShaderProgram shaderProgram = null;
		ShaderProgram oldShaderProgram = null;
		Uniform[] uniforms = null;
		Uniform[] oldUniforms = null;
		int modelViewMatrixLocation = 0;
		int perspectiveMatrixLocation = 0;
		int normalMatrixLocation = 0;

		GeometryInfo geometryInfo = null;

		// Bind FBO
		bindFBO(fbo);

		// Clear color and depth buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// Enable depth test
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		Iterator<GeometryNode> it = renderNodes.iterator();
		Iterator<Transform> tit = renderTransforms.iterator();
		while (it.hasNext() && tit.hasNext()) {
			GeometryNode geometryNode = it.next();
			Transform transform = tit.next();

			Geometry geometry = geometryNode.getGeometry();

			if (oldGeometry != geometry) {

				geometryInfo = geometryManager.getGeometryInfo(geometry);

				// Bind VAO
				ARBVertexArrayObject.glBindVertexArray(geometryInfo.VAO);
			}

			// Set shader
			shaderProgram = geometryNode.getMaterial().getShaderProgram();
			if (oldShaderProgram != shaderProgram) {
				shaderProgramHandle = shaderManager
						.getShaderProgramHandle(geometryNode.getMaterial()
								.getShaderProgram());
				ARBShaderObjects.glUseProgramObjectARB(shaderProgramHandle);
			}

			// Bind textures
			Map<String, Texture> textures = geometryNode.getMaterial()
					.getTextures();
			if (oldTextures != textures) {
				bindTextures(shaderProgramHandle, textures);
			}

			// Upload uniforms
			uniforms = geometryNode.getMaterial().getUniforms();
			if (oldUniforms != uniforms
					|| oldShaderProgramHandle != shaderProgramHandle) {
				// TODO: check for changes instead?
				uploadUniforms(shaderProgramHandle, uniforms);
			}

			if (oldShaderProgram != shaderProgram) {
				modelViewMatrixLocation = ARBShaderObjects
						.glGetUniformLocationARB(shaderProgramHandle,
								"modelViewMatrix");
				perspectiveMatrixLocation = ARBShaderObjects
						.glGetUniformLocationARB(shaderProgramHandle,
								"perspectiveMatrix");
				normalMatrixLocation = ARBShaderObjects
						.glGetUniformLocationARB(shaderProgramHandle,
								"normalMatrix");
			}

			modelViewMatrix.clear();
			modelViewMatrix.put(transform.toMatrix4());
			modelViewMatrix.flip();
			ARBShaderObjects.glUniformMatrix4ARB(modelViewMatrixLocation, true,
					modelViewMatrix);
			ARBShaderObjects.glUniformMatrix4ARB(perspectiveMatrixLocation,
					false, perspectiveMatrix);
			normalMatrix.clear();
			normalMatrix.put(transform.getRotation().toMatrix3());
			normalMatrix.flip();
			ARBShaderObjects.glUniformMatrix3ARB(normalMatrixLocation, false,
					normalMatrix);

			// Bind vertex attributes to uniform names
			if (oldGeometryInfo != geometryInfo
					|| oldShaderProgramHandle != shaderProgramHandle)
				bindAttributes(shaderProgramHandle, geometryInfo.attributeNames);

			// Draw
			GL11.glDrawElements(GL11.GL_TRIANGLES, geometryInfo.count,
					GL11.GL_UNSIGNED_INT, geometryInfo.indexOffset);

			oldGeometry = geometry;
			oldGeometryInfo = geometryInfo;
			oldTextures = textures;
			oldShaderProgramHandle = shaderProgramHandle;
			oldShaderProgram = shaderProgram;
			oldUniforms = uniforms;
		}

		if(fbo != null) {
			EXTFramebufferObject.glBindFramebufferEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
			fboManager.generateMipmaps(fbo);
		}

	}

	private void bindAttributes(int shaderProgram, String[] attributeNames) {
		for (int i = 0; i < attributeNames.length; i++) {
			ARBVertexShader.glBindAttribLocationARB(shaderProgram, i,
					attributeNames[i]);
		}
	}

	private void bindTextures(int shaderProgramHandle,
			Map<String, Texture> textures) {
		Iterator<String> it = textures.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			String name = it.next();
			Texture texture = textures.get(name);

			int textureLocation = ARBShaderObjects.glGetUniformLocationARB(
					shaderProgramHandle, name);
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(texture.getTextureType().getValue(),
					textureManager.getTextureHandle(texture));
			ARBShaderObjects.glUniform1iARB(textureLocation, i);

			i++;
		}
	}

	private void bindFBO(FBO fbo) {
		if (fbo != null) {

			if (!fboManager.isComplete(fbo))
				System.out.println("FBO not complete!");

			EXTFramebufferObject.glBindFramebufferEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboManager
							.getFBOHandle(fbo));

			GL11.glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
		} else {
			EXTFramebufferObject.glBindFramebufferEXT(
					EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
			GL11.glViewport(0, 0, Display.getDisplayMode().getWidth(), Display
					.getDisplayMode().getHeight());
		}
	}

	private void ProcessNode(Node node, Transform transform) {

		Transform currentTransform = transform.mult(node.getTransform());

		if (node instanceof GeometryNode) {
			backRenderNodes.add((GeometryNode) node);
			backRenderTransforms.add(currentTransform);
		}

		Iterator<Node> it = node.getChildren().iterator();
		while (it.hasNext()) {
			ProcessNode(it.next(), currentTransform);
		}
	}

	private void uploadUniforms(int shaderProgram, Uniform[] uniforms) {
		if (uniforms != null) {
			for (int i = 0; i < uniforms.length; i++) {
				int uniformLocation = ARBShaderObjects.glGetUniformLocationARB(
						shaderProgram, uniforms[i].getName());
				float[] floats = uniforms[i].getFloats();
				switch (uniforms[i].getType()) {
				case FLOAT:
					ARBShaderObjects.glUniform1fARB(uniformLocation, floats[0]);
					break;
				case FLOAT2:
					ARBShaderObjects.glUniform2fARB(uniformLocation, floats[0],
							floats[1]);
					break;
				case FLOAT3:
					ARBShaderObjects.glUniform3fARB(uniformLocation, floats[0],
							floats[1], floats[2]);
					break;
				case FLOAT4:
					ARBShaderObjects.glUniform4fARB(uniformLocation, floats[0],
							floats[1], floats[2], floats[3]);
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
							(int) floats[0], (int) floats[1], (int) floats[2]);
					break;
				case INT4:
					ARBShaderObjects.glUniform4iARB(uniformLocation,
							(int) floats[0], (int) floats[1], (int) floats[2],
							(int) floats[3]);
					break;
				default:
					break;
				}
			}
		}
	}
}
