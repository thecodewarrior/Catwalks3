package catwalks.part.converter;

import catwalks.Const;
import catwalks.EnumCatwalkMaterial;
import catwalks.block.EnumCatwalkMaterialOld;
import catwalks.part.PartScaffold;
import catwalks.register.BlockRegister;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.IPartConverter;
import mcmultipart.multipart.IReversePartConverter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by TheCodeWarrior
 */
public class PartConverterScaffold implements IPartConverter, IReversePartConverter {
	
	Collection<Block> collection = Arrays.asList(BlockRegister.scaffolds);
	
	@Override
	public Collection<Block> getConvertableBlocks() {
		return collection;
	}
	
	@Override
	public Collection<? extends IMultipart> convertBlock(IBlockAccess world, BlockPos pos, boolean simulated) {
		IBlockState state = world.getBlockState(pos);
//		BlockScaffolding block = state.getBlock();
		PartScaffold part = new PartScaffold();
		part.setCatwalkMaterial(state.getValue(Const.MATERIAL_META));
		return Collections.singletonList(part);
	}
	
	@Override
	public boolean convertToBlock(IMultipartContainer container) {
		Collection<? extends IMultipart> parts = container.getParts();
		if(parts.size() == 1) {
			IMultipart firstPart = parts.iterator().next();
			if(firstPart instanceof PartScaffold) {
				PartScaffold part = (PartScaffold) firstPart;
				EnumCatwalkMaterial mat = part.getCatwalkMaterial();
				container.removePart(part);
				container.getWorldIn().setBlockState(container.getPosIn(), BlockRegister.getScaffold(mat).getDefaultState().withProperty(Const.MATERIAL_META, mat));
			}
		}
		return false;
	}
}
