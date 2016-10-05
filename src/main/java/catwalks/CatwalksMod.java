package catwalks;

import catwalks.block.EnumCatwalkMaterialOld;
import catwalks.movement.MovementHandler;
import catwalks.proxy.CommonProxy;
import catwalks.register.BlockRegister;
import catwalks.register.ItemRegister;
import catwalks.register.MultipartRegister;
import catwalks.register.RecipeRegister;
import catwalks.util.WrenchChecker;
import catwalks.util.meta.ArrayProp;
import catwalks.util.meta.BoolArrayProp;
import catwalks.util.meta.MetaStorage;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static catwalks.util.meta.MetaStorage.bits;

@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION,
	guiFactory = "catwalks.gui.CatwalksModConfGuiFactory",
	dependencies="")
public class CatwalksMod {
	public static final String MODID = "catwalks";
    public static final String VERSION = "0.3.0";
    
    @SidedProxy(serverSide="catwalks.proxy.CommonProxy", clientSide="catwalks.proxy.ClientProxy")
    public static CommonProxy proxy;
    
    @Instance
    public static CatwalksMod INSTANCE;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.register();
    	ItemRegister.register();
    	RecipeRegister.register();
    	MultipartRegister.register();
	    
    	Conf.loadConfigsFromFile(event.getSuggestedConfigurationFile());
    	MinecraftForge.EVENT_BUS.register(proxy);
    	proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	WrenchChecker.init();
    	// just to load the classes and their instances
		MovementHandler.INSTANCE.getClass();
    }
    
    public static CreativeTabs tab = new CreativeTabs("tabCatwalks") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock( BlockRegister.catwalk );
		}
	};
	
	public static void YELL_AT_DEV() {
		Object.class.getClass(); // BREAKPOINT HERE OR YOU ARE A HORRIBLE PERSON!!!!!
	}
	
	public static void SCREAM_AT_DEV() throws DEV_SCREAMING_EXCEPTION {
		YELL_AT_DEV();
		throw new DEV_SCREAMING_EXCEPTION();
	}
	
	public static class DEV_SCREAMING_EXCEPTION extends RuntimeException {
		public DEV_SCREAMING_EXCEPTION() {
			super("I'M SCREAMING AT YOU CAUSE YOU DIDN'T DO GOOD!!!!");
		}
	}
	
	public static ArrayProp<EnumCatwalkMaterial> allocate_material(MetaStorage.Allocator allocator) {
		return allocator.allocateArray("material", EnumCatwalkMaterial.values(), bits(128));
	}
	
	public static BoolArrayProp allocate_decor(MetaStorage.Allocator allocator) {
		return allocator.allocateBoolArray("decor", 15);
	}
}
