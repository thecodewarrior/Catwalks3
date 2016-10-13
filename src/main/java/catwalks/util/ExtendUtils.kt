package catwalks.util

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.Tuple
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object ExtendUtils {

    fun getExtendPos(stack: ItemStack, player: EntityPlayer, world: World, posClicked: BlockPos, sideClicked: EnumFacing, testingState: IBlockState): Tuple<BlockPos, EnumFacing> {

        var state = world.getBlockState(posClicked)
        var block = state.block

        if (state != testingState)
        // if the block being placed isn't the same as the one clicked, don't try to extend, just pass the origional info
            return Tuple(
                    posClicked,
                    sideClicked)

        val placeSide = sideClicked.opposite
        val testingPos = BetterMutableBlockPos()
        testingPos.set(posClicked)

        for (i in 1..31) { // 32 block search range, starting from 1 because otherwise it'll go 33 blocks
            testingPos.offset(placeSide) // offset the block we're testing for if the block can be placed

            state = world.getBlockState(testingPos)
            block = state.block

            if (state == testingState) {// if the block is the same as the one clicked, 
                continue
            } else if (block.isReplaceable(world, testingPos) &&
                    player.canPlayerEdit(testingPos, placeSide, stack) &&
                    world.canBlockBePlaced(testingState.block, testingPos, false, placeSide, null as Entity, stack)) { // if the block can be placed here, return the appropriate information
                return Tuple(
                        testingPos.offset(placeSide.opposite).toImmutable(), // offset it back one because that's the block that it'll be placed against
                        placeSide)
            } else { // if the block is something else, break, as we shouldn't continue searching
                break
            }

        }
        return Tuple(
                posClicked,
                sideClicked) // if no position was found return the origional info
    }

    fun getRetractPos(world: World, posClicked: BlockPos, sideClicked: EnumFacing, testingState: IBlockState): BlockPos? {

        var state = world.getBlockState(posClicked)
        val nextState = world.getBlockState(posClicked.offset(sideClicked.opposite))

        if (state != testingState || nextState != testingState)
        // if we don't have a >1 block chain of blocks don't even try
            return null

        val searchDirection = sideClicked.opposite
        val testingPos = BetterMutableBlockPos()
        testingPos.set(posClicked)

        for (i in 1..31) { // 32 block search range, starting from 1 because otherwise it'll go 33 blocks
            testingPos.offset(searchDirection) // offset the block we're testing for if the block can be placed

            state = world.getBlockState(testingPos)

            if (state != testingState) { // if we find a block that isn't the one we're retracting, back up and break out of the loop
                testingPos.offset(searchDirection.opposite)
                break
            }

        }

        return testingPos
    }

    class BetterMutableBlockPos @JvmOverloads constructor(
            /** Mutable X Coordinate  */
            private var x: Int = 0,
            /** Mutable Y Coordinate  */
            private var y: Int = 0,
            /** Mutable Z Coordinate  */
            private var z: Int = 0) : BlockPos(0, 0, 0) {

        /**
         * Get the X coordinate
         */
        override fun getX(): Int {
            return this.x
        }

        /**
         * Get the Y coordinate
         */
        override fun getY(): Int {
            return this.y
        }

        /**
         * Get the Z coordinate
         */
        override fun getZ(): Int {
            return this.z
        }

        fun set(otherPos: BlockPos): BetterMutableBlockPos {
            this[otherPos.x, otherPos.y] = otherPos.z
            return this
        }

        fun setX(x: Int): BetterMutableBlockPos {
            this.x = x
            return this
        }

        fun setY(y: Int): BetterMutableBlockPos {
            this.y = y
            return this
        }

        fun setZ(z: Int): BetterMutableBlockPos {
            this.z = z
            return this
        }

        /**
         * Set the values
         */
        operator fun set(xIn: Int, yIn: Int, zIn: Int): BetterMutableBlockPos {
            setX(xIn)
            setY(yIn)
            setZ(zIn)
            return this
        }

        override fun toImmutable(): BlockPos {
            return BlockPos(this)
        }

        override fun offset(facing: EnumFacing, n: Int): BetterMutableBlockPos {
            setX(this.getX() + facing.frontOffsetX * n)
            setY(this.getY() + facing.frontOffsetY * n)
            setZ(this.getZ() + facing.frontOffsetZ * n)
            return this
        }

    }
}
