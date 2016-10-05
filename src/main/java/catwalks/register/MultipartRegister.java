package catwalks.register;

import catwalks.part.PartCatwalk;
import catwalks.part.PartScaffold;
import catwalks.part.PartStair;
import catwalks.part.converter.PartConverterScaffold;
import mcmultipart.multipart.MultipartRegistry;

/**
 * Created by TheCodeWarrior
 */
public class MultipartRegister {
	
	public static void register() {
		// scaffold
		MultipartRegistry.registerPart(PartScaffold.class, PartScaffold.ID);
		PartConverterScaffold converter = new PartConverterScaffold();
		MultipartRegistry.registerPartConverter(converter);
		MultipartRegistry.registerReversePartConverter(converter);
		
		// catwalk
		MultipartRegistry.registerPart(PartCatwalk.class, PartCatwalk.ID);
		
		
		// catwalk
		MultipartRegistry.registerPart(PartStair.class, PartStair.ID);
	}
	
}
