/*
 Author: Matt Hermes
   File: Classification.py
Version: 0.310
   Date: 02/08/20
   Desc: Defines the enumerations for the different classifications
         of moves:
         --PHYSICAL: Damage is based on the users Attack stat and the
           defending pokemon's Defense stat
         --SPECIAL: Damage is based on the users Special Attack stat and the
           defending pokemon's Special Defense stat
         --STATUS: Does not deal direct damage
     
Change(s):
   --None made
TODO:
   --Nothing   
*/

package pokemonBattle;

public enum Classification {
	PHYSICAL, SPECIAL, STATUS, NONE
}
