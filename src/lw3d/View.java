package lw3d;

import lw3d.renderer.Renderer;

import org.lwjgl.Sys;
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

		openGLThread = new Thread(openGLRunnable, "OpenGL");
		openGLThread.start();
	}

	private final Runnable openGLRunnable = new Runnable() {
		public void run() {
			state = State.INIT;
			renderer = new Renderer(45f, 0.01f, 1000f);
			
			state = State.LOOP;
			lastFPSTime = Sys.getTime();

			while (state != State.CLOSING) {

				renderer.render(model.getRootNode(), model.getCameraNode());

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

				if (Display.isCloseRequested())
					state = State.CLOSING;

				// Yield while paused
				while (state == State.PAUSE)
					Thread.yield();
			}
		}
	};

}
