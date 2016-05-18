package catwalks.node;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;
import catwalks.util.AABB;
import catwalks.util.GeneralUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NodeUtil {

	public static ITraceResult<NodeHit> rayTraceNodes(World world, @Nullable EntityPlayer player, Vec3d start, Vec3d look, double reachDistance) {
//		double step = Math.min(2, reachDistance);
//		double buffer = 1;
		Vec3d end = start.add( look.scale(reachDistance) );
		
		AxisAlignedBB bb = new AABB(start, end).expandXyz(5);
		
//		List<AxisAlignedBB> bbs = new ArrayList<>();
//		for(double i = step; i <= reachDistance; i += step) {
//			if(i > reachDistance)
//				i = reachDistance;
//			
//			bbs.add(  GeneralUtil.getAABB(
//						start.add(look.scale(i-step)),
//						start.add(look.scale(i     ))
//					  ).expandXyz(buffer)
//					);
//			if( i == reachDistance )
//				break;
//		}
//		
		ITraceResult<NodeHit> result = null;
//				
//		for (AxisAlignedBB bb : bbs) {
			List<Entity> entities = world.getEntitiesWithinAABB(EntityNodeBase.class, bb);
			
			for (Entity entity : entities) {
				EntityNodeBase nodeEntity = (EntityNodeBase) entity;
				
				ITraceResult<NodeHit> hit = nodeEntity.rayTraceNode(player, start, end);
				if(hit != null) {
					result = RayTraceUtil.min(result, hit);
				}
			}
//		}
		
		return result;
	}
	
}
