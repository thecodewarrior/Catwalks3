package catwalks.render;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;

@SuppressWarnings("deprecation")
public abstract class SmartModelBase implements IFlexibleBakedModel, ISmartBlockModel {

	public SmartModelBase() {}

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

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing side) {
		// This should never be called! The handleBlockState returns an
		// AssembledBakedModel instead of CompositeModel
		throw new UnsupportedOperationException();
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		// This should never be called! The handleBlockState returns an
		// AssembledBakedModel instead of CompositeModel
		throw new UnsupportedOperationException();
	}

	// returns the vertex format for this model (each vertex in the list of
	// quads can have a variety of information
	// associated with it - for example, not just position and texture
	// information, but also colour, world brightness,
	// etc.) Just use DEFAULT_BAKED_FORMAT unless you really know what you're
	// doing...
	@Override
	public VertexFormat getFormat() {
		return Attributes.DEFAULT_BAKED_FORMAT;
	}

	@Override
	public IBakedModel handleBlockState(IBlockState iBlockState) {
		if (iBlockState instanceof IExtendedBlockState) {
			IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) iBlockState;
			return newBakedModel(iExtendedBlockState);
		}
		return newBakedModel();
	}
	
	
	public abstract IBakedModel newBakedModel(IExtendedBlockState state);
	
	/**
	 * Called if the block state isn't an IExtendedBlockState
	 * @return
	 */
	public abstract IBakedModel newBakedModel();
}
