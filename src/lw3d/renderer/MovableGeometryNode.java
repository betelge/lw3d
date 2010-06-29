package lw3d.renderer;

import lw3d.math.Transform;

public class MovableGeometryNode extends GeometryNode implements Movable {
	
	Transform movement = new Transform();

	public MovableGeometryNode(Geometry geometry, Material material) {
		super(geometry, material);
	}

	@Override
	public Transform getMovement() {
		return movement;
	}

	@Override
	public void setMovement(Transform movement) {
		this.movement = movement;
	}

}
