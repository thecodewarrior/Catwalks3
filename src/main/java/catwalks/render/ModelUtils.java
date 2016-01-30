package catwalks.render;

import java.util.List;

import com.google.common.primitives.Ints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class ModelUtils {
	
	public static TextureAtlasSprite getSprite(ResourceLocation location) {
		if(Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(location.toString()) == null) {
			int i = 0; // breakpoint here to test for non-registered texture vs. not found texture.
		}
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
	}
	
    public static int[] vertexToInts(double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
        return new int[] {
                Float.floatToRawIntBits((float) x),
                Float.floatToRawIntBits((float) y),
                Float.floatToRawIntBits((float) z),
                -1,
                Float.floatToRawIntBits(sprite.getInterpolatedU(u)),
                Float.floatToRawIntBits(sprite.getInterpolatedV(v)),
                0
        };
    }

	public static void twoFace(List<BakedQuad> quads, TextureAtlasSprite sprite, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		quads.add(quad( sprite, v1, v2, v3, v4 ));
		quads.add(quad( sprite, v4, v3, v2, v1 ));
	}
    
    public static BakedQuad quad(TextureAtlasSprite sprite, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);

        return new BakedQuad(Ints.concat(
                vertexToInts(v1.xCoord, v1.yCoord, v1.zCoord, 0,  0 , sprite),
                vertexToInts(v2.xCoord, v2.yCoord, v2.zCoord, 0,  16, sprite),
                vertexToInts(v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, sprite),
                vertexToInts(v4.xCoord, v4.yCoord, v4.zCoord, 16, 0 , sprite)
        ), -1, side);
    }
    
    public static void putFace(List<BakedQuad> quads, EnumFacing facing, TextureAtlasSprite sprite) {
    	
    	switch(facing) {
    	case DOWN:
    		twoFace(quads, sprite,
        		new Vec3(1, 0, 0),
        		new Vec3(1, 0, 1),
        		new Vec3(0, 0, 1),
        		new Vec3(0, 0, 0)
        	);
    		break;
    	case UP:
    		twoFace(quads, sprite,
            	new Vec3(0, 1, 0),
        		new Vec3(0, 1, 1),
        		new Vec3(1, 1, 1),
        		new Vec3(1, 1, 0)
        	);
    		break;
    	case NORTH:
    		twoFace(quads, sprite,
    			new Vec3(1, 1, 0),
            	new Vec3(1, 0, 0),
        		new Vec3(0, 0, 0),
    			new Vec3(0, 1, 0)
        	);
    		break;
    	case SOUTH:
    		twoFace(quads, sprite,
            	new Vec3(0, 1, 1),
            	new Vec3(0, 0, 1),
        		new Vec3(1, 0, 1),
        		new Vec3(1, 1, 1)
        	);
    		break;
    	case EAST:
    		twoFace(quads, sprite,
        		new Vec3(0, 1, 0),
            	new Vec3(0, 0, 0),
            	new Vec3(0, 0, 1),
    			new Vec3(0, 1, 1)
        	);
    		break;
    	case WEST:
    		twoFace(quads, sprite,
    			new Vec3(1, 1, 1),
            	new Vec3(1, 0, 1),
            	new Vec3(1, 0, 0),
        		new Vec3(1, 1, 0)
        	);
    		break;
    	}
    }
}
