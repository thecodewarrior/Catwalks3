package catwalks.render.cached.models;

import java.util.ArrayList;
import java.util.List;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessConditionalQuad;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.cached.SimpleModel;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import scala.actors.threadpool.Arrays;

public class LadderModel extends SimpleModel {

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getKey(IBlockState rawstate) {
		IExtendedBlockState state = (IExtendedBlockState) rawstate;
		return Arrays.asList(new Object[] {
				state.getValue(Const.MATERIAL),
				state.getValue(Const.FACING),
				state.getValue(Const.BOTTOM),
				state.getValue(Const.NORTH),
				state.getValue(Const.SOUTH),
				state.getValue(Const.EAST),
				state.getValue(Const.WEST),
				state.getValue(Const.NORTH_LADDER_EXT),
				state.getValue(Const.SOUTH_LADDER_EXT),
				state.getValue(Const.EAST_LADDER_EXT),
				state.getValue(Const.WEST_LADDER_EXT),
				state.getValue(Const.TAPE),
				state.getValue(Const.LIGHTS),
				state.getValue(Const.SPEED)
		});
	}

	@Override
	protected List<BakedQuad> generateQuads(List<Object> list) {
		int i = 0;
		
		EnumCatwalkMaterial material = (EnumCatwalkMaterial) list.get(i++);
		EnumFacing facing = (EnumFacing) list.get(i++);
		
		boolean down      = (boolean) list.get(i++),
				north     = (boolean) list.get(i++),
				south     = (boolean) list.get(i++),
				west      = (boolean) list.get(i++),
				east      = (boolean) list.get(i++),
				north_ext = (boolean) list.get(i++),
				south_ext = (boolean) list.get(i++),
				west_ext  = (boolean) list.get(i++),
				east_ext  = (boolean) list.get(i++),
				tape      = (boolean) list.get(i++),
				lights    = (boolean) list.get(i++),
				speed     = (boolean) list.get(i++);
		
        String mat = material.getName().toLowerCase();
		
		TextureAtlasSprite
			baseTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/base")),
        
			tapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/tape")),
        	lightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/lights")),
        	speedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/speed"));
		
		
		List<SpritelessConditionalQuad> quads;
		quads = new ArrayList<>();
		int cond = 0;
		double p  = 1/16f, P  = 1-p;
//		double p2 = 2/16f, P2 = 1-p2;
		
		// bottom
		ModelUtils.twoFace(quads, EnumFacing.DOWN, cond++,
			0, 0, 0,  0,  0,
			0, 0, 1,  0, .5,
			1, 0, 1, .5, .5,
			1, 0, 0, .5,  0
		);
		//north ( ladder )
		ModelUtils.twoFace(quads, null, cond++,
			0, 0, p, .5,  1,
			0, 1, p, .5, .5,
			1, 1, p,  1, .5,
			1, 0, p,  1,  1
		);
		//south
		ModelUtils.twoFace(quads, null, cond++,
			0, 0, P,  0,  1,
			0, 1, P,  0, .5,
			1, 1, P, .5, .5,
			1, 0, P, .5,  1
		);
		//east
		ModelUtils.twoFace(quads, null, cond++,
			P, 0, 0, .5,  1,
			P, 1, 0, .5, .5,
			P, 1, 1,  0, .5,
			P, 0, 1,  0,  1
		);
		//west
		ModelUtils.twoFace(quads, null, cond++,
			p, 0, 0,  0,  1,
			p, 1, 0,  0, .5,
			p, 1, 1, .5, .5,
			p, 0, 1, .5,  1
		);

		int rot = -GeneralUtil.getRotation(EnumFacing.NORTH, facing);
		Matrix4 matrix = new Matrix4().translate(new Vector3(0.5,0.5,0.5)).rotate((Math.PI/2)*rot, new Vector3(0,1,0)).translate(new Vector3(-0.5,-0.5,-0.5));
		Vector3 vec = new Vector3();
		
		for (SpritelessQuad quad : quads) {
			matrix.apply(vec.set(quad.p1.xCoord, quad.p1.yCoord, quad.p1.zCoord));
			quad.p1 = vec.vec3();
			
			matrix.apply(vec.set(quad.p2.xCoord, quad.p2.yCoord, quad.p2.zCoord));
			quad.p2 = vec.vec3();
			
			matrix.apply(vec.set(quad.p3.xCoord, quad.p3.yCoord, quad.p3.zCoord));
			quad.p3 = vec.vec3();
			
			matrix.apply(vec.set(quad.p4.xCoord, quad.p4.yCoord, quad.p4.zCoord));
			quad.p4 = vec.vec3();
		}
		
		List<BakedQuad> finalQuads = new ArrayList<>();
    	
    	ModelUtils.processConditionalQuads(quads, finalQuads, baseTex,
        		down, north, south, east, west);
        if(  tape) ModelUtils.processConditionalQuads(quads, finalQuads, tapeTex,
        		down, north, south, east, west);
        if(lights) ModelUtils.processConditionalQuads(quads, finalQuads, lightsTex,
        		down, north, south, east, west);
        if( speed) ModelUtils.processConditionalQuads(quads, finalQuads, speedTex,
        		down, north, south, east, west);
		
		return finalQuads;
	}

}
