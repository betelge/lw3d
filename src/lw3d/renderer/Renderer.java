package lw3d.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lw3d.Lw3dModel.RendererMode;
import lw3d.math.Quaternion;
import lw3d.math.Transform;
import lw3d.math.Vector3f;
import lw3d.renderer.managers.FBOManager;
import lw3d.renderer.managers.GeometryManager;
import lw3d.renderer.managers.RenderBufferManager;
import lw3d.renderer.managers.ShaderManager;
import lw3d.renderer.managers.TextureManager;
import lw3d.renderer.managers.GeometryManager.GeometryInfo;
import lw3d.renderer.passes.SetPass;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ARBPointSprite;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class Renderer {

	ContextCapabilities capabilities;

	final public RendererMode rendererMode;
	
	final int width, height;

	GeometryManager geometryManager;
	ShaderManager shaderManager;
	TextureManager textureManager;
	RenderBufferManager renderBufferManager;
	FBOManager fboManager;

	// TODO: fix this in a better way
	// Current camera transform
	Transform cameraTransform;

	// TODO: Create matrix class?
	FloatBuffer modelViewMatrix;
	FloatBuffer perspectiveMatrix;
	FloatBuffer normalMatrix;

	List<GeometryNode> renderNodes = new ArrayList<GeometryNode>();
	List<Transform> renderTransforms = new ArrayList<Transform>();
	Transform lightTransform = new Transform();

	// Function as a "backbuffer" for the procesnode to write to. Are then
	// swapped with the "front"
	List<GeometryNode> backRenderNodes = new ArrayList<GeometryNode>();
	List<Transform> backRenderTransforms = new ArrayList<Transform>();
	Transform backLightTransform = new Transform();
	
	// Current states
	FBO oldFBO = null;

	long time = 0;

	public Renderer(int width, int height, RendererMode rendererMode) {
		this.width = width;
		this.height = height;
		this.rendererMode = rendererMode;
		
		capabilities = GLContext.getCapabilities();

		// TODO: Optionally set (core) profile

		// Demand VBO support
		if (!capabilities.GL_ARB_vertex_buffer_object) {
			System.out.println("No VBO support.");
			return;
		}

		// Demand shaders
		// TODO: allow fallback to fixed function?
		if (!capabilities.GL_ARB_shader_objects) {
			System.out.println("No shader support.");
			return;
		}

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

		geometryManager = new GeometryManager(rendererMode);
		shaderManager = new ShaderManager(rendererMode);
		textureManager = new TextureManager();
		renderBufferManager = new RenderBufferManager();
		fboManager = new FBOManager(textureManager, renderBufferManager);

		// Initialize model-view matrix
		modelViewMatrix = BufferUtils.createFloatBuffer(16);

		// Initialize normal matrix
		normalMatrix = BufferUtils.createFloatBuffer(9);

		// Allocate perspective matrix
		perspectiveMatrix = BufferUtils.createFloatBuffer(16);

		if (rendererMode != RendererMode.SHADERS) {
			GL11.glEnable(GL11.GL_LIGHTING);

			FloatBuffer ambientColorBuffer = ByteBuffer.allocateDirect(16)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();

			ambientColorBuffer.put(new float[] { 1f, 1f, 1f, 1f });
			ambientColorBuffer.flip();

			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambientColorBuffer);

			// Create a directional light (w=0)
			GL11.glEnable(GL11.GL_LIGHT0);
			ByteBuffer light0PositionBuffer = ByteBuffer.allocateDirect(4 * 4);
			light0PositionBuffer.order(ByteOrder.nativeOrder());
			light0PositionBuffer.putFloat(1f);
			light0PositionBuffer.putFloat(1f);
			light0PositionBuffer.putFloat(1f);
			light0PositionBuffer.putFloat(0f);
			light0PositionBuffer.flip();
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, light0PositionBuffer
					.asFloatBuffer());
		}
		
		// Enable back-faces culling
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	static public void setPerspectiveMatrix( FloatBuffer perspectiveMatrix, 
			float aspect, float fov, float zNear, float zFar ) {
		// Assumes that perspectveMatrix has size >=16 and the position 0.
		
		perspectiveMatrix.put(getPerspectiveMatrix(aspect, fov, zNear, zFar));
		perspectiveMatrix.flip();
	}
	
	static public float[] getPerspectiveMatrix( 
			float aspect, float fov, float zNear, float zFar ) {
		// Assumes that perspectveMatrix has size >=16 and the position 0.
		
		float[] floats = new float[16]; 
				
		float h = 1f / (float) Math.tan(fov * (float) Math.PI / 360f);
		floats[0] = h / aspect;
		floats[1] = 0f;
		floats[2] = 0f;
		floats[3] = 0f;
		
		floats[4] = 0f;
		floats[5] = h;
		floats[6] = 0f;
		floats[7] = 0f;
	
		floats[8] = 0f;
		floats[9] = 0f;
		floats[10] = (zNear + zFar) / (zNear - zFar);
		floats[11] = -1f;
	
		floats[12] = 0f;
		floats[13] = 0f;
		floats[14] = 2f * (zNear * zFar) / (zNear - zFar);
		floats[15] = 0f;
		
		return floats;
	}
	
	public void setState(SetPass.State state, boolean set) {
		switch(state) {
		case DEPTH_WRITE:
			GL11.glDepthMask(set);
			break;
		default:	
			if(set)
				GL11.glEnable(state.getValue());
			else
				GL11.glDisable(state.getValue());
		}
	}
	
	public void clear(int bufferBits) {
		clear(bufferBits, null);
	}
	
	public void clear(int bufferBits, FBO fbo) {
		// Bind FBO
		bindFBO(fbo);

		// Clear color and depth buffers
		GL11.glClear(bufferBits);
	}

	public void renderQuad(Material material) {
		renderQuad(material, null);
	}

	public void renderQuad(Material material, FBO fbo) {
		if (rendererMode != RendererMode.SHADERS) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
		}

		// Bind FBO
		bindFBO(fbo);

		// Disable depth test
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		// Disable alpha
		GL11.glDisable(GL11.GL_BLEND);

		// Bind quad VAO
		GeometryInfo geometryInfo = geometryManager
				.getGeometryInfo(Geometry.QUAD);
		ARBVertexArrayObject.glBindVertexArray(geometryInfo.VAO);

		// Set shader
		if (rendererMode != RendererMode.FIXED) {
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
		}

		// Draw
		GL11.glDrawElements(GL11.GL_TRIANGLES, geometryInfo.count,
				GL11.GL_UNSIGNED_INT, geometryInfo.indexOffset);

		if (fbo != null) {
			bindFBO(null);
			fboManager.generateMipmaps(fbo);
		}

	}

	public void renderSceneNonOpenGL(Node rootNode, CameraNode cameraNode) {
		backRenderNodes.clear();
		backRenderTransforms.clear();

		cameraTransform = cameraNode.getAbsoluteTransform().getCameraTransform();
		
		setPerspectiveMatrix(perspectiveMatrix, cameraNode.getAspect(),
				cameraNode.getFov(), cameraNode.getzNear(), cameraNode.getzFar());

		time = Sys.getTime();

		ProcessNode(rootNode, new Transform());

		List<GeometryNode> tempRenderNodes = backRenderNodes;
		List<Transform> tempRenderTransforms = backRenderTransforms;
		Transform tempLightTransform = backLightTransform;

		backRenderNodes = renderNodes;
		backRenderTransforms = renderTransforms;
		backLightTransform = lightTransform;

		renderNodes = tempRenderNodes;
		renderTransforms = tempRenderTransforms;
		lightTransform = tempLightTransform;
	}

	public void renderScene(Node rootNode, CameraNode cameraNode) {
		renderScene(rootNode, cameraNode, null, null);
	}
	
	public void renderScene(Node rootNode, CameraNode cameraNode, FBO fbo) {
		renderScene(rootNode, cameraNode, fbo, null);
	}

	public void renderScene(Node rootNode, CameraNode cameraNode, FBO fbo, Material overrideMaterial) {

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
		int lightPosVectorLocation = 0;

		GeometryInfo geometryInfo = null;

		if (rendererMode != RendererMode.SHADERS) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadMatrix(perspectiveMatrix);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		// Bind FBO
		bindFBO(fbo);

		// Enable depth test
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// Enable vertex point size
		GL11.glEnable(ARBVertexShader.GL_VERTEX_PROGRAM_POINT_SIZE_ARB);
		// Enable this to get gl_PointCoord in fragment shader, for pre-OpenGL3.2
		GL11.glEnable(ARBPointSprite.GL_POINT_SPRITE_ARB);
		// Enable alpha
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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
			
			Material material;
			if(overrideMaterial == null)
				material = geometryNode.getMaterial();
			else
				material = overrideMaterial;

			// Set shader
			Map<String, Texture> textures = null;
			if (rendererMode != RendererMode.FIXED) {
				shaderProgram = material.getShaderProgram();
				if (oldShaderProgram != shaderProgram) {
					shaderProgramHandle = shaderManager
							.getShaderProgramHandle(material.getShaderProgram());
					ARBShaderObjects.glUseProgramObjectARB(shaderProgramHandle);
				}

				// Bind textures
				textures = material.getTextures();
				if (oldTextures != textures
						|| oldShaderProgramHandle != shaderProgramHandle) {
					bindTextures(shaderProgramHandle, textures);
				}

				// Upload uniforms
				uniforms = material.getUniforms();
				if (oldUniforms != uniforms
						|| oldShaderProgramHandle != shaderProgramHandle) {
					// TODO: check for changes instead?
					uploadUniforms(shaderProgramHandle, uniforms);
				}

				if (oldShaderProgram != shaderProgram && rendererMode != RendererMode.FIXED) {
					modelViewMatrixLocation = ARBShaderObjects
							.glGetUniformLocationARB(shaderProgramHandle,
									"modelViewMatrix");
					perspectiveMatrixLocation = ARBShaderObjects
							.glGetUniformLocationARB(shaderProgramHandle,
									"perspectiveMatrix");
					normalMatrixLocation = ARBShaderObjects
							.glGetUniformLocationARB(shaderProgramHandle,
									"normalMatrix");
					lightPosVectorLocation = ARBShaderObjects
							.glGetUniformLocationARB(shaderProgramHandle,
									"lightPos");
				}
				
				Vector3f lightPosVector = lightTransform.getPosition();
				ARBShaderObjects.glUniform3fARB(lightPosVectorLocation,
						lightPosVector.x, lightPosVector.y, lightPosVector.z);

				modelViewMatrix.clear();
				modelViewMatrix.put(transform.toMatrix4());
				modelViewMatrix.flip();
			}

			if (rendererMode == RendererMode.SHADERS) {
				ARBShaderObjects.glUniformMatrix4ARB(modelViewMatrixLocation,
						false, modelViewMatrix);
				ARBShaderObjects.glUniformMatrix4ARB(perspectiveMatrixLocation,
						false, perspectiveMatrix);
				normalMatrix.clear();
				normalMatrix.put(transform.getRotation().toMatrix3());
				normalMatrix.flip();
				ARBShaderObjects.glUniformMatrix3ARB(normalMatrixLocation,
						false, normalMatrix);

				// Bind vertex attributes to uniform names
				if (oldGeometryInfo != geometryInfo
						|| oldShaderProgramHandle != shaderProgramHandle)
					bindAttributes(shaderProgramHandle,
							geometryInfo.attributeNames);
			} else
				GL11.glLoadMatrix(modelViewMatrix);

			// Draw
			GL11.glDrawElements(geometry.getPrimitiveType().getValue(), geometryInfo.count,
					GL11.GL_UNSIGNED_INT, geometryInfo.indexOffset);

			oldGeometry = geometry;
			oldGeometryInfo = geometryInfo;
			oldTextures = textures;
			oldShaderProgramHandle = shaderProgramHandle;
			oldShaderProgram = shaderProgram;
			oldUniforms = uniforms;
		}

		if (fbo != null) {
			bindFBO(null);
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
		if (fbo == oldFBO) return;
		
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
			GL11.glViewport(0, 0, width, height);
		}
		
		oldFBO = fbo;
	}

	private void ProcessNode(Node node, Transform transform) {

		// System.out.println("Renderer processing node: " + node);

		Transform currentTransform = null;
		if (node instanceof Movable) {
			Movable movable = (Movable) node;
			float ratio = 0;
			if (movable.getNextTime() != movable.getLastTime())
				ratio = (time - movable.getLastTime())
						/ (movable.getNextTime() - movable.getLastTime());
			currentTransform = transform.mult(movable.getTransform()
					.interpolate(movable.getNextTransform(), ratio));
		} else {
			currentTransform = transform.mult(node.getTransform());
		}

		if (node instanceof GeometryNode) {
			backRenderNodes.add((GeometryNode) node);
			backRenderTransforms.add(cameraTransform.mult(currentTransform));
		}

		// TODO: Handle multiple lights
		if (node instanceof Light) {
			backLightTransform.set(cameraTransform.mult(currentTransform));
		}
		synchronized (node) {
			Iterator<Node> it = node.getChildren().iterator();
			while (it.hasNext()) {
				ProcessNode(it.next(), currentTransform);
			}
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
				case MATRIX2:
					ARBShaderObjects.glUniformMatrix2ARB(uniformLocation, uniforms[i].isTranspose(), uniforms[i].getMatrix());
					break;
				case MATRIX3:
					ARBShaderObjects.glUniformMatrix3ARB(uniformLocation, uniforms[i].isTranspose(), uniforms[i].getMatrix());
					break;
				case MATRIX4:
					ARBShaderObjects.glUniformMatrix4ARB(uniformLocation, uniforms[i].isTranspose(), uniforms[i].getMatrix());
					break;
				default:
					System.out.println("Error: Unknown uniform type, " + uniforms[i].getName());
					break;
				}
			}
		}
	}
}
