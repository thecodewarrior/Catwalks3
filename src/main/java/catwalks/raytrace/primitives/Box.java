package catwalks.raytrace.primitives;

import java.util.List;

import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.IRenderableTraceResult;
import catwalks.raytrace.RayTraceUtil.SimpleRenderableTraceResult;
import catwalks.raytrace.RayTraceUtil.TraceablePrimitive;
import catwalks.raytrace.RayTraceUtil.VertexList;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.util.AABB;
import catwalks.util.GeneralUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import scala.actors.threadpool.Arrays;

public class Box extends TraceablePrimitive<Box> {

	List<Quad> quadList;
	Quad[] quads = new Quad[6];
	Vec3d[] points = new Vec3d[8];
	
	private Box(Quad[] quads, Vec3d[] points) {
		this.quads = quads;
		this.points = points;
	}
	
	@SuppressWarnings("unchecked")
	public Box(AxisAlignedBB bb) {
		points[0] = new Vec3d(bb.minX, bb.minY, bb.minZ);
		points[1] = new Vec3d(bb.maxX, bb.minY, bb.minZ);
		points[2] = new Vec3d(bb.maxX, bb.minY, bb.maxZ);
		points[3] = new Vec3d(bb.minX, bb.minY, bb.maxZ);
		
		points[4] = new Vec3d(bb.minX, bb.maxY, bb.minZ);
		points[5] = new Vec3d(bb.maxX, bb.maxY, bb.minZ);
		points[6] = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);
		points[7] = new Vec3d(bb.minX, bb.maxY, bb.maxZ);

		quads[0] = new Quad(points[0], points[1], points[2], points[3]);
		quads[1] = new Quad(points[4], points[5], points[6], points[7]);
		
		quads[2] = new Quad(points[0], points[1], points[5], points[4]);
		quads[3] = new Quad(points[2], points[3], points[7], points[6]);
		
		quads[4] = new Quad(points[1], points[2], points[6], points[5]);
		quads[5] = new Quad(points[0], points[3], points[7], points[4]);
		
		quadList = Arrays.asList(quads);
	}
	
	public Box(Vec3d min, Vec3d max) {
		this(new AABB(min, max));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IRenderableTraceResult<Box> trace(Vec3d start, Vec3d end) {
		return new SimpleRenderableTraceResult<Box>(RayTraceUtil.trace(start, end, quadList, null), this, Arrays.asList(new VertexList[] {
			new VertexList(false, quads[0].points()),
			new VertexList(false, quads[1].points()),
			new VertexList(false, points[0], points[4]),
			new VertexList(false, points[1], points[5]),
			new VertexList(false, points[2], points[6]),
			new VertexList(false, points[3], points[7])
		}));
	}

	@Override
	public TraceablePrimitive<Box> clone() {
		return null;
	}

	@Override
	public Vec3d[] edges() {
		return new Vec3d[] {
			points[0], points[1], points[1], points[2], points[2], points[3], points[3], points[0],
			points[4], points[5], points[5], points[6], points[6], points[7], points[7], points[4],
			points[0], points[4],
			points[1], points[5],
			points[2], points[6],
			points[3], points[7]
		};
	}
	
	@Override
	public Vec3d[] points() {
		return points;
	}

	@Override
	public void rotate(int yRotation) {
		for (int i = 0; i < quads.length; i++) {
			quads[i].rotate(yRotation);
		}
		for (int i = 0; i < points.length; i++) {
			points[i] = GeneralUtil.rotateVector(yRotation, points[i]);
		}
	}

	@Override
	public void translate(Vec3d vec) {
		for (int i = 0; i < quads.length; i++) {
			quads[i].translate(vec);
		}
		for (int i = 0; i < points.length; i++) {
			points[i] = points[i].add(vec);
		}
	}

	@Override
	public void apply(Matrix4 matrix) {
		for (int i = 0; i < quads.length; i++) {
			quads[i].apply(matrix);
		}
		for (int i = 0; i < points.length; i++) {
			points[i] = matrix.apply(points[i]);
		}
	}

}
