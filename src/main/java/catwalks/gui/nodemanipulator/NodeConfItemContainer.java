package catwalks.gui.nodemanipulator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import catwalks.Const;
import catwalks.gui.inventory.ItemInventoryWrapper;
import catwalks.register.ItemRegister;
import catwalks.util.GeneralUtil;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;

public class NodeConfItemContainer extends GenericContainer {
	
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
    
	public NodeConfItemContainer(EntityPlayer player) {
		super(Const.developmentEnvironment ? createFactory() : factory);
		
		ItemStack stack = GeneralUtil.getHeld(player, (s) -> s.getItem() == ItemRegister.nodeConf);
		
		addInventory(CONTAINER_INVENTORY, new ItemInventoryWrapper(stack));
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
	}

}
