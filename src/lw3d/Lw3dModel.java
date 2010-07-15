package lw3d;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import lw3d.math.Quaternion;
import lw3d.math.Transform;
import lw3d.math.Vector3f;
import lw3d.renderer.CameraNode;
import lw3d.renderer.FBO;
import lw3d.renderer.Geometry;
import lw3d.renderer.GeometryNode;
import lw3d.renderer.Light;
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

	final public boolean isUseFixedVertexPipeline = false;
	
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

	public boolean isUseFixedVertexPipeline() {
		return isUseFixedVertexPipeline;
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
