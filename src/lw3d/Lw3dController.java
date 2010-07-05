package lw3d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public abstract class Lw3dController {

	private Lw3dModel model;
	private Lw3dView view;

	Lw3dSimulator simulator;

	Thread inputThread;
	private boolean isRunning = true;
	private Runnable inputRunnable = new Runnable() {
		@Override
		public void run() {

			simulator.start();

			while (isRunning) {

				while (Keyboard.next()) {
					lw3dOnKey(Keyboard.getEventKey(), Keyboard.getEventKeyState(),
							Keyboard.isRepeatEvent());
					onKey(Keyboard.getEventKey(), Keyboard.getEventKeyState(),
							Keyboard.isRepeatEvent());
					if (!isRunning)
						continue;
				}

				if (isRunning)
					while (Mouse.next()) {
						if (Mouse.getEventButton() != -1)
							onMouseButton(Mouse.getEventButton(), Mouse
									.getEventButtonState(), Mouse.getEventX(),
									Mouse.getEventY());
						else {
							if (Mouse.getEventDWheel() != 0)
								onMouseWheel(Mouse.getEventDWheel(), Mouse
										.getEventX(), Mouse.getEventY());
							if (Mouse.getEventDX() != 0
									|| Mouse.getEventDY() != 0)
								onMouseMove(Mouse.getEventDX(), Mouse
										.getEventDY(), Mouse.getEventX(), Mouse
										.getEventY());
						}
					}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (isRunning)
					if (Display.isCloseRequested())
						exit();
			}
		}
	};

	public Lw3dController(Lw3dModel model, Lw3dView view) {
		this.model = model;
		this.view = view;

		simulator = new Lw3dSimulator(model.getSimulatedNodes());
		simulator.start();

		inputThread = new Thread(inputRunnable, "inputThread");
		inputThread.start();
	}

	abstract protected void onMouseMove(int dX, int dY, int x, int y);

	abstract protected void onMouseWheel(int dWheel, int x, int y);

	abstract protected void onMouseButton(int button, boolean buttonState, int x, int y);

	abstract protected void onKey(int key, boolean isDown, boolean repeatEvent);

	private void lw3dOnKey(int key, boolean isDown,
			boolean repeatEvent) {
		if (isDown && !repeatEvent) {
			model.getKeys().add(key);
		} else if (!isDown && !repeatEvent) {
			model.getKeys().remove(key);
		}
		
	}

	protected void exit() {
		this.isRunning = false;
		view.exit();
		simulator.exit();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		isRunning = false;
	}

}
