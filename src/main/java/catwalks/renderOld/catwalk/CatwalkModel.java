package catwalks.renderOld.catwalk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.IExtendedBlockState;

public class CatwalkModel implements IModel {
	
	public static final ResourceLocation loc = new ResourceLocation(CatwalksMod.MODID + ":catwalk");
	
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Lists.newArrayList(
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/bottom/base")
			);
	}

	@Override
	public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		
		return null;
	}
	
	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
	public static class Model implements IFlexibleBakedModel, ISmartBlockModel {

		private TextureAtlasSprite side, bottom;

        private boolean north;
        private boolean south;
        private boolean west;
        private boolean east;
        private boolean down;

        public Model() {}
        
        public Model(boolean down, boolean north, boolean south, boolean west, boolean east) {
        	TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
            side = map.getAtlasSprite("minecraft:blocks/diamond_block");
            bottom = map.getAtlasSprite(CatwalksMod.MODID + ":blocks/catwalk/bottom/base");
            this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.down = down;
        }
        
        @Override
		public IBakedModel handleBlockState(IBlockState rawState) {
        	IExtendedBlockState state = (IExtendedBlockState) rawState;
        	
            return new Model(
            		state.getValue(BlockCatwalk.BOTTOM),
            		state.getValue(BlockCatwalk.NORTH),
            		state.getValue(BlockCatwalk.SOUTH),
            		state.getValue(BlockCatwalk.EAST),
            		state.getValue(BlockCatwalk.WEST)
            	);
		}
		
        
        private int[] vertexToInts(double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
            return new int[] {
                    Float.floatToRawIntBits((float) x),
                    Float.floatToRawIntBits((float) y),
                    Float.floatToRawIntBits((float) z),
                    -1,
                    Float.floatToRawIntBits(sprite.getInterpolatedU(u)),
                    Float.floatToRawIntBits(sprite.getInterpolatedV(v)),
                    0
            };
        }

    	private void twoFace(List<BakedQuad> quads, TextureAtlasSprite sprite, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
    		quads.add(quad( sprite, v1, v2, v3, v4 ));
    		quads.add(quad( sprite, v4, v3, v2, v1 ));
    	}
        
        private BakedQuad quad(TextureAtlasSprite sprite, Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
            EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);

            return new BakedQuad(Ints.concat(
                    vertexToInts(v1.xCoord, v1.yCoord, v1.zCoord, 0,  0 , sprite),
                    vertexToInts(v2.xCoord, v2.yCoord, v2.zCoord, 0,  16, sprite),
                    vertexToInts(v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, sprite),
                    vertexToInts(v4.xCoord, v4.yCoord, v4.zCoord, 16, 0 , sprite)
            ), -1, side);
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side) {
            return Collections.emptyList();
        }

        @Override
        public List<BakedQuad> getGeneralQuads() {
            List<BakedQuad> quads = new ArrayList<>();
            
            if(north) {
            	putFace(quads, EnumFacing.NORTH, side);
            }
            if(south) {
            	putFace(quads, EnumFacing.SOUTH, side);
            }
            if(east) {
            	putFace(quads, EnumFacing.EAST, side);
            }
            if(west) {
            	putFace(quads, EnumFacing.WEST, side);
            }
            
            if(down) {
            	putFace(quads, EnumFacing.DOWN, bottom);
            }
            return quads;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }

		@Override
		public TextureAtlasSprite getTexture() {
			return null;
		}
		
		private void putFace(List<BakedQuad> quads, EnumFacing facing, TextureAtlasSprite sprite) {
        	
        	switch(facing) {
        	case DOWN:
        		twoFace(quads, sprite,
            		new Vec3(0, 0, 0),
            		new Vec3(1, 0, 0),
            		new Vec3(1, 0, 1),
            		new Vec3(0, 0, 1)
            	);
        		break;
        	case UP:
        		twoFace(quads, sprite,
            		new Vec3(0, 1, 0),
            		new Vec3(1, 1, 0),
            		new Vec3(1, 1, 1),
            		new Vec3(0, 1, 1)
            	);
        		break;
        	case NORTH:
        		twoFace(quads, sprite,
            		new Vec3(0, 0, 0),
            		new Vec3(1, 0, 0),
            		new Vec3(1, 1, 0),
            		new Vec3(0, 1, 0)
            	);
        		break;
        	case SOUTH:
        		twoFace(quads, sprite,
            		new Vec3(0, 0, 1),
            		new Vec3(1, 0, 1),
            		new Vec3(1, 1, 1),
            		new Vec3(0, 1, 1)
            	);
        		break;
        	case EAST:
        		twoFace(quads, sprite,
            		new Vec3(0, 0, 0),
            		new Vec3(0, 1, 0),
            		new Vec3(0, 1, 1),
            		new Vec3(0, 0, 1)
            	);
        		break;
        	case WEST:
        		twoFace(quads, sprite,
            		new Vec3(1, 0, 0),
            		new Vec3(1, 1, 0),
            		new Vec3(1, 1, 1),
            		new Vec3(1, 0, 1)
            	);
        		break;
        	}
        	
        }
		
		@Override
		public VertexFormat getFormat() {
			return DefaultVertexFormats.BLOCK;
		}
	}
}
