package catwalks.render.catwalk;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.render.BakedModelBase;
import catwalks.render.ModelUtils;
import catwalks.render.SmartModelBase;
import catwalks.texture.CatwalkVariant;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

public class CatwalkStairSmartModel extends SmartModelBase {

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
        		state.getValue(BlockCatwalkBase.VINES),
        		state.getValue(BlockCatwalkBase.FACING)
        	);
	}

	@Override
	public IBakedModel newBakedModel() {
		return new Model();
	}

	public static class Model extends BakedModelBase {
		
		private TextureAtlasSprite texture;

        private boolean north;
        private boolean south;
        private boolean west;
        private boolean east;
        private boolean down;
		
		public Model() {
			texture = ModelUtils.getSprite( new ResourceLocation(CatwalkVariant.NULLS[0].getTextureName("stair/base")) );
		}
		
		public Model(EnumCatwalkMaterial material, boolean down, boolean north, boolean south, boolean west, boolean east, boolean tape, boolean lights, boolean vines, EnumFacing facing) {
			this();
			this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.down = down;
            
            CatwalkVariant variant = new CatwalkVariant(material, tape, lights, vines);

            texture = ModelUtils.getSprite( new ResourceLocation(variant.getTextureName("stair/base")) );
            
            genFaces();
		}
		
		List<BakedQuad> quads = new ArrayList<>();
		
		public void genFaces() {
	        if(north) {
	        	ModelUtils.putFace(quads, EnumFacing.NORTH, texture);
	        }
	        if(south) {
	        	ModelUtils.putFace(quads, EnumFacing.SOUTH, texture);
	        }
	        if(east) {
	        	ModelUtils.putFace(quads, EnumFacing.EAST, texture);
	        }
	        if(west) {
	        	ModelUtils.putFace(quads, EnumFacing.WEST, texture);
	        }
	        
	        if(down) {
	        	ModelUtils.putFace(quads, EnumFacing.DOWN, texture);
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
			return texture;
		}
		
	}
	
}
