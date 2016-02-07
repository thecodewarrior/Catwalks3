package catwalks.langplus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import catwalks.util.Logs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class LangPlusParser {

	static Pattern braceLine   = Pattern.compile("^([\\w.]+)\\s*\\{");
	static Pattern bracketLine = Pattern.compile("^([\\w.]+)\\s*\\[");
	
	
	private static void parse(String path, String name, StringBuilder output, Map<String, String> variables, Rewriter varRewriter) throws IOException {
		InputStream stream = stream(path + "/" + name + ".lang");
		
		if(stream == null) {
			Logs.error("[LangPlus] Null stream for %s%s.lang", path, name);
			return;
		}
		
		boolean isAddingVars = false;
		
		int lineNum = 0;
		Stack<String> region = new Stack<String>();
		
		boolean isInBlock = false;
		String currentBlockName = "";
		StringBuilder currentBlock = new StringBuilder();
		
		for (String line : IOUtils.readLines(stream, Charsets.UTF_8)) {
			lineNum++;
			
			// remove all leading whitespace
			line = line.replaceFirst("^\\s+", "");
			if(line.length() == 0) {
				if(isInBlock)
					currentBlock.append("\n");
				continue;					
			}
			// replace variables
			line = varRewriter.rewrite(line);
			// replace unknown variables
			line = line.replaceAll("\\\\(\\$\\{\\w+\\})", "$1");
			
			// close text area
			if(line.charAt(0) == ']') {
				output.append(e( currentBlockName, currentBlock.toString() )+"\n");
				isInBlock = false;
				currentBlockName = "";
				currentBlock = new StringBuilder();
				continue;
			}
			
			// remove prefix char
			line = line.replaceAll("^\\.", "");
			// remove all escaped prefix chars
			line = line.replaceAll("^\\\\" + "\\.", ".");
			
			// handle text in text area
			if(isInBlock) {
				if(line.endsWith("\\"))
					currentBlock.append(line.substring(0, line.length()-1));
				else
					currentBlock.append(line + "\n");
				continue;
			}
			
			if(line.startsWith("${") && region.isEmpty()) {
				isAddingVars = true;
				continue;
			}
			if(line.charAt(0) == '#')
				continue;
			if(line.charAt(0) == '}') {
				if(isAddingVars) {
					isAddingVars = false;
					continue;
				}
					
				if(region.isEmpty()) {
					Logs.error("[Advanced Lang Loader] Ignoring unexpected close bracket: line %d", lineNum);
				} else {
					region.pop();
				}
				continue;
			}
			
			if(isAddingVars) {
				String[] parts = line.split("=", 2);
				if(parts.length < 2) {
					Logs.error("[Advanced Lang Loader] Invalid variable declaration, '=' not found: line %d", lineNum);
				} else {
					variables.put(parts[0], parts[1]);
				}
				continue;
			}
			if(line.startsWith("$import")) {
				parse(path, line.substring("$import".length()).replaceAll("^\\s+", ""), output, variables, varRewriter);
				continue;
			}
			
			Matcher m = braceLine.matcher(line);
			
			if(m.matches()) {
				region.push( (region.isEmpty() ? "" : region.peek() ) + m.group(1) + ".");
				continue;
			}
			
			m = bracketLine.matcher(line);
			
			if(m.matches() && !isInBlock) {
				currentBlockName = (region.isEmpty() ? "" : region.peek() ) + m.group(1);
				isInBlock = true;
				continue;
			}
			
			output.append((region.isEmpty() ? "" : region.peek()) + line + "\n");
			
		}
		
		IOUtils.closeQuietly(stream);
	}
	
	public static InputStream parse(String path) {		
		try {
			StringBuilder real = new StringBuilder();
			
			final Map<String, String> variables = new HashMap<String, String>();
			
			Rewriter vars = new Rewriter("(?<!\\\\)\\$\\{(\\w+)\\}") {
				
				@Override
				public String replacement() {
					String name = group(1);
					if(variables.containsKey(name)) {
						return variables.get(name);
					}
					Logs.error("[Advanced Lang Loader] Variable not found: %s", name);
					return "!!VAR_NOT_FOUND(" + name + ")!!";
				}
			};
			
			parse(path, "main", real, variables, vars);
			
			InputStream output = new ByteArrayInputStream(real.toString().getBytes(StandardCharsets.UTF_8));
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static InputStream stream(String resource) {
		try {
			return Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(resource)).getInputStream();
		} catch (IOException e) {
			return null;
		}
	}
	
	private static String e(String name, String text) {
		return name + "=" + text.replaceAll("\n", "\\\\n");
	}
	
}
