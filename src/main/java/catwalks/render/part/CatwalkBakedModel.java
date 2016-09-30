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
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by TheCodeWarrior
 */
public class CatwalkBakedModel implements IBakedModel
{
	private final String mat;
	private final TextureAtlasSprite particle;
	
	final ResourceLocation CATWALK_BLOCKSTATE_LOC;
	
	public CatwalkBakedModel(String mat, TextureAtlasSprite particle) {
		this.mat = mat;
		this.particle = particle;
		
		CATWALK_BLOCKSTATE_LOC = Const.location("internal/catwalk/" + mat);
		
		handle_bottom = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "bottom");
		
		handle_corner_ne = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_ne");
		handle_corner_sw = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_sw");
		handle_corner_nw = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_nw");
		handle_corner_se = StateHandle.of(CATWALK_BLOCKSTATE_LOC, "corner_se");
		
		sideModels = new HierarchyMap<>(3);
		
		for(EnumLeftRight leftRight : EnumLeftRight.values()) {
			for(CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType end : CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.values()) {
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s=%s_%s", "north", leftRight.name(), end.name()).toLowerCase()),
					EnumFacing.NORTH, leftRight, end);
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s=%s_%s", "south", leftRight.name(), end.name()).toLowerCase()),
					EnumFacing.SOUTH, leftRight, end);
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s=%s_%s", "east", leftRight.name(), end.name()).toLowerCase()),
					EnumFacing.EAST, leftRight, end);
				sideModels.put(StateHandle.of(CATWALK_BLOCKSTATE_LOC, String.format("%s=%s_%s", "west", leftRight.name(), end.name()).toLowerCase()),
					EnumFacing.WEST, leftRight, end);
			}
		}
	}
	
	private final StateHandle handle_bottom;
	
	private final StateHandle handle_corner_ne;
	private final StateHandle handle_corner_sw;
	private final StateHandle handle_corner_nw;
	private final StateHandle handle_corner_se;
	
	private final HierarchyMap<StateHandle> sideModels; // side, left/right, endType
	
	@Override
	public List<BakedQuad> getQuads(IBlockState normalState, EnumFacing side, long rand)
	{
		IExtendedBlockState state = (IExtendedBlockState)normalState;
		
		QuadManager quads = new QuadManager(normalState, side, rand);
		
		if(state != null)
		{
			CatwalkRenderData data = state.getValue(Const.CATWALK_RENDER_DATA);
			
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				
				if(data.sides.get(facing) != null) {
					CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(facing);
					StateHandle left = sideModels.get(facing, EnumLeftRight.LEFT, sideData.left);
					StateHandle right = sideModels.get(facing, EnumLeftRight.RIGHT, sideData.right);
					
					if(left != null)
						quads.add(left);
					if(right != null)
						quads.add(right);
				}
			}
			
			if(data.corner_ne) {
				quads.add(handle_corner_ne);
			}
			if(data.corner_nw) {
				quads.add(handle_corner_nw);
			}
			if(data.corner_se) {
				quads.add(handle_corner_se);
			}
			if(data.corner_sw) {
				quads.add(handle_corner_sw);
			}
			if(data.bottom) {
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
			quads.addAll(handle.get().getQuads(state, side, rand));
		}
		
		public void add(StateHandle handle) {
			quads.addAll(handle.get().getQuads(state, side, rand));
		}
		
		public List<BakedQuad> getQuads() {
			return quads;
		}
	}
	
	@Override
	public boolean isAmbientOcclusion()
	{
		return true;
	}
	
	@Override
	public boolean isGui3d()
	{
		return true;
	}
	
	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return particle;
	}
	
	@Deprecated
	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return null;
	}
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return null;
	}
	
	public static class Model implements IModel, IRetexturableModel
	{
		private final ResourceLocation particle;
		private final String mat;
		
		public Model()
		{
			this.particle = null;
			this.mat = "";
		}
		
		public Model(String particle, String mat)
		{
			this.particle = particle == null ? null : new ResourceLocation(particle);
			this.mat = mat;
		}
		
		@Override
		public Collection<ResourceLocation> getDependencies()
		{
			List<ResourceLocation> dependencies = Lists.newArrayList();
			return dependencies;
		}
		
		@Override
		public Collection<ResourceLocation> getTextures()
		{
			if (particle != null)
				return Collections.singletonList(particle);
			return Collections.emptyList();
		}
		
		@Override
		public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
		{
			TextureAtlasSprite part = null;
			if (particle != null) part = bakedTextureGetter.apply(particle);
			return new CatwalkBakedModel(mat, part);
		}
		
		@Override
		public IModelState getDefaultState()
		{
			return null;
		}
		
		@Override
		public IModel retexture(ImmutableMap<String, String> textures)
		{
			return new Model(textures.get("particle"), mat);
		}
	}
	
	public static class ModelLoader implements ICustomModelLoader
	{
		public static final String PREFIX = "models/block/internal/catwalk";
		
		@Override
		public boolean accepts(ResourceLocation modelLocation)
		{
			if(!modelLocation.getResourceDomain().equals(Const.MODID))
				return false;
			return modelLocation.getResourcePath().startsWith(PREFIX);
		}
		
		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception
		{
			return new Model(null, modelLocation.getResourcePath().substring(PREFIX.length()+1));
		}
		
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager)
		{
			// Nothing to do
		}
	}
	
	public static class Statemapper extends StateMapperBase
	{
		public static final ModelResourceLocation LOCATION = new ModelResourceLocation(Const.location("catwalkpart"), "normal");
		
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			return LOCATION;
		}
	}
}
