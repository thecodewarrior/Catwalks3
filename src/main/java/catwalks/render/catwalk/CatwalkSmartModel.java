package catwalks.render.catwalk;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.render.BakedModelBase;
import catwalks.render.ModelUtils;
import catwalks.render.ModelUtils.SpritelessConditionalQuad;
import catwalks.render.ModelUtils.SpritelessQuad;
import catwalks.render.SmartModelBase;
import catwalks.util.Logs;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.property.IExtendedBlockState;

public class CatwalkSmartModel extends SmartModelBase {

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public IBakedModel newBakedModel(IExtendedBlockState state) {
		if(!Model.initialized)
			Model.initQuads();
		
		EnumCatwalkMaterial mat;
		boolean bottom, north, south, east, west, tape, lights, speed;
		try {
			mat     = state.getValue(Const.MATERIAL);
			bottom  = state.getValue(Const.BOTTOM);
			north   = state.getValue(Const.NORTH);
			south   = state.getValue(Const.SOUTH);
			east    = state.getValue(Const.EAST);
			west    = state.getValue(Const.WEST);
			tape    = state.getValue(Const.TAPE);
			lights  = state.getValue(Const.LIGHTS);
			speed   = state.getValue(Const.SPEED);
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
            
            side          = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/base"));
            
            sideTapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/tape"));
            sideLightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/lights"));
            sideSpeedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/side/speed"));

            bottom          = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/base"));

            bottomTapeTex   = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/tape"));
            bottomLightsTex = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/lights"));
            bottomSpeedTex  = ModelUtils.getSprite( new ResourceLocation(Const.MODID + ":blocks/catwalk/"+mat+"/bottom/speed"));
            
            genFaces();
		}
		
		public static long avgTime = 0;
		public static int samples = 0;
		
		static List<SpritelessConditionalQuad> sideQuads;
		static List<SpritelessQuad> bottomQuads;
		public static boolean initialized = false;
		public static void initQuads() {
			sideQuads = new ArrayList<>();
			bottomQuads = new ArrayList<>();
			ModelUtils.putFace(sideQuads, EnumFacing.NORTH, 0);
			ModelUtils.putFace(sideQuads, EnumFacing.SOUTH, 1);
			ModelUtils.putFace(sideQuads, EnumFacing.EAST,  2);
			ModelUtils.putFace(sideQuads, EnumFacing.WEST,  3);
//			ModelUtils.twoFace(sideQuads, 3,
//    			new Vec3(1, 1, 0.1),
//            	new Vec3(1, 0, 0.1),
//        		new Vec3(0, 0, 0.1),
//    			new Vec3(0, 1, 0.1)
//        	);
        	ModelUtils.putFace(bottomQuads, EnumFacing.DOWN);
        	initialized = true;
		}
		
		List<BakedQuad> quads = new ArrayList<>();
		
		public void genFaces() {
	        ModelUtils.processConditionalQuads(sideQuads, quads, side,
	        		north, south, east, west);
	        if(  tape) ModelUtils.processConditionalQuads(sideQuads, quads, sideTapeTex,
	        		north, south, east, west);
	        if(lights) ModelUtils.processConditionalQuads(sideQuads, quads, sideLightsTex,
	        		north, south, east, west);
	        if( speed) ModelUtils.processConditionalQuads(sideQuads, quads, sideSpeedTex,
	        		north, south, east, west);
	        
	        if(down) { // seperate because it uses a different sprite
	        	ModelUtils.processQuads(bottomQuads, quads, bottom);
	        	if(  tape) ModelUtils.processQuads(bottomQuads, quads,   bottomTapeTex);
		        if(lights) ModelUtils.processQuads(bottomQuads, quads, bottomLightsTex);
		        if( speed) ModelUtils.processQuads(bottomQuads, quads,  bottomSpeedTex);
	        }
		}
		
		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing side) {
			long start = System.nanoTime();
			List<BakedQuad> faceQuads = new ArrayList<>();
			for (BakedQuad bakedQuad : quads) {
				if(bakedQuad.getFace() == side) {
					faceQuads.add(bakedQuad);
					faceQuads.add(bakedQuad);
				}
			}
			long time = System.nanoTime() - start;
			avgTime = avgTime + time;
			samples++;
			return ImmutableList.of(); //; //
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
