package net.robbytu.computercraft.materials;

import net.robbytu.computercraft.CCMain;

public class Materials {
	public static ComputerBlock ComputerBlockSouth;
	public static ComputerBlock ComputerBlockNorth;
	public static ComputerBlock ComputerBlockEast;
	public static ComputerBlock ComputerBlockWest;
	
	public Materials() {
		ComputerBlockSouth = new ComputerBlock(CCMain.instance, "ComputerBlockSouth", false, 0);
		ComputerBlockNorth = new ComputerBlock(CCMain.instance, "ComputerBlockNorth", false, 1);
		ComputerBlockEast = new ComputerBlock(CCMain.instance, "ComputerBlockEast", false, 2);
		ComputerBlockWest = new ComputerBlock(CCMain.instance, "ComputerBlockWest", false, 3);
	}
}
