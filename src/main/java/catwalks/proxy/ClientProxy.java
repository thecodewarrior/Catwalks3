package catwalks.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import catwalks.CatwalksMod;
import catwalks.register.BlockRegister;
import catwalks.register.ItemRegister;
import catwalks.render.catwalk.CatwalkSmartModel;
import catwalks.texture.TextureGenerator;
import catwalks.util.ExtendedFlatHighlightMOP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	
	public EntityPlayer getPlayerLooking(Vec3 start, Vec3 end) {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	public void preInit() {
		BlockRegister.initRender();
		ItemRegister.initRender();
		MinecraftForge.EVENT_BUS.register(TextureGenerator.instance);
//		( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener(TextureGenerator.instance);
	}
	
	Map<ModelResourceLocation, IBakedModel> models = new HashMap<>();
	
	private void model(String loc, IBakedModel model) {
		models.put(new ModelResourceLocation(CatwalksMod.MODID + ":" + loc), model);
	}
	
	@SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
		models.clear();
		
		model("catwalk", new CatwalkSmartModel());
		
        for (Entry<ModelResourceLocation, IBakedModel> model : models.entrySet()) {
			
        	Object object =  event.modelRegistry.getObject(model.getKey());
            if (object != null) {
                event.modelRegistry.putObject(model.getKey(), model.getValue());
            }
        	
		}
    }
	
	@SubscribeEvent
	public void highlight(DrawBlockHighlightEvent event) {
		BlockPos pos = event.target.getBlockPos();
		
		if(event.target instanceof ExtendedFlatHighlightMOP) {
			event.setCanceled(true);
			
			ExtendedFlatHighlightMOP mop = (ExtendedFlatHighlightMOP) event.target;
			
			GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            
            double d0 = event.player.lastTickPosX + (event.player.posX - event.player.lastTickPosX) * (double)event.partialTicks;
            double d1 = event.player.lastTickPosY + (event.player.posY - event.player.lastTickPosY) * (double)event.partialTicks;
            double d2 = event.player.lastTickPosZ + (event.player.posZ - event.player.lastTickPosZ) * (double)event.partialTicks;
            
            GlStateManager.translate(pos.getX()-d0, pos.getY()-d1, pos.getZ()-d2);
            
            GlStateManager.translate(0.5, 0.5, 0.5);
            
            switch (event.target.sideHit) {
			case WEST:
				GlStateManager.rotate(180, 0, 1, 0);
				break;
			case NORTH:
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case SOUTH:
				GlStateManager.rotate(-90, 0, 1, 0);
				break;
			case DOWN:
				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.scale(1, 1, -1);
				break;
			case UP:
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(180, 1, 0, 0);
				break;
			default:
				break;
			}
            
            GlStateManager.translate(-0.5, -0.5, -0.5);

            
            
            
            
            
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
            double d = 0.0020000000949949026D, D = 1+d;
            
            worldrenderer.begin(1, DefaultVertexFormats.POSITION);
            
            worldrenderer.pos(D-mop.sideDistance, D-mop.top,	d+mop.right).endVertex();
            worldrenderer.pos(D-mop.sideDistance, D-mop.top,	D-mop.left).endVertex();
            
            worldrenderer.pos(D-mop.sideDistance, d+mop.bottom, d+mop.right).endVertex();
            worldrenderer.pos(D-mop.sideDistance, d+mop.bottom, D-mop.left).endVertex();
            
            worldrenderer.pos(D-mop.sideDistance, d+mop.bottom, D-mop.left).endVertex();
            worldrenderer.pos(D-mop.sideDistance, D-mop.top,	D-mop.left).endVertex();
            
            worldrenderer.pos(D-mop.sideDistance, d+mop.bottom, d+mop.right).endVertex();
            worldrenderer.pos(D-mop.sideDistance, D-mop.top,	d+mop.right).endVertex();
            
            worldrenderer.pos(D-mop.sideDistance, d+mop.bottom, d+mop.right).endVertex();
            worldrenderer.pos(D-mop.sideDistance, D-mop.top,	D-mop.left).endVertex();
            
            worldrenderer.pos(D-mop.sideDistance, d+mop.bottom, D-mop.left).endVertex();
            worldrenderer.pos(D-mop.sideDistance, D-mop.top,	d+mop.right).endVertex();
            
            tessellator.draw();
            

            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
		}
	}
	

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();

		if(!mc.getRenderManager().isDebugBoundingBox() || mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.getEntityBoundingBox() == null)
			return;
		double d = 0.0020000000949949026D;
		mc.mcProfiler.startSection("blockBBs");

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
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.partialTicks;
        double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.partialTicks;
        double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.partialTicks;
        GlStateManager.translate(-x, -y, -z);
		
        AxisAlignedBB searchBB = mc.thePlayer.getEntityBoundingBox().expand(2,2,2);
        
		List<AxisAlignedBB> aabbs = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, searchBB);
		
		for (AxisAlignedBB bb : aabbs) {
			RenderGlobal.drawOutlinedBoundingBox(bb.expand(d, d, d), 127, 255, 127, 255);
		}
		
		GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		
		mc.mcProfiler.endSection();
	}
	
}
