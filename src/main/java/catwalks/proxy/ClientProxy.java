package catwalks.proxy;

import catwalks.Conf;
import catwalks.Const;
import catwalks.raytrace.RayTraceUtil.VertexList;
import catwalks.register.RenderRegister;
import catwalks.render.ModelHandle;
import catwalks.render.ShaderHelper;
import catwalks.shade.ccl.raytracer.RayTracer;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.CustomFaceRayTraceResult;
import catwalks.util.GeneralUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClientProxy extends CommonProxy {
	
	@Override
	public MinecraftServer getServer() {
		return FMLClientHandler.instance().getServer();
	}
	
	public EntityPlayer getPlayerLooking(Vec3d start, Vec3d end) {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	public static List<Tuple<Vector3, Double>> hits = new ArrayList<>();
	
	public void preInit() {
		RenderRegister.Blocks.initRender();
		RenderRegister.Items.initRender();
		RenderRegister.Parts.initRender();
		MinecraftForge.EVENT_BUS.register(new Conf());
		OBJLoader.INSTANCE.addDomain(Const.MODID);
		ShaderHelper.initShaders();
		ModelHandle.init();
	}
	
	public void reloadConfigs() {
		if(Minecraft.getMinecraft().renderGlobal != null)
			Minecraft.getMinecraft().renderGlobal.loadRenderers();
	}
	
	@SubscribeEvent
	public void highlight(DrawBlockHighlightEvent event) {
		BlockPos pos = event.getTarget().getBlockPos();
		
		if(event.getTarget() instanceof CustomFaceRayTraceResult) {
			event.setCanceled(true);
			
			CustomFaceRayTraceResult mop = (CustomFaceRayTraceResult) event.getTarget();
			
			GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            
            double d0 = event.getPlayer().lastTickPosX + (event.getPlayer().posX - event.getPlayer().lastTickPosX) * (double)event.getPartialTicks();
            double d1 = event.getPlayer().lastTickPosY + (event.getPlayer().posY - event.getPlayer().lastTickPosY) * (double)event.getPartialTicks();
            double d2 = event.getPlayer().lastTickPosZ + (event.getPlayer().posZ - event.getPlayer().lastTickPosZ) * (double)event.getPartialTicks();
            
            GlStateManager.translate(pos.getX()-d0, pos.getY()-d1, pos.getZ()-d2);
            
            
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer worldrenderer = tessellator.getBuffer();
            
            
            List<VertexList> points = mop.quad.getVertices();
            
            double s = 1.005, centering = ((1-s)/2)/s; // ( (1m size difference) / 2 ) / adjust for previous scale
            GlStateManager.scale(s, s, s);
            GlStateManager.translate(centering, centering, centering);
            
            GlStateManager.translate(-mop.offset.getX(), -mop.offset.getY(), -mop.offset.getZ());
            
            for (VertexList drawList : points) {
				
                worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            	
                Vec3d prev = null;
                
                for (int i = 0; i < drawList.vertices.length; i++) {
                	if(i == 0)
                		prev = drawList.vertices[drawList.vertices.length-1];
    				Vec3d point = drawList.vertices[i];
    				
            		worldrenderer.pos( prev.xCoord,  prev.yCoord,  prev.zCoord).endVertex();
    				worldrenderer.pos(point.xCoord, point.yCoord, point.zCoord).endVertex();
    				
    				if(drawList.shouldHaveNetting) {
    					for(int j = i+1; j < drawList.vertices.length; j++) {
    						Vec3d netPoint = drawList.vertices[j];
    	    				worldrenderer.pos(   point.xCoord,    point.yCoord,    point.zCoord).endVertex();
    						worldrenderer.pos(netPoint.xCoord, netPoint.yCoord, netPoint.zCoord).endVertex();
    					}
    				}
    				
    				prev = point;
				}
            	
                tessellator.draw();
                
			}
            
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
		}
	}
	
	{ /* dev only */ }
	
	@SubscribeEvent
	public void debugText(RenderGameOverlayEvent.Text event) {
		if(!Const.developmentEnvironment)
			return;
		
		if(!Minecraft.getMinecraft().gameSettings.showDebugInfo)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;
		if(player.isSneaking()) {
			event.getLeft().add(9, String.format("Motion: actual %.5f / %.5f / %.5f",
					player.posX - player.lastTickPosX,
					player.posY - player.lastTickPosY,
					player.posZ - player.lastTickPosZ));
		} else {
			event.getLeft().add(9, String.format("Motion: fields %.5f / %.5f / %.5f",
					player.motionX, player.motionY, player.motionZ));
		}
		
		event.getLeft().add(String.format("Move: forward %.5f, strafe %.5f", player.moveForward, player.moveStrafing));
		
		if(player.isSneaking()) {
			if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = mc.theWorld.getBlockState(blockpos);
                
                IBlockState maybeExtended = iblockstate.getBlock().getExtendedState(iblockstate, mc.theWorld, blockpos);
                
                if(maybeExtended instanceof IExtendedBlockState) {
                	IExtendedBlockState extended = (IExtendedBlockState)maybeExtended;
                	
                	for (IUnlistedProperty<?> entry : extended.getUnlistedNames())
                    {
                		Object value = extended.getValue(entry);
                		
                		String NULL_VALUE =
                				ChatFormatting.OBFUSCATED + "|" +
                				ChatFormatting.RESET + ChatFormatting.ITALIC + "NULL" +
                				ChatFormatting.OBFUSCATED + "|";
                		
                        String s = value == null ? NULL_VALUE : value.toString();

                        if (value == Boolean.TRUE)
                        {
                            s = ChatFormatting.GREEN + "" + ChatFormatting.ITALIC + s;
                        }
                        else if (value == Boolean.FALSE)
                        {
                            s = ChatFormatting.RED + "" +  ChatFormatting.ITALIC + s;
                        }
                        
                        event.getRight().add(ChatFormatting.ITALIC + entry.getName() + ": " + s);
                    }
                }
                
                
                
                event.getLeft().add("Looking at side: " + mc.objectMouseOver.sideHit.getName() + " - meta: " + iblockstate.getBlock().getMetaFromState(iblockstate));
            }
		}
	}
	

	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event)
	{
		if(!Const.developmentEnvironment)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();

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
		
		mc.mcProfiler.startSection("blockBBs");
		
		if(!mc.getRenderManager().isDebugBoundingBox() || mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.getEntityBoundingBox() == null)
		{} else {
			
			double d = 0.0020000000949949026D;
	        AxisAlignedBB searchBB = mc.thePlayer.getEntityBoundingBox().expand(2,2,2);
	        
			List<AxisAlignedBB> aabbs = mc.theWorld.getCollisionBoxes(mc.thePlayer, searchBB);
			
			for (AxisAlignedBB bb : aabbs) {
				AxisAlignedBB bb_ = bb.expand(d, d, d);
				RenderGlobal.drawBoundingBox(bb_.minX, bb_.minY, bb_.minZ, bb.maxX, bb.maxY, bb.maxZ, 0.5f, 1, 0.5f, 1);
			}
			
			// desired move vec
			
			if(mc.gameSettings.thirdPersonView > 0) {
				Vec3d p1 = new Vec3d(x, y+1, z);
				Vec3d p2 = p1.add(GeneralUtil.getDesiredMoveVector(rootPlayer));
				
				Tessellator tessellator = Tessellator.getInstance();
		        VertexBuffer vertexbuffer = tessellator.getBuffer();
		        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		        vertexbuffer.pos(p1.xCoord, p1.yCoord, p1.zCoord).color(127, 127, 255, 255).endVertex();
		        vertexbuffer.pos(p2.xCoord, p2.yCoord, p2.zCoord).color(127, 127, 255, 255).endVertex();
		        tessellator.draw();
			}
		}
		
		mc.mcProfiler.endStartSection("hitDist");
		
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo && rootPlayer.isSneaking()) {
		
			if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
			
				
                if(hits.isEmpty()) {
                	Vector3 vec = new Vector3(mc.objectMouseOver.hitVec);
                	Vector3 eyepos = new Vector3(RayTracer.getStartVec(rootPlayer));
                	hits.add(new Tuple<Vector3, Double>(vec, vec.copy().sub(eyepos).mag()));
                }
				
		        GlStateManager.enableTexture2D();
				
				RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
				hits.sort(new Comparator<Tuple<Vector3, Double>>() {
					@Override
					public int compare(Tuple<Vector3, Double> o1, Tuple<Vector3, Double> o2) {
						if(o1.getSecond() < o2.getSecond())
							return 1;
						if(o1.getSecond() == o2.getSecond())
							return 0;
						if(o1.getSecond() > o2.getSecond())
							return -1;
						return 0;
					}
				});
				int height = 9;
				int i = 0;
				if(mc.objectMouseOver.sideHit == EnumFacing.UP) {
					height = -9;
					i = 1;
				}
				for (Tuple<Vector3, Double> tuple : hits) {
					Vector3 v = tuple.getFirst();
					GlStateManager.pushMatrix();
					GlStateManager.translate(v.x, v.y, v.z);
					GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GlStateManager.scale(0.007, 0.007, 0.007);
					GlStateManager.rotate(180, 0, 0, 1);
					GlStateManager.translate(5, i*height, 0);
					
					mc.fontRendererObj.drawString(String.format("%.6f", tuple.getSecond() ), 0, 0, 0xFFFFFF);
					
					GlStateManager.popMatrix();
					i++;
				}
				
            	hits.clear();
			}
		}
		mc.mcProfiler.endSection();
		
		GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
	
}
