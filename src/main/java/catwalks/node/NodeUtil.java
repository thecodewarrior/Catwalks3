package catwalks.node;

import java.util.List;
import java.util.function.Function;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import catwalks.node.types.NodeParticleEmitter;
import catwalks.node.types.NodeRedstoneReader;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;
import catwalks.shade.ccl.raytracer.RayTracer;

public class NodeUtil {

	@SideOnly(Side.CLIENT)
	public static ITraceResult<NodeHit> nodeHit;
	
	public static NodeUtil INSTANCE = new NodeUtil();
	
	public NodeUtil() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void retrace(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.START)
			return;
		
		nodeHit = rayTraceNodes();
		if(nodeHit != null && nodeHit.data() == null)
			nodeHit = null;
		if(nodeHit != null) {
			Minecraft.getMinecraft().objectMouseOver = new RayTraceResult(nodeHit.data().node);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static ITraceResult<NodeHit> rayTraceNodes() {
		Minecraft mc = Minecraft.getMinecraft();
				
		RayTraceResult rtr = mc.objectMouseOver;
		EntityPlayer player = mc.thePlayer;
		if(rtr == null || player == null)
			return null;
		if(rtr.entityHit instanceof EntityNodeBase) {
	        Vec3d start = player.getPositionEyes(1);
	        Vec3d look = player.getLook(1);
	        double d0 = mc.playerController.extendedReach() ? 6 : mc.playerController.getBlockReachDistance();
	        Vec3d end = start.addVector(look.xCoord * d0, look.yCoord * d0, look.zCoord * d0);
			
			ITraceResult<NodeHit> hit = ((EntityNodeBase)rtr.entityHit).rayTraceNode(player, start, end);
			return hit;
		}
		
		return null;
	}
	
	public static enum EnumNodes {
		PARTICLE((entity) -> new NodeParticleEmitter(entity)),
		REDSTONEREADER((entity) -> new NodeRedstoneReader(entity));
		
		private Function<EntityNodeBase, NodeBase> constructor;
		
		private EnumNodes(Function<EntityNodeBase, NodeBase> constructor) {
			this.constructor = constructor;
		}
		
		public NodeBase create(EntityNodeBase entity) {
			return constructor.apply(entity);
		}
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
	
}
