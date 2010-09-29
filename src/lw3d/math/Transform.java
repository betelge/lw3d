package lw3d.math;

public class Transform {
	private Vector3f position;
	private Quaternion rotation;
	private Vector3f scale;

	public Transform() {
		this(new Vector3f(), new Quaternion());
	}

	public Transform(Transform transform) {
		if( position == null || rotation == null || scale == null) {
			position = new Vector3f();
			rotation = new Quaternion();
			scale = new Vector3f(1f, 1f, 1f);
		}
			
		set(transform);
	}

	public Transform(Vector3f position, Quaternion rotation) {
		this(position, rotation, new Vector3f(1f, 1f, 1f));
	}

	public Transform(Vector3f position, Quaternion rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Transform set(Transform transform) {
		return set(transform.position, transform.rotation, transform.scale);
	}

	public Transform set(Vector3f position, Quaternion rotation) {
		scale.set(1f, 1f, 1f);
		return this.set(position, rotation, scale);
	}
	
	public Transform add(Transform transform) {
		Transform result = new Transform(this);
		result.addThis(transform);
		return result;
	}
	
	public void addThis(Transform transform) {
		position.addThis(transform.position);
		scale.multThis(transform.scale);
		rotation.multThis(transform.rotation);
	}

	public Transform set(Vector3f position, Quaternion rotation, Vector3f scale) {
		this.position.set(position);
		this.rotation.set(rotation);
		this.scale.set(scale);
		return this;
	}

	public Transform mult(Transform transform) {
		return new Transform(position.add(rotation.mult(transform.position)
				.mult(scale)), rotation.mult(transform.rotation), scale
				.mult(transform.scale));
	}

	public void multThis(Transform transform) {
		position.addThis(rotation.mult(transform.position).mult(scale));
		scale.multThis(transform.scale);
		rotation.multThis(transform.rotation);
	}

	public Transform invert() {
		Transform result = new Transform(this);
		result.invertThis();
		return result;
	}

	public void invertThis() {
		position.negateThis();
		rotation.inverseThis();
		rotation.mult(position, position);
	}

	public Transform interpolate(Transform transform, float x) {
		Vector3f vec = this.position.mult(1 - x)
				.add(transform.position.mult(x));
		Vector3f sca = this.scale.mult(1 - x).add(transform.scale.mult(x));
		Quaternion rot = Quaternion.slerp(this.rotation, transform.rotation, x);

		return new Transform(vec, rot, sca);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	public Transform getCameraTransform() {
		Vector3f camPos = position.mult(-1f);
		Quaternion camRot = rotation.inverse();

		Transform rotationTransform = new Transform(new Vector3f(), camRot);
		Transform translationTransform = new Transform(camPos, new Quaternion());

		// Inverted order
		return rotationTransform.mult(translationTransform);
	}
	
	public float[] toMatrix4() {
		float[] m = new float[16];
		float[] qm = rotation.toMatrix3();
		
		// Rotation
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				m[4*i+j] = qm[3*i+j];
		
		// Translation
		m[12] = position.x;
		m[13] = position.y;
		m[14] = position.z;
		m[3] = m[7] = m[11] = 0;
		m[15] = 1;
		
		// Scaling
		if(scale.x != 1f || scale.y != 1f || scale.z != 1f) {
			for(int i = 0; i < 3; i++)
				m[i] *= scale.x;
			for(int i = 4; i < 7; i++)
				m[i] *= scale.y;
			for(int i = 8; i < 11; i++)
				m[i] *= scale.z;
		}
		
		return m;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Transform) || object == null) {
			return false;
		}

		return this.position.equals(((Transform) object).getPosition())
				&& this.rotation.equals(((Transform) object).getRotation())
				&& this.scale.equals(((Transform) object).getScale());
	}
	
	public String toString() {
		return "" + position + ", " + rotation + ", " + scale;
	}

	public int hashCode() {
		return position.hashCode() ^ rotation.hashCode() ^ scale.hashCode();
	}
}
