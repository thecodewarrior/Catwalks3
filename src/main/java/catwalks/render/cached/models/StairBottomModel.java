package catwalks.render.cached.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import catwalks.Const;
import catwalks.block.BlockCatwalkStair;
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

public class StairBottomModel extends SimpleModel {

	@Override
	public List<Object> getKey(IBlockState rawstate) {
		IExtendedBlockState state = (IExtendedBlockState) rawstate;
		return Arrays.asList(new Object[] {
				state.getValue(Const.MATERIAL),
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
		
		int i = 0;
		EnumCatwalkMaterial material = (EnumCatwalkMaterial) list.get(i++);
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
		
		for (i = 0; i < BlockCatwalkStair.STEP_COUNT; i++) {
            
            double y = i*stepLength + stepLength/2, minZ = 1-i*stepLength, maxZ = 1-(i+1)*stepLength;
            float minV = 16-(i*2), maxV = minV-2;
            ModelUtils.doubleQuad(quads, null, -1,
            		0, y, minZ,  0, minV/16f,
            		0, y, maxZ,  0, maxV/16f,
            		1, y, maxZ, .5, maxV/16f,
            		1, y, minZ, .5, minV/16f
            	);
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
        
		ModelUtils.doubleQuad(quads, GeneralUtil.rotateFacing(r, EnumFacing.EAST), 0,
    		1, 0, 1, 12.5f/16f, .5,
    		1, 1, 1, 12.5f/16f, 0,
    		1, 1, 0, 0, .5,
    		1, 1, 0, 0, .5
    	);
		
		ModelUtils.doubleQuad(quads, GeneralUtil.rotateFacing(r, EnumFacing.WEST), 1,
    		0, 0, 1, 12.5f/16f, .5,
    		0, 1, 1, 12.5f/16f, 0,
    		0, 1, 0, 0, .5,
    		0, 1, 0, 0, .5
    	);
		
		ModelUtils.doubleQuad(quads, GeneralUtil.rotateFacing(r, EnumFacing.SOUTH), 2,
    		0, 0, 1, .5,  1,
    		0, 1, 1, .5, .5,
    		1, 1, 1,  1, .5,
    		1, 0, 1,  1,  1
    	);
		
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
