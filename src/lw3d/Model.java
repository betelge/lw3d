package lw3d;

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
import lw3d.renderer.Geometry.Attribute;
import lw3d.renderer.ShaderProgram.Shader;

public class Model {
	Node rootNode = new Node();
	CameraNode cameraNode = new CameraNode();
	
	public Model() {
		IntBuffer indices = BufferUtils.createIntBuffer(6);
		for(int i = 0; i < 6; i++)
			indices.put(i);
		indices.flip();
		
		Attribute positions = new Attribute();
		positions.name = "pos";
		positions.type = Geometry.Type.FLOAT;
		positions.size = 3;
		positions.buffer = BufferUtils.createFloatBuffer(4 * 6*3);
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
		
		ArrayList<Attribute> aList= new ArrayList<Attribute>();
		aList.add(positions);
		
		Geometry cubeMesh = new Geometry(indices, aList);
		
		Set<Shader> shaders = new HashSet<Shader>();
		shaders.add(new Shader(Shader.Type.VERTEX, "#version 120\nuniform mat4 transformMatrix;\nuniform mat4 perspectiveMatrix;\nattribute vec4 pos;\nvarying vec4 col;\nvoid main()\n{\ncol= pos;\ngl_Position = perspectiveMatrix * (transformMatrix * vec4(pos.xyz,1.0));\n}\n"));
		shaders.add(new Shader(Shader.Type.FRAGMENT, "#version 120\nvarying vec4 col;\nvoid main()\n{\ngl_FragColor = vec4(col);\n}\n"));
		ShaderProgram shaderProgram = new ShaderProgram(shaders);
		Material defaultMaterial = new Material(shaderProgram);
		
		GeometryNode cube = new GeometryNode(cubeMesh, defaultMaterial);
		
		rootNode.attach(cube);
		cube.getPosition().z = -5f;
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
