package catwalks.render;

import java.util.List;

import com.google.common.primitives.Ints;

import catwalks.util.Logs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class ModelUtils {
	
	public static class SpritelessQuad {
		public Vec3 p1, p2, p3, p4;
		public float u1, v1, u2, v2, u3, v3, u4, v4;
		
		public EnumFacing side;
		
		public SpritelessQuad(
				Vec3 p1, float u1, float v1,
				Vec3 p2, float u2, float v2,
				Vec3 p3, float u3, float v3,
				Vec3 p4, float u4, float v4,
				EnumFacing side) {
			
			this.p1 = p1;
			this.u1 = u1;
			this.v1 = v1;
			
			this.p2 = p2;
			this.u2 = u2;
			this.v2 = v2;
			
			this.p3 = p3;
			this.u3 = u3;
			this.v3 = v3;
			
			this.p4 = p4;
			this.u4 = u4;
			this.v4 = v4;
			
			this.side = side;
		}
		
		public BakedQuad bakedQuad(TextureAtlasSprite sprite) {
			return new BakedQuad(Ints.concat(
	                vertexToInts(p1.xCoord, p1.yCoord, p1.zCoord, u1, v1, sprite),
	                vertexToInts(p2.xCoord, p2.yCoord, p2.zCoord, u2, v2, sprite),
	                vertexToInts(p3.xCoord, p3.yCoord, p3.zCoord, u3, v3, sprite),
	                vertexToInts(p4.xCoord, p4.yCoord, p4.zCoord, u4, v4, sprite)
	        ), -1, side);
		}
	}
	
	public static class SpritelessConditionalQuad extends SpritelessQuad {
		public int conditionID;
		public SpritelessConditionalQuad( int conditionID,
				Vec3 p1, float u1, float v1,
				Vec3 p2, float u2, float v2,
				Vec3 p3, float u3, float v3,
				Vec3 p4, float u4, float v4,
				EnumFacing side) {
			super(p1, u1, v1, p2, u2, v2, p3, u3, v3, p4, u4, v4, side);
			this.conditionID = conditionID;
		}
	}
	
	public static TextureAtlasSprite getSprite(ResourceLocation location) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		return map.getAtlasSprite(location.toString());
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

    public static void twoFace(List<SpritelessConditionalQuad> quads, int condition, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		quads.add(quadInvertSide( condition, v1, v2, v3, v4 ));
		quads.add(quad( condition, v4, v3, v2, v1 ));
	}
    
    public static SpritelessConditionalQuad quad(int condition, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);

        return new SpritelessConditionalQuad(condition,
        		v1, 16, 0,
        		v2, 16, 16,
        		v3, 0,  16,
        		v4, 0,  0,
        	side);
    }
    
    public static SpritelessConditionalQuad quadInvertSide(int condition, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord).getOpposite();

        return new SpritelessConditionalQuad(condition,
        		v1, 0,  0,
        		v2, 0,  16,
        		v3, 16, 16,
        		v4, 16, 0,
        	side);
    }
    
    
	public static void twoFace(List<SpritelessQuad> quads, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		quads.add(quadInvertSide( v1, v2, v3, v4 ));
		quads.add(quad( v4, v3, v2, v1 ));
	}
    
    public static SpritelessQuad quad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);

        return new SpritelessQuad(
        		v1, 16, 0,
        		v2, 16, 16,
        		v3, 0,  16,
        		v4, 0,  0,
        	side);
    }
    
    public static SpritelessQuad quadInvertSide(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord).getOpposite();

        return new SpritelessQuad(
        		v1, 0,  0,
        		v2, 0,  16,
        		v3, 16, 16,
        		v4, 16, 0,
        	side);
    }
    
    public static void processQuads(List<SpritelessQuad> rawQuads, List<BakedQuad> quads, TextureAtlasSprite sprite) {
    	for (SpritelessQuad quad : rawQuads) {
			quads.add(quad.bakedQuad(sprite));
		}
    }
    
    public static void processConditionalQuads(List<SpritelessConditionalQuad> rawQuads, List<BakedQuad> quads, TextureAtlasSprite sprite, boolean... conditions) {
    	for (SpritelessConditionalQuad quad : rawQuads) {
			if(quad.conditionID < 0 || quad.conditionID < conditions.length && conditions[quad.conditionID]) {
				quads.add(quad.bakedQuad(sprite));
			}
		}
    }
    
    public static void putQuad(List<SpritelessQuad> quads, EnumFacing facing,
    		double x1, double y1, double z1, float u1, float v1,
    		double x2, double y2, double z2, float u2, float v2,
    		double x3, double y3, double z3, float u3, float v3,
    		double x4, double y4, double z4, float u4, float v4) {
    	
    	Vec3
    		p1 = new Vec3(x1,y1,z1),
    		p2 = new Vec3(x2,y2,z2),
    		p3 = new Vec3(x3,y3,z3),
    		p4 = new Vec3(x4,y4,z4);

        quads.add(
        	new SpritelessQuad(
        		p1, u1, v1,
        		p2, u2, v2,
        		p3, u3, v3,
        		p4, u4, v4,
        	facing)
        );
        
        quads.add(
        	new SpritelessQuad(
        		p4, u4, v4,
        		p3, u3, v3,
        		p2, u2, v2,
        		p1, u1, v1,
        	facing)
        );
    	
    }
    
    public static void putFace(List<SpritelessQuad> quads, EnumFacing facing) {
    	
    	switch(facing) {
    	case DOWN:
    		twoFace(quads,
        		new Vec3(1, 0, 0),
        		new Vec3(1, 0, 1),
        		new Vec3(0, 0, 1),
        		new Vec3(0, 0, 0)
        	);
    		break;
    	case UP:
    		twoFace(quads,
            	new Vec3(0, 1, 0),
        		new Vec3(0, 1, 1),
        		new Vec3(1, 1, 1),
        		new Vec3(1, 1, 0)
        	);
    		break;
    	case NORTH:
    		twoFace(quads,
    			new Vec3(1, 1, 0),
            	new Vec3(1, 0, 0),
        		new Vec3(0, 0, 0),
    			new Vec3(0, 1, 0)
        	);
    		break;
    	case SOUTH:
    		twoFace(quads,
            	new Vec3(0, 1, 1),
            	new Vec3(0, 0, 1),
        		new Vec3(1, 0, 1),
        		new Vec3(1, 1, 1)
        	);
    		break;
    	case EAST:
    		twoFace(quads,
        		new Vec3(0, 1, 0),
            	new Vec3(0, 0, 0),
            	new Vec3(0, 0, 1),
    			new Vec3(0, 1, 1)
        	);
    		break;
    	case WEST:
    		twoFace(quads,
    			new Vec3(1, 1, 1),
            	new Vec3(1, 0, 1),
            	new Vec3(1, 0, 0),
        		new Vec3(1, 1, 0)
        	);
    		break;
    	}
    }
    
public static void putFace(List<SpritelessConditionalQuad> quads, EnumFacing facing, int condition) {
    	
    	switch(facing) {
    	case DOWN:
    		twoFace(quads, condition,
        		new Vec3(1, 0, 0),
        		new Vec3(1, 0, 1),
        		new Vec3(0, 0, 1),
        		new Vec3(0, 0, 0)
        	);
    		break;
    	case UP:
    		twoFace(quads, condition,
            	new Vec3(0, 1, 0),
        		new Vec3(0, 1, 1),
        		new Vec3(1, 1, 1),
        		new Vec3(1, 1, 0)
        	);
    		break;
    	case NORTH:
    		twoFace(quads, condition,
    			new Vec3(1, 1, 0),
            	new Vec3(1, 0, 0),
        		new Vec3(0, 0, 0),
    			new Vec3(0, 1, 0)
        	);
    		break;
    	case SOUTH:
    		twoFace(quads, condition,
            	new Vec3(0, 1, 1),
            	new Vec3(0, 0, 1),
        		new Vec3(1, 0, 1),
        		new Vec3(1, 1, 1)
        	);
    		break;
    	case EAST:
    		twoFace(quads, condition,
        		new Vec3(0, 1, 0),
            	new Vec3(0, 0, 0),
            	new Vec3(0, 0, 1),
    			new Vec3(0, 1, 1)
        	);
    		break;
    	case WEST:
    		twoFace(quads, condition,
    			new Vec3(1, 1, 1),
            	new Vec3(1, 0, 1),
            	new Vec3(1, 0, 0),
        		new Vec3(1, 1, 0)
        	);
    		break;
    	}
    }
}
