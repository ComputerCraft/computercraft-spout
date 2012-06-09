-- test.lua tests for all lua api functions

test = function() 
	print(color.RED .. "term.clear doesn't work")
	term.clear()
	print(color.GREEN .. "term.clear works if you don't see a line that tells you, that it doesn't work")
	-- Can we print?
	print(color.GREEN .. "print works")
	write(color.GREEN .. "write works")
	writeline(color.GREEN .. "writeline works")
	-- run("/", "testrun.lua") currently commented out as of bug #117
	
	if (color.byString("BLACK") == color.BLACK) then
		print(color.GREEN .. "colors work")
	else
		print(color.RED .. "colors don't work");
	end

	print("term.setInputTip works if you see \"Please type something here\" in the input field")
	term.setInputTip("Please type something here")
	print("term.setInputPasswordField works if your input is masqueraded by asterisk")
	term.setInputPasswordField(true)
	print("Testing terminal input field:")
	print("Type something into the input field")
	input = term.getInput()
	print(color.GREEN .. "term.getInput works, you typed: " .. input)
	term.setInputPasswordField(false)

	if fs.separator then
		print(color.GREEN .. "fs.separator works, set to " .. fs.separator) 
	else
		print(color.RED .. "fs.separator is not set")
	end

	if (not fs.isDir(fs.separator)) then
		print(color.RED .. "fs.isDir doesn't work as it can't find the root directory")
	elseif (fs.isDir(fs.separator .. "..")) then
		print(color.RED .. "fs.isDir doesn't work as it finds parent of root directory")
	else
		print(color.GREEN .. "fs.isDir seems to work")
	end
	
	if (fs.makeDir(fs.separator .. "testdir")) then
		if (fs.isDir(fs.separator .. "testdir")) then 
			print(color.GREEN .. "fs.makeDir works")
		else
			print(color.RED .. "fs.isDir or fs.makeDir doesn't work, fs.makeDir tells success, fs.isDir doesn't")
		end
	else
		print(color.RED .. "fs.makeDir doesn't work, failed in creating \"testdir\"")
	end
	
	if ((fs.combine("somepath", "otherpath") ~= "somepath" .. fs.separator .. "otherpath") or
	   (fs.combine("cool1" .. fs.separator, fs.separator .. "cool2") ~= "cool1" .. fs.separator .. "cool2") or
	   (fs.combine("cool3", fs.separator .. "cool4") ~= "cool3" .. fs.separator .. "cool4") or
	   (fs.combine("cool4" .. fs.separator, "cool5") ~= "cool4" .. fs.separator .. "cool5")) then
		print(color.RED .. "fs.combine doesn't work")
	else
		print(color.GREEN .. "fs.combine works")
	end
	
	if ((not fs.exists(fs.separator .. "testdir")) or fs.exists(fs.separator .. "someunbelievabledir")) then
		print(color.RED .. "fs.exists doesn't work")
	else
		print(color.GREEN .. "fs.exists works")
	end
	
	if (fs.isDir(fs.separator .. "testdir")) then
		local result = fs.delete(fs.separator .. "testdir")
		if result ~= "RM_DIR_OK" then
			print(color.RED .. "fs.delete doesn't work: " .. result)
		elseif fs.isDir(fs.separator .. "testdir") then
			print(color.RED .. "fs.delete or fs.isDir doesn't work, fs.delete called successfull but fs.isDir is true")
		else
			print(color.GREEN .. "fs.delete work")
		end
	else
		print(color.RED .. "Can't test fs.delete: no directory to delete")
	end
	
	print("Waiting for OK")
	term.setInputTip("Press OK")
	term.getInput()
	fs.printList(fs.separator)
	print("if you see a directory listing with testdir and rom content, fs.printList works")
end

test()