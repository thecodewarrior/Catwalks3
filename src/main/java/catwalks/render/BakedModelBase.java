package catwalks.render;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("deprecation")
public abstract class BakedModelBase implements IBakedModel {
	public BakedModelBase() {}
	
	@Override
	public abstract List<BakedQuad> getFaceQuads(EnumFacing side);

	@Override
	public abstract List<BakedQuad> getGeneralQuads();

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
	public abstract TextureAtlasSprite getParticleTexture();

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

}
