package catwalks

import catwalks.block.EnumDecoration
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.IStringSerializable
import java.util.*

/**
 * Created by TheCodeWarrior
 */
enum class EnumCatwalkMaterial constructor(val ALLOCATED: Boolean, val WIP: Boolean, val GROUP: EnumMaterialGroup, val LAYER: BlockRenderLayer, vararg decorations: EnumDecoration) : IStringSerializable {
    // MODPACK
    CUSTOM_0(true, Conf.CUSTOM_ENABLED[0], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_1(true, Conf.CUSTOM_ENABLED[1], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_2(true, Conf.CUSTOM_ENABLED[2], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_3(true, Conf.CUSTOM_ENABLED[3], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_4(true, Conf.CUSTOM_ENABLED[4], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_5(true, Conf.CUSTOM_ENABLED[5], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_6(true, Conf.CUSTOM_ENABLED[6], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_7(true, Conf.CUSTOM_ENABLED[7], EnumMaterialGroup.MODPACK, BlockRenderLayer.CUTOUT),

    // CATWALKS
    NORMAL(true, false, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    GLASS(true, false, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    NYAN(true, false, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    BAMBOO(true, false, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),

    TBA_CW_00(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_01(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_02(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_03(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_04(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_05(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_06(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_07(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_08(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_09(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_10(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),
    TBA_CW_11(false, true, EnumMaterialGroup.CATWALKS, BlockRenderLayer.CUTOUT),

    // VANILLA

    OAK(true, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    SPRUCE(true, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    BIRCH(true, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    JUNGLE(true, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    ACACIA(true, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    DARK_OAK(true, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),

    TBA_MC_00(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_01(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_02(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_03(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_04(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_05(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_06(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_07(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_08(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),
    TBA_MC_09(false, true, EnumMaterialGroup.VANILLA, BlockRenderLayer.CUTOUT),

    // IMMERSIVE_ENGINEERING
    STEEL(true, false, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TREATED_WOOD(true, false, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    ALUMINUM(true, false, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),

    TBA_IE_00(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_01(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_02(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_03(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_04(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_05(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_06(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_07(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),
    TBA_IE_08(false, true, EnumMaterialGroup.IMMERSIVE_ENGINEERING, BlockRenderLayer.CUTOUT),

    // BOTANIA
    LIVINGROCK(true, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    LIVINGWOOD(true, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    DREAMWOOD(true, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    MANAGLASS(true, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    ALFGLASS(true, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),

    TBA_BT_00(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    TBA_BT_01(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    TBA_BT_02(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    TBA_BT_03(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    TBA_BT_04(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    TBA_BT_05(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),
    TBA_BT_06(false, true, EnumMaterialGroup.BOTANIA, BlockRenderLayer.CUTOUT),

    // CHISEL
    FACTORY(true, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),
    LABORATORY(true, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),

    TBA_CH_00(false, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),
    TBA_CH_01(false, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),
    TBA_CH_02(false, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),
    TBA_CH_03(false, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),
    TBA_CH_04(false, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT),
    TBA_CH_05(false, true, EnumMaterialGroup.CHISEL, BlockRenderLayer.CUTOUT);

    val DECORATIONS: List<EnumDecoration>

    init {
        DECORATIONS = Arrays.asList(*decorations)
    }

    fun show(): Boolean {
        return ALLOCATED && (Const.developmentEnvironment || !WIP)
    }

    fun getID(decor: EnumDecoration): Int {
        return DECORATIONS.indexOf(decor)
    }

    fun getDecor(id: Int): EnumDecoration {
        return DECORATIONS[id]
    }

    override fun getName(): String {
        return this.GROUP.name.toLowerCase() + "_" + this.name.toLowerCase()
    }
}
