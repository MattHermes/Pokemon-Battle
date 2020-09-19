/*
   Author: Matt Hermes
     File: Move.java
  Version: 0.310
     Date: 02/08/20
     Desc: Defines a move that can be used by a pokemon
Change(s):
   --Now implements Cloneable
   
     TODO:
      --Nothing
*/

package pokemonBattle;

import java.util.*;

public class Move implements Cloneable{
	private String name;
	private Type moveType;
    private Classification classification;
	private int basePower;
	private int maxPP;
	private int pp;
	private int accuracy;
	private int priority;
	private double critChance;
	private List<String> effects;
	
	
	/*
	    Parameters:        
        --moveType: What the move's type is (e.g., FIRE, WATER, GRASS, etc.)
        --classification: determines how the move interacts with the defending pokemon (see classifications.py for more details)
        --basePower: The higher the base power the more damage the move does (not used for fixed damage moves and non-attack moves)
        --maxPP: The max number of times a move can be used
        --accuracy: The chance that the move will hit
        --priority: A move with a higher priority will be executed before a move with a lower priority
        --critChance: The chance that a move will get a Critical Hit and deal more damage
        --effects: A list of secondary effects the move may have beyond dealing damage

        Desc/Purpose:
        --Constructor for the Move Class
        
        Returns:
        --N/A
	 */
	public Move(String name, Type moveType, Classification classification, 
			int basePower, int maxPP, int accuracy, int priority,
			double critChance, List<String> effects) {
		this.name = name;
		this.moveType = moveType;
		this.classification = classification;
		this.basePower = basePower;
		this.maxPP = maxPP;
		this.pp = maxPP;
		this.accuracy = accuracy;
		this.priority = priority;
		this.critChance = critChance;
		this.effects = effects;		
	} // Constructor
	
	/*
	  (non-Javadoc)
	  @see java.lang.Object#clone()
	  
	  	Parameters:
	  	--N/A
	  	
	  	Desc/Purpose:
	  	--To allow us to do a deep copy of a move object
	  	
	  	Returns:
	  	--The cloned pokemon
	*/
	protected Object clone() throws CloneNotSupportedException{
		Move move = (Move) super.clone();
		return move;
	} // clone()
	
	/*
    	Parameters:    	
    	--pp: The number of uses a move has left

    	Desc/Purpose:
    	--Set the remaining power points (PP) of the move
    
    	Returns:
    	--N/A
    */
	public void setPP(int pp) {
		this.pp = pp;
	} // setPP(int pp)
	
	/*
    Parameters:
    --N/A    

    Desc/Purpose:
    --The name is used so that the player knows what move they are
      selecting
    
    Returns:
    --The move's name
   */
	public String getMoveName() {
		return this.name;
	} // getMoveName()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--The moves type is needed so that we determine the effectiveness
      	of a move and if we need to apply a STAB bonus to moves with a
      	base power > 0
    
    	Returns:
    	--The move's type
	*/
	public Type getMoveType() {
		return this.moveType;
	} // getMoveType()
	
	/*
    Parameters:
    --N/A        

    Desc/Purpose:
    --The base power is used in damage calculations
    
    Returns:
    --The move's name
    */
	public int getBasePower() {
		return this.basePower;
	} // getBasePower()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--The classification is used to determine how the move functions
    
    	Returns:
    	--The move's classification
    */
	public Classification getClassification() {
		return this.classification;
	} // getClassification()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to get the move's current PP so that the player knows
      	how many uses a move has left
    
    	Returns:
    	--The move's remaining power points(PP)
    */
	public int getPP() {
		return this.pp;
	} // getPP()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to get the move's max PP so that the player knows
      	how many uses a move has in total and so that we can reset
      	a move's PP if necessary
    
    	Returns:
    	--The move's max power points(PP)
	*/
	public int getMaxPP() {
		return this.maxPP;
	} // maxPP()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to get the move's accuracy to determine if the move
      	will hit the defending pokemon
    
      Returns:
    	--The move's accuracy
    */
	public int getAccuracy() {
		return this.accuracy;
	} // getAccuracy()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to get the move's priority to determine which pokemon
      	uses its move first
    
      	Returns:
    	--The move's priority
    */
	public int getPriority() {
		return this.priority;		
	} // getPriority()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to get the move's crit chance to determine if an attack
      	should get a bonus to its damage
    
      	Returns:
    	--The move's crit chance
    */
    public double getCritChance() {
		return this.critChance;
	} // getCritChance()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to get the move's effects to determine what, if any,
      	additional effect a move does beyond dealing damage
    
      	Returns:
    	--The move's effects
    */
    public List<String> getEffects(){
    	return this.effects;
    } // getEffects()	
} // Class