package catwalks.render.cached;

import java.util.List;
import java.util.concurrent.ExecutionException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import catwalks.render.ModelUtils;

public class CachedSmartModel implements IBakedModel {

	LoadingCache<List<Object>, List<List<BakedQuad>>> modelCache;
	SimpleModel model;
	
	public CachedSmartModel(SimpleModel model) {
		this.model = model;
		modelCache = CacheBuilder.newBuilder().build(new CacheLoader<List<Object>, List<List<BakedQuad>>>() {

			@Override
			public List<List<BakedQuad>> load(List<Object> key) throws Exception {
				return model.getQuads(key);
			}
			
		});
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		try {
			if(side == null)
				return modelCache.get(model.getKey(state)).get(EnumFacing.VALUES.length);
			else
				return modelCache.get(model.getKey(state)).get(side.ordinal());
		} catch (ExecutionException e) {
			e.printStackTrace();
			return ImmutableList.of();
		}
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
		return ModelUtils.getSprite(null);
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

}
