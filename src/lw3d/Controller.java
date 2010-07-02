package lw3d;

import java.util.Iterator;

import lw3d.View.State;
import lw3d.math.Quaternion;
import lw3d.math.Transform;
import lw3d.math.Vector3f;
import lw3d.renderer.CameraNode;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Controller {

	private Model model;
	private View view;

	Simulator simulator;

	Thread inputThread;
	private boolean isRunning = true;
	private Runnable inputRunnable = new Runnable() {
		@Override
		public void run() {

			simulator.start();

			while (isRunning) {

				while (Keyboard.next()) {
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

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;

		simulator = new Simulator(model.getSimulatedNodes());
		simulator.start();

		inputThread = new Thread(inputRunnable, "inputThread");
		inputThread.start();
	}

	protected void onMouseMove(int dX, int dY, int x, int y) {
		Quaternion rot = new Quaternion().fromAngleNormalAxis(-dX * 0.01f,
				Vector3f.UNIT_Y);
		rot.multThis(new Quaternion().fromAngleNormalAxis(dY * 0.01f,
				Vector3f.UNIT_X));
		model.getCameraNode().getTransform().getRotation().multThis(rot);
	}

	protected void onMouseWheel(int dWheel, int x, int y) {
		Quaternion rot = new Quaternion().fromAngleNormalAxis(-dWheel * 0.001f,
				Vector3f.UNIT_Z);
		model.getCameraNode().getTransform().getRotation().multThis(rot);
	}

	protected void onMouseButton(int button, boolean buttonState, int x, int y) {
		if (buttonState)
			System.out.println("Click: (" + x + ", " + y + ")");
	}

	protected void onKey(int key, boolean isDown, boolean repeatEvent) {

		if (isDown && !repeatEvent) {
			model.getKeys().add(key);
		} else if (!isDown && !repeatEvent) {
			model.getKeys().remove(key);
		}

		if (!repeatEvent) {
			Vector3f vector = new Vector3f();
			float speed = 0.15f;

			Iterator<Integer> it = model.getKeys().iterator();
			while (it.hasNext()) {
				switch (it.next()) {
				case Keyboard.KEY_UP:
				case Keyboard.KEY_W:
					vector.addThis(0f, 0f, -1f);
					break;
				case Keyboard.KEY_RIGHT:
				case Keyboard.KEY_D:
					vector.addThis(1f, 0f, 0f);
					break;
				case Keyboard.KEY_DOWN:
				case Keyboard.KEY_S:
					vector.addThis(0f, 0f, 1f);
					break;
				case Keyboard.KEY_LEFT:
				case Keyboard.KEY_A:
					vector.addThis(-1f, 0f, 0f);
					break;
				default:
				}
			}

			vector.normalizeThis();
			vector.multThis(speed);
			//model.getCameraNode().getTransform().getRotation().mult(vector, vector);
			model.getCameraNode().getMovement().getPosition().set(vector);

		}

		if (key == Keyboard.KEY_ESCAPE) {
			exit();
		}
		
		if (key == Keyboard.KEY_SPACE && isDown) {
			/*Quaternion q = new Quaternion().fromAngleNormalAxis((float) Math.random(),
					new Vector3f((float) Math.random(),(float) Math.random(),(float) Math.random()).normalize());
			System.out.println("q: " + q);
			Quaternion iq = q.inverse();
			System.out.println("iq: " + iq);
			
			Vector3f v = new Vector3f((float) (Math.random()*2f*Math.PI),(float) Math.random(),(float) Math.random());
			System.out.println("v: " + v);
			
			System.out.println("q*v: " + q.mult(v));
			System.out.println("iq*v: " + iq.mult(v));
			System.out.println("q*iq*v: " + q.mult(iq.mult(v)));
			System.out.println("iq*q*v: " + iq.mult(q.mult(v)));
			System.out.println("(q*iq)*-v: " + q.mult(iq).mult(v.mult(-1f)));
			System.out.println("(iq*q)*v: " + iq.mult(q).mult(v));
			
			System.out.println("v + q*iq*-v: " + v.add(q.mult(iq.mult(v.mult(-1f)))));
			System.out.println("v + q*iq*-v: " + v.add(q.mult(iq.mult(v.mult(-1f)))));*/
			
			Transform t = new Transform(new Vector3f((float) (Math.random()*2f*Math.PI),(float) Math.random(),(float) Math.random()),
					new Quaternion().fromAngleNormalAxis((float) Math.random(),
							new Vector3f((float) Math.random(),(float) Math.random(),(float) Math.random()).normalize()));
			System.out.println("t: " + t);
			
			Transform it = t.invert();
			System.out.println("it:" + it);
			
			Transform tit = t.mult(it);
			System.out.println("tit:" + tit);
			
			Transform itt = it.mult(t);
			System.out.println("itt:" + itt);
			
			System.out.println("not: " + new Transform());

		}

	}

	private void exit() {
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
