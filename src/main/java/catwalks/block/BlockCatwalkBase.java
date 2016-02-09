package catwalks.block;

import net.minecraft.block.material.Material;

public abstract class BlockCatwalkBase extends BlockBase {

	public BlockCatwalkBase(Material material, String name) {
		super(material, name);
	}
	
	public BlockCatwalkBase(Material materialIn, String name, Class<?> clazz) {
		super(materialIn, name, clazz);
	}
	
}
