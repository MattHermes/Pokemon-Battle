/*
   Author: Matt Hermes
     File: Tests.java
  Version: 0.310
     Date: 02/08/19
     Desc: Used to test the functionality of the game as new functions
           and classes are added
Change(s):
    --None made
	  
     TODO:
     --Nothing
*/

package pokemonBattle;
import java.util.*;

public class Tests {
	private final int MIN_STAGE = -6;
	private final int BASE_STAGE = 0;
	private final int MAX_STAGE = 6;
	private final static int NUM_TYPES = 19;
	private double[][] typeChart = new double[NUM_TYPES][NUM_TYPES];
	
	public Tests(double[][] typeChart) {
		this.typeChart = typeChart;
	}// Constructor
	
	/*
    Parameters:
    --N/A        

    Desc/Purpose:
    --Ensure that we have correctly stored the type chart
    
    Returns:
    --N/A
    */
	public void printTypeChart() {
		for (int i = 0; i < NUM_TYPES; i++) {
			for (int j = 0; j < NUM_TYPES; j++) {
				System.out.print(this.typeChart[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	/*
    Parameters:    
    --move: A move in a pokemon's moveset

    Desc/Purpose:
    --To ensure that we have all of the necessary information for a given move
    
    Returns:
    --N/A
 	*/
	public void printMove(Move move) {
		System.out.println("Name: " + move.getMoveName());
		System.out.println("Type: " + move.getMoveType());
		System.out.println("Base Power: " + move.getBasePower());
        System.out.println("Classification: " + move.getClassification());
        System.out.println("Priority: " + move.getPriority());
        System.out.println("Accuracy: " + move.getAccuracy());
        System.out.println("Crit Chance: " + move.getCritChance() + "%");
        System.out.println("PP: " + move.getPP() + "/" + move.getMaxPP());       
        System.out.println("Effects: " + move.getEffects());
	} // printMove(Move move)
	
	/*
    	Parameters:
    	--pokemon: A pokemon in a player's team

    	Desc/Purpose:
    	--To ensure that we have all of the necessary information for a given pokemon
    
    	Returns:
    	--N/A
    */
	public void printPokemon(Pokemon pokemon) {
		System.out.println("pokeID: " + pokemon.getPokeID());
		System.out.println("Pokedex #: " + pokemon.getPokedexNumber());
        System.out.println("Name: " + pokemon.getPokeName());
        System.out.println("Lv: " + pokemon.getLevel());
        System.out.println("Gender: " + pokemon.getGender());
        
        Type primaryType = pokemon.getPrimaryType();
        Type secondaryType = pokemon.getSecondaryType();
        
        if (secondaryType == Type.NONE) {
        	System.out.println("Type: " + primaryType + "/" + secondaryType);
        }
		
        else {
        	System.out.println("Type: " + primaryType);
        }
        
        System.out.println();
        System.out.println("Moveset:");
        
        for (Move move : pokemon.getMoveset()) {
        	if (move != null) {
        		System.out.println(move.getMoveName());
        	}
        	
        	else {
        		System.out.println("-----------");
        	}
        }
        
        System.out.println();
        System.out.println("Stats:");
        System.out.println("--HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP());
        System.out.println("--Attack: " + pokemon.getAtk() + " Attack Stage: " + pokemon.getAtkStage());
        System.out.println("--Defense: " + pokemon.getDef() + " Defense Stage: " + pokemon.getDefStage());
        System.out.println("--Special Attack: " + pokemon.getSpAtk() + " Special Attack Stage: " + pokemon.getSpAtkStage());
        System.out.println("--Special Defense: " + pokemon.getSpDef() + " Special Defense Stage: " + pokemon.getSpDefStage());
        System.out.println("--Speed: " + pokemon.getSpeed() + " Speed Stage: " + pokemon.getSpeedStage());
        System.out.println("--Accuracy: " + pokemon.getAccuracy() + " Accuracy Stage: " + pokemon.getAccuracyStage());
        System.out.println("--Evasion: " + pokemon.getEvasion() + " Evasion Stage: " + pokemon.getEvasionStage());
        System.out.println();
        System.out.println("Statuses:");
        System.out.println("--Non-volatile status: " + pokemon.getNonVolatileStatus());
        System.out.println("--Volatile statuses: " + pokemon.getVolatileStatuses());        
        System.out.println(); 
	} // printPokemon(Pokemon pokemon)
	
	/*
   		Parameters:
   		--pokemon: A pokemon in a player's team

   		Desc/Purpose:
   		--To print the pokemon's name and their current stats
    
   		Returns:
   		--N/A
    */ 
	public void printCurrentStats(Pokemon pokemon) {
		System.out.println("____________________Starting Test: Print Current Stats____________________");
		System.out.println("pokeID: " + pokemon.getPokeID());		
        System.out.println("Name: " + pokemon.getPokeName());
        System.out.println("Stats:");
        System.out.println("--HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP());
        System.out.println("--Attack: " + pokemon.getAtk() + " Attack Stage: " + pokemon.getAtkStage());
        System.out.println("--Defense: " + pokemon.getDef() + " Defense Stage: " + pokemon.getDefStage());
        System.out.println("--Special Attack: " + pokemon.getSpAtk() + " Special Attack Stage: " + pokemon.getSpAtkStage());
        System.out.println("--Special Defense: " + pokemon.getSpDef() + " Special Defense Stage: " + pokemon.getSpDefStage());
        System.out.println("--Speed: " + pokemon.getSpeed() + " Speed Stage: " + pokemon.getSpeedStage());
        System.out.println("--Accuracy: " + pokemon.getAccuracy() + " Accuracy Stage: " + pokemon.getAccuracyStage());
        System.out.println("--Evasion: " + pokemon.getEvasion() + " Evasion Stage: " + pokemon.getEvasionStage());
        System.out.println();
        System.out.println("Done");
        System.out.println("__________________________________________________________________________");
        System.out.println();
        
	} // printCurrentStats(Pokemon pokemon)
	
	/*
    	Parameters:
    	--team1: Player one's team
    	--team2: Player two's team
    	--menuOption: The menu option that a player might choose

    	Desc/Purpose:
    	--To simulate a pokemon using a damaging move against an opponent
      	  to see if the move will deal damage or not
    
    	Returns:
    	--N/A
    */
	public void usingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, String option) {
		System.out.println("____________________Starting Test: Using Move____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		results.add(pOnePoke);
		results.add(pTwoPoke);
		
		results = battle.turn(pOnePoke, pTwoPoke, option, "1");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("_________________________________________________________________");
        System.out.println();
	} // usingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, String option)
	
	/*
    	Parameters:
    	--team1: Player one's team
    	--team2: Player two's team
    	--atkPokeMove: The attacking pokemon's move

    	Desc/Purpose:
    	--To ensure that the "attackEffectiveness" attribute has the correct value
      	  based on the type of the move and the type(s) of the defending pokemon
    
    	Returns:
    	--N/A
    */
	public void testAttackEffectiveness(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, Move atkPokeMove) {
		System.out.println("____________________Starting Test: Attack Effectiveness____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		Type pTwoPrimaryType = pTwoPoke.getPrimaryType();
		Type pTwoSecondaryType = pTwoPoke.getSecondaryType();		
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		Type moveType = atkPokeMove.getMoveType();
		double typeDamage = battle.getTypeResult(moveType, pTwoPrimaryType, pTwoSecondaryType);
		battle.setAttackEffectiveness(typeDamage);
		
		String attackEffectiveness = battle.getAttackEffectiveness();
		System.out.println(attackEffectiveness);
		System.out.println();
        System.out.println("Done");
        System.out.println("_____________________________________________________________________________");
        System.out.println();
	} // testAttackEffectiveness(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, Move atkPokeMove)
	
	/*
    	Parameters:
       	--pokemon: The pokemon using the move        
    	--move: The attacking pokemon's move

    	Desc/Purpose:
    	--To ensure that a Same Type Attack Bonus (STAB) is givin when
      	  the move's type matches either the primary or secondary type
      	  of the pokemon
    
    	Returns:
    	--N/A
    */
	public void testIsSTAB(Pokemon pokemon, Move move) {
		System.out.println("____________________Starting Test: Test isSTAB____________________");
		Type moveType = move.getMoveType();
		String moveName = move.getMoveName();
		String pokeName = pokemon.getPokeName();
		Type primaryType = pokemon.getPrimaryType();
		Type secondaryType = pokemon.getSecondaryType();
		
		ArrayList<Pokemon> team1 = new ArrayList<Pokemon>();
		team1.add(pokemon);
		ArrayList<Pokemon> team2 = new ArrayList<Pokemon>();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		if (battle.isSTAB(moveType, primaryType, secondaryType)) {
			System.out.println(pokeName + " got a STAB when using " + moveName + ".");
		}
		
		else {
			System.out.println(pokeName + " did not get a STAB when using " + moveName + ".");
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("__________________________________________________________________");
        System.out.println();
	} // testIsSTAB(Pokemon pokemon, Move move)
	
	/*
    	Parameters:
    	--team1: Player one's team
    	--team2: Player two's team

    	Desc/Purpose:
    	--To ensure that attacks do a different amount of damage
      	  based on the damage roll
    
    	Returns:
    	--N/A
    */
	public void testDamageRolls(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Damage Rolls____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		int numUses = 5; // Number of times to use the move
	    double typeDamage = 0.0;
	    List<Move> moveset = pOnePoke.getMoveset();
	    Move atkPokeMove = moveset.get(0);
	    Type moveType = atkPokeMove.getMoveType();
	    String moveName = atkPokeMove.getMoveName();
	    String atkPokeName = pOnePoke.getPokeName();
	    
	    Type defPrimaryType = pTwoPoke.getPrimaryType();
	    Type defSecondaryType = pTwoPoke.getSecondaryType();
	    
	    double damage = 0.0;
	    
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		for (int i = 0; i < numUses; i++) {
			typeDamage = battle.getTypeResult(moveType, defPrimaryType, defSecondaryType);
			damage = battle.calcAttackDamage(pOnePoke, atkPokeMove, typeDamage, pTwoPoke);
			battle.printUsedMoveMessage(atkPokeName, moveName);
			battle.printAttackMessage((int)damage);
			System.out.println();
	        System.out.println("Done");
	        System.out.println("__________________________________________________________________");
	        System.out.println();
		}		
	} // testDamageRolls(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
    	Parameters:
        --team1: Player one's team
    	--team2: Player two's team

    	Desc/Purpose:
    	--To ensure that the move Struggle is used when a pokemon is
          out of usable moves
    
    	Returns:
    	--N/A
    */
	public void testOutOfMoves(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Out of Usable Moves____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon attackingPoke = team1.get(0);
		Pokemon defendingPoke = team2.get(0);
		
		List<Move> moveset = attackingPoke.getMoveset();
		int numMoves = moveset.size();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		for (int i = 0; i < numMoves; i++) { // Simulate the pokemon running out of PP for its moves
			if (moveset.get(i) != null) {
				moveset.get(i).setPP(0);
			}
		}
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(attackingPoke, defendingPoke, "15", "1"); // "15" is what the move menu would return if the
		                                                                // the move Struggle was used
		
		printPokemon(attackingPoke);
		System.out.println();
        System.out.println("Done");
        System.out.println("_______________________________________________________________________________________");
        System.out.println();			
	} // testOutOfMoves(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
    	Parameters:
        --team1: Player one's team
    	--team2: Player two's team

    	Desc/Purpose:
    	--To ensure that critical hits
      	(without any buffs or debuffs)
      	work 
    
    	Returns:
    	--N/A
    */
	public void testCritNoChanges(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Critical Hit (No Stat Changes)____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		ArrayList<String> testPMoveEffects = new ArrayList<String>();
		ArrayList<String> testSMoveEffects = new ArrayList<String>();
		
		Move testPhysicalMove = new Move("Test Physical Move", Type.DARK, Classification.PHYSICAL, 70, 8, 100, 0, (double)100, testPMoveEffects);
		Move testSpecialMove = new Move("Test Special Move", Type.DARK, Classification.SPECIAL, 70, 8, 100, 0, (double)100, testSMoveEffects);
		
		ArrayList<Move> testMoveset = new ArrayList(Arrays.asList(new Move[] {testPhysicalMove, testSpecialMove}));
		
		Pokemon testPokemon1 = null;
		try {
			testPokemon1 = (Pokemon) team1.get(0).clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testPokemon1.setMoveset(testMoveset);
		
		Pokemon testPokemon2 = team2.get(0);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		results = battle.turn(testPokemon1, testPokemon2, "11", "1");
		testPokemon2.setHP(testPokemon2.getMaxHP());
		results = battle.turn(testPokemon1, testPokemon2, "12", "1");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("_______________________________________________________________________________________");
        System.out.println();		
	} // testCritNoChanges(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --To ensure that critical hits
          (with buffs and debuffs to
          Def and SpDef) work 
        
        Returns:
        --N/A
	 */
	public void testCritDefandSpDefChanges(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Critical Hit (Changes in Def and SpDef)____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		ArrayList<String> testPMoveEffects = new ArrayList<String>();
		ArrayList<String> testSMoveEffects = new ArrayList<String>();
		
		Move testPhysicalMove = new Move("Test Physical Move", Type.DARK, Classification.PHYSICAL, 70, 8, 100, 0, (double)100, testPMoveEffects);
		Move testSpecialMove = new Move("Test Special Move", Type.DARK, Classification.SPECIAL, 70, 8, 100, 0, (double)100, testSMoveEffects);
		
		ArrayList<Move> testMoveset = new ArrayList<Move>(Arrays.asList(new Move[] {testPhysicalMove, testSpecialMove}));
		
		Pokemon testPokemon1 = team1.get(0);
		testPokemon1.setMoveset(testMoveset);		
		Pokemon testPokemon2 = team2.get(0);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		int buffStages = this.BASE_STAGE + 2;
		int debuffStages = this.BASE_STAGE - 2;
		
		System.out.println("-----------------Test 1: Defending Poke has Def and SpDef Buffs-----------------");
		testPokemon2.setDefStage(buffStages);
		testPokemon2.setSpDefStage(buffStages);
		
		results = battle.turn(testPokemon1, testPokemon2, "11", "1");
		testPokemon2.setHP(testPokemon2.getMaxHP());
		results = battle.turn(testPokemon1, testPokemon2, "12", "1");
		System.out.println("--------------------------------------------------------------------------------");
		
		System.out.println("----------------Test 2: Defending Poke has Def and SpDef Debuffs----------------");
		testPokemon2.setDefStage(debuffStages);
		testPokemon2.setSpDefStage(debuffStages);
		
		results = battle.turn(testPokemon1, testPokemon2, "11", "1");
		testPokemon2.setHP(testPokemon2.getMaxHP());
		results = battle.turn(testPokemon1, testPokemon2, "12", "1");
		System.out.println("--------------------------------------------------------------------------------");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("_______________________________________________________________________________________");
        System.out.println();
	} // testCritDefandSpDefChanges(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --To ensure that critical hits
          (with buffs and debuffs to
          Def and SpDef) work 
        
        Returns:
        --N/A
	 */
	public void testCritAtkandSpAtkChanges(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Critical Hit (Changes in Atk and SpAtk)____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		ArrayList<String> testPMoveEffects = new ArrayList<String>();
		ArrayList<String> testSMoveEffects = new ArrayList<String>();
		
		Move testPhysicalMove = new Move("Test Physical Move", Type.DARK, Classification.PHYSICAL, 70, 8, 100, 0, (double)100, testPMoveEffects);
		Move testSpecialMove = new Move("Test Special Move", Type.DARK, Classification.SPECIAL, 70, 8, 100, 0, (double)100, testSMoveEffects);
		
		ArrayList<Move> testMoveset = new ArrayList<Move>(Arrays.asList(new Move[] {testPhysicalMove, testSpecialMove}));
		
		Pokemon testPokemon1 = team1.get(0);
		testPokemon1.setMoveset(testMoveset);		
		Pokemon testPokemon2 = team2.get(0);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		int buffStages = this.BASE_STAGE + 2;
		int debuffStages = this.BASE_STAGE - 2;
		
		System.out.println("-----------------Test 1: Defending Poke has Atk and SpAtk Buffs-----------------");
		testPokemon1.setAtkStage(buffStages);
		testPokemon1.setSpAtkStage(buffStages);
		
		results = battle.turn(testPokemon1, testPokemon2, "11", "1");
		testPokemon2.setHP(testPokemon2.getMaxHP());
		results = battle.turn(testPokemon1, testPokemon2, "12", "1");
		System.out.println("--------------------------------------------------------------------------------");
		
		System.out.println("----------------Test 2: Defending Poke has Atk and SpAtk Debuffs----------------");
		testPokemon1.setAtkStage(debuffStages);
		testPokemon1.setSpAtkStage(debuffStages);
		
		results = battle.turn(testPokemon1, testPokemon2, "11", "1");
		testPokemon2.setHP(testPokemon2.getMaxHP());
		results = battle.turn(testPokemon1, testPokemon2, "12", "1");
		System.out.println("--------------------------------------------------------------------------------");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("_______________________________________________________________________________________");
        System.out.println();
	} // testCritAtkandSpAtkChanges(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
    	Parameters:
    	--pokemon: A pokemon in a player's team
    
    	Desc/Purpose:
    	--To ensure that a pokemon's nonvolatile status cannot be overridden
      	  unless we want it to
    
    	Returns:
    	--N/A
    */
	public void setNonVolatileStatus(Pokemon pokemon) {
		System.out.println("____________________Starting Test: Set NonVolatile Status____________________");
		pokemon.resetEverything();
		System.out.println("--------------------Attempting To Poison Pokemon--------------------");
		pokemon.setNonVolatileStatus(NonVolatileStatus.POISON);
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Burn Pokemon--------------------");
		pokemon.setNonVolatileStatus(NonVolatileStatus.BURN);
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Reset Pokemon--------------------");
		pokemon.setNonVolatileStatus(NonVolatileStatus.NONE);
		printPokemon(pokemon);
		System.out.println();
        System.out.println("Done");
        System.out.println("______________________________________________________________________________");
        System.out.println();
	} // setNonVolatileStatus(Pokemon pokemon)
	
	/*
    	Parameters:
        --pokemon: A pokemon in a player's team
    
    	Desc/Purpose:
    	--To test that we cannot apply the same volatile status twice
    	  and that we can apply multiple different volatile statuses
    
    	Returns:
    	--N/A
    */
	public void setVolatileStatus(Pokemon pokemon) {
		System.out.println("____________________Starting Test: Set Volatile Status____________________");
		pokemon.resetEverything();
		System.out.println("--------------------Attempting To Confuse Pokemon--------------------");
		pokemon.setVolatileStatus(VolatileStatus.CONFUSION);
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Confuse Pokemon Again--------------------");
		pokemon.setVolatileStatus(VolatileStatus.CONFUSION);
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Attract Pokemon--------------------");
		pokemon.setVolatileStatus(VolatileStatus.ATTRACTION);
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Flinch Pokemon--------------------");
		pokemon.setVolatileStatus(VolatileStatus.FLINCH);
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Reset Confusion--------------------");
		pokemon.resetConfusion();
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Reset Attraction--------------------");
		pokemon.resetAttraction();
		printPokemon(pokemon);
		System.out.println("--------------------Attempting To Reset Flinching--------------------");
		pokemon.resetFlinch();
		printPokemon(pokemon);
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // setVolatileStatus(Pokemon pokemon)
	
	/*
    	Parameters:
        --pokemon: A pokemon in a player's team
    	--damage: The damage taken by the status        

    	Desc/Purpose:
    	--To display how much damage the pokemon took from a particular status
    
    	Returns:
    	--N/A
    */
	public void printStatusDamage(Pokemon pokemon, int damage) {
		NonVolatileStatus nonVolatileStatus = pokemon.getNonVolatileStatus();
		ArrayList<VolatileStatus> volatileStatuses = pokemon.getVolatileStatuses();
		
		if (nonVolatileStatus != NonVolatileStatus.NONE) {
			System.out.println(pokemon.getPokeName() + " took " + damage + " HP of damage from " + nonVolatileStatus);
		}
		
		else {
			int max = volatileStatuses.size();
			for (int i = 0; i < max; i++) {
				VolatileStatus status = volatileStatuses.get(i);
				
				if (status != VolatileStatus.NONE) {
					System.out.println(pokemon.getPokeName() + " took " + damage + " HP of damage from " + status);
					break;
				}
			}
		}
	} // printStatusDamage(Pokemon pokemon, int damage)
	
	/*
    	Parameters:
        --pOneTeam: Player one's team
    	--pTwoTeam: Player two's team

    	Desc/Purpose:
    	--Test to see how many turns it takes before a pokemon on each player's
          wakes up
    
    	Returns:
    	--N/A
    */
	public void testSleepStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Sleep Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		String pOnePokeName = pOnePoke.getPokeName();
		String pTwoPokeName = pTwoPoke.getPokeName();
		
		String firstToAct = battle.determineFirstToAct(pOnePoke, pTwoPoke, "11", "11");
		
		// Put the pokemon to sleep
		pOnePoke.setNonVolatileStatus(NonVolatileStatus.SLEEP);
		pTwoPoke.setNonVolatileStatus(NonVolatileStatus.SLEEP);
		
		// Set the number of sleep turns for both pokemon
		pOnePoke = battle.setOpponentSleepTurns(pOnePoke, firstToAct);
		pTwoPoke = battle.setOpponentSleepTurns(pTwoPoke, firstToAct);
		
		while(true) {
			pOnePoke = battle.handleSleepStatus(pOnePoke);
			
			NonVolatileStatus status = pOnePoke.getNonVolatileStatus();
			if (status == NonVolatileStatus.SLEEP) {
				System.out.println(pOnePoke.getSleepTurns() + " turns left until " + pOnePokeName + " wakes up.");
				System.out.println();
			}
			else {
				break;
			}
		}
		
		while(true) {
			pTwoPoke = battle.handleSleepStatus(pTwoPoke);
			
			NonVolatileStatus status = pTwoPoke.getNonVolatileStatus();
			if (status == NonVolatileStatus.SLEEP) {
				System.out.println(pTwoPoke.getSleepTurns() + " turns left until " + pTwoPokeName + " wakes up.");
				System.out.println();
			}
			else {
				break;
			}
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testSleepStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
    	Parameters:
        --team1: Player one's team
    	--team2: Player two's team

    	Desc/Purpose:
    	--Test to see how much damage a badly poisoned pokemon on each player's
      	  team take each turn
    
    	Returns:
    	--N/A
    */
	public void testBadlyPoisonStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Badly Poison Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		// Badly poison the pokemon
		pOnePoke.setNonVolatileStatus(NonVolatileStatus.BADLY_POISON);
		pTwoPoke.setNonVolatileStatus(NonVolatileStatus.BADLY_POISON);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		while(true) {
			if (!battle.hasFainted(pOnePoke)) {
				pOnePoke.setBadlyPoisonedTurns(pOnePoke.getBadlyPoisonedTurns() + 1);
				int damage = battle.calcNonVolatileStatusDamage(pOnePoke);
				pOnePoke = battle.updateHealth(pOnePoke, damage, 0);
				printStatusDamage(pOnePoke, damage);
			}
			else {
				System.out.println();
				break;
			}
		}
		
		while(true) {
			if (!battle.hasFainted(pTwoPoke)) {
				pTwoPoke.setBadlyPoisonedTurns(pTwoPoke.getBadlyPoisonedTurns() + 1);
				int damage = battle.calcNonVolatileStatusDamage(pTwoPoke);
				pOnePoke = battle.updateHealth(pTwoPoke, damage, 0);
				printStatusDamage(pTwoPoke, damage);
			}
			else {
				System.out.println();
				break;
			}
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testBadlyPoisonStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
		Parameters:    
		--team1: Player one's team
		--team2: Player two's team
		
		Desc/Purpose:
        --Test to see the following
          --The burn status should reduce the damage done by physical moves
          --Burned pokemon take damage at the end of their turn
        
        Returns:
        --N/A
	*/
	public void testBurnStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Burn Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		String pOnePokeName = pOnePoke.getPokeName();		
		Type pTwoPrimaryType = pTwoPoke.getPrimaryType();
		Type pTwoSecondaryType = pTwoPoke.getSecondaryType();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 85, 24, 100, 0, (double)6.25, testMoveEffect);
		Type moveType = testMove.getMoveType();
		String moveName = testMove.getMoveName();
		
		printPokemon(pTwoPoke);
		
		// Deal damage before the Burn status is applied
		double typeDamage = battle.getTypeResult(moveType, pTwoPrimaryType, pTwoSecondaryType);
		int damage = battle.calcAttackDamage(pOnePoke, testMove, typeDamage, pTwoPoke);
		battle.printUsedMoveMessage(pOnePokeName, moveName);
		battle.printAttackMessage(damage);
		pTwoPoke = battle.updateHealth(pTwoPoke, damage, 0);
		printPokemon(pTwoPoke);
		
		// Burn the attacking pokemon and then deal damage
		pOnePoke.setNonVolatileStatus(NonVolatileStatus.BURN);
		typeDamage = battle.getTypeResult(moveType, pTwoPrimaryType, pTwoSecondaryType);
		damage = battle.calcAttackDamage(pOnePoke, testMove, typeDamage, pTwoPoke);
		battle.printUsedMoveMessage(pOnePokeName, moveName);
		battle.printAttackMessage(damage);
		pTwoPoke = battle.updateHealth(pTwoPoke, damage, 0);
		printPokemon(pOnePoke);
		printPokemon(pTwoPoke);
		
		// The burned pokemon should now take damage
		int statusDamage = battle.calcNonVolatileStatusDamage(pOnePoke);
		pOnePoke = battle.updateHealth(pOnePoke, statusDamage, 0);
		printPokemon(pOnePoke);
		printStatusDamage(pOnePoke, statusDamage);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
		
	} // testBurnStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
    	Parameters:    
    	--team1: Player one's team
    	--team2: Player two's team
    	--useTestLoop: Used to check if we want to do the loop that
          tests to see how many turns it takes before a pokemon is
          fully paralyzed

    	Desc/Purpose:
    	--Test to see the following:
      	  --How paralysis changes speed comparisons
      	  --How many turns it takes before a pokemon each player's
        	team is fully paralyzed
    
    	Returns:
    	--N/A
    */ 
	public void testParalysisStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean useTestLoop) {
		System.out.println("____________________Starting Test: Paralysis Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		// Show the speed comparison before any pokemon are paralyzed		
		String result = battle.compareSpeedStats(pOnePoke, pTwoPoke);
		System.out.println("Speed result (No Paralysis): " + result);
		
		// Paralyze the first pokemon in player one's team and see how the speed comparison changes
		pOnePoke.setNonVolatileStatus(NonVolatileStatus.PARALYSIS);
		result = battle.compareSpeedStats(pOnePoke, pTwoPoke);
		System.out.println("Speed result (Player one's pokemon is paralyzed): " + result);
		
		// Paralyze the first pokemon in player two's team so that both pokemon are paralyzed
		pTwoPoke.setNonVolatileStatus(NonVolatileStatus.PARALYSIS);
		result = battle.compareSpeedStats(pOnePoke, pTwoPoke);
		System.out.println("Speed result (Both pokemon are paralyzed): " + result);
		
		int numTurns = 0;
		
		if (useTestLoop) {
			String pokeName = pOnePoke.getPokeName();
			while(true) { // See how long it takes before player one's pokemon is fully paralyzed
				numTurns++;
				if (!battle.wasFullyParalyzed()) { 
					System.out.println("Turn " + numTurns + ": " + pokeName + " used a move.");
				}
				
				else {
					System.out.println("Turn " + numTurns + ": " + pokeName + " was fully paralyzed.");
					System.out.println();
					break;
				}
			}
			
			numTurns = 0;
			pokeName = pTwoPoke.getPokeName();
			while(true) { // See how long it takes before player one's pokemon is fully paralyzed
				numTurns++;
				if (!battle.wasFullyParalyzed()) { 
					System.out.println("Turn " + numTurns + ": " + pokeName + " used a move.");
				}
				
				else {
					System.out.println("Turn " + numTurns + ": " + pokeName + " was fully paralyzed.");
					System.out.println();
					break;
				}
			}
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testParalysisStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean useTestLoop)
		
	public void testFreezeStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2){
		System.out.println("____________________Starting Test: Freeze Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		ArrayList<String> testMoveEffects = new ArrayList<String>();
		testMoveEffects.add("No Battle Effect None");
		Move testMove = new Move("Test Move", Type.NORMAL, Classification.STATUS, 0, 24, 999, 0, 0, testMoveEffects);
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		pOnePoke.setNonVolatileStatus(NonVolatileStatus.FREEZE);
		pTwoPoke.setNonVolatileStatus(NonVolatileStatus.FREEZE);
		
		String pokeName = pOnePoke.getPokeName();
		int numTurns = 0;
		while (true) {
			numTurns++;
			if (!battle.wasFrozenSolid(testMove)) {
				System.out.println("Turn " + numTurns + ": " + pokeName + " used a move.");
				System.out.println();
				break;
			}
			
			else {
				System.out.println("Turn " + numTurns + ": " + pokeName + " is frozen solid.");
			}
		}
		
		numTurns = 0;
		pokeName = pTwoPoke.getPokeName();
		while (true) {
			numTurns++;
			if (!battle.wasFrozenSolid(testMove)) {
				System.out.println("Turn " + numTurns + ": " + pokeName + " used a move.");
				System.out.println();
				break;
			}
			
			else {
				System.out.println("Turn " + numTurns + ": " + pokeName + " is frozen solid.");
			}
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testFreezeStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
    	Parameters:
        --team1: Player one's team
    	--team2: Player two's team
   
    	Desc/Purpose:
    	--Test to ensure that only certain attack can thaw an opponent
    
    	Returns:
    	--N/A
    */
	public void testThawOpponent(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Thaw Opponent____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> stoneEdgeEffect = new ArrayList<String>();		
		ArrayList<String> triAttackEffects = new ArrayList<String>();
		triAttackEffects.add("6.67% Chance To Paralysis Opponent");
		triAttackEffects.add("6.67% Chance To Freeze Opponent");
		triAttackEffects.add("6.67% Chance To Burn Opponent");
		
		ArrayList<String> flamethrowerEffect = new ArrayList<String>();		
		flamethrowerEffect.add("10% Chance To Burn Opponent");	
		
		ArrayList<String> scaldEffect = new ArrayList<String>();
		scaldEffect.add("30% Chance to Burn Opponent");	
		
		Move stoneEdge = new Move ("Stone Edge", Type.ROCK, Classification.PHYSICAL, 100, 8, 80, 0, (double)12.5, stoneEdgeEffect);
		Move triAttack = new Move("Tri Attack", Type.NORMAL, Classification.SPECIAL, 80, 16, 100, 0, (double)6.25, triAttackEffects);
		Move scald = new Move("Scald", Type.WATER, Classification.SPECIAL, 80, 24, 100, 0, (double) 6.25, scaldEffect);
		Move flamethrower = new Move("Flamethower", Type.FIRE, Classification.SPECIAL, 90, 24, 100, 0, (double) 6.25, flamethrowerEffect);
		
		ArrayList<Move> testMoveset = new ArrayList<Move>();
		testMoveset.add(stoneEdge);
		testMoveset.add(triAttack);
		testMoveset.add(scald);
		testMoveset.add(flamethrower);
		pOnePoke.setMoveset(testMoveset);
		
		int numMoves = testMoveset.size();
		
		int pOneMaxHP = pOnePoke.getMaxHP();
		int pTwoMaxHP = pTwoPoke.getMaxHP();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		String menuSelected = "1";
		String option;
		for (int i = 0; i < numMoves; i++) {
			option = menuSelected + Integer.toString(i+1);
			pTwoPoke.setNonVolatileStatus(NonVolatileStatus.FREEZE); // Freeze the opponent
			results = battle.turn(pOnePoke, pTwoPoke, option, "1"); // Try to thaw the opponent
			pOnePoke = results.get(0);
			pTwoPoke = results.get(1);
			
			results = battle.turn(pTwoPoke, pOnePoke, "11", "1"); // The opponent take its turn
			pTwoPoke = results.get(0);
			pOnePoke = results.get(1);
			
			printPokemon(pTwoPoke);
			pOnePoke.setHP(pOneMaxHP);
			pTwoPoke.setHP(pTwoMaxHP);			
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testThawOpponent(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
		Parameters:
    	--team1: Player one's team
		--team2: Player two's team

		Desc/Purpose:
		--Test to ensure that only certain attack can thaw the user

		Returns:
		--N/A
	*/
	public void testThawSelf(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Thaw Opponent____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> stoneEdgeEffect = new ArrayList<String>();		
		ArrayList<String> triAttackEffects = new ArrayList<String>();
		triAttackEffects.add("6.67% Chance To Paralysis Opponent");
		triAttackEffects.add("6.67% Chance To Freeze Opponent");
		triAttackEffects.add("6.67% Chance To Burn Opponent");
		
		ArrayList<String> flamethrowerEffect = new ArrayList<String>();		
		flamethrowerEffect.add("10% Chance To Burn Opponent");	
		
		ArrayList<String> scaldEffect = new ArrayList<String>();
		scaldEffect.add("30% Chance to Burn Opponent");	
		
		Move stoneEdge = new Move ("Stone Edge", Type.ROCK, Classification.PHYSICAL, 100, 8, 80, 0, (double)12.5, stoneEdgeEffect);
		Move triAttack = new Move("Tri Attack", Type.NORMAL, Classification.SPECIAL, 80, 16, 100, 0, (double)6.25, triAttackEffects);
		Move scald = new Move("Scald", Type.WATER, Classification.SPECIAL, 80, 24, 100, 0, (double) 6.25, scaldEffect);
		Move flamethrower = new Move("Flamethower", Type.FIRE, Classification.SPECIAL, 90, 24, 100, 0, (double) 6.25, flamethrowerEffect);
		
		ArrayList<Move> testMoveset = new ArrayList<Move>();
		testMoveset.add(stoneEdge);
		testMoveset.add(triAttack);
		testMoveset.add(scald);
		testMoveset.add(flamethrower);
		pOnePoke.setMoveset(testMoveset);
		
		int numMoves = testMoveset.size();
		
		int pOneMaxHP = pOnePoke.getMaxHP();
		int pTwoMaxHP = pTwoPoke.getMaxHP();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		String menuSelected = "1";
		String option;
		for (int i = 0; i < numMoves; i++) {
			option = menuSelected + Integer.toString(i+1);
			pOnePoke.setNonVolatileStatus(NonVolatileStatus.FREEZE); // Freeze the opponent
			results = battle.turn(pOnePoke, pTwoPoke, option, "1"); // Try to thaw the opponent
			pOnePoke = results.get(0);
			pTwoPoke = results.get(1);
			
			results = battle.turn(pTwoPoke, pOnePoke, "11", "1"); // The opponent take its turn
			pTwoPoke = results.get(0);
			pOnePoke = results.get(1);
			
			printPokemon(pOnePoke);
			pOnePoke.setHP(pOneMaxHP);
			pTwoPoke.setHP(pTwoMaxHP);			
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testThawSelf(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that moves with the
          effect: "No Battle Effect" do nothing
        
        Returns:
        --N/A
	*/
	public void testNoBattleEffectMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Move with Effect 'No Battle Effect'____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		ArrayList<String> testMoveEffects = new ArrayList<String>();
		testMoveEffects.add("No Battle Effect None");
		Move testMove = new Move("Test Move", Type.NORMAL, Classification.STATUS, 0, 24, 999, 0, 0, testMoveEffects);
		ArrayList<Move> testMoveset = new ArrayList<Move>();
		testMoveset.add(testMove);
		Pokemon testPokemon1 = null;
		try {
			testPokemon1 = (Pokemon) team1.get(0).clone();
		} catch (CloneNotSupportedException e) {			
			e.printStackTrace();
		}
		testPokemon1.setMoveset(testMoveset);
		
		Pokemon testPokemon2 = team2.get(0);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		results = battle.turn(testPokemon1, testPokemon2, "11", "1");
		printCurrentStats(testPokemon2);
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testNoBattleEffectMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that OHKO moves work properly
        
        Returns:
        --N/A
	*/
	public void testOHKOMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: OHKO Move____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
				
		Pokemon attackingPoke = team1.get(0);
		Pokemon defendingPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("OHKO Opponent");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 0, 24, 100, 0, 0, testMoveEffect);
		
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		attackingPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(attackingPoke, defendingPoke, "11", "1");
		attackingPoke = results.get(0);
		defendingPoke = results.get(1);
		
		printPokemon(defendingPoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testOHKOMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that flinching moves work properly
        
        Returns:
        --N/A
	*/
	public void testFlinching(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Flinching____________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMove1Effect = new ArrayList<String>();
		testMove1Effect.add("100% Chance To Flinch Opponent");
		ArrayList<String> testMove2Effect = new ArrayList<String>();
		
		Move testMove1 = new Move("Test Move 1", Type.BUG, Classification.PHYSICAL, 60, 16, 100, 0, (double)6.25, testMove1Effect);
		Move testMove2 = new Move("Test Move 2", Type.BUG, Classification.PHYSICAL, 60, 16, 100, 0, (double)6.25, testMove2Effect);
		
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove1);
		moveset.add(testMove2);
		
		pOnePoke.setMoveset(moveset);
		pTwoPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "12", "1");

		results = battle.turn(pOnePoke, pTwoPoke, "12", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testFlinching(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to confirm that the attraction status works
        
        Returns:
        --N/A
	*/
	public void testAttraction(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Attraction____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
				
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		String pOnePokeName = pOnePoke.getPokeName();
		String pTwoPokeName = pTwoPoke.getPokeName();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		while(true) {
			battle.printInLoveMessage(pOnePokeName, pTwoPokeName);
			if (battle.immobilizedByLove()) {
				battle.printImmobilizedByLoveMessage(pOnePokeName);
				break;
			}
			else {
				System.out.println(pOnePokeName + " used a move.");
				System.out.println();
			}
		}
		
		while(true) {
			battle.printInLoveMessage(pTwoPokeName, pOnePokeName);
			if (battle.immobilizedByLove()) {
				battle.printImmobilizedByLoveMessage(pTwoPokeName);
				break;
			}
			else {
				System.out.println(pTwoPokeName + " used a move.");
				System.out.println();
			}
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testAttractionStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to confirm that the confusion status works
        
        Returns:
        --N/A
	*/
	public void testConfusion(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Confusion____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
						
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		int pOneMaxHP = pOnePoke.getMaxHP();
		int pTwoMaxHP = pTwoPoke.getMaxHP();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		// Confuse both of the pokemon
		pOnePoke.setVolatileStatus(VolatileStatus.CONFUSION);
		pOnePoke = battle.setOpponentConfusionTurns(pOnePoke, "1");
		pTwoPoke.setVolatileStatus(VolatileStatus.CONFUSION);
		pTwoPoke = battle.setOpponentConfusionTurns(pTwoPoke, "2");
		
		final int MAX_ITERATIONS = 5;
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pOnePoke = results.get(0);
			pTwoPoke = results.get(1);			
			
			results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
			pOnePoke = results.get(0);
			pTwoPoke = results.get(1);
			
			printPokemon(pOnePoke);
			printPokemon(pTwoPoke);
			
			pOnePoke.setHP(pOneMaxHP);
			pTwoPoke.setHP(pTwoMaxHP);
			
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
		
	} // testConfusion(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	public void testAttractedConfusedParalyzed(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Attracted, Confused, and Paralyzed____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
								
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		int pOneMaxHP = pOnePoke.getMaxHP();
		int pTwoMaxHP = pTwoPoke.getMaxHP();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		pOnePoke.setNonVolatileStatus(NonVolatileStatus.PARALYSIS);
		pOnePoke.setVolatileStatus(VolatileStatus.ATTRACTION);
		pOnePoke.setVolatileStatus(VolatileStatus.CONFUSION);
		pOnePoke = battle.setOpponentConfusionTurns(pOnePoke, "1");
		
		pTwoPoke.setNonVolatileStatus(NonVolatileStatus.PARALYSIS);
		pTwoPoke.setVolatileStatus(VolatileStatus.ATTRACTION);
		pTwoPoke.setVolatileStatus(VolatileStatus.CONFUSION);
		pTwoPoke = battle.setOpponentConfusionTurns(pTwoPoke, "2");
		
		final int MAX_TURNS = 10;
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		for (int i = 0; i < MAX_TURNS; i++) {
			System.out.println("-------------Turn " + (i+1) + "-------------");
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pOnePoke = results.get(0);
			pTwoPoke = results.get(1);		
			
			pOnePoke.setHP(pOneMaxHP);
			pTwoPoke.setHP(pTwoMaxHP);
			
			results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
			pOnePoke = results.get(0);
			pTwoPoke = results.get(1);
			System.out.println();
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testAttractedConfusedParalyzed(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to see how effective status moves are based on the
          move's type and the defending pokemon's type(s)
        
        Returns:
        --N/A
	*/
	public void testCanApplyNonVolatileStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Can Apply NonVolatile Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		// Create some test moves
		// Note: We pick these specific ones because they man not be
		//       applied depending on the pokemon's type and/or the
		//       move's type.
		ArrayList<String> grassSleepHaxEffect = new ArrayList<String>();
		ArrayList<String> freezeHaxEffect = new ArrayList<String>();
		ArrayList<String> badlyPoisonHaxEffect = new ArrayList<String>();
		ArrayList<String> poisonHaxEffect = new ArrayList<String>();
		ArrayList<String> burnHaxEffect = new ArrayList<String>();
		ArrayList<String> electricParalysisHaxEffect = new ArrayList<String>();		
		ArrayList<String> grassParalysisHaxEffect = new ArrayList<String>();
		
		grassSleepHaxEffect.add("100% Chance To Sleep Opponent");
		freezeHaxEffect.add("100% Chance to Freeze Opponent");
		badlyPoisonHaxEffect.add("100% Chance To Badly_Poison Opponent");
		poisonHaxEffect.add("100% Chance To Poison Opponent");
		burnHaxEffect.add("100% Chance To Burn Opponent");
		electricParalysisHaxEffect.add("100% Chance To Paralysis Opponent");
		grassParalysisHaxEffect.add("100% Chance To Paralysis Opponent");
		
		Move grassSleepHax = new Move("Grass Sleep Hax", Type.GRASS, Classification.STATUS, 0, 24, 100, 0, 0, grassSleepHaxEffect);
		Move freezeHax = new Move("Freeze Hax", Type.ICE, Classification.STATUS, 0, 24, 100, 0, 0, freezeHaxEffect);
		Move badlyPoisonHax = new Move("Badly Poison Hax", Type.POISON, Classification.STATUS, 0, 24, 100, 0, 0, badlyPoisonHaxEffect);
		Move poisonHax = new Move("Poison Hax", Type.POISON, Classification.STATUS, 0, 24, 100, 0, 0, poisonHaxEffect);
		Move burnHax = new Move("Burn Hax", Type.FIRE, Classification.STATUS, 0, 24, 100, 0, 0, burnHaxEffect);
		Move electricParalysisHax = new Move("Electric Paraylsis Hax", Type.ELECTRIC, Classification.STATUS, 0, 24, 100, 0, 0, electricParalysisHaxEffect);
		Move grassParalysisHax = new Move("Grass Paraylsis Hax", Type.GRASS, Classification.STATUS, 0, 24, 100, 0, 0, grassParalysisHaxEffect);
	
		// Create the test moveset
		ArrayList<Move> testMoveset = new ArrayList<Move>();
		testMoveset.add(grassSleepHax);
		testMoveset.add(freezeHax);
		testMoveset.add(badlyPoisonHax);
		testMoveset.add(poisonHax);
		testMoveset.add(burnHax);
		testMoveset.add(electricParalysisHax);
		testMoveset.add(grassParalysisHax);
		int numMoves = testMoveset.size();
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");		
				
		// Try each test move on player one's team
		Pokemon attackingPoke = team2.get(0);
		String atkPokeName = attackingPoke.getPokeName();
		attackingPoke.setMoveset(testMoveset);
		Pokemon defendingPoke;
		String defPokeName;
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		String option;
		System.out.println("--------------------------Player 1's team--------------------------");
		for (int i = 0; i < teamSize; i++) {
			defendingPoke = team1.get(i);
			defPokeName = defendingPoke.getPokeName();
			
			for (int j = 0; j < numMoves; j++) {
				System.out.println("Attacking Pokemon: " + atkPokeName + " Defending Pokemon: " + defPokeName);
				option = "1" + Integer.toString(j+1);
				results = battle.turn(attackingPoke, defendingPoke, option, "1");
				defendingPoke.setNonVolatileStatus(NonVolatileStatus.NONE);
			}
		}
		System.out.println("-------------------------------------------------------------------");
		
		// Try each test move on player two's team
		attackingPoke = team1.get(0);
		attackingPoke.setMoveset(testMoveset);
		System.out.println("--------------------------Player 2's team--------------------------");
		for (int i = 0; i < teamSize; i++) {
			defendingPoke = team2.get(i);
			defPokeName = defendingPoke.getPokeName();
			
			for (int j = 0; j < numMoves; j++) {
				System.out.println("Attacking Pokemon: " + atkPokeName + " Defending Pokemon: " + defPokeName);
				option = "1" + Integer.toString(j+1);
				results = battle.turn(attackingPoke, defendingPoke, option, "1");
				defendingPoke.setNonVolatileStatus(NonVolatileStatus.NONE);
			}
		}
		System.out.println("-------------------------------------------------------------------");
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testCanApplyNonVolatileStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that damaging moves that have a chance to
          apply a status effect will eventually apply the status
        
        Returns:
        --N/A
	*/
	public void testAttackApplyingStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Attack Applying Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon attackingPoke = team1.get(0);
		Pokemon defendingPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("50% Chance To Paralysis Opponent");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 50, 32, 100, 0, (double)6.25, testMoveEffect);
		
		ArrayList<Move> testMoveset = new ArrayList<Move>();
		testMoveset.add(testMove);
		attackingPoke.setMoveset(testMoveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		int numTurns = 0;
		final int MAX_TURNS = 1000; // We don't want the test to go on forever
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		String defPokeName = defendingPoke.getPokeName();
		NonVolatileStatus status;
		int maxHP = defendingPoke.getMaxHP();
		
		while(numTurns < MAX_TURNS) {
			numTurns++;
			results = battle.turn(attackingPoke, defendingPoke, "11", "1");
			defendingPoke = results.get(1);
			status = defendingPoke.getNonVolatileStatus();
			defendingPoke.setHP(maxHP);
			
			if (status != NonVolatileStatus.NONE) { // End the test
				System.out.println(" End of turn " + numTurns + ": " + defPokeName + " has status: " + status);
				break; 
			}
			
			else {
				System.out.println("End of turn " + numTurns + ": " + defPokeName + " has no status.");
				System.out.println();
			}			
		}
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testAttackApplyingStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that the move Tri Attack works
        
        Returns:
        --N/A
	*/
	public void testTriAttack(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Tri Attack____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon attackingPoke = team1.get(0);
		Pokemon defendingPoke = team2.get(0);
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		int maxHP = defendingPoke.getMaxHP();
		
		if (defendingPoke.getPrimaryType() == Type.GHOST || defendingPoke.getSecondaryType() == Type.GHOST) {
			System.out.println("The defending pokemon must not be a GHOST type!");
			System.out.println();
			return;
		}
		
		ArrayList<String> triAttackEffects = new ArrayList<String>();
		triAttackEffects.add("6.67% Chance To Paralysis Opponent");
		triAttackEffects.add("6.67% Chance To Freeze Opponent");
		triAttackEffects.add("6.67% Chance To Burn Opponent");
		
		Move triAttack = new Move("Tri Attack", Type.NORMAL, Classification.SPECIAL, 80, 16, 100, 0, (double)6.25, triAttackEffects);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(triAttack);
		
		attackingPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		NonVolatileStatus status = defendingPoke.getNonVolatileStatus();
		
		while(status == NonVolatileStatus.NONE) { // Keep going until a status is applied
			results = battle.turn(attackingPoke, defendingPoke, "11", "1");
			defendingPoke = results.get(1);			
			status = defendingPoke.getNonVolatileStatus();
			defendingPoke.setHP(maxHP);
		}
		printPokemon(defendingPoke);
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testTriAttack(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that a non-volatile status effect cannot be applied
          to a pokemon that was swapped in that already has one
        
        Returns:
        --N/A
	*/
	public void testSwapAndStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Swap and Status____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		team1.get(1).setNonVolatileStatus(NonVolatileStatus.SLEEP);
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();	
		testMoveEffect.add("100% Chance To Sleep Opponent");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.STATUS, 0, 24, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pTwoPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		// Swap player one's pokemon and have player two try to put an already statused pokemon to sleep
		results = battle.turn(pOnePoke, pTwoPoke, "22", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testSwapAndStatus(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	  	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --To ensure that the new pokemon to come is takes damage when its
          owner switches and the opponent chooses to use an attack
        
        Returns:
        --N/A
	*/
	public void testSwapAndAttack(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Swap and Attack____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();		
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();		
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 50, 24, 100, 0, (double)6.25, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pTwoPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		// Swap player one's pokemon and have player two try to put an already statused pokemon to sleep
		results = battle.turn(pOnePoke, pTwoPoke, "22", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testSwapAndAttack(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test that the move "Counter" works when the defending
          pokemon is hit with a physical move
        
        Returns:
        --N/A
	*/
	public void testCounterSuccess(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Counter Success____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
				
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMove1Effect = new ArrayList<String>();
		ArrayList<String> testMove2Effect = new ArrayList<String>();
		testMove2Effect.add("Counters Physical Damage Opponent");
		
		Move testMove1 = new Move("Test Move 1", Type.BUG, Classification.PHYSICAL, 75, 32, 100, 0, (double)6.25, testMove1Effect);
		Move testMove2 = new Move("Test Move 2", Type.BUG, Classification.PHYSICAL, 0, 32, 100, -5, 0, testMove2Effect);
		
		ArrayList<Move> moveset1 = new ArrayList<Move>();
		ArrayList<Move> moveset2 = new ArrayList<Move>();
		moveset1.add(testMove1);
		moveset2.add(testMove2);
		
		pOnePoke.setMoveset(moveset1);
		pTwoPoke.setMoveset(moveset2);	
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testCounterSuccess(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test that the move "Counter" fails when the defending
          pokemon is hit with a special or status move
        
        Returns:
        --N/A
	*/
	public void testCounterFailure(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Counter Failure____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
				
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMove1Effect = new ArrayList<String>();
		ArrayList<String> testMove2Effect = new ArrayList<String>();
		ArrayList<String> testMove3Effect = new ArrayList<String>();
		testMove2Effect.add("100% Chance To Poison Opponent");
		testMove3Effect.add("Counters Physical Damage Opponent");
		
		
		Move testMove1 = new Move("Test Move 1", Type.BUG, Classification.SPECIAL, 75, 32, 100, 0, (double)6.25, testMove1Effect);
		Move testMove2 = new Move("Test Move 2", Type.BUG, Classification.STATUS, 0, 16, 100, 0, 0, testMove2Effect);
		Move testMove3 = new Move("Test Move 3", Type.BUG, Classification.PHYSICAL, 0, 32, 100, -5, 0, testMove3Effect);
		
		ArrayList<Move> moveset1 = new ArrayList<Move>();
		ArrayList<Move> moveset2 = new ArrayList<Move>();
		moveset1.add(testMove1);
		moveset1.add(testMove2);
		moveset2.add(testMove3);
		
		pOnePoke.setMoveset(moveset1);
		pTwoPoke.setMoveset(moveset2);	
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		System.out.println("---------------------------Special Move-----------------------------");
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		System.out.println("--------------------------------------------------------------------");
		
		System.out.println("---------------------------Status Move-----------------------------");
		results = battle.turn(pOnePoke, pTwoPoke, "12", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		System.out.println("--------------------------------------------------------------------");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testCounterFailure(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test that the move "Mirror Coat" works when the defending
          pokemon is hit with a apecial move
        
        Returns:
        --N/A
	*/
	public void testMirrorCoatSuccess(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Mirror Coat Success____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
				
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMove1Effect = new ArrayList<String>();
		ArrayList<String> testMove2Effect = new ArrayList<String>();
		testMove2Effect.add("Counters Special Damage Opponent");
		
		Move testMove1 = new Move("Test Move 1", Type.BUG, Classification.SPECIAL, 75, 32, 100, 0, (double)6.25, testMove1Effect);
		Move testMove2 = new Move("Test Move 2", Type.BUG, Classification.SPECIAL, 0, 32, 100, -5, 0, testMove2Effect);
		
		ArrayList<Move> moveset1 = new ArrayList<Move>();
		ArrayList<Move> moveset2 = new ArrayList<Move>();
		moveset1.add(testMove1);
		moveset2.add(testMove2);
		
		pOnePoke.setMoveset(moveset1);
		pTwoPoke.setMoveset(moveset2);	
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testMirrorCoatSuccess(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	public void testMirrorCoatFailure(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Mirror Coat Failure____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
				
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMove1Effect = new ArrayList<String>();
		ArrayList<String> testMove2Effect = new ArrayList<String>();
		ArrayList<String> testMove3Effect = new ArrayList<String>();
		testMove2Effect.add("100% Chance To Poison Opponent");
		testMove3Effect.add("Counters Special Damage Opponent");
		
		
		Move testMove1 = new Move("Test Move 1", Type.BUG, Classification.PHYSICAL, 75, 32, 100, 0, (double)6.25, testMove1Effect);
		Move testMove2 = new Move("Test Move 2", Type.BUG, Classification.STATUS, 0, 16, 100, 0, 0, testMove2Effect);
		Move testMove3 = new Move("Test Move 3", Type.BUG, Classification.SPECIAL, 0, 32, 100, -5, 0, testMove3Effect);
		
		ArrayList<Move> moveset1 = new ArrayList<Move>();
		ArrayList<Move> moveset2 = new ArrayList<Move>();
		moveset1.add(testMove1);
		moveset1.add(testMove2);
		moveset2.add(testMove3);
		
		pOnePoke.setMoveset(moveset1);
		pTwoPoke.setMoveset(moveset2);	
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		System.out.println("---------------------------Physical Move-----------------------------");
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		System.out.println("--------------------------------------------------------------------");
		
		System.out.println("---------------------------Status Move-----------------------------");
		results = battle.turn(pOnePoke, pTwoPoke, "12", "1");
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		System.out.println("--------------------------------------------------------------------");
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testMirrorCoatFailure(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that copying a pokemon work properly
        
        Returns:
        --N/A
	*/
	public void testCopyingPokemon(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Copying Pokemon____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
				
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Copy Pokemon Self");
		
		Move testMove = new Move("Test Move", Type.NORMAL, Classification.STATUS, 0, 16, 999, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		printPokemon(pOnePoke);
		
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();			
	} // testCopyingPokemon(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that copying a pokemon's stat stages
          works properly
        
        Returns:
        --N/A
	*/
	public void testCopyingStatStages(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Copying Stat Stages____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		// We want to see if the stages are being copied
		pTwoPoke.setAtkStage(9);
		pTwoPoke.setDefStage(9);
		pTwoPoke.setSpeedStage(5);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Copy Stat_Stages Self");
		
		Move testMove = new Move("Test Move", Type.NORMAL, Classification.STATUS, 0, 16, 999, 0, 0, testMoveEffect);
		
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		
		printCurrentStats(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();		
	} // testCopyingStatStages(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that the pokemon that used a copying move is restored
          after it is swapped out
        
        Returns:
        --N/A
	*/
	public void testRestoreAfterSwap(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Restore After Swap____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Copy Pokemon Self");
		
		Move testMove = new Move("Test Move", Type.NORMAL, Classification.STATUS, 0, 16, 999, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		// Use the copying move
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		printPokemon(pOnePoke);
		
		// Swap the pokemon out
		results = battle.turn(pOnePoke, pTwoPoke, "23", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		printPokemon(pOnePoke);
		
		// Swap it back in
		results = battle.turn(pOnePoke, pTwoPoke, "21", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testRestoreAfterSwap(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure sacrificing moves work
        
        Returns:
        --N/A
	*/
	public void testSacrificingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Sacrificing Move____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Sacrifice Self");
		
		Move testMove = new Move("Test Move", Type.NORMAL, Classification.PHYSICAL, 250, 16, 100, 0, (double)6.25, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);	
		
		if (battle.hasFainted(pOnePoke)) {
			battle.printFaintedMessage("Player 1", pOnePoke.getPokeName());
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testSacrificingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test moves with recoil based on the damage dealt
        
        Returns:
        --N/A
	*/
	public void testRecoilBasedOnDamage(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Recoil Based on Damage____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Recoil 25% Damage Self");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 250, 16, 100, 0, (double)6.25, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testRecoilBasedODamage(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test moves with recoil based on the max HP of the
          attacking pokemon
        
        Returns:
        --N/A
	*/
	public void testRecoilBasedOnMaxHP(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Recoil Based on Max HP____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Recoil 25% Health Self");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 250, 16, 100, 0, (double)6.25, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testRecoilBasedOnMaxHP(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test moves that heal based on the damage dealt
        
        Returns:
        --N/A
	*/
	public void testHealingBasedOnDamage(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Healing Based on Damage____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		pOnePoke.setHP((int)(pOnePoke.getMaxHP()* .5));
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Heal 50% Damage Self");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 250, 16, 100, 0, (double)6.25, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testHealingBasedODamage(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test moves that heal based on the max HP of the
          attacking pokemon
        
        Returns:
        --N/A
	*/
	public void testHealingBasedOnMaxHP(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Healing Based on Health____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		pOnePoke.setHP((int)(pOnePoke.getMaxHP()* .5));
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Heal 50% Health Self");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testHealingBasedOnMaxHP(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team
        --isTeamsFainted:
          --True: All pokemon except the attacking pokemon are fainted
          --False: There are pokemon we can swap to

        Desc/Purpose:
        --Test to see ensure that when the attacking pokemon uses a swapping
          move, it can swap to other pokemon of the player's choice
        
        Returns:
        --N/A
	*/
	public void testForcedSwapAttackingPoke(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean teamsAreFainted) {
		System.out.println("____________________Starting Test: Forced Swap (Attacking Poke)____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		if (teamsAreFainted) { // Simulate the scenario where we use a move that forces a swap but there
			                   // are no other pokemon to swap to
			for (int i = 1; i < teamSize; i++) {
				team1.get(i).setHP(0);
				team2.get(i).setHP(0);
			}
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Swap Self");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 60, 16, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		pOnePoke = results.get(1);
		System.out.println();
		printPokemon(pOnePoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testForcedSwapAttackingPoke(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean teamsAreFainted)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team
        --teamsAreFainted:
          --True: All pokemon except the attacking pokemon are fainted
          --False: There are pokemon we can swap to

        Desc/Purpose:
        --Test to ensure that the opponent will be forced out into
          a different, random pokemon when hit with a swapping move
        
        Returns:
        --N/A
	*/
	public void testForcedSwapDefendingPokeDamagingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean teamsAreFainted) {
		System.out.println("____________________Starting Test: Forced Swap w/ Damage (Defending Poke)____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		if (teamsAreFainted) { // Simulate the scenario where we use a move that forces a swap but there
			                   // are no other pokemon to swap to
			for (int i = 1; i < teamSize; i++) {
				team1.get(i).setHP(0);
				team2.get(i).setHP(0);
			}
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Swap Opponent");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 60, 16, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		System.out.println();
		printPokemon(pTwoPoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testForcedSwapDefendingPoke(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean teamsAreFainted)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team
        --teamsAreFainted:
          --True: All pokemon except the attacking pokemon are fainted
          --False: There are pokemon we can swap to

        Desc/Purpose:
        --Test to ensure that the opponent will be forced out into
          a different, random pokemon when hit with a swapping move
        
        Returns:
        --N/A
	*/
	public void testForcedSwapDefendingPoke(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean teamsAreFainted) {
		System.out.println("____________________Starting Test: Forced Swap (Defending Poke)____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}
		
		if (teamsAreFainted) { // Simulate the scenario where we use a move that forces a swap but there
			                   // are no other pokemon to swap to
			for (int i = 1; i < teamSize; i++) {
				team1.get(i).setHP(0);
				team2.get(i).setHP(0);
			}
		}
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("Swap Opponent");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.STATUS, 0, 16, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		System.out.println();
		printPokemon(pTwoPoke);
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();	
	} // testForcedSwapDefendingPoke(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2, boolean teamsAreFainted)
	
	/*
	 	
        Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that buff moves will continue to buff the
          targeted pokemon until its stats are maxed
        
        Returns:
        --N/A    
	 */
	public void testBuffStatsUntilMax(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Buff Stats Until Max____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("100% Chance To Buff Atk 2 Stages; Buff Def 2 Stages; Buff SpAtk 2 Stages; Buff SpDef 2 Stages; Buff Speed 2 Stages; Buff Accuracy 2 Stages; Buff Evasion 2 Stages Self");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		int numBuffs = 4;
		
		for (int i = 0; i < numBuffs; i++) {
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pOnePoke = results.get(0);
			printCurrentStats(pOnePoke);
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testBuffStatsUntilMax(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that buff moves will continue to debuff the
	      targeted pokemon until its stats at the minimum
        
        Returns:
        --N/A
	 */
	public void testDebuffStatsUntilMin(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Debuff Stats Until Min____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();
		testMoveEffect.add("100% Chance To Debuff Atk 2 Stages; Debuff Def 2 Stages; Debuff SpAtk 2 Stages; Debuff SpDef 2 Stages; Debuff Speed 2 Stages; Debuff Accuracy 2 Stages; Debuff Evasion 2 Stages Opponent");
		
		Move testMove = new Move("Test Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		int numBuffs = 4;
		
		for (int i = 0; i < numBuffs; i++) {
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pTwoPoke = results.get(1);
			printCurrentStats(pTwoPoke);
		}
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testBuffStatsUntilMax(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that we can debuff pokemon to their lowest stats
          and then buff them to their max stats
        
        Returns:
        --N/A
	 */
	public void testMinToMaxStats(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Min to Max Stats____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testDebuffMoveEffect = new ArrayList<String>();
		ArrayList<String> testBuffMoveEffect = new ArrayList<String>();
		testDebuffMoveEffect.add("100% Chance To Debuff Atk 3 Stages; Debuff Def 3 Stages; Debuff SpAtk 3 Stages; Debuff SpDef 3 Stages; Debuff Speed 3 Stages; Debuff Accuracy 3 Stages; Debuff Evasion 3 Stages Opponent");
		testBuffMoveEffect.add("100% Chance To Buff Atk 3 Stages; Buff Def 3 Stages; Buff SpAtk 3 Stages; Buff SpDef 3 Stages; Buff Speed 3 Stages; Buff Accuracy 3 Stages; Buff Evasion 3 Stages Self");
		
		Move debuffMove = new Move("Test Debuff Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testDebuffMoveEffect);
		Move buffMove = new Move("Test Buff Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testBuffMoveEffect);
		
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(debuffMove);
		moveset.add(buffMove);
		pOnePoke.setMoveset(moveset);
		pTwoPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		int numDebuffs = 3;
		int numBuffs = 5;
		
		System.out.println("Debuffing Pokemon...");
		for (int i = 0; i < numDebuffs; i++) {
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pTwoPoke = results.get(1);
			printCurrentStats(pTwoPoke);
		}
		
		System.out.println("Buffing Pokemon...");
		for (int i = 0; i < numBuffs; i++) {
			results = battle.turn(pTwoPoke, pOnePoke, "12", "1");
			pTwoPoke = results.get(0);
			printCurrentStats(pTwoPoke);
		}		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testMinToMaxStats(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that we can buff pokemon to their max stats and
          then debuff them to their lowest stats
        
        Returns:
        --N/A
	 */
	public void testMaxToMinStats(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Max to Min Stats____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testDebuffMoveEffect = new ArrayList<String>();
		ArrayList<String> testBuffMoveEffect = new ArrayList<String>();
		testDebuffMoveEffect.add("100% Chance To Debuff Atk 3 Stages; Debuff Def 3 Stages; Debuff SpAtk 3 Stages; Debuff SpDef 3 Stages; Debuff Speed 3 Stages; Debuff Accuracy 3 Stages; Debuff Evasion 3 Stages Opponent");
		testBuffMoveEffect.add("100% Chance To Buff Atk 3 Stages; Buff Def 3 Stages; Buff SpAtk 3 Stages; Buff SpDef 3 Stages; Buff Speed 3 Stages; Buff Accuracy 3 Stages; Buff Evasion 3 Stages Self");
		
		Move debuffMove = new Move("Test Debuff Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testDebuffMoveEffect);
		Move buffMove = new Move("Test Buff Move", Type.BUG, Classification.STATUS, 0, 16, 999, 0, 0, testBuffMoveEffect);
		
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(debuffMove);
		moveset.add(buffMove);
		pOnePoke.setMoveset(moveset);
		pTwoPoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		int numDebuffs = 5;
		int numBuffs = 3;
		
		System.out.println("Buffing Pokemon...");
		for (int i = 0; i < numBuffs; i++) {
			results = battle.turn(pTwoPoke, pOnePoke, "12", "1");
			pTwoPoke = results.get(0);
			printCurrentStats(pTwoPoke);
		}
		
		System.out.println("Debuffing Pokemon...");
		for (int i = 0; i < numDebuffs; i++) {
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pTwoPoke = results.get(1);
			printCurrentStats(pTwoPoke);
		}	
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testMaxToMinStats(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that moves that can debuff some of the target's stats
          and buff other stats will fail when none of the buffs can be applied
        
        Returns:
        --N/A
	*/
	public void testDebuffingAndBuffingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Debuffing and Buffing Move____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		int numBuffs = 4;		
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();		
		testMoveEffect.add("100% Chance To Debuff Def 1 Stage; Debuff SpDef 1 Stage; Buff Atk 2 Stages; Buff SpAtk 2 Stages; Buff Speed 2 Stages Self");
		Move testMove = new Move("Test Move", Type.BUG, Classification.STATUS, 0, 32, 999, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		for (int i = 0; i < numBuffs; i++) {
			results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
			pOnePoke = results.get(0);
			printCurrentStats(pOnePoke);
		}
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testDebuffingAndBuffingMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that moves that deal damage and buff stat(s)
          work properly
        
        Returns:
        --N/A
	 */
	public void testBuffingAttackMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Buffing Attack Move____________________");
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();		
		testMoveEffect.add("100% Chance To Buff Speed 1 Stages Self");
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 50, 32, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		printCurrentStats(pOnePoke);	
		
		System.out.println();
        System.out.println("Done");
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testBuffingAttackMove(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that moves that deal damage and buff stat(s)
          work properly when the opponent has fainted
        
        Returns:
        --N/A
	 */
	public void testBuffingAttackWhenOpponentFainted(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Buffing Attack When Opponent Fainted___________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);		
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();		
		testMoveEffect.add("100% Chance To Buff Speed 1 Stage Self");
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 50, 32, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		pTwoPoke.setHP(0); // Simulate the opponent being fainted
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		printCurrentStats(pOnePoke);	
		
		System.out.println();
        System.out.println("Done");        
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testBuffingAttackWhenOpponentFainted(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
	
	/*
	 	Parameters:        
        --team1: Player one's team
        --team2: Player two's team

        Desc/Purpose:
        --Test to ensure that moves that can apply a
		  status effect and change an opponent's stats		
        
        Returns:
        --N/A
	 */
	public void testStatusEffectAndStatChange(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
		System.out.println("____________________Starting Test: Status Effect and Stat Change___________________");
		
		// Reset both teams before starting the test		
		int teamSize = team1.size();
		for (int i = 0; i < teamSize; i++) {
			team1.get(i).resetEverything();
			team2.get(i).resetEverything();
		}		
		
		Pokemon pOnePoke = team1.get(0);
		Pokemon pTwoPoke = team2.get(0);
		
		ArrayList<String> testMoveEffect = new ArrayList<String>();		
		testMoveEffect.add("100% Chance to Confusion Opponent");
		testMoveEffect.add("100% Chance To Buff Atk 2 Stages Opponent");
		Move testMove = new Move("Test Move", Type.BUG, Classification.PHYSICAL, 0, 32, 100, 0, 0, testMoveEffect);
		ArrayList<Move> moveset = new ArrayList<Move>();
		moveset.add(testMove);
		
		pOnePoke.setMoveset(moveset);
		
		Battle battle = new Battle(this.typeChart, team1, team2, "Player 1", "Player 2");
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		results = battle.turn(pOnePoke, pTwoPoke, "11", "1");
		pOnePoke = results.get(0);
		pTwoPoke = results.get(1);
		printPokemon(pTwoPoke);
		
		results = battle.turn(pTwoPoke, pOnePoke, "11", "1");
		pTwoPoke = results.get(0);
		printPokemon(pTwoPoke);
		
		System.out.println();
        System.out.println("Done");        
        System.out.println("___________________________________________________________________________");
        System.out.println();
	} // testStatusEffectAndStatChange(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2)
} // Class
