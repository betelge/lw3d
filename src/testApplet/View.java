package testApplet;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;

import testApplet.renderer.Renderer;

public class View {

	private Model model;

	public enum State {
		INIT, LOOP, PAUSE, CLOSING
	}

	private State state;

	private Renderer renderer;

	public View(Model model) {
		this.model = model;

		state = State.INIT;

		renderer = new Renderer();

		openGLLoop();
	}

	private void openGLLoop() {
		state = State.LOOP;

		while (state != State.CLOSING) {

			renderer.render(model.getRootNode(), model.getCameraNode());

			Display.update();
			Thread.yield();

			if (Display.isCloseRequested())
				state = State.CLOSING;

			// Yield while paused
			while (state == State.PAUSE)
				Thread.yield();
		}
	}

}
