/*
 Author: Matt Hermes
   File: Type.py
Version: 0.310
   Date: 02/08/20
   Desc: Defines the enumerations for the move and pokemon types
         e.g., FIRE, WATER, etc.
     
Change(s):
   --None made
TODO:
   --Nothing   
*/

package pokemonBattle;

public enum Type {
	NORMAL(0), FIRE(1), WATER(2), GRASS(3), ELECTRIC(4),
	ICE(5), FIGHTING(6), POISON(7), GROUND(8), FLYING(9),
	PSYCHIC(10), BUG(11), ROCK(12), GHOST(13), DRAGON(14),
	DARK(15), STEEL(16), FAIRY(17), NONE(18);
	
	private final int value;
	
	private Type(int value) {
		this.value = value;
	}
	
	public final int getValue() {
		return this.value;
	}
}
