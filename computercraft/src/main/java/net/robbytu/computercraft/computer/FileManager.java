package net.robbytu.computercraft.computer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.database.ComputerData;

public class FileManager {
	private static File computersDir = new File(CCMain.instance.getDataFolder(), "computers");
	public static final String separator = "/";
	
	public static void newComputerEvent(ComputerData data) {
		File computerDir = new File(computersDir, Integer.toString(data.getId()));
		if(!computerDir.exists()) {
			computerDir.mkdir();
		}
		
		// TODO: Copy defaults
	}
	
	public static void deleteComputerEvent(ComputerData data) {
		//File computersDir = new File(CCMain.instance.getDataFolder().getAbsolutePath() + File.separator + "computers");
		if(!computersDir.exists()) computersDir.mkdir();
		
		File computerDir = new File(computersDir, Integer.toString(data.getId()));
		if(computerDir.exists()) {
			removeDirectory(computerDir);
		}
	}
	
	/**
	 * Gets a valid real path for the given computer path
	 * 
	 * @param path The path to translate to real path
	 * @param CID The id of the computer to create the real path for
	 * 
	 * @return The real path or an empty string if the path is not a valid computer path.
	 * @deprecated Use {@link #toRealPath(String, int) toRealPath} instead
	 */
	@Deprecated
	private static String getValidPath(String path, int CID) {
		File result = toRealPath(path, CID);
		return result == null ? "" : result.getAbsolutePath();
	}
	
	/**
	 * Converts a real file path to a computer path for the given computer.
	 * 
	 * @param path The file path to convert.
	 * @param CID The id of the computer, this path should be converted for.
	 * @return The computer path as a string or an empty string if the given real path is not in the path for the given computer id.
	 */
	public static String toComputerPath(File path, int CID) {
		String result = path.getAbsolutePath();
		if (result.startsWith(computersDir.getAbsolutePath() + File.separator + CID + File.separator)) {
			result = result.replace(computersDir.getAbsolutePath() + File.separator + CID, "");
			return result.replace(File.separator, separator);
		}
		else if (result.equals(computersDir.getAbsolutePath() + File.separator + CID)) {
			return separator;
		}
		
		return "";
	}
	
	/**
	 * Combines a parent path with a sub path.
	 * @param path The parent path.
	 * @param subPath The child path.
	 * @return The parent path combined with the sub path in one string.
	 */
	public static String combine(String path, String subPath) {
		if (!path.endsWith(separator)) {
			path += separator; 
		}
		
		if (subPath.startsWith(separator)) {
			subPath = subPath.substring(1);
		}
		
		return path + subPath;
	}
	
	/**
	 * Converts a computer path to a real file system path represented by a File object.
	 * 
	 * @param path The path to convert
	 * @param CID The computer, for what this computer path needs to be generated.
	 * @return The File object representing the real path or <b>null</b> if the path is not valid.
	 */
	public static File toRealPath(String path, int CID) {
		path = path.replace(separator, File.separator);
		String[] split = path.split(Pattern.quote(File.separator));
		String newPath = computersDir.getAbsolutePath() + File.separator + CID;
		
		for (int i = 0; i < split.length; i++) {
			String pathPart = split[i];
			if (!pathPart.isEmpty()) {
				if (pathPart.equals("..")) {
					newPath = newPath.substring(0, newPath.lastIndexOf(File.separator));
				}
				else {
					newPath += File.separator + pathPart;
				}
			}
		}
		
		if (newPath.startsWith(computersDir.getAbsolutePath() + File.separator + CID)) {
			return new File(newPath);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Gets a file or directory for a given computer path. 
	 * 
	 * The difference between this method and {@link #toRealPath(String, int) toRealPath} is, that 
	 * this method will return <b>null</b>, if the given path does not exist. 
	 * 
	 * @param path The path to get the file or directory for.
	 * @param CID The computer id of the computer to get the file or directory for.
	 * @return The file or folder as a File instance or <b>null</b> if it is invalid or doesn't exist.
	 */
	public static File get(String path, int CID) {
		File file = toRealPath(path, CID);
		
		if (file != null && file.exists()) {
			return file;
		}
		
		return null;
	}
	
	public static String getDir(String path, int CID) {
		File file = get(path, CID);
		
		if (file != null && file.isDirectory()) {
			return toComputerPath(file, CID);
		}
		
		return "";
	}
	
	public static boolean isDir(String path, int CID) {
		return !getDir(path, CID).isEmpty();
	}
	
	/**
	 * Creates a new directory in the given path.
	 * 
	 * @param path The path the directory should be created in
	 * @param name The name of the new directory
	 * @param CID  The id of the computer to create the directory for
	 * @return true if the directory was successfully created, false otherwise.
	 * @deprecated Use {@link #mkDir(String, int)} instead.
	 */
	@Deprecated
	public static boolean mkDir(String path, String name, int CID) {
		return mkDir(combine(path,name), CID);
	}
	
	public static boolean mkDir(String newDir, int CID) {
		File path = toRealPath(newDir, CID);
		if (path == null) return false;
		if (path.exists() && path.isDirectory()) return true;
		else if(path.exists()) return false;
		else if(path.mkdir()) return true;
		return false;		
	}
	
	public static File getFile(String path, String name, int CID) {
		File file = get(combine(path, name), CID);
		
		if (file.isFile()) {
			return file;
		}
		
		return null;
	}
	
	public static String getFileAsString(String path, String name, int CID) {
		StringBuilder strFile = new StringBuilder();
		File file = getFile(path, name, CID);
		
		if (file != null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				String output = reader.readLine();

				while(output != null) {
					if (!strFile.toString().isEmpty())
						strFile.append("\n");
					strFile.append(output);
					output = reader.readLine();
				}
				
				return strFile.toString();
			} 
			catch (FileNotFoundException e) {	}
			catch (IOException e) { }
		}
		
		return null;
	}
	
	public static boolean exists(String path, int CID) {
		return (get(path, CID) != null);
	}
	
	public static boolean mkFile(String path, String name, int CID) {
		String newPath = getValidPath(path + File.separator + name, CID);
		if (!newPath.equals("")) {
			File newFile = new File(newPath);
			if (!newFile.exists()) {
				try {
					return newFile.createNewFile();
				} 
				catch (IOException e) { }
			}
		}
		
		return false;
	}
	
	public static void printList(String path, int CID) {
		String[] rom_files = null;
		String[] files = null;
		if(path.equals(separator)) {
			// root, also do rom
			File rom = new File(CCMain.instance.getDataFolder().getAbsolutePath() + File.separator + "rom");
			rom_files = rom.list();
			if(rom_files != null) {
				for(int i = 0; i < rom_files.length; i++) {
					if(rom_files[i] != "boot.lua") {
						// Don't print the os file
						CCMain.instance.ComputerThreads.get(CID).gui.addEntry(rom_files[i]);
					}
				}
			}
		}
		
		File file = toRealPath(path, CID);
		if(file != null && file.exists()) {
			files = file.list();
			if(files != null) {
				for(int i = 0; i < files.length; i++) {
					CCMain.instance.ComputerThreads.get(CID).gui.addEntry(files[i]);
				}
			}
			else {
				CCMain.instance.ComputerThreads.get(CID).gui.addEntry("No such directory.");
			}
		}
		
		String total = "";
		if(rom_files != null) {
			total += rom_files.length + " files on ROM, ";
		}
		
		if(files == null) {
			total += "0 files in folder";
		}
		else {
			total += files.length + " files in folder";
		}

		CCMain.instance.ComputerThreads.get(CID).gui.addEntry(total);
	}
	
	public static String rm(String path, int CID) {
		if(path.equals(separator)) {
			return "RM_ROOT_ACCESS_DENIED";
		}
		else {
			File file = get(path, CID);
			if (file != null) {
				if(!file.exists()) {
					return "RM_DOES_NOT_EXIST";
				}
				else if(file.isDirectory()) {
					removeDirectory(file);
					return "RM_DIR_OK";
				}
				else if(!file.isDirectory() && file.delete()) {
					return "RM_FILE_OK";
				}
			}
			return "RM_FAIL";
		}
	}
	
	
	
	public static void removeDirectory(File directory) {
		if(!directory.isDirectory()) return;
		String[] files = directory.list();
		
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				File f = new File(directory, files[i]);
				if(f.isDirectory()) {
					removeDirectory(f);
				}
				else {
					f.delete();
				}
			}
		}

		directory.delete();
	}
}
