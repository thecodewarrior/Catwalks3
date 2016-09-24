package catwalks.register;

import catwalks.Const;
import catwalks.part.PartCatwalkBase;
import catwalks.part.PartScaffold;
import catwalks.part.converter.PartConverterScaffold;
import com.sun.org.apache.xpath.internal.operations.Mult;
import mcmultipart.multipart.MultipartRegistry;

/**
 * Created by TheCodeWarrior
 */
public class MultipartRegister {
	
	public static void register() {
		MultipartRegistry.registerPart(PartScaffold.class, Const.MODID + ":scaffold");
		PartConverterScaffold converter = new PartConverterScaffold();
		MultipartRegistry.registerPartConverter(converter);
		MultipartRegistry.registerReversePartConverter(converter);
	}
	
}
