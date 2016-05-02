package catwalks.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExtendUtils {

	public static Tuple<BlockPos, EnumFacing> getExtendPos(ItemStack stack, EntityPlayer player, World world, BlockPos posClicked, EnumFacing sideClicked, IBlockState testingState) {
		
		IBlockState state = world.getBlockState(posClicked);
		Block block = state.getBlock();
		
		if(!state.equals(testingState)) // if the block being placed isn't the same as the one clicked, don't try to extend, just pass the origional info
			return new Tuple<BlockPos, EnumFacing>(
					posClicked,
					sideClicked
				);
		
		EnumFacing placeSide = sideClicked.getOpposite();
		BetterMutableBlockPos testingPos = new BetterMutableBlockPos();
		testingPos.set(posClicked);
		
		for(int i = 1; i < 32; i++) { // 32 block search range, starting from 1 because otherwise it'll go 33 blocks
			testingPos.offset(placeSide); // offset the block we're testing for if the block can be placed
			
			state = world.getBlockState(testingPos);
			block = state.getBlock();
			
			if(state.equals(testingState)) {// if the block is the same as the one clicked, 
				continue;
			}
			else if(
						block.isReplaceable(world, testingPos) &&
						player.canPlayerEdit(testingPos, placeSide, stack) &&
						world.canBlockBePlaced(testingState.getBlock(), testingPos, false, placeSide, (Entity)null, stack)
					) { // if the block can be placed here, return the appropriate information
				return new Tuple<BlockPos, EnumFacing>(
						testingPos.offset(placeSide.getOpposite()).getImmutable(),	// offset it back one because that's the block that it'll be placed against
						placeSide
					);
			} else { // if the block is something else, break, as we shouldn't continue searching
				break;
			}
			
		}
		return new Tuple<BlockPos, EnumFacing>(
				posClicked,
				sideClicked
			); // if no position was found return the origional info
	}
	
	public static BlockPos getRetractPos(World world, BlockPos posClicked, EnumFacing sideClicked, IBlockState testingState) {
		
		IBlockState state = world.getBlockState(posClicked);
		IBlockState nextState = world.getBlockState(posClicked.offset(sideClicked.getOpposite()));
		
		if(!state.equals(testingState) || !nextState.equals(testingState)) // if we don't have a >1 block chain of blocks don't even try
			return null;
		
		EnumFacing searchDirection = sideClicked.getOpposite();
		BetterMutableBlockPos testingPos = new BetterMutableBlockPos();
		testingPos.set(posClicked);
		
		for(int i = 1; i < 32; i++) { // 32 block search range, starting from 1 because otherwise it'll go 33 blocks
			testingPos.offset(searchDirection); // offset the block we're testing for if the block can be placed
			
			state = world.getBlockState(testingPos);
			
			if(!state.equals(testingState)) { // if we find a block that isn't the one we're retracting, back up and break out of the loop
				testingPos.offset(searchDirection.getOpposite());
				break;
			}
			
		}
		
		return testingPos;
	}

	public static class BetterMutableBlockPos extends BlockPos {

		/** Mutable X Coordinate */
		private int x;
		/** Mutable Y Coordinate */
		private int y;
		/** Mutable Z Coordinate */
		private int z;

		public BetterMutableBlockPos() {
			this(0, 0, 0);
		}

		public BetterMutableBlockPos(int x_, int y_, int z_) {
			super(0, 0, 0);
			this.x = x_;
			this.y = y_;
			this.z = z_;
		}

		/**
		 * Get the X coordinate
		 */
		public int getX() {
			return this.x;
		}

		/**
		 * Get the Y coordinate
		 */
		public int getY() {
			return this.y;
		}

		/**
		 * Get the Z coordinate
		 */
		public int getZ() {
			return this.z;
		}

		public BetterMutableBlockPos set(BlockPos otherPos) {
			this.set(otherPos.getX(), otherPos.getY(), otherPos.getZ());
			return this;
		}
		
		public BetterMutableBlockPos setX(int x) {
			this.x = x;
			return this;
		}
		
		public BetterMutableBlockPos setY(int y) {
			this.y = y;
			return this;
		}
		
		public BetterMutableBlockPos setZ(int z) {
			this.z = z;
			return this;
		}

		/**
		 * Set the values
		 */
		public BetterMutableBlockPos set(int xIn, int yIn, int zIn) {
			setX(xIn);
			setY(yIn);
			setZ(zIn);
			return this;
		}

		@Override
		public BlockPos getImmutable() {
			return new BlockPos(this);
		}

		public BetterMutableBlockPos offset(EnumFacing facing, int n) {
			setX(this.getX()+facing.getFrontOffsetX()*n);
			setY(this.getY()+facing.getFrontOffsetY()*n);
			setZ(this.getZ()+facing.getFrontOffsetZ()*n);
			return this;
		}

	}
}
