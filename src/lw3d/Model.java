package lw3d;

import lw3d.renderer.CameraNode;
import lw3d.renderer.Node;

public class Model {
	Node rootNode = new Node();
	CameraNode cameraNode = new CameraNode();

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
