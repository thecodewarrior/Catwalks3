package catwalks.node.render;

import java.util.List;

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

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.item.ItemNodeBase;
import catwalks.node.EntityNodeBase;
import catwalks.node.NodeUtil;
import catwalks.node.net.OutputPort;
import catwalks.raytrace.node.NodeTraceable;
import catwalks.raytrace.primitives.TexCoords;
import catwalks.raytrace.primitives.TexCoords.UV;
import catwalks.register.ItemRegister;
import catwalks.render.ShaderCallback;
import catwalks.render.ShaderHelper;
import catwalks.util.GeneralUtil;

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
		if(!GeneralUtil.isHolding(Minecraft.getMinecraft().thePlayer, (stack) -> stack.getItem() instanceof ItemNodeBase))
			return;
		
		int colorHex = entity.getNode().getColor();
        int alpha = 255;
        int red   = (colorHex >> 16) & 0xFF;
        int green = (colorHex >> 8) & 0xFF;
        int blue  = (colorHex >> 0) & 0xFF;
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(entity.rotationYaw, 0, -1, 0);
        GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
        
        GlStateManager.depthMask(false);
        
        // back
        GlStateManager.depthFunc(GL11.GL_GREATER);
        renderBounding(entity, partialTicks, red/2, green/2, blue/2, alpha);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        
        GlStateManager.depthMask(true);
        
        // normal        
        renderBounding(entity, partialTicks, red, green, blue, alpha);
        vb.begin(3, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(0, 0, 0).color(0, 0, 255, 255).endVertex();
        vb.pos(0, 0, 0.375).color(0, 0, 255, 255).endVertex();
        tessellator.draw();
        
        // highlight
        if(NodeUtil.nodeHit != null && NodeUtil.nodeHit.data().node == entity) {
	        double selectScale = 1.05;
	        GlStateManager.scale(selectScale,selectScale,selectScale);
	        GlStateManager.depthFunc(GL11.GL_ALWAYS);
	        int highlightBrightness = 64;
	        renderBounding(entity, partialTicks, highlightBrightness, highlightBrightness, highlightBrightness, 255);
	        GlStateManager.depthFunc(GL11.GL_LEQUAL);
	        GlStateManager.scale(1/selectScale,1/selectScale,1/selectScale);
        }
        
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        
        GlStateManager.translate(x, y, z);
        
        // normal
        GlStateManager.depthMask(false);
        
        // back
        GlStateManager.depthFunc(GL11.GL_GREATER);
        renderConnections(entity, partialTicks, 2);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        
        renderConnections(entity, partialTicks, 1);

        GlStateManager.depthMask(true);

        
        if(CatwalksMod.proxy.getSelectedNode() == entity && GeneralUtil.isHolding(Minecraft.getMinecraft().thePlayer, (stack) -> stack.getItem() == ItemRegister.nodeManipulator)) {
        	GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
        	
        	ShaderHelper.useShader(ShaderHelper.ring, new ShaderCallback() {
				@Override
				public void call(int shader) {
					int thicknessUniform = ARBShaderObjects.glGetUniformLocationARB(shader, "thickness");
					ARBShaderObjects.glUniform1fARB(thicknessUniform, 0.25f);
				}
			});
	        
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			vb.pos(-0.5, 0, -0.5).tex(0, 0).color(0, 0, 255, 255).endVertex();
			vb.pos( 0.5, 0, -0.5).tex(1, 0).color(0, 0, 255, 255).endVertex();
			vb.pos( 0.5, 0,  0.5).tex(1, 1).color(0, 0, 255, 255).endVertex();
			vb.pos(-0.5, 0,  0.5).tex(0, 1).color(0, 0, 255, 255).endVertex();
			tessellator.draw();
			
	        GlStateManager.rotate(entity.rotationYaw, 0, -1, 0);
	        
	        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			vb.pos(0, -0.5, -0.5).tex(0, 0).color(0, 0, 127, 255).endVertex();
			vb.pos(0,  0.5, -0.5).tex(1, 0).color(0, 0, 127, 255).endVertex();
			vb.pos(0,  0.5,  0.5).tex(1, 1).color(0, 0, 127, 255).endVertex();
			vb.pos(0, -0.5,  0.5).tex(0, 1).color(0, 0, 127, 255).endVertex();
			tessellator.draw();
	        
			ShaderHelper.releaseShader();
			
			GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
		}
        
	    GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
		
        GlStateManager.popMatrix();
	}
	
	public void renderConnections(EntityNodeBase entity, float partialTicks, int div) {
		Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        Vec3d posVec = entity.getPositionVector();
        
        for (OutputPort port : entity.getNode().outputs()) {
        	if(port.connectedLocs == null)
        		continue;
        	
        	int colorHex = port.getColor();
            int alpha = 255;
            int red   = (colorHex >> 16) & 0xFF; red = red/div;
            int green = (colorHex >> 8) & 0xFF; green = green/div;
            int blue  = (colorHex >> 0) & 0xFF; blue = blue/div;
            
            List<Vec3d> connections = port.connectedPoints();
            
            for (Vec3d loc : connections) {
            	Vec3d point = loc.subtract(posVec);
                
        		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                vb.pos(0, 0, 0).color(red, green, blue, alpha).endVertex();
                vb.pos(point.xCoord, point.yCoord, point.zCoord).color(red, green, blue, alpha).endVertex();
                tessellator.draw();
			}
		}
	}
	
	public void renderBounding(EntityNodeBase entity, float partialTicks, int red, int green, int blue, int alpha) {
		int redAcc = 0, greenAcc = 0, blueAcc = 255, alphaAcc = 255;
		
		AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 0, 0, 0).expandXyz(EntityNodeBase.SIZE/2);
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
        
        double d = entity.destroyTimer == 0 ? 0 : ( (entity.destroyTimer/10.0) - (partialTicks/10.0) );
        
        double amt = (EntityNodeBase.SIZE/2)*d;
        // crosses
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        // bottom
        vb.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(-amt, -amt, -amt).color(red, green, blue, alpha).endVertex();

        vb.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(amt, -amt, -amt).color(red, green, blue, alpha).endVertex();
        
        vb.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(amt, -amt, amt).color(red, green, blue, alpha).endVertex();
        
        vb.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(-amt, -amt, amt).color(red, green, blue, alpha).endVertex();
        
        // top
        vb.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(-amt, amt, -amt).color(red, green, blue, alpha).endVertex();

        vb.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(amt, amt, -amt).color(red, green, blue, alpha).endVertex();
        
        vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(amt, amt, amt).color(red, green, blue, alpha).endVertex();
        
        vb.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(-amt, amt, amt).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        
        if(entity.destroyTimer > 0) {
        	vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            // bottom
            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(-amt, -amt, -amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();

            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(amt, -amt, -amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            
            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(amt, -amt, amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            
            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(-amt, -amt, amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            
            // top
            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(-amt, amt, -amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();

            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(amt, amt, -amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            
            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(amt, amt, amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            
            vb.pos(0, 0, 0).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            vb.pos(-amt, amt, amt).color(redAcc, greenAcc, blueAcc, alphaAcc).endVertex();
            tessellator.draw();
        }

        // destroy timer
//        double d = entity.destroyTimer == 0 ? 0 : (bb.maxY-bb.minY) * ( (entity.destroyTimer/10.0) - (partialTicks/10.0) );

        // sides
        
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        
        vb.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        
//        if(entity.destroyTimer > 0) {
//	        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//	        vb.pos(bb.minX, bb.minY, bb.minZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.minX, bb.minY+d, bb.minZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.maxX, bb.minY, bb.minZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.maxX, bb.minY+d, bb.minZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.minX, bb.minY, bb.maxZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.minX, bb.minY+d, bb.maxZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.maxX, bb.minY, bb.maxZ).color(0, 0, 255, 255).endVertex();
//	        vb.pos(bb.maxX, bb.minY+d, bb.maxZ).color(0, 0, 255, 255).endVertex();
//	        tessellator.draw();
//	    }
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		// NOOP
	}
	

}
