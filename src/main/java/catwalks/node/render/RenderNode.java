package catwalks.node.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import catwalks.Const;
import catwalks.node.EntityNodeBase;
import catwalks.raytrace.node.NodeTraceable;
import catwalks.raytrace.primitives.TexCoords;
import catwalks.raytrace.primitives.TexCoords.UV;
import catwalks.register.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class RenderNode extends Render<EntityNodeBase> {
	public static final ResourceLocation BASE_NODE_TEX = new ResourceLocation(Const.MODID, "textures/nodes/base.png");

	public RenderNode(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityNodeBase entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void doRender(EntityNodeBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(!(
			( Minecraft.getMinecraft().thePlayer.getHeldItemMainhand() != null && Minecraft.getMinecraft().thePlayer.getHeldItemMainhand().getItem() == ItemRegister.nodeManipulator ) ||
			( Minecraft.getMinecraft().thePlayer.getHeldItemOffhand() != null && Minecraft.getMinecraft().thePlayer.getHeldItemOffhand().getItem() == ItemRegister.nodeManipulator )
		))
			return;
		
		double l = 0.5;
        int red = 255, green = 255, blue = 255, alpha = 255;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(entity.rotationYaw, 0, -1, 0);
        GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
        
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        
        AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 0, 0, 0).expandXyz(EntityNodeBase.SIZE/2);
//        AxisAlignedBB bb = axisalignedbb.offset(-entity.posX, -entity.posY, -entity.posZ);
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        // bottom loop
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        
        // top loop
        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        
        // crosses
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        
        vb.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        
        vb.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();

        vb.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        // destroy timer
        double d = entity.destroyTimer == 0 ? 0 : (bb.maxY-bb.minY) * ( (entity.destroyTimer/10.0) - (partialTicks/10.0) );

        // sides
        
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        
        vb.pos(bb.minX, bb.minY+d, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.minY+d, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.minY+d, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.minY+d, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

//        Vec3d vec3d = entity.getLook(partialTicks);
        vb.begin(3, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(0, 0, 0).color(0, 0, 255, 255).endVertex();
        vb.pos(0, 0, l).color(0, 0, 255, 255).endVertex();
        tessellator.draw();
        
        GlStateManager.depthMask(false);
        
        if(entity.destroyTimer > 0) {
	        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
	        vb.pos(bb.minX, bb.minY, bb.minZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.minX, bb.minY+d, bb.minZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.maxX, bb.minY, bb.minZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.maxX, bb.minY+d, bb.minZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.minX, bb.minY, bb.maxZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.minX, bb.minY+d, bb.maxZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.maxX, bb.minY, bb.maxZ).color(0, 0, 255, 255).endVertex();
	        vb.pos(bb.maxX, bb.minY+d, bb.maxZ).color(0, 0, 255, 255).endVertex();
	        tessellator.draw();
	    }
        
        GlStateManager.depthMask(true);
        
        red   = 0;
        green = 127;
        blue  = 0;
        alpha = 255;
        
        List<NodeTraceable> list = entity.baseHits();
        
        // outlines
        for (NodeTraceable trace : list) {
			if(trace.getUv() == null) {
	        	Vec3d[] points = trace.getTraceable().edges();
	        	if(points.length == 0)
	        		continue;
				vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
				for (Vec3d vec : points) {
					vb.pos(vec.xCoord, vec.yCoord, vec.zCoord).color(red, green, blue, alpha).endVertex();
				}
				tessellator.draw();
			}
		}
        
        GlStateManager.enableTexture2D();
        
        Minecraft.getMinecraft().renderEngine.bindTexture(BASE_NODE_TEX);
                
        for (NodeTraceable trace : list) {
			if(trace.getUv() != null && trace.getUv() != TexCoords.NULL) {
	        	Vec3d[] points = trace.getTraceable().points();
	        	UV[] uvs = trace.getUv().uvs;
	        	
	        	if(uvs.length != points.length)
	        		continue;
	        	
	        	if(points.length == 3) {
	        		vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
					for (int i = 0; i < 3; i++) {
						vb.pos(points[i].xCoord, points[i].yCoord, points[i].zCoord).tex(uvs[i].u, uvs[i].v).endVertex();
					}
					tessellator.draw();
	        	}
	        	
	        	if(points.length == 4) {
	        		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					for (int i = 0; i < 4; i++) {
						vb.pos(points[i].xCoord, points[i].yCoord, points[i].zCoord).tex(uvs[i].u, uvs[i].v).endVertex();
					}
					tessellator.draw();
	        	}
	        	
				
			}
		}
        
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
		
        GlStateManager.popMatrix();
        
//		this.renderLivingLabel(entity, entity.getNodeName().getFormattedText(), x, y-0.375, z, 64);
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		// NOOP
	}
	

}
