package catwalks.register;

import catwalks.EnumCatwalkMaterial;
import catwalks.block.*;
import catwalks.block.extended.BlockCagedLadder;
import catwalks.block.extended.tileprops.TileExtended;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockRegister {

	
	public static BlockCatwalk catwalk;
	public static BlockCatwalkStair catwalkStair;
	public static BlockCatwalkStairTop stairTop;
	public static BlockCagedLadder cagedLadder;
	public static BlockScaffolding[] scaffolds;
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
		catwalkStair = new BlockCatwalkStair();
		stairTop = new BlockCatwalkStairTop();
		cagedLadder = new BlockCagedLadder();
		scaffolds = new BlockScaffolding[(int)Math.ceil(EnumCatwalkMaterial.values().length/16f)];
		for(int i = 0; i < scaffolds.length; i++) {
			scaffolds[i] = new BlockScaffolding(i);
		}
	}
	
	public static BlockScaffolding getScaffold(EnumCatwalkMaterial mat) {
		return scaffolds[mat.ordinal() >> 4];
	}
}
