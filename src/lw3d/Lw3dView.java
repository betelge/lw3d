package lw3d;

import java.util.Iterator;
import java.util.List;

import lw3d.math.Quaternion;
import lw3d.math.Vector3f;
import lw3d.renderer.Renderer;
import lw3d.renderer.managers.FBOManager;
import lw3d.renderer.passes.QuadRenderPass;
import lw3d.renderer.passes.RenderMultiPass;
import lw3d.renderer.passes.RenderPass;
import lw3d.renderer.passes.SceneRenderPass;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Lw3dView {

	private Lw3dModel model;

	public enum State {
		INIT, LOOP, PAUSE, CLOSING
	}

	private State state;

	private Thread openGLThread;
	private Renderer renderer;

	private long time;
	private long lastFPSTime;
	private int frames = 0;

	public Lw3dView(Lw3dModel model) {
		this.model = model;
		state = State.INIT;
		
		// Try to set vsync on/off
		Display.setVSyncEnabled(model.vsync);

		openGLThread = new Thread(openGLRunnable, "OpenGL");
		openGLThread.start();
		
		// Wait until the view is initialized
		while(state == State.INIT)
			Thread.yield();
	}
	
	private final Runnable nonOpenGLRunnable = new Runnable() {
		public void run() {
			
		}
	};

	private final Runnable openGLRunnable = new Runnable() {
		public void run() {
			
			// Create the display.
			try {
				Display.setParent(model.getDisplayParent());
				Display.create();
				Keyboard.create();
				Mouse.create();
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(model.getDisplayParent() != null) {
				model.setDrawWidth(
						model.getDisplayParent().getWidth());
				model.setDrawHeight(
						model.getDisplayParent().getHeight());
			} else {
				model.setDrawWidth(
						Display.getDisplayMode().getWidth());
				model.setDrawHeight(
						Display.getDisplayMode().getHeight());
			}
						
			renderer = new Renderer(45f, 0.01f, 1000f,
					model.isUseFixedVertexPipeline());
			
			state = State.LOOP;
			lastFPSTime = Sys.getTime();

			while (state != State.CLOSING) {

				List<RenderPass> renderPasses = model.getRenderPasses();
				synchronized (renderPasses) {
					processRenderPasses(renderPasses);
				}

				Display.update();
				Thread.yield();

				// FPS counter
				frames++;
				time = Sys.getTime();
				if (time - lastFPSTime > 1000) {
					System.out.println("FPS: " + frames);
					frames = 0;
					lastFPSTime = time;
				}

				// Yield while paused
				while (state == State.PAUSE)
					Thread.yield();
			}
			
			// Clean up
			Mouse.destroy();
			Keyboard.destroy();
			Display.destroy();
		}

		private void processRenderPasses(List<RenderPass> renderPasses) {
			Iterator<RenderPass> it = renderPasses.iterator();
			while(it.hasNext()) {
				
				RenderPass renderPass = it.next();
				
				if(renderPass instanceof SceneRenderPass)
					synchronized (((SceneRenderPass) renderPass).getRootNode()) {
					renderer.renderScene(
							((SceneRenderPass) renderPass).getRootNode(),
							((SceneRenderPass) renderPass).getCameraNode(),
							renderPass.getFbo());
				}
				else if(renderPass instanceof QuadRenderPass) {
					renderer.renderQuad(
							((QuadRenderPass) renderPass).getMaterial(),
							renderPass.getFbo());
				}
				else if(renderPass instanceof RenderMultiPass) {
					processRenderPasses(((RenderMultiPass) renderPass).getRenderPasses());
				}
			}
		}
	};

	public void exit() {
		state = State.CLOSING;
	}

}
