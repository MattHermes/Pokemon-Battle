/*
 Author: Matt Hermes
   File: battle.py
Version: 0.310
   Date: 02/08/20
   Desc: Handles the battle loop.
Change(s):
  --Fixed the following functions
  	--turn
  	  --Checks for effects that target the user
  	    regardless if the opponent has fainted
    TODO: 
    Nothing

 */

package pokemonBattle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Battle extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private final static int NUM_TYPES = 19;
	//private final static int NUM_MULTIPLIERS = 13;
	
	private double[][] typeChart; 
	
	private ArrayList<Pokemon> team1;
	private ArrayList<Pokemon> team2;
	
	private String pOneName;
	private String pTwoName;
	private String attackEffectiveness;
	
	private ArrayList<String> nonVolatileStrings;
	private ArrayList<String> volatileStrings;
	
	private boolean wasCrit;
	private boolean wasImmune;
	private boolean pOneForcedOut;
	private boolean pTwoForcedOut;
	
	private final int MIN_STAGE = -6;
	private final int BASE_STAGE = 0;
	private final int MAX_STAGE = 6;
	
	private final int STRUGGLE_OPTION = 9;
	
	private double[] multipliers;
	private double[] alternateMultipliers; // For accuracy and evasion

	Move struggle;
	
	/*
    	Parameters:
       	--typeChart: Chart used to determine the effectiveness an attack has based on the type
                 	 of the attack and the type(s) of the defending pokemon
        --team1: Player one's team 
    	--team2: Player two's team
    	--pOneName: Player one's name
    	--pTwoName: Player two's name

    	Desc/Purpose:
    	--Constructor for the Battle Class
    
    	Returns:
    	--N/A
	 */
	public Battle(double[][] typeChart, ArrayList<Pokemon>team1, ArrayList<Pokemon>team2, String pOneName, String pTwoName) {
		this.typeChart = typeChart;
		this.team1 = team1;
		this.team2 = team2;
		this.pOneName = pOneName;
		this.pTwoName = pTwoName;
		this.wasCrit = false;
		this.wasImmune = false;
		
		// Multipliers are from https://www.dragonflycave.com/mechanics/stat-stages
		this.multipliers = new double[]{(double)1/4, (double)2/7, (double)1/3, (double)2/5, (double)1/2, (double)2/3, (double)1.0, (double)1.5, (double)2.0, (double)2.5, (double)3.0, (double)3.5, (double)4.0};
		this.alternateMultipliers = new double[] {(double)1/3, (double)3/8, (double)3/7,(double)1/2, (double)3/5, (double)3/4, (double)1.0, (double)4/3, (double)5/3, (double)2.0, (double)7/3, (double)8/3, (double)3.0};
		
		ArrayList<String> struggleEffect = new ArrayList<String>();
		struggleEffect.add("Recoil 25% Health Self");
		this.struggle = new Move("Struggle", Type.NONE, Classification.PHYSICAL, 50, 1000000, 100, 0, (double) 6.25, struggleEffect);
		
		this.pOneForcedOut = false;
		this.pTwoForcedOut = false;	
		
		attackEffectiveness = " ";
		
		nonVolatileStrings = new ArrayList<String>(Arrays.asList(new String[] {"Poison", "Badly_Poison", "Burn", "Paralysis", "Sleep", "Freeze", "None"}));
		volatileStrings = new ArrayList<String>(Arrays.asList(new String[] {"Attraction", "Confusion", "Flinch"}));
	} // Constructor	
	
	/*
    	Parameters:
    	--N/A
    
    	Desc/Purpose:
    	--This is the main loop for the battle that calls other methods as necessary
          until the battle is over
    
    	Returns:
    	--N/A
	 */
	public void battleLoop() {
		boolean keepGoing = true;
		boolean pOneForcedOut, pTwoForcedOut;
		boolean pOneTeamHasFainted = false, pTwoTeamHasFainted = false;	
		boolean pOneForcedSwap, pTwoForcedSwap;
		String pOneOption, pTwoOption;		
		String firstToAct;
		Pokemon pOnePoke, pTwoPoke;
		ArrayList<Pokemon> results = new ArrayList<Pokemon>(); // Stores the results of a turn 
		                                                       // Note: index '0' is for the attacking pokemon
															   //       index '1' is for the defending pokemon
		pOnePoke = this.team1.get(0);
		pTwoPoke = this.team2.get(0);      
		
		while(keepGoing) {
			pOneOption = "";
			pTwoOption = "";
			firstToAct = "";
			pOneForcedOut = false;
			pTwoForcedOut = false;	
			pOneForcedSwap = false;
			pTwoForcedSwap = false;
			
			// Get both player's input
			pOneOption = battleMenu(pOneName, pOnePoke, pTwoPoke);
			pTwoOption = battleMenu(pTwoName, pTwoPoke, pOnePoke);
			
			// Check for turn order based on speed, the priority of the move, and if one
			// or both players swapped pokemon
			firstToAct = determineFirstToAct(pOnePoke, pTwoPoke, pOneOption, pTwoOption);
										
			// Execute each player's turn based on their choices and who is first to act
			if (firstToAct.compareTo("1")== 0) {
				results = turn(pOnePoke, pTwoPoke, pOneOption, firstToAct);
				
				// Update the pokemon
				pOnePoke = results.get(0);
				pTwoPoke = results.get(1);
				
				// Check to see if player two's pokemon was not forced out
				if (pTwoForcedOut == false) {
					results = turn(pTwoPoke, pOnePoke, pTwoOption, firstToAct);
					// Update the pokemon
					pTwoPoke = results.get(0);
					pOnePoke = results.get(1);
				}				
			}
			
			else if (firstToAct.compareTo("2")== 0) {
				results = turn(pTwoPoke, pOnePoke, pTwoOption, firstToAct);				
				
				// Update the pokemon
				pTwoPoke = results.get(0);
				pOnePoke = results.get(1);				
				
				// Check to see if player one's pokemon was not forced out
				if (pOneForcedOut == false) {
					results = turn(pOnePoke, pTwoPoke, pOneOption, firstToAct);					
					
					// Update the pokemon
					pOnePoke = results.get(0);
					pTwoPoke = results.get(1);					
				}				
			}
			// Reset hitByClassification and recentDamageTaken for both pokemon
			pOnePoke.setHitByClassification(Classification.NONE);
            pOnePoke.setRecentDamageTaken(0);

            pTwoPoke.setHitByClassification(Classification.NONE);
            pTwoPoke.setRecentDamageTaken(0);
            
			// Apply status effect damage to any afflicted pokemon
			// Note: The speed stats of both pokemon determines
			//       ones takes damage first. This is different from
            //       "firstToAct" because "firstToAct" is dependent on 
            //   	 multiple factors.
            String firstToTakeDamage = compareSpeedStats(pOnePoke, pTwoPoke);
            
            if (pOnePoke.getNonVolatileStatus() == NonVolatileStatus.BADLY_POISON) {
            	pOnePoke.setBadlyPoisonedTurns(pOnePoke.getBadlyPoisonedTurns() + 1);
            }
            
            if (pTwoPoke.getNonVolatileStatus() == NonVolatileStatus.BADLY_POISON) {
            	pTwoPoke.setBadlyPoisonedTurns(pTwoPoke.getBadlyPoisonedTurns() + 1);
            }
            
            int statusDamagePOne = calcNonVolatileStatusDamage(pOnePoke);
            int statusDamagePTwo = calcNonVolatileStatusDamage(pTwoPoke);
            
            if (firstToTakeDamage.compareTo("1") == 0) {
            	if (!hasFainted(pOnePoke)) {
            		if (statusDamagePOne > 0) {
            			pOnePoke = updateHealth(pOnePoke, statusDamagePOne, 0);
            			printEndOfTurnDamageMessage(pOnePoke, statusDamagePOne);
            		}
            	}
            	
            	if (!hasFainted(pTwoPoke)) {
            		if (statusDamagePTwo > 0) {
            			pTwoPoke = updateHealth(pTwoPoke, statusDamagePTwo, 0);
            			printEndOfTurnDamageMessage(pTwoPoke, statusDamagePTwo);
            		}
            	}
            }
            
            else if (firstToTakeDamage.compareTo("2") == 0) {
            	if (!hasFainted(pTwoPoke)) {
            		if (statusDamagePTwo > 0) {
            			pTwoPoke = updateHealth(pTwoPoke, statusDamagePTwo, 0);
            			printEndOfTurnDamageMessage(pTwoPoke, statusDamagePTwo);
            		}
            	}
            	
            	if (!hasFainted(pOnePoke)) {
            		if (statusDamagePOne > 0) {
            			pOnePoke = updateHealth(pOnePoke, statusDamagePOne, 0);
            			printEndOfTurnDamageMessage(pOnePoke, statusDamagePOne);
            		}
            	}            	
            }
            
            String pOneSwapOption = "", pTwoSwapOption = "";
			
			// Check to see if any of the pokemon have fainted
            if (hasFainted(pOnePoke)) {
				printFaintedMessage(pOneName, pOnePoke.getPokeName());
				pTwoPoke.resetAttraction();
				
				if (hasTeamFainted(team1)) {
					pOneTeamHasFainted = true;
				}
				
				else {
					pOneSwapOption = swapMenu(this.pOneName, pOnePoke, team1, true);
					pOneSwapOption = "2" + pOneSwapOption; // turn expects a '2' before the swap option
					pOneForcedSwap = true;					
				}
			}
			
			if (hasFainted(pTwoPoke)) {
				printFaintedMessage(pTwoName, pTwoPoke.getPokeName());
				pOnePoke.resetAttraction();
				
				if (hasTeamFainted(team2)) {
					pTwoTeamHasFainted = true;
				}	
				
				else {
					pTwoSwapOption = swapMenu(this.pTwoName, pTwoPoke, team2, true);
					pTwoSwapOption = "2" + pTwoSwapOption; // turn expects a '2' before the swap option
					pTwoForcedSwap = true;					
				}
			}           
			
			// Print the appropriate message if at least one team
			// is out of usable pokemon
			if (pOneTeamHasFainted && pTwoTeamHasFainted) {
				printTieMessage();
				keepGoing = false;
			}
			
			else if (pOneTeamHasFainted) {
				printVictoryMessage(this.pTwoName);
				keepGoing = false;
			}
			
			else if (pTwoTeamHasFainted) {
				printVictoryMessage(this.pOneName);
				keepGoing = false;
			}
			
			// Force a player to swap if one of their pokemon has fainted
			if (pOneForcedSwap) {
				results = turn(pOnePoke, pTwoPoke, pOneSwapOption, firstToAct);
				pOnePoke = results.get(0);
				pTwoPoke = results.get(1);
			}
			
			if (pTwoForcedSwap) {
				results = turn(pTwoPoke, pOnePoke, pTwoSwapOption, firstToAct);
				pTwoPoke = results.get(0);
				pOnePoke = results.get(1);
			}
			
			
			// Reset it so that we can assume that neither pokemon has been forced out via
			// a swapping move that forces the opponent out
			this.pOneForcedOut = false;
			this.pTwoForcedOut = false;						
		}
	} // battleLoop()
	
	/*
    	Parameters:
    	--playerName: The name of the player picking the option
    	--attackingPoke: The pokemon that belongs to the player currently
      	  selecting the menu option 
    	--defendingPoke: The pokemon that belongs to the opponent 

    	Desc/Purpose:
    	--The main menu used by the player
    
    	Returns:
    	--A string where the first digit indicates what menu the option came
    	  from and the second digit indicates the the menu option from said
    	  menu
	 */
	public String battleMenu(String playerName, Pokemon attackingPoke, Pokemon defendingPoke) {
		boolean selectingOption = true;
		List<Move> moveset = attackingPoke.getMoveset();
		String atkPokeName = attackingPoke.getPokeName();
		Type atkPrimaryType = attackingPoke.getPrimaryType();
		Type atkSecondaryType = attackingPoke.getSecondaryType();
		Gender atkGender = attackingPoke.getGender();
		int atkCurrHP = attackingPoke.getHP();
		int atkMaxHP = attackingPoke.getMaxHP();
		NonVolatileStatus atkNonVolatileStatus = attackingPoke.getNonVolatileStatus();
		String atkPokeID = attackingPoke.getPokeID();
		char atkOwner = atkPokeID.charAt(0);
		
		String defPokeName = defendingPoke.getPokeName();
		Type defPrimaryType = defendingPoke.getPrimaryType();
		Type defSecondaryType = defendingPoke.getSecondaryType();
		Gender defGender = defendingPoke.getGender();
		int defCurrHP = defendingPoke.getHP();
		int defMaxHP = defendingPoke.getMaxHP();
		NonVolatileStatus defNonVolatileStatus = defendingPoke.getNonVolatileStatus();
		
		String option;
		Scanner scanner = new Scanner(System.in);
		while (selectingOption) {
			System.out.println("**********************************Currently Out**********************************");
			System.out.println("Your Pokemon: " + atkPokeName);
			printPokemonTypes(atkPrimaryType, atkSecondaryType);
			System.out.println("Gender: " + atkGender);
			System.out.println("HP: " + atkCurrHP + " / " + atkMaxHP);
			System.out.println("Status: " + atkNonVolatileStatus);
			System.out.println();
			System.out.println("Opponent's Pokemon: " + defPokeName);
			printPokemonTypes(defPrimaryType, defSecondaryType);
			System.out.println("Gender: " + defGender);
			System.out.println("HP: " + defCurrHP + " / " + defMaxHP);
			System.out.println("Status: " + defNonVolatileStatus);
			System.out.println();
			System.out.println("*********************************************************************************");
			System.out.println();
			System.out.println("**********************************Select an option**********************************");
			System.out.println("1) Attack");
			System.out.println("2) Swap");
			System.out.println("3) Summary");			
			System.out.println("************************************************************************************");
			System.out.println();
			option = scanner.nextLine();
			
			if (option.compareTo("1") == 0) {
				if (!hasUsableMoves(moveset)) { // The attacking pokemon is out of usable moves
					//scanner.close();
					return "1" + Integer.toString(this.STRUGGLE_OPTION); // The pokemon will use the move "Struggle"
				}
				
				boolean selectingMove = true;
				
				while(selectingMove) { // Keep going until the player chooses a move or changes their mind
					String moveOption = moveMenu(playerName, attackingPoke.getMoveset());
					
					if ((moveOption.compareTo("1") == 0) || (moveOption.compareTo("2") == 0) || 
							(moveOption.compareTo("3") == 0) || (moveOption.compareTo("4") == 0)){ // The player selected a move
						//scanner.close();
						return "1" + moveOption;
					}
					
					else if ((moveOption.compareTo("B") == 0) || (moveOption.compareTo("b") == 0)) { // The player changed their mind
						selectingMove = false;
					}
				}				
			}
			
			else if (option.compareTo("2") == 0) {				
				boolean selectingPokemon = true;
				while(selectingPokemon) {
					String swapOption = "";
					if (atkOwner == '1') {
						swapOption = swapMenu(playerName, attackingPoke, this.team1, false);
					}
					
					else if (atkOwner == '2') {
						swapOption = swapMenu(playerName, attackingPoke, this.team2, false);
					}
					
					if ((swapOption.compareTo("1") == 0) || (swapOption.compareTo("2") == 0) || 
							(swapOption.compareTo("3") == 0) || (swapOption.compareTo("4") == 0) ||
							(swapOption.compareTo("5") == 0) || (swapOption.compareTo("6") == 0)){ // The player selected a move
						//scanner.close();
						return "2" + swapOption;
					}
					
					else if ((swapOption.compareTo("B") == 0) || (swapOption.compareTo("b") == 0)) { // The player changed their mind
						selectingPokemon = false;
					}
				}								
			}
			
			else if (option.compareTo("3") == 0) {				
				boolean selectingSummary = true;
				int teamSize = this.team1.size();
				String summaryOption = "";
				
				while(selectingSummary) {
					if (atkOwner == '1') {
						summaryOption = summaryMenu(this.pOneName, this.team1);
						if ((summaryOption.compareTo("1") == 0) || (summaryOption.compareTo("2") == 0) || 
								(summaryOption.compareTo("3") == 0) || (summaryOption.compareTo("4") == 0) ||
								(summaryOption.compareTo("5") == 0) || (summaryOption.compareTo("6") == 0)){ // The player selected a move
							viewSummary(Integer.valueOf(summaryOption), team1);
							
						}
						
						else if (summaryOption.compareTo("B") == 0 || summaryOption.compareTo("b") == 0) {
							selectingSummary = false;
						}						
					}
					
					else if (atkOwner == '2') {
						summaryOption = summaryMenu(this.pTwoName, this.team2);
						if ((summaryOption.compareTo("1") == 0) || (summaryOption.compareTo("2") == 0) || 
								(summaryOption.compareTo("3") == 0) || (summaryOption.compareTo("4") == 0) ||
								(summaryOption.compareTo("5") == 0) || (summaryOption.compareTo("6") == 0)){ // The player selected a move
							viewSummary(Integer.valueOf(summaryOption), team2);
							
						}
						
						else if (summaryOption.compareTo("B") == 0 || summaryOption.compareTo("b") == 0) {
							selectingSummary = false;
						}						
					}
				}
			}
			
			else {
				System.out.println("That is an invalid option!");
			}
		}	
		//scanner.close();
		return null; // We should never get here
	} // battleMenu(String playerName, Pokemon attackingPoke, Pokemon defendingPoke)
	
	/*
    	Parameters:
      	--playerName: The name of the player selecting the move
    	--moveset: The moveset of the pokemon that will be using the selected move        

    	Desc/Purpose:
    	--The menu a player uses to select their pokemon's move
    
    	Returns:
    	--The option the player chose
	 */
	public String moveMenu(String playerName, List<Move> moveset) {		
		int numMoves = moveset.size();
		ArrayList<String> validOptions = new ArrayList<String>();
		boolean keepGoing = true;
		Scanner scanner = new Scanner(System.in);
		String option;
		
		while(keepGoing) {
			System.out.println("**********************Select a move " + playerName + "**********************");
			for (int i = 0; i < numMoves; i++) {
				Move move = moveset.get(i);
				if (move != null) {
					String str = Integer.toString(i+1);
					System.out.println(str + ") " + move.getMoveName() + " " + move.getPP() + " / " +  move.getMaxPP());
					validOptions.add(str);
				}
				else { // For pokemon with less than four moves
					System.out.println("-------------------");
				}
			}
			System.out.println("B)ack");
			validOptions.add("B");
			validOptions.add("b");
			
			System.out.println();
			option = scanner.nextLine();
			
			if (validOptions.contains(option)) {
				//scanner.close();
				return option;
			}
			
			else {
				System.out.println("That is an invalid option!");
			}
		}
		//scanner.close();
		return null; // We should never get here
	} // moveMenu(String playerName, Pokemon pokemon)
	
	/*
	 	Parameters:
        --self: A reference to the instance whose method was called
        --playerName: The name of the player selecting the move
        --currentPoke: The pokemon that is currently out
        --team: The player's team
        --forcedSwap: Used to determine if the player has to swap

        Desc/Purpose:
        --The menu a player uses to select their pokemon's move
        
        Returns:
        --The option the player chose
	 */
	public String swapMenu(String playerName, Pokemon currentPoke, ArrayList<Pokemon> team, boolean forcedSwap) {
		boolean selectingOption = true;
		ArrayList<String> validOptions = new ArrayList<String>(Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6", "B", "b"}));
		String option;
		Scanner scanner = new Scanner(System.in);
		
		while (selectingOption) {
			System.out.println("**********************Select a pokemon " + playerName + "**********************");
			int teamSize = team.size();
			
			for (int i = 0; i < teamSize; i++) {
				Pokemon pokemon = team.get(i);
				System.out.println((i+1) + ") " + pokemon.getPokeName() + " HP: " + pokemon.getHP() + " / " + pokemon.getMaxHP());
				System.out.println("Gender: " + pokemon.getGender());
				System.out.println("Status: " + pokemon.getNonVolatileStatus());
				System.out.println();
			}
			System.out.println("B)ack");
			System.out.println("********************************************************************************");
			System.out.println();
			
			option = scanner.nextLine();
			
			if (validOptions.contains(option)) {
				if (isInteger(option)) {
					if (isValidSwap(currentPoke, team, option)) {
						//scanner.close();
						return option;
					}
				}
				
				else if ((option.compareTo("B") == 0) || (option.compareTo("b") == 0)) {
					if (forcedSwap) { // The player must swap to a different pokemon
						System.out.println("You must select a pokemon!");
					}
					else {
						//scanner.close();
						return option;
					}
				}
			}
			else {
				System.out.println("That is an invalid option!");
			}
		}
		//scanner.close();
		return null; // We should never reach this point
	} // swapMenu(String playerName, Pokemon currentPoke, ArrayList<Pokemon> team, boolean forcedSwap)
	
	/*
	 	Parameters:        
        --playerName: The name of the player selecting the move        
        --team: The player's team        

        Desc/Purpose:
        --The menu a player uses to view a summary their team
        
        Returns:
        --The option the player chose
	 */
	public String summaryMenu(String playerName, ArrayList<Pokemon> team) {
		boolean selectingOption = true;
		ArrayList<String> validOptions = new ArrayList<String>((Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6", "B", "b"})));
		String option;
		Scanner scanner = new Scanner(System.in);		
		
		while (selectingOption) {
			System.out.println("**********************Select a pokemon " + playerName + "**********************");
			int teamSize = team.size();
			
			for (int i = 0; i < teamSize; i++) {
				Pokemon pokemon = team.get(i);
				System.out.println((i+1) + ") " + pokemon.getPokeName() + " HP: " + pokemon.getHP() + " / " + pokemon.getMaxHP());
				System.out.println("Gender: " + pokemon.getGender());
				System.out.println("Status: " + pokemon.getNonVolatileStatus());
				System.out.println();
			}
			System.out.println("B)ack");
			System.out.println("********************************************************************************");
			System.out.println();
			
			option = scanner.nextLine();
			
			if (validOptions.contains(option)) {
				if (isInteger(option)) {					
					//scanner.close();
					return option;					
				}
				
				else if ((option.compareTo("B") == 0) || (option.compareTo("b") == 0)) {
					//scanner.close();
					return option;					
				}
			}
			else {
				System.out.println("That is an invalid option!");
			}
		}
		
		return null;
	} // summaryMenu(String playerName, ArrayList<Pokemon> team)
	
	/*
	 	Parameters:        
        --summaryOption: The option the player picked from the
          summary menu
        --team: The player's team        

        Desc/Purpose:
        --Displays a summary of the selected pokemon
        
        Returns:
        --The option the player chose
	 */
	public void viewSummary(int summaryOption, ArrayList<Pokemon> team) {
		System.out.println();
		Pokemon pokemon = team.get(summaryOption - 1);
		printGeneralInfo(pokemon);
		printStats(pokemon);
	} // viewSummary(int summaryOption, ArrayList<Pokemon> team)
	
	/*
	 	Parameters:        
        --pokemon: The selected pokemon 

        Desc/Purpose:
        --To allow the player to view:
          --Name,
          --HP,
          --Typing,
          --Non-Volatile status, and
          --Moveset
          of the selected pokemon
            
        Returns:
        --N/A
	 */
	public void printGeneralInfo(Pokemon pokemon) {
		System.out.println("Name: " + pokemon.getPokeName());
		System.out.println("HP: " + pokemon.getHP() + " / " + pokemon.getMaxHP());
		printPokemonTypes(pokemon.getPrimaryType(), pokemon.getSecondaryType());
		System.out.println("Gender: " + pokemon.getGender());
		System.out.println("Status: " + pokemon.getNonVolatileStatus());
		System.out.println();
		printMoveset(pokemon.getMoveset());
		System.out.println();		
	} //  printGeneralInfo(Pokemon pokemon)
	
	/*
	 	Parameters:        
        --moveset: The moveset of the selected pokemon 

        Desc/Purpose:
        --To allow the player to view the moveset of the selected
          pokemon
            
        Returns:
        --N/A
	 */
	public void printMoveset(List<Move> moveset) {
		System.out.println("Moveset:");
		int numMoves = moveset.size();
		
		for (int i = 0; i < numMoves; i++) {
			Move move = moveset.get(i);
			if (move != null) {
				System.out.println(move.getMoveName() + ": " + move.getPP() + " / " + move.getMaxPP());
			}
			
			else {
				System.out.println("--------------------");
			}			
		}
		System.out.println();
	} // printMoveset(ArrayList<Move> moveset)
	
	/*
	 	Parameters:        
        --pokemon: The selected pokemon 

        Desc/Purpose:
        --To allow the player to view:
          --Atk,
          --Def,
          --SpAtk,
          --SpDef, and
          --Speed
          of the selected pokemon
            
        Returns:
        --N/A
	 */
	public void printStats(Pokemon pokemon) {
		System.out.println("Stats:");
		System.out.println("--HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP());
        System.out.println("--Attack: " + pokemon.getAtk() + " Attack Stage: " + pokemon.getAtkStage());
        System.out.println("--Defense: " + pokemon.getDef() + " Defense Stage: " + pokemon.getDefStage());
        System.out.println("--Special Attack: " + pokemon.getSpAtk() + " Special Attack Stage: " + pokemon.getSpAtkStage());
        System.out.println("--Special Defense: " + pokemon.getSpDef() + " Special Defense Stage: " + pokemon.getSpDefStage());
        System.out.println("--Speed: " + pokemon.getSpeed() + " Speed Stage: " + pokemon.getSpeedStage());
        System.out.println();
	} // printStats(Pokemon pokemon)
	
	/*
		Parameters:    	
		--str: The string to check		

		Desc/Purpose:
		--To determine if a string is an integer 

		Returns:
		--N/A
	 */
	public boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("\\d+");
		
		if (str == null) {
			return false;
		}
		return pattern.matcher(str).matches();
	} // isInteger(String str)
	
	/*
    	Parameters:    	
    	--primaryType: The primary type of the pokemon
    	--secondaryType: The secondary type of the pokemon

    	Desc/Purpose:
    	--To display a pokemon's type(s)
    
    	Returns:
    	--N/A
	 */
	public void printPokemonTypes(Type primaryType, Type secondaryType) {
		if (secondaryType != Type.NONE) {
			System.out.println("Type: " + primaryType + "/" + secondaryType);
		}
		
		else {
			System.out.println("Type: " + primaryType);
		}
	} // printPokemonTypes(Type primaryType, Type secondaryType)
	
	/*
    	Parameters:
       	--moveset: A pokemon's moveset        

    	Desc/Purpose:
    	--To determine if any of the moves in the moveset have
      	  remaining PP
    
    	Returns:
    	--True if the moveset has at least one move with PP left
    	--False, otherwise
	 */
	public boolean hasUsableMoves(List<Move> moveset) {
		int numMoves = moveset.size();
		
		for (int i = 0; i < numMoves; i++) {
			if (hasRemainingUses(moveset.get(i))) {
				return true;
			}
		}
		return false;
	} // hasUsableMove(ArrayList<Move> moveset)
	
	/*
    	Parameters:
        --move: The move that the player wants to use        

    	Desc/Purpose:
    	--To determine if the move has any remaining PP (uses)
    
    	Returns:
    	--True if the move has PP left
    	--False, otherwise
	 */
	public boolean hasRemainingUses(Move move) {
		if (move.getPP() <= 0) {
			return false;
		}
		return true;
	} // hasRemainingUses(Move move)
	
	/*
	 	Parameters:               
        --currentPoke: The pokemon that is currently out
        --team: The player's team
        --swapOption: The player's swap option

        Desc/Purpose:
        --To determine if the swap can occur
        
        Returns:
        --True if the swap is valid
        --False, otherwise
	 */
	public boolean isValidSwap(Pokemon currentPoke, ArrayList<Pokemon> team, String swapOption) {
		int index = Integer.parseInt(swapOption) - 1;
		
		String currentPokeID = currentPoke.getPokeID();
		Pokemon newPoke = team.get(index);
		String newPokeID = newPoke.getPokeID();
		
		if (hasFainted(newPoke)) {
			System.out.println("That pokemon has fainted!");
			return false;
		}
		
		else if (currentPokeID.compareTo(newPokeID) == 0) {
			System.out.println("That pokemon is already out!");
			return false;
		}
		return true;		
	} // isValidSwap(Pokemon currentPoke, ArrayList<Pokemon> team, String swapOption)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to be swapped
        --team: The team the pokemon belongs to

        Desc/Purpose:
        --To determine if there are any other pokemon that can
          be swapped to
        
        Returns:
        --True if there are is another pokemon that can
          be swapped to
        --False, otherwise
	 */
	public boolean hasValidSwap(Pokemon pokemon, ArrayList<Pokemon> team) {
		int teamSize = team.size();		
		String myPokeID = pokemon.getPokeID();
		ArrayList<Pokemon> otherPokemon = new ArrayList<Pokemon>();
		String pokeID = "";
		Pokemon p;
		
		for (int i = 0; i < teamSize; i++) {
			p = team.get(i);
			pokeID = p.getPokeID();
			
			if (pokeID.compareTo(myPokeID) != 0) { // We don't want the current pokemon in the list
				if (!hasFainted(p)) {
					otherPokemon.add(p);
				}
			}
		}
		
		if (!otherPokemon.isEmpty()) {			
			return true;
		}
		return false;
	} // hasValidSwap(Pokemon pokemon, ArrayList<Pokemon> team)
	
	/*
	 	Parameters:
        --self: A reference to the instance whose method was called
        --pOnePoke: Player one's current pokemon
        --pTwoPoke: Player two's current pokemon
        --pOneOption: Player one's option
        --pTwoOption: Player two's option        
          
        Desc/Purpose:
        --To determine which pokemon acts first based on each player's choice
        
        Returns:
        --"1" if player one's pokemon acts first
        --"2" if player two's pokemon acts first
	*/
	public String determineFirstToAct(Pokemon pOnePoke, Pokemon pTwoPoke, String pOneOption, String pTwoOption) {
		// First check to see if either player swapped because swapping has the highest priority
		char menuOption1 = pOneOption.charAt(0);
		char menuOption2 = pTwoOption.charAt(0);
		
		if (menuOption1 == '2' && menuOption2 == '2') { // Both players want to swap
			return compareSpeedStats(pOnePoke, pTwoPoke); // See who swaps first
		}
		
		else if (menuOption1 == '2') { // Only player one wanted to swap
			return "1";
		}
		
		else if (menuOption2 == '2') { // Only player two wanted to swap
			return "2";
		}
		
		// Neither player wanted to swap so compare priorities to see if either
		// player picked a move with a higher priority
		char option1 = pOneOption.charAt(1);
		char option2 = pOneOption.charAt(1);
		
		List<Move> pOneMoveset = pOnePoke.getMoveset();
		Move pOneMove = pOneMoveset.get(Character.getNumericValue(option1) - 1);
		List<Move> pTwoMoveset = pTwoPoke.getMoveset();
		Move pTwoMove = pTwoMoveset.get(Character.getNumericValue(option2) - 1);
		
		String priorityResult = comparePriorities(pOneMove.getPriority(), pTwoMove.getPriority());
		
		if ((priorityResult.compareTo("1") == 0) || (priorityResult.compareTo("2") == 0)) {
			return priorityResult;
		}
		
		else {
			return compareSpeedStats(pOnePoke, pTwoPoke);
		}
		
		// Compare the speed stats of the two pokemon if both players picked a 
		// a move with equal priority
	} // determineFirstToAct(Pokemon pOnePoke, Pokemon pTwoPoke, String pOneOption, String pTwoOption)
	
	/*
    	Parameters:
    	--self: A reference to the instance whose method was called
    	--pOnePriority: The priority of the move that player one selected         
    	--pTwoPriority: The priority of the move that player two selected
    
    	Desc/Purpose:
    	--To determine which pokemon acts first based the priority of the
      	  selected moves
    
    	Returns:
    	--"1" if player one's pokemon is using a move with a higher priority
    	--"2" if player two's pokemon is using a move with a higher priority
    	--"tie" if the priorities are equal
	*/
	public String comparePriorities(int pOnePriority, int pTwoPriority) {
		if (pOnePriority > pTwoPriority) {
			return "1";
		}
		
		else if (pOnePriority < pTwoPriority) {
			return "2";
		}
		
		return "tie";
	} // comparePriorities(int pOnePriority, int pTwoPriority)
	
	/*
    Parameters:
    --self: A reference to the instance whose method was called
    --pOnePoke: Player one's current pokemon
    --pTwoPoke: Player two's current pokemon
              
    	Desc/Purpose:
    	--To determine which pokemon acts first based on each pokemon's
    	  Speed stat
    
    	Returns:
    	--"1" if player one's pokemon has a higher Speed stat
    	--"2" if player two's pokemon has a higher Speed stat
	*/
	public String compareSpeedStats(Pokemon pOnePoke, Pokemon pTwoPoke) {
		int pOnePokeSpeed = pOnePoke.getSpeed();
		int pOnePokeSpeedStage = pOnePoke.getSpeedStage();
		double pOnePokeSpeedMultiplier = this.multipliers[pOnePokeSpeedStage + 6];
		double pOnePokeFinalSpeed = pOnePokeSpeed * pOnePokeSpeedMultiplier;
		
		int pTwoPokeSpeed = pTwoPoke.getSpeed();
		int pTwoPokeSpeedStage = pTwoPoke.getSpeedStage();
		double pTwoPokeSpeedMultiplier = this.multipliers[pTwoPokeSpeedStage + 6];
		double pTwoPokeFinalSpeed = pTwoPokeSpeed * pTwoPokeSpeedMultiplier;
		
		double speedReduc = 0.5;
		
		// Paralyzed pokemon have their speed reduced
		if (pOnePoke.getNonVolatileStatus() == NonVolatileStatus.PARALYSIS) {
			pOnePokeFinalSpeed *= speedReduc;
		}
		
		if (pTwoPoke.getNonVolatileStatus() == NonVolatileStatus.PARALYSIS) {
			pTwoPokeFinalSpeed *= speedReduc;
		}
		
		// Compare the final speeds
		if (pOnePokeFinalSpeed > pTwoPokeFinalSpeed) {
			return "1";
		}
		
		else if (pOnePokeFinalSpeed < pTwoPokeFinalSpeed) {
			return "2";
		}
		
		else {
			return resolveSpeedTie();
		}
	} // compareSpeedStats(Pokemon pOnePoke, Pokemon pTwoPoke)
	
	/*
    	Parameters:
    	--N/A
                      
    	Desc/Purpose:
    	--To determine which pokemon acts first based on a coin flip
    
    	Returns:
    	--"1" if player one's pokemon won the Speed tie
    	--"2" if player two's pokemon won the Speed tie
    */
	public String resolveSpeedTie() {
		Random rand = new Random();
		int result = rand.nextInt(1); // Flip a coin
		
		if (result == 0) {
			return "1";
		}
		
		else if (result == 1) {
			return "2";
		}
		
		else {
			System.out.println(result + " is not what we wanted.");
			return null;
		}
	} // resolveSpeedTie()
	
	/*
    	Parameters:
        --attackingPoke: The pokemon that will either:
          --use a selected move against the opponent's pokemon or
          --be swapped out for a different pokemon on the current player's team
        --defendingPoke: The opposing player's pokemon
        --playerOption: The attacking player's option
        --atkPokeIndex: The index the attacking pokemon is stored at (May not use)
        --defPokeIndex: The index the defending pokemon is stored at (May not use)
        --firstToAct: The pokemon that is the first to use an action (either swapping or
      using a move)

    Desc/Purpose:
    --To execute a player's turn based on his/her choice
    
    Returns:
    --An arraylist of pokemon that contains the attacking pokemon and the defending pokemon
    */
	public ArrayList<Pokemon> turn(Pokemon attackingPoke, Pokemon defendingPoke, String playerOption, String firstToAct) {
		String atkPokeName = attackingPoke.getPokeName();
		String defPokeName = defendingPoke.getPokeName();
		String atkPokeID = attackingPoke.getPokeID();		
		String atkOwner = atkPokeID.substring(0,1);
		
		String moveName = "";
		int damage = 0;
		double typeDamage = -1.0; // -1.0 indicates that a non-damaging move was used
		List<String> moveEffects = new ArrayList<String>();
		this.wasImmune = false;
		Type moveType = Type.NONE;
		Classification moveClassification = Classification.NONE;
		int basePower = 0;
		
		String defPokeID = defendingPoke.getPokeID();		
		String defOwner = defPokeID.substring(0,1);
		Move atkPokeMove = null;
		
		NonVolatileStatus nonVolatileStatus;
		ArrayList<VolatileStatus> volatileStatuses = new ArrayList<VolatileStatus>();
		
		boolean needToReturn = true;
		
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();	
		char menuOption = playerOption.charAt(0); // The first digit determines which menu we selected an option from
		
		// Immediately end the turn if the pokemon that is supposed to
		// attack recently fainted AND we have not yet forced that player 
		// to swap
		if (hasFainted(attackingPoke)) {
			if (menuOption != '2') {
				results.add(attackingPoke);
				results.add(defendingPoke);				
				return results;
			}
		}				
		
		if (menuOption == '1') { // The player selected a move
			char charMoveOption = playerOption.charAt(1);
			int intMoveOption = Character.getNumericValue(charMoveOption);
			
			
			if (intMoveOption < this.STRUGGLE_OPTION) {
				atkPokeMove = getPokeMove(attackingPoke, intMoveOption);
			}
				
			else if (intMoveOption == this.STRUGGLE_OPTION){ // Use "struggle" if the pokemon is out of usable moves
				atkPokeMove = getStruggleMove();
			}
			moveName = atkPokeMove.getMoveName();
			moveType = atkPokeMove.getMoveType();
			moveClassification = atkPokeMove.getClassification();
			moveEffects = atkPokeMove.getEffects();
			basePower = atkPokeMove.getBasePower();
				
			nonVolatileStatus = attackingPoke.getNonVolatileStatus();
			volatileStatuses = attackingPoke.getVolatileStatuses();
			
			// Check to see if the opponent has fainted before moving on
			if (hasFainted(defendingPoke)) {				
				if (dealsDirectDamage(moveClassification)) {					
					printUsedMoveMessage(atkPokeName, moveName);
					printMoveFailedMessage();
					attackingPoke = updatePowerPoints(attackingPoke, intMoveOption);
					results.add(attackingPoke);
					results.add(defendingPoke);				
					return results;
				}
				
				// The move fails if it has an effect that targets the opponent
				int numEffects = moveEffects.size();
				String splitBy = " ";
				for (int i = 0; i < numEffects; i++) {
					String effect = moveEffects.get(i);
					String[] words = effect.split(splitBy);
					//String keyword = getKeyword(words);
					Target effectTarget = convertStringToTarget(words[words.length - 1]);
					
					if (effectTarget == Target.OPPONENT) {
						printUsedMoveMessage(atkPokeName, moveName);
						printMoveFailedMessage();
						attackingPoke = updatePowerPoints(attackingPoke, intMoveOption);
						results.add(attackingPoke);
						results.add(defendingPoke);				
						return results;
					}
				}
			}
			// Handle the case where the attacking pokemon is asleep
			if (nonVolatileStatus == NonVolatileStatus.SLEEP) {
				attackingPoke = handleSleepStatus(attackingPoke);
				
				nonVolatileStatus = attackingPoke.getNonVolatileStatus();
				
				// End the turn if the attacking pokemon is still asleep
				if (nonVolatileStatus == NonVolatileStatus.SLEEP) {
					results.add(attackingPoke);
					results.add(defendingPoke);				
					return results;
				}
			}			
				
			// Check to see if the attacking pokemon can use a move before checking anything else
	        // (i.e., it has status effect that has a chance to prevent the attacking pokemon from
	        // from using a move)
	        // Note: The status effects checked for are:
	        // --Paralysis
	        // --Freeze
	        // --Confusion
	        // --Attraction
	        // Note 2: We also check to see if the pokemon flinched when
	        //   it was hit		
			if (hasConfusionStatus(volatileStatuses)) { // Confused pokemon have a chance of hurting themselves
				attackingPoke = handleConfusionStatus(attackingPoke);
				volatileStatuses = attackingPoke.getVolatileStatuses();
				if (hasConfusionStatus(volatileStatuses)) {
					if (hurtSelfInConfusion()) {
						int confusionDamage = calcConfusionDamage(attackingPoke);
						printHurtSelfInConfusionMessage(confusionDamage);
						attackingPoke = updateHealth(attackingPoke, confusionDamage, 0);
						results.add(attackingPoke);
						results.add(defendingPoke);				
						return results;
					}
				}
			}
			
			if (nonVolatileStatus == NonVolatileStatus.PARALYSIS) { // Paralyzed pokemon have a chance of not moving
				if (wasFullyParalyzed()) {
					printFullyParalyzedMessage(atkPokeName);
					results.add(attackingPoke);
					results.add(defendingPoke);				
					return results;
				}
			}
			
			else if (nonVolatileStatus == NonVolatileStatus.FREEZE) { // Frozen pokemon have a chance of not moving
				if (wasFrozenSolid(atkPokeMove)) {
					printFrozenSolidMessage(atkPokeName);
					results.add(attackingPoke);
					results.add(defendingPoke);				
					return results;
				}
				
				else { // The attacking pokemon thawed out and it can make a move
					attackingPoke = thawPokemon(attackingPoke);
					printAttackerThawedMessage(atkPokeName);
				}
			}
			
			if (didFlinch(volatileStatuses)) { // The attacking pokemon flinched and cannot use its move
				printFlinchedMessage(atkPokeName);
				attackingPoke.resetFlinch();
				results.add(attackingPoke);
				results.add(defendingPoke);				
				return results;
			}
			
			if (hasAttractionStatus(volatileStatuses)) { // The attacking pokemon is attracted to the defending pokemon
				printInLoveMessage(atkPokeName, defPokeName);
				
				if (immobilizedByLove()) { // The attacking pokemon fell in love with the opponent
					printImmobilizedByLoveMessage(atkPokeName);
					results.add(attackingPoke);
					results.add(defendingPoke);				
					return results;
				}
			}
			
			// Reduce the PP of the move used except for "Struggle"
			if (moveName != "Struggle") { 
				attackingPoke = updatePowerPoints(attackingPoke, intMoveOption);
			}			
			
			printUsedMoveMessage(atkPokeName, moveName);
			if (didMoveHit(atkPokeMove.getAccuracy(), attackingPoke, defendingPoke)) {
				defendingPoke.setHitByClassification(moveClassification);
				if (dealsDirectDamage(moveClassification)) {
					// Get the type damage from the type chart
					typeDamage = getTypeResult(moveType, defendingPoke.getPrimaryType(), defendingPoke.getSecondaryType()); // Value based on type chart
					
					 if (basePower > 0) { // i.e., not a One-Hit Knockout (OHKO) move
						 // Determine how much damage was done
						 damage = calcAttackDamage(attackingPoke, atkPokeMove, typeDamage, defendingPoke);
						 defendingPoke.setRecentDamageTaken(damage);
						 
						 // Update the opponent's team
						 defendingPoke = updateHealth(defendingPoke, damage, 0);
						 
						 // Print the appropriate message
						 if (typeDamage > 0) {
							 
							 printAttackMessage(damage);			 
						 }
						 else {
							 printImmunityMessage(atkPokeMove, typeDamage, defPokeName);
							 // Check for sacrificing moves
							 int numEffects = moveEffects.size();
							 String splitBy = " ";
							 for (int i = 0; i < numEffects; i++) {
								String effect = moveEffects.get(i);
								String[] words = effect.split(splitBy);
								String keyword = getKeyword(words);
								
								if (isSacrificingMove(keyword)) { // We let the pokemon sacrifice itself even if the opponent is immune to the attack
									needToReturn = false;
									break;
								}								
							 }
							 
							 if (needToReturn) {
								 results.add(attackingPoke);
								 results.add(defendingPoke);
								 return results;
							 }
						 }
					 }
					 
					 // Deal with attacking moves that have a base power of zero
					 else {
						int numEffects = moveEffects.size();
						String splitBy = " ";
						for (int i = 0; i < numEffects; i++) {
							String effect = moveEffects.get(i);
							String[] words = effect.split(splitBy);
							String keyword = getKeyword(words);
							//System.out.println(keyword);
							if (isOHKOMove(keyword)) { // OHKO moves automatically faint the opponent
								damage = defendingPoke.getHP();
								defendingPoke = updateHealth(defendingPoke, damage, 0);
								printOHKOMessage();
							}
							
							else if (isCounteringMove(keyword)) {
								Classification classification = attackingPoke.getHitByClassification();
								if (words[1].compareTo("Physical") == 0) { // The move counters physical damage taken
									if (classification == Classification.PHYSICAL) {
										damage = calcCounteringDamage(attackingPoke.getRecentDamageTaken());
										defendingPoke = updateHealth(defendingPoke, damage, 0);
										printAttackMessage(damage);
									}
									
									else {
										printMoveFailedMessage();
									}									
								}
								
								else if(words[1].compareTo("Special") == 0) { // The move counters special damage taken
									if (classification == Classification.SPECIAL) { 
										damage = calcCounteringDamage(attackingPoke.getRecentDamageTaken());
										defendingPoke = updateHealth(defendingPoke, damage, 0);
										printAttackMessage(damage);
									}
									
									else {
										printMoveFailedMessage();
									}
								}								
							}
						 }
					 }
				}
				
				// Make sure the defending pokemon has not fainted before
				// applying status moves that target it
				if (!hasFainted(defendingPoke)) {
					int numEffects = moveEffects.size();
					String splitBy = " ";
					
					if (defendingPoke.getNonVolatileStatus() == NonVolatileStatus.FREEZE) {
						if (canThawPokemon(atkPokeMove)) {
							defendingPoke = thawPokemon(defendingPoke);
							printOpponentThawedMessage(defPokeName);
						}
					}
					
					for (int i = 0; i < numEffects; i++) {
						String effect = moveEffects.get(i);
						String[] words = effect.split(splitBy);
						String keyword = getKeyword(words);						
						Target effectTarget = convertStringToTarget(words[words.length - 1]);
						//System.out.println(keyword);
						//System.out.println(effectTarget);
						if (hasNoBattleEffect(effect)) {
							printHasNoBattleEffectMessage();
							attackingPoke = updatePowerPoints(attackingPoke, intMoveOption);
							results.add(attackingPoke);
							results.add(defendingPoke);								
							return results;
						}
						
						if (effectTarget == Target.OPPONENT) { // We are only interested in effects that target the opponent
							if (isStatusMove(keyword)) {
								double chanceToStatus = getEffectChance(words[0]);
								
								if (isNonVolatileStatusMove(keyword)) {									
									if (canApplyNonVolatileStatus(defendingPoke, moveType, keyword)) {
										if (wasSuccess(chanceToStatus)) { // Some moves are not guaranteed to apply a status
											defendingPoke = applyNonVolatileStatus(defendingPoke, keyword, firstToAct);
											printStatusEffectSuccessMessage(defPokeName, keyword);
										}
									}
									
									else {								
										if (defendingPoke.getNonVolatileStatus() == NonVolatileStatus.NONE) {
											if (!dealsDirectDamage(moveClassification)) { // Do not print for damaging moves
												if (this.wasImmune) {												
													printImmunityMessage(atkPokeMove, typeDamage, defPokeName);
												}											
											}
										}
										
										else {
											if (!dealsDirectDamage(moveClassification)) {
												NonVolatileStatus status = convertStringToNonVolatileStatus(keyword);
												printAlreadyHasNonVolatileStatusMessage(status, defendingPoke);
											}
										}
									}
								}								
								
								else if (canFlinchOpponent(keyword)) {									
									if (atkOwner.compareTo(firstToAct) == 0) {
										if (wasSuccess(chanceToStatus)) {
											defendingPoke.setVolatileStatus(VolatileStatus.FLINCH);
										}
									}
								}
								
								else if (isVolatileStatusMove(keyword)) {
									if (canApplyVolatileStatus(attackingPoke, keyword, defendingPoke)) {
										if (wasSuccess(chanceToStatus)) {
											defendingPoke = applyVolatileStatus(defendingPoke, keyword, firstToAct);
											printStatusEffectSuccessMessage(defPokeName, keyword);
										}
									}
								}																
							}
							
							else if (isSwappingMove(keyword)) {	
								char swapOption;
								if (defOwner.compareTo("1") == 0) {
									if (hasValidSwap(defendingPoke, team1)) {
										swapOption = getRandomSwapOption(defendingPoke, team1);
										results = handleRandomSwap(attackingPoke, defendingPoke, defOwner, swapOption);
										return results;
									}
									
									else {
										if (!dealsDirectDamage(moveClassification)) {
											printMoveFailedMessage();
										}										
									}
								}
								
								else if (defOwner.compareTo("2") == 0) {
									if (hasValidSwap(defendingPoke, team2)) {
										swapOption = getRandomSwapOption(defendingPoke, team2);
										results = handleRandomSwap(attackingPoke, defendingPoke, defOwner, swapOption);
										return results;
									}
									
									else {
										if (!dealsDirectDamage(moveClassification)) {
											printMoveFailedMessage();
										}	
									}
								}
							}
							
							else if (isStatChangingMove(keyword)) {
								double chanceToChangeStats = getEffectChance(words[0]);
								
								if (wasSuccess(chanceToChangeStats)) {
									results = changeStats(attackingPoke, moveName, effectTarget, words, defendingPoke);									
									return results;
								}
							}
						}						
					}
				}
				// Check for moves that affect the user
				int numEffects = moveEffects.size();
				String splitBy = " ";
				for (int i = 0; i < numEffects; i++) {
					String effect = moveEffects.get(i);
					String[] words = effect.split(splitBy);
					String keyword = getKeyword(words);						
					Target effectTarget = convertStringToTarget(words[words.length - 1]);
				
					if (effectTarget == Target.SELF) { // We are only interested in effects that effect the user
						if (isCopyingMove(keyword)) {
							attackingPoke = handleCopying(attackingPoke, words[1], defendingPoke);								
						}
						
						else if (isSacrificingMove(keyword)) {
							attackingPoke = updateHealth(attackingPoke, attackingPoke.getHP(), 0); // Remove the remainder of the pokemon's health
						}
						
						else if (hasRecoil(keyword)) {
							int recoilDamage = calcAmount(effect, damage, attackingPoke.getMaxHP());
							attackingPoke = updateHealth(attackingPoke, recoilDamage, 0);
							printRecoilMessage(recoilDamage);
						}
						
						else if (isHealingMove(keyword)) {
							int hp = attackingPoke.getHP();
							int maxHP = attackingPoke.getMaxHP();
							int amountToHeal = calcAmount(effect, damage, maxHP);
							
							if (canHeal(hp, maxHP)) {
								attackingPoke = updateHealth(attackingPoke, 0, amountToHeal);
								printHealingMessage(amountToHeal);
							}
							
							else {
								printHealingFailedMessage(atkPokeName);
							}
						}
						
						else if (isSwappingMove(keyword)) {
							String stringSwapOption;
							char swapOption;
							if (atkOwner.compareTo("1") == 0) {
								if (hasValidSwap(defendingPoke, team1)) {
									stringSwapOption = swapMenu(this.pOneName, attackingPoke, this.team1, true);
									swapOption = stringSwapOption.charAt(0);
									results = handleNormalSwap(attackingPoke, defendingPoke, atkPokeName, atkOwner, swapOption);
									return results;
								}									
							}
							
							else if (defOwner.compareTo("2") == 0) {
								if (hasValidSwap(defendingPoke, team2)) {
									stringSwapOption = swapMenu(this.pTwoName, attackingPoke, this.team2, true);
									swapOption = stringSwapOption.charAt(0);
									results = handleNormalSwap(attackingPoke, defendingPoke, atkPokeName, atkOwner, swapOption);
									return results;
								}									
							}
						}
						
						else if (isStatChangingMove(keyword)) {
							double chanceToChangeStats = getEffectChance(words[0]);
							
							if (wasSuccess(chanceToChangeStats)) {
								results = changeStats(attackingPoke, moveName, effectTarget, words, defendingPoke);
								return results;
							}
						}
					}
				}
			}						
			
			else {
				printMissedMessage();
			}
				
			results.add(attackingPoke);
			results.add(defendingPoke);				
			return results;
		}
		
		// The player wants to swap
		else if (menuOption == '2') {			
			char charSwapOption = playerOption.charAt(1);			
			results = handleNormalSwap(attackingPoke, defendingPoke, atkPokeName, atkOwner, charSwapOption);
			return results;
		}
		return null; // Should never get here
				
	} // turn(Pokemon attackingPoke, Pokemon defendingPoke, String playerOption, int atkPokeIndex, int defPokeIndex, String firstToAct)

	/*
	  	Parameters:
        --self: A reference to the instance whose method was called        
        --attackingPoke: The pokemon that is swapping
        --defendingPoke: The opponent's pokemon
        --atkPokeName: The attacking pokemon's name
        --atkOwner: The attacking pokemon's owner
        --swapOption: The option from the swap menu

        Desc/Purpose:
        --To handle a "normal" swap i.e., a swap that is the result of
          a move that forces a pokemon into a different pokemon
        
        Returns:
        --The attackingPoke and the defendingPoke in an ArrayList
	*/
	public ArrayList<Pokemon> handleNormalSwap(Pokemon attackingPoke, Pokemon defendingPoke, String atkPokeName, String atkOwner, char swapOption) {
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		if (atkOwner.compareTo("1") == 0) {
			String oldName = atkPokeName;
			
			this.team1 = updateTeam(attackingPoke, this.team1);
			attackingPoke = resetPokemon(attackingPoke);
			attackingPoke = swapPokemon(attackingPoke, this.team1, swapOption);
			defendingPoke.resetAttraction(); // We need to remove the "Attraction" volatile status from the
            								 // defending pokemon when the attacking pokemon swaps out
		
			String newName = attackingPoke.getPokeName();
			printSwapMessage(oldName, newName);
		}
		
		else if (atkOwner.compareTo("2") == 0) {
			String oldName = atkPokeName;
			
			this.team2 = updateTeam(attackingPoke, this.team2);
			attackingPoke = resetPokemon(attackingPoke);
			attackingPoke = swapPokemon(attackingPoke, this.team2, swapOption);
			defendingPoke.resetAttraction(); // We need to remove the "Attraction" volatile status from the
            								 // defending pokemon when the attacking pokemon swaps out
		
			String newName = attackingPoke.getPokeName();
			printSwapMessage(oldName, newName);
		}
		results.add(attackingPoke);
		results.add(defendingPoke);
		return results;
	} // handleNormalSwap(Pokemon attackingPoke, Pokemon defendingPoke, String atkPokeName, String atkOwner, char swapOption)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to be swapped
        --team: The team the pokemon belongs to

        Desc/Purpose:
        --To get a random swap option when the pokemon is forced out
        
        Returns:
        --The swap option when the pokemon is forced out
	*/
	public char getRandomSwapOption(Pokemon pokemon, ArrayList<Pokemon> team) {
		int teamSize = team.size();
		String myPokeID = pokemon.getPokeID();
		ArrayList<Character> pokeIndices = new ArrayList<Character>();
		String pokeID = "";
		Pokemon p;
		char index;
		
		for (int i = 0; i < teamSize; i++) {
			p = team.get(i);
			pokeID = p.getPokeID();
			index = pokeID.charAt(1);
			
			if (pokeID.compareTo(myPokeID) != 0) {
				if (!hasFainted(p)) {
					pokeIndices.add(index);
				}
			}
		}
		Random rand = new Random();
		int randIndex = rand.nextInt(pokeIndices.size());
		
		pokeIndices.get(randIndex);
		int intSwapOption = Character.getNumericValue(pokeIndices.get(randIndex)) + 1;
		String stringOption = String.valueOf(intSwapOption);
		char charSwapOption = stringOption.charAt(0);
		
		return charSwapOption;
		
	} // getRandomSwapOption(Pokemon pokemon, ArrayList<Pokemon> team)
	
	ArrayList<Pokemon> handleRandomSwap(Pokemon attackingPoke, Pokemon defendingPoke, String defOwner, char swapOption){
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();		
		if (defOwner.compareTo("1") == 0) {
			defendingPoke = resetPokemon(defendingPoke);
			this.team1 = updateTeam(defendingPoke, this.team1);
			defendingPoke = swapPokemon(defendingPoke, team1, swapOption);
			
			attackingPoke.resetAttraction(); // We need to remove the "Attraction" volatile status
			                                 // from the attacking pokemon when the defending pokemon
			                                 // swaps out.
			
			String newPokeName = defendingPoke.getPokeName();
			printForcedOutMessage(newPokeName);
			this.pOneForcedOut = true;
		}
		
		if (defOwner.compareTo("2") == 0) {
			defendingPoke = resetPokemon(defendingPoke);
			this.team2 = updateTeam(defendingPoke, this.team2);
			defendingPoke = swapPokemon(defendingPoke, team2, swapOption);
			
			attackingPoke.resetAttraction(); // We need to remove the "Attraction" volatile status
			                                 // from the attacking pokemon when the defending pokemon
			                                 // swaps out.
			
			String newPokeName = defendingPoke.getPokeName();
			printForcedOutMessage(newPokeName);
			this.pTwoForcedOut = true;
		}
		
		results.add(attackingPoke);
		results.add(defendingPoke);
		
		return results;
	} // handleRandomSwap(Pokemon attackingPoke, Pokemon defendingPoke, String defOwner, char swapOption)
	
	/*
	 	Parameters:        
        --currentPoke: The pokemon that is currently out
        --team: The player's team
        --option: The player's swap option

        Desc/Purpose:
        --To swap the current pokemon with a new one
        
        Returns:
        --The pokemon to be swapped in
	*/
	public Pokemon swapPokemon(Pokemon currentPoke, ArrayList<Pokemon> team, char option) {
		int index = Character.getNumericValue(option) - 1;
		return team.get(index);
	} // swapPokemon(Pokemon currentPoke, ArrayList<Pokemon> team, char option)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon that needs to be reset on a swap

        Desc/Purpose:
        --To reset the following:
          --The number of turns a pokemon has been out when it has been
            badly poisoned
          --Volatile Statuses
          --Stats and stages
        --To restore the following:
          --Moveset
          --Types
          --Name
          --Pokedex #
          --Gender
        
        Returns:
        --The pokemon that has been reset
	*/
	public Pokemon resetPokemon(Pokemon pokemon) {
		pokemon.setBadlyPoisonedTurns(0);
        pokemon.resetVolatileStatuses();
        pokemon.resetStats();
        pokemon.resetStages();
        pokemon.restoreMoveset();
        pokemon.restoreGeneralInfo();
        return pokemon;
	} // resetPokemon(Pokemon pokemon)
	
	/*
    	Parameters:    
    	--attackingPoke: The pokemon that is using an attack move (i.e., a move with a
      	  base power > 0)
    	--move: The move used by the attacking pokemon
    	--typeDamage: The damage multiplier to apply to the damage calc based
      	  on the move's type and the defending pokemon's type(s)
    	--defendingPoke: The opposing player's pokemon        

    	Desc/Purpose:
    	--Calculates the damage that the attacking pokemon does to the defendingPoke
    
    	Returns:
    	--The damage dealt to the defendingPoke

    	Note:
    	--Damage formula comes from 'http://bulbapedia.bulbagarden.net/wiki/Damage'
    */
	public int calcAttackDamage(Pokemon attackingPoke, Move move, double typeDamage, Pokemon defendingPoke) {
		double totalDamage = 0;
		double modifier = 1.0; // Modifies the damage dealt based on the effectiveness of a move and
		                  //   whether or not we get a Same Type Attack Bonus (STAB)
		double stabBonus = 1.5;
		double critBonus = 1.5;
		double burnDmgReduc = 0.5;
		double powerDamage = 1.0;
		
		Type moveType = move.getMoveType();		
		Classification moveClassification = move.getClassification();
		int moveBasePower = move.getBasePower();
		
		// The attack and special attack will be based on the attacking pokemon's attack 
		//    and special attack stats, respectively, and it's attack and special attack 
		//    stages, respectively
		int attackingAtk = attackingPoke.getAtk();
		int attackingSpAtk = attackingPoke.getSpAtk();
		int attackingAtkStage = attackingPoke.getAtkStage();
		int attackingSpAtkStage = attackingPoke.getSpAtkStage();		
		
		double attackingFinalAtk = attackingAtk * this.multipliers[attackingAtkStage + 6];
		double attackingFinalSpAtk = attackingSpAtk * this.multipliers[attackingSpAtkStage + 6];
		                   
		// The defense and special defense will be based on the defending pokemon's defense
		//     and special defense stats, respectively, and it's defense and special defense
		//     stages, respectively
		int defendingDef = attackingPoke.getDef();
		int defendingSpDef = attackingPoke.getSpDef();
		int defendingDefStage = defendingPoke.getDefStage();
		int defendingSpDefStage = defendingPoke.getSpDefStage();
		
		double defendingFinalDef = defendingDef * this.multipliers[defendingDefStage + 6];				
		double defendingFinalSpDef = defendingSpDef * this.multipliers[defendingSpDefStage + 6];
		
		// Calculate the values needed to determine the damage dealt
		double levelDamage = ((2 * attackingPoke.getLevel()) / 5) + 2;
		
		modifier *= typeDamage;		
		
		if (wasSuccess(move.getCritChance())) { // Did we get a critical hit
			if (defendingDefStage > this.BASE_STAGE) { // We ignore any defense buffs
				defendingFinalDef = defendingDef;
			}
			
			if (defendingSpDefStage > this.BASE_STAGE) { // We ignore any special defense buffs
				defendingFinalSpDef = defendingSpDef;
			}
			
			if (attackingAtkStage < this.BASE_STAGE) { // We ignore any attack debuffs
				attackingFinalAtk = attackingAtk;
			}
			
			if (attackingSpAtkStage < this.BASE_STAGE) { // We ignore any special attack debuffs
				attackingFinalSpAtk = attackingSpAtk;
			}
			
			this.wasCrit = true;
			modifier *= critBonus;
		}
		
		else {
			this.wasCrit = false;
		}
		
		// Check if we have a STAB
		if (isSTAB(moveType, attackingPoke.getPrimaryType(), defendingPoke.getSecondaryType())) {
			modifier *= stabBonus;
		}
			
		// If the move is physical, use the attacking pokemon's Atk and the defending
		// pokemon's Def
		if (moveClassification == Classification.PHYSICAL) {			
			powerDamage = moveBasePower * attackingFinalAtk / defendingFinalDef;
			
			// Burned pokemon deal reduced damage when using physical moves
			if (attackingPoke.getNonVolatileStatus() == NonVolatileStatus.BURN) {
				modifier *= burnDmgReduc;
			}
		}
		
		else if (moveClassification == Classification.SPECIAL) {
			powerDamage = moveBasePower * attackingFinalSpAtk / defendingFinalSpDef;
		}
		
		double damageRoll = getDamageRoll();
		modifier *= damageRoll;		
		totalDamage = (((levelDamage * powerDamage) / 50) + 2) * modifier;
		
		return (int) Math.ceil(totalDamage);
	} // (Pokemon attackingPoke, Move move, double typeDamage, Pokemon defendingPoke)
	
	/*
    	Parameters:
    	--N/A     

    	Desc/Purpose:
    	--Get the damage roll to apply to the total damage dealt
      	  by an attack
    
    	Returns:
    	--The damage roll to apply to the total damage dealt
      	  by an attack which is a value between minRoll and
      	  maxRoll
    */
	public double getDamageRoll() {
		double minRoll = 0.85;
		double maxRoll = 1.0;
		double randNum = Math.random() * ((maxRoll - minRoll) + 1) + minRoll;
		
		if (randNum > 1.0) { // Make sure to cap the max roll
			randNum = 1.0;
		}
		
		return randNum; 
	} // getDamageRoll()
	
	/*
    	Parameters:
       	--successChance: The chance for something to succeed    

    	Desc/Purpose:
    	--To check if the event was a success
    
    	Returns:
    	--True if we succeeded
    	--False otherwise
	*/
	public boolean wasSuccess(double successChance) {		
		int maxRange = 255;	
		int randNum = new Random().nextInt(maxRange + 1); // [0...maxRange + 1] [min = 0, max = maxRange + 1]
		int endOfRange = (int) (successChance * .01 * maxRange);
		
		if (randNum <= endOfRange) {
			return true;
		}
		return false;
	} // wasSuccess(double successChance)
	
	/*
	 	Parameters:
	 	--damage: The damage recently taken
	 	
	 	Desc/Purpose:
	 	--To calculate the amount of damage
	 	  to deal when a pokemon uses a countering
	 	  move
	 	  
	 	Returns:
	 	--The amount of damage to deal
	*/
	public int calcCounteringDamage(int damage) {
		return 2 * damage;
	} // calcCounteringDamage(int damage)
	
	/*
    	Parameters:    	
    	--attackingPoke: The pokemon that is using the status move
    	--keyword: The keyword in an effect's description    	                

    	Desc/Purpose:
    	--To see if it is possible to apply the nonvolatile status
      	  to the pokemon
    
    	Returns:
    	--True if the nonvolatile status can be applied
    	--False, otherwise
    */
	public boolean canApplyNonVolatileStatus(Pokemon pokemon, Type moveType, String keyword) {
		NonVolatileStatus status = convertStringToNonVolatileStatus(keyword);
		
		// Check if the pokemon has a NonVolatile status
		if (pokemon.getNonVolatileStatus() == NonVolatileStatus.NONE) {
			if (isImmuneToStatus(pokemon, moveType, status)) { // Some pokemon are immune to certain statuses
				this.wasImmune = true;
				return false;
			}
			this.wasImmune = false;
			return true;
		}
		
		else {
			if (isImmuneToStatus(pokemon, moveType, status)) {
				this.wasImmune = true;
			}
			
			else {
				this.wasImmune = false;
			}
			return false;
		}
		
	} // canApplyNonVolatileStatus(Pokemon pokemon, Type moveType, String keyword)
	
	/*
    	Parameters:    	
    	--attackingPoke: The pokemon that is using the status move
    	--keyword: The keyword in an effect's description
    	--defendingPoke: The pokemon that is being statused                  

    	Desc/Purpose:
    	--To see if it is possible to apply the volatile status
      	  to the pokemon
    
    	Returns:
    	--True if the volatile status can be applied
    	--False, otherwise
    */
	public boolean canApplyVolatileStatus(Pokemon attackingPoke, String keyword, Pokemon defendingPoke) {
		VolatileStatus status = convertStringToVolatileStatus(keyword);
		
		if (status == VolatileStatus.CONFUSION) {
			if (hasConfusionStatus(defendingPoke.getVolatileStatuses())) {
				return false;
			}
			return true;
		}
		
		else if (status == VolatileStatus.ATTRACTION) {
			if (canBeAttracted(attackingPoke, defendingPoke)) {
				return true;
			}
			return false;
		}
		return true;
	} // canApplyVolatileStatus(Pokemon attackingPoke, String keyword, Pokemon defendingPoke)
	
	/*
    	Parameters:
        --pokemon: The pokemon that is being statused
    	--moveType: The move's type (e.g., Fire, Water, etc.)
    	--status: The nonvolatile status to apply to the pokemon           

    	Desc/Purpose:
    	--To see if it the pokemon is immune to the move and/or status
    
    	Returns:
    	--True if the pokemon is immune to the move and/or status
    	--False, otherwise
	*/
	public boolean isImmuneToStatus(Pokemon pokemon, Type moveType, NonVolatileStatus status) {
		Type primaryType = pokemon.getPrimaryType();
		Type secondaryType = pokemon.getSecondaryType();
		
		// Check to see if we can apply the nonvolatile status
		if (status == NonVolatileStatus.POISON || status == NonVolatileStatus.BADLY_POISON) {
			if (canBePoisoned(moveType, primaryType, secondaryType)) {
				return false;
			}
			return true;
		}
		
		else if (status == NonVolatileStatus.BURN) {
			if (canBeBurned(moveType, primaryType, secondaryType)) {
				return false;
			}
			return true;
		}
		
		else if (status == NonVolatileStatus.PARALYSIS) {
			if (canBeParalyzed(moveType, primaryType, secondaryType)) {
				return false;
			}
			return true;
		}
		
		else if (status == NonVolatileStatus.SLEEP) {
			if (canBeAsleep(moveType, primaryType, secondaryType)) {
				return false;
			}
			return true;
		}
		
		else if (status == NonVolatileStatus.FREEZE) {
			if (canBeFrozen(moveType, primaryType, secondaryType)) {
				return false;
			}
			return true;
		}	
		
		else {
			System.out.println("In isImmuneToStatus: " + status + " is not a valid nonvolatile status effect.");
		}
		return false;
	} // isImmuneToStatus(Pokemon pokemon, Type moveType, NonVolatileStatus status)
	
	/*
    	Parameters:
        --moveType: The move's typing (e.g. FIRE, WATER, etc.)
    	--primaryType: A pokemon's primary type
    	--secondaryType: A pokemon's secondary type

    	Desc/Purpose:
    	--Check to see if a pokemon can be poisoned or badly poisoned
       	  based on the pokemon's primary and/or secondary types
    
    	Returns:
    	--True if the pokemon can be poisoned or badly poisoned
    	--False otherwise
	*/
	boolean canBePoisoned(Type moveType, Type primaryType, Type secondaryType) {
		if (moveType == Type.POISON) {
			if (primaryType == Type.POISON || secondaryType == Type.POISON) {
				return false; // Poison type pokemon cannot be poisoned or badly poisoned
			}
		}
		
		if (primaryType == Type.POISON || secondaryType == Type.POISON) {
			return false; // Poison type pokemon cannot be poisoned or badly poisoned
		}
		
		else if (primaryType == Type.STEEL|| secondaryType == Type.STEEL) {
			return false; // Steel type pokemon cannot be poisoned or badly poisoned
		}
		return true;
	} // canBePoisoned(Type moveType, Type primaryType, Type secondaryType)
	
	/*
		Parameters:
    	--moveType: The move's typing (e.g. FIRE, WATER, etc.)
		--primaryType: A pokemon's primary type
		--secondaryType: A pokemon's secondary type

		Desc/Purpose:
		--Check to see if a pokemon can be burned
   	  	based on the pokemon's primary and/or secondary types

		Returns:
		--True if the pokemon can be burned
		--False otherwise
	 */
	boolean canBeBurned(Type moveType, Type primaryType, Type secondaryType) {
		if (primaryType == Type.FIRE || secondaryType == Type.FIRE) {
			return false; // Fire type pokemon cannot be burned
		}
		
		return true;
	} // canBeBurned(Type moveType, Type primaryType, Type secondaryType)
	
	
	/*
		Parameters:
    	--moveType: The move's typing (e.g. FIRE, WATER, etc.)
		--primaryType: A pokemon's primary type
		--secondaryType: A pokemon's secondary type

		Desc/Purpose:
		--Check to see if a pokemon can be paralyzed based on 
		  the pokemon's primary and/or secondary types

		Returns:
		--True if the pokemon can be paralyzed
		--False otherwise
	 */
	boolean canBeParalyzed(Type moveType, Type primaryType, Type secondaryType) {
		if (moveType == Type.GRASS) {
			if (primaryType == Type.GRASS || secondaryType == Type.GRASS) {
				return false; // Grass type pokemon cannot be paralyzed by grass type moves
			}
		}
		
		else if (moveType == Type.ELECTRIC) {
			if (primaryType == Type.ELECTRIC || secondaryType == Type.ELECTRIC) {
				return false; // Electric type pokemon cannot be paralyzed by electric type moves
			}
			
			else if (primaryType == Type.GROUND|| secondaryType == Type.GROUND) {
				return false; // Ground type pokemon cannot be paralyzed by electric type moves
			}
			return true;
		}		
		return true;
	} // canBeParalyzed(Type moveType, Type primaryType, Type secondaryType)
	
	/*
		Parameters:
		--moveType: The move's typing (e.g. FIRE, WATER, etc.)
		--primaryType: A pokemon's primary type
		--secondaryType: A pokemon's secondary type

		Desc/Purpose:
		--Check to see if a pokemon can be frozen based on 
		  the pokemon's primary and/or secondary types

		Returns:
		--True if the pokemon can be burned
		--False otherwise
	*/
	boolean canBeFrozen(Type moveType, Type primaryType, Type secondaryType) {
		if (primaryType == Type.ICE || secondaryType == Type.ICE) {
			return false; // Ice type pokemon cannot be frozen
		}	
		return true;
	} // canBeFrozen(Type moveType, Type primaryType, Type secondaryType)
	
	/*
		Parameters:
		--moveType: The move's typing (e.g. FIRE, WATER, etc.)
		--primaryType: A pokemon's primary type
		--secondaryType: A pokemon's secondary type

		Desc/Purpose:
		--Check to see if a pokemon can be paralyzed based on 
	      the pokemon's primary and/or secondary types

		Returns:
		--True if the pokemon can be paralyzed
		--False otherwise
	 */
	boolean canBeAsleep(Type moveType, Type primaryType, Type secondaryType) {
		if (moveType == Type.GRASS) {
			if (primaryType == Type.GRASS || secondaryType == Type.GRASS) {
				return false; // Grass type pokemon cannot be put to sleep by grass type moves
			}
			return true; // Any other pokemon can be put to sleep by Grass type moves
		}	
		return true; // The move is not Grass type
	} // canBeAsleep(Type moveType, Type primaryType, Type secondaryType)
	
	/*
	 	Parameters:                
        --attackingPoke: The pokemon that is using the status move 
        --defendingPoke: The pokemon that is being statused

        Desc/Purpose:
        --Check to see if the defending pokemon can be attracted to
          the attacking pokemon
        
        Returns:
        --True if the defending pokemon can be attracted
        --False otherwise
	*/
	boolean canBeAttracted(Pokemon attackingPoke, Pokemon defendingPoke) {
		if (hasAttractionStatus(defendingPoke.getVolatileStatuses())) {
			return false;
		}
		
		else if (isGenderMismatch(attackingPoke.getGender(), defendingPoke.getGender())) {
			this.wasImmune = true;
			return false;
		}
		return true;
	} // canBeAttracted(Pokemon attackingPoke, Pokemon defendingPoke)	
	
	/*
    	Parameters:
        --pokemon: The pokemon that is being statused
    	--keyword: The keyword in an effect's description
    	--firstToAct: The pokemon that will act first

    	Desc/Purpose:
    	--To apply a volatile status to a pokemon
    
    	Returns:
    	--The pokemon with an updated volatile status
	*/
	Pokemon applyNonVolatileStatus(Pokemon pokemon, String keyword, String firstToAct) {
		NonVolatileStatus status = convertStringToNonVolatileStatus(keyword);
		
		if (status == NonVolatileStatus.SLEEP) {
			pokemon.setNonVolatileStatus(NonVolatileStatus.SLEEP);
			pokemon = setOpponentSleepTurns(pokemon, firstToAct);
		}
		
		else if (status == NonVolatileStatus.POISON || status == NonVolatileStatus.BADLY_POISON ||
				status == NonVolatileStatus.BURN || status == NonVolatileStatus.PARALYSIS ||
				status == NonVolatileStatus.FREEZE) {
			pokemon.setNonVolatileStatus(status);
		}
		
		else {
			System.out.println("In applyNonVolatileStatus: " + status + " is not a valid nonvolatile status effect.");
		}
		
		return pokemon;
	} // applyNonVolatileStatus(Pokemon pokemon, String keyword, String firstToAct)
	
	/*
	  	Parameters:        
        --pokemon: The pokemon that is being statused
        --keyword: The keyword in an effect's description
        --firstToAct: The pokemon that will act first

        Desc/Purpose:
        --To apply a volatile status to a pokemon
        
        Returns:
        --The pokemon with an updated volatile status
	*/
	public Pokemon applyVolatileStatus(Pokemon pokemon, String keyword, String firstToAct) {
		VolatileStatus status = convertStringToVolatileStatus(keyword);
				
		if (status == VolatileStatus.CONFUSION) {
			pokemon.setVolatileStatus(status);
			pokemon = setOpponentConfusionTurns(pokemon, firstToAct);
			
		}
		
		else if (status == VolatileStatus.ATTRACTION) {
			pokemon.setVolatileStatus(status);
		}
		
		else {
			System.out.println("In applyNonVolatileStatus: " + status + " is not a valid volatile status effect.");
		}			
		
		return pokemon;
	} // applyVolatileStatus(Pokemon pokemon, String keyword, String firstToAct)	
	
	/*
    	Parameters:
       	--pokemon: The pokemon that may need to take damage from a
          non-volatile status effect

    	Desc/Purpose:
    	--To calculate how much health a poisoned, badly poisoned, or
      	  burned pokemon will lose after both player's have
      	  completed their actions
    
    	Returns:
    	--The amount of damage taken after both player's have
          completed their actions
	*/
	public int calcNonVolatileStatusDamage(Pokemon pokemon) {
		NonVolatileStatus status = pokemon.getNonVolatileStatus();
		int badlyPoisonTurns = pokemon.getBadlyPoisonedTurns();
		
		if (status == NonVolatileStatus.POISON) {
			return (int)(0.125 * pokemon.getMaxHP()); // Poison damage is equal to 1/8 (12.5%) of the pokemon's max HP
		}
		
		else if (status == NonVolatileStatus.BURN) {
			return (int)(0.0625 * pokemon.getMaxHP()); // Burn damage is equal to 1/16 (6.25%) of the pokemon's max HP
		}
		
		else if (status == NonVolatileStatus.BADLY_POISON) {
			// Badly poison damage is equal to 1/16 * the number of turns out * the pokemon's max HP
			return (int)(0.0625 * badlyPoisonTurns * pokemon.getMaxHP());
		}
		
		return 0; // We do not need to deal damage		
	} // calcNonVolatileStatusDamage(Pokemon pokemon)
	
	/*
	  	Parameters:        
        --pokemon: The pokemon that hurt itself in confusion

        Desc/Purpose:
        --To calculate how much health a confused pokemon will lose
          when it hurts itself
        
        Returns:
        --The amount of damage a confused pokemon does to itself

        Note: Damage formula comes from
        'https://www.math.miami.edu/~jam/azure/attacks/comp/confuse.htm'
	*/
	public int calcConfusionDamage(Pokemon pokemon) {
		double levelDamage = ((2 * pokemon.getLevel()) / 5) + 2;
		double basePower = 40;
		double powerDamage = basePower * pokemon.getAtk() / pokemon.getDef();
		int totalDamage = (int)((levelDamage * powerDamage / 50 + 2));
		return totalDamage;
	} // calcConfusionDamage(Pokemon pokemon)
	
	/*
		Parameters:        
        --effect: The effect of the attacking pokemon's move
        --damage: The damage dealt by the move
        --maxHP: The max HP of the attacking pokemon

        Desc/Purpose:
        --An amount that has a different meaning depending on
          the context
          --Ex: If a move has recoil, the amount will indicate
            how much recoil the attacking pokemon will take
        
        Returns:
        --The amount to heal or the amount of recoil
	*/
	public int calcAmount(String effect, int damage, int maxHP) {
		String splitBy = " ";		
		String[] words = effect.split(splitBy);
		double percentage = Double.valueOf(words[1].substring(0, words[1].length()-1)); // Get the percentage while ignoring the '%'
		
		if (words[2].compareTo("Health") == 0) {
			return (int) (Math.ceil(maxHP * percentage * .01));
		}
		
		else if (words[2].compareTo("Damage") == 0) {
			return (int) (Math.ceil(damage * percentage * .01));
		}
		
		System.out.println("In calcAmount: " + words[2] + " is an invalid keyword.");
		return 0;
	} // calcAmount(String effect, int damage, int maxHP)
	
	/*
    	Parameters:
    	--N/A

    	Desc/Purpose:
    	--To determine if the pokemon was fully paralyzed
    
    	Returns:
    	--True if the pokemon was unable to use its move
    	--False otherwise
    */
	public boolean wasFullyParalyzed() {
		if(wasSuccess(75)) { // Paralyzed pokemon have a 75% chance of using a move
			return false; // The pokemon was able to use a move
		}
		return true;
	} // wasFullyParalyzed()	
	
	/*
    	Parameters:
        --move: The move used by the frozen pokemon        

    	Desc/Purpose:
    	--To determine if the pokemon was frozen solid
    
    	Returns:
    	--True if the pokemon was unable to use its move
    	--False otherwise
    */
	public boolean wasFrozenSolid(Move move) {
		if (canThawPokemon(move)) {
			return false;
		}
		
		if (wasSuccess(20)) { // Frozen pokemon have a 20% chance of thawing out and using a move
			return false; // The pokemon was able to use its move
		}
		
		return true;
	} // wasFrozenSolid(Move move)
	
	/*
    	Parameters:
        --move: The move used by the pokemon

    	Desc/Purpose:
    	--To determine if the move can thaw a pokemon
    
    	Returns:
    	--True if the move is an attacking move that can thaw a pokemon
    	--False otherwise
    */
	public boolean canThawPokemon(Move move) {
		List<String> effects = move.getEffects();
		int numEffects = effects.size();
		Classification moveClassification = move.getClassification();
		ArrayList<String> movesThatCanThaw = new ArrayList<String>();
		movesThatCanThaw.add("Scald");
		movesThatCanThaw.add("Steam Eruption");
		
		String[] effect;
		String splitBy = " ";		
		String keyword = "";
		
		if (dealsDirectDamage(moveClassification)) {
			for (int i = 0; i < numEffects; i++) {
				effect = effects.get(i).split(splitBy);
				keyword = getKeyword(effect);
				//System.out.println(keyword);
				if (keyword.compareTo("Burn") == 0) { // Fire type moves and any move in "movesThatCanBurn" can thaw pokemon
					if (move.getMoveType() == Type.FIRE) {
						return true;
					}
					
					else if (movesThatCanThaw.contains(move.getMoveName())) {
						return true;
					}
					
					else {
						return false; // Ex: Tri Attack can burn the opponent but cannot thaw it
					}					
				}
			}
			return false;
		}
		return false;		
	} // canThawPokemon(Move move)
	
	/*
    	Parameters:
        --pokemon: The pokemon that needs to be thawed

    	Desc/Purpose:
    	--To thaw out the pokemon 
    
    	Returns:
    	--The pokemon with its NonVolatileStatus set to NONE
    */
	public Pokemon thawPokemon(Pokemon pokemon) {
		pokemon.setNonVolatileStatus(NonVolatileStatus.NONE);
		return pokemon;
	} // thawPokemon(Pokemon pokemon)
	
	/*
    	Parameters:
        --pokemon: A pokemon with the Sleep status

    	Desc/Purpose:
    	--Handles the following situations:
      	  --The pokemon is still asleep
      	  --The pokemon woke up
    
    	Returns:
    	--The pokemon with updated sleep turns and/or non-volatile status
    */
	public Pokemon handleSleepStatus(Pokemon pokemon) {
		int sleepTurns = pokemon.getSleepTurns();
		if (sleepTurns > 0) {
			printStillSleepingMessage(pokemon.getPokeName());
			pokemon.setSleepTurns(sleepTurns - 1);
		}
		
		else {
			printWokeUpMessage(pokemon.getPokeName());
			pokemon.setNonVolatileStatus(NonVolatileStatus.NONE);
		}
		return pokemon;
	} // handleSleepStatus(Pokemon pokemon)
	
	/*
    	Parameters:
    	--pokemon: A pokemon that was put to sleep
    	--firstToAct: Which pokemon will use its action first

    	Desc/Purpose:
    	--To set the number of turns that a pokemon that was recently
      	  afflicted with the Sleep status will be asleep
    
    	Returns:
    	--The pokemon with its sleep turns updated
    */
	Pokemon setOpponentSleepTurns(Pokemon pokemon, String firstToAct) {
		Random rand = new Random();
		if (firstToAct.compareTo("1") == 0) {
			//pokemon.setSleepTurns(ThreadLocalRandom.current().nextInt(2, 4+1));
			int randNum = rand.nextInt((4 - 2) + 1 )+ 2; // Use 2 to 4 so that we don't have 
			                                             // a first wake
			if (randNum > 4) { // Ensure that we don't exceed 4 turns
				randNum = 4;
			}
			pokemon.setSleepTurns(randNum); 
		}
		
		else if (firstToAct.compareTo("2") == 0) {
			//pokemon.setSleepTurns(ThreadLocalRandom.current().nextInt(1, 3+1));
			
			int randNum = rand.nextInt((3 - 1)+ 1)+ 1;
			
			if (randNum > 3) { // Ensure that we don't exceed 3 turns
				randNum = 3;
			}
			pokemon.setSleepTurns(randNum); // Sleep lasts for 1 to 3 turns
		}
		
		else {
			System.out.println("In setOpponentSleepTurns: " + firstToAct + " is invalid!");
		}
		
		return pokemon;
	} // setOpponentSleepTurns(Pokemon pokemon, String firstToAct)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon that is confused

        Desc/Purpose:
        --Handles the following situations:
          --The pokemon is still confused
          --The pokemon shook of its confusion
        
        Returns:
        --The pokemon with updated confusion turns and/or volatile status
	*/
	public Pokemon handleConfusionStatus(Pokemon pokemon) {
		int confusionTurns = pokemon.getConfusionTurns();
		if (confusionTurns > 0) {
			printIsConfusedMessage(pokemon.getPokeName());
			pokemon.setConfusionTurns(confusionTurns - 1);
		}
		
		else {
			printShookOffConfusionMessage(pokemon.getPokeName());
			pokemon.resetConfusion();
		}
		return pokemon;
	} // handleConfusionStatus(Pokemon pokemon)
	
	/*
	 	Parameters:        
        --statuses: The volatile status conditions of a pokemon

        Desc/Purpose:
        --To determine if the pokemon is still confused after reducing
          its confusion turns
        
        Returns:
        --True if the pokemon was unable to use its move
        --False otherwise
	*/
	public boolean hasConfusionStatus(ArrayList<VolatileStatus> statuses) {
		int max = statuses.size();
		for (int i = 0; i < max; i++) {
			if (statuses.get(i) == VolatileStatus.CONFUSION) {
				return true;
			}
		}
		return false;
	} // hasConfusionStatus(ArrayList<VolatileStatus> statuses)
	
	/*
	 	Parameters:        
        --statuses: The volatile status conditions of a pokemon

        Desc/Purpose:
        --To determine if the pokemon is still confused after reducing
          its confusion turns
        
        Returns:
        --True if the pokemon was unable to use its move
        --False otherwise
	*/
	Pokemon setOpponentConfusionTurns(Pokemon pokemon, String firstToAct) {
		Random rand = new Random();
		if (firstToAct.compareTo("1") == 0) {
			//pokemon.setSleepTurns(ThreadLocalRandom.current().nextInt(2, 5+1));
			int randNum = rand.nextInt((5 - 2) + 1) + 2;
			
			if (randNum > 5) {
				randNum = 5;
			}
			
			pokemon.setConfusionTurns(randNum); // Use 2 to 5 so that we don't have a first
			                                                          // turn wake
		}
		
		else if (firstToAct.compareTo("2") == 0) {
			//pokemon.setSleepTurns(ThreadLocalRandom.current().nextInt(1, 4+1));			
			int randNum = rand.nextInt((4 - 1) + 1) + 1; 
			
			if (randNum > 4) { // Ensure that we don't exceed 4 turns
				randNum = 4;
			}
			
			pokemon.setConfusionTurns(randNum); // Confusion lasts for 1 to 4 turns
		}
		
		else {
			System.out.println("In setOpponentConfusionTurns: " + firstToAct + " is invalid!");
		}
		
		return pokemon;
	} // setOpponentConfusionTurns(Pokemon pokemon, String firstToAct)
	
	/*
		Parameters:
		--N/A

		Desc/Purpose:
		--To determine if a pokemon hurt itself in confusion

		Returns:
		--True if the pokemon hurt itself in confusion
		--False, otherwise
	*/
	public boolean hurtSelfInConfusion() {
		if (wasSuccess(67)) { // Confused pokemon have a 67% chance of using a move
			return false; // The pokemon was able to use its move
		}		
		return true;
	} // hurtSelfInConfusion()
	
	/*
		Parameters:        
        --statuses: The volatile statuses that a pokemon has        

        Desc/Purpose:
        --To determine if the pokemon is attracted
        
        Returns:
        --True if the pokemon is attracted to its opponent
        --False, otherwise
	*/
	public boolean hasAttractionStatus(ArrayList<VolatileStatus> statuses) {
		int max = statuses.size();
		for (int i = 0; i < max; i++) {
			if (statuses.get(i) == VolatileStatus.ATTRACTION) {
				return true;
			}
		}
		return false;
	} // hasAttractionStatus(ArrayList<VolatileStatus> statuses)
	
	/*
		Parameters:
    	--N/A

		Desc/Purpose:
		--To determine if a a pokemon is immobilized by love

		Returns:
		--True if the pokemon was immobilized by love
		--False, otherwise
	 */
	public boolean immobilizedByLove() {
		if (wasSuccess(50)) { // Attracted pokemon have a 50% chance of using a move
			return false; // The pokemon was able to use its move
		}		
		return true;
	} // immobilizedByLove()
	
	/*
	 	Parameters:
        --atkPokeGender: The gender of the attacking pokemon
        --defPokeGender: The gender of the defending pokemon       

        Desc/Purpose:
        --To determine if the defending pokemon can be attracted
          to the attacking pokemon based on the genders of both
          pokemon
        
        Returns:
        --True if the defending pokemon can be attracted
        --False, otherwise
	*/
	public boolean isGenderMismatch(Gender atkPokeGender, Gender defPokeGender) {
		if (atkPokeGender == defPokeGender) {
			return true;
		}
		
		else if (defPokeGender == Gender.NONE) {
			return true;
		}
		return false;
	} // isGenderMismatch(Gender atkPokeGender, Gender defPokeGender)
	
	/*
    	Parameters:
        --keyword: The keyword in an effect's description

    	Desc/Purpose:
    	--To determine if a move can flinch the opponent
    
    	Returns:
    	--True if the move can flinch the opponent
    	--False, otherwise
    */
	public boolean canFlinchOpponent(String keyword) {
		if (keyword.compareTo("Flinch") == 0) {
			return true;
		}
		return false;
	} // canFlinchOpponent(String keyword)
	
	/*
    	Parameters:
    	--statuses: The volatile statuses that a pokemon has        

    	Desc/Purpose:
    	--To determine if the pokemon flinched
    
    	Returns:
    	--True if the pokemon flinched
    	--False, otherwise
    */
	public boolean didFlinch(ArrayList<VolatileStatus> statuses) {
		int max = statuses.size();
		for (int i = 0; i < max; i++) {
			if (statuses.get(i) == VolatileStatus.FLINCH) {
				return true;
			}
		}
		return false;
	} // didFlinch(ArrayList<VolatileStatus> statuses)
	
	/*
    	Parameters:
    	--keyword: The word in the effect that tells us what status
      	  effect to apply

    	Desc/Purpose:
    	--To convert the keyword into a nonvolatile status
          
    	Returns:     	
        --A NonVolatileStatus
    */
	public NonVolatileStatus convertStringToNonVolatileStatus(String keyword){
		if (keyword.compareTo("Poison") == 0) {
			return NonVolatileStatus.POISON;
		}
		
		else if (keyword.compareTo("Badly_Poison") == 0) {
			return NonVolatileStatus.BADLY_POISON;
		}
		
		else if (keyword.compareTo("Paralysis") == 0) {
			return NonVolatileStatus.PARALYSIS;
		}
		
		else if (keyword.compareTo("Burn") == 0) {
			return NonVolatileStatus.BURN;
		}
		
		else if (keyword.compareTo("Sleep") == 0) {
			return NonVolatileStatus.SLEEP;
		}
		
		else if (keyword.compareTo("Freeze") == 0) {
			return NonVolatileStatus.FREEZE;
		}
		return NonVolatileStatus.NONE;
	} // convertStringToNonVolatileStatus(String keyword)
	
	/*
		Parameters:
		--keyword: The word in the effect that tells us what status
  	  	  effect to apply

		Desc/Purpose:
		--To convert the keyword into a volatile status
      
		Returns:     	
    	--A VolatileStatus
	 */
	public VolatileStatus convertStringToVolatileStatus(String keyword) {
		if (keyword.compareTo("Attraction") == 0) {
			return VolatileStatus.ATTRACTION;
		}
		
		else if (keyword.compareTo("Confusion") == 0) {
			return VolatileStatus.CONFUSION;
		}
		
		else if (keyword.compareTo("Flinch") == 0) {
			return VolatileStatus.FLINCH;
		}
		
		return VolatileStatus.NONE;
	} // convertStringToVolatileStatus(String keyword)
	
	/*
    	Parameters:
    	--moveType: The move's typing (e.g. FIRE, WATER, etc.)
    	--primaryType: A pokemon's primary type
    	--secondaryType: A pokemon's secondary type

    	Desc/Purpose:
    	--Check if the move gets a Same Type Attack Bonus (STAB)
    	  (i.e., the move's type matches one of the pokemon's
    	  type(s))
    
    	Returns:
    	--True if the move is STABed
    	--False otherwise
	*/
	public boolean isSTAB(Type moveType, Type primaryType, Type secondaryType) {
		if (moveType == primaryType) { 
			return true;
		}
		
		else if (secondaryType != Type.NONE) { // The pokemon has a secondary type
			if (moveType == secondaryType) {
				return true;
			}
		}
		return false; // The move's type does not match the pokemon's type(s)
	} // isSTAB(Type moveType, Type primaryType, Type secondaryType)
	
	/*
	 	Parameters:
	 	--moveClassification: The move's classification:
	 	  --PHYSICAL,
	 	  --SPECIAL, or
	 	  --STATUS
	 	  
	 	Desc/Purpose:
	 	--To determine if a move will deal direct damage to the opponent
	 	
	 	Returns:
	 	--true, if the move will deal direct damage
	 	--false, otherwise
	 */
	public boolean dealsDirectDamage(Classification moveClassification) {
		if (moveClassification == Classification.PHYSICAL || moveClassification == Classification.SPECIAL) {
			return true;
		}
		return false;
	} // dealsDirectDamage(Classification moveClassification)
	
	/*
    	Parameters:
        --pokemon: The pokemon to get the move from
    	--moveOption: The option the player chose from the Move Menu

    	Desc/Purpose:
    	--To get the move from the pokemon's moveset based on the player's choice
    
    	Returns:
    	--A move from the pokemon's moveset
	*/
	public Move getPokeMove(Pokemon pokemon, int moveOption) {		
		List<Move> moveset = pokemon.getMoveset();
		return moveset.get(moveOption - 1);
	} // getPokeMove(Pokemon pokemon, int moveOption)
	
	/*
    	Parameters:
    	--N/A      

    	Desc/Purpose:
    	--To get the move option for the move "Struggle"
    
    	Returns:
    	--The move option for the move "Struggle"
    */
	public final int getStruggleOption() {
		return this.STRUGGLE_OPTION;
	} // getStruggleOption()
	
	/*
    	Parameters:
    	--N/A
    
    	Desc/Purpose:
    	--This used to get the move "Struggle" so that we are not
          working directly with the move (We don't want to accidently
          modify it)
    
    	Returns:
    	--N/A
    */
	public Move getStruggleMove() {
		return this.struggle;
	} // getStruggleMove()
	
	/*
    	Parameters:
    	--N/A
    
    	Desc/Purpose:
    	--This used to get the appropriate string for display how
      	  effective an attacking move was
    
    	Returns:
    	--N/A
    */
	public String getAttackEffectiveness() {
		return this.attackEffectiveness;
	} // getAttackEffectiveness()
	
	/*
    	Parameters:    
    	--moveAccuracy: The move's accuracy
    	--atkAccuracy: The accuracy of the pokemon using the move
    	--defEvasion: The evasion of the opponent

    	Desc/Purpose:
    	--The pokemon games generate a random number between 0 and 255 (255 is the max decimal for
          the original 8-bit games) and then uses the formula:
          	chanceToHit = 255 * (move_accuracy / 100) * (attacking_pokemon_accuracy) * (defending_pokemon_evasion)
          and then checks to see if the random number is less than or equal to the chanceToHit to
          determine if the move hit the defending pokemon

          Note: Formula is from https://pokemondb.net/pokebase/37866/how-do-you-calculate-the-chance-of-a-move-hitting
    
    	Returns:
    	--True if the move hit
    	--False otherwise
    */
	public boolean didMoveHit(double moveAccuracy, Pokemon attackingPoke, Pokemon defendingPoke) {
		if (moveAccuracy > 100) { // We do not need to check the accuracy
			return true;
		}
		Random rand = new Random();
		double atkAccuracy = attackingPoke.getAccuracy() * this.alternateMultipliers[attackingPoke.getAccuracyStage() + 6];
		double defEvasion = defendingPoke.getEvasion() * this.alternateMultipliers[defendingPoke.getEvasionStage() + 6];
				
		double endOfRange = 100;
		int randNum = rand.nextInt((int)endOfRange) + 1;		
		double chanceToHit = moveAccuracy * (atkAccuracy / defEvasion);		
		
		if (randNum <= chanceToHit) {
			return true;
		}
		return false;
	} // didMoveHit(int moveAccuracy, Pokemon attackingPoke, Pokemon defendingPoke)
	
	/*
    	Parameters:
        --word: The word in the effect that tells us what the effect
          chance is

    	Desc/Purpose:
    	--To get the chance of applying an effect
    
    	Returns:
    	--The chance of applying an effect as a double
    */
	double getEffectChance(String word) {
		String newStr = word.replace("%", ""); // Remove the "%"		
		return Double.parseDouble(newStr);
	} // getEffectChance(String word)
	
	/*
    	Parameters:
    	--words: An array where each element is a word in the effect

    	Desc/Purpose:
    	--To get the keyword in an effect's description
    
    	Returns:
    	--The keyword in an effect's description
	*/
	String getKeyword(String[] words) {
		// The effect starts with the template: "% chance to...", e.g. 
		//    "100% Chance To Poison Opponent" 
		if (words[0].contains("%")) {
			return words[3]; 
		}
		return words[0];
	} // getKeyword(String[] words)	
	
	/*
    	Parameters:
        --target: The category info taken from moveList.txt

    	Desc/Purpose:
    	--Takes the string target so that we can get the appropriate
          target for a move's effect
        
    	Returns:
    	--Target.SELF,
    	--Target.OPPONENT, or
    	--Target.NONE if the target is valid
    	--null if the target is invalid    
    */
	Target convertStringToTarget(String target) {
		if (target.compareTo("Self") == 0) {
			return Target.SELF;
		}
		
		else if (target.compareTo("Opponent") == 0) {			
			return Target.OPPONENT;
		}
		
		else if (target.compareTo("None") == 0) {
			return Target.NONE;
		}
		
		// We should not reach this point
		System.out.println(target + " is not a valid target!");
		return null;
	} // convertStringToTarget(String target)
	
	/*
    	Parameters:
      	--effect: The effect of a move     

    	Desc/Purpose:
    	--To check if a move has no battle effect
    
    	Returns:
    	--True if the move has no battle effect
    	--False otherwise
    */
	public boolean hasNoBattleEffect(String effect) {
		if (effect.compareTo("No Battle Effect None") == 0) {
			return true;
		}
		return false;
	} // hasNoBattleEffect(String effect)
	
	/*
	 	Parameters:
        --self: A reference to the instance whose method was called
        --attackingPoke: The pokemon that used the move
        --moveName: The name of the move used by the attacking pokemon
        --target: The target of the move's effect
        --words: A list of words from the move's effect
        --defendingPoke: The opponent's pokemon

        Desc/Purpose:
        --To change the stats of either the attacking pokemon or the
          defending pokemon based on the move's target
        
        Returns:
        --The pokemon with the appropriate stat changes applied to it
	 */
	ArrayList<Pokemon> changeStats(Pokemon attackingPoke, String moveName, Target target, String[] words, Pokemon defendingPoke){
		int numSuccessfulBuffs = 0;
		//String atkPokeName = attackingPoke.getPokeName();
		String defPokeName = defendingPoke.getPokeName();
		int numWords = words.length;
		boolean canDebuffAndBuff = isDebuffingAndBuffingMove(words, numWords);
		int numSteps = 4;
		int startingPoint = 3; // Skips the words "x% chance to"
		int stoppingPoint = numWords - 2; // Stop before the target
		ArrayList<Pokemon> results = new ArrayList<Pokemon>();
		
		// Determine if the pokemon has any stats that can be buffed
		// Note: We want to skip the word(s) "Stage"/"Stages"
		if (target == Target.SELF) {
			for (int i = startingPoint; i < stoppingPoint; i += numSteps) {
				if (words[i].compareTo("Buff") == 0) {
					if (canBuffStat(attackingPoke, words[i+1])) {
						numSuccessfulBuffs++;
					}
				}
			}
		}
		
		else if (target == Target.OPPONENT) {
			for (int i = startingPoint; i < stoppingPoint; i += numSteps) {
				if (words[i].compareTo("Buff") == 0) {
					if (canBuffStat(defendingPoke, words[i+1])) {
						numSuccessfulBuffs++;
					}
				}
			}
		}			
		
		// The move fails if it can debuff some stats and buff other stats but
		// none of the target's stats can be buffed
		if (canDebuffAndBuff && numSuccessfulBuffs == 0) {			
			printMoveFailedMessage();
			results.add(attackingPoke);
			results.add(defendingPoke);
			return results;			
		}
		
		else {
			int statWordIndex, numStagesIndex, numStages;
			String statWord = "";			
			for (int i = startingPoint; i < stoppingPoint; i += numSteps) {
				statWordIndex = i + 1;
				numStagesIndex = i + 2;
				statWord = words[statWordIndex];
				numStages = Integer.valueOf(words[numStagesIndex]);
				
				if (words[i].compareTo("Debuff") == 0) {
					if (target == Target.SELF) {
						if (canDebuffStat(attackingPoke, statWord)) {
							attackingPoke = debuffStat(attackingPoke, statWord, numStages);
							printDebuffSuccessMessage(statWord, numStages, target, defPokeName);
						}
						
						else {
							printDebuffFailedMessage(statWord, target, defPokeName);
						}
					}
					
					else if (target == Target.OPPONENT) {
						if (canDebuffStat(defendingPoke, statWord)) {
							defendingPoke = debuffStat(defendingPoke, statWord, numStages);
							printDebuffSuccessMessage(statWord, numStages, target, defPokeName);
						}
						
						else {
							printDebuffFailedMessage(statWord, target, defPokeName);
						}
					}
				}
				
				else if (words[i].compareTo("Buff") == 0) {
					if (target == Target.SELF) {
						if (canBuffStat(attackingPoke, statWord)) {
							attackingPoke = buffStat(attackingPoke, statWord, numStages);
							printBuffSuccessMessage(statWord, numStages, target, defPokeName);
						}
						
						else {
							printBuffFailedMessage(statWord, target, defPokeName);
						}
					}
					
					else if (target == Target.OPPONENT) {
						if (canBuffStat(defendingPoke, statWord)) {
							defendingPoke = buffStat(defendingPoke, statWord, numStages);
							printBuffSuccessMessage(statWord, numStages, target, defPokeName);
						}
						
						else {
							printBuffFailedMessage(statWord, target, defPokeName);
						}
					}
				}
			}
			System.out.println();
			results.add(attackingPoke);
			results.add(defendingPoke);
			return results;
		}
	} // changeStats(Pokemon attackingPoke, String moveName, Target target, String[] words, Pokemon defendingPoke)
	
	/*
    	Parameters:
        --defPokeName: The name of the defending pokemon
    --status: The status that was applied

    Desc/Purpose:
    --To print an appropriate message when a pokemon uses an attacking move
      	that hits the opponent and also display how much damage was done         
    
    	Returns:
    	--N/A
	*/
	public void printStatusEffectSuccessMessage(String defPokeName, String status) {
		if (status.compareTo("Poison") == 0) {
			System.out.println("The opposing " + defPokeName + " was poisoned.");
		}
		
		else if (status.compareTo("Badly_Poison") == 0) {
			System.out.println("The opposing " + defPokeName + " was badly poisoned.");
		}
		
		else if (status.compareTo("Burn") == 0) {
			System.out.println("The opposing " + defPokeName + " was burned.");
		}
		
		else if (status.compareTo("Paralysis") == 0) {
			System.out.println("The opposing " + defPokeName + " was paralyzed.");
			System.out.println("It may not move.");
		}
		
		else if (status.compareTo("Freeze") == 0) {
			System.out.println("The opposing " + defPokeName + " was frozen.");
		}
		
		else if (status.compareTo("Sleep") == 0) {
			System.out.println("The opposing " + defPokeName + " fell asleep.");
		}
		
		else if (status.compareTo("Confusion") == 0) {
			System.out.println("The opposing " + defPokeName + " was confused.");
		}
		
		else if (status.compareTo("Attraction") == 0) {
			System.out.println("The opposing " + defPokeName + " was fell in love with the opponent.");
		}
		System.out.println();
	} // printStatusEffectSuccessMessage(String defPokeName, String status)
	
	/*
    	Parameters:
    	--moveType: The type of the move used against the pokemon
    	--primaryType: The primary type of a pokemon         
    	--secondaryType: The secondary type of a pokemon        

    	Desc/Purpose:
    	--Get the type modifier that goes into the damage calculations
    
    	Returns:
    	--The type modifier
    */
	public double getTypeResult(Type moveType, Type primaryType, Type secondaryType) {
		int row = moveType.getValue(); // The move's type tells us what row of the type chart to use
		
		// The columns refer to the pokemon's type(s)
		int column1 = primaryType.getValue();
		int column2 = secondaryType.getValue();
		
		double typeDamage = this.typeChart[row][column1] * this.typeChart[row][column2];
		
		setAttackEffectiveness(typeDamage);
		
		return typeDamage;		
	} // getTypeResult(Type moveType, Type primaryType, Type secondaryType)
	
	/*
		Parameters:
        --typeDamage: The damaging modifier based on the type of the attacking pokemon's
          move and the type(s) of the defending pokemon

    	Desc/Purpose:
    	--Sets the appropriate message based on if the move was
      	  --super effective
      	  --not very effective
      	  --not effective
      	  --neutral    
	*/
	public void setAttackEffectiveness(double typeDamage) {
		if (typeDamage >= 2.0) { // Move will deal at least double damage
			this.attackEffectiveness = " super effective";
		} 
		
		else if (typeDamage < 1.0 && typeDamage > 0) { // Move will deal reduced damage
			this.attackEffectiveness = " not very effective";
		}
		
		else if (typeDamage == 0.0) { // Move will deal no damage
			this.attackEffectiveness = " not effective";
		}
		
		else { // Move will deal neutral damage
			this.attackEffectiveness = " ";
		}
	} // setAttackEffectiveness(double typeDamage)
	
	/*
    Parameters:
    --self: A reference to the instance whose method was called        
    --pokemon: The pokemon taking damage or being healed        
    --damage: How much damage is dealt to the pokemon
    --healAmount: How much to heal the pokemon

    	Desc/Purpose:
    	--Update a pokemon's health based on the damage dealt
    
    	Returns:
    	--A pokemon with its updated health
	*/
	public Pokemon updateHealth(Pokemon pokemon, int damage, int healAmount) {
		int currentHealth = pokemon.getHP();
		String pokeID = pokemon.getPokeID();
		char owner = pokeID.charAt(0);
		
		if (damage > 0) {
			if (damage > currentHealth) {
				pokemon.setHP(0); // A pokemon's HP should not go below zero (0)
			}
			
			else {
				pokemon.setHP(currentHealth - damage);
			}
		}
		
		else if (healAmount > 0) {
			int totalHP = currentHealth + healAmount;
			int maxHP = pokemon.getMaxHP();
			
			if (totalHP <= maxHP) {
				pokemon.setHP(totalHP);
			}
			
			else {
				pokemon.setHP(maxHP); // A pokemon's health should not go above its max HP
			}
		}
		
		// Update the appropriate team
		if (owner == '1') {
			this.team1 = updateTeam(pokemon, this.team1);
		}
		
		else if (owner == '2') {
			this.team2 = updateTeam(pokemon, this.team2);
		}
		
		return pokemon;
	} // updateHealth(Pokemon pokemon, int damage, int healAmount)
	
	
	/*
    	Parameters:           
    	--pokemon: The pokemon taking damage        
    	--moveOption: The move option selected by the pokemon's owner              

    	Desc/Purpose:
    	--Update a pokemon's  remaining PP for the move used
    
    	Returns:
    	--A pokemon with its moveset updated
    */
	public Pokemon updatePowerPoints(Pokemon pokemon, int moveOption) {
		String pokeID = pokemon.getPokeID();
		char owner = pokeID.charAt(0);
		List<Move> moveset = pokemon.getMoveset();
		int index = moveOption - 1;
		int remainingPP = moveset.get(index).getPP() - 1;
		moveset.get(index).setPP(remainingPP);
		pokemon.setMoveset(moveset);
		
		if (owner == '1') {
			this.team1 = updateTeam(pokemon, this.team1);
		}
		
		else if (owner == '2') {
			this.team2 = updateTeam(pokemon, this.team2);
		}
		
		return pokemon;
	} // updatePowerPoints(Pokemon pokemon, int moveOption)
	
	/*
    	Parameters:    
    	--pokemon: The pokemon taking damage
    	--team: The player's team                       

    	Desc/Purpose:
    	--Update the player's team
    
    	Returns:
    	--The player's updated team
    */
	public ArrayList<Pokemon> updateTeam(Pokemon pokemon, ArrayList<Pokemon> team){
		String pokeID = pokemon.getPokeID();
		char charIndex = pokeID.charAt(1);
		int intIndex = Character.getNumericValue(charIndex);
		
		team.set(intIndex, pokemon);
		
		return team;
	} // updateTeam(Pokemon pokemon, ArrayList<Pokemon> team)
	
	/*
    	Parameters:
      	--pokemon: The pokemon we want to check to see if it has fainted      

    	Desc/Purpose:
    	--To check to see if a pokemon has fainted or not based on its
      	  current HP
    
    	Returns:
    	--True if the pokemon has fainted (i.e., its current HP <= 0
    	--False otherwise
    */
	boolean hasFainted(Pokemon pokemon) {
		if (pokemon.getHP() <= 0) {
			return true;
		}
		return false;
	} // hasFainted(Pokemon pokemon)
	
	/*
	 	Parameters:                
        --team: The pokemon we want to check to see if it has fainted      

        Desc/Purpose:
        --To check to see if a player's entire team has fainted
        
        Returns:
        --True if the entire team has fainted (i.e., their current HP <= 0)
        --False otherwise
	*/
	boolean hasTeamFainted(ArrayList<Pokemon> team) {
		int teamSize = team.size();
		
		for(int i = 0; i < teamSize; i++) {
			if (!hasFainted(team.get(i))) {
				return false; // There is at least one pokemon that has not fainted
			}
		}		
		return true;
	} // hasTeamFainted(ArrayList<Pokemon> team)
	
	/*
	 	Parameters:        
        --attackingPoke: The pokemon using the copying move
        --thingToCopy: The thing to copy from the defending pokemon
        --defendingPoke: The pokemon that is being copied

        Desc/Purpose:
        --To copy something from the defending pokemon
        
        Returns:
        --The attacking pokemon after it copies something
	*/
	public Pokemon handleCopying(Pokemon attackingPoke, String thingToCopy, Pokemon defendingPoke) {
		if (thingToCopy.compareTo("Pokemon") == 0) {
			attackingPoke.copyPokemon(defendingPoke);
			printTransformedMessage(attackingPoke.getPokeName(), defendingPoke.getPokeName());
		}
		
		else if (thingToCopy.compareTo("Stat_Stages") == 0) {
			attackingPoke.copyStages(defendingPoke);
			printCopyStatsMessage();
		}
		
		else {
			System.out.println("Cannot copy " + thingToCopy + ".");
		}
		
		return attackingPoke;
	} // handleCopying(Pokemon attackingPoke, String thingToCopy, Pokemon defendingPoke)
	
	/*
    	Parameters:
    	--keyword: The keyword in an effect's description    

    	Desc/Purpose:
    	--To check if a move is a Non-Volatile or Volatile status move
    
    	Returns:
    	--True if the move is a status move
    	--False otherwise
    */
	boolean isStatusMove(String keyword) {
		if (isNonVolatileStatusMove(keyword)) {
			return true;
		}
		
		else if (isVolatileStatusMove(keyword)) {
			return true;
		}
		
		return false;
	} // isStatusMove(String keyword)
	
	/*
    	Parameters:    	        
    	--keyword: The keyword in an effect's description    

    	Desc/Purpose:
    	--To check if a status is non-volatile
    
    	Returns:
    	--True if the status is non-volatile
    	--False otherwise
	*/
	boolean isNonVolatileStatusMove(String keyword) {
		if (this.nonVolatileStrings.contains(keyword)) {
			return true;
		}		
		return false;
	} // isNonVolatileStatusMove(String keyword)
	
	/*
		Parameters:    	        
		--keyword: The keyword in an effect's description    

		Desc/Purpose:
		--To check if a status is volatile

		Returns:
		--True if the status is volatile
		--False otherwise
	 */
	boolean isVolatileStatusMove(String keyword) {
		if (this.volatileStrings.contains(keyword)) {
			return true;
		}		
		return false;
	} // isVolatileStatusMove(String keyword)
	
	/*
	  	Parameters:        
        --effects: The effects of the attacking pokemon's move

        Desc/Purpose:
        --To determine if the move is an OHKO move
        
        Returns:
        --True if the move is an OHKO move
        --False, otherwise
	*/
	boolean isOHKOMove(String keyword) {
		if (keyword.compareTo("OHKO") == 0) {
			return true;
		}		
		return false;
	} // isOHKOMove(String keyword)
	
	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if the move is a copying move
        
        Returns:
        --True if the move is a copying move
        --False, otherwise
	*/
	boolean isCopyingMove(String keyword) {
		if (keyword.compareTo("Copy") == 0) {
			return true;
		}		
		return false;
	} // isCopying(String keyword)
	
	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if a move counters an opponent's move
          (i.e., deals extra damage in return)
        
        Returns:
        --True if the move counters an opponent's move
        --False, otherwise
	*/
	boolean isCounteringMove(String keyword) {
		if (keyword.compareTo("Counters") == 0) {
			return true;
		}
		return false;
	} // isCounteringMove(String keyword)
	
	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if a move has sacrifices the pokemon
        
        Returns:
        --True if the move sacrifices the pokemon
        --False, otherwise
	*/
	boolean isSacrificingMove(String keyword) {
		if (keyword.compareTo("Sacrifice") == 0) {
			return true;
		}
		return false;
	} // isSacrificingMove(String keyword)
	
	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if a move has recoil
        
        Returns:
        --True if the move has recoil
        --False, otherwise
	*/
	boolean hasRecoil(String keyword) {
		if (keyword.compareTo("Recoil") == 0) {
			return true;
		}
		return false;
	} // hasRecoil(String keyword)

	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if a move heals the user
        
        Returns:
        --True if the move heals the user
        --False, otherwise
	*/
	boolean isHealingMove(String keyword) {
		if (keyword.compareTo("Heal") == 0) {
			return true;
		}
		return false;
	} // isHealingMove(String keyword)
	
	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if a move can force a swap
        
        Returns:
        --True if the move can force a swap
        --False, otherwise
	*/
	boolean isSwappingMove(String keyword) {		
		if (keyword.compareTo("Swap") == 0) {
			return true;
		}		
		return false;
	} // isSwappingMove(String keyword)
	
	/*
	 	Parameters:        
        --keyword: The keyword in an effect's description

        Desc/Purpose:
        --To determine if the move change change any stats
        
        Returns:
        --True if the move can change stats
        --False, otherwise
	*/
	boolean isStatChangingMove(String keyword) {		
		if (keyword.compareTo("Buff") == 0 || keyword.compareTo("Debuff") == 0) {
			return true;
		}		
		return false;
	} // isStatChangingMove(String keyword)
	
	/*
	 	Parameters:        
        --words: The individual words describing the effect          
        --numWords: The number of "words" that words has        

        Desc/Purpose:
        --To determine if a move can debuff certain stats and buff other stats
          of a pokemon
        
        Returns:
        --True: The move can debuff certain stats and buff other stats
          of a pokemon
        --False: otherwise
	*/
	public boolean isDebuffingAndBuffingMove(String[] words, int numWords) {
		boolean canDebuff = false;
		boolean canBuff = false;
		
		for (int i = 0; i < numWords; i++) {
			if (words[i].compareTo("Buff") == 0) {
				canBuff = true;
			}
			
			else if (words[i].compareTo("Debuff") == 0) {
				canDebuff = true;
			}
			
			if (canDebuff && canBuff) {
				return true;
			}
		}
		return false;		
	} // isDebuffingAndBuffingMove(String[] words, int numWords)
	
	/*
	 	Parameters:        
        --hp: The attacking pokemon's Hit Points
        --maxHP: The attacking pokemon's max Hit Points

        Desc/Purpose:
        --To determine if the user's hp is less than
          its max HP
        
        Returns:
        --True if the user's hp is less than
          its max HP
	*/
	public boolean canHeal(int hp, int maxHP) {
		if (hp < maxHP) {
			return true;
		}
		return false;
	} // canHeal(int hp, int maxHP)
	
	/*
	 	Parameters:        
        --statToChange: The stat to change

        Desc/Purpose:
        --To determine if a stat can be buffed
        
        Returns:
        --True if the stat can be buffed
        --False, otherwise
	*/
	public boolean canBuffStat(Pokemon pokemon, String statToChange) {
		int stage = getAppropriateStatStage(pokemon, statToChange);
		
		if (stage == this.MAX_STAGE) { // The stat is maxed out
			return false;
		}
		return true;
	} // canBuffStat(Pokemon pokemon, String statToChange)
	
	/*
	 	Parameters:
        --self: A reference to the instance whose method was called
        --statToChange: The stat to change

        Desc/Purpose:
        --To determine if a stat can be debuffed
        
        Returns:
        --True if the stat can be debuffed
        --False, otherwise
	*/
	public boolean canDebuffStat(Pokemon pokemon, String statToChange) {
		int stage = getAppropriateStatStage(pokemon, statToChange);
		
		if (stage == this.MIN_STAGE) { // The stat is as low as it can get
			return false;
		}
		return true;
	} // canDebuffStat(Pokemon pokemon, String statToChange)
	
	/*
	 	Parameters:        
        --statToChange: The stat to change

        Desc/Purpose:
        --To get the appropriate base stat so that we can determine
          if a stat can be changed
        
        Returns:
        --One of the following modifiers:
          --Atk,
          --Def,
          --SpAtk,
          --SpDef,
          --Speed,
          --Accuracy, or
          --Evasion

          depending on the stat to change
	*/
	public int getAppropriateStatStage(Pokemon pokemon, String statToChange) {
		if (statToChange.compareTo("Atk") == 0) {
			return pokemon.getAtkStage();
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			return pokemon.getDefStage();
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			return pokemon.getSpAtkStage();
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			return pokemon.getSpDefStage();
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			return pokemon.getSpeedStage();
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			return pokemon.getAccuracyStage();
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			return pokemon.getEvasionStage();
		}
		
		System.out.println(statToChange + " is not a valid pokemon stat!");
		return -1;
	} // getAppropriateStatStage(Pokemon pokemon, String statToChange)
	
	/*
	 	Parameters:        
        --statToChange: The stat to change
        --numStages: The number of stages to buff the stat

        Desc/Purpose:
        --To buff the appropriate stat
        
        Returns:
        --The pokemon with buffed stats
	*/
	public Pokemon buffStat(Pokemon pokemon, String statToChange, int numStages) {
		if (statToChange.compareTo("Atk") == 0) {
			pokemon = buffAtk(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			pokemon = buffDef(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			pokemon = buffSpAtk(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			pokemon = buffSpDef(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			pokemon = buffSpeed(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			pokemon = buffAccuracy(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			pokemon = buffEvasion(pokemon, numStages);
		}
		
		return pokemon;
	} // buffStat(Pokemon pokemon, String statToChange, int numStages)
	
	/*
	 	Parameters:        
        --statToChange: The stat to change
        --numStages: The number of stages to debuff the stat

        Desc/Purpose:
        --To debuff the appropriate stat
        
        Returns:
        --The pokemon with debuffed stats
	*/
	public Pokemon debuffStat(Pokemon pokemon, String statToChange, int numStages) {
		if (statToChange.compareTo("Atk") == 0) {
			pokemon = debuffAtk(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			pokemon = debuffDef(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			pokemon = debuffSpAtk(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			pokemon = debuffSpDef(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			pokemon = debuffSpeed(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			pokemon = debuffAccuracy(pokemon, numStages);
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			pokemon = debuffEvasion(pokemon, numStages);
		}
		
		return pokemon;
	} // debuffStat(Pokemon pokemon, String statToChange, int numStages)
	
	/*
		Parameters:        
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the Atk stat

        Desc/Purpose:
        --To buff the pokemon's Atk stat
        
        Returns:
        --The pokemon with a buffed Atk stat
	*/
	public Pokemon buffAtk(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getAtkStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setAtkStage(newStage);
		return pokemon;
	} // Pokemon buffAtk(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the Atk stat

        Desc/Purpose:
        --To debuff the pokemon's Atk stat
        
        Returns:
        --The pokemon with a debuffed Atk stat
	*/
	public Pokemon debuffAtk(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getAtkStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setAtkStage(newStage);
		return pokemon;
	} // debuffAtk(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the Def stat

        Desc/Purpose:
        --To buff the pokemon's Def stat
        
        Returns:
        --The pokemon with a buffed Def stat
	*/
	public Pokemon buffDef(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getDefStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setDefStage(newStage);
		return pokemon;
	} // buffDef(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the Def stat

        Desc/Purpose:
        --To debuff the pokemon's Def stat
        
        Returns:
        --The pokemon with a debuffed Def stat
	*/
	public Pokemon debuffDef(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getDefStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setDefStage(newStage);
		return pokemon;
	} // debuffDef(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the SpAtk stat

        Desc/Purpose:
        --To buff the pokemon's SpAtk stat
        
        Returns:
        --The pokemon with a buffed SpAtk stat
	*/
	public Pokemon buffSpAtk(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getSpAtkStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setSpAtkStage(newStage);
		return pokemon;
	} // buffSpAtk(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the SpAtk stat

        Desc/Purpose:
        --To debuff the pokemon's SpAtk stat
        
        Returns:
        --The pokemon with a debuffed SpAtk stat
	*/
	public Pokemon debuffSpAtk(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getSpAtkStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setSpAtkStage(newStage);
		return pokemon;
	} // debuffSpAtk(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:       
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the SpDef stat

        Desc/Purpose:
        --To buff the pokemon's SpDef stat
        
        Returns:
        --The pokemon with a buffed SpDef stat
	*/
	public Pokemon buffSpDef(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getSpDefStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setSpDefStage(newStage);
		return pokemon;
	} // buffSpDef(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:	 	
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the SpDef stat

        Desc/Purpose:
        --To debuff the pokemon's SpDef stat
        
        Returns:
        --The pokemon with a debuffed SpDef stat
	*/
	public Pokemon debuffSpDef(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getSpDefStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setSpDefStage(newStage);
		return pokemon;
	} // debuffSpDef(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the Speed stat

        Desc/Purpose:
        --To buff the pokemon's Speed stat
        
        Returns:
        --The pokemon with a buffed Speed stat
	*/
	public Pokemon buffSpeed(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getSpeedStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setSpeedStage(newStage);
		return pokemon;
	} // buffSpeed(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the Speed stat

        Desc/Purpose:
        --To debuff the pokemon's Speed stat
        
        Returns:
        --The pokemon with a debuffed Speed stat
	*/
	public Pokemon debuffSpeed(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getSpeedStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setSpeedStage(newStage);
		return pokemon;
	} // debuffSpeed(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the Accuracy

        Desc/Purpose:
        --To buff the pokemon's Accuracy
        
        Returns:
        --The pokemon with a buffed Accuracy stat
	*/
	public Pokemon buffAccuracy(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getAccuracyStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setAccuracyStage(newStage);
		return pokemon;
	} // buffAccuracy(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the Accuracy stat

        Desc/Purpose:
        --To debuff the pokemon's Accuracy
        
        Returns:
        --The pokemon with a debuffed Accuracy
	*/
	public Pokemon debuffAccuracy(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getAccuracyStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setAccuracyStage(newStage);
		return pokemon;
	} // debuffAccuracy(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to buff
        --numStages: The number of stages to buff the Evasion

        Desc/Purpose:
        --To buff the pokemon's Evasion
        
        Returns:
        --The pokemon with a buffed Evasion
	*/
	public Pokemon buffEvasion(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getEvasionStage();
		int newStage = currStage + numStages;
		
		if (newStage > this.MAX_STAGE) {
			newStage = this.MAX_STAGE;
		}
		
		pokemon.setEvasionStage(newStage);
		return pokemon;
	} // buffEvasion(Pokemon pokemon, int numStages)
	
	/*
	 	Parameters:        
        --pokemon: The pokemon to debuff
        --numStages: The number of stages to debuff the Evasion

        Desc/Purpose:
        --To debuff the pokemon's Evasion
        
        Returns:
        --The pokemon with a debuffed Evasion
	*/
	public Pokemon debuffEvasion(Pokemon pokemon, int numStages) {
		int currStage = pokemon.getEvasionStage();
		int newStage = currStage - numStages;
		
		if (newStage < this.MIN_STAGE) {
			newStage = this.MIN_STAGE;
		}
		
		pokemon.setEvasionStage(newStage);
		return pokemon;
	} // debuffEvasion(Pokemon pokemon, int numStages)
	
	/*
 		Parameters:            
    	--numStages: The number of stages the pokemon was buffed       

    	Desc/Purpose:
    	--To get the appropriate description of how much a stat changed
    
    	Returns:
    	--" increased" if the stat was changed by 1 stage        
    	--" sharply increased" if the stat was changed by 2 stages
    	--" drastically increased" if the stat was changed by 3 or more stages
    
    	Note: Info found at https://www.dragonflycave.com/mechanics/stat-stages
	 */
	public String getStatBuffDesc(int numStages) {
		if (numStages == 1) {
			return " increased";
		}
	
		else if (numStages == 2) {
			return " sharply increased";
		}	
	
		return " drastically increased"; // Greater than or equal to 3		
	} // getStatBuffDesc(int numStages)

	/*
		Parameters:    	     
		--numStages: The number of stages the pokemon was debuffed       

		Desc/Purpose:
		--To get the appropriate description of how much a stat changed

		Returns:
		--" decreased" if the stat was changed by 1 stage    	
		--" sharply decreased" if the stat was changed by 2 stages
		--" drastically decreased" if the stat was changed by 3 or more stages
	
		Note: Info found at https://www.dragonflycave.com/mechanics/stat-stages
	 */
	public String getStatDebuffDesc(int numStages) {
		if (numStages == 1) {
			return " decreased";
		}
	
		else if (numStages == 2) {
			return " harshly decreased";
		}		
	
		return " severely decreased"; // Greater than or equal to 3
	} // getStatDebuffDesc(int numStages)
	
	/*
    	Parameters:    		
    	--pokeName: The name of the pokemon that used the move
        --moveName: The name of the move used by the pokemon

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon
          uses a move
    
      	Returns:
    	--N/A
	 */
	public void printUsedMoveMessage(String pokeName, String moveName) {
		System.out.println(pokeName + " used " + moveName + ".");
	} // printUsedMoveMessage(String pokeName, String moveName)
	
	/*
		Parameters:    		
		--pokeName: The name of the pokemon that used the move    

		Desc/Purpose:
		--To print an appropriate message when the attacking pokemon
      	  uses a move that hits the opponent 

  		Returns:
		--N/A
	 */
	public void printAttackMessage(int damage) {
		if (this.wasCrit) {
			System.out.println("Critical Hit");
		}
		
		if (this.attackEffectiveness != " ") {
			System.out.println("It was" + this.attackEffectiveness + ".");
		}
		System.out.println("It did " + damage + " HP of damage.");
		System.out.println();
	} // printAttackMessage(int damage)
	
	/*
    	Parameters:
    	--N/A
    
    	Desc/Purpose:
    	--To print an appropriate message when a pokemon uses an attacking move
      	  that does not have an effect in battle

      	Note: This function is used only for debugging
    
    	Returns:
    	--N/A
	*/
	public void printHasNoBattleEffectMessage() {
		System.out.println("But nothing happened.");
		System.out.println();
	} // printHasNoBattleEffectMessage()
	
	/*
	 	Parameters:
        --N/A

        Desc/Purpose:
        --To print an appropriate message when a pokemon uses an OHKO
          move
          
        Returns:
        --N/A
	*/
	public void printOHKOMessage() {
		System.out.println("It's a One-Hit KO!");
		System.out.println();
	} // printOHKOMessage()
	
	/*
	 	Parameters:
        --atkPokeName: The name of the player's pokemon
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the player's pokemon
          transformed
        
        Returns:
        --N/A
	*/
	public void printTransformedMessage(String atkPokeName, String defPokeName) {
		System.out.println(atkPokeName + " transformed into the opposing " + defPokeName);
		System.out.println();
	} // printTransformedMessage(String atkPokeName, String defPokeName)
	
	/*
	 	Parameters:
        --N/A
        
        Desc/Purpose:
        --To print a message indicating that the player's pokemon
          copied the opponents stat 
          --Note: is actually copying the stages
        
        Returns:
        --N/A
	*/
	public void printCopyStatsMessage() {
		System.out.println("It copied the opponent's stats.");
		System.out.println();
	} // printCopyStatsMessage(String pokeName)
	
	/*
	 	Parameters:        
        --recoilDamage: The amount of damage done by the recoil

        Desc/Purpose:
        --To print a message indicating that the player's pokemon
          hurt itself if it used a move with recoil
        
        Returns:
        --N/A
	*/
	public void printRecoilMessage(int recoilDamage) {
		System.out.println("It hurt itself in recoil for " + recoilDamage + " HP of damage.");
		System.out.println();
	} // printRecoilMessage(int recoilDamage)
	
	/*
    	Parameters:
        --pokeName: The pokemon that is taking damage
      	  at the end of both pokemons' turn
      	--damage: The amount of damage taken from the status
      	  effect

    	Desc/Purpose:
    	--To print an appropriate message when the pokemon takes
      	  damage at the end of both pokemons' turn
    
    	Returns:
    	--N/A
	*/
	public void printEndOfTurnDamageMessage(Pokemon pokemon, int damage){
		NonVolatileStatus nonVolatileStatus = pokemon.getNonVolatileStatus();
		
		if (nonVolatileStatus == NonVolatileStatus.BURN) {
			System.out.println(pokemon.getPokeName() + " was hurt by the burn for " + damage + " HP of damage.");
		}
		
		else if (nonVolatileStatus == NonVolatileStatus.POISON || nonVolatileStatus == NonVolatileStatus.BADLY_POISON) {
			System.out.println(pokemon.getPokeName() + " was hurt by the poison for " + damage + " HP of damage." );
		}
	} // printEndOfTurnDamageMessage(Pokemon pokemon)
	
	/*
		Parameters:    		
		--N/A    

		Desc/Purpose:
		--To print an appropriate message when the attacking pokemon
  	  	  uses a move that misses the opponent 

		Returns:
		--N/A
	 */
	public void printMissedMessage() {
		System.out.println("It missed.");
		System.out.println();
	} // printMissedMessage()
	
	/*
    	Parameters:
    	--self: A reference to the instance whose method was called    

    	Desc/Purpose:
    	--To print a message indicating that the attacking pokemon's
      	move failed in the instance that it tried to use a move that
      	targeted a recently fainted pokemon
    
    	Returns:
    	--N/A
	*/
	public void printMoveFailedMessage() {
		System.out.println("But it failed.");
		System.out.println();
	} // printMoveFailedMessage()
	
	/*
	 	Parameters:        
        --oldName: The name of the pokemon that is currently out
        --newName: The name of the pokemon that will be swapped in       

        Desc/Purpose:
        --To print a message indicating that the attacking pokemon
          was swapped out for a new one
        
        Returns:
        --N/A
	 */
	public void printSwapMessage(String oldName, String newName) {
		System.out.println("Return, " + oldName + "!");
		System.out.println("Go! " + newName + "!");
		System.out.println();		
	} // printSwapMessage(String oldName, String newName)
	
	/*
    	Parameters:
        --playerName: The name of the player that owns the pokemon
    	--pokeName: The name of the pokemon that has fainted   

    	Desc/Purpose:
    	--To print a message for when a pokemon faints
    
    	Returns:
    	--N/A
	 */
	public void printFaintedMessage(String playerName, String pokeName) {
		System.out.println(playerName + "'s " + pokeName + " has fainted!");
		System.out.println();
	} // printFaintedMessage(String playerName, String pokeName)
	
	/*
	 	Parameters:        
        --playerName: The name of the player that won    

        Desc/Purpose:
        --To print a message for when a the match ends with a victor
        
        Returns:
        --N/A
	 */
	public void printVictoryMessage(String playerName) {
		System.out.println(playerName + " wins!");
		System.out.println();
	} // printVictoryMessage(String playerName)
	
	/*
	 	Parameters:
        --N/A   

        Desc/Purpose:
        --To print a message for when a the match ends in a draw
        
        Returns:
        --N/A
	 */
	public void printTieMessage() {
		System.out.println("Both teams have fainted. The match is a draw.");
		System.out.println();
	} // printTieMessage()
	
	/*
    	Parameters:    
    	--move: The move used by the attacking pokemon
    	--typeDamage: The damaging modifier based on the type of the attacking pokemon's
      	  move and the type(s) of the defending pokemon
    	--defPokeName: The name of the defending pokemon        

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon uses a
      	  move that the defending pokemon is immune to      
    
    	Returns:
    	--N/A
	 */
	public void printImmunityMessage(Move move, double typeDamage, String defPokeName) {		
		String status = "";		
		List<String> effects = move.getEffects();
		int numEffects = effects.size();
		String splitBy = " ";
		for (int i = 0; i < numEffects; i++) {
			String[] words = effects.get(i).split(splitBy);
			String keyword = getKeyword(words);
			
			if (isNonVolatileStatusMove(keyword)) {				
				status = keyword;
				break;
			}
			
			else if (isVolatileStatusMove(keyword)) {
				status = keyword;
				break;
			}			
		}		
		
		if (typeDamage != -1.0) { // The move does direct damage
			if (typeDamage <= 0.0) { // The move did no damage
				printImmuneToAttackMessage(move.getMoveType(), defPokeName);
			}
		}
		
		else {			
			printImmuneToStatusMoveMessage(move.getMoveType(), defPokeName, status);
		}
	} // printImmunityMessage(Move move, double typeDamage, String defPokeName)
	
	/*
    	Parameters:
        --moveType: The type of the move used by the attacking pokemon
    	--defPokeName: The name of the defending pokemon        

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon uses a
      	  an attacking move that the defending pokemon is immune to

        Returns:
    	--N/A
	 */
	public void printImmuneToAttackMessage(Type moveType, String defPokeName) {
		System.out.println("The opposing " + defPokeName + " is immune to " + moveType + " moves.");
		System.out.println();
	} // printImmuneToAttackMessage(Type moveType, String defPokeName)
	
	/*
    	Parameters:    	
    	--moveType: The type of move the attacking pokemon used
    	--defPokeName: The name of the defending pokemon
    	--status: The status that was could not be applied

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon uses a
      	  an attacking move that the defending pokemon is immune to
    
    	Returns:
    	--N/A
	 */
	public void printImmuneToStatusMoveMessage(Type moveType, String defPokeName, String status) {
		if ((status.compareTo("Poison") == 0) || (status.compareTo("Badly_Poison") == 0)) {
			System.out.println("The opposing " + defPokeName + " cannot be poisoned.");
		}
		
		else if (status.compareTo("Burn") == 0) {
			System.out.println("The opposing " + defPokeName + " cannot be burned.");
		}
		
		else if (status.compareTo("Paralysis") == 0) {
			System.out.println("The opposing " + defPokeName + " cannot be paralyzed by " + moveType + " moves.");
		}
		
		else if (status.compareTo("Freeze") == 0) {
			System.out.println("The opposing " + defPokeName + " cannot be frozen.");
		}
		
		else if (status.compareTo("Sleep") == 0) {
			System.out.println("The opposing " + defPokeName + " cannot be put to sleep by " + moveType + " moves.");
		}
		
		else if (status.compareTo("Attraction") == 0) {
			System.out.println("The opposing" + defPokeName + " could not be attracted.");
		}
		System.out.println();
	} // printImmuneToStatusMoveMessage(Type moveType, String defPokeName, String status)
	
	/*
    	Parameters:
    	--status: The nonvolatile status that the move applies
    	--defendingPoke: The defending pokemon        

    	Desc/Purpose:
    	--To print an appropriate message the defending pokemon already has
          a particular status ailment         	
    
    	Returns:
    	--N/A
	 */
	public void printAlreadyHasNonVolatileStatusMessage(NonVolatileStatus status, Pokemon defendingPoke) {
		NonVolatileStatus pokeStatus = defendingPoke.getNonVolatileStatus();
		String pokeName = defendingPoke.getPokeName();
		
		if (pokeStatus == status) {
			if (status == NonVolatileStatus.POISON || status == NonVolatileStatus.BADLY_POISON) {
				System.out.println("The opposing " + pokeName + " is already poisoned.");
			}
			
			else if (status == NonVolatileStatus.BURN) {
				System.out.println("The opposing " + pokeName + " is already burned.");
			}
			
			else if (status == NonVolatileStatus.PARALYSIS) {
				System.out.println("The opposing " + pokeName + " is already paralyzed.");
			}
			
			else if (status == NonVolatileStatus.FREEZE) {
				System.out.println("The opposing " + pokeName + " is already frozen.");
			}
			
			else if (status == NonVolatileStatus.SLEEP) {
				System.out.println("The opposing " + pokeName + " is already asleep.");
			}
		} 
		
		else {
			System.out.println("It was not effective.");
		}
		System.out.println();
	} // printAlreadyHasNonVolatileStatusMessage(NonVolatileStatus status, Pokemon defendingPoke)
	
	/*
    	Parameters:
        --pokeName: The name of the pokemon that is asleep

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon
      	  cannot move due to being asleep
    
    	Returns:
    	--N/A
	 */
	public void printStillSleepingMessage(String pokeName) {
		System.out.println(pokeName + " is fast asleep.");
		System.out.println();
	} // printStillSleepingMessage(String pokeName)
	
	/*
    	Parameters:    
    	--pokeName: The name of the pokemon that woke up

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon
          wakes up
    
    	Returns:
    	--N/A
	 */
	public void printWokeUpMessage(String pokeName) {
		System.out.println(pokeName + " woke up.");
		System.out.println();
	} // printWokeUpMessage(String pokeName)
	
	/*
    	Parameters:
        --pokeName: The name of the pokemon that thawed out

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon
          is thawed out
    
    	Returns:
    	--N/A
	 */
	public void printAttackerThawedMessage(String pokeName) {
		System.out.println(pokeName + " thawed out.");
	} // printAttackerThawedMessage(String pokeName)
	
	/*
    	Parameters:
       	--pokeName: The name of the pokemon that thawed out

    	Desc/Purpose:
    	--To print an appropriate message when the defending pokemon
          is thawed out
    
    	Returns:
    	--N/A
	 */
	public void printOpponentThawedMessage(String pokeName) {
		System.out.println("The opposing " + pokeName + " thawed out.");
		System.out.println();
	} // printOpponentThawedMessage(String pokeName)
	
	/*
    	Parameters:
       --pokeName: The name of the pokemon that is paralyzed

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon
      	  cannot move due to paralysis
    
    	Returns:
    	--N/A
	 */
	public void printFullyParalyzedMessage(String pokeName) {
		System.out.println(pokeName + " was fully paralyzed.");
		System.out.println();
	} // printFullyParalyzedMessage(String pokeName)
	
	/*
    	Parameters:
    	--self: A reference to the instance whose method was called
    	--pokeName: The name of the pokemon that is frozen

    	Desc/Purpose:
    	--To print an appropriate message when the attacking pokemon
      	  cannot move due to being frozen
    
    	Returns:
    	--N/A
	 */
	public void printFrozenSolidMessage(String pokeName) {
		System.out.println(pokeName + " was frozen solid.");
		System.out.println();
	} // printFrozenSolidMessage(String pokeName)
	
	/*
    	Parameters:
        --pokeName: The pokemon's name

    	Desc/Purpose:
    	--To print a message indicating that the player's pokemon
      	  flinched when it was hit
    
    	Returns:
    	--N/A
	 */
	public void printFlinchedMessage(String pokeName) {
		System.out.println(pokeName + " flinched.");
		System.out.println();
	} // printFlinchedMessage(String pokeName)
	
	/*
	  	Parameters:
        --atkPokeName: The name of the pokemon trying to use a move
        --defPokeName: The name of the pokemon that the attacking pokemon
          is attracted to

        Desc/Purpose:
        --To print an appropriate message when the attacking pokemon is
          trying to use a move when it is in love with the defending pokemon
        
        Returns:
        --N/A
	 */
	public void printInLoveMessage(String atkPokeName, String defPokeName) {
		System.out.println(atkPokeName + " is in love with " + defPokeName + "!");
	} // printInLoveMessage(String atkPokeName, String defPokeName)
	
	/*
	 	Parameters:        
        --pokeName: The name of the pokemon trying to use a move        

        Desc/Purpose:
        --To print an appropriate message when the attacking pokemon is
          attracted to the defending pokemon and fails to use the move
        
        Returns:
        --N/A
	 */
	public void printImmobilizedByLoveMessage(String pokeName) {
		System.out.println(pokeName + " was immobilized by love.");
		System.out.println();
	} // printImmobilizedByLoveMessage(String pokeName)
	
	/*
	 	Parameters:        
        --pokeName: The name of the pokemon that is confused

        Desc/Purpose:
        --To print an appropriate message when the attacking pokemon
          is confused
        
        Returns:
        --N/A
	 */
	public void printIsConfusedMessage(String pokeName) {
		System.out.println(pokeName + " is confused!");
	} // printIsConfusedMessage(String pokeName)
	
	/*
	 	Parameters:       
        --pokeName: The name of the pokemon that is confused

        Desc/Purpose:
        --To print an appropriate message when the attacking pokemon
          is no longer confused
        
        Returns:
        --N/A
	 */
	public void printShookOffConfusionMessage(String pokeName) {
		System.out.println(pokeName + " shook off its confusion!");
	} // printShookOffConfusionMessage(String pokeName)
	
	/*
	 	Parameters:        
        --damage: The damage a confused pokemon did to itself

        Desc/Purpose:
        --To print an appropriate message when a pokemon
          hurts itself in confusion
        
        Returns:
        --N/A
	 */
	public void printHurtSelfInConfusionMessage(int damage) {
		System.out.println("It hurt itself in confusion for " + damage + " HP of damage.");
		System.out.println();
	} // printHurtSelfInConfusion(int damage)
	
	/*
	 	Parameters:        
        --amount: The amount of health recovered

        Desc/Purpose:
        --To print a message indicating that the player's pokemon
          healed itself
        
        Returns:
        --N/A
	 */
	public void printHealingMessage(int amount) {
		System.out.println("It restored " + amount + " HP.");
		System.out.println();
	} // printHealingMessage(int amount)
	
	/*
	 	Parameters:        
        --pokeName: The pokemon's name

        Desc/Purpose:
        --To print a message indicating that the player's pokemon
          health is full
        
        Returns:
        --N/A
	 */
	public void printHealingFailedMessage(String pokeName) {
		System.out.println(pokeName + "'s health is full!");
		System.out.println();
	} // printHealingFailedMessage(String pokeName)
	
	/*
	 	Parameters:        
        --pokeName: The pokemon's name

        Desc/Purpose:
        --To print a message indicating that the player's pokemon
          was forced out
        
        Returns:
        --N/A
	 */
	public void printForcedOutMessage(String pokeName) {
		System.out.println(pokeName + " was forced out!");
		System.out.println();
	} // printForcedOutMessage(String pokeName)
	
	/*
	 	Parameters:        
        --statToChange: The stat that was buffed
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's stat was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffSuccessMessage(String statToChange, int numStages, Target target, String defPokeName) {
		if (statToChange.compareTo("Atk") == 0) {
			printBuffedAtkMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			printBuffedDefMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			printBuffedSpAtkMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			printBuffedSpDefMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			printBuffedSpeedMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			printBuffedAccuracyMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			printBuffedEvasionMessage(numStages, target, defPokeName);
		}
		
		else {
			System.out.println("Failed to print message.");
		}		
	} // printBuffSuccessMessage(String statToChange, int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --statToChange: The stat that was debuffed
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's stat was debuffed
        
        Returns:
        --N/A
	*/
	public void printDebuffSuccessMessage(String statToChange, int numStages, Target target, String defPokeName) {
		if (statToChange.compareTo("Atk") == 0) {
			printDebuffedAtkMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			printDebuffedDefMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			printDebuffedSpAtkMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			printDebuffedSpDefMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			printDebuffedSpeedMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			printDebuffedAccuracyMessage(numStages, target, defPokeName);
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			printDebuffedEvasionMessage(numStages, target, defPokeName);
		}
		
		else {
			System.out.println("Failed to print message.");
		}		
	} // printDebuffSuccessMessage(String statToChange, int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:
        --statToChange: The stat that was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's stat could not be buffed
        
        Returns:
        --N/A
	 */
	public void printBuffFailedMessage(String statToChange, Target target, String defPokeName) {
		if (statToChange.compareTo("Atk") == 0) {
			printBuffAtkFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			printBuffDefFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			printBuffSpAtkFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			printBuffSpDefFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			printBuffSpeedFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			printBuffAccuracyFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			printBuffEvasionFailedMessage( target, defPokeName);
		}
		
		else {
			System.out.println("Failed to print message.");
		}		
	} // printBuffFailedMessage(String statToChange, int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --statToChange: The stat that was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's stat could not be debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffFailedMessage(String statToChange, Target target, String defPokeName) {
		if (statToChange.compareTo("Atk") == 0) {
			printDebuffAtkFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Def") == 0) {
			printDebuffDefFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpAtk") == 0) {
			printDebuffSpAtkFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("SpDef") == 0) {
			printDebuffSpDefFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Speed") == 0) {
			printDebuffSpeedFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Accuracy") == 0) {
			printDebuffAccuracyFailedMessage(target, defPokeName);
		}
		
		else if (statToChange.compareTo("Evasion") == 0) {
			printDebuffEvasionFailedMessage(target, defPokeName);
		}
		
		else {
			System.out.println("Failed to print message.");
		}		
	} // printDebuffFailedMessage(String statToChange, int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Atk stat was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedAtkMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its attack!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s attack" + stagesDesc + "!");
		}		
	} // printBuffedAtkMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:       
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Atk stat was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedAtkMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its attack!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s attack" + stagesDesc + "!");
		}		
	} // printDebuffedAtkMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Def stat was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedDefMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its defense!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s defense" + stagesDesc + "!");
		}		
	} // printBuffedDefMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Def stat was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedDefMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its defense!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s defense" + stagesDesc + "!");
		}		
	} // printDebuffedDefMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpAtk stat was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedSpAtkMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its special attack!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s special attack" + stagesDesc + "!");
		}		
	} // printBuffedSpAtkMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpAtk stat was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedSpAtkMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its special attack!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s special attack" + stagesDesc + "!");
		}		
	} // printDebuffedSpAtkMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpDef stat was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedSpDefMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its defense!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s defense" + stagesDesc + "!");
		}		
	} // printBuffedSpDefMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpDef stat was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedSpDefMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its special defense!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s special defense" + stagesDesc + "!");
		}		
	} // printDebuffedSpDefMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:              
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Speed stat was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedSpeedMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its speed!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s speed" + stagesDesc + "!");
		}		
	} // printBuffedSpeedMessage(int numStages, Target target, String defPokeName)
	
	/*
	  	Parameters:        
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Speed stat was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedSpeedMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its speed!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s speed" + stagesDesc + "!");
		}		
	} // printDebuffedSpeedMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:              
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Accuracy was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedAccuracyMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its accuracy!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s accuracy" + stagesDesc + "!");
		}
	} // printBuffedAccuracyMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Accuracy was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedAccuracyMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its accuracy!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s accuracy" + stagesDesc + "!");
		}
	} // printDebuffedAccuracyMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:              
        --numStages: The number of stages the pokemon was buffed
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Evasion was buffed
        
        Returns:
        --N/A
	 */
	public void printBuffedEvasionMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatBuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its evasion!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s evasion" + stagesDesc + "!");
		}	
	} // printBuffedEvasionMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:
        --numStages: The number of stages the pokemon was debuffed
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Evasion was debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffedEvasionMessage(int numStages, Target target, String defPokeName) {
		String stagesDesc = getStatDebuffDesc(numStages);
		
		if (target == Target.SELF) {
			System.out.println("It" + stagesDesc + " its evasion!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s evasion" + stagesDesc + "!");
		}
	} // printDebuffedEvasionMessage(int numStages, Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Atk stat was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffAtkFailedMessage(Target target, String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its attack cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s attack could not be increased!");
		}
	} // printBuffAtkFailedMessage(Target target, String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Atk stat was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffAtkFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its attack could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s attack could not be decreased!");
		}
	} // printDebuffAtkFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Def stat was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffDefFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its defense cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s defense could not be increased!");
		}
	} // printBuffDefFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Def stat was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffDefFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its defense could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s defense could not be decreased!");
		}
	} // printDebuffDefFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpAtk stat was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffSpAtkFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its special attack cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s special attack could not be increased!");
		}
	} // printBuffSpAtkFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpAtk stat was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffSpAtkFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its special attack could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s special attack could not be decreased!");
		}
	} // printDebuffSpAtkFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpDef stat was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffSpDefFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its special defense cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s special defense could not be increased!");
		}
	} // printBuffSpDefFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's SpDef stat was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffSpDefFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its special defense could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s defense could not be decreased!");
		}
	} // printDebuffSpDefFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Speed stat was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffSpeedFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its speed cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s speed could not be increased!");
		}
	} // printBuffSpeedFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Speed stat was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffSpeedFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its speed could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s speed could not be decreased!");
		}
	} // printDebuffSpeedFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Accuracy was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffAccuracyFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its accuracy cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s accuracy could not be increased!");
		}
	} // printBuffAccuracyFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Accuracy was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffAccuracyFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its accuracy could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s accuracy could not be decreased!");
		}
	} // printDebuffAccuracyFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was buffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Evasion was not buffed
        
        Returns:
        --N/A
	 */
	public void printBuffEvasionFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its evasion cannot be increased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s evasion could not be increased!");
		}
	} // printBuffEvasionFailedMessage(Target target , String defPokeName)
	
	/*
	 	Parameters:        
        --target: The pokemon that was debuffed
        --defPokeName: The name of the opponent's pokemon       

        Desc/Purpose:
        --To print a message indicating that the a pokemon's Accuracy was not debuffed
        
        Returns:
        --N/A
	 */
	public void printDebuffEvasionFailedMessage(Target target , String defPokeName) {
		if (target == Target.SELF) {
			System.out.println("Its evasion could not be decreased!");
		}
		
		else if (target == Target.OPPONENT) {
			System.out.println("The opposing " + defPokeName + "'s evasion could not be decreased!");			
		}
	} // printDebuffEvasionFailedMessage(Target target , String defPokeName)
} // Class