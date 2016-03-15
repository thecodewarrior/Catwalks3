package catwalks.render.catwalk;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.property.UPropertyBool;
import catwalks.render.BakedModelBase;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.SmartModelBase;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

public class CatwalkStairTopSmartModel extends SmartModelBase {

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public IBakedModel newBakedModel(IExtendedBlockState state) {
		EnumCatwalkMaterial mat;
		boolean north, westtop, easttop, tape, lights, speed;
		EnumFacing facing;
		try {
			mat     = state.getValue(BlockCatwalkBase.MATERIAL);
			north   = state.getValue(BlockCatwalkBase.NORTH);
			westtop = state.getValue(BlockCatwalkStair.WEST_TOP);
			easttop = state.getValue(BlockCatwalkStair.EAST_TOP);
			tape    = state.getValue(BlockCatwalkBase.TAPE);
			lights  = state.getValue(BlockCatwalkBase.LIGHTS);
			speed   = state.getValue(BlockCatwalkBase.SPEED);
			facing  = state.getValue(BlockCatwalkBase.FACING);
		} catch(NullPointerException e) {
			if(state == null)
				throw e;
			Logs.error(e, "Extreme edge case NPE, likely a freak race condition... *shrugs*");
			return new Model();
		}
		return new Model(mat, north, westtop, easttop, tape, lights, speed, facing);
	}

	@Override
	public IBakedModel newBakedModel() {
		return new Model();
	}

	public static class Model extends BakedModelBase {
		
		private TextureAtlasSprite texture, tapeTex, lightsTex, speedTex;

        private boolean north;
        private boolean westtop;
        private boolean easttop;
        private boolean tape, lights, speed;
		
        private EnumFacing facing;
        
		public Model() {
            texture = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/steel/base"));
		}
		
		public Model(EnumCatwalkMaterial material, boolean north, boolean westtop, boolean easttop, boolean tape, boolean lights, boolean speed, EnumFacing facing) {
			this();
			this.north = north;
            
            this.westtop = westtop;
            this.easttop = easttop;
            
            this.tape = tape;
            this.lights = lights;
            this.speed = speed;
            
            this.facing = facing;
            String mat = material.getName().toLowerCase();
            
            texture = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/base"));
            
            tapeTex   = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/tape"));
            lightsTex = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/lights"));
            speedTex  = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/speed"));
            
            genFaces();
		}
		
		List<BakedQuad> quads = new ArrayList<>();
		
		public void genFaces() {
			List<SpritelessQuad> rawQuads = new ArrayList<>();
			
			if(westtop)
				ModelUtils.putQuad(rawQuads, null,
	        		0, 0, 1, 12.5f, 0,
	        		0, 1, 0, 0, 0,
	        		0, 0, 0, 0, 8,
	        		0, 0, 0, 0, 8
	        	);
			
			if(easttop)
				ModelUtils.putQuad(rawQuads, null,
	        		1, 0, 1, 12.5f, 0,
	        		1, 1, 0, 0, 0,
	        		1, 0, 0, 0, 8,
	        		1, 0, 0, 0, 8
	        	);
			
			if(north)
				ModelUtils.putQuad(rawQuads, null,
	        		0, 0, 0, 8, 16,
	        		0, 1, 0, 8, 8,
	        		1, 1, 0, 16, 8,
	        		1, 0, 0, 16, 16
	        	);
			
			int rot = -GeneralUtil.getRotation(EnumFacing.NORTH, facing);
			Matrix4 matrix = new Matrix4().translate(new Vector3(0.5,0.5,0.5)).rotate((Math.PI/2)*rot, new Vector3(0,1,0)).translate(new Vector3(-0.5,-0.5,-0.5));
			Vector3 vec = new Vector3();
			for (SpritelessQuad quad : rawQuads) {
				matrix.apply(vec.set(quad.p1.xCoord, quad.p1.yCoord, quad.p1.zCoord));
				quad.p1 = vec.vec3();
				
				matrix.apply(vec.set(quad.p2.xCoord, quad.p2.yCoord, quad.p2.zCoord));
				quad.p2 = vec.vec3();
				
				matrix.apply(vec.set(quad.p3.xCoord, quad.p3.yCoord, quad.p3.zCoord));
				quad.p3 = vec.vec3();
				
				matrix.apply(vec.set(quad.p4.xCoord, quad.p4.yCoord, quad.p4.zCoord));
				quad.p4 = vec.vec3();
			}
			
			ModelUtils.processQuads(rawQuads, quads, texture);
	        if(  tape) ModelUtils.processQuads(rawQuads, quads,   tapeTex);
	        if(lights) ModelUtils.processQuads(rawQuads, quads, lightsTex);
	        if( speed) ModelUtils.processQuads(rawQuads, quads,  speedTex);
		}
		
		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing side) {
			List<BakedQuad> faceQuads = new ArrayList<>();
			for (BakedQuad bakedQuad : quads) {
				if(bakedQuad.getFace() == side) {
					faceQuads.add(bakedQuad);
				}
			}
			return ImmutableList.of();
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			return quads;
	    }

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return texture;
		}
		
	}
	
}
