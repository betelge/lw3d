package lw3d.math;

public interface Procedural {
	public double getValue(double x, double y, double z, double resolution);
	public double getValueNormal(double x, double y, double z, double resolution, Vector3f normal);
}
