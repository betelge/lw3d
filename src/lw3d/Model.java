package lw3d;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import lw3d.math.Vector3f;
import lw3d.renderer.CameraNode;
import lw3d.renderer.FBO;
import lw3d.renderer.Geometry;
import lw3d.renderer.GeometryNode;
import lw3d.renderer.Material;
import lw3d.renderer.MovableGeometryNode;
import lw3d.renderer.MovableNode;
import lw3d.renderer.Node;
import lw3d.renderer.RenderBuffer;
import lw3d.renderer.ShaderProgram;
import lw3d.renderer.Texture;
import lw3d.renderer.Uniform;
import lw3d.renderer.FBOAttachable.Format;
import lw3d.renderer.Geometry.Attribute;
import lw3d.renderer.ShaderProgram.Shader;
import lw3d.renderer.Texture.Filter;
import lw3d.renderer.Texture.TexelType;
import lw3d.renderer.Texture.TextureType;
import lw3d.renderer.Texture.WrapMode;
import lw3d.renderer.managers.FBOManager;
import lw3d.renderer.passes.BloomPass;
import lw3d.renderer.passes.QuadRenderPass;
import lw3d.renderer.passes.RenderPass;
import lw3d.renderer.passes.SceneRenderPass;
import lw3d.utils.GeometryLoader;
import lw3d.utils.StringLoader;
import lw3d.utils.TextureLoader;

public class Model {
	
	private List<RenderPass> renderPasses = new ArrayList<RenderPass>();
	
	final private Set<Node> simulatedNodes = new HashSet<Node>();
	
	public boolean vsync = true;
	
	final public boolean isUseFixedVertexPipeline = false;
	
	private CameraNode cameraNode;

	public Model() {

		Geometry cubeMesh = GeometryLoader.loadObj(new File("resources/untitled.obj"));

		Set<Shader> shaders = new HashSet<Shader>();
		Set<Shader> fboShaders = new HashSet<Shader>();
		
		try {
			shaders
					.add(new Shader(
							Shader.Type.VERTEX,
							StringLoader.loadString(new File("resources/default.vertex"))));

			shaders
					.add(new Shader(
							Shader.Type.FRAGMENT,
							StringLoader.loadString(new File("resources/default.fragment"))));
			
			fboShaders
				.add(new Shader(
					Shader.Type.VERTEX,
					StringLoader.loadString(new File("resources/direct.vertex"))));
			fboShaders
				.add(new Shader(
					Shader.Type.FRAGMENT,
					StringLoader.loadString(new File("resources/direct.fragment"))));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ShaderProgram shaderProgram = new ShaderProgram(shaders);
		ShaderProgram fboShaderProgram = new ShaderProgram(fboShaders);
		Material defaultMaterial = new Material(shaderProgram);
		Material fboMaterial = new Material(fboShaderProgram);

		MovableGeometryNode cube = new MovableGeometryNode(cubeMesh, defaultMaterial);
		Uniform[] uniforms = new Uniform[1];
		uniforms[0] = new Uniform("col2", 0f, 1f, 0f, 1f);
		defaultMaterial.setUniforms(uniforms);
		
		Texture texture = null;
		try {
			texture = TextureLoader.loadTexture(new File("resources/test.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// FBO texture
		Texture fboTexture = new Texture(null, TextureType.TEXTURE_2D,
				Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight(),
				TexelType.UBYTE, Format.GL_RGBA8, Filter.LINEAR, WrapMode.CLAMP);
		//fboTexture.setMipmapLevel(0f);
		
		RenderBuffer depthBuffer = new RenderBuffer(Format.GL_DEPTH_COMPONENT,
				Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
		
		FBO myFBO = new FBO(fboTexture, depthBuffer,
				Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
		
		defaultMaterial.addTexture("texture0", texture);
		fboMaterial.addTexture("source", fboTexture);
		
		Node rootNode = new Node();
		simulatedNodes.add(rootNode);
		cameraNode = new CameraNode();
		rootNode.attach(cameraNode);
		//cameraNode.getTransform().getPosition().z = -1f;
		
		// Create render passes
		renderPasses.add(new SceneRenderPass(rootNode, cameraNode, myFBO));
		//renderPasses.add(new QuadRenderPass(fboMaterial));
		renderPasses.add(new BloomPass(fboMaterial.getTextures().get("source")));
		
		rootNode.attach(cube);
		cube.getTransform().getPosition().z = -5f;
		/*cube.getTransform().getPosition().x = 1f;
		cube.getTransform().getPosition().y = -1f;*/

		cube.getMovement().getPosition().x = 0.000f;
		cube.getMovement().getRotation().fromAngleNormalAxis(0.03f, Vector3f.UNIT_Z);
	}

	public List<RenderPass> getRenderPasses() {
		return renderPasses;
	}

	public Set<Node> getSimulatedNodes() {
		return simulatedNodes;
	}

	public boolean isUseFixedVertexPipeline() {
		return isUseFixedVertexPipeline;
	}

	public CameraNode getCameraNode() {
		return cameraNode;
	}
}
