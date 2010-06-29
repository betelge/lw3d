package lw3d.renderer;

import lw3d.math.Transform;

public class MovableNode extends Node implements Movable {

	Transform movement = new Transform();

	public Transform getMovement() {
		return movement;
	}

	public void setMovement(Transform movement) {
		this.movement = movement;
	}
}
