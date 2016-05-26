package catwalks.gui.nodemanipulator;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import catwalks.Const;
import catwalks.gui.CommandContainer;
import catwalks.gui.inventory.ItemInventoryWrapper;
import catwalks.gui.inventory.SlotWrapper;
import catwalks.item.ItemNode;
import catwalks.register.ItemRegister;
import catwalks.util.GeneralUtil;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import mcjty.lib.network.Argument;

public class NodeConfItemContainer extends GenericContainer implements CommandContainer {
	
    public static final String CONTAINER_INVENTORY = "container";

    public static ContainerFactory factory = createFactory();
	
    public static ContainerFactory createFactory() {
    	return new ContainerFactory() {
            @Override
            protected void setup() {
            	addSlotRange(new SlotDefinition(SlotType.SLOT_SPECIFICITEM, ItemNode.class), CONTAINER_INVENTORY, 0, 44, 20, 5, 18);
                layoutPlayerInventorySlots(8, 51);
            }
        };
    }
    
    public int index = 0;
    public int selectedSlot = -1;
    public ItemStack stack;
    
	public NodeConfItemContainer(EntityPlayer player) {
		super(Const.developmentEnvironment ? createFactory() : factory);
		
		ItemStack stack = GeneralUtil.getHeld(player, (s) -> s.getItem() == ItemRegister.nodeConf);
		this.stack = stack;
		
		if(stack.getTagCompound() != null)
			index = stack.getTagCompound().getInteger("selectedSlot");
		
		addInventory(CONTAINER_INVENTORY, new ItemInventoryWrapper(stack));
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
        
        
        if(player.inventory.mainInventory[player.inventory.currentItem] == stack) {
	        int selectedSlot = this.inventorySlots.size() - ( 9 - player.inventory.currentItem );
	        Slot s = this.inventorySlots.get(selectedSlot);
	        s = new SlotStaticWrapper(s);
	        this.inventorySlots.set(selectedSlot, s);
	        
	        this.selectedSlot = player.inventory.currentItem;
        }
	}

	@Override
	public void execute(String command, Map<String, Argument> args) {
		if(command.equals(Const.COMMAND_OPTIONS)) {
			index = args.get("sel").getInteger();
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("selectedSlot", index);
		}
	}

	public static class SlotStaticWrapper extends SlotWrapper {
		
		public SlotStaticWrapper(Slot s) {
			super(s);
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return false;
		}
		
		@Override
	    public boolean isItemValid(ItemStack stack) {
	        return false;
	    }
		
		@Override
		public boolean canBeHovered() {
			return false;
		}
	}
	
}
