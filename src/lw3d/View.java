package lw3d;

import lw3d.math.Quaternion;
import lw3d.math.Vector3f;
import lw3d.renderer.Renderer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class View {

	private Model model;

	public enum State {
		INIT, LOOP, PAUSE, CLOSING
	}

	private State state;

	private Thread openGLThread;
	private Renderer renderer;

	private long time;
	private long lastFPSTime;
	private int frames = 0;

	public View(Model model) {
		this.model = model;
		
		// Try to set vsync on/off
		Display.setVSyncEnabled(model.vsync);

		openGLThread = new Thread(openGLRunnable, "OpenGL");
		openGLThread.start();
	}

	private final Runnable openGLRunnable = new Runnable() {
		public void run() {
			state = State.INIT;
			
			// Create the display.
			try {
				Display.create();
				Keyboard.create();
				Mouse.create();
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			renderer = new Renderer(45f, 0.01f, 1000f);
			
			state = State.LOOP;
			lastFPSTime = Sys.getTime();

			while (state != State.CLOSING) {

				synchronized (model.getRootNode()) {
					renderer.render(model.getRootNode(), model.getCameraNode());
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
				
				model.cube.getTransform().getRotation().multThis(
						new Quaternion().fromAngleAxis(0.05f, Vector3f.UNIT_Y));
				
				if (Display.isCloseRequested())
					state = State.CLOSING;

				// Yield while paused
				while (state == State.PAUSE)
					Thread.yield();
			}
			
			// Clean up
			Mouse.destroy();
			Keyboard.destroy();
			Display.destroy();
		}
	};

	public void exit() {
		state = State.CLOSING;
	}

}
