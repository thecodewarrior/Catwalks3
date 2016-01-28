package catwalks.register;

import catwalks.block.BlockCatwalk;
import catwalks.render.CatwalkModModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegister {

	public static BlockCatwalk catwalk;
	
	
	public static void register() {
		catwalk = new BlockCatwalk();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		ModelLoaderRegistry.registerLoader(CatwalkModModelLoader.instance);
		catwalk.initModel();
	}
	
}
