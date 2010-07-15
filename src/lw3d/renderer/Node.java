package lw3d.renderer;

import java.util.HashSet;
import java.util.Set;

import lw3d.math.Transform;

public class Node {
	private Node parent = null;
	private Set<Node> children = new HashSet<Node>();

	private Transform transform;

	public Node() {
		this(new Transform());
	}

	public Node(Transform transform) {
		this.transform = transform;
	}

	public Transform getTransform() {
		return transform;
	}
	
	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	
	public Transform getAbsoluteTransform(){
		if(parent != null)
			return getTransform().mult(parent.getAbsoluteTransform());
		else
			return getTransform();
	}

	public void attach(Node node) {
		synchronized (this) {
			children.add(node);
			node.parent = this;
		}
	}

	public void detachFromParent() {
		parent.children.remove(this);
		parent = null;
	}

	public Set<Node> getChildren() {
		return children;
	}
}
