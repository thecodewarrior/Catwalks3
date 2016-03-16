package catwalks.render.catwalk;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.render.BakedModelBase;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.catwalk.CatwalkStairSmartModel.Model;
import catwalks.util.Logs;
import catwalks.render.SmartModelBase;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

public class CatwalkSmartModel extends SmartModelBase {

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public IBakedModel newBakedModel(IExtendedBlockState state) {
		EnumCatwalkMaterial mat;
		boolean bottom, north, south, east, west, tape, lights, speed;
		try {
			mat     = state.getValue(BlockCatwalkBase.MATERIAL);
			bottom  = state.getValue(BlockCatwalkBase.BOTTOM);
			north   = state.getValue(BlockCatwalkBase.NORTH);
			south   = state.getValue(BlockCatwalkBase.SOUTH);
			east    = state.getValue(BlockCatwalkBase.EAST);
			west    = state.getValue(BlockCatwalkBase.WEST);
			tape    = state.getValue(BlockCatwalkBase.TAPE);
			lights  = state.getValue(BlockCatwalkBase.LIGHTS);
			speed   = state.getValue(BlockCatwalkBase.SPEED);
		} catch(NullPointerException e) {
			if(state == null)
				throw e;
			Logs.error(e, "Extreme edge case NPE, likely a freak race condition... *shrugs*");
			return new Model();
		}
		
		return new Model(mat, bottom, north, south, east, west, tape, lights, speed);	
	}

	@Override
	public IBakedModel newBakedModel() {
		return new Model();
	}

	public static class Model extends BakedModelBase {
		
		private TextureAtlasSprite side, sideTapeTex, sideLightsTex, sideSpeedTex;
		private TextureAtlasSprite bottom, bottomTapeTex, bottomLightsTex, bottomSpeedTex;

        private boolean north, south, west, east, down;
        private boolean tape, lights, speed;
		
		public Model() {
			this(EnumCatwalkMaterial.STEEL, false, false, false, false, false, false, false, false);
		}
		
		public Model(EnumCatwalkMaterial material, boolean down, boolean north, boolean south, boolean west, boolean east, boolean tape, boolean lights, boolean speed) {
			this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.down = down;
            this.tape = tape;
            this.lights = lights;
            this.speed = speed;
            
            String mat = material.getName().toLowerCase();
            
            side          = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/base"));
            
            sideTapeTex   = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/tape"));
            sideLightsTex = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/lights"));
            sideSpeedTex  = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/speed"));

            bottom          = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/bottom/base"));

            bottomTapeTex   = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/bottom/tape"));
            bottomLightsTex = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/bottom/lights"));
            bottomSpeedTex  = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/bottom/speed"));
            
            genFaces();
		}
		
		List<BakedQuad> quads = new ArrayList<>();
		
		public void genFaces() {
			List<SpritelessQuad> rawQuads = new ArrayList<>();
	        if(north) {
	        	ModelUtils.putFace(rawQuads, EnumFacing.NORTH);
	        }
	        if(south) {
	        	ModelUtils.putFace(rawQuads, EnumFacing.SOUTH);
	        }
	        if(east) {
	        	ModelUtils.putFace(rawQuads, EnumFacing.EAST);
	        }
	        if(west) {
	        	ModelUtils.putFace(rawQuads, EnumFacing.WEST);
	        }
	        
	        ModelUtils.processQuads(rawQuads, quads, side);
	        if(  tape) ModelUtils.processQuads(rawQuads, quads,   sideTapeTex);
	        if(lights) ModelUtils.processQuads(rawQuads, quads, sideLightsTex);
	        if( speed) ModelUtils.processQuads(rawQuads, quads,  sideSpeedTex);
	        
	        if(down) { // seperate because it uses a different sprite
	        	rawQuads.clear();
	        	ModelUtils.putFace(rawQuads, EnumFacing.DOWN);
	        	ModelUtils.processQuads(rawQuads, quads, bottom);
	        	if(  tape) ModelUtils.processQuads(rawQuads, quads,   bottomTapeTex);
		        if(lights) ModelUtils.processQuads(rawQuads, quads, bottomLightsTex);
		        if( speed) ModelUtils.processQuads(rawQuads, quads,  bottomSpeedTex);
	        }
		}
		
		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing side) {
			List<BakedQuad> faceQuads = new ArrayList<>();
			for (BakedQuad bakedQuad : quads) {
				if(bakedQuad.getFace() == side) {
					faceQuads.add(bakedQuad);
				}
			}
			return faceQuads;
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			return ImmutableList.of();
	    }

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return bottom;
		}
		
	}
	
}
