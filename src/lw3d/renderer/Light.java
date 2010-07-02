package lw3d.renderer;

import lw3d.math.Transform;
import lw3d.math.Vector3f;

public class Light extends Node {
	private Vector3f colorIntensity;

	public Light() {
		colorIntensity = new Vector3f(1f, 1f, 1f);
	}

	public Light(Transform transform) {
		super(transform);
		colorIntensity = new Vector3f(1f, 1f, 1f);
	}
	
	public Light(Transform transform, Vector3f colorIntensity) {
		super(transform);
		this.colorIntensity = colorIntensity;
	}
	
	public Light(Vector3f colorIntensity) {
		this.colorIntensity = colorIntensity;
	}
	
	public void setColorIntensity(Vector3f colorIntensity) {
		this.colorIntensity = colorIntensity;
	}

	public Vector3f getColorIntensity() {
		return colorIntensity;
	}
}
