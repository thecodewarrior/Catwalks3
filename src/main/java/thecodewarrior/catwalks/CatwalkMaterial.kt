package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.saving.serializers.builtin.special.FallbackEnumValue
import net.minecraft.util.IStringSerializable

/**
 * TODO: Document file CatwalkMaterial
 *
 * Created by TheCodeWarrior
 */
enum class CatwalkMaterial() : IStringSerializable {
    @FallbackEnumValue
    CLASSIC;

    override fun getName(): String {
        return this.name.toLowerCase()
    }
}
