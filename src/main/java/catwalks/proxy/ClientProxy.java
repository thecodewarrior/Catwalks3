package catwalks.proxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import catwalks.Conf;
import catwalks.Const;
import catwalks.block.BlockCatwalkBase.Face;
import catwalks.movement.MovementHandler;
import catwalks.register.BlockRegister;
import catwalks.register.ItemRegister;
import catwalks.render.cached.CachedSmartModel;
import catwalks.render.cached.models.CatwalkModel;
import catwalks.render.cached.models.LadderModel;
import catwalks.render.cached.models.StairBottomModel;
import catwalks.render.cached.models.StairTopModel;
import catwalks.shade.ccl.raytracer.RayTracer;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.ExtendedFlatHighlightMOP;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	
	public EntityPlayer getPlayerLooking(Vec3 start, Vec3 end) {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	public static List<Tuple<Vector3, Double>> hits = new ArrayList<>();
	
	public void preInit() {
		BlockRegister.initRender();
		ItemRegister.initRender();
		MinecraftForge.EVENT_BUS.register(new Conf());
		OBJLoader.instance.addDomain(Const.MODID);
	}
	
	public void reloadConfigs() {
		if(Minecraft.getMinecraft().renderGlobal != null)
			Minecraft.getMinecraft().renderGlobal.loadRenderers();
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
            
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            
            worldrenderer.begin(1, DefaultVertexFormats.POSITION);
            
            Face q = mop.quad.copy();
            Vector3[] points = q.points();
            
            Matrix4 matrix = new Matrix4();
            matrix.translate(Vector3.center.copy().multiply(-1));
            
            if(!Minecraft.getMinecraft().theWorld.isSideSolid(event.target.getBlockPos().offset(event.target.sideHit), event.target.sideHit.getOpposite()))
            	matrix.scale(new Vector3(1.002, 1.002, 1.002));
            matrix.translate(Vector3.center);
            q.apply(matrix);
            
            Vector3 prev = null;
            
            for (int i = 0; i < points.length; i++) {
            	if(i == 0)
            		prev = points[points.length-1];
				Vector3 point = points[i];
				
				worldrenderer.pos(prev.x,  prev.y,  prev.z ).endVertex();
				worldrenderer.pos(point.x, point.y, point.z).endVertex();
				
				for (int j = 0; j < points.length/2; j++) {
					if(j != i && j != i+1 && j != i-1) {
						
						Vector3 opp = points[j];
						
						worldrenderer.pos(opp.x,   opp.y,   opp.z  ).endVertex();
						worldrenderer.pos(point.x, point.y, point.z).endVertex();
						
					}
				}
				prev = point;
			}
            
            tessellator.draw();
            

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
			event.left.add(9, String.format("Motion: actual %.5f / %.5f / %.5f",
					player.posX - player.lastTickPosX,
					player.posY - player.lastTickPosY,
					player.posZ - player.lastTickPosZ));
		} else {
			event.left.add(9, String.format("Motion: fields %.5f / %.5f / %.5f",
					player.motionX, player.motionY, player.motionZ));
		}
		
		event.left.add(String.format("Move: forward %.5f, strafe %.5f", player.moveForward, player.moveStrafing));
		/*
		for(IAttributeInstance instance : player.getAttributeMap().getAllAttributes()) {
			if(instance instanceof ModifiableAttributeInstance) {
				ModifiableAttributeInstance modInstance = (ModifiableAttributeInstance)instance;
				
				if(modInstance.func_111122_c().size() > 0)
					event.left.add(String.format("%.3f", modInstance.getBaseValue()));
				
				double x = modInstance.getBaseValue();
				if(modInstance.getModifiersByOperation(0).size() > 0) {
					event.left.add(String.format("    %.3f | op0 => x+a+b", x));
					for (AttributeModifier mod : modInstance.getModifiersByOperation(0)) {
						event.left.add(String.format("  + %.3f = %.3f | %s", mod.getAmount(), mod.getName()));
						x += mod.getAmount();
					}
				}
				
				double v = 1;
				if(modInstance.getModifiersByOperation(1).size() > 0) {
					event.left.add(String.format( "    %.3f | op1 => x*(1+a+b)", v));
					for (AttributeModifier mod : modInstance.getModifiersByOperation(1)) {
						event.left.add(String.format("  + %.3f | %s", mod.getAmount(), mod.getName()));
						v += mod.getAmount();
					}
					event.left.add(String.format("x = %.3f * %.3f", x, v));
					x = x * v;
				}
				
				if(modInstance.getModifiersByOperation(2).size() > 0) {
					event.left.add(String.format( "    %.3f | op1 => x*(1+a+b)", x));
					for (AttributeModifier mod : modInstance.getModifiersByOperation(2)) {
						event.left.add(String.format("  * %.3f + 1 | %s", mod.getAmount(), mod.getName()));
						x = x * (1+mod.getAmount());
					}
				}
				
				if(modInstance.func_111122_c().size() > 0)
					event.left.add(String.format("%.3f => %.3f  %s", modInstance.getBaseValue(), x, modInstance.getAttributeValue() == x ? "Y" : "N - " + modInstance.getAttributeValue()));
			}
		} /**/
		
		if(player.isSneaking()) {
			if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = mc.theWorld.getBlockState(blockpos);
                
                IBlockState maybeExtended = iblockstate.getBlock().getExtendedState(iblockstate, mc.theWorld, blockpos);
                
                if(maybeExtended instanceof IExtendedBlockState) {
                	IExtendedBlockState extended = (IExtendedBlockState)maybeExtended;
                	
                	for (IUnlistedProperty<?> entry : extended.getUnlistedNames())
                    {
                		Object value = extended.getValue(entry);
                        String s = value.toString();

                        if (value == Boolean.TRUE)
                        {
                            s = EnumChatFormatting.GREEN + "" + EnumChatFormatting.ITALIC + s;
                        }
                        else if (value == Boolean.FALSE)
                        {
                            s = EnumChatFormatting.RED + "" +  EnumChatFormatting.ITALIC + s;
                        }

                        event.right.add(EnumChatFormatting.ITALIC + entry.getName() + ": " + s);
                    }
                }
                
                
                
                event.left.add("Looking at side: " + mc.objectMouseOver.sideHit.getName() + " - meta: " + iblockstate.getBlock().getMetaFromState(iblockstate));
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
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.partialTicks;
        double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.partialTicks;
        double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.partialTicks;
        GlStateManager.translate(-x, -y, -z);
		
		mc.mcProfiler.startSection("blockBBs");
		
		if(!mc.getRenderManager().isDebugBoundingBox() || mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.getEntityBoundingBox() == null)
		{} else {
			
			double d = 0.0020000000949949026D;
	        AxisAlignedBB searchBB = mc.thePlayer.getEntityBoundingBox().expand(2,2,2);
	        
			List<AxisAlignedBB> aabbs = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, searchBB);
			
			for (AxisAlignedBB bb : aabbs) {
				RenderGlobal.drawOutlinedBoundingBox(bb.expand(d, d, d), 127, 255, 127, 255);
			}
		}
		
		mc.mcProfiler.endStartSection("hitDist");
		
		if(Minecraft.getMinecraft().gameSettings.showDebugInfo && rootPlayer.isSneaking()) {
		
			if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			
				
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
