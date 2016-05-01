package catwalks.render;

import java.util.List;

import com.google.common.primitives.Ints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class ModelUtils {

	public static class SpritelessQuad {
		public Vec3 p1, p2, p3, p4;
		public float u1, v1, u2, v2, u3, v3, u4, v4;

		public EnumFacing side;
		public int conditionID;

		public SpritelessQuad( int condition,
				Vec3 p1, float u1, float v1,
				Vec3 p2, float u2, float v2,
				Vec3 p3, float u3, float v3,
				Vec3 p4, float u4, float v4,
				EnumFacing side) {

			this.conditionID = condition;

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

	/**
	 * Get the sprite from the texture sheet for the specified location.
	 * @param location
	 * @return
	 */
	public static TextureAtlasSprite getSprite(ResourceLocation location) {
<<<<<<< HEAD
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		return map.getAtlasSprite(location == null ? null : location.toString());
=======
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
>>>>>>> Scaffolds
	}

	/**
	 * Creates a quad and it's inverse so it can be seen from both sides, UV values span the entire sprite
	 * @param quads The list of quads to add to
	 * @param cull The side to cull the face, null for no culling
	 * @param condition The condition ID for enabling this quad
	 * @param v1 Top left uv(0,0)
	 * @param v2 Top right uv(1,0)
	 * @param v3 Bottom right uv(1,1)
	 * @param v4 Bottom left uv(0,1)
	 */
	public static void fullSpriteDoubleQuad(List<SpritelessQuad> quads, EnumFacing cull, int condition, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
		doubleQuad(quads, cull, condition,
			v1.xCoord, v1.yCoord, v1.zCoord, 0, 0,
			v2.xCoord, v2.yCoord, v2.zCoord, 1, 0,
			v3.xCoord, v3.yCoord, v3.zCoord, 1, 1,
			v4.xCoord, v4.yCoord, v4.zCoord, 0, 1
		);
	}

	/**
	 * Creates a quad and it's inverse so it can be seen from both sides
	 * @param quads The list of quads to add to
	 * @param side The side to cull the face, null for no culling
	 * @param condition The condition ID for enabling this quad
	 */
	public static void doubleQuad(List<SpritelessQuad> quads, EnumFacing side, int condition,
			double x1, double y1, double z1, double u1, double v1,
			double x2, double y2, double z2, double u2, double v2,
			double x3, double y3, double z3, double u3, double v3,
			double x4, double y4, double z4, double u4, double v4) {

		quads.add(new SpritelessQuad(condition,
			new Vec3(x1,y1,z1), (float)u1*16, (float)v1*16,
			new Vec3(x2,y2,z2), (float)u2*16, (float)v2*16,
			new Vec3(x3,y3,z3), (float)u3*16, (float)v3*16,
			new Vec3(x4,y4,z4), (float)u4*16, (float)v4*16,
			side));  // v1, v2, v3, v4
		quads.add(new SpritelessQuad(condition,
			new Vec3(x4,y4,z4), (float)u4*16, (float)v4*16,
			new Vec3(x3,y3,z3), (float)u3*16, (float)v3*16,
			new Vec3(x2,y2,z2), (float)u2*16, (float)v2*16,
			new Vec3(x1,y1,z1), (float)u1*16, (float)v1*16,
			side));  // v4, v3, v2, v1 (reverse order, face is flipped)
	}

	/**
	 * Creates a quad without it's inverse, meaning it's only visible from one side
	 * @param quads The list of quads to add to
	 * @param side The side to cull the face, null for no culling
	 * @param condition The condition ID for enabling this quad
	 */
	public static void singleQuad(List<SpritelessQuad> quads, EnumFacing side, int condition,
			double x1, double y1, double z1, double u1, double v1,
			double x2, double y2, double z2, double u2, double v2,
			double x3, double y3, double z3, double u3, double v3,
			double x4, double y4, double z4, double u4, double v4) {

		quads.add(new SpritelessQuad(condition,
			new Vec3(x1,y1,z1), (float)u1*16, (float)v1*16,
			new Vec3(x2,y2,z2), (float)u2*16, (float)v2*16,
			new Vec3(x3,y3,z3), (float)u3*16, (float)v3*16,
			new Vec3(x4,y4,z4), (float)u4*16, (float)v4*16,
			side));  // v1, v2, v3, v4
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
			}
		}
	}

	/**
	 * Creates a full sprite double face for the supplied side of a normal cube
	 * @param quads List of quads to put faces in
	 * @param facing Side of block to generate side for
	 * @param condition Condition ID for sides
	 */
	public static void putFace(List<SpritelessQuad> quads, EnumFacing facing, int condition) {

		switch(facing) {
		case DOWN:
			fullSpriteDoubleQuad(quads, EnumFacing.DOWN, condition,
				new Vec3(0, 0, 0),
				new Vec3(1, 0, 0),
				new Vec3(1, 0, 1),
				new Vec3(0, 0, 1)
			);
			break;
		case UP:
			fullSpriteDoubleQuad(quads, EnumFacing.UP, condition,
				new Vec3(0, 1, 0),
				new Vec3(1, 1, 0),
				new Vec3(1, 1, 1),
				new Vec3(0, 1, 1)
			);
			break;
		case NORTH:
			fullSpriteDoubleQuad(quads, EnumFacing.NORTH, condition,
				new Vec3(0, 1, 0),
				new Vec3(1, 1, 0),
				new Vec3(1, 0, 0),
				new Vec3(0, 0, 0)
			);
			break;
		case SOUTH:
			fullSpriteDoubleQuad(quads, EnumFacing.SOUTH, condition,
				new Vec3(1, 1, 1),
				new Vec3(0, 1, 1),
				new Vec3(0, 0, 1),
				new Vec3(1, 0, 1)
			);
			break;
		case EAST:
			fullSpriteDoubleQuad(quads, EnumFacing.EAST, condition,
				new Vec3(1, 1, 0),
				new Vec3(1, 1, 1),
				new Vec3(1, 0, 1),
				new Vec3(1, 0, 0)
			);
			break;
		case WEST:
			fullSpriteDoubleQuad(quads, EnumFacing.WEST, condition,
				new Vec3(0, 1, 1),
				new Vec3(0, 1, 0),
				new Vec3(0, 0, 0),
				new Vec3(0, 0, 1)
			);
			break;
		}
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
}
