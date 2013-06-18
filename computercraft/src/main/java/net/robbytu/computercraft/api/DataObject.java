package net.robbytu.computercraft.api;

/**
 * A data object can be used to transfer data from one device to another.
 * 
 * It's up to the Device developer what data a DataObject can provide,
 * but it should be independent from the Device instance itself, so it should not have 
 * any references to the Device instance.
 * 
 * @author hapm
 */
public interface DataObject {

}
