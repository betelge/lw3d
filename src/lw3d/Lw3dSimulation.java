package lw3d;

import java.util.Iterator;

import lw3d.renderer.CameraNode;
import lw3d.renderer.Movable;
import lw3d.renderer.Node;

public class Lw3dSimulation {
	
	private long time;
	private long timeStep;
	private long realTime;
		
	public Lw3dSimulation(long timeStep) {
		this.timeStep = timeStep;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}
	
	public long getTimeStep() {
		return timeStep;
	}
	
	protected void beforeProcessingNodes() {
		// Empty. Can be overridden.
	}

	protected void preProcessNode(Node next) {
		// Empty. Can be overridden.
	}

	protected void processNode(Node node) {
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
			movableNode.getNextTransform().set(movableNode.getTransform());
			movableNode.getNextTransform().addThis(movableNode.getMovement());
			movableNode.setLastTime(realTime);
			movableNode.setNextTime(realTime + timeStep);

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

		synchronized (node) {
			Iterator<Node> iterator = node.getChildren().iterator();

			while (iterator.hasNext()) {
				processNode(iterator.next());
			}
		}
	}

	public void setRealTime(long realTime) {
		this.realTime = realTime;
	}
}
