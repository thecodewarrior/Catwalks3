package catwalks.render.part;

import catwalks.Const;
import catwalks.part.data.CatwalkRenderData;
import catwalks.render.ModelHandle;
import catwalks.render.StateHandle;
import catwalks.util.EnumLeftRight;
import catwalks.util.nestedmap.HierarchyMap;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static catwalks.CatwalksMod.SCREAM_AT_DEV;

/**
 * Created by TheCodeWarrior
 */
public class CatwalkBakedModel implements IBakedModel {
	private final String mat;
	private final TextureAtlasSprite particle;
	
	final ResourceLocation CATWALK_BLOCKSTATE_LOC;
	
	public CatwalkBakedModel(String mat, TextureAtlasSprite particle) {
		boolean[] TF = new boolean[]{true, false};
		
		this.mat = mat;
		this.particle = particle;
		
		CATWALK_BLOCKSTATE_LOC = Const.location("internal/catwalk/" + mat);
		
		handle_bottom = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom").loadNull();
		
		handles_bottom = new StateHandle[16];
		for (boolean north : TF) {
			for (boolean south : TF) {
				for (boolean east : TF) {
					for (boolean west : TF) {
						
						handles_bottom[encodeSides(north, south, east, west)] = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom=" + ( north ? "n" : "" ) + ( south ? "s" : "" ) + ( east ? "e" : "" ) + ( west ? "w" : "" )).load();
						
					}
				}
			}
		}
		
		
		handle_corner_ne = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_ne").load();
		handle_corner_sw = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_sw").load();
		handle_corner_nw = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_nw").load();
		handle_corner_se = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_se").load();
		
		handle_corner_ne_180 = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_ne_180").load();
		handle_corner_sw_180 = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_sw_180").load();
		handle_corner_nw_180 = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_nw_180").load();
		handle_corner_se_180 = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_se_180").load();
		
		sideModels = new HierarchyMap<>(3);
		
		for (EnumLeftRight leftRight : EnumLeftRight.values()) {
			for (CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType end : CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.values()) {
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s_%s=%s", "north", leftRight.name(), end.name()).toLowerCase()).load(),
					EnumFacing.NORTH, leftRight, end);
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s_%s=%s", "south", leftRight.name(), end.name()).toLowerCase()).load(),
					EnumFacing.SOUTH, leftRight, end);
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s_%s=%s", "east", leftRight.name(), end.name()).toLowerCase()).load(),
					EnumFacing.EAST, leftRight, end);
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s_%s=%s", "west", leftRight.name(), end.name()).toLowerCase()).load(),
					EnumFacing.WEST, leftRight, end);
			}
		}
	}
	
	private final StateHandle handle_bottom;
	private final StateHandle handles_bottom[];
	
	private final StateHandle handle_corner_ne;
	private final StateHandle handle_corner_ne_180;
	private final StateHandle handle_corner_sw;
	private final StateHandle handle_corner_sw_180;
	private final StateHandle handle_corner_nw;
	private final StateHandle handle_corner_nw_180;
	private final StateHandle handle_corner_se;
	private final StateHandle handle_corner_se_180;
	
	private final HierarchyMap<StateHandle> sideModels; // side, left/right, endType
	
	private static int encodeSides(boolean north, boolean south, boolean east, boolean west) {
		return ( ( ( north ? 1 : 0 ) ) |
			( ( south ? 1 : 0 ) << 1 ) |
			( ( east ? 1 : 0 ) << 2 ) |
			( ( west ? 1 : 0 ) << 3 )
		);
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState normalState, EnumFacing side, long rand) {
		IExtendedBlockState state = (IExtendedBlockState) normalState;
		
		QuadManager quads = new QuadManager(normalState, side, rand);
		
		if (state != null) {
			CatwalkRenderData data = state.getValue(Const.CATWALK_RENDER_DATA);
			
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				
				if (data.sides.get(facing) != null) {
					CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(facing);
					StateHandle left = sideModels.get(facing, EnumLeftRight.LEFT, sideData.left);
					StateHandle right = sideModels.get(facing, EnumLeftRight.RIGHT, sideData.right);
					
					if (sideData.left != null)
						quads.add(left);
					if (sideData.right != null)
						quads.add(right);
				}
			}
			
			if (data.corner_ne == CatwalkRenderData.EnumCatwalkCornerType.CORNER) {
				quads.add(handle_corner_ne);
			} else if (data.corner_ne == CatwalkRenderData.EnumCatwalkCornerType.CORNER_180) {
				quads.add(handle_corner_ne_180);
			}
			if (data.corner_nw == CatwalkRenderData.EnumCatwalkCornerType.CORNER) {
				quads.add(handle_corner_nw);
			} else if (data.corner_nw == CatwalkRenderData.EnumCatwalkCornerType.CORNER_180) {
				quads.add(handle_corner_nw_180);
			}
			if (data.corner_se == CatwalkRenderData.EnumCatwalkCornerType.CORNER) {
				quads.add(handle_corner_se);
			} else if (data.corner_se == CatwalkRenderData.EnumCatwalkCornerType.CORNER_180) {
				quads.add(handle_corner_se_180);
			}
			if (data.corner_sw == CatwalkRenderData.EnumCatwalkCornerType.CORNER) {
				quads.add(handle_corner_sw);
			} else if (data.corner_sw == CatwalkRenderData.EnumCatwalkCornerType.CORNER_180) {
				quads.add(handle_corner_sw_180);
			}
			if (data.bottom) {
				if(handle_bottom.getNull() == null)
					quads.add(handles_bottom[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)]);
				else
					quads.add(handle_bottom);
			}
		}
		
		return quads.getQuads();
	}
	
	private static class QuadManager {
		List<BakedQuad> quads = Lists.newArrayList();
		IBlockState state;
		EnumFacing side;
		long rand;
		
		public QuadManager(IBlockState state, EnumFacing side, long rand) {
			this.state = state;
			this.side = side;
			this.rand = rand;
		}
		
		public void add(ModelHandle handle) {
			if (handle == null)
				SCREAM_AT_DEV();
			add(handle.get());
		}
		
		public void add(StateHandle handle) {
			if (handle == null)
				SCREAM_AT_DEV();
			add(handle.get());
		}
		
		public void add(IBakedModel model) {
			if (model == null)
				SCREAM_AT_DEV();
			quads.addAll(model.getQuads(state, side, rand));
		}
		
		public List<BakedQuad> getQuads() {
			return quads;
		}
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
	public TextureAtlasSprite getParticleTexture() {
		return particle;
	}
	
	@Deprecated
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return null;
	}
	
	@Override
	public ItemOverrideList getOverrides() {
		return null;
	}
	
	public static class Model implements IModel, IRetexturableModel {
		private final ResourceLocation particle;
		private final String mat;
		
		public Model() {
			this.particle = null;
			this.mat = "";
		}
		
		public Model(String particle, String mat) {
			this.particle = particle == null ? null : new ResourceLocation(particle);
			this.mat = mat;
		}
		
		@Override
		public Collection<ResourceLocation> getDependencies() {
			List<ResourceLocation> dependencies = Lists.newArrayList();
			return dependencies;
		}
		
		@Override
		public Collection<ResourceLocation> getTextures() {
			if (particle != null)
				return Collections.singletonList(particle);
			return Collections.emptyList();
		}
		
		@Override
		public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			TextureAtlasSprite part = null;
			if (particle != null) part = bakedTextureGetter.apply(particle);
			return new CatwalkBakedModel(mat, part);
		}
		
		@Override
		public IModelState getDefaultState() {
			return null;
		}
		
		@Override
		public IModel retexture(ImmutableMap<String, String> textures) {
			return new Model(textures.get("particle"), mat);
		}
	}
	
	public static class ModelLoader implements ICustomModelLoader {
		public static final String PREFIX = "models/block/internal/catwalk";
		
		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			if (!modelLocation.getResourceDomain().equals(Const.MODID))
				return false;
			return modelLocation.getResourcePath().startsWith(PREFIX);
		}
		
		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception {
			return new Model(null, modelLocation.getResourcePath().substring(PREFIX.length() + 1));
		}
		
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			// Nothing to do
		}
	}
}
