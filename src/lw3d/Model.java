package lw3d;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.BufferUtils;

import lw3d.renderer.CameraNode;
import lw3d.renderer.Geometry;
import lw3d.renderer.GeometryNode;
import lw3d.renderer.Material;
import lw3d.renderer.Node;
import lw3d.renderer.ShaderProgram;
import lw3d.renderer.Texture;
import lw3d.renderer.Uniform;
import lw3d.renderer.Geometry.Attribute;
import lw3d.renderer.ShaderProgram.Shader;
import lw3d.utils.GeometryLoader;
import lw3d.utils.StringLoader;
import lw3d.utils.TextureLoader;

public class Model {
	Node rootNode = new Node();
	CameraNode cameraNode = new CameraNode();

	public Model() {
		/*IntBuffer indices = BufferUtils.createIntBuffer(6);
		for (int i = 0; i < 6; i++)
			indices.put(i);
		indices.flip();

		Attribute positions = new Attribute();
		positions.name = "position";
		positions.type = Geometry.Type.FLOAT;
		positions.size = 3;
		positions.buffer = BufferUtils.createFloatBuffer(4 * 6 * 3);
		((FloatBuffer) positions.buffer).put(-1f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(0f);

		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(0f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(0);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(-1f);
		((FloatBuffer) positions.buffer).put(0f);
		positions.buffer.flip();

		ArrayList<Attribute> aList = new ArrayList<Attribute>();
		aList.add(positions);

		Geometry cubeMesh = new Geometry(indices, aList);*/
		
		Geometry cubeMesh = GeometryLoader.loadObj(new File("resources/cube.obj"));

		Set<Shader> shaders = new HashSet<Shader>();
		try {
			shaders
					.add(new Shader(
							Shader.Type.VERTEX,
							StringLoader.loadString(new File("resources/default.vertex"))));

			shaders
					.add(new Shader(
							Shader.Type.FRAGMENT,
							StringLoader.loadString(new File("resources/default.fragment"))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ShaderProgram shaderProgram = new ShaderProgram(shaders);
		Material defaultMaterial = new Material(shaderProgram);

		GeometryNode cube = new GeometryNode(cubeMesh, defaultMaterial);
		Uniform[] uniforms = new Uniform[1];
		uniforms[0] = new Uniform("col2", 0f, 1f, 0f, 1f);
		defaultMaterial.setUniforms(uniforms);
		
		Texture[] textures = new Texture[1];
		try {
			textures[0] = TextureLoader.loadTexture(new File("resources/test.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textures[0].setName("texture0");
		defaultMaterial.setTextures(textures);

		rootNode.attach(cube);
		cube.getTransform().getPosition().z = -5f;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	public CameraNode getCameraNode() {
		return cameraNode;
	}

	public void setCameraNode(CameraNode cameraNode) {
		this.cameraNode = cameraNode;
	}
}
