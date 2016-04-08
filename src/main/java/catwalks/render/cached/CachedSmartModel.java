package catwalks.render.cached;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;

public class CachedSmartModel implements ISmartBlockModel {

	LoadingCache<List<Object>, IBakedModel> modelCache;
	SimpleModel model;
	
	public CachedSmartModel(SimpleModel model) {
		this.model = model;
		modelCache = CacheBuilder.newBuilder().build(new CacheLoader<List<Object>, IBakedModel>() {

			@Override
			public IBakedModel load(List<Object> key) throws Exception {
				return new BakedModelCache(model.getQuads(key));
			}
			
		});
	}
	
	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		try {
			return modelCache.get(model.getKey(state));
		} catch (ExecutionException e) {
			e.printStackTrace();
			return BakedModelCache.NULL;
		}
	}
	
	public static class BakedModelCache implements IBakedModel {

		public static final IBakedModel NULL = new BakedModelCache(
			ImmutableList.of()
		);
		
		List<List<BakedQuad>> quads;
		
		public BakedModelCache(List<List<BakedQuad>> quads) {
			this.quads = quads;
		}
		
		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing side) {
			int index = side.getIndex();
			if(index < quads.size())
				return quads.get(index);
			else
				return ImmutableList.of();
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			int index = EnumFacing.values().length;
			if(index < quads.size())
				return quads.get(index);
			else
				return ImmutableList.of();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return null;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return null;
		}
		
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) { return null; }

	@Override
	public List<BakedQuad> getGeneralQuads() { return null; }

	@Override
	public boolean isAmbientOcclusion() { return false; }

	@Override
	public boolean isGui3d() { return false; }

	@Override
	public boolean isBuiltInRenderer() { return false; }

	@Override
	public TextureAtlasSprite getParticleTexture() { return null; }

	@Override
	public ItemCameraTransforms getItemCameraTransforms() { return null; }

}
