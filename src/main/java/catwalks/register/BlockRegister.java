package catwalks.register;

import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkStairTop;
import catwalks.block.BlockScaffolding;
import catwalks.block.extended.BlockCagedLadder;
import catwalks.block.extended.tileprops.TileExtended;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
