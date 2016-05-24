package catwalks.render;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import catwalks.item.ItemNodeBase;
import catwalks.node.EntityNodeBase;
import catwalks.util.GeneralUtil;

public class NodeConnectionRenderer {

	public static NodeConnectionRenderer INSTANCE = new NodeConnectionRenderer();
	
	private static Map<Integer, List<Vec3d>> connections = new HashMap<>();
	private static WeakReference<World> currentWorld;
	
	public static void set(int eid, List<Vec3d> points) {
		connections.put(eid, points);
	}
	
	public static int connectionCount() {
		return connections.size();
	}
	
	public NodeConnectionRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if(currentWorld == null || currentWorld.get() != mc.theWorld) { // clear list when world changes
			if(currentWorld != null)
				connections.clear();
			currentWorld = new WeakReference<World>(mc.theWorld);
		}
		
		if(!GeneralUtil.isHolding(mc.thePlayer, (stack) -> stack.getItem() instanceof ItemNodeBase))
			return;
		
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		
		GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        
		GlStateManager.pushMatrix();
		
		EntityPlayer rootPlayer = Minecraft.getMinecraft().thePlayer;
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.getPartialTicks();
        double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.getPartialTicks();
        double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.getPartialTicks();
        GlStateManager.translate(-x, -y, -z);
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
        
		Iterator<Entry<Integer, List<Vec3d>>> iter = connections.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, List<Vec3d>> entry = iter.next();
			Entity entity = mc.theWorld.getEntityByID(entry.getKey());
		    if(entity == null || !(entity instanceof EntityNodeBase)){
		        iter.remove();
		        continue;
		    }
		    
		    EntityNodeBase enode = (EntityNodeBase) entity;
		    if(enode.getNode().outputs().size() == 0) {
		    	iter.remove();
		    	continue;
		    }
		    	        
	        // back
	        GlStateManager.depthFunc(GL11.GL_GREATER);
	        renderConnections(entity.getPositionVector(), entry.getValue(), enode.getNode().outputs().get(0).getColor(), 2);
	        GlStateManager.depthFunc(GL11.GL_LEQUAL);
	        
	        renderConnections(entity.getPositionVector(), entry.getValue(), enode.getNode().outputs().get(0).getColor(), 1);
		}
		
		GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
	
	private void renderConnections(Vec3d start, List<Vec3d> ends, int colorHex, int div) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
    	
        int alpha = 255;
        int red   = (colorHex >> 16) & 0xFF; red = red/div;
        int green = (colorHex >> 8) & 0xFF; green = green/div;
        int blue  = (colorHex >> 0) & 0xFF; blue = blue/div;
        
        
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		
		for (Vec3d point : ends) {
            vb.pos(start.xCoord, start.yCoord, start.zCoord).color(red, green, blue, alpha).endVertex();
            vb.pos(point.xCoord, point.yCoord, point.zCoord).color(red, green, blue, alpha).endVertex();
		}
		
        tessellator.draw();
	}
}
