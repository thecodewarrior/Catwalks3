package catwalks.render;

import java.util.List;

import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class ModelUtils {

	public static class SpritelessQuad {
		public Vec3d p1, p2, p3, p4;
		public float u1, v1, u2, v2, u3, v3, u4, v4;

		public EnumFacing side;
		public int conditionID;
		public boolean bothFaces;
		public VertexFormat format = DefaultVertexFormats.ITEM; // forge uses this as the default
		
		public SpritelessQuad(
				Vec3d p1, float u1, float v1,
				Vec3d p2, float u2, float v2,
				Vec3d p3, float u3, float v3,
				Vec3d p4, float u4, float v4) {

			this.conditionID = -1;
			this.side = null;

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
		}
		
		public SpritelessQuad showBackface() {
			this.bothFaces = true;
			return this;
		}
		
		public SpritelessQuad condition(int condition) {
			this.conditionID = condition;
			return this;
		}
		
		public SpritelessQuad setSide(EnumFacing side) {
			this.side = side;
			return this;
		}
		
		public SpritelessQuad setFormat(VertexFormat format) {
			this.format = format;
			return this;
		}
		
		public SpritelessQuad north() {
			return setSide(EnumFacing.NORTH);
		}
		
		public SpritelessQuad south() {
			return setSide(EnumFacing.SOUTH);
		}
		
		public SpritelessQuad east() {
			return setSide(EnumFacing.EAST);
		}
		
		public SpritelessQuad west() {
			return setSide(EnumFacing.WEST);
		}
		
		public SpritelessQuad up() {
			return setSide(EnumFacing.UP);
		}
		
		public SpritelessQuad down() {
			return setSide(EnumFacing.DOWN);
		}
		
		public SpritelessQuad nocull() {
			return setSide(null);
		}
		
	    public BakedQuad bakedQuad(TextureAtlasSprite sprite) {
	        Vec3d normal = p1.subtract(p2).crossProduct(p3.subtract(p2)).normalize().scale(-1);

	        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
	        builder.setTexture(sprite);
	        builder.setQuadOrientation(side);
	        putVertex(builder, normal, p1.xCoord, p1.yCoord, p1.zCoord, u1, v1, sprite);
	        putVertex(builder, normal, p2.xCoord, p2.yCoord, p2.zCoord, u2, v2, sprite);
	        putVertex(builder, normal, p3.xCoord, p3.yCoord, p3.zCoord, u3, v3, sprite);
	        putVertex(builder, normal, p4.xCoord, p4.yCoord, p4.zCoord, u4, v4, sprite);
	        return builder.build();
	    }
	    
	    public BakedQuad backBakedQuad(TextureAtlasSprite sprite) {
	        Vec3d normal = p1.subtract(p2).crossProduct(p3.subtract(p2)).normalize();

	        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
	        builder.setTexture(sprite);
	        builder.setQuadOrientation(side);
	        putVertex(builder, normal, p4.xCoord, p4.yCoord, p4.zCoord, u4, v4, sprite);
	        putVertex(builder, normal, p3.xCoord, p3.yCoord, p3.zCoord, u3, v3, sprite);
	        putVertex(builder, normal, p2.xCoord, p2.yCoord, p2.zCoord, u2, v2, sprite);
	        putVertex(builder, normal, p1.xCoord, p1.yCoord, p1.zCoord, u1, v1, sprite);
	        return builder.build();
	    }
	    
	    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
	        for (int e = 0; e < format.getElementCount(); e++) {
	            switch (format.getElement(e).getUsage()) {
	                case POSITION:
	                    builder.put(e, (float)x, (float)y, (float)z, 1.0f);
	                    break;
	                case COLOR:
	                    builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
	                    break;
	                case UV:
	                    if (format.getElement(e).getIndex() == 0) {
	                        u = sprite.getInterpolatedU(u);
	                        v = sprite.getInterpolatedV(v);
	                        builder.put(e, u, v, 0f, 1f);
	                        break;
	                    }
	                case NORMAL:
	                    builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
	                    break;
	                default:
	                    builder.put(e);
	                    break;
	            }
	        }
	    }
	}

	/**
	 * Get the sprite from the texture sheet for the specified location.
	 * @param location
	 * @return
	 */
	public static TextureAtlasSprite getSprite(ResourceLocation location) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		return map.getAtlasSprite(location == null ? null : location.toString());
	}

	public static int conditionCounter = 0;
	public static void resetConditionCounter() {
		conditionCounter = 0;
	}
	public static void sameCondition() { conditionCounter--; }
	
	/**
	 * Creates a quad and adds it to the list, UV values span the entire sprite
	 * @param quads The list of quads to add to
	 * @param v1 Top left uv(0,0)
	 * @param v2 Top right uv(1,0)
	 * @param v3 Bottom right uv(1,1)
	 * @param v4 Bottom left uv(0,1)
	 */
	public static SpritelessQuad fullSpriteQuad(List<SpritelessQuad> quads, Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4) {
		SpritelessQuad quad = createQuad(
			v1.xCoord, v1.yCoord, v1.zCoord, 0, 0,
			v2.xCoord, v2.yCoord, v2.zCoord, 1, 0,
			v3.xCoord, v3.yCoord, v3.zCoord, 1, 1,
			v4.xCoord, v4.yCoord, v4.zCoord, 0, 1
		);
		
		quads.add(quad);
		return quad;
	}

	/**
	 * Creates a quad and adds it to the list, UVs are from 0-1
	 * @param quads The list of quads to add to
	 */
	public static SpritelessQuad quad(List<SpritelessQuad> quads,
			double x1, double y1, double z1, double u1, double v1,
			double x2, double y2, double z2, double u2, double v2,
			double x3, double y3, double z3, double u3, double v3,
			double x4, double y4, double z4, double u4, double v4) {

		SpritelessQuad quad = createQuad(
			x1, y1, z1, u1, v1,
			x2, y2, z2, u2, v2,
			x3, y3, z3, u3, v3,
			x4, y4, z4, u4, v4
		);
		
		quads.add(quad);
		return quad;
	}
	
	private static int texSize = 16;
	
	public static void texSize(int size) {
		texSize = size;
	}
	
	/**
	 * Creates a quad and adds it to the list, UVs are from 0-16
	 * @param quads The list of quads to add to
	 */
	public static SpritelessQuad quadP(List<SpritelessQuad> quads,
			double x1, double y1, double z1, double u1, double v1,
			double x2, double y2, double z2, double u2, double v2,
			double x3, double y3, double z3, double u3, double v3,
			double x4, double y4, double z4, double u4, double v4) {
		
		SpritelessQuad quad = createQuad(
			x1, y1, z1, u1/texSize, v1/texSize,
			x2, y2, z2, u2/texSize, v2/texSize,
			x3, y3, z3, u3/texSize, v3/texSize,
			x4, y4, z4, u4/texSize, v4/texSize
		);
		
		quads.add(quad);
		return quad;
	}
	
	/**
	 * Creates a quad
	 * @param quads The list of quads to add to
	 */
	public static SpritelessQuad createQuad(
			double x1, double y1, double z1, double u1, double v1,
			double x2, double y2, double z2, double u2, double v2,
			double x3, double y3, double z3, double u3, double v3,
			double x4, double y4, double z4, double u4, double v4) {

		SpritelessQuad quad = new SpritelessQuad(
			new Vec3d(x1,y1,z1), (float)u1*16, (float)v1*16,
			new Vec3d(x2,y2,z2), (float)u2*16, (float)v2*16,
			new Vec3d(x3,y3,z3), (float)u3*16, (float)v3*16,
			new Vec3d(x4,y4,z4), (float)u4*16, (float)v4*16
		);
		
		if(conditionCounter >= 0)
			quad.condition(conditionCounter++);
		
		return quad;
	}

	/**
	 * Put the conditional quads into the baked quads list, using the supplied sprite and conditions.
	 * @param rawQuads List of spriteless quads to be processed
	 * @param quads List for final quads to be inserted into
	 * @param sprite TextureAtlasSprite to use
	 * @param conditions List of conditions
	 */
	public static void processConditionalQuads(List<SpritelessQuad> rawQuads, List<BakedQuad> quads, TextureAtlasSprite sprite, boolean... conditions) {
		for (SpritelessQuad quad : rawQuads) {
			if(quad.conditionID == -1 || ( quad.conditionID < conditions.length && conditions[quad.conditionID]) ) {
				// -1 is always true, anything else that's out of bounds is false
				quads.add(quad.bakedQuad(sprite));
				if(quad.bothFaces)
					quads.add(quad.backBakedQuad(sprite));
			}
		}
	}

	/**
	 * Creates a full sprite double face for the supplied side of a normal cube
	 * @param quads List of quads to put faces in
	 * @param facing Side of block to generate side for
	 * @param condition Condition ID for sides
	 */
	public static SpritelessQuad putFace(List<SpritelessQuad> quads, EnumFacing facing, int condition) {

		switch(facing) {
		case DOWN:
			return fullSpriteQuad(quads,
				new Vec3d(0, 0, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(1, 0, 1),
				new Vec3d(0, 0, 1)
			).down().condition(condition);
		case UP:
			return fullSpriteQuad(quads,
				new Vec3d(0, 1, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 1, 1),
				new Vec3d(0, 1, 1)
			).up().condition(condition);
		case NORTH:
			return fullSpriteQuad(quads,
				new Vec3d(0, 1, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(0, 0, 0)
			).north().condition(condition);
		case SOUTH:
			return fullSpriteQuad(quads,
				new Vec3d(1, 1, 1),
				new Vec3d(0, 1, 1),
				new Vec3d(0, 0, 1),
				new Vec3d(1, 0, 1)
			).south().condition(condition);
		case EAST:
			return fullSpriteQuad(quads,
				new Vec3d(1, 1, 0),
				new Vec3d(1, 1, 1),
				new Vec3d(1, 0, 1),
				new Vec3d(1, 0, 0)
			).east().condition(condition);
		case WEST:
			return fullSpriteQuad(quads,
				new Vec3d(0, 1, 1),
				new Vec3d(0, 1, 0),
				new Vec3d(0, 0, 0),
				new Vec3d(0, 0, 1)
			).west().condition(condition);
		}
		return null;
	}
}
