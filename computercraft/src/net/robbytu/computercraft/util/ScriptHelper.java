package net.robbytu.computercraft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ScriptHelper {
	public static String getScript(File scriptFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
		StringBuilder script = new StringBuilder("");
		String output = reader.readLine();

		while(output != null) {
			script.append(output);
			output = reader.readLine();
			
			if(output != null) {
				script.append("\n");
			}
		}
		
		return script.toString();
	}
}
