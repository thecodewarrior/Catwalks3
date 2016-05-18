package catwalks.raytrace;

import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.util.AABB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class CustomAABBCollide extends AABB {

	ITraceable<Object, ?> trace;
	
	public CustomAABBCollide(AxisAlignedBB box, ITraceable<Object, ?> trace) {
		super(box);
		this.trace = trace;
	}
	
	@Override
	public RayTraceResult calculateIntercept(Vec3d vecA, Vec3d vecB) {
		ITraceResult<?> result = trace.trace(vecA, vecB, null);
		if(result == null)
			return null;
		if(Double.isInfinite(result.hitDistance()))
			return null;
		return new RayTraceResult(result.hitPoint(), EnumFacing.UP);
	}
	
	@Override
	public AABB expandXyz(double value) {
		return this;
	}

}
