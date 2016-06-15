package catwalks.raytrace.primitives;

import java.util.Arrays;

import net.minecraft.util.math.Vec3d;

import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.IRenderableTraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.SimpleRenderableTraceResult;
import catwalks.raytrace.RayTraceUtil.TraceablePrimitive;
import catwalks.raytrace.RayTraceUtil.VertexList;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.util.GeneralUtil;

public class Quad extends TraceablePrimitive<Quad> {

	Vec3d v1, v2, v3, v4;
	Tri t1, t2;
	boolean hasNet = true;
	
	public Quad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
		
		t1 = new Tri(v1, v2, v3);
		t2 = new Tri(v1, v3, v4);
	}
	
	@Override
	public Vec3d[] edges() {
		return new Vec3d[] { v1, v2, v2, v3, v3, v4, v4, v1 };
	}
	
	@Override
	public Vec3d[] points() {
		return new Vec3d[] { v1, v2, v3, v4 };
	}
	
	public Quad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, boolean hasNet) {
		this(v1, v2, v3, v4);
		this.hasNet = hasNet;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IRenderableTraceResult<Quad> trace(Vec3d start, Vec3d end) {
		
		ITraceResult<?> result = RayTraceUtil.min(
			t1.trace(start, end),
			t2.trace(start, end)
		);
		
		return new SimpleRenderableTraceResult<Quad>(result, this,
			Arrays.asList(new VertexList[] {
				new VertexList(hasNet, new Vec3d[] { v1, v2, v3, v4 })
			})
		);
	}
	
	public Quad noNet() {
		hasNet = false;
		return this;
	}
	
	public Quad withNet() {
		hasNet = true;
		return this;
	}
	
	public Quad setNet(boolean value) {
		hasNet = value;
		return this;
	}

	@Override
	public Quad clone() {
		return new Quad(v1, v2, v3, v4, hasNet);
	}

	@Override
	public void rotate(int yRotation) {
		v1 = GeneralUtil.rotateVector(yRotation, v1);
		v2 = GeneralUtil.rotateVector(yRotation, v2);
		v3 = GeneralUtil.rotateVector(yRotation, v3);
		v4 = GeneralUtil.rotateVector(yRotation, v4);
		t1.rotate(yRotation);
		t2.rotate(yRotation);
	}

	@Override
	public void translate(Vec3d vec) {
		v1 = v1.add(vec);
		v2 = v2.add(vec);
		v3 = v3.add(vec);
		v4 = v4.add(vec);
		t1.translate(vec);
		t2.translate(vec);
	}
	
	@Override
	public void apply(Matrix4 matrix) {
		v1 = matrix.apply(v1);
		v2 = matrix.apply(v2);
		v3 = matrix.apply(v3);
		v4 = matrix.apply(v4);
		t1.apply(matrix);
		t2.apply(matrix);
	}

}
