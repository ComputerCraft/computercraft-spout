package net.robbytu.computercraft;

import java.io.File;

import net.robbytu.computercraft.database.ComputerData;

public class FileManager {
	private static File computersDir = new File(CCMain.instance.getDataFolder().getAbsolutePath() + "/computers/");
	
	public static void newComputerEvent(ComputerData data) {
		File computerDir = new File(computersDir, Integer.toString(data.getId()));
		if(!computerDir.exists()) {
			computerDir.mkdir();
		}
		
		// TODO: Copy defaults
	}
	
	public static void deleteComputerEvent(ComputerData data) {
		//File computersDir = new File(CCMain.instance.getDataFolder().getAbsolutePath() + "/computers");
		if(!computersDir.exists()) computersDir.mkdir();
		
		File computerDir = new File(computersDir, Integer.toString(data.getId()));
		if(computerDir.exists()) {
			removeDirectory(computerDir);
		}
	}
	
	public static String getDir(String path, int CID) {
		String[] split = path.split("/");
		String newPath = computersDir + "/" + CID;
		
		for (int i = 0; i < split.length; i++) {
			String pathPart = split[i];
			if (!pathPart.isEmpty()) {
				if (pathPart.equals("..")) {
					newPath = newPath.substring(0, newPath.lastIndexOf("/"));
				}
				else {
					newPath += "/" + pathPart;
				}
			}
		}
		
		newPath += "/";
		
		if (newPath.startsWith(computersDir.getAbsolutePath() + "/" + CID)) {
			File file = new File(computersDir, CID + "/" + path); 
			
			if (file.isDirectory()) {
				return newPath.replace(computersDir.getAbsolutePath() + "/" + CID, "");
			}
		}
		return "";
	}
	
	public static boolean isDir(String path, int CID) {
		return !getDir(path, CID).isEmpty();
	}
	
	public static boolean mkDir(String path, String name, int CID) {
		File file = new File(computersDir, CID + "/" + path + "/" + name); // Potentially dangerous

		if(file.exists() && file.isDirectory()) return true;
		else if(file.exists()) return false;
		else if(file.mkdir()) return true;
		else return false;
	}
	
	public static File getFile(String path, String name, int CID) {
		File file = new File(computersDir, CID + "/" + path + "/" + name); // Potentially dangerous
		if (file.exists())
			return file;
		return null;
	}
	
	public static void printList(String path, int CID) {
		String[] rom_files = null;
		String[] files = null;
		if(path == "/") {
			// root, also do rom
			File rom = new File(CCMain.instance.getDataFolder().getAbsolutePath() + "/rom");
			rom_files = rom.list();
			if(rom_files != null) {
				for(int i = 0; i < rom_files.length; i++) {
					if(rom_files[i] != "boot.lua") {
						// Don't print the os file
						CCMain.instance.ComputerThreads.get(Integer.toString(CID)).gui.addEntry(rom_files[i]);
					}
				}
			}
		}
		
		File file = new File(computersDir, CID + "/" + path); // Potentially dangerous
		if(file.exists() && file.getAbsolutePath().startsWith(computersDir.getAbsolutePath() + CID)) {
			files = file.list();
			if(files != null) {
				for(int i = 0; i < files.length; i++) {
					CCMain.instance.ComputerThreads.get(Integer.toString(CID)).gui.addEntry(files[i]);
				}
			}
			else {
				CCMain.instance.ComputerThreads.get(Integer.toString(CID)).gui.addEntry("No such directory.");
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

		CCMain.instance.ComputerThreads.get(Integer.toString(CID)).gui.addEntry(total);
	}
	
	public static String rm(String path, int CID) {
		if(path == "/") {
			return "RM_ROOT_ACCESS_DENIED";
		}
		else {
			File file = new File(computersDir, CID + "/" + path); // Potentially dangerous
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
			else {
				return "RM_FAIL";
			}
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
