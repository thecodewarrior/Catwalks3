package catwalks.render.cached.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.cached.SimpleModel;
import catwalks.util.GeneralUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

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
				state.getValue(Const.NORTH_LADDER_EXT_TOP),
				state.getValue(Const.SOUTH_LADDER_EXT_TOP),
				state.getValue(Const.EAST_LADDER_EXT_TOP),
				state.getValue(Const.WEST_LADDER_EXT_TOP),
				state.getValue(Const.NE_LADDER_EXT),
				state.getValue(Const.NW_LADDER_EXT),
				state.getValue(Const.SE_LADDER_EXT),
				state.getValue(Const.SW_LADDER_EXT),
				state.getValue(Const.TAPE),
				state.getValue(Const.LIGHTS),
				state.getValue(Const.SPEED)
		}); // 16,777,216 combonations, wow.
	}

	@Override
	protected List<BakedQuad> generateQuads(List<Object> list) {
		int i = 0;
		
		EnumCatwalkMaterial material = (EnumCatwalkMaterial) list.get(i++);
		EnumFacing facing = (EnumFacing) list.get(i++);
		
		boolean down          = (boolean) list.get(i++),
				north         = (boolean) list.get(i++),
				south         = (boolean) list.get(i++),
				east          = (boolean) list.get(i++),
				west          = (boolean) list.get(i++),
				north_ext     = (boolean) list.get(i++),
				south_ext     = (boolean) list.get(i++),
				east_ext      = (boolean) list.get(i++),
				west_ext      = (boolean) list.get(i++),
				north_ext_top = (boolean) list.get(i++),
				south_ext_top = (boolean) list.get(i++),
				east_ext_top  = (boolean) list.get(i++),
				west_ext_top  = (boolean) list.get(i++),
				ne_ext        = (boolean) list.get(i++),
				nw_ext        = (boolean) list.get(i++),
				se_ext        = (boolean) list.get(i++),
				sw_ext        = (boolean) list.get(i++),
				tape          = (boolean) list.get(i++),
				lights        = (boolean) list.get(i++),
				speed         = (boolean) list.get(i++);
		
        String mat = material.getName().toLowerCase();
		
		TextureAtlasSprite
			baseTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/base")),
        
			tapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/tape")),
        	lightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/lights")),
        	speedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/ladder/"+mat+"/speed"));
		
		
		List<SpritelessQuad> quads;
		quads = new ArrayList<>();
		int cond = 0;
		double p  = 1/16f, P  = 1-p;
		double ptx = 1/32f, Ptx = 1-ptx;
//		double p2 = 2/16f, P2 = 1-p2;
		
		// bottom
		ModelUtils.doubleQuad(quads, EnumFacing.DOWN, cond++,
			p, 0, p,  ptx,    ptx,
			p, 0, P,  ptx,   .5-ptx,
			P, 0, P, .5-ptx, .5-ptx,
			P, 0, p, .5-ptx,  ptx
		);
		//north ( ladder )
		ModelUtils.doubleQuad(quads, null, cond++,
			p, 0, p, .5+ptx,  1,
			p, 1, p, .5+ptx, .5,
			P, 1, p,  1-ptx, .5,
			P, 0, p,  1-ptx,  1
		);
		//south
		ModelUtils.doubleQuad(quads, null, cond++,
			p, 0, P,  ptx,    1,
			p, 1, P,  ptx,   .5,
			P, 1, P, .5-ptx, .5,
			P, 0, P, .5-ptx,  1
		);
		//east
		ModelUtils.doubleQuad(quads, null, cond++,
			P, 0, p, .5-ptx,  1,
			P, 1, p, .5-ptx, .5,
			P, 1, P,  ptx,   .5,
			P, 0, P,  ptx,    1
		);
		//west
		ModelUtils.doubleQuad(quads, null, cond++,
			p, 0, p,  ptx,    1,
			p, 1, p,  ptx,   .5,
			p, 1, P, .5-ptx, .5,
			p, 0, P, .5-ptx,  1
		);
		
		//north landing
		ModelUtils.doubleQuad(quads, EnumFacing.DOWN, cond++,
			0, 0, 0,  0,     0,
			p, 0, p,  ptx,   ptx,
			P, 0, p, .5-ptx, ptx,
			1, 0, 0, .5,     0
		);
		
		//south landing
		ModelUtils.doubleQuad(quads, EnumFacing.DOWN, cond++,
			0, 0, 1,  0,     .5,
			p, 0, P,  ptx,   .5-ptx,
			P, 0, P, .5-ptx, .5-ptx,
			1, 0, 1, .5,     .5
		);
		
		//east landing
		ModelUtils.doubleQuad(quads, EnumFacing.DOWN, cond++,
			1, 0, 0, .5,      0,
			P, 0, p, .5-ptx,  ptx,
			P, 0, P, .5-ptx, .5-ptx,
			1, 0, 1, .5,     .5
		);
		
		//west landing
		ModelUtils.doubleQuad(quads, EnumFacing.DOWN, cond++,
			0, 0, 0, 0,  0,
			p, 0, p, ptx, ptx,
			p, 0, P, ptx, .5-ptx,
			0, 0, 1, 0,  .5
		);
		
		//north top landing
		ModelUtils.doubleQuad(quads, EnumFacing.UP, cond++,
			0, 1, 0,  0,     0,
			p, 1, p,  ptx,   ptx,
			P, 1, p, .5-ptx, ptx,
			1, 1, 0, .5,     0
		);
		
		//south top landing
		ModelUtils.doubleQuad(quads, EnumFacing.UP, cond++,
			0, 1, 1,  0,     .5,
			p, 1, P,  ptx,   .5-ptx,
			P, 1, P, .5-ptx, .5-ptx,
			1, 1, 1, .5,     .5
		);
		
		//east top landing
		ModelUtils.doubleQuad(quads, EnumFacing.UP, cond++,
			1, 1, 0, .5,      0,
			P, 1, p, .5-ptx,  ptx,
			P, 1, P, .5-ptx, .5-ptx,
			1, 1, 1, .5,     .5
		);
		
		//west top landing
		ModelUtils.doubleQuad(quads, EnumFacing.UP, cond++,
			0, 1, 0, 0,  0,
			p, 1, p, ptx, ptx,
			p, 1, P, ptx, .5-ptx,
			0, 1, 1, 0,  .5
		);
		
		//north-east connection
		ModelUtils.doubleQuad(quads, null, cond++,
			1, 0, 0, .5+ptx, .5,
			P, 0, p, .5,     .5,
			P, 1, p, .5,     0,
			1, 1, 0, .5+ptx, 0
		);
		
		//north-west connection
		ModelUtils.doubleQuad(quads, null, cond++,
			0, 0, 0, .5+ptx, .5,
			p, 0, p, .5,     .5,
			p, 1, p, .5,     0,
			0, 1, 0, .5+ptx, 0
		);
		
		//south-east connection
		ModelUtils.doubleQuad(quads, null, cond++,
			1, 0, 1, .5+ptx, .5,
			P, 0, P, .5,     .5,
			P, 1, P, .5,     0,
			1, 1, 1, .5+ptx, 0
		);
		
		//south-west connection
		ModelUtils.doubleQuad(quads, null, cond++,
			0, 0, 1, .5+ptx, .5,
			p, 0, P, .5,     .5,
			p, 1, P, .5,     0,
			0, 1, 1, .5+ptx, 0
		);

		int rot = GeneralUtil.getRotation(EnumFacing.NORTH, facing);
		for (SpritelessQuad quad : quads) {
			quad.p1 = GeneralUtil.rotateVectorCenter(rot, quad.p1);
			quad.p2 = GeneralUtil.rotateVectorCenter(rot, quad.p2);
			quad.p3 = GeneralUtil.rotateVectorCenter(rot, quad.p3);
			quad.p4 = GeneralUtil.rotateVectorCenter(rot, quad.p4);
		}
		
		List<BakedQuad> finalQuads = new ArrayList<>();
    	
		boolean[] conditions = new boolean[] {
				down,
				north,
				south,
				east,
				west,
				
				north_ext,
				south_ext,
				east_ext,
				west_ext,
				
				north_ext_top,
				south_ext_top,
				east_ext_top,
				west_ext_top,
				
				ne_ext,
				nw_ext,
				se_ext,
				sw_ext
		};
		
    	ModelUtils.processConditionalQuads(quads, finalQuads, baseTex, conditions);
        if(  tape) ModelUtils.processConditionalQuads(quads, finalQuads, tapeTex,   conditions);
        if(lights) ModelUtils.processConditionalQuads(quads, finalQuads, lightsTex, conditions);
        if( speed) ModelUtils.processConditionalQuads(quads, finalQuads, speedTex,  conditions);
		
		return finalQuads;
	}

}
