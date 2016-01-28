package catwalks.proxy;

import catwalks.CatwalksMod;
import catwalks.register.BlockRegister;
import catwalks.render.CatwalkModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
//	
//	List<String>
//	
//	@SubscribeEvent
//    // Allows us to add entries for our icons
//    public void textureStitch(TextureStitchEvent.Pre event) {
//		
//        icons = new TextureAtlasSprite[BlockDenseOre.maxMetdata];
//
//        TextureMap textureMap = event.map;
//
//        for (DenseOre entry : DenseOresRegistry.ores.values()) {
//            int i = entry.id;
//
//            // Note: Normally you would simply use map.registerSprite(), this method
//            // is only required for custom texture classes.
//
//            // name of custom icon ( must equal getIconName() )
//            String name = TextureOre.getDerivedName(entry.texture);
//            // see if there's already an icon of that name
//            TextureAtlasSprite texture = textureMap.getTextureExtry(name);
//            if (texture == null) {
//                // if not create one and put it in the register
//                texture = new TextureOre(entry);
//                textureMap.setTextureEntry(name, texture);
//            }
//
//            icons[i] = textureMap.getTextureExtry(name);
//        }
//
//    }
	
	public void preInit() {
		BlockRegister.initRender();
	}
	
//	@SubscribeEvent
//    public void onModelBakeEvent(ModelBakeEvent event) {
//        Object object =  event.modelRegistry.getObject(CatwalkModel.modelResourceLocation);
//        if (object == null) {
//        	CatwalkModel customModel = new CatwalkModel();
//            event.modelRegistry.putObject(CatwalkModel.modelResourceLocation, customModel);
//        }
//    }
//	@SubscribeEvent
//	public void onTextureStitchEvent(TextureStitchEvent event) {
//		event.map.registerSprite(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base"));
//		event.map.registerSprite(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/bottom/base"));
//	}
	
//	public void
	
}
