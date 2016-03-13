package catwalks.render.catwalk;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.block.BlockCatwalkBase.Quad;
import catwalks.render.BakedModelBase;
import catwalks.render.ModelUtils;
import catwalks.render.SmartModelBase;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.texture.CatwalkVariant;
import catwalks.util.GeneralUtil;
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
        		state.getValue(BlockCatwalkStair.WEST_TOP),
        		state.getValue(BlockCatwalkStair.EAST_TOP),
        		state.getValue(BlockCatwalkBase.TAPE),
        		state.getValue(BlockCatwalkBase.LIGHTS),
        		state.getValue(BlockCatwalkBase.SPEED),
        		state.getValue(BlockCatwalkBase.FACING)
        	);
	}

	@Override
	public IBakedModel newBakedModel() {
		return new Model();
	}

	public static class Model extends BakedModelBase {
		
		private TextureAtlasSprite texture, tapeTex, lightsTex, vinesTex;

        private boolean north;
        private boolean south;
        private boolean west;
        private boolean east;
        private boolean down;
        private boolean westtop;
        private boolean easttop;
        private boolean tape, lights, vines;
		
        private EnumFacing facing;
        
		public Model() {
            texture = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/steel/base"));
		}
		
		public Model(EnumCatwalkMaterial material, boolean down, boolean north, boolean south, boolean west, boolean east, boolean westtop, boolean easttop, boolean tape, boolean lights, boolean vines, EnumFacing facing) {
			this();
			this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.down = down;
            
            this.westtop = westtop;
            this.easttop = easttop;
            
            this.tape = tape;
            this.lights = lights;
            this.vines = vines;
            
            this.facing = facing;
            String mat = material.getName().toLowerCase();
            
            texture = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/base"));
            
            tapeTex   = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/tape"));
            lightsTex = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/lights"));
            vinesTex  = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/"+mat+"/vines"));
            
            genFaces();
		}
		
		List<BakedQuad> quads = new ArrayList<>();
		
		public void genFaces() {
			List<SpritelessQuad> rawQuads = new ArrayList<>();
			
			double stepLength = 1/BlockCatwalkStair.STEP_COUNT;
			
			for (int i = 0; i < BlockCatwalkStair.STEP_COUNT; i++) {
	            
	            double y = i*stepLength + stepLength/2, minZ = 1-i*stepLength, maxZ = 1-(i+1)*stepLength;
	            float minV = 16-(i*2), maxV = minV-2;
	            ModelUtils.putQuad(rawQuads, null,
	            		0, y, minZ, 0, minV,
	            		0, y, maxZ, 0, maxV,
	            		1, y, maxZ, 8, maxV,
	            		1, y, minZ, 8, minV
	            	);
			}
			
			if(east)
				ModelUtils.putQuad(rawQuads, null,
	        		0, 0, 1, 12.5f, 8,
	        		0, 1, 1, 12.5f, 0,
	        		0, 1, 0, 0, 8,
	        		0, 1, 0, 0, 8
	        	);
			
			if(westtop)
				ModelUtils.putQuad(rawQuads, null,
	        		0, 1, 1, 12.5f, 0,
	        		0, 2, 0, 0, 0,
	        		0, 1, 0, 0, 8,
	        		0, 1, 0, 0, 8
	        	);
			
			if(west)
				ModelUtils.putQuad(rawQuads, null,
	        		1, 0, 1, 12.5f, 8,
	        		1, 1, 1, 12.5f, 0,
	        		1, 1, 0, 0, 8,
	        		1, 1, 0, 0, 8
	        	);
			
			if(easttop)
				ModelUtils.putQuad(rawQuads, null,
	        		1, 1, 1, 12.5f, 0,
	        		1, 2, 0, 0, 0,
	        		1, 1, 0, 0, 8,
	        		1, 1, 0, 0, 8
	        	);
			
			if(south)
				ModelUtils.putQuad(rawQuads, null,
	        		0, 0, 1, 8, 16,
	        		0, 1, 1, 8, 8,
	        		1, 1, 1, 16, 8,
	        		1, 0, 1, 16, 16
	        	);
			
			if(north)
				ModelUtils.putQuad(rawQuads, null,
	        		0, 1, 0, 8, 16,
	        		0, 2, 0, 8, 8,
	        		1, 2, 0, 16, 8,
	        		1, 1, 0, 16, 16
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
	        if( vines) ModelUtils.processQuads(rawQuads, quads,  vinesTex);
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
