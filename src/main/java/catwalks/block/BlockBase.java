package catwalks.block;

import catwalks.CatwalksMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Function;

public class BlockBase extends Block {

	
	public BlockBase(Material material, String name) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CatwalksMod.tab);
		initPreRegister();
		GameRegistry.register(this);
	}
	
	@SuppressWarnings("unchecked")
	public BlockBase(Material materialIn, String name, Function<Block, ItemBlock> item) {
		this(materialIn, name);
		if(item == null) {
			GameRegistry.register(new ItemBlock(this).setRegistryName(getRegistryName()));
		} else {
			GameRegistry.register(item.apply(this).setRegistryName(getRegistryName()));
		}
	}
	
	public void initPreRegister() {}
	
}
