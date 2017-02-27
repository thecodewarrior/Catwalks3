package catwalks

import catwalks.block.property.UPropertyEnum
import catwalks.block.property.UPropertyObject
import catwalks.part.data.CatwalkRenderData
import catwalks.part.data.ScaffoldRenderData
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

object Const {

    init { /* Centeralizing constants */
    }

    var MATERIAL_BITS = 128.combination_bits()

    init { /* DRYing constants */
    }

    val log: Logger = LogManager.getLogger(CatwalksMod.MODID)

    val RAND = Random()

    val HORIZONTALS_FROM_NORTH = arrayOf(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST)

    var developmentEnvironment = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
    val MODID = CatwalksMod.MODID

    val VEC_CENTER = Vec3d(0.5, 0.5, 0.5)
    val VEC_ANTICENTER = Vec3d(-0.5, -0.5, -0.5)

    val ENTITY_DATA_CAPABILITY_LOC = ResourceLocation(MODID, "entityData")
    //
    //	@CapabilityInject(ICWEntityData.class)
    //	public static Capability<ICWEntityData> CW_ENTITY_DATA_CAPABILITY = null;

    init {    /* blockstate properties*/
    }

    val FACING = UPropertyEnum.create<EnumFacing>("facing", EnumFacing::class.java)
    val MATERIAL = PropertyEnum.create<EnumCatwalkMaterial>("material", EnumCatwalkMaterial::class.java) {
        it?.STATUS?.shouldRegister ?: false
    }

    init { /* render-only properties (unlisted properties) */
    }

    val CATWALK_RENDER_DATA = UPropertyObject<CatwalkRenderData>("renderdata", CatwalkRenderData::class.java)
    val SCAFFOLD_RENDER_DATA = UPropertyObject<ScaffoldRenderData>("renderdata", ScaffoldRenderData::class.java)

    fun location(path: String): ResourceLocation {
        return ResourceLocation(MODID, path)
    }
}
