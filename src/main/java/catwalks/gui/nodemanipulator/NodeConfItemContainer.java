package catwalks.gui.nodemanipulator;

import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import catwalks.Const;
import catwalks.gui.CommandContainer;
import catwalks.gui.inventory.ItemInventoryWrapper;
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
            	addSlotRange(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, 0, 44, 20, 5, 18);
                layoutPlayerInventorySlots(8, 51);
            }
        };
    }
    
    public int index = 0;
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
        
        int selectedSlot = this.inventorySlots.size() - ( 9 - player.inventory.currentItem );
        Slot s = this.inventorySlots.get(selectedSlot);
        s = new SlotStaticWrapper(s);
        this.inventorySlots.set(selectedSlot, s);
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

	public static class SlotStaticWrapper extends Slot {
		
		Slot s;
		
		public SlotStaticWrapper(Slot s) {
			super(s.inventory, s.getSlotIndex(), s.xDisplayPosition, s.yDisplayPosition);
			this.slotNumber = s.slotNumber;
			this.s = s;
		}
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return false;
		}
		
		@Override
	    public boolean isItemValid(ItemStack stack) {
	        return false;
	    }
		
		// forwarding
		
		@Override
		public boolean canBeHovered() {
			return s.canBeHovered();
		}
		
		@Override
		public ItemStack decrStackSize(int amount) {
			return s.decrStackSize(amount);
		}
		
		@Override
		public ResourceLocation getBackgroundLocation() {
			return s.getBackgroundLocation();
		}
		
		@Override
		public TextureAtlasSprite getBackgroundSprite() {
			return s.getBackgroundSprite();
		}
		
		@Override
		public boolean getHasStack() {
			return s.getHasStack();
		}
		
		@Override
		public int getItemStackLimit(ItemStack stack) {
			return s.getItemStackLimit(stack);
		}
		
		@Override
		public int getSlotIndex() {
			return s.getSlotIndex();
		}
		
		@Override
		public int getSlotStackLimit() {
			return s.getSlotStackLimit();
		}
		
		@Override
		public String getSlotTexture() {
			return s.getSlotTexture();
		}
		
		@Override
		public ItemStack getStack() {
			return s.getStack();
		}
		
		@Override
		public boolean isHere(IInventory inv, int slotIn) {
			return s.isHere(inv, slotIn);
		}
		
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
			s.onPickupFromSlot(playerIn, stack);
		}
		
		@Override
		public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
			s.onSlotChange(p_75220_1_, p_75220_2_);
		}
		
		@Override
		public void onSlotChanged() {
			s.onSlotChanged();
		}
		
		@Override
		public void putStack(ItemStack stack) {
			s.putStack(stack);
		}
		
		@Override
		public void setBackgroundLocation(ResourceLocation texture) {
			s.setBackgroundLocation(texture);
		}
		
		@Override
		public void setBackgroundName(String name) {
			s.setBackgroundName(name);
		}
	}
	
}
