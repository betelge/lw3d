package lw3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.BufferUtils;

import lw3d.renderer.CameraNode;
import lw3d.renderer.Geometry;
import lw3d.renderer.GeometryManager;
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
		IntBuffer indices = BufferUtils.createIntBuffer(3);
		for(int i = 0; i < 3; i++)
			indices.put(i);
		indices.flip();
		
		Attribute positions = new Attribute();
		positions.name = "vertPositions";
		positions.type = Geometry.Type.FLOAT;
		positions.size = 3;
		positions.buffer = BufferUtils.createFloatBuffer(4 * 3*3);
		((FloatBuffer) positions.buffer).put(-1f);
		((FloatBuffer) positions.buffer).put(-1f);
		((FloatBuffer) positions.buffer).put(0);
		((FloatBuffer) positions.buffer).put(-1f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(0);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(1f);
		((FloatBuffer) positions.buffer).put(0);
		positions.buffer.flip();
		
		ArrayList<Attribute> aList= new ArrayList<Attribute>();
		aList.add(positions);
		
		Geometry cubeMesh = new Geometry(indices, aList);
		
		Set<Shader> shaders = new HashSet<Shader>();
		shaders.add(new Shader(Shader.Type.VERTEX, "#version 120\nvoid main()\n{\ngl_Position = vec4(0.0,0.0,0.0,1.0);\n}\n"));
		shaders.add(new Shader(Shader.Type.FRAGMENT, "#version 120\nvoid main()\n{\ngl_FragColor = vec4(1.0,0.0,0.0,1.0);\n}\n"));
		ShaderProgram shaderProgram = new ShaderProgram(shaders);
		Material defaultMaterial = new Material(shaderProgram);
		
		GeometryNode cube = new GeometryNode(cubeMesh, defaultMaterial);
		
		rootNode.attach(cube);
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
