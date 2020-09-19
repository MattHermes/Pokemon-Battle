/*
 Author: Matt Hermes
   File: Game.py
Version: 0.310
   Date: 02/08/20
   Desc: Handles the main game loop i.e. allowing players
         to choose their pokemon and sending the teams to the
         battle loop. Also deals with any files that need to
         be read.
     
Change(s):
   --Added the following functions:
     --isInteger
	--isOutsideOfRange
	--mainMenu
	--printGeneralInfo
	--printPokemonTypes
	--printTeam
	--viewAndSelectPokemon

     
     TODO:
     --Nothing
       
   
*/

package pokemonBattle;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Game {
	final static int NUM_TYPES = 19;
	final static int TEAM_SIZE = 6;
	final static int MAX_MOVESET_SIZE = 4;
	final static int MAX_NUM_POKEMON = 251;
	static Scanner scanner = new Scanner(System.in);
	
   /*
	 	Parameters:
    	--N/A

    	Desc/Purpose:
    	--Takes the comma-separated info from moveList.txt and parses it so
      	  that they can be used to create the moves that can be used by the
      	  pokemon
        
    	Returns:
    	--The move list
	*/
	public static ArrayList<Move> getMoveList() throws NumberFormatException, IOException{
		String file = "moveList.txt";
		BufferedReader br = null;
		String line = "";
		String splitBy = ", ";
		String splitBy2 = " :: ";
		int rowNumber = 1; // For debugging
		ArrayList<Move> moveList = new ArrayList<Move>();
		
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				String[] sLine = line.split(splitBy);
				String moveName = sLine[0];
				Type moveType = convertStringToType(sLine[1]);
				Classification classification = convertStringToClassification(sLine[2]);				
				int basePower = Integer.parseInt(sLine[3]);
				int maxPP = Integer.parseInt(sLine[4]);
				int accuracy = Integer.parseInt(sLine[5]);
				int priority = Integer.parseInt(sLine[6]);
				double critChance = Double.parseDouble(sLine[7]);
				String effectsString = sLine[8];
				List<String> moveEffects = new ArrayList<String>();
				if (effectsString.compareTo("None") != 0) {
					effectsString = sLine[8].substring(1, sLine[8].length()-1); // Remove the "" marks
					
					// Convert the string to an array list
					String str[] = effectsString.split(splitBy2);					
					moveEffects = Arrays.asList(str);
				}				
				
				Move move = new Move(moveName, moveType, classification, basePower, maxPP, accuracy, priority, critChance, moveEffects);
				moveList.add(move);
				rowNumber++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return moveList;
	} // getMoveList()
	
	/*
	 	Parameters:
    	--classification: The category info taken from moveList.txt

    	Desc/Purpose:
    	--Takes the string category so that we can get the appropriate
          classification for a move
        
    	Returns:
    	--Classification.PHYSICAL,
    	--Classification.SPECIAL, or
    	--Classification.STATUS if the classification is valid
    	--null if the classification is invalid
	*/	
	public static Classification convertStringToClassification(String classification) {
		if (classification.compareTo("Physical") == 0) {
			return Classification.PHYSICAL;
		}
		
		else if (classification.compareTo("Special") == 0) {
			return Classification.SPECIAL;
		}
		
		else if (classification.compareTo("Status") == 0) {
			return Classification.STATUS;
		}
		System.out.println(classification + " is not a valid classification!");
		return null;
	} // convertStringToClassification(String classification)
	
	/*
	 	Parameters:
    	--moveType: The type info taken from either moveList.txt or
          pokemonStats.txt

    	Desc/Purpose:
    	--Takes the string category so that we can get the appropriate
          classification for a move
        
    	Returns:
    	--See Type.py for a list of the types if the type is valid
    	--null if the type is invalid
	*/
	public static Type convertStringToType(String moveType) {
		if (moveType.compareTo("Normal") == 0) {
			return Type.NORMAL;
		}
		
		else if (moveType.compareTo("Fire") == 0) {
			return Type.FIRE;
		}
		
		else if (moveType.compareTo("Water") == 0) {
			return Type.WATER;
		}
		
		else if (moveType.compareTo("Grass") == 0) {
			return Type.GRASS;
		}
		
		else if (moveType.compareTo("Electric") == 0) {
			return Type.ELECTRIC;
		}
		
		else if (moveType.compareTo("Ice") == 0) {
			return Type.ICE;
		}
		
		else if (moveType.compareTo("Fighting") == 0) {
			return Type.FIGHTING;
		}
		
		else if (moveType.compareTo("Poison") == 0) {
			return Type.POISON;
		}
		
		else if (moveType.compareTo("Ground") == 0) {
			return Type.GROUND;
		}
		
		else if (moveType.compareTo("Flying") == 0) {
			return Type.FLYING;
		}
		
		else if (moveType.compareTo("Psychic") == 0) {
			return Type.PSYCHIC;
		}
		
		else if (moveType.compareTo("Bug") == 0) {
			return Type.BUG;
		}
		
		else if (moveType.compareTo("Rock") == 0) {
			return Type.ROCK;
		}
		
		else if (moveType.compareTo("Ghost") == 0) {
			return Type.GHOST;
		}
		
		else if (moveType.compareTo("Dragon") == 0) {
			return Type.DRAGON;
		}
		
		else if (moveType.compareTo("Dark") == 0) {
			return Type.DARK;
		}
		
		else if (moveType.compareTo("Steel") == 0) {
			return Type.STEEL;
		}
		
		else if (moveType.compareTo("Fairy") == 0) {
			return Type.FAIRY;
		}
		
		else if (moveType.compareTo("None") == 0) {
			return Type.NONE;
		}
		
		System.out.println(moveType + " is not a valid type!");
		return null;
	} // convertStringToType(String moveType)
	
	/*
	 	Parameters:
    	--gender: The gender info taken from pokemonGeneralInfo.txt

    	Desc/Purpose:
    	--Takes the string gender so that we can get the appropriate
      	  gender of a pokemon
        
    	Returns:
    	--Gender.MALE,
    	--Gender.FEMALE, or
    	--Gender.NONE if the gender is valid
    	--None if the gender is invalid
	 */
	public static Gender convertStringToGender(String gender) {
		if (gender.compareTo("Male") == 0) {
			return Gender.MALE;
		}
		
		else if (gender.compareTo("Female") == 0) {
			return Gender.FEMALE;
		}
		
		else if (gender.compareTo("None") == 0) {
			return Gender.NONE;
		}
		
		System.out.println(gender + " is not a valid gender!");
		return null;
	} // convertStringToGender(String gender)
	
	/*
	 	Parameters:
    	--movesToAdd: A list of move names
    	--moveList: A list of all of the available moves

    	Desc/Purpose:
    	--To build a moveset for a pokemon based on the list of
      	  move names
        
    	Returns:
    	--The moveset for a pokemon
    	--Note: Returns null if the moveset could not be built
	 */
	public static ArrayList<Move> buildMoveset(List<String> moveNames, ArrayList<Move> moveList){
		int movesetSize = moveNames.size();
		Move move, cloneOfMove = null;
		String moveName = "";
		ArrayList<Move> moveset = new ArrayList<Move>();
		
		for (int i = 0; i < movesetSize; i++) {
			moveName = moveNames.get(i);
			
			if (moveName.compareTo("None") != 0) {
				move = findMove(moveName, moveList);
				
				if (move == null) {
					System.out.println("Error! Moveset could not be built!");
					System.out.println();
					return null;
				}
				/*
				Type moveType = move.getMoveType();
				Classification classification = move.getClassification();
			    int basePower = move.getBasePower();
			    int maxPP = move.getMaxPP();
			    int accuracy = move.getAccuracy();
			    int priority = move.getPriority();
			    double critChance = move.getCritChance();            
			    List<String> effects = move.getEffects();
				cloneOfMove = new Move(moveName, moveType, classification, basePower,
						maxPP, accuracy, priority, critChance, effects);
				*/
				try {					
					cloneOfMove = (Move) move.clone();
				} catch (CloneNotSupportedException e) {			
					e.printStackTrace();
				}
				
				moveset.add(cloneOfMove);
			}
			else {
				moveset.add(null);
			}
		}
		return moveset;
	} // buildMoveset(ArrayList<String> movesToAdd, ArrayList<Move> moveList)
	
	/*
	 	Parameters:
    	--moveName: The name of a move
    	--moveList: A list of all of the available moves

    	Desc/Purpose:
    	--To find the move in the move list based on the
          given name
        
    	Returns:
    	--The move, if it is found
    	--null, otherwise
	 */
	public static Move findMove(String moveName, ArrayList<Move> moveList) {		
		int numMoves = moveList.size();
		for (int i = 0; i < numMoves; i++) {
			if (moveName.compareTo(moveList.get(i).getMoveName()) == 0) {
				return moveList.get(i);
			}
		}
		
		System.out.println(moveName + " was not found!");
		return null;
	} // findMove(String moveName, ArrayList<Move> moveList)
	
	/*
 		Parameters:
		--moveList: A list of all of the available moves

		Desc/Purpose:
		--To get all of the movesets of the available pokemon
    
		Returns:
		--The movesets of the available pokemon
	 */
	public static Move[][] getPokemonMovesets(ArrayList<Move> moveList) throws IOException {
		String file = "pokemonMovesets.txt";
		BufferedReader br = null;
		String line = "";
		String splitBy = ", ";		
		int fileRowNumber = 1; // For debugging 		
		List<String> moveNames = new ArrayList<String>();
		Move[][] movesets = new Move[MAX_NUM_POKEMON][MAX_MOVESET_SIZE];
		ArrayList<Move> moveset = new ArrayList<Move>();
		int row = 0;
	
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				String[] sLine = line.split(splitBy);
				int numWords = sLine.length;
			
				for (int i = 2; i < numWords; i++) { // Skip the pokedex number and pokemon name
					moveNames.add(sLine[i]);
				}
			
				moveset = buildMoveset(moveNames, moveList);		
				moveNames.clear();
				for(int j = 0; j < MAX_MOVESET_SIZE; j++) {	
					
					movesets[row][j] = moveset.get(j);
				}
				//System.out.println();
				row++;
				fileRowNumber++;
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return movesets;
	} // getPokemonMovesets(ArrayList<Move> moveList)
	
	/*
	 	Parameters:
    	--N/A

    	Desc/Purpose:
    	--To get the general info of the available pokemon
        
    	Returns:
    	--The general of the available pokemon as strings
    	--Note: Any conversions that need to happen will occur
                in buildPokemon()
	 */
	public static String[][] getPokemonGeneralInfo() throws IOException{
		final int NUM_WORDS = 6;
		String[][] generalInfo = new String[MAX_NUM_POKEMON][NUM_WORDS];
		
		String file = "pokemonGeneralInfo.txt";
		BufferedReader br = null;
		String line = "";
		String splitBy = ", ";		
		int fileRowNumber = 1; // For debugging		
		int row = 0;
	
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				String[] sLine = line.split(splitBy);				
							
				for(int j = 0; j < NUM_WORDS; j++) {	
					generalInfo[row][j] = sLine[j];
				}
				
				row++;
				fileRowNumber++;
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return generalInfo;
	} // getPokemonGeneralInfo()
	
	/*
	 	Parameters:
    	--N/A

    	Desc/Purpose:
    	--To get the base stats of the available pokemon
        
    	Returns:
    	--The base stats of the available pokemon as strings
    	--Note: Any conversions that need to happen will occur
                in buildPokemon()
	 */
	public static String[][] getPokemonStats() throws IOException{
		final int NUM_WORDS = 8;
		String[][] statsList = new String[MAX_NUM_POKEMON][NUM_WORDS];
		
		String file = "pokemonStats.txt";
		BufferedReader br = null;
		String line = "";
		String splitBy = ", ";		
		int fileRowNumber = 1; // For debugging 				
		int row = 0;
	
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				String[] sLine = line.split(splitBy);				
				int i = 0;
				for (int j = 2; j < NUM_WORDS; j++) { // Skip the pokedex number and pokemon name
					statsList[row][i] = sLine[j];
					i++;
				}
							
				row++;
				fileRowNumber++;
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return statsList;
	} // getPokemonStats()
	
	/*
 		Parameters:
		--generalInfo: The general info of the selected pokemon
		--stats: The stats of the selected pokemon
		--moveset: The moveset of the selected pokemon    

		Desc/Purpose:
		--To build a pokemon that will be put into a player's team
    
		Returns:
		--A pokemon
	 */
	public static Pokemon buildPokemon(String[]generalInfo, String[] stats, Move[] moveset) {
		// General Info
		String pokedexNumber = generalInfo[0];
		String name = generalInfo[1];
		int level = Integer.valueOf(generalInfo[2]);
		Gender gender = convertStringToGender(generalInfo[3]);
		Type primaryType = convertStringToType(generalInfo[4]);
		Type secondaryType = convertStringToType(generalInfo[5]);
	
		// Moveset
		// Note: This is done to insure that two movesets
		// do not share the same address
		List<Move> cloneOfMoveset = new ArrayList<Move>();
		int numMoves = moveset.length;
		for(int i = 0; i < numMoves; i++) {
			Move cloneOfMove = null;
			try {					
				cloneOfMove = (Move) moveset[i].clone();
			} catch (CloneNotSupportedException e) {			
				e.printStackTrace();
			}
			cloneOfMoveset.add(cloneOfMove);
		}
		
		// Stats
		int maxHP = Integer.valueOf(stats[0]);
		int baseAtk = Integer.valueOf(stats[1]);
		int baseDef = Integer.valueOf(stats[2]);
		int baseSpAtk = Integer.valueOf(stats[3]);
		int baseSpDef = Integer.valueOf(stats[4]);
		int baseSpeed = Integer.valueOf(stats[5]);
		
		Pokemon pokemon = new Pokemon(pokedexNumber, name, level, gender, 
				primaryType, secondaryType, cloneOfMoveset, maxHP, baseAtk, baseDef,
				baseSpAtk, baseSpDef, baseSpeed);
	
		return pokemon;
	} // buildPokemon(String[]generalInfo, String[] stats, Move[] moveset)
	
	/*
	 	Parameters:
    	--pokedexNumbers: The pokedex numbers of the selected pokemon
    	--generalInfoList: A list of general info of every available pokemon
    	--statsList: A list of all of the stat of every available pokemon
    	--movesetList: A list of all of the available movesets    

    	Desc/Purpose:
    	--To build a pokemon team based on the given info
        
    	Returns:
    	--A player's pokemon team
	 */
	public static ArrayList<Pokemon> buildTeam(ArrayList<String> pokedexNumbers, String[][] generalInfoList, String[][] statsList, Move[][] movesetList) {
		int teamSize = pokedexNumbers.size();
		ArrayList<Pokemon> team = new ArrayList<Pokemon>();
		int index = 0;
		Pokemon pokemon = null;
		for (int i = 0; i < teamSize; i++) {
			index = Integer.valueOf(pokedexNumbers.get(i)) - 1;
			pokemon = buildPokemon(generalInfoList[index], statsList[index], movesetList[index]);
			team.add(pokemon);
		}
		return team;
	} // buildTeam(ArrayList<String> pokedexNumbers, String[][] generalInfoList, String[][] statsList, Move[][] movesetList)
	
	/*
	 	Parameters:
    	--playerName: The name of the player selecting the pokemon
    	--generalInfoList: A list of general info of every available pokemon
    	--movesetList: A list of all of the available movesets    	

    	Desc/Purpose:
    	--To allow the player to select which pokemon to view based on the pokedex
      	  numbers
        
    	Returns:
    	--A list of pokedex numbers so that we can build the team
	 */
	public static ArrayList<String> mainMenu(String playerName, String[][] generalInfoList, Move[][] movesetList) throws IOException{
		ArrayList<String> pokedexNumbers = new ArrayList<String>();
		ArrayList<String> pokemonNames = new ArrayList<String>();
		int numSelected = 0;
		ArrayList<String> validOptions = new ArrayList<String>((Arrays.asList(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9"})));
		Scanner scanner = new Scanner(System.in);
		
		while(numSelected < TEAM_SIZE) {
			System.out.println("---------------------------------------------------------------------------------------------------");
			System.out.println("Select a range of pokedex numbers:");
			System.out.println("1) 001-030");
			System.out.println("2) 031-060");
			System.out.println("3) 061-090");
			System.out.println("4) 091-120");
			System.out.println("5) 121-151");
			System.out.println("6) 152-182");
			System.out.println("7) 183-213");
			System.out.println("8) 214-243");
			System.out.println("9) 244-251");
			System.out.println("---------------------------------------------------------------------------------------------------");
			System.out.println(playerName + "'s current team:");
			printTeam(pokemonNames);
			String rangeSelection = scanner.nextLine();
			
			if (validOptions.contains(rangeSelection)) {
				String possibleInput = viewAndSelectPokemon(rangeSelection, generalInfoList, movesetList);
				
				if (possibleInput.compareTo("") != 0) {
					pokedexNumbers.add(possibleInput);
					pokemonNames.add(generalInfoList[Integer.valueOf(possibleInput)-1][1]);
					numSelected++;
				}				
			}
			
			else {
				System.out.println("That is not a valid option!");
				System.out.println();
			}
		}
		System.out.println(playerName + "'s current team:");
		printTeam(pokemonNames);
		System.out.println();
		return pokedexNumbers;
	} // mainMenu(String playerName, String[][] generalInfoList, Move[][] movesetList)
	
	/*
	 	Parameters:
    	--rangeSelection: The option the player picked in the main menu    	
    	--generalInfoList: A list of general info of every available pokemon
    	--movesetList: A list of all of the available movesets

    	Desc/Purpose:
    	--To allow the player to view the pokemon and select the ones they
      	  want to add to the team
        
    	Returns:
    	--A pokedex number if the player selected a pokemon
    	--"", if the player wanted to go back
	 */
	public static String viewAndSelectPokemon(String rangeSelection, String[][] generalInfoList, Move[][] movesetList) {
		//Scanner scanner = new Scanner(System.in);
		
		final int BEGIN_OF_RANGE_ONE = 0;
		final int END_OF_RANGE_ONE = 30;
		
		final int BEGIN_OF_RANGE_TWO = END_OF_RANGE_ONE;
		final int END_OF_RANGE_TWO = 60;
		
		final int BEGIN_OF_RANGE_THREE = END_OF_RANGE_TWO;
		final int END_OF_RANGE_THREE = 90;
		
		final int BEGIN_OF_RANGE_FOUR = END_OF_RANGE_THREE;
		final int END_OF_RANGE_FOUR = 120;
		
		final int BEGIN_OF_RANGE_FIVE = END_OF_RANGE_FOUR;
		final int END_OF_RANGE_FIVE = 151;
		
		final int BEGIN_OF_RANGE_SIX = END_OF_RANGE_FIVE;
		final int END_OF_RANGE_SIX = 182;
		
		final int BEGIN_OF_RANGE_SEVEN = END_OF_RANGE_SIX;
		final int END_OF_RANGE_SEVEN = 213;
		
		final int BEGIN_OF_RANGE_EIGHT = END_OF_RANGE_SEVEN;
		final int END_OF_RANGE_EIGHT = 243;
		
		final int BEGIN_OF_RANGE_NINE = END_OF_RANGE_EIGHT;
		final int END_OF_RANGE_NINE = 251;
		
		int beginOfRangeToView = 0, endOfRangeToView = 0;
		
		// Determine which range of pokemon the player wants to see
		if (rangeSelection.compareTo("1") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_ONE;
			endOfRangeToView = END_OF_RANGE_ONE;
		}
		
		else if (rangeSelection.compareTo("2") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_TWO;
			endOfRangeToView = END_OF_RANGE_TWO;
		}
		
		else if (rangeSelection.compareTo("3") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_THREE;
			endOfRangeToView = END_OF_RANGE_THREE;
		}
		
		else if (rangeSelection.compareTo("4") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_FOUR;
			endOfRangeToView = END_OF_RANGE_FOUR;
		}
		
		else if (rangeSelection.compareTo("5") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_FIVE;
			endOfRangeToView = END_OF_RANGE_FIVE;
		}
		
		else if (rangeSelection.compareTo("6") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_SIX;
			endOfRangeToView = END_OF_RANGE_SIX;
		}
		
		else if (rangeSelection.compareTo("7") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_SEVEN;
			endOfRangeToView = END_OF_RANGE_SEVEN;
		}
		
		else if (rangeSelection.compareTo("8") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_EIGHT;
			endOfRangeToView = END_OF_RANGE_EIGHT;
		}
		
		else if (rangeSelection.compareTo("9") == 0) {
			beginOfRangeToView = BEGIN_OF_RANGE_NINE;
			endOfRangeToView = END_OF_RANGE_NINE;
		}
		
		else {
			System.out.println("That is not a valid range!");
			return "";
		}
		
		boolean viewingRange = true;
		String format = "%-10s%s%n"; // for tabs
		while(viewingRange) {
			System.out.println();
			System.out.println("Select a pokemon (or use 'B' to go back):");
			
			for (int i = beginOfRangeToView; i < endOfRangeToView; i++) {
				System.out.printf(format, generalInfoList[i][0], generalInfoList[i][1]); // Print the pokedex number and name of the pokemon
			}
			System.out.println("B)ack");
			
			String option = scanner.nextLine();
			if (isInteger(option)) {
				int intOption = Integer.valueOf(option);				
				
				if (isOutsideOfRange(intOption, beginOfRangeToView + 1, endOfRangeToView + 1)) {
					System.out.println("Please pick an option that is displayed on the screen.");
					System.out.println();
				}
				
				else {
					while(true) {
						int index = intOption - 1;
						printGeneralInfo(generalInfoList[index], movesetList[index]);
											
						System.out.println("Do you want to add " + generalInfoList[index][1] + " to your team (Y/N)");
						String yesOrNo = scanner.nextLine();
						ArrayList<String> validOptions = new ArrayList<String>(Arrays.asList(new String[] {"n", "N", "y", "Y"}));
						if (validOptions.contains(yesOrNo)) {
							if (yesOrNo.compareTo("y") == 0 || yesOrNo.compareTo("Y") == 0) {
								return generalInfoList[index][0];
							}
							else if (yesOrNo.compareTo("n") == 0 || yesOrNo.compareTo("N") == 0) {
								break;
							}
						}
						else {
							System.out.println("That is not a valid option!");
						}
						
					}
				}
			}
			
			else if (option.compareTo("b") == 0 || option.compareTo("B") == 0) {
				return "";
			}
			
			else {
				System.out.println("That is not a vaild option!");
				System.out.println();
			}
		}
		return "";
	} // viewAndSelectPokemon(String rangeSelection, String[][] generalInfoList, Move[][] movesetList)
	
	/*
	 	Parameters:
	 	--pokemonNames: A list of the pokemon names on a player's team
	 	
	 	Desc/Purpose:
	 	--Prints the current list of names
	 	
	 	Returns:
	 	--N/A
	 */
	public static void printTeam(ArrayList<String> pokemonNames) {
		if (!pokemonNames.isEmpty()) {
			int numNames = pokemonNames.size();
			for (int i = 0; i < numNames; i++) {
				if (i < numNames - 1) {
					System.out.print(pokemonNames.get(i) + ", ");
				}
				else {
					System.out.print(pokemonNames.get(i));
				}				
			}
		}
		
		else {
			System.out.println("None selected");
		}
		System.out.println();
	} // printTeam(ArrayList<String> pokemonNames)
	
	/*
		Parameters:    	
		--str: The string to check		

		Desc/Purpose:
		--To determine if a string is an integer 

		Returns:
		--N/A
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("\\d+");
	
		if (str == null) {
			return false;
		}
		return pattern.matcher(str).matches();
	} // isInteger(String str)
	
	/*
	 	Parameters:
    	--option: The option the player picked
    	--begin: The start of the range of options
    	--end: The end of the range of options

    	Desc/Purpose:
    	--To determine if the option is inside the given range
      	  from begin to end
        
    	Returns:
    	--True, if the option is outside of the range
    	--False, otherwise
	 */
	public static boolean isOutsideOfRange(int option, int begin, int end) {
		if ((option < begin) || (option > end)) {
			return true;
		}
		return false;
	} // isOutsideOfRange(int option, int begin, int end)
	
	/*
	 	Parameters:
    	--generalInfo: The general info of the selected pokemon 
    	--moveset: The moveset of the selected pokemon

    	Desc/Purpose:
    	--To print the general info of the selected pokemon
        
    	Returns:
    	--N/A
	 */
	public static void printGeneralInfo(String[] generalInfo, Move[] moveset) {
		System.out.println("General Info:");
		System.out.println("Name: " + generalInfo[1]);
		System.out.println("Level: " + generalInfo[2]);
		System.out.println("Gender: " + generalInfo[3]);
		printPokemonTypes(generalInfo[4], generalInfo[5]);
		System.out.println();
		printMoveset(moveset);
	} // printGeneralInfo(String[] generalInfo, Move[] moveset)
	
	/*
		Parameters:    	
		--primaryType: The primary type of the pokemon
		--secondaryType: The secondary type of the pokemon

		Desc/Purpose:
		--To display a pokemon's type(s)

		Returns:
		--N/A
	 */
	public static void printPokemonTypes(String primaryType, String secondaryType) {
		if (secondaryType.compareTo("None") != 0) {
			System.out.println("Type: " + primaryType + "/" + secondaryType);
		}
	
		else {
			System.out.println("Type: " + primaryType);
		}
	} // printPokemonTypes(String primaryType, String secondaryType)
	
	/*
	 	Parameters:
    	--moveset: The moveset of the selected pokemon 

    	Desc/Purpose:
    	--To allow the player to view the moveset of the selected
      	  pokemon
        
    	Returns:
    	--N/A
	 */
	public static void printMoveset(Move[] moveset) {
		System.out.println("Moveset:");
		int numMoves = moveset.length;
		
		for (int i = 0; i < numMoves; i++) {
			Move move = moveset[i];
			if (move != null) {
				System.out.println(move.getMoveName() + ": " + move.getPP() + " / " + move.getMaxPP());
			}
			
			else {
				System.out.println("--------------------");
			}			
		}
		System.out.println();
	} // printMoveset(Move[] moveset)
	
	/*
	 	Parameters:
    	--N/A

    	Desc/Purpose:
    	--Takes the comma-separated numbers from typeChart.txt and parses it so
          that they can be used for damage calculations
        
    	Returns:
    	--The type chart
	*/
	public static double[][] getTypeChart() throws IOException{
		String file = "typeChart.txt";
		BufferedReader br = null;
		String line = "";
		String splitBy = ", ";
		int rowNumber = 0; // For debugging 
		double[][] typeChart = new double[NUM_TYPES][NUM_TYPES];
		
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				String[] sLine = line.split(splitBy);
				double[] dLine = new double[NUM_TYPES];
				
				for (int i = 0; i < NUM_TYPES; i++) { // Convert line of strings to doubles
					dLine[i] = Double.parseDouble(sLine[i]);
				}
				
				for (int j = 0; j < NUM_TYPES; j++) { // Put the line of doubles in the appropriate row of the type chart
					typeChart[rowNumber][j] = dLine[j];
				}
				rowNumber++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return typeChart;		
	} // getTypeChart()
	
	/*
	 	Parameters:
    	--typeChart: The type chart used to determine the type matchups

    	Desc/Purpose:
    	--Takes the type chart and inverts the multipliers
      	  --'0.0' becomes '2.0'
      	  --'0.5' becomes '2.0
      	  --'1.0' stays the same
      	  --'2.0' becomes '0.5'
        
    Returns:
    --The inverse type chart
	 */
	public static double[][] invertTypeChart(double[][] typeChart){
		double [][] inverseTypeChart = new double[NUM_TYPES][NUM_TYPES];
		for (int i = 0; i < NUM_TYPES; i ++) {
			for (int j = 0; j < NUM_TYPES; j++) {
				if (typeChart[i][j] == 0.0 || typeChart[i][j] == 0.5) {
					inverseTypeChart[i][j] = (double) 2.0;
				}
				
				else if (typeChart[i][j] == 1.0) {
					inverseTypeChart[i][j] = (double) 1.0;
				}
				
				else if (typeChart[i][j] == 2.0) {
					inverseTypeChart[i][j] = (double) 0.5;
				}
			}
		}
		return inverseTypeChart;
	} // invertTypeChart(double[][] typeChart)
	
	/*
	 	Parameters:
    	--team: The team of selected pokemon 
    	--playerID: The player's ID number

    	Desc/Purpose:
    	--To set the poke IDs of the given team
        
    	Returns:
    	--The pokemon team with ids assigned to them
	 */
	public static ArrayList<Pokemon> assignPokeIDs(ArrayList<Pokemon> team, String playerID){
		int teamSize = team.size();
		String pokeID = "";
		for (int i = 0; i < teamSize; i++) {
			pokeID = playerID + Integer.toString(i);
			team.get(i).setPokeID(pokeID);
		}
		
		return team;
	} // assignPokeIDs(ArrayList<Pokemon> team, String playerID)	
	
	/*
	 	Parameters:
    	--None

    	Desc/Purpose:
    	--The main function for the entire game that allows players to:
      	  --Select pokemon
      	  --Give themselves a name
      	  --Decide on the battle mode
      
    Returns:
    --N/A
	 */
	public static void main(String[] args) throws IOException {	
		//Scanner scanner = new Scanner(System.in);
		double[][] typeChart = new double[NUM_TYPES][NUM_TYPES];
		double[][] inverseTypeChart = new double[NUM_TYPES][NUM_TYPES];
		typeChart = getTypeChart();
		inverseTypeChart = invertTypeChart(typeChart);
		
		ArrayList<Move> moveList = new ArrayList<Move>();
		moveList = getMoveList();
		
		Move[][] movesets = new Move[MAX_NUM_POKEMON][MAX_MOVESET_SIZE];
		movesets = getPokemonMovesets(moveList);
			
		String[][] generalInfoList = getPokemonGeneralInfo();
		String[][] statsList = getPokemonStats();
		
		/* For testing */
		/*
		ArrayList<String> pokedexNumbers01 = new ArrayList<String>(Arrays.asList(new String[] {"003", "003", "003", "003", "003", "003"}));
		ArrayList<String> pokedexNumbers02 = new ArrayList<String>(Arrays.asList(new String[] {"006", "006", "006", "006", "006", "006"}));
		
		String playerOneID = "1";
		String playerTwoID = "2";		
		
		String playerOneName = "Player 1";
		String playerTwoName = "Player 2";
		
		ArrayList<Pokemon> team1 = buildTeam(pokedexNumbers01, generalInfoList, statsList, movesets);
		ArrayList<Pokemon> team2 = buildTeam(pokedexNumbers02, generalInfoList, statsList, movesets);
		
		team1 = assignPokeIDs(team1, playerOneID);
		team2 = assignPokeIDs(team2, playerTwoID);
		
		//Battle battle = new Battle(typeChart, team1, team2, playerOneName, playerTwoName);
		Battle battle = new Battle(inverseTypeChart, team1, team2, playerOneName, playerTwoName);
		//battle.battleLoop();
		
		Tests test = new Tests(typeChart);
		//test.printMove(moveList.get(110));
		test.testBuffStatsUntilMax(team1, team2);
		//test.testForcedSwapAttackingPoke(team1, team2, false);
		 */
		/* End test block */
		//if (args.length == 0) {
			//Process p = Runtime.getRuntime().exec("cmd.exe /c start java -jar" + (new File(Game.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getAbsolutePath() + " cmd");
		
		
		System.out.println("Please enter a name 'Player One':");
		String playerOneName = scanner.nextLine();
		System.out.println();
		System.out.println("Please enter a name 'Player Two':");
		String playerTwoName = scanner.nextLine();
		System.out.println();
		
		boolean keepGoing = true;
		
		while(keepGoing) {
			String mode = "";
			ArrayList<String> validModeOptions = new ArrayList<String>(Arrays.asList(new String[] {"r", "R", "i", "I"}));
			
			ArrayList<String> pokedexNumbers01 = mainMenu(playerOneName, generalInfoList, movesets);
			ArrayList<String> pokedexNumbers02 = mainMenu(playerTwoName, generalInfoList, movesets);
			
			ArrayList<Pokemon> team1 = buildTeam(pokedexNumbers01, generalInfoList, statsList, movesets);
			ArrayList<Pokemon> team2 = buildTeam(pokedexNumbers02, generalInfoList, statsList, movesets);
			
			String playerOneID = "1";
			String playerTwoID = "2";
			team1 = assignPokeIDs(team1, playerOneID);
			team2 = assignPokeIDs(team2, playerTwoID);
			
			while(true) {
				System.out.println("---------------------------------------------");
				System.out.println("Select a mode:");
				System.out.println("R)egular battle (Uses standard type matchups)");
				System.out.println("I)nverse battle (Inverts the type matchups)");
				System.out.println("---------------------------------------------");
				System.out.println();
				mode = scanner.nextLine();
				
				if (validModeOptions.contains(mode)) {
					if (mode.compareTo("r") == 0 || mode.compareTo("R") == 0) {
						Battle battle = new Battle(typeChart, team1, team2, playerOneName, playerTwoName);
						battle.battleLoop();
						break;
					}
					
					else if (mode.compareTo("i") == 0 || mode.compareTo("I") == 0) {
						Battle battle = new Battle(inverseTypeChart, team1, team2, playerOneName, playerTwoName);
						battle.battleLoop();
						break;
					}
				}
				
				else {
					System.out.println("That is not a valid option!");
					System.out.println();
				}
			}
			ArrayList<String> validOptions = new ArrayList<String>(Arrays.asList(new String[] {"n", "N", "y", "Y"}));
			String option = "";
			while(true) {
				System.out.println("Play again (Y/N)");
				option = scanner.nextLine();
				System.out.println();
				if (validOptions.contains(option)) {
					break;
				}
				else {
					System.out.println("That is not a valid option!");
					System.out.println();
				}
			}
			if (option.compareTo("n") == 0 || option.compareTo("N") == 0) {
				scanner.close();
				keepGoing = false;
			}
		}
		
		//}
	} // main(String[] args)
} // Class
