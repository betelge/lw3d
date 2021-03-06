package lw3d.renderer;

import lw3d.math.Transform;

public class MovableNode extends Node implements Movable {

	Transform movement = new Transform();
	
	Transform nextTransform = new Transform();
	
	long lastTime = 0;
	long nextTime = 0;

	@Override
	public Transform getMovement() {
		return movement;
	}

	@Override
	public void setMovement(Transform movement) {
		this.movement = movement;
	}
	
	@Override
	public long getLastTime() {
		return lastTime;
	}

	@Override
	public long getNextTime() {
		return nextTime;
	}

	@Override
	public Transform getNextTransform() {
		return nextTransform;
	}

	@Override
	public void setLastTime(long time) {
		this.lastTime = time;
	}

	@Override
	public void setNextTime(long time) {
		this.nextTime = time;
	}
}
