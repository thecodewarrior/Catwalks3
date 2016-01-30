package catwalks.render.catwalk;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import catwalks.CatwalksMod;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

public class CatwalkModel implements IModel {

    public static final ResourceLocation TEXTURE_SHEET = new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base");

  // return all other resources used by this model (not strictly needed for this example because we load all the subcomponent
  //   models during the bake anyway)
    @Override
    public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of();
    }

    // return all the textures used by this model (not strictly needed for this example because we load all the subcomponent
    //   models during the bake anyway)
    @Override
    public Collection<ResourceLocation> getTextures() {
      return ImmutableList.copyOf(new ResourceLocation[]{TEXTURE_SHEET});
    }

    //  Bake the subcomponents into a CompositeModel
    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      return new CatwalkSmartModel();
    }

    // Our custom loaded doesn't need a default state, just return null
    @Override
    public IModelState getDefaultState() {
      return null;
    }
}
