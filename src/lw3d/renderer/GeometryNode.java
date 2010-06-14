package lw3d.renderer;

public class GeometryNode extends Node {
	
	Geometry geometry;

	public GeometryNode(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
}
