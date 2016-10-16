package catwalks

import catwalks.block.EnumDecoration
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.IStringSerializable
import java.util.*
import catwalks.EnumMaterialGroup as _Group

/**
 * Created by TheCodeWarrior
 */
enum class EnumCatwalkMaterial constructor(val ALLOCATED: Boolean, val WIP: Boolean, val GROUP: catwalks.EnumMaterialGroup, val LAYER: BlockRenderLayer = BlockRenderLayer.SOLID, vararg decorations: EnumDecoration) : IStringSerializable {
    // MODPACK
    CUSTOM_0(true, Conf.CUSTOM_ENABLED[0], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_1(true, Conf.CUSTOM_ENABLED[1], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_2(true, Conf.CUSTOM_ENABLED[2], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_3(true, Conf.CUSTOM_ENABLED[3], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_4(true, Conf.CUSTOM_ENABLED[4], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_5(true, Conf.CUSTOM_ENABLED[5], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_6(true, Conf.CUSTOM_ENABLED[6], _Group.MODPACK, BlockRenderLayer.CUTOUT),
    CUSTOM_7(true, Conf.CUSTOM_ENABLED[7], _Group.MODPACK, BlockRenderLayer.CUTOUT),

    // CATWALKS
    NORMAL(true, false, _Group.CATWALKS, BlockRenderLayer.CUTOUT),
    GLASS(true, false, _Group.CATWALKS, BlockRenderLayer.TRANSLUCENT),
    NYAN(true, false, _Group.CATWALKS, BlockRenderLayer.CUTOUT),
    BAMBOO(true, false, _Group.CATWALKS),

    TBA_CW_00(false, true, _Group.CATWALKS),
    TBA_CW_01(false, true, _Group.CATWALKS),
    TBA_CW_02(false, true, _Group.CATWALKS),
    TBA_CW_03(false, true, _Group.CATWALKS),
    TBA_CW_04(false, true, _Group.CATWALKS),
    TBA_CW_05(false, true, _Group.CATWALKS),
    TBA_CW_06(false, true, _Group.CATWALKS),
    TBA_CW_07(false, true, _Group.CATWALKS),
    TBA_CW_08(false, true, _Group.CATWALKS),
    TBA_CW_09(false, true, _Group.CATWALKS),
    TBA_CW_10(false, true, _Group.CATWALKS),
    TBA_CW_11(false, true, _Group.CATWALKS),

    // VANILLA

    OAK(true, false, _Group.VANILLA, BlockRenderLayer.CUTOUT),
    SPRUCE(true, false, _Group.VANILLA, BlockRenderLayer.CUTOUT),
    BIRCH(true, false, _Group.VANILLA, BlockRenderLayer.CUTOUT),
    JUNGLE(true, false, _Group.VANILLA, BlockRenderLayer.CUTOUT),
    ACACIA(true, false, _Group.VANILLA, BlockRenderLayer.CUTOUT),
    DARK_OAK(true, false, _Group.VANILLA, BlockRenderLayer.CUTOUT),

    TBA_MC_00(false, true, _Group.VANILLA),
    TBA_MC_01(false, true, _Group.VANILLA),
    TBA_MC_02(false, true, _Group.VANILLA),
    TBA_MC_03(false, true, _Group.VANILLA),
    TBA_MC_04(false, true, _Group.VANILLA),
    TBA_MC_05(false, true, _Group.VANILLA),
    TBA_MC_06(false, true, _Group.VANILLA),
    TBA_MC_07(false, true, _Group.VANILLA),
    TBA_MC_08(false, true, _Group.VANILLA),
    TBA_MC_09(false, true, _Group.VANILLA),

    // IMMERSIVE_ENGINEERING
    STEEL(true, false, _Group.IMMERSIVE_ENGINEERING),
    TREATED_WOOD(true, false, _Group.IMMERSIVE_ENGINEERING),
    ALUMINUM(true, false, _Group.IMMERSIVE_ENGINEERING),

    TBA_IE_00(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_01(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_02(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_03(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_04(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_05(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_06(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_07(false, true, _Group.IMMERSIVE_ENGINEERING),
    TBA_IE_08(false, true, _Group.IMMERSIVE_ENGINEERING),

    // BOTANIA
    LIVINGROCK(true, true, _Group.BOTANIA),
    LIVINGWOOD(true, true, _Group.BOTANIA),
    DREAMWOOD(true, true, _Group.BOTANIA),
    MANAGLASS(true, true, _Group.BOTANIA, BlockRenderLayer.TRANSLUCENT),
    ALFGLASS(true, true, _Group.BOTANIA, BlockRenderLayer.TRANSLUCENT),

    TBA_BT_00(false, true, _Group.BOTANIA),
    TBA_BT_01(false, true, _Group.BOTANIA),
    TBA_BT_02(false, true, _Group.BOTANIA),
    TBA_BT_03(false, true, _Group.BOTANIA),
    TBA_BT_04(false, true, _Group.BOTANIA),
    TBA_BT_05(false, true, _Group.BOTANIA),
    TBA_BT_06(false, true, _Group.BOTANIA),

    // CHISEL
    FACTORY(true, true, _Group.CHISEL),
    LABORATORY(true, true, _Group.CHISEL),

    TBA_CH_00(false, true, _Group.CHISEL),
    TBA_CH_01(false, true, _Group.CHISEL),
    TBA_CH_02(false, true, _Group.CHISEL),
    TBA_CH_03(false, true, _Group.CHISEL),
    TBA_CH_04(false, true, _Group.CHISEL),
    TBA_CH_05(false, true, _Group.CHISEL);

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
