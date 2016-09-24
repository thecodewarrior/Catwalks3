package catwalks.register;

import catwalks.Const;
import catwalks.part.PartCatwalk;
import catwalks.part.PartScaffold;
import catwalks.part.converter.PartConverterScaffold;
import mcmultipart.multipart.MultipartRegistry;

/**
 * Created by TheCodeWarrior
 */
public class MultipartRegister {
	
	public static void register() {
		// scaffold
		MultipartRegistry.registerPart(PartScaffold.class, Const.MODID + ":scaffold");
		PartConverterScaffold converter = new PartConverterScaffold();
		MultipartRegistry.registerPartConverter(converter);
		MultipartRegistry.registerReversePartConverter(converter);
		
		// catwalk
		MultipartRegistry.registerPart(PartCatwalk.class, Const.MODID + ":catwalk");
	}
	
}
