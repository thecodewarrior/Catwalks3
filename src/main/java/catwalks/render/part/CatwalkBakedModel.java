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
import java.util.EnumMap;
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
		
		bottom_xaxis = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_xaxis");
		bottom_zaxis = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_zaxis");
		
		bottom_xaxis_nsew = new StateHandle[16];
		bottom_zaxis_nsew = new StateHandle[16];
		
		for (boolean north : TF) {
			for (boolean south : TF) {
				for (boolean east : TF) {
					for (boolean west : TF) {
						int i = encodeSides(north, south, east, west);
						String nsew = ( north ? "N" : "n" ) + ( south ? "S" : "s" ) + ( east ? "E" : "e" ) + ( west ? "W" : "w" );
						
						bottom_xaxis_nsew[i] = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_xaxis_" + nsew);
						bottom_zaxis_nsew[i] = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_zaxis_" + nsew);
						
					}
				}
			}
		}
		
		corner_north_east = new EnumMap<>(CatwalkRenderData.EnumCatwalkCornerType.class);
		corner_north_west = new EnumMap<>(CatwalkRenderData.EnumCatwalkCornerType.class);
		corner_south_east = new EnumMap<>(CatwalkRenderData.EnumCatwalkCornerType.class);
		corner_south_west = new EnumMap<>(CatwalkRenderData.EnumCatwalkCornerType.class);
		
		for (CatwalkRenderData.EnumCatwalkCornerType corner : CatwalkRenderData.EnumCatwalkCornerType.values()) {
			corner_north_east.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_east_" + corner.name().toLowerCase()));
			corner_north_west.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_west_" + corner.name().toLowerCase()));
			corner_south_east.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_east_" + corner.name().toLowerCase()));
			corner_south_west.put(corner, StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_west_" + corner.name().toLowerCase()));
		}
		
		bottom_edge_north = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_north");
		bottom_edge_south = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_south");
		bottom_edge_east = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_east");
		bottom_edge_west = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_edge_west");
		
		bottom_corner_north_east = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_east");
		bottom_corner_north_west = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_north_west");
		bottom_corner_south_east = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_east");
		bottom_corner_south_west = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom_corner_south_west");
		
		
		sides = new HierarchyMap<>(3);
		
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			for (EnumLeftRight leftRight : EnumLeftRight.values()) {
				for (CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType end : CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.values()) {
					sides.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("side_%s_%s_%s", facing.name(), leftRight.name(), end.name()).toLowerCase()),
						facing, leftRight, end);
				}
			}
		}
	}
	
	private final StateHandle
		bottom_edge_north,
		bottom_edge_south,
		bottom_edge_east,
		bottom_edge_west,
		bottom_corner_north_east,
		bottom_corner_north_west,
		bottom_corner_south_east,
		bottom_corner_south_west;
	
	private final StateHandle
		bottom_xaxis,
		bottom_zaxis;
	
	private final StateHandle[]
		bottom_xaxis_nsew,
		bottom_zaxis_nsew;
	
	private final EnumMap<CatwalkRenderData.EnumCatwalkCornerType, StateHandle>
		corner_north_east,
		corner_north_west,
		corner_south_east,
		corner_south_west;
	
	private final HierarchyMap<StateHandle> sides; // side, left/right, endType
	
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
			
			// region sides
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				
				if (data.sides.get(facing) != null) {
					CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(facing);
					StateHandle left = sides.get(facing, EnumLeftRight.LEFT, sideData.left);
					StateHandle right = sides.get(facing, EnumLeftRight.RIGHT, sideData.right);
					
					if (sideData.left != null)
						quads.add(left);
					if (sideData.right != null)
						quads.add(right);
				}
			}
			// endregion
			
			// region corners
			if (data.corner_ne != null) {
				quads.add(corner_north_east.get(data.corner_ne));
			}
			if (data.corner_nw != null) {
				quads.add(corner_north_west.get(data.corner_nw));
			}
			if (data.corner_se != null) {
				quads.add(corner_south_east.get(data.corner_se));
			}
			if (data.corner_sw != null) {
				quads.add(corner_south_west.get(data.corner_sw));
			}
			// endregion
			
			// region bottom middle
			if (data.bottom == EnumFacing.Axis.X) {
				StateHandle handle = bottom_xaxis_nsew[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)];
				if(handle == null || handle.isMissing()) {
					quads.add(bottom_xaxis);
				} else {
					quads.add(handle);
				}
			}
			if (data.bottom == EnumFacing.Axis.Z) {
				StateHandle handle = bottom_zaxis_nsew[encodeSides(data.bottomNorth, data.bottomSouth, data.bottomEast, data.bottomWest)];
				if(handle == null || handle.isMissing()) {
					quads.add(bottom_zaxis);
				} else {
					quads.add(handle);
				}
			}
			// endregion
			
			// region bottom edges
			if(data.bottomNorth && !bottom_edge_north.isMissing()) {
				quads.add(bottom_edge_north);
			}
			if(data.bottomSouth && !bottom_edge_south.isMissing()) {
				quads.add(bottom_edge_south);
			}
			if(data.bottomEast && !bottom_edge_east.isMissing()) {
				quads.add(bottom_edge_east);
			}
			if(data.bottomWest && !bottom_edge_west.isMissing()) {
				quads.add(bottom_edge_west);
			}
			// endregion
			
			// region bottom corners
			if(data.bottomNE && !bottom_corner_north_east.isMissing()) {
				quads.add(bottom_corner_north_east);
			}
			if(data.bottomNW && !bottom_corner_north_west.isMissing()) {
				quads.add(bottom_corner_north_west);
			}
			if(data.bottomSE && !bottom_corner_south_east.isMissing()) {
				quads.add(bottom_corner_south_east);
			}
			if(data.bottomSW && !bottom_corner_south_west.isMissing()) {
				quads.add(bottom_corner_south_west);
			}
			// endregion
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
