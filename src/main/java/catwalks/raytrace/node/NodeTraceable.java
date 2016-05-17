package catwalks.raytrace.node;

import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.RayTraceUtil.ITraceablePrimitive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class NodeTraceable implements ITraceable<EntityPlayer, NodeHit> {

	ITraceablePrimitive<?> traceable;
	
	
	
	@Override
	public ITraceResult<NodeHit> trace(Vec3d start, Vec3d end, EntityPlayer param) {
		return null;
	}

}
