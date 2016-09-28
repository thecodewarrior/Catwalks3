package catwalks.render.part;

import catwalks.Const;
import catwalks.part.data.CatwalkRenderData;
import catwalks.render.ModelHandle;
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

import static com.sun.tools.internal.xjc.reader.Ring.add;

/**
 * Created by TheCodeWarrior
 */
public class CatwalkBakedModel implements IBakedModel
{
	public static final ResourceLocation FAKE_LOCATION = Const.location("models/block/internal/catwalk");
	
	public static final ResourceLocation CATWALK_SIDE_LEFT_MERGE = Const.location("block/catwalk/custom1/left_merge");
	public static final ResourceLocation CATWALK_SIDE_LEFT_END = Const.location("block/catwalk/custom1/left_end");
	public static final ResourceLocation CATWALK_SIDE_LEFT_CORNER = Const.location("block/catwalk/custom1/left_corner");
	public static final ResourceLocation CATWALK_SIDE_LEFT_CONNECT = Const.location("block/catwalk/custom1/left_connect");
	
	public static final ResourceLocation CATWALK_SIDE_RIGHT_MERGE = Const.location("block/catwalk/custom1/right_merge");
	public static final ResourceLocation CATWALK_SIDE_RIGHT_END = Const.location("block/catwalk/custom1/right_end");
	public static final ResourceLocation CATWALK_SIDE_RIGHT_CORNER = Const.location("block/catwalk/custom1/right_corner");
	public static final ResourceLocation CATWALK_SIDE_RIGHT_CONNECT = Const.location("block/catwalk/custom1/right_connect");
	
	public static final ResourceLocation CATWALK_CORNER = Const.location("block/catwalk/custom1/corner");
	public static final ResourceLocation CATWALK_BOTTOM = Const.location("block/catwalk/custom1/bottom");
	
	private final ModelHandle handle_corner_ne = ModelHandle.of(CATWALK_CORNER);
	private final ModelHandle handle_corner_sw = ModelHandle.of(CATWALK_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_corner_nw = ModelHandle.of(CATWALK_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_corner_se = ModelHandle.of(CATWALK_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_left_merge_n = ModelHandle.of(CATWALK_SIDE_LEFT_MERGE);
	private final ModelHandle handle_side_left_merge_s = ModelHandle.of(CATWALK_SIDE_LEFT_MERGE).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_left_merge_w = ModelHandle.of(CATWALK_SIDE_LEFT_MERGE).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_left_merge_e = ModelHandle.of(CATWALK_SIDE_LEFT_MERGE).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_left_end_n = ModelHandle.of(CATWALK_SIDE_LEFT_END);
	private final ModelHandle handle_side_left_end_s = ModelHandle.of(CATWALK_SIDE_LEFT_END).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_left_end_w = ModelHandle.of(CATWALK_SIDE_LEFT_END).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_left_end_e = ModelHandle.of(CATWALK_SIDE_LEFT_END).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_left_corner_n = ModelHandle.of(CATWALK_SIDE_LEFT_CORNER);
	private final ModelHandle handle_side_left_corner_s = ModelHandle.of(CATWALK_SIDE_LEFT_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_left_corner_w = ModelHandle.of(CATWALK_SIDE_LEFT_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_left_corner_e = ModelHandle.of(CATWALK_SIDE_LEFT_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_left_connect_n = ModelHandle.of(CATWALK_SIDE_LEFT_CONNECT);
	private final ModelHandle handle_side_left_connect_s = ModelHandle.of(CATWALK_SIDE_LEFT_CONNECT).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_left_connect_w = ModelHandle.of(CATWALK_SIDE_LEFT_CONNECT).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_left_connect_e = ModelHandle.of(CATWALK_SIDE_LEFT_CONNECT).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_right_connected_n = ModelHandle.of(CATWALK_SIDE_RIGHT_MERGE);
	private final ModelHandle handle_side_right_connected_s = ModelHandle.of(CATWALK_SIDE_RIGHT_MERGE).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_right_connected_w = ModelHandle.of(CATWALK_SIDE_RIGHT_MERGE).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_right_connected_e = ModelHandle.of(CATWALK_SIDE_RIGHT_MERGE).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_right_end_n = ModelHandle.of(CATWALK_SIDE_RIGHT_END);
	private final ModelHandle handle_side_right_end_s = ModelHandle.of(CATWALK_SIDE_RIGHT_END).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_right_end_w = ModelHandle.of(CATWALK_SIDE_RIGHT_END).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_right_end_e = ModelHandle.of(CATWALK_SIDE_RIGHT_END).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_right_corner_n = ModelHandle.of(CATWALK_SIDE_RIGHT_CORNER);
	private final ModelHandle handle_side_right_corner_s = ModelHandle.of(CATWALK_SIDE_RIGHT_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_right_corner_w = ModelHandle.of(CATWALK_SIDE_RIGHT_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_right_corner_e = ModelHandle.of(CATWALK_SIDE_RIGHT_CORNER).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final ModelHandle handle_side_right_connect_n = ModelHandle.of(CATWALK_SIDE_RIGHT_CONNECT);
	private final ModelHandle handle_side_right_connect_s = ModelHandle.of(CATWALK_SIDE_RIGHT_CONNECT).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
	private final ModelHandle handle_side_right_connect_w = ModelHandle.of(CATWALK_SIDE_RIGHT_CONNECT).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
	private final ModelHandle handle_side_right_connect_e = ModelHandle.of(CATWALK_SIDE_RIGHT_CONNECT).state(new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
	
	private final TextureAtlasSprite particle;
	
	public CatwalkBakedModel(TextureAtlasSprite particle)
	{
		this.particle = particle;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState normalState, EnumFacing side, long rand)
	{
		IExtendedBlockState state = (IExtendedBlockState)normalState;
		
		QuadManager quads = new QuadManager(normalState, side, rand);
		
		if(state != null)
		{
			CatwalkRenderData data = state.getValue(Const.CATWALK_RENDER_DATA);
			
			if(data.sides.get(EnumFacing.NORTH) != null) {
				CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(EnumFacing.NORTH);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_left_end_n);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_left_merge_n);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_left_corner_n);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_left_connect_n);
				
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_right_end_n);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_right_connected_n);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_right_corner_n);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_right_connect_n);
			}
			
			if(data.sides.get(EnumFacing.SOUTH) != null) {
				CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(EnumFacing.SOUTH);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_left_end_s);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_left_merge_s);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_left_corner_s);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_left_connect_s);
				
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_right_end_s);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_right_connected_s);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_right_corner_s);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_right_connect_s);
			}
			
			if(data.sides.get(EnumFacing.EAST) != null) {
				CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(EnumFacing.EAST);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_left_end_e);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_left_merge_e);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_left_corner_e);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_left_connect_e);
				
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_right_end_e);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_right_connected_e);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_right_corner_e);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_right_connect_e);
			}
			
			if(data.sides.get(EnumFacing.WEST) != null) {
				CatwalkRenderData.CatwalkSideRenderData sideData = data.sides.get(EnumFacing.WEST);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_left_end_w);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_left_merge_w);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_left_corner_w);
				if(sideData.left == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_left_connect_w);
				
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END)
					quads.add(handle_side_right_end_w);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE)
					quads.add(handle_side_right_connected_w);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CORNER)
					quads.add(handle_side_right_corner_w);
				if(sideData.right == CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.CONNECT)
					quads.add(handle_side_right_connect_w);
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
		
		public Model()
		{
			this.particle = null;
		}
		
		public Model(String particle)
		{
			this.particle = particle == null ? null : new ResourceLocation(particle);
		}
		
		@Override
		public Collection<ResourceLocation> getDependencies()
		{
			List<ResourceLocation> dependencies = Lists.newArrayList();
			dependencies.add(CATWALK_BOTTOM);
			dependencies.add(CATWALK_SIDE_LEFT_END);
			dependencies.add(CATWALK_SIDE_LEFT_MERGE);
			dependencies.add(CATWALK_SIDE_LEFT_CORNER);
			dependencies.add(CATWALK_SIDE_LEFT_CONNECT);
			dependencies.add(CATWALK_SIDE_RIGHT_END);
			dependencies.add(CATWALK_SIDE_RIGHT_MERGE);
			dependencies.add(CATWALK_SIDE_RIGHT_CORNER);
			dependencies.add(CATWALK_SIDE_RIGHT_CONNECT);
			dependencies.add(CATWALK_CORNER);
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
			return new CatwalkBakedModel(part);
		}
		
		@Override
		public IModelState getDefaultState()
		{
			return null;
		}
		
		@Override
		public IModel retexture(ImmutableMap<String, String> textures)
		{
			return new Model(textures.get("particle"));
		}
	}
	
	public static class ModelLoader implements ICustomModelLoader
	{
		
		@Override
		public boolean accepts(ResourceLocation modelLocation)
		{
			if(!modelLocation.getResourceDomain().equals(Const.MODID))
				return false;
			return modelLocation.equals(FAKE_LOCATION);
		}
		
		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception
		{
			return new Model();
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
