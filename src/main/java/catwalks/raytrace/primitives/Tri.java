package catwalks.raytrace.primitives;

import java.util.Arrays;

import net.minecraft.util.math.Vec3d;

import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.IRenderableTraceResult;
import catwalks.raytrace.RayTraceUtil.SimpleRenderableTraceResult;
import catwalks.raytrace.RayTraceUtil.TraceablePrimitive;
import catwalks.raytrace.RayTraceUtil.VertexList;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.GeneralUtil;

public class Tri extends TraceablePrimitive<Tri> {

	private static final double SMALL_NUM = 0.00000001f;
	
	Vec3d v1, v2, v3;
	
	public Tri(Vec3d v1, Vec3d v2, Vec3d v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	@Override
	public Vec3d[] edges() {
		return new Vec3d[] { v1, v2, v2, v3, v3, v1 };
	}
	
	@Override
	public Vec3d[] points() {
		return new Vec3d[] { v1, v2, v3 };
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IRenderableTraceResult<Tri> trace(Vec3d startRaw, Vec3d endRaw) {
		Vector3 v1 = new Vector3(this.v1);
		Vector3 v2 = new Vector3(this.v2);
		Vector3 v3 = new Vector3(this.v3);
		Vector3 start = new Vector3(startRaw), end = new Vector3(endRaw);
		
		Vector3 intersect;
	    Vector3    u, v, n;              // triangle vectors
	    Vector3    dir, w0, w;           // ray vectors
	    double     r, a, b;              // params to calc ray-plane intersect

	    // get triangle edge vectors and plane normal
	    u = v2.copy().sub(v1);
	    v = v3.copy().sub(v1);
	    n = u.crossProduct(v);              // cross product
	    if (n.equals( Vector3.zero ))             // triangle is degenerate
	        return RayTraceUtil.miss(this);                   // do not deal with this case

	    dir = end.copy().sub(start);             // ray direction vector
	    start.sub(dir.copy().normalize().multiply(0.25));
	    dir = end.copy().sub(start);             // ray direction vector
	    w0 = start.copy();
	    w0.sub(v1);
	    a = -n.copy().dotProduct(w0);
	    b =  n.copy().dotProduct(dir);
	    if (Math.abs(b) < SMALL_NUM) {     // ray is  parallel to triangle plane
	        if (a == 0)                 // ray lies in triangle plane
	            return RayTraceUtil.miss(this);
	        else
	        	return RayTraceUtil.miss(this);              // ray disjoint from plane
	    }

	    // get intersect point of ray with triangle plane
	    r = a / b;
	    if (r < 0.0)                    // ray goes away from triangle
	        return RayTraceUtil.miss(this);                   // => no intersect
	    if (r > 1.0)                    // ray doesn't reach triangle
	    	return RayTraceUtil.miss(this);                   // => no intersect

	    intersect = start.copy().add(dir.copy().multiply(r));            // intersect point of ray and plane
	    
	    float angles = 0;

        v1 = new Vector3(intersect.x - this.v1.xCoord, intersect.y - this.v1.yCoord, intersect.z - this.v1.zCoord).normalize();
        v2 = new Vector3(intersect.x - this.v2.xCoord, intersect.y - this.v2.yCoord, intersect.z - this.v2.zCoord).normalize();
        v3 = new Vector3(intersect.x - this.v3.xCoord, intersect.y - this.v3.yCoord, intersect.z - this.v3.zCoord).normalize();

        angles += Math.acos(v1.copy().dotProduct(v2));
        angles += Math.acos(v2.copy().dotProduct(v3));
        angles += Math.acos(v3.copy().dotProduct(v1));

        if(Math.abs(angles - 2*Math.PI) > 0.005)
        	return RayTraceUtil.miss(this);
                
        return new SimpleRenderableTraceResult<Tri>(startRaw, intersect.vec3(), this,
        		Arrays.asList(new VertexList[] {
        			new VertexList(false, new Vec3d[] { this.v1, this.v2, this.v3 })
        		})
        	);
	}
	
	@Override
	public Tri clone() {
		return new Tri(v1, v2, v3);
	}

	@Override
	public void rotate(int yRotation) {
		v1 = GeneralUtil.rotateVector(yRotation, v1);
		v2 = GeneralUtil.rotateVector(yRotation, v2);
		v3 = GeneralUtil.rotateVector(yRotation, v3);
	}

	@Override
	public void translate(Vec3d vec) {
		v1 = v1.add(vec);
		v2 = v2.add(vec);
		v3 = v3.add(vec);
	}
	
	@Override
	public void apply(Matrix4 matrix) {
		v1 = matrix.apply(v1);
		v2 = matrix.apply(v2);
		v3 = matrix.apply(v3);
	}
	
}
