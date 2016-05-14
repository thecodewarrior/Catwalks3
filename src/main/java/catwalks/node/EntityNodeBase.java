package catwalks.node;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class EntityNodeBase extends Entity {

	public static double SIZE = 0.25;
	
	public EntityNodeBase(World worldIn) {
		super(worldIn);
		setEntityInvulnerable(true);
		setSize((float)SIZE, (float)SIZE);
	}
	
	public EntityNodeBase(World worldIn, double x, double y, double z) {
		this(worldIn);
		setPosition(x, y, z);
	}

	@Override
	public void onUpdate() {
		
	}
	
	@Override
	public void onEntityUpdate() {
		// NOOP
	}
	
	@Override
	protected void entityInit() {
		
	}

	public ITextComponent getNodeName() {
		return new TextComponentString("ASDF");
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
	}

}
