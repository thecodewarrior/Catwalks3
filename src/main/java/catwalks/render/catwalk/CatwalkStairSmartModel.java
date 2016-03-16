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

public class CatwalkStairSmartModel extends SmartModelBase {

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public IBakedModel newBakedModel(IExtendedBlockState state) {
		EnumCatwalkMaterial mat;
		boolean bottom, north, south, east, west, westtop, easttop, tape, lights, speed;
		EnumFacing facing;
		try {
			mat     = state.getValue(BlockCatwalkBase.MATERIAL);
			bottom  = state.getValue(BlockCatwalkBase.BOTTOM);
			north   = state.getValue(BlockCatwalkBase.NORTH);
			south   = state.getValue(BlockCatwalkBase.SOUTH);
			east    = state.getValue(BlockCatwalkBase.WEST);
			west    = state.getValue(BlockCatwalkBase.EAST);
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
		return new Model(mat, bottom, north, south, east, west, westtop, easttop, tape, lights, speed, facing);
	}

	@Override
	public IBakedModel newBakedModel() {
		return new Model();
	}

	public static class Model extends BakedModelBase {
		
		private TextureAtlasSprite texture, tapeTex, lightsTex, speedTex;

        private boolean north;
        private boolean south;
        private boolean west;
        private boolean east;
        private boolean down;
//        private boolean westtop;
//        private boolean easttop;
        private boolean tape, lights, speed;
		
        private EnumFacing facing;
        
		public Model() {
            texture = ModelUtils.getSprite( new ResourceLocation(CatwalksMod.MODID + ":blocks/stair/steel/base"));
		}
		
		public Model(EnumCatwalkMaterial material, boolean down, boolean north, boolean south, boolean west, boolean east, boolean westtop, boolean easttop, boolean tape, boolean lights, boolean speed, EnumFacing facing) {
			this();
			this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.down = down;
            
//            this.westtop = westtop;
//            this.easttop = easttop;
            
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
		List<BakedQuad> nonFaceQuads = new ArrayList<>();
		
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
			
			ModelUtils.processQuads(rawQuads, nonFaceQuads, texture);
	        if(  tape) ModelUtils.processQuads(rawQuads, nonFaceQuads,   tapeTex);
	        if(lights) ModelUtils.processQuads(rawQuads, nonFaceQuads, lightsTex);
	        if( speed) ModelUtils.processQuads(rawQuads, nonFaceQuads,  speedTex);
			
	        rawQuads.clear();
	        
			int r = GeneralUtil.getRotation(EnumFacing.NORTH, facing);
	        
			if(east)
				ModelUtils.putQuad(rawQuads, GeneralUtil.rotateFacing(r, EnumFacing.EAST), // for some reason these have to be swapped
	        		1, 0, 1, 12.5f, 8,
	        		1, 1, 1, 12.5f, 0,
	        		1, 1, 0, 0, 8,
	        		1, 1, 0, 0, 8
	        	);
			
			if(west)
				ModelUtils.putQuad(rawQuads, GeneralUtil.rotateFacing(r, EnumFacing.WEST), // for some reason these have to be swapped
	        		0, 0, 1, 12.5f, 8,
	        		0, 1, 1, 12.5f, 0,
	        		0, 1, 0, 0, 8,
	        		0, 1, 0, 0, 8
	        	);
			
			if(south)
				ModelUtils.putQuad(rawQuads, GeneralUtil.rotateFacing(r, EnumFacing.SOUTH),
	        		0, 0, 1, 8, 16,
	        		0, 1, 1, 8, 8,
	        		1, 1, 1, 16, 8,
	        		1, 0, 1, 16, 16
	        	);
			
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
			int rot = GeneralUtil.getRotation(EnumFacing.NORTH, facing);
			List<BakedQuad> faceQuads = new ArrayList<>();
			for (BakedQuad bakedQuad : quads) {
				if(GeneralUtil.rotateFacing(rot, bakedQuad.getFace()) == side) {
					faceQuads.add(bakedQuad);
				}
			}
			return faceQuads;
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			return nonFaceQuads;
	    }

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return texture;
		}
		
	}
	
}
