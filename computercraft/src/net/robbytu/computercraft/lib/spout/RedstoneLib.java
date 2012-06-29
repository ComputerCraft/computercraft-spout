package net.robbytu.computercraft.lib.spout;

import org.getspout.spoutapi.block.SpoutBlock;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;
import net.robbytu.computercraft.util.BlockManager;

public class RedstoneLib extends LuaLib {

	private ComputerThread computer;

	public RedstoneLib() {
		super("redstone");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		LuaTable redstone = new LuaTable();
		redstone.set("setOutput", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				setOuput(val1.checkint(), val2.checkboolean());
				return LuaValue.NIL;
			}
		});
		
		redstone.set("isPowered", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return valueOf(isPowered(val.checkint()));
			}
		});

		redstone.set("isPowered", redstone.get("getInput")); //deprecated, use rs.getInput
		
		env.set("rs", redstone);
		return redstone;
	}
	
	public void setOuput(int side, boolean power) {
		if (side > 3 || side < 0) return;
		SpoutBlock target = BlockManager.blockAtSide(computer.getBlock(), side);
		target.setBlockPowered(power);
	}
	
	public boolean isPowered(int side)
	{
		if (side > 3 || side < 0) return false;		
		SpoutBlock target = BlockManager.blockAtSide(computer.getBlock(), side);
		return target.isBlockPowered();
	}

}
