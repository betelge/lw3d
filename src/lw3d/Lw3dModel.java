package lw3d;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lw3d.renderer.CameraNode;
import lw3d.renderer.Node;
import lw3d.renderer.passes.RenderPass;

public class Lw3dModel {
	
	protected List<RenderPass> renderPasses = new ArrayList<RenderPass>();
	
	protected final Set<Node> simulatedNodes = new HashSet<Node>();
	
	public void setCameraNode(CameraNode cameraNode) {
		this.cameraNode = cameraNode;
	}

	private LinkedHashSet<Integer> keys = new LinkedHashSet<Integer>();
	
	public boolean vsync = false;
	
	private int drawWidth, drawHeight;
	
	public int getDrawWidth() {
		return drawWidth;
	}

	public void setDrawWidth(int drawWidth) {
		this.drawWidth = drawWidth;
	}

	public int getDrawHeight() {
		return drawHeight;
	}

	public void setDrawHeight(int drawHeight) {
		this.drawHeight = drawHeight;
	}

	final public RendererMode rendererMode = RendererMode.SHADERS;
	
	public enum RendererMode {
		SHADERS, FIXED_VERTEX, FIXED
	}
	
	// Used to redirect drawing to an applet area
	final private Canvas displayParent;
	
	protected CameraNode cameraNode;
	
	public Lw3dModel() {
		this(null);
	}

	public Lw3dModel(Canvas displayParent) {
		this.displayParent = displayParent;		
	}

	public List<RenderPass> getRenderPasses() {
		return renderPasses;
	}

	public Set<Node> getSimulatedNodes() {
		return simulatedNodes;
	}

	public RendererMode getRendererMode() {
		return rendererMode;
	}

	public CameraNode getCameraNode() {
		return cameraNode;
	}

	public LinkedHashSet<Integer> getKeys() {
		return keys;
	}

	public Canvas getDisplayParent() {
		return displayParent;
	}
}
