package catwalks.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockBase extends Block {

	
	public BlockBase(Material material, String name) {
		this(material, name, null);
	}
	
	public BlockBase(Material materialIn, String name, Class<? extends ItemBlock> clazz) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		if(clazz == null) {
			GameRegistry.registerBlock(this);
		} else {
			GameRegistry.registerBlock(this, clazz);
		}
	}

}
