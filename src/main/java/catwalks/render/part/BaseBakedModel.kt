package catwalks.render.part

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraftforge.client.model.IPerspectiveAwareModel
import net.minecraftforge.common.model.IModelState
import org.apache.commons.lang3.tuple.Pair
import javax.vecmath.Matrix4f

/**
 * Created by TheCodeWarrior
 */
abstract class BaseBakedModel(private val loc: String, private val modelState: IModelState) : IPerspectiveAwareModel {

    override fun isAmbientOcclusion(): Boolean {
        return true
    }

    override fun isGui3d(): Boolean {
        return true
    }

    override fun isBuiltInRenderer(): Boolean {
        return false
    }

    override fun getParticleTexture(): TextureAtlasSprite? {
        return null
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms? {
        return ItemCameraTransforms.DEFAULT
    }

    override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Pair<out IBakedModel, Matrix4f> {
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, modelState, cameraTransformType)
    }

    override fun getOverrides(): ItemOverrideList? {
        return ItemOverrideList.NONE
    }

}
