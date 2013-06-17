/*******************************************************************************
* Copyright (c) 2009 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package net.robbytu.computercraft.lib.spout;

import java.util.Random;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

/** 
 * Subclass of {@link LibFunction} which implements the lua standard {@code math} 
 * library. 
 * <p> 
 * It contains only the math library support that is possible on JME.  
 * For a more complete implementation based on math functions specific to JSE 
 * use {@link org.luaj.vm2.lib.jse.JseMathLib}. 
 * In Particular the following math functions are <b>not</b> implemented by this library:
 * <ul>
 * <li>acos</li>
 * <li>asin</li>
 * <li>atan</li>
 * <li>cosh</li>
 * <li>log</li>
 * <li>log10</li>
 * <li>sinh</li>
 * <li>tanh</li>
 * <li>atan2</li>
 * </ul>
 * <p>
 * The implementations of {@code exp()} and {@code pow()} are constructed by 
 * hand for JME, so will be slower and less accurate than when executed on the JSE platform.
 * <p> 
 * Typically, this library is included as part of a call to either 
 * {@link JmePlatform#standardGlobals()}
 * <p>
 * To instantiate and use it directly, 
 * link it into your globals table via {@link LuaValue#load(LuaValue)} using code such as:
 * <pre> {@code
 * LuaTable _G = new LuaTable();
 * LuaThread.setGlobals(_G);
 * _G.load(new BaseLib());
 * _G.load(new PackageLib());
 * _G.load(new MathLib());
 * System.out.println( _G.get("math").get("sqrt").call( LuaValue.valueOf(2) ) );
 * } </pre>
 * Doing so will ensure the library is properly initialized 
 * and loaded into the globals table. 
 * <p>
 * This has been implemented to match as closely as possible the behavior in the corresponding library in C.
 * @see LibFunction
 * @see JsePlatform
 * @see JmePlatform
 * @see JseMathLib
 * @see <a href="http://www.lua.org/manual/5.1/manual.html#5.6">http://www.lua.org/manual/5.1/manual.html#5.6</a>
 */
public class MathLib extends LuaLib {
	
	public static MathLib MATHLIB = null;

	private Random random;
	
	public MathLib() {
		super("math");
		MATHLIB = this;
	}


	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		LuaTable t = new LuaTable(0,30); // TODO optimize combined JseMathLib and MathLib 
		t.set( "pi", Math.PI );
		t.set( "huge", LuaDouble.POSINF );
		t.set("abs", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.abs(arg.checkdouble()));
			}
		});
		t.set("ceil", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.ceil(arg.checkdouble())); 
			}
		});
		t.set("cos", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.cos(arg.checkdouble()));
			}
		});
		t.set("deg", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.toDegrees(arg.checkdouble()));
			}
		});
		t.set("exp", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.exp(arg.checkdouble()));
			}
		});
		t.set("floor", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.floor(arg.checkdouble()));
			}
		});
		t.set("rad", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.toRadians(arg.checkdouble()));
			}
		});
		t.set("sin", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.sin(arg.checkdouble()));
			}
		});
		t.set("sqrt", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.sqrt(arg.checkdouble()));
			}
		});
		t.set("tan", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.tan(arg.checkdouble()));
			}
		});
		t.set("acos", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.acos(arg.checkdouble()));
			}
		});
		t.set("asin", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.asin(arg.checkdouble()));
			}
		});
		t.set("atan", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.atan(arg.checkdouble()));
			}
		});
		t.set("cosh", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.cosh(arg.checkdouble()));
			}
		});
		t.set("exp", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.exp(arg.checkdouble()));
			}
		});
		t.set("log", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.log(arg.checkdouble()));
			}
		});
		t.set("log10", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.log10(arg.checkdouble()));
			}
		});
		t.set("sinh", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.sinh(arg.checkdouble()));
			}
		});
		t.set("tanh", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(Math.tanh(arg.checkdouble()));
			}
		});
		
		t.set("fmod", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				double x = arg1.checkdouble();
				double y = arg2.checkdouble();
				double q = x/y;
				double f = x - y * (q>=0? Math.floor(q): Math.ceil(q));
				return valueOf( f );
			}
		});
		t.set("ldexp", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				double x = arg1.checkdouble();
				double y = arg2.checkdouble()+1023.5;
				long e = (long) ((0!=(1&((int)y)))? Math.floor(y): Math.ceil(y-1));
				return valueOf(x * Double.longBitsToDouble(e << 52));
			}
		});
		t.set("pow", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				return valueOf(Math.pow(arg1.checkdouble(), arg2.checkdouble()));
			}
		});
		t.set("atan2", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				return valueOf(Math.atan2(arg1.checkdouble(), arg2.checkdouble()));
			}
		});
		
		t.set("frexp", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				double x = args.checkdouble(1);
				if ( x == 0 ) return varargsOf(ZERO,ZERO);
				long bits = Double.doubleToLongBits( x );
				double m = ((bits & (~(-1L<<52))) + (1L<<52)) * ((bits >= 0)? (.5 / (1L<<52)): (-.5 / (1L<<52)));
				double e = (((int) (bits >> 52)) & 0x7ff) - 1022;
				return varargsOf( valueOf(m), valueOf(e) );
			}
		});
		t.set("max", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				double m = args.checkdouble(1);
				for ( int i=2,n=args.narg(); i<=n; ++i )
					m = Math.max(m,args.checkdouble(i));
				return valueOf(m);
			}
		});
		t.set("min", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				double m = args.checkdouble(1);
				for ( int i=2,n=args.narg(); i<=n; ++i )
					m = Math.min(m,args.checkdouble(i));
				return valueOf(m);
			}
		});
		t.set("modf", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				double x = args.checkdouble(1);
				double intPart = ( x > 0 ) ? Math.floor( x ) : Math.ceil( x );
				double fracPart = x - intPart;
				return varargsOf( valueOf(intPart), valueOf(fracPart) );
			}
		});
		t.set("randomseed", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				long seed = args.checklong(1);
				random = new Random(seed);
				return NONE;
			}
		});
		t.set("random", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				if ( random == null )
					random = new Random();
				
				switch ( args.narg() ) {
				case 0:
					return valueOf( random.nextDouble() );
				case 1: {
					int m = args.checkint(1);
					if (m<1) argerror(1, "interval is empty");
					return valueOf( 1 + random.nextInt(m) );
				}
				default: {
					int m = args.checkint(1);
					int n = args.checkint(2);
					if (n<m) argerror(2, "interval is empty");
					return valueOf( m + random.nextInt(n+1-m) );
				}
				}
			}
		});
		
		env.set("math", t);
		return t;
	}

	/** compute power using installed math library, or default if there is no math library installed */
	public static LuaValue dpow(double a, double b) {
		return LuaDouble.valueOf( 
				MATHLIB!=null?
				MATHLIB.dpow_lib(a,b):
				dpow_default(a,b) );
	}
	public static double dpow_d(double a, double b) {
		return MATHLIB!=null? 
				MATHLIB.dpow_lib(a,b): 
				dpow_default(a,b);
	}
	
	/** 
	 * Hook to override default dpow behavior with faster implementation.  
	 */
	public double dpow_lib(double a, double b) {
		return dpow_default(a,b);
	}

	/** 
	 * Default Spout version. 
	 */
	protected static double dpow_default(double a, double b) {
		return Math.pow(a, b);
	}

	public static final class JseMathLib2 extends TwoArgFunction {
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			switch ( opcode ) {
			case 0: return valueOf(Math.atan2(arg1.checkdouble(), arg2.checkdouble()));
			case 1: return valueOf(Math.pow(arg1.checkdouble(), arg2.checkdouble()));
			}
			return NIL;
		}
	}
}
