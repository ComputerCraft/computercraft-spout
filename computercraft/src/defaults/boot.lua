-- Vars
_os = "CraftOS 0.0.3 Alpha"
_motd = "This is a test-version of ComputerCraft. TEST THIS SHIT YOAA" -- Don't make this too much longer!
_show_motd = true
_curdir = fs.separator

_exit_shell = false

-- Functions
cd = function(dir)
   	_newdir = fs.getDir(fs.combine(_curdir, dir))
	if _newdir == "" then
   		print(color.byString("RED") .. "No such directory")
	else
		_curdir = _newdir
		pwd()
	end
end

lls = function()
	if fs.isDir(_curdir) then
		fs.printList(_curdir)
	else
		print(color.byString("RED") .. "Current working directory does not exist!")		
	end
end

rm = function(filename)
	if fs.isDir(_curdir) then
		result = fs.delete(fs.combine(_curdir, filename))
		if result == "RM_FILE_OK" then
			print(color.byString("GREEN") .. "File got deleted.")
		elseif result == "RM_DIR_OK" then
			print(color.byString("GREEN") .. "Directory got deleted.")
		elseif result == "RM_DOES_NOT_EXIST" then
			print(color.byString("RED") .. "No such file or directory.")
		else
			print(color.byString("RED") .. "Something strange is goin' on...")
		end
	else
		print(color.byString("RED") .. "Current working directory does not exist!")
	end
end

wlc = function()
	print("Please enter your SSID:")
	term.setInputTip("SSID")
	SSID = term.getInput()
	print("Enter network's password: ")
	term.setInputTip("Password (optional)")
	PWD = term.getInput()

	RET = rednet.open(SSID, PWD)

	if RET == "RN_CONNECTED" then
		print(color.byString("GREEN") .. "Now connected to " .. SSID)
	else
		print(color.byString("RED") .. "Failed to connect with error " .. RET)
	end
end

pwd = function()
	print(color.byString("GRAY") .. "Working directory: " .. _curdir)
end

motd = function()
	print(color.byString("AQUA") .. " ** " .. _motd)
end

shell = function()
	term.setInputTip("Enter a command")
	input = string.upper(term.getInput())

	if string.sub(input, 0, 3) == "CD " then
		cd(string.sub(input, 4))
	elseif input == "PWD" then
		pwd()
	elseif input == "LS" then
		lls()
	elseif input == "MOTD" then
		motd()
	elseif string.sub(input, 0, 3) == "RM " then
		rm(string.sub(input, 4))
	elseif input == "LUA" or input == "CONSOLE" then
		luaConsole()
	elseif string.sub(input, 0, 6) == "MKDIR " then
		if fs.makeDir(fs.combine(_curdir, string.sub(input, 7))) then
			print(color.byString("GREEN") .. "Directory was created!")
		else
			print(color.byString("RED") .. "Something went wrong!")
		end
	elseif string.sub(input, 0, 4) == "RUN " then
		if run(_curdir, string.sub(input, 5)) then
			_exit_shell = true
		end
	elseif string.sub(input, 0, 7) == "MKFILE " then
		if io.mkFile(_curdir, string.sub(input, 8)) then
			print(color.byString("GREEN") .. "File was created!")
		else
			print(color.byString("RED") .. "Something went wrong!")
		end
	elseif string.sub(input, 0, 9) == "VIEWFILE " then
		_fileContents = io.getFile(_curdir, string.sub(input, 10))

		if _fileContents == nil then
			print(color.byString("RED") .. "Something went wrong!")
		else
			print(_fileContents)
		end
	elseif string.sub(input, 0, 7) == "CONNECT" then
		wlc()
	else
		print(color.byString("RED") .. "Command not found.")
	end

	if _exit_shell == false then
		shell()
	end
end

luaConsole = function()
	term.setInputTip("Type a command or " .. color.byString("GRAY") .. "exit")

	print(color.byString("WHITE") .. " ** Welcome to the LUA console.")
	print(color.byString("WHITE") .. " ** Type a command or " .. color.byString("GRAY") .. "exit" .. color.byString("WHITE") .. " to leave.")

	con = true
	while con do
		input = term.getInput()
		if input == "exit" then
			print(color.byString("WHITE") .. " ** Lua console exiting...")
			con = false
		else
			new_input = ""
			if string.sub(input, 0, 5) == "PRINT" then 
				new_input = input
			else
				new_input = "print(" .. input .. ")"
			end
		
			_tc = loadstring(new_input)
			xpcall(_tc, eh)
		end
	end
end

eh = function(s)
	print(color.byString("RED") .. "An error occured!")
	print(s)
end

ev = function(eventId, message)
	print(eventId .. " - " .. message)
end

boot = function()
	_try_boot_custom = false
	_booting_custom = false

	if fs.exists(_curdir .. "startup.lua") then
		_try_boot_custom = true
	end

	if _try_boot_custom == true then
		print("Would you like to start startup.lua? (Y/N)")
		input = term.getInput()
		if input == "Y" then
			run(_curdir, "startup.lua")
			_booting_custom = true
		end
	end

	if _booting_custom == false then
		event.registerListener("rednet_receive", "ev")

		print(_os .. "\n")
		if _show_motd then
			motd()
			print(" ")	-- new line, looks better
		end
		pwd()
		shell()
	end
end

boot()