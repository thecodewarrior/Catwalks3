package catwalks.render.catwalk;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.render.BakedModelBase;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessQuad;
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
		return new Model(
				state.getValue(BlockCatwalkBase.MATERIAL),
        		state.getValue(BlockCatwalkBase.BOTTOM),
        		state.getValue(BlockCatwalkBase.NORTH),
        		state.getValue(BlockCatwalkBase.SOUTH),
        		state.getValue(BlockCatwalkBase.EAST),
        		state.getValue(BlockCatwalkBase.WEST),
        		state.getValue(BlockCatwalkBase.TAPE),
        		state.getValue(BlockCatwalkBase.LIGHTS),
        		state.getValue(BlockCatwalkBase.VINES)
        	);
	}

	@Override
	public IBakedModel newBakedModel() {
		return new Model();
	}

	public static class Model extends BakedModelBase {
		
		private TextureAtlasSprite side, tapeTex, lightsTex, vinesTex, bottom;

        private boolean north, south, west, east, down;
        private boolean tape, lights, vines;
		
		public Model() {
			side = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/steel/side/base"));
            bottom = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/steel/bottom/base"));
		}
		
		public Model(EnumCatwalkMaterial material, boolean down, boolean north, boolean south, boolean west, boolean east, boolean tape, boolean lights, boolean vines) {
			this();
			this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.down = down;
            this.tape = tape;
            this.lights = lights;
            this.vines = vines;
            
            String mat = material.getName().toLowerCase();
            
            side = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/base"));
            bottom = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/bottom/base"));
            
            tapeTex   = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/decorations/tape"));
            lightsTex = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/decorations/lights"));
            vinesTex  = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/"+mat+"/side/decorations/vines"));
                        
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
	        if(  tape) ModelUtils.processQuads(rawQuads, quads,   tapeTex);
	        if(lights) ModelUtils.processQuads(rawQuads, quads, lightsTex);
	        if( vines) ModelUtils.processQuads(rawQuads, quads,  vinesTex);
	        
	        if(down) {
	        	rawQuads.clear();
	        	ModelUtils.putFace(rawQuads, EnumFacing.DOWN);
	        	ModelUtils.processQuads(rawQuads, quads, bottom);
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
