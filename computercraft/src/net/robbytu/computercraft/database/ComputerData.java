package net.robbytu.computercraft.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name="ComputerData")
public class ComputerData {
	@Id
	private int id;
	
	@NotNull
	private int x;
	
	@NotNull
	private int y;
	
	@NotNull
	private int z;
	
	@NotEmpty
	private String world;
	
	private boolean wireless = false;
	
	private String networkName;
	
	private String networkPassword;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public String getWorld() {
		return world;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public boolean isWireless() {
		return this.wireless;
	}
	
	public void setWireless(boolean Wireless) {
		this.wireless = Wireless;
	}
	
	public String getNetworkName() {
		return networkName;
	}
	
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	
	public String getNetworkPassword() {
		return networkPassword;
	}
	
	public void setNetworkPassword(String networkPassword) {
		this.networkPassword = networkPassword;
	}
}
