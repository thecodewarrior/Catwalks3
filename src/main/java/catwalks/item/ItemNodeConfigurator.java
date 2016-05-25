package catwalks.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeSettingsQuery;
import catwalks.node.EntityNodeBase;
import catwalks.node.NodeUtil.EnumNodes;
import catwalks.proxy.ClientProxy;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;

public class ItemNodeConfigurator extends ItemNodeBase {

	public ItemNodeConfigurator() {
		super("nodeConfigurator");
		setMaxStackSize(1);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String nodeText = "NONE";
		if(stack.hasTagCompound()) {
			EnumNodes type = EnumNodes.values()[stack.getTagCompound().getInteger("type")];
			nodeText = I18n.format("node." + type.toString() + ".name");
		}
		return I18n.format(this.getUnlocalizedName(stack), nodeText);
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		if(ClientProxy.getSelectedNode() == hit.data().node) {
			NetworkHandler.network.sendToServer(new PacketNodeSettingsQuery(hit.data().node.getEntityId()));
		}
		return null;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking() && !worldIn.isRemote) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("type", 0);
			}
			
			EntityNodeBase entity = new EntityNodeBase(worldIn, pos.getX()+hitX, pos.getY()+hitY, pos.getZ()+hitZ, EnumNodes.values()[stack.getTagCompound().getInteger("type")]);
			int i = (stack.getTagCompound().getInteger("type")+1)%EnumNodes.values().length;
			stack.getTagCompound().setInteger("type", i);
			switch (facing) {
			case NORTH:
				entity.rotationYaw = 180;
				break;
			case SOUTH:
				break;
			case EAST:
				entity.rotationYaw = -90;
				break;
			case WEST:
				entity.rotationYaw = 90;
				break;
			case UP:
				entity.rotationPitch = -90;
				break;
			case DOWN:
				entity.rotationPitch = 90;
				break;

			default:
				break;
			}
			
			worldIn.spawnEntityInWorld(entity);
			
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
