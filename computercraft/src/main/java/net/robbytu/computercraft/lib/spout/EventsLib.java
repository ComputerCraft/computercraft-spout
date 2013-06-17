package net.robbytu.computercraft.lib.spout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;

public class EventsLib extends LuaLib {
	private HashMap<String, List<String>> eventListeners = new HashMap<String, List<String>>();
	private LuaValue env;

	public EventsLib() {
		super("events");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.env = env;
		LuaTable events = new LuaTable();
		events.set("registerListener", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue functionName) {
				if(!eventListeners.containsKey(eventId.toString())) {
					eventListeners.put(eventId.toString(), new ArrayList<String>());
				}
				
				if(!eventListeners.get(eventId.toString()).contains(functionName.toString())) { // Prevent from adding a callback multiple times
					eventListeners.get(eventId.toString()).add(functionName.toString());
				}
				
				return LuaValue.NIL;
			}
		});

		events.set("unregisterListener", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue functionName) {
				if(!eventListeners.containsKey(eventId.toString())) {
					return LuaValue.NIL; // EventId isn't even registered, so how are we supposed to delete anything?
				}
				
				if(eventListeners.get(eventId.toString()).contains(functionName.toString())) {
					eventListeners.get(eventId.toString()).remove(functionName.toString());
				}
				
				return LuaValue.NIL;
			}
		});

		events.set("isRegistered", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue functionName) {
				if(!eventListeners.containsKey(eventId.toString())) {
					return LuaValue.FALSE; // EventId isn't even registered, so how can a callback be registered?
				}
				
				return LuaValue.valueOf(eventListeners.get(eventId.toString()).contains(functionName.toString()));
			}
		});
		
		events.set("triggerEvent", new TwoArgFunction(env) {
			public LuaValue call(LuaValue eventId, LuaValue message) {
				triggerEvent(eventId.checkjstring(), message.optjstring(null));
				return LuaValue.NIL;
			}
		});
		
		env.set("event", events);
		return events;
	}
	
	public void triggerEvent(String eventId, String message) {
		if(!eventListeners.containsKey(eventId)) { // EventId isn't even registered, so there's nothing to be called
			return;
		}
		LuaValue luaEventId = LuaValue.valueOf(eventId);
		LuaValue luaMessage = LuaValue.NIL;
		if (message != null)
			luaMessage = LuaValue.valueOf(message);
		
		for(int i = 0; i < eventListeners.get(eventId).size(); i++) {
			env.get(eventListeners.get(eventId).get(i)).call(luaEventId, luaMessage);
		}
	}

}
