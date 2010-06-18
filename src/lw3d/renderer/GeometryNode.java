package lw3d.renderer;

public class GeometryNode extends Node {
	
	Geometry geometry;
	Material material;

	public GeometryNode(Geometry geometry, Material material) {
		this.geometry = geometry;
		this.material = material;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public Material getMaterial() {
		return material;
	}
}
