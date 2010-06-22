package lw3d.math;

public class Transform {
	private Vector3f position;
	private Quaternion rotation;
	private Vector3f scale;

	public Transform() {
		this(new Vector3f(), new Quaternion());
	}

	public Transform(Transform transform) {
		this(transform.position, transform.rotation, transform.scale);
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

	public Transform set(Vector3f position, Quaternion rotation, Vector3f scale) {
		this.position.set(position);
		this.rotation.set(rotation);
		this.scale.set(scale);
		return this;
	}

	public Transform mult(Transform transform) {
		return new Transform(position.add(rotation.mult(transform.position)
				.mult(scale)), rotation.mult(transform.rotation), scale
				.mult(rotation.mult(transform.scale)));
	}

	public void multThis(Transform transform) {
		position.addThis(rotation.mult(transform.position).mult(scale));
		scale.multThis(/* rotation.mult( */transform.scale/* ) */);
		rotation.multThis(transform.rotation);
	}

	public Transform invert() {
		Transform transform = new Transform(this);
		transform.invertThis();
		return transform;
	}

	public void invertThis() {
		position.negateThis();
		rotation.inverseThis();
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
	
	public float[] toMatrix4() {
		float[] m = new float[16];
		float[] qm = rotation.toMatrix3();
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				m[4*i+j] = qm[3*i+j];
		m[3] = position.x;
		m[7] = position.y;
		m[11] = position.z;
		m[12] = m[13] = m[14] = 0;
		m[15] = 1;
		
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

	public int hashCode() {
		return position.hashCode() ^ rotation.hashCode() ^ scale.hashCode();
	}
}
