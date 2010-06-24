package lw3d;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Controller {

	private Model model;
	private View view;

	Thread inputThread;
	private boolean running = true;
	private Runnable inputRunnable = new Runnable() {
		@Override
		public void run() {
			
			while (running) {

				while (Keyboard.next())
					onKey(Keyboard.getEventKey(), Keyboard
							.getEventKeyState(), Keyboard.isRepeatEvent());

				while (Mouse.next()) {
					if (Mouse.getEventButton() != -1)
						onMouseButton(Mouse.getEventButton(), Mouse
								.getEventButtonState(), Mouse.getEventX(),
								Mouse.getEventY());
					else {
						if (Mouse.getEventDWheel() != 0)
							onMouseWheel(Mouse.getEventDWheel(),
									Mouse.getEventX(), Mouse.getEventY());
						if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0)
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
			}
		}
	};

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;

		inputThread = new Thread(inputRunnable, "inputThread");
		inputThread.start();
	}

	protected void onMouseMove(int dX, int dY, int x, int y) {
		
	}

	protected void onMouseWheel(int dWheel, int x, int y) {
		
	}

	protected void onMouseButton(int button, boolean buttonState,
			int x, int y) {
		if(buttonState)
			System.out.println("Click: (" + x + ", " + y + ")");
	}

	protected void onKey(int key, boolean keyState,
			boolean repeatEvent) {
		
		if(key == Keyboard.KEY_ESCAPE) {
			this.running = false;
			view.exit();
		}	
		
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		running = false;
	}

}
