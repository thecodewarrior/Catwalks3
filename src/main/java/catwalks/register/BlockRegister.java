package catwalks.register;

import catwalks.EnumCatwalkMaterial;
import catwalks.block.BlockScaffolding;

public class BlockRegister {

	public static BlockScaffolding[] scaffolds;
	
	public static void register() {
		scaffolds = new BlockScaffolding[(int)Math.ceil(EnumCatwalkMaterial.values().length/16f)];
		for(int i = 0; i < scaffolds.length; i++) {
			scaffolds[i] = new BlockScaffolding(i);
		}
	}
	
	public static BlockScaffolding getScaffold(EnumCatwalkMaterial mat) {
		return scaffolds[mat.ordinal() >> 4];
	}
}
