package lw3d.renderer.passes;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderMultiPass extends RenderPass {

	protected List<RenderPass> renderPasses = new ArrayList<RenderPass>();

	public List<RenderPass> getRenderPasses() {
		return renderPasses;
	}
	
}
