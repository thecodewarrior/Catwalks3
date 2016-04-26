package catwalks.render.cached.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.cached.SimpleModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

public class CatwalkModel extends SimpleModel {

	@Override
	public List<Object> getKey(IBlockState rawstate) {
		IExtendedBlockState state = (IExtendedBlockState) rawstate;
		return Arrays.asList(new Object[]{
				state.getValue(Const.MATERIAL),
				state.getValue(Const.BOTTOM),
				state.getValue(Const.NORTH),
				state.getValue(Const.SOUTH),
				state.getValue(Const.EAST),
				state.getValue(Const.WEST),
				state.getValue(Const.TAPE),
				state.getValue(Const.LIGHTS),
				state.getValue(Const.SPEED)
		});
		
	}

	@Override
	protected List<BakedQuad> generateQuads(List<Object> list) {
		int i = 0;
		
		EnumCatwalkMaterial material = (EnumCatwalkMaterial) list.get(i++);
		boolean down   = (boolean) list.get(i++),
				north  = (boolean) list.get(i++),
				south  = (boolean) list.get(i++),
				east   = (boolean) list.get(i++),
				west   = (boolean) list.get(i++),
				tape   = (boolean) list.get(i++),
				lights = (boolean) list.get(i++),
				speed  = (boolean) list.get(i++);
		
        String mat = material.getName().toLowerCase();
		
		TextureAtlasSprite
			side          = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/base")),
        
			sideTapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/tape")),
        	sideLightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/lights")),
        	sideSpeedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/speed")),

        	bottom          = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/base")),

        	bottomTapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/tape")),
        	bottomLightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/lights")),
        	bottomSpeedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/speed"));
		
		List<SpritelessQuad> sideQuads;
		List<SpritelessQuad> bottomQuads;
		
		sideQuads = new ArrayList<>();
		bottomQuads = new ArrayList<>();
		ModelUtils.putFace(sideQuads, EnumFacing.NORTH, 0);
		ModelUtils.putFace(sideQuads, EnumFacing.SOUTH, 1);
		ModelUtils.putFace(sideQuads, EnumFacing.EAST,  2);
		ModelUtils.putFace(sideQuads, EnumFacing.WEST,  3);
    	ModelUtils.putFace(bottomQuads, EnumFacing.DOWN, -1);
    	
    	List<BakedQuad> quads = new ArrayList<>();
    	
    	ModelUtils.processConditionalQuads(sideQuads, quads, side,
        		north, south, east, west);
        if(  tape) ModelUtils.processConditionalQuads(sideQuads, quads, sideTapeTex,
        		north, south, east, west);
        if(lights) ModelUtils.processConditionalQuads(sideQuads, quads, sideLightsTex,
        		north, south, east, west);
        if( speed) ModelUtils.processConditionalQuads(sideQuads, quads, sideSpeedTex,
        		north, south, east, west);
        
        if(down) { // seperate because it uses a different sprite
        	ModelUtils.processConditionalQuads(bottomQuads, quads, bottom);
        	if(  tape) ModelUtils.processConditionalQuads(bottomQuads, quads,   bottomTapeTex);
	        if(lights) ModelUtils.processConditionalQuads(bottomQuads, quads, bottomLightsTex);
	        if( speed) ModelUtils.processConditionalQuads(bottomQuads, quads,  bottomSpeedTex);
        }
		return quads;
	}

}
