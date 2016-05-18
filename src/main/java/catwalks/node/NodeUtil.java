package catwalks.node;

import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NodeUtil {

	@SideOnly(Side.CLIENT)
	public static ITraceResult<NodeHit> rayTraceNodes() {
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult rtr = mc.objectMouseOver;

		EntityPlayer player = mc.thePlayer;
        Vec3d start = player.getPositionEyes(1);
        Vec3d look = player.getLook(1);
        double d0 = mc.playerController.extendedReach() ? 6 : mc.playerController.getBlockReachDistance();
        Vec3d end = start.addVector(look.xCoord * d0, look.yCoord * d0, look.zCoord * d0);
        
		if(rtr.entityHit instanceof EntityNodeBase) {
			ITraceResult<NodeHit> hit = ((EntityNodeBase)rtr.entityHit).rayTraceNode(player, start, end);
			return hit;
		}
		
		return null;
	}
	
}
