package lw3d.renderer;

import lw3d.math.Transform;

public interface Movable {
	
	public Transform getMovement();

	public void setMovement(Transform movement);

	public Transform getTransform();

	public void setTransform(Transform transform);
}
