package catwalks

import catwalks.proxy.CommonProxy
import catwalks.register.BlockRegister
import catwalks.register.ItemRegister
import catwalks.register.MultipartRegister
import catwalks.register.RecipeRegister
import catwalks.util.WrenchChecker
import catwalks.util.meta.ArrayProp
import catwalks.util.meta.BoolArrayProp
import catwalks.util.meta.MetaStorage
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.Instance
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION, guiFactory = "catwalks.gui.CatwalksModConfGuiFactory", dependencies = "")
class CatwalksMod {

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Const
        BlockRegister
        ItemRegister
        RecipeRegister.register()
        MultipartRegister.register()

        Conf.loadConfigsFromFile(event.suggestedConfigurationFile)
        MinecraftForge.EVENT_BUS.register(proxy)
        proxy.preInit()
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        WrenchChecker.init()
        // just to load the classes and their instances
        //		MovementHandler.INSTANCE.getClass();
    }

    companion object {
        const val MODID = "catwalks"
        const val VERSION = "0.3.0"

        @SidedProxy(serverSide = "catwalks.proxy.CommonProxy", clientSide = "catwalks.proxy.ClientProxy")
        lateinit var proxy: CommonProxy

        @Instance
        lateinit var INSTANCE: CatwalksMod

        var tab: CreativeTabs = object : CreativeTabs("tabCatwalks") {
            @SideOnly(Side.CLIENT)
            override fun getTabIconItem(): Item? {
                return Item.getItemFromBlock(Blocks.BRICK_BLOCK)
            }
        }

        fun allocate_material(allocator: MetaStorage.Allocator): ArrayProp<EnumCatwalkMaterial> {
            return allocator.allocateArray("material", EnumCatwalkMaterial.values(), 128.combination_bits())
        }

        fun allocate_decor(allocator: MetaStorage.Allocator): BoolArrayProp {
            return allocator.allocateBoolArray("decor", 15)
        }
    }
}
