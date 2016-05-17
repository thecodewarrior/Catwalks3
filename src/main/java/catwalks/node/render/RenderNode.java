package catwalks.node.render;

import catwalks.node.EntityNodeBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class RenderNode extends Render<EntityNodeBase> {

	public RenderNode(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityNodeBase entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void doRender(EntityNodeBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
		// TODO Turn off rendering normally, show only with manipulator in hand
		
		double l = 0.5;
        int red = 255, green = 255, blue = 255, alpha = 255;

		GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        
        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
        AxisAlignedBB bb = axisalignedbb.offset(x - entity.posX, y - entity.posY, z - entity.posZ);
        bb = bb.offset(0, ( bb.maxX - bb.minX ) / -2, 0);
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        Vec3d vec3d = entity.getLook(partialTicks);
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(x, y, z).color(0, 0, 255, 255).endVertex();
        vertexbuffer.pos(x + vec3d.xCoord * l, y + vec3d.yCoord * l, z + vec3d.zCoord * l).color(0, 0, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
		
		this.renderLivingLabel(entity, entity.getNodeName().getFormattedText(), x, y-0.375, z, 64);
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		// NOOP
	}
	

}
