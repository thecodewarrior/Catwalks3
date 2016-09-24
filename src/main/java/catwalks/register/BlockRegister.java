package catwalks.register;

import java.util.function.Supplier;

import catwalks.Const;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkStairTop;
import catwalks.block.BlockScaffolding;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.extended.BlockCagedLadder;
import catwalks.block.extended.tileprops.TileExtended;
import catwalks.render.ModelHandler;
import catwalks.render.cached.CachedSmartModel;
import catwalks.render.cached.models.CatwalkModel;
import catwalks.render.cached.models.LadderModel;
import catwalks.render.cached.models.StairBottomModel;
import catwalks.render.cached.models.StairTopModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegister {

	
	public static BlockCatwalk catwalk;
	public static BlockCatwalkStair catwalkStair;
	public static BlockCatwalkStairTop stairTop;
	public static BlockCagedLadder cagedLadder;
	public static BlockScaffolding scaffold;
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
		catwalkStair = new BlockCatwalkStair();
		stairTop = new BlockCatwalkStairTop();
		cagedLadder = new BlockCagedLadder();
		scaffold = new BlockScaffolding();
	}
}
