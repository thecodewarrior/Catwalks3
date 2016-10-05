package catwalks.render.cached.models;

import catwalks.Const;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.EnumCatwalkMaterialOld;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StairBottomModel extends SimpleModel {

	@Override
	public List<Object> getKey(IBlockState rawstate) {
		IExtendedBlockState state = (IExtendedBlockState) rawstate;
		return Arrays.asList(new Object[] {
				state.getValue(Const.MATERIAL_OLD),
				state.getValue(Const.SOUTH),
				state.getValue(Const.WEST),
				state.getValue(Const.EAST),
				state.getValue(Const.TAPE),
				state.getValue(Const.LIGHTS),
				state.getValue(Const.SPEED),
				state.getValue(Const.FACING)	
		});
	}

	@Override
	protected List<BakedQuad> generateQuads(List<Object> list) {
		List<BakedQuad> output = new ArrayList<>();
        List<SpritelessQuad> quads = new ArrayList<>();
		ModelUtils.texSize(64, 64);
		
		int i = 0;
		EnumCatwalkMaterialOld material = (EnumCatwalkMaterialOld) list.get(i++);
		boolean south  = (boolean) list.get(i++),
				west   = (boolean) list.get(i++),
				east   = (boolean) list.get(i++),
				tape   = (boolean) list.get(i++),
				lights = (boolean) list.get(i++),
				speed  = (boolean) list.get(i++);
		EnumFacing facing = (EnumFacing) list.get(i++);
		int rot = GeneralUtil.getRotation(EnumFacing.NORTH, facing);

		String mat = material.getName().toLowerCase();
        
        TextureAtlasSprite texture = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/base"));
        
        TextureAtlasSprite tapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/tape"));
        TextureAtlasSprite lightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/lights"));
        TextureAtlasSprite speedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/speed"));
		
		double stepLength = 1/BlockCatwalkStair.STEP_COUNT;
		int p = 16;
		
		for (i = 0; i < BlockCatwalkStair.STEP_COUNT; i++) {
            
            double y = i*stepLength + stepLength/2, minZ = 1-i*stepLength, maxZ = 1-(i+1)*stepLength;
            ModelUtils.quadP(quads,
        		0, y, minZ, 43, p,
        		0, y, maxZ, 43, p-4,
        		1, y, maxZ, 59, p-4,
        		1, y, minZ, 59, p
        	).nocull().condition(-1).showBackface();
            p -= 4;
		}
		
		for (SpritelessQuad quad : quads) {
			quad.p1 = GeneralUtil.rotateVectorCenter(rot, quad.p1);
			quad.p2 = GeneralUtil.rotateVectorCenter(rot, quad.p2);
			quad.p3 = GeneralUtil.rotateVectorCenter(rot, quad.p3);
			quad.p4 = GeneralUtil.rotateVectorCenter(rot, quad.p4);
		}
		
		ModelUtils.processConditionalQuads(quads, output, texture);
        if(  tape) ModelUtils.processConditionalQuads(quads, output,   tapeTex);
        if(lights) ModelUtils.processConditionalQuads(quads, output, lightsTex);
        if( speed) ModelUtils.processConditionalQuads(quads, output,  speedTex);
		
        quads.clear();
        
		int r = GeneralUtil.getRotation(EnumFacing.NORTH, facing);
        
		ModelUtils.resetConditionCounter();
		
		// EAST
		// tweaked
		ModelUtils.quadP(quads,
    		1, 0, 1, 25, 16,
    		1, 1, 1, 25, 0,
    		1, 1, 0, 0, 16,
    		1, 1, 0, 0, 16
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.EAST)).showBackface();
		ModelUtils.sameCondition();
		// diagonal
		ModelUtils.quadP(quads,
    		1, 0, 1, 39, 31,
    		1, 1, 1, 26, 18,
    		1, 1, 0, 13, 31,
    		1, 1, 0, 13, 31
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.EAST)).showBackface();
		ModelUtils.sameCondition();
		// vertical
		ModelUtils.quadP(quads,
    		1, 0, 1, 42, 32,
    		1, 1, 1, 42, 16,
    		1, 1, 0, 26, 16,
    		1, 1, 0, 26, 16
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.EAST)).showBackface();
		
		// WEST
		
		// tweaked
		ModelUtils.quadP(quads,
    		0, 0, 1, 25, 16,
    		0, 1, 1, 25, 0,
    		0, 1, 0, 0, 16,
    		0, 1, 0, 0, 16
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.WEST)).showBackface();
		ModelUtils.sameCondition();
		// diagonal
		ModelUtils.quadP(quads,
    		0, 0, 1, 39, 31,
    		0, 1, 1, 26, 18,
    		0, 1, 0, 13, 31,
    		0, 1, 0, 13, 31
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.WEST)).showBackface();
		ModelUtils.sameCondition();
		// vertical
		ModelUtils.quadP(quads,
    		0, 0, 1, 42, 32,
    		0, 1, 1, 42, 16,
    		0, 1, 0, 26, 16,
    		0, 1, 0, 26, 16
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.WEST)).showBackface();
		
		// other stuff
		ModelUtils.quadP(quads,
    		0, 0, 1, 43, 32,
    		0, 1, 1, 43, 16,
    		1, 1, 1, 59, 16,
    		1, 0, 1, 59, 32
    	).setSide(GeneralUtil.rotateFacing(r, EnumFacing.SOUTH)).showBackface();
		
		for (SpritelessQuad quad : quads) {
			quad.p1 = GeneralUtil.rotateVectorCenter(rot, quad.p1);
			quad.p2 = GeneralUtil.rotateVectorCenter(rot, quad.p2);
			quad.p3 = GeneralUtil.rotateVectorCenter(rot, quad.p3);
			quad.p4 = GeneralUtil.rotateVectorCenter(rot, quad.p4);
		}
		
		ModelUtils.processConditionalQuads(quads, output, texture, east, west, south);
        if(  tape) ModelUtils.processConditionalQuads(quads, output,   tapeTex, east, west, south);
        if(lights) ModelUtils.processConditionalQuads(quads, output, lightsTex, east, west, south);
        if( speed) ModelUtils.processConditionalQuads(quads, output,  speedTex, east, west, south);
        
		return output;
	}

}
