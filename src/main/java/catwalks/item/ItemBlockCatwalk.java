package catwalks.item;

import catwalks.block.EnumCatwalkMaterialOld;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCatwalk extends ItemBlock {
	public ItemBlockCatwalk(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
    	return super.getUnlocalizedName(stack) + "." + EnumCatwalkMaterialOld.values()[stack.getItemDamage()].getName().toLowerCase();
    }
    
}
