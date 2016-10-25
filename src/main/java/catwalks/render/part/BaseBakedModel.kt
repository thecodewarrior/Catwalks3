package catwalks.render.part

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite

/**
 * Created by TheCodeWarrior
 */
abstract class BaseBakedModel(private val loc: String) : IBakedModel {

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
        return null
    }

    override fun getOverrides(): ItemOverrideList? {
        return null
    }
}
