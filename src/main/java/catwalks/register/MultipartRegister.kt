package catwalks.register

import catwalks.part.PartCatwalk
import catwalks.part.PartScaffold
import catwalks.part.PartStair
import catwalks.part.converter.PartConverterScaffold
import mcmultipart.multipart.MultipartRegistry

/**
 * Created by TheCodeWarrior
 */
object MultipartRegister {

    fun register() {
        // scaffold
        MultipartRegistry.registerPart(PartScaffold::class.java, PartScaffold.ID)
        val converter = PartConverterScaffold()
        MultipartRegistry.registerPartConverter(converter)
        MultipartRegistry.registerReversePartConverter(converter)

        // catwalk
        MultipartRegistry.registerPart(PartCatwalk::class.java, PartCatwalk.ID)

        // stair
        MultipartRegistry.registerPart(PartStair::class.java, PartStair.ID)
    }

}
