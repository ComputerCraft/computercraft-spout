package net.robbytu.computercraft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.robbytu.computercraft.CCMain;

import org.bukkit.Bukkit;

/**
 * Class to install the default resources from the jar to the server file system.
 * 
 * @author Markus Andree
 *
 */
public class DefaultResources {
	/**
	 * Extracts all default resources to the given directory.
	 * 
	 * @param destDir The directory to extract to.
	 * @param overwrite If true all existing files will be overwritten, 
	 *                  otherwise only missing files will be extracted.
	 */
	public static void extractDefaults(String destDir, boolean overwrite) {
		JarFile jar = getResourceJar();
		if (jar == null) {
			Bukkit.getLogger().warning("Couldn't extract default rom content: jar not found.");
			return;
		}
		
		Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			JarEntry entry = e.nextElement();
			String file = entry.getName();
			if (file.startsWith("defaults/")) {
				File dest = new File(destDir, file.substring(9));
				if (overwrite || !dest.exists())
					extractFile(file, dest);
			}
		}
		
		try {
			jar.close();
		} catch (IOException ex) {
			return; 
		}
	}
	
	public static boolean extractFile(String resourcePath, File dest) {
		OutputStream output = null;
		InputStream input = null;
		try {
			if (!dest.exists())
				dest.createNewFile();
			output = new FileOutputStream(dest, false);
			input = CCMain.class.getResourceAsStream("/" + resourcePath);
			byte[] buf = new byte[8192];
			while (true) {
				int length = input.read(buf);
				if (length < 0) {
					break;
				}
				output.write(buf, 0, length);
			}
			input.close();
			output.close();
		} catch (IOException e) {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e1) {
				}
			}
			
			if (input != null) {
				try {
					input.close();
				} catch (IOException e1) {
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	public static JarFile getResourceJar() {
		URL ownUrl = DefaultResources.class.getResource("/plugin.yml"); 
		String path = ownUrl.getPath();
	    File jarFile;
		try {
			jarFile = new File(new URI(path));
			while (jarFile != null && !jarFile.exists()) {
				if (!path.contains("!/"))
					return null;
				path = path.substring(0, path.lastIndexOf("!/"));
				jarFile = new File(new URI(path));
			}

			return new JarFile(jarFile);
		} catch (URISyntaxException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
