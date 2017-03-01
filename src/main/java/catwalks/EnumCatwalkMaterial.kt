package catwalks

import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.IStringSerializable
import java.util.*

enum class EnumCatwalkMaterial constructor(_STATUS: EnumMaterialStatus = O.statusCache, val GROUP: catwalks.EnumMaterialGroup = O.groupCache, val LAYER: BlockRenderLayer = BlockRenderLayer.CUTOUT_MIPPED, vararg decorations: EnumDecoration) : IStringSerializable {
    // MODPACK
    CUSTOM_0(EnumMaterialStatus.ALLOCATED, EnumMaterialGroup.MODPACK),
    CUSTOM_1(),
    CUSTOM_2(),
    CUSTOM_3(),
    CUSTOM_4(),
    CUSTOM_5(),
    CUSTOM_6(),
    CUSTOM_7(),

    // CATWALKS
    NORMAL(EnumMaterialStatus.ENABLED, EnumMaterialGroup.CATWALKS),
    GLASS(),
    NYAN(),
    BAMBOO(),
    SUGAR_CANE(),

    TBA_CW_01(EnumMaterialStatus.RESERVED),
    TBA_CW_02(),
    TBA_CW_03(),
    TBA_CW_04(),
    TBA_CW_05(),
    TBA_CW_06(),
    TBA_CW_07(),
    TBA_CW_08(),
    TBA_CW_09(),
    TBA_CW_10(),
    TBA_CW_11(),

    // VANILLA

    OAK(EnumMaterialStatus.ENABLED, EnumMaterialGroup.VANILLA),
    SPRUCE(),
    BIRCH(),
    JUNGLE(),
    ACACIA(),
    DARK_OAK(),
    STONE(),

    TBA_MC_01(EnumMaterialStatus.RESERVED),
    TBA_MC_02(),
    TBA_MC_03(),
    TBA_MC_04(),
    TBA_MC_05(),
    TBA_MC_06(),
    TBA_MC_07(),
    TBA_MC_08(),
    TBA_MC_09(),

    // IMMERSIVE_ENGINEERING
    STEEL(EnumMaterialStatus.ENABLED, EnumMaterialGroup.IMMERSIVE_ENGINEERING),
    TREATED_WOOD(),
    ALUMINUM(EnumMaterialStatus.ALLOCATED),

    TBA_IE_00(EnumMaterialStatus.RESERVED),
    TBA_IE_01(),
    TBA_IE_02(),
    TBA_IE_03(),
    TBA_IE_04(),
    TBA_IE_05(),
    TBA_IE_06(),
    TBA_IE_07(),
    TBA_IE_08(),

    // BOTANIA
    LIVINGROCK(EnumMaterialStatus.ALLOCATED, EnumMaterialGroup.BOTANIA),
    LIVINGWOOD(),
    DREAMWOOD(),
    MANAGLASS(),
    ALFGLASS(),

    TBA_BT_00(EnumMaterialStatus.RESERVED),
    TBA_BT_01(),
    TBA_BT_02(),
    TBA_BT_03(),
    TBA_BT_04(),
    TBA_BT_05(),
    TBA_BT_06(),

    // CHISEL
    FACTORY(EnumMaterialStatus.ALLOCATED, EnumMaterialGroup.CHISEL),
    LABORATORY(),

    TBA_CH_00(EnumMaterialStatus.RESERVED),
    TBA_CH_01(),
    TBA_CH_02(),
    TBA_CH_03(),
    TBA_CH_04(),
    TBA_CH_05();

    val DECORATIONS: List<EnumDecoration>
    val FULLNAME = GROUP.name.toLowerCase() + "_" + super.name.toLowerCase()

    val STATUS = if(FULLNAME in Conf.ENABLED) {
        EnumMaterialStatus.ENABLED
    } else if(FULLNAME in Conf.DISABLED) {
        EnumMaterialStatus.ALLOCATED
    } else {
        _STATUS
    }

    init {
        DECORATIONS = Arrays.asList(*decorations)
        O.statusCache = _STATUS
        O.groupCache = GROUP
    }

    fun getID(decor: EnumDecoration): Int {
        return DECORATIONS.indexOf(decor)
    }

    fun getDecor(id: Int): EnumDecoration {
        return DECORATIONS[id]
    }

    override fun getName(): String {
        return FULLNAME
    }

}

enum class EnumMaterialGroup {
    MODPACK,
    VANILLA,
    CATWALKS,
    IMMERSIVE_ENGINEERING,
    BOTANIA,
    CHISEL
}

enum class EnumMaterialStatus(val shouldShow: Boolean, val shouldRegister: Boolean) {
    UNALLOCATED(false, false),
    RESERVED(false, true),
    ALLOCATED(Const.developmentEnvironment, true),
    ENABLED(true, true)
}

private object O {
    var statusCache = EnumMaterialStatus.UNALLOCATED
    var groupCache = EnumMaterialGroup.MODPACK
}
