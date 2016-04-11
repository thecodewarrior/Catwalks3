package catwalks.render.cached.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.cached.SimpleModel;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.GeneralUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

public class StairTopModel extends SimpleModel {

	@SuppressWarnings("unchecked")
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
		
		int r = GeneralUtil.getRotation(EnumFacing.NORTH, facing);
		
		String mat = material.getName().toLowerCase();
        
        TextureAtlasSprite texture = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/base"));
        
        TextureAtlasSprite tapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/tape"));
        TextureAtlasSprite lightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/lights"));
        TextureAtlasSprite speedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/stair/"+mat+"/speed"));
		
        if(westtop)
			ModelUtils.putQuad(quads, GeneralUtil.rotateFacing(r, EnumFacing.WEST),
        		0, 0, 1, 12.5f, 0,
        		0, 1, 0, 0, 0,
        		0, 0, 0, 0, 8,
        		0, 0, 0, 0, 8
        	);
		
		if(easttop)
			ModelUtils.putQuad(quads, GeneralUtil.rotateFacing(r, EnumFacing.EAST),
        		1, 0, 1, 12.5f, 0,
        		1, 1, 0, 0, 0,
        		1, 0, 0, 0, 8,
        		1, 0, 0, 0, 8
        	);
		
		if(north)
			ModelUtils.putQuad(quads, GeneralUtil.rotateFacing(r, EnumFacing.NORTH),
        		0, 0, 0, 8, 16,
        		0, 1, 0, 8, 8,
        		1, 1, 0, 16, 8,
        		1, 0, 0, 16, 16
        	);
        
		Matrix4 matrix = new Matrix4().translate(new Vector3(0.5,0.5,0.5)).rotate((Math.PI/2)*-r, new Vector3(0,1,0)).translate(new Vector3(-0.5,-0.5,-0.5));
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
		
		ModelUtils.processQuads(quads, output, texture);
        if(  tape) ModelUtils.processQuads(quads, output,   tapeTex);
        if(lights) ModelUtils.processQuads(quads, output, lightsTex);
        if( speed) ModelUtils.processQuads(quads, output,  speedTex);
		
		return output;
	}

}
