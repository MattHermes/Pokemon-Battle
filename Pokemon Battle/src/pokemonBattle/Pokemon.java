/*
   Author: Matt Hermes
     File: Pokemon.java
   Version: 0.310
      Date: 02/08/19
      Desc: Defines a pokemon that can be used by a player
 Change(s):
 --None made
 
      TODO:
      --Nothing
 */

package pokemonBattle;
import java.util.*;

public class Pokemon implements Cloneable{
	private String pokeID;
	private String pokedexNumber;
	private String name;
	private int level;
	private Gender gender;
	private Type primaryType;
	private Type secondaryType;
	private List<Move> moveset;
	private int maxHP;
	private int baseAtk;
	private int baseDef;
	private int baseSpAtk;
	private int baseSpDef;
	private int baseSpeed;
	private int baseAccuracy;
	private int baseEvasion;
	
	private int hp;
	private int atk;
	private int def;
	private int spAtk;
	private int spDef;
	private int speed;
	private int accuracy;
	private int evasion;
	
	private int MAX_NUM_VOLATILE_STATUSES = 3; // A pokemon can have multiple volatile statuses
	private NonVolatileStatus nonVolatileStatus;
	private ArrayList<VolatileStatus> volatileStatuses = new ArrayList<VolatileStatus>();
	private int confusionTurns;
	private int sleepTurns;
	private int badlyPoisonedTurns;
	
	private int BASE_STAGE = 0; // "0" is the default value for a Stage meaning that there is a x1 multiplier for the stat
	private int atkStage;
	private int defStage;
	private int spAtkStage;
	private int spDefStage;
	private int speedStage;
	private int accuracyStage;
	private int evasionStage;	
	
	private String backupPokedexNumber;
	private String backupName;
	private Gender backupGender;
	private Type backupPrimaryType;
	private Type backupSecondaryType;
	private List<Move> backupMoveset;
	
	private int recentDamageTaken;
	private Classification hitByClassification;
	
	/*
	    Parameters:
        --this: A reference to a newly created object
        --pokedexNumber: Where the pokemon appears in the pokedex
        --name: The name of the pokemon
        --level: Used to determine damage in an attacking move
        --primaryType: The pokemon's main type (used when determining move effectives and
          if a move gets a Same Type Attack Bonus (STAB))
        --secondaryType: This is used when a pokemon has two types (same usage as the primary type)
        --moveset: A list of 4 moves that the pokemon can use
        --maxHP: The max hit points (HP) that the pokemon has
        --baseAtk: The pokemon's base attack (Atk) before any buffs/debuffs (remains static)
        --baseDef: The pokemon's base defense (Def) before any buffs/debuffs (remains static)
        --baseSpAtk: The pokemon's base special attack (SpAtk) before any buffs/debuffs (remains static)
        --baseSpDef: The pokemon's base special defense (SpDef) before any buffs/debuffs (remains static)
        --baseSpeed: The pokemon's base speed before any buffs/debuffs (remains static)

        Desc/Purpose:
        --Constructor for the Pokemon Class
        
        Returns:
        --N/A

        Note: Whenever stats, besides HP, a buffed/debuffed, we leave the base stats static and
              change the Atk, Def, SpAtk, SpDef, and Speed depending on the move used. This is
              useful because when a pokemon swaps out, we need to reset its stats which is why
              we have different variables for the base stats and the stats a pokemon has while
              it's out in battle.
	 */
	public Pokemon(String pokedexNumber, String name, int level, 
			Gender gender, Type primaryType, Type secondaryType, 
			List<Move> moveset, int maxHP, int baseAtk,
			int baseDef, int baseSpAtk, int baseSpDef, 
			int baseSpeed) {
		this.pokeID = ""; // pokeID: Used to indicate who the owner is and where in a player's team it is
                          // --Ex: pokeID = "11"
                          //   --The first digit indicates it belongs to player one
                          //     and is at index "1" in the pokemon team
		
		this.pokedexNumber = pokedexNumber;
		this.name = name;
		this.level = level;
		this.gender = gender;
		this.primaryType = primaryType;
		this.secondaryType = secondaryType;
		this.moveset = moveset;
		
		// Base stats
		this.maxHP = maxHP;
		this.baseAtk = baseAtk;
		this.baseDef = baseDef;
		this.baseSpAtk = baseSpAtk;
		this.baseSpDef = baseSpDef;
		this.baseSpeed = baseSpeed;
		this.baseAccuracy = 1;
	    this.baseEvasion = 1;
		
	    // Stats (change throughout the battle)
		this.hp = maxHP;        // HP determines the remaining hit points (HP) a pokemon has
		this.atk = baseAtk;     // Atk determines how much damage is dealt by physical attacks
		this.def = baseDef;     // Def determines how much damage is taken by physical attacks
		this.spAtk = baseSpAtk; // SpAtk determines how much damage is dealt by special attacks
		this.spDef = baseSpDef; // SpDef determines how much damage is taken by special attacks
		this.speed = baseSpeed; // Speed determines how fast a pokemon is (the higher the speed, the more
                                //    the more like a pokemon is to go first)
		this.accuracy = 1;      // Accuracy determines how likely the pokemon's move is to hit
		this.evasion = 1;       // Evasion determines how likely the opponent's move is to hit
		
		// Stages (used to determine the multipliers applied to each stat except HP)
		this.atkStage = BASE_STAGE;
		this.defStage = BASE_STAGE;
		this.spAtkStage = BASE_STAGE;
		this.spDefStage = BASE_STAGE;
		this.speedStage = BASE_STAGE;
		this.accuracyStage = BASE_STAGE;
		this.evasionStage = BASE_STAGE;
		
		// Statuses
		this.nonVolatileStatus = NonVolatileStatus.NONE; // A pokemon can only have one non-volatile status
		for (int i = 0; i < MAX_NUM_VOLATILE_STATUSES; i++) {
			volatileStatuses.add(VolatileStatus.NONE);
		}
		confusionTurns = 0;    // Number of turns the pokemon will be confused
		sleepTurns = 0;        // Number of turns the pokemon will be asleep 
		                       //    (this will only decrease every turn the pokemon is out)
		badlyPoisonedTurns = 0;  // Number of turns the pokemon has been out while it is badly poisoned
                               //    (this gets reset when the pokemon is swapped out)
		
		// Allows us to restore information after a
        // pokemon that uses a copying move is swapped
        // out
	    this.backupPokedexNumber = pokedexNumber;
		this.backupName = name;
		this.backupGender = gender;
		this.backupPrimaryType = primaryType;
		this.backupSecondaryType = secondaryType;
		this.backupMoveset = new ArrayList<Move>();		
	} // Constructor
	
	/*
	  (non-Javadoc)
	  @see java.lang.Object#clone()
	  
	  	Parameters:
	  	--N/A
	  	
	  	Desc/Purpose:
	  	--To allow us to do a deep copy of a pokemon object
	  	
	  	Returns:
	  	--The cloned pokemon
	*/
	protected Object clone() throws CloneNotSupportedException{
		Pokemon pokemon = (Pokemon) super.clone();
		return pokemon;
	} // clone()
	
	/*
    	Parameters:
        --pokeID: The ID for the pokemon

    	Desc/Purpose:
    	--Set the pokeID for the pokemon
    
    	Returns:
    	--N/A
    */
	public void setPokeID(String pokeID) {
		this.pokeID = pokeID;
	} // setPokeID(String pokeID)
	
	/*
    	Parameters:    
    	--hp: The amount of hit points (HP) a pokemon has remaining

    	Desc/Purpose:
    	--Set the remaining HP of the pokemon
    
    	Returns:
    	--N/A
    */
	public void setHP(int hp) {
		this.hp = hp;
	} // setHP(int hp)
	
	/*
		Parameters:   
		--atkStage: What the pokemon's Attack Stage should be changed to

		Desc/Purpose:
		--This is used to change the Attack Stage when the
		  Attack stat is buffed/debuffed

		Returns:
		--N/A
	 */
	public void setAtkStage(int atkStage) {
		this.atkStage = atkStage;
	} // setAtkStage(int atkStage)

	/*
		Parameters:   
		--defStage: What the pokemon's Defense Stage should be changed to

		Desc/Purpose:
		--This is used to change the Defense Stage when the
	  	Defense stat is buffed/debuffed

		Returns:
		--N/A
	 */
	public void setDefStage(int defStage) {
		this.defStage = defStage;
	} // setDefStage(int defStage)
	/*
		Parameters:   
		--spAtkStage: What the pokemon's Special Attack Stage should be changed to

		Desc/Purpose:
		--This is used to change the Special Attack Stage when the
  		  Special Attack stat is buffed/debuffed

		Returns:
		--N/A
	*/
	public void setSpAtkStage(int spAtkStage) {
		this.spAtkStage = spAtkStage;
	} // setAtkStage(int spAtkStage)
	
	/*
		Parameters:   
		--spDefStage: What the pokemon's Special Defense Stage should be changed to

		Desc/Purpose:
		--This is used to change the Special Defense Stage when the
  	      Special Defense stat is buffed/debuffed

		Returns:
	--N/A
	 */
	public void setSpDefStage(int spDefStage) {
		this.spDefStage = spDefStage;
	} // setDefStage(int spDefStage)
	
	/*
    	Parameters:
      	--speedStage: What the pokemon's Speed Stage should be changed to

    	Desc/Purpose:
    	--This is used to change the Speed Stage when the
      	  Speed stat is buffed/debuffed
    
    	Returns:
    	--N/A
	*/
	public void setSpeedStage(int speedStage) {
		this.speedStage = speedStage;
	} // setSpeedStage(int speedStage)
	
	/*
    	Parameters:
        --accuracyStage: What the pokemon's Accuracy Stage should be changed to

    	Desc/Purpose:
    	--This is used to change the Accuracy Stage when the
      	  pokemon's accuracy is buffed/debuffed
    
    	Returns:
    	--N/A
    */
	public void setAccuracyStage(int accuracyStage) {
		this.accuracyStage = accuracyStage;
	} // setAccuracyStage(int accuracyStage)
	
	/*
    	Parameters:
      	--evasionStage: What the pokemon's Evasion Stage should be changed to

    	Desc/Purpose:
    	--This is used to change the Evasion Stage when the
      	  pokemon's evasion is buffed/debuffed
    
    	Returns:
    	--N/A
    */
	public void setEvasionStage(int evasionStage) {
		this.evasionStage = evasionStage;
	} // setEvasionStage(int evasionStage)
	
	/*
		Parameters:
  		--moveset: What the pokemon's moveset should be changed to

		Desc/Purpose:
		--This is used to update the PP usage of a move after
	      a pokemon's turn

		Returns:
		--N/A
	 */
	public void setMoveset(List<Move> moveset) {
		this.moveset = moveset;
	} // setMoveset(ArrayList<Move> moveset)
	
	/*
    	Parameters:
       	--status: The non-volatile status effect to apply to the pokemon

    	Desc/Purpose:
    	--This is used to change the non-volatile status that a pokemon
      	  has
      	--Note: This should only be changed when a pokemon does not currently
          have a non-volatile status or if we are resetting it back to NONE
    
        Returns:
    	--N/A
    */
	public void setNonVolatileStatus(NonVolatileStatus status) {
		if (this.nonVolatileStatus == NonVolatileStatus.NONE) {
			this.nonVolatileStatus = status; 
		}
		
		else if (status == NonVolatileStatus.NONE) {
			this.nonVolatileStatus = status; 
		}
	} // setNonVolatileStatus(NonVolatileStatus status)
	
	/*
    	Parameters:
        --status: The non-volatile status effect to apply to the pokemon

    	Desc/Purpose:
    	--This is used to change the non-volatile status that a pokemon
          has
      	--Note: This should only be changed when a pokemon does not currently
          have a non-volatile status or if we are resetting it back to NONE
    
    	Returns:
    	--N/A
    */
	public void setVolatileStatus(VolatileStatus status) {
		for (int i = 0; i < MAX_NUM_VOLATILE_STATUSES; i++) {
			if (this.volatileStatuses.get(i) == VolatileStatus.NONE) {
				if (!this.volatileStatuses.contains(status)) {
					this.volatileStatuses.set(i, status); // Set the status at index 'i'
				}
			}
		}
	} // setVolatileStatus(VolatileStatus status)
	
	/*
    	Parameters:
        --sleepTurns: The number of turns a pokemon will be asleep

    	Desc/Purpose:
    	--This is used to set the number of turns that the pokemon will
          be affected by the Sleep non-volatile status
              
    	Returns:
    	--N/A
    */
	public void setSleepTurns(int sleepTurns) {
		this.sleepTurns = sleepTurns;
	} // setSleepTurns(int sleepTurns)
	
	/*
    	Parameters:
       	--badlyPoisonedTurns: Number of turns the pokemon has been out
    	  while it is badly poisoned

      	Desc/Purpose:
    	--This is used to set the number of turns that the pokemon has
      	  been out while it is badly poisoned
              
      	Returns:
    	--N/A
    */
	public void setBadlyPoisonedTurns(int badlyPoisonedTurns) {
		this.badlyPoisonedTurns = badlyPoisonedTurns;
	} // setBadlyPoisonedTurns(int badlyPoisonedTurns)
	
	/*
    	Parameters:
       	--confusionTurns: The number of turns a pokemon will be confused

    	Desc/Purpose:
    	--This is used to set the number of turns that the pokemon has
      	  been out while it is badly poisoned
              
      	Returns:
    	--N/A
    */
	public void setConfusionTurns(int confusionTurns) {
		this.confusionTurns = confusionTurns;
	} // setConfusionTurns(int confusionTurns)
	
	/*
    	Parameters:
        --classification: The classification when of the move that
      	  most recently hit the pokemon

      	Desc/Purpose:
    	--This is used to set classification when of the move that
      	  most recently hit the pokemon
              
      	Returns:
    	--N/A
    */
	public void setHitByClassification(Classification classification) {
		this.hitByClassification = classification;
	} // setHitByClassification(Classification classification)
	
	/*
    	Parameters:
    	--damage: The damage taken by the move that
          most recently hit the pokemon

    	Desc/Purpose:
    	--This is used to set the damage taken from the move that
          most recently hit the pokemon
              
    	Returns:
    	--N/A
    */
	public void setRecentDamageTaken(int damage) {
		this.recentDamageTaken = damage;
	} // setRecentDamageTaken(int damage)
	
	/*
     	Parameters:
       	--N/A        

       	Desc/Purpose:
       	--We need the pokemon's name so that the player knows what pokemon he/she is using
    
       	Returns:
       	--The pokemon's name
    */
	public String getPokeID() {
		return pokeID;
	} // getPokeID()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's name so that the player knows what pokemon he/she is using
    
    	Returns:
    	--The pokemon's name
	*/
	public String getPokeName() {
		return name;
	} // getPokeName()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's pokedex number so that we can use it to help
      	  find the appropriate pokemon when allowing players to build their
          team
    
    	Returns:
    	--The pokemon's pokedex number
    */
	public String getPokedexNumber() {
		return this.pokedexNumber;
	} // getPokedexNumber()
	
	/*
    	Parameters:
        --N/A

    	Desc/Purpose:
    	--We need the pokemon's level so that we can use it in damage calculations
    
    	Returns:
    	--The pokemon's level
    */
	public int getLevel() {
		return this.level;
	} // getLevel()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's gender so that we can use it when dealing
          with the Attraction volatile status effect
    
    	Returns:
    	--The pokemon's gender
    */
	public Gender getGender() {
		return this.gender;
	} // getGender()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's HP so that the player knows how much HP the pokemon
          has left
    
    	Returns:
    	--The pokemon's remaining HP
    */
	public int getHP() {
		return this.hp;
	} // getHP()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's max HP so that the player knows how much HP the pokemon
          can have
    
    	Returns:
    	--The pokemon's max HP
    */
	public int getMaxHP() {
		return this.maxHP;
	} // getMaxHP()
	
	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's Atk to determine how much damage is dealt by
      	  physical attacks
    
    	Returns:
    	--The pokemon's Atk
	*/
	public int getAtk() {
		return this.atk;
	} // getAtk()		

	/*
    	Parameters:
    	--N/A       

    	Desc/Purpose:
    	--We need the pokemon's Def to determine how much damage is taken by
          physical attacks
    
    	Returns:
    	--The pokemon's Def
	*/
	public int getDef() {
		return this.def;
	} // getDef()		

	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's SpAtk to determine how much damage is dealt by
          physical attacks
    
    	Returns:
    	--The pokemon's SpAtk
	*/ 
	public int getSpAtk() {
		return this.spAtk;
	} // getSpAtk()		

	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's SpDef to determine how much damage
          is taken by special attacks
    
    	Returns:
    	--The pokemon's SpDef
	*/
	public int getSpDef() {
		return this.spDef;
	} // getSpDef()
		

	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's Speed to determine if it will use its move first
    
    	Returns:
    	--The pokemon's Speed
	*/
	public int getSpeed() {
		return this.speed;
	} // getSpeed()    

	/*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's Accuracy to determine how likely its
          move is to hit
    
    	Returns:
    	--The pokemon's Accuracy
	*/
	public int getAccuracy() {
		return this.accuracy;
	} // getAccuracy()    
	
    /*
     	Parameters:
        --N/A       

        Desc/Purpose:
        --We need the pokemon's Evasion to determine how likely the
          opponent's move is to hit
            
        Returns:
        --The pokemon's Evasion
    */
    public int getEvasion() {
    	  return this.evasion;
    } // getEvasion()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--We need to get the Attack modifier to calculate damage dealt by
          physical attacks and determine if the modifier can be changed
    
    	Returns:
    	--The pokemon's Attack Modifier
    */
    public int getAtkStage() {
    	return this.atkStage;
    } // getAtkStage()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--We need to get the Defense modifier to calculate damage taken by
      	physical attacks and determine if the modifier can be changed
    
    	Returns:
    	--The pokemon's Defense Modifier
    */
    public int getDefStage() {
    	return this.defStage;
    } // getDefStage()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--We need the pokemon's Special Attack modifier to calculate damage dealt by
          special attacks and determine if the modifier can be changed
    
    	Returns:
    	--The pokemon's Special Attack Modifier
    */
    public int getSpAtkStage() {
    	return this.spAtkStage;
    } // getSpAtkStage()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--We need the pokemon's Special Defense modifier to calculate damage taken by
      	  special attacks and determine if the modifier can be changed
    
      	Returns:
    	--The pokemon's Special Defense Modifier
    */
    public int getSpDefStage() {
    	return this.spDefStage;
    } // getSpDefStage()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--We need the pokemon's Speed modifier to determine which pokemon
          makes the first move
    
    	Returns:
    	--The pokemon's Speed modifier
    */
    public int getSpeedStage() {
    	return this.speedStage;
    } // getSpeedStage()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--We need the pokemon's Accuracy modifier to determine how
          likely the pokemon's move is to hit
    
    	Returns:
    	--The pokemon's Accuracy modifier
	*/
    public int getAccuracyStage() {
    	return this.accuracyStage;
    }
    
    /*
    	Parameters:
    	--this: A reference to the instance whose method was called

    	Desc/Purpose:
    	--We need the pokemon's Evasion modifier to determine how
          likely the opponent's move is to hit
    
    	Returns:
    	--The pokemon's Evasion Stage
    */
    public int getEvasionStage() {
    	return this.evasionStage;
    }// getEvasionStage()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's primary type to determine a move's effectives and
          if a move gets a Same Type Attack Bonus (STAB)
    
    	Returns:
    	--The pokemon's primary type
    */
    public Type getPrimaryType() {
    	return this.primaryType;
    } // getPrimaryType()
    
    /*
		Parameters:
		--N/A        

		Desc/Purpose:
		--We need the pokemon's secondary type to determine a move's effectives and
          if a move gets a Same Type Attack Bonus (STAB)

		Returns:
		--The pokemon's primary type
     */
    public Type getSecondaryType() {
    	return this.secondaryType;
    } // getSecondaryType()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need the pokemon's moveset so that the player can select a pokemon's move
    
    	Returns:
    	--The pokemon's moveset
    */
    public List<Move> getMoveset(){
    	return this.moveset;
    } // getMoveset()
    
    /*
    	Parameters:
        --N/A        

    	Desc/Purpose:
    	--We need to know what non-volatile status a pokemon has to
          determine the following:
      	  --Should the pokemon's attack be reduced (Burn)
          --Should the pokemon's speed be reduced (Paralysis)
          --Should the pokemon take damage at the end of the turn
            (Burn, Poison, Badly Poison)
          --Can the pokemon attack (Freeze, Sleep)
    
    	Returns:
    	--The pokemon's non-volatile status effect
    */
    public NonVolatileStatus getNonVolatileStatus() {
    	return this.nonVolatileStatus;
    } // getNonVolatileStatus()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to know what volatile status(es) a pokemon has to
          determine the following:
          --Can the pokemon attack (Confusion, Infatuation)
          --Should the pokemon take damage if it fails to attack
           (Confusion)
          --Did the pokemon fail to use a move (Flinch)
    
    	Returns:
    --The pokemon's volatile status effects
    */
    public ArrayList<VolatileStatus> getVolatileStatuses(){
    	return this.volatileStatuses;
    } // getVolatileStatuses()
    
    /*
    	Parameters:
    	--N/A      

    	Desc/Purpose:
    	--We need to know the number of turns a pokemon is affected by
          the Sleep non-volatile status affect so that we can determine
      	  if the status should be removed
    
    	Returns:
    	--The number of turns a pokemon will be affected by
      	  the Sleep non-volatile status effect
    */
    public int getSleepTurns() {
    	return this.sleepTurns;
    } // getSleepTurns()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to know the number of turns a pokemon has been out
          while it is badly poisoned so that we can calculate the amount
          of damage it should take at the end of the turn
    
    	Returns:
    	--The number of turns a pokemon has been out
          while it is badly poisoned
	*/
    public int getBadlyPoisonedTurns() {
    	return this.badlyPoisonedTurns;
    } // getBadlyPoisonedTurns()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to know the number of turns a pokemon is affected by
          the Confusion volatile status affect so that we can determine
          if the status should be removed
    
    	Returns:
        --The number of turns a pokemon will be affected by the
      	  Confusion volatile status effect
    */
    public int getConfusionTurns() {
    	return this.confusionTurns;
    } // getConfusionTurns()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to know the classification of the move that most
          recently hit the pokemon because moves like "Counter" and
      	  "Mirror Coat" only work if the pokemon is hit with the
      	  correct classification (i.e., PHYSICAL and SPECIAL).
    
    	Returns:
    	--The classification of the move that most
          recently hit the pokemon
	*/
    public Classification getHitByClassification() {
    	return this.hitByClassification;
    } // getHitByClassification()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--We need to know the damage done by the move that most
          recently hit the pokemon because moves like "Counter" and
      	  "Mirror Coat" multiply the damage taken by the move.
    
    	Returns:
    	--The damage done by the move that most
          recently hit the pokemon
    */
    public int getRecentDamageTaken() {
    	return this.recentDamageTaken;
    } // getRecentDamageTaken()
    
    /*
    	Parameters:    
    	--defendingPoke: The pokemon that is being copied    	          

    	Desc/Purpose:
    	--Copies the following:
      	  --Name,
          --Pokedex #,
          --Gender,
          --Primary and secondary types,
          --Stats,
          --Stages, and
          --Moveset
      	  of the defending pokemon
    
    	Returns:
    	--N/A
	*/
    public void copyPokemon(Pokemon defendingPoke) {    	
    	copyGeneralInfo(defendingPoke);   	
    	copyStats(defendingPoke);
    	copyStages(defendingPoke);
    	this.backupMoveset = this.moveset;
    	this.moveset = copyMoveset(defendingPoke);    	
    } // copyPokemon(Pokemon defendingPoke)
    
    /*
		Parameters:    
		--defendingPoke: The pokemon that is being copied
	
		Desc/Purpose:
		--Copies the following:
  	  	  --Name,
       	  --Pokedex #,
          --Gender, and
          --Primary and secondary types
  	  	of the defending pokemon

		Returns:
		--N/A
     */
    public void copyGeneralInfo(Pokemon defendingPoke) {
    	this.name = defendingPoke.getPokeName();
    	this.pokedexNumber = defendingPoke.getPokedexNumber();
    	this.gender = defendingPoke.getGender();
    	this.primaryType = defendingPoke.getPrimaryType();
    	this.secondaryType = defendingPoke.getSecondaryType();
    } // copyGeneralInfo(Pokemon defendingPoke)
    
    /*
		Parameters:    
		--defendingPoke: The pokemon that is being copied    	          

		Desc/Purpose:
		--Copies the stats of the defending pokemon

		Returns:
		--N/A
    */
    public void copyStats(Pokemon defendingPoke) {
    	this.atk = defendingPoke.getAtk();
    	this.def = defendingPoke.getDef();
    	this.spAtk = defendingPoke.getSpAtk();
    	this.spDef = defendingPoke.getSpDef();
    	this.speed = defendingPoke.getSpeed();
    	this.accuracy = defendingPoke.getAccuracy();
    	this.evasion = defendingPoke.getEvasion();
    } // copyStats(Pokemon defendingPoke)
    
    /*
    	Parameters:    
    	--defendingPokemon: The pokemon whose stages are being copied          

    	Desc/Purpose:
    	--Copies the stat stages of the defending pokemon
    
    	Returns:
    	--N/A
	*/
    public void copyStages(Pokemon defendingPoke) {
    	this.atkStage = defendingPoke.getAtkStage();
    	this.defStage = defendingPoke.getDefStage();
    	this.spAtkStage = defendingPoke.getSpAtkStage();
    	this.spDefStage = defendingPoke.getSpDefStage();
    	this.speedStage = defendingPoke.getSpeedStage();
    	this.accuracyStage = defendingPoke.getAccuracyStage();
    	this.evasionStage = defendingPoke.getEvasionStage();
    } // copyStages(Pokemon defendingPoke)
    
    /*
    	Parameters:
        --defMoveset: The defending pokemon's moveset          

    	Desc/Purpose:
    	--Copies the moveset of the defending pokemon where
      	  each move only has five PP
    
    	Returns:
    	--The new moveset
	*/
    public ArrayList<Move> copyMoveset(Pokemon defendingPoke){
    	List<Move> defMoveset = defendingPoke.getMoveset();
    	ArrayList<Move> newMoveset = new ArrayList<Move>();
    	int numMoves = defMoveset.size();
    	String moveName;
    	Type moveType;
    	Classification classification;
    	int basePower;
    	int MAX_PP = 5; // Copied moves only have five PP
    	int accuracy;
    	int priority;
    	double critChance;
    	List<String> effects = new ArrayList<String>();
    	Move copyOfMove;
    	
    	for (int i = 0; i < numMoves; i++) {
    		if (defMoveset.get(i) != null) {
    			moveName = defMoveset.get(i).getMoveName();
    			moveType = defMoveset.get(i).getMoveType();
    	        classification = defMoveset.get(i).getClassification();
    	        basePower = defMoveset.get(i).getBasePower();
    	        accuracy = defMoveset.get(i).getAccuracy();
    	        priority = defMoveset.get(i).getPriority();
    	        critChance = defMoveset.get(i).getCritChance();               
    	        effects = defMoveset.get(i).getEffects();
    	        
    	        copyOfMove = new Move(moveName, moveType, classification, basePower, MAX_PP, accuracy, priority, critChance, effects);
    			newMoveset.add(copyOfMove);
    		}
    	}    	
    	
    	return newMoveset;
    } // copyMoveset(Pokemon defendingPoke)
    
    /*
    	Parameters:
    	--N/A       

    	Desc/Purpose:
    	--To remove the Attraction volatile status when one of the
      	  following happens:
      	--The player with the Attraction status swaps out
      	--The pokemon that attracted its opponent swaps out
      	--The pokemon that attracted its opponent faints
    
    	Returns:
    	--N/A
	*/
    public void resetAttraction() {
    	for(int i = 0; i < this.MAX_NUM_VOLATILE_STATUSES; i++) {
    		if (this.volatileStatuses.get(i) == VolatileStatus.ATTRACTION) {
    			this.volatileStatuses.set(i, VolatileStatus.NONE);
    			return;
    		}
    	}
    } // resetAttraction()
    
    /*
    	Parameters:
    	--N/A    

    	Desc/Purpose:
    	--To remove the Confusion volatile status when one of the
      	  following happens:
      	  --The player with the Confusion status swaps out
      	  --The confusion turns are reduced to zero (0)
    
    	Returns:
    	--N/A
	*/
    public void resetConfusion() {
    	for(int i = 0; i < this.MAX_NUM_VOLATILE_STATUSES; i++) {
    		if (this.volatileStatuses.get(i) == VolatileStatus.CONFUSION) {
    			this.volatileStatuses.set(i, VolatileStatus.NONE);
    			return;
    		}
    	}
    } // resetConfusion()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--The flinched status will be removed on the for the next
      	  turn
    
    	Returns:
    	--N/A
	*/
    public void resetFlinch() {
    	for(int i = 0; i < this.MAX_NUM_VOLATILE_STATUSES; i++) {
    		if (this.volatileStatuses.get(i) == VolatileStatus.FLINCH) {
    			this.volatileStatuses.set(i, VolatileStatus.NONE);
    			return;
    		}
    	}
    } // resetFlinch()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--To remove all volatile status effects when the pokemon swaps
      	  out
    
    	Returns:
    	--N/A
    */
    public void resetVolatileStatuses() {
    	resetAttraction();
    	resetConfusion();
    	resetFlinch();
    } // resetVolatileStatuses()
    
    /*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--To reset the pokemon's stats back to their base stats
    
    	Returns:
    	--N/A
	*/
    public void resetStats() {
    	this.atk = this.baseAtk;
    	this.def = this.baseDef;
    	this.spAtk = this.baseSpAtk;
    	this.spDef = this.baseSpDef;
    	this.speed = this.baseSpeed;
    	this.accuracy = this.baseAccuracy;
    	this.evasion = this.baseEvasion;
    } // resetStats()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--To reset the pokemon's stages back to their base stages
    
    	Returns:
    	--N/A
    */
    public void resetStages() {
    	this.atkStage = this.BASE_STAGE;
    	this.defStage = this.BASE_STAGE;
    	this.spAtkStage = this.BASE_STAGE;
    	this.spDefStage = this.BASE_STAGE;
    	this.speedStage = this.BASE_STAGE;
    	this.accuracyStage = this.BASE_STAGE;
    	this.evasionStage = this.BASE_STAGE;
    } // resetStages()
    
    /*
    	Parameters:
    	--N/A        

    	Desc/Purpose:
    	--To restore the pokemon's moveset back to its original moveset
    
    	Returns:
    	--N/A
    */
    public void restoreMoveset() {
    	if (!this.backupMoveset.isEmpty()) {
    		this.moveset = this.backupMoveset;  		
    	}
    } // restoreMoveset()
    
    /*
     	Parameters:
     	--N/A
     	
     	Desc/Purpose:
     	--To restore the following back to their
     	  original values:
    	  --Name,
 		  --Pokedex #,
    	  --Gender, and
    	  --Primary and secondary types
    */
    public void restoreGeneralInfo() {
    	this.name = this.backupName;
    	this.pokedexNumber = this.backupPokedexNumber;
    	this.gender = this.backupGender;
    	this.primaryType = this.backupPrimaryType;
    	this.secondaryType = this.backupSecondaryType;
    } // restoreGeneralInfo()
    
    /*
    	Parameters:
    	--N/A      

    	Desc/Purpose:
    	--To completely reset the pokemon
    
    	Returns:
    	--N/A
    */
    public void resetEverything() {
    	setNonVolatileStatus(NonVolatileStatus.NONE);
    	this.sleepTurns = 0;
    	this.badlyPoisonedTurns = 0;
    	resetVolatileStatuses();
    	resetStats();
    	resetStages();
    	setHP(getMaxHP());
    	restoreGeneralInfo();
    	restoreMoveset();    	
    	setHitByClassification(Classification.NONE);
    	setRecentDamageTaken(0);
    	
    	int numMoves = this.moveset.size();    	
    	for (int i = 0; i < numMoves; i++) {
    		if (this.moveset.get(i) != null) {
    			this.moveset.get(i).setPP(this.moveset.get(i).getMaxPP());
    		}
    	}    	
    } // resetEverything()
} // Class
