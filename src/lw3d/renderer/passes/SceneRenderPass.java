package lw3d.renderer.passes;

import lw3d.renderer.CameraNode;
import lw3d.renderer.FBO;
import lw3d.renderer.Material;
import lw3d.renderer.Node;

public class SceneRenderPass extends RenderPass {

	private Node rootNode;
	private CameraNode cameraNode;
	private Material overrideMaterial = null;
	
	public SceneRenderPass(Node rootNode, CameraNode cameraNode) {
		this.rootNode = rootNode;
		this.cameraNode = cameraNode;
	}
		
	public SceneRenderPass(Node rootNode, CameraNode cameraNode, FBO fbo) {
		this.rootNode = rootNode;
		this.cameraNode = cameraNode;
		setFbo(fbo);
	}
	
	public SceneRenderPass(Node rootNode, CameraNode cameraNode, FBO fbo, Material overrideMaterial) {
		this.rootNode = rootNode;
		this.cameraNode = cameraNode;
		this.setOverrideMaterial(overrideMaterial);
		setFbo(fbo);
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

	public void setOverrideMaterial(Material overrideMaterial) {
		this.overrideMaterial = overrideMaterial;
	}

	public Material getOverrideMaterial() {
		return overrideMaterial;
	}
	
}
