package testApplet.renderer;

import java.util.HashSet;
import java.util.Set;

import testApplet.math.Vector3f;

public class Node {
	private Node parent = null;
	private Set<Node> children = new HashSet();

	private Vector3f position;

	public Node() {
		this(new Vector3f());
	}

	public Node(Vector3f position) {
		this.position = position;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getAbsolutePosition(){
		if(parent != null)
			return position.add(parent.getAbsolutePosition());
		else
			return position;
	}

	public void attach(Node node) {
		children.add(node);
		node.parent = this;
	}

	public void detachFromParent() {
		parent.children.remove(this);
		parent = null;
	}

	public Set<Node> getChildren() {
		return children;
	}
}
