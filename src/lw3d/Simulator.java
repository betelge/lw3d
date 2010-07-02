package lw3d;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.Sys;

import lw3d.renderer.CameraNode;
import lw3d.renderer.Movable;
import lw3d.renderer.Node;

public class Simulator {

	final private Set<Node> nodes;

	long time;
	int timeStep = 50;
	int timeAccumulator = 0;

	Thread simulatorThread;
	boolean isRunning = false;
	private Runnable simulatorRunnable = new Runnable() {
		@Override
		public void run() {

			time = Sys.getTime();
			while (isRunning) {
				long newTime = Sys.getTime();
				timeAccumulator += newTime - time;
				time = newTime;
								
				// TODO: check if the sim runs slow
				if (timeAccumulator >= timeStep) {
					timeAccumulator -= timeStep;

					Iterator<Node> it = nodes.iterator();
					while (it.hasNext()) {
						processNode(it.next());
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

	public Simulator(Set<Node> nodes) {
		this.nodes = nodes;
		System.out.println(nodes);
		simulatorThread = new Thread(simulatorRunnable, "simulatorThread");
		simulatorThread.start();
	}

	public void start() {
		time = Sys.getTime();
		isRunning = true;
	}

	public void exit() {
		isRunning = false;
	}

	private void processNode(Node node) {
		/*
		 * if (node instanceof KeyFramable) { KeyFramable keyFramed =
		 * ((KeyFramable) node); long animationTime = time -
		 * keyFramed.getTimeOffset(); long time1, time2;
		 * 
		 * keyFramed.setCurrentTime(time); keyFramed.setNextTime(time +
		 * timeStep);
		 * 
		 * float interpolationValue;
		 * 
		 * switch (keyFramed.getTimingType()) { case CLAMP: { if (animationTime
		 * <= keyFramed.getKeyFrames().firstKey()) { animationTime =
		 * keyFramed.getKeyFrames().firstKey() + 1; } else if (animationTime >
		 * keyFramed.getKeyFrames().lastKey()) { animationTime =
		 * keyFramed.getKeyFrames().lastKey();
		 * 
		 * }
		 * 
		 * time1 = keyFramed.getKeyFrames().subMap(0l, animationTime)
		 * .lastKey(); time2 = keyFramed.getKeyFrames().tailMap(animationTime)
		 * .firstKey();
		 * 
		 * if (time2 - time1 <= 0) { interpolationValue = 0; } else {
		 * interpolationValue = (animationTime - time1) / (float) (time2 -
		 * time1); }
		 * 
		 * keyFramed.setLocalTransform(keyFramed.getKeyFrames().get(time1)
		 * .interpolate( keyFramed.getKeyFrames().get(time2), (animationTime -
		 * time1) / (float) (time2 - time1)));
		 * 
		 * animationTime = time - keyFramed.getTimeOffset() + timeStep; if
		 * (animationTime <= keyFramed.getKeyFrames().firstKey()) {
		 * animationTime = keyFramed.getKeyFrames().firstKey() + 1; } else if
		 * (animationTime > keyFramed.getKeyFrames().lastKey()) { animationTime
		 * = keyFramed.getKeyFrames().lastKey();
		 * 
		 * }
		 * 
		 * time1 = keyFramed.getKeyFrames().subMap(0l, animationTime)
		 * .lastKey(); time2 = keyFramed.getKeyFrames().tailMap(animationTime)
		 * .firstKey();
		 * 
		 * if (time2 - time1 <= 0) { interpolationValue = 0; } else {
		 * interpolationValue = (animationTime - time1) / (float) (time2 -
		 * time1); }
		 * 
		 * keyFramed.setNextLocalTransform(keyFramed.getKeyFrames().get(
		 * time1).interpolate(keyFramed.getKeyFrames().get(time2),
		 * interpolationValue)); keyFramed.getLastLocalTransform().set(
		 * keyFramed.getLocalTransform());
		 * 
		 * break; }
		 * 
		 * case REPEAT: { animationTime -= keyFramed.getKeyFrames().firstKey();
		 * animationTime %= (keyFramed.getKeyFrames().lastKey() - keyFramed
		 * .getKeyFrames().firstKey()); animationTime +=
		 * keyFramed.getKeyFrames().firstKey();
		 * 
		 * if (animationTime <= keyFramed.getKeyFrames().firstKey()) {
		 * animationTime = keyFramed.getKeyFrames().firstKey() + 1; }
		 * 
		 * time1 = keyFramed.getKeyFrames().subMap(0l, animationTime)
		 * .lastKey(); time2 = keyFramed.getKeyFrames().tailMap(animationTime)
		 * .firstKey();
		 * 
		 * if (time2 - time1 <= 0) { interpolationValue = 0; } else {
		 * interpolationValue = (animationTime - time1) / (float) (time2 -
		 * time1); }
		 * 
		 * keyFramed.setLocalTransform(keyFramed.getKeyFrames().get(time1)
		 * .interpolate( keyFramed.getKeyFrames().get(time2), (animationTime -
		 * time1) / (float) (time2 - time1)));
		 * 
		 * animationTime = time - keyFramed.getTimeOffset() + timeStep;
		 * animationTime -= keyFramed.getKeyFrames().firstKey(); animationTime
		 * %= (keyFramed.getKeyFrames().lastKey() - keyFramed
		 * .getKeyFrames().firstKey()); animationTime +=
		 * keyFramed.getKeyFrames().firstKey();
		 * 
		 * if (animationTime <= keyFramed.getKeyFrames().firstKey()) {
		 * animationTime = keyFramed.getKeyFrames().firstKey() + 1; } else if
		 * (animationTime > keyFramed.getKeyFrames().lastKey()) { animationTime
		 * = keyFramed.getKeyFrames().lastKey();
		 * 
		 * }
		 * 
		 * time1 = keyFramed.getKeyFrames().subMap(0l, animationTime)
		 * .lastKey(); time2 = keyFramed.getKeyFrames().tailMap(animationTime)
		 * .firstKey();
		 * 
		 * if (time2 - time1 <= 0) { interpolationValue = 0; } else {
		 * interpolationValue = (animationTime - time1) / (float) (time2 -
		 * time1); }
		 * 
		 * keyFramed.setNextLocalTransform(keyFramed.getKeyFrames().get(
		 * time1).interpolate(keyFramed.getKeyFrames().get(time2),
		 * (animationTime - time1) / (float) (time2 - time1)));
		 * keyFramed.getLastLocalTransform().set(
		 * keyFramed.getLocalTransform()); break; }
		 * 
		 * default:
		 * 
		 * } } else
		 */
		if ( node instanceof CameraNode) {
			Movable movableNode = (Movable) node;
			movableNode.getTransform().multThis(movableNode.getMovement());
		}
		else if (node instanceof Movable) {
			Movable movableNode = (Movable) node;
			movableNode.getTransform().addThis(movableNode.getMovement());


			/*
			 * Transform localTransform = ((Movable) node).getLocalTransform();
			 * if (((Movable) node).getLocalTransform().equals( ((Movable)
			 * node).getLastLocalTransform())) { localTransform.set(((Movable)
			 * node).getNextLocalTransform()); } else {
			 * localTransform.multiplyThis(((Movable) node).getMovement()); }
			 * 
			 * ((Movable) node).getLastLocalTransform().set(localTransform);
			 * 
			 * Transform nextLocalTransform = ((Movable) node)
			 * .getNextLocalTransform(); nextLocalTransform.set(localTransform);
			 * nextLocalTransform.multiplyThis(((Movable) node).getMovement());
			 * 
			 * ((Movable) node).setCurrentTime(time); ((Movable)
			 * node).setNextTime(time + timeStep);
			 */
		}

		Iterator<Node> iterator = node.getChildren().iterator();

		while (iterator.hasNext()) {
			processNode(iterator.next());
		}
	}

}
