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

public class StairTopModel extends SimpleModel {

	@Override
	public List<Object> getKey(IBlockState rawstate) {
		IExtendedBlockState state = (IExtendedBlockState) rawstate;
		return Arrays.asList(new Object[] {
				state.getValue(Const.MATERIAL),
				state.getValue(Const.NORTH),
				state.getValue(Const.WEST_TOP),
				state.getValue(Const.EAST_TOP),
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
		boolean north   = (boolean) list.get(i++),
				westtop = (boolean) list.get(i++),
				easttop = (boolean) list.get(i++),
				tape    = (boolean) list.get(i++),
				lights  = (boolean) list.get(i++),
				speed   = (boolean) list.get(i++);
		EnumFacing facing = (EnumFacing) list.get(i++);
		
		int rot = GeneralUtil.getRotation(EnumFacing.NORTH, facing);
		
		String mat = material.getName().toLowerCase();
        
        TextureAtlasSprite texture = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/base"));
        
        TextureAtlasSprite tapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/tape"));
        TextureAtlasSprite lightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/lights"));
        TextureAtlasSprite speedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/speed"));
		
		ModelUtils.doubleQuad(quads, GeneralUtil.rotateFacing(rot, EnumFacing.WEST), 0,
    		0, 0, 1, 12.5f/16f, 0,
    		0, 1, 0, 0,         0,
    		0, 0, 0, 0,        .5,
    		0, 0, 0, 0,        .5
    	);
	
		ModelUtils.doubleQuad(quads, GeneralUtil.rotateFacing(rot, EnumFacing.EAST), 1,
    		1, 0, 1, 12.5f/16f, 0,
    		1, 1, 0, 0,         0,
    		1, 0, 0, 0,        .5,
    		1, 0, 0, 0,        .5
    	);
	
		ModelUtils.doubleQuad(quads, GeneralUtil.rotateFacing(rot, EnumFacing.NORTH), 2,
    		0, 0, 0, .5,  1,
    		0, 1, 0, .5, .5,
    		1, 1, 0,  1, .5,
    		1, 0, 0,  1,  1
    	);
		
		for (SpritelessQuad quad : quads) {
			quad.p1 = GeneralUtil.rotateVectorCenter(rot, quad.p1);
			quad.p2 = GeneralUtil.rotateVectorCenter(rot, quad.p2);
			quad.p3 = GeneralUtil.rotateVectorCenter(rot, quad.p3);
			quad.p4 = GeneralUtil.rotateVectorCenter(rot, quad.p4);
		}
		
		ModelUtils.processConditionalQuads(quads, output, texture, westtop, easttop, north);
        if(  tape) ModelUtils.processConditionalQuads(quads, output,   tapeTex, westtop, easttop, north);
        if(lights) ModelUtils.processConditionalQuads(quads, output, lightsTex, westtop, easttop, north);
        if( speed) ModelUtils.processConditionalQuads(quads, output,  speedTex, westtop, easttop, north);
		
		return output;
	}

}
