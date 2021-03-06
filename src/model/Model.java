package model;

import java.util.StringTokenizer;

import players.Players;

public class Model {
	
	private static volatile Model instance = null;
	 
	Players players;
	Grid grid;
	GameState gameState;
	ChatModel chatModel;
	
	private Model() {
		super();
		players = new Players();
		grid = new Grid();
		gameState = new GameState();
		chatModel = new ChatModel();
	}
	
	public final static Model getInstance(){
		if(Model.instance == null){
			synchronized (Model.class) {
				if(Model.instance == null)
					Model.instance = new Model();
			}
		}
		return Model.instance;
	}

	public Players getPlayers() { return players; }
	public Grid getGrid() { return grid; }
	public GameState getGameState() { return gameState; }
	public ChatModel getChatModel() { return chatModel; }

	public void playerConnected(String name) {
		players.add(name);
	}

	public void playerLeaved(String name) {
		players.remove(name);
	}
	
	public Integer[][] getGridFromBuffer(int size_x, int size_y, String buffer) {
		Integer[][] mat = new Integer[size_x][size_y];
		for(int i = 0; i < size_x; i++)
			for(int j = 0; j < size_y; j++)
				mat[i][j] = 0;
		
		int i = 0;
		
		String current = "";
		while(!buffer.isEmpty() && i < buffer.length()){
			if(buffer.charAt(i) == '('){
				current = "";
			}
			else if(buffer.charAt(i) == ')'){
				String[] tokens = current.split(",");
				int x = Integer.valueOf(tokens[0]);
				int y = Integer.valueOf(tokens[1]);
				String wallPlacement = tokens[2];
				mat[x][y] += getValueWall(wallPlacement);
			}
			else {
				current += buffer.charAt(i);
			}
			i++;
		}
		
		return mat;
	}
	
	private Integer getValueWall(String w){
		Integer v = 0;
		switch(w){
		case "H":
			v = 1;
			break;
		case "D":
			v = 2;
			break;
		case "B":
			v = 4;
			break;
		case "G":
			v = 8;
			break;
		default:;
		}
		return v;
	}


	public void setRobotsFromBuffer(String buffer){
		int size_x = this.grid.getSizeX();
		int size_y = this.grid.getSizeY();
		Integer[][] symbolsGrid = new Integer[size_x][size_y];

		for(int x = 0; x < size_x; x++)
			for(int y = 0; y < size_y; y++)
				symbolsGrid[x][y] = 0;
		
		this.grid.setSymbolGrid(symbolsGrid);
		
		buffer = buffer.substring(1, buffer.length()-1);
		String[] tokens = buffer.split(",");

		this.grid.setTarget(tokens[tokens.length-1].charAt(0));
		String[] listCoordinates = new String[tokens.length-1];
		for(int c = 0; c < listCoordinates.length; c++){
			listCoordinates[c] = tokens[c];
		}
		for(int index = 0; index < listCoordinates.length; index+=2){
			char robotColor = listCoordinates[index].charAt(listCoordinates[index].length()-1);
			String x_str = listCoordinates[index].substring(0, listCoordinates[index].length()-1);
			String y_str = listCoordinates[index+1].substring(0, listCoordinates[index+1].length()-1);

			int x = Integer.parseInt(x_str);
			int y = Integer.parseInt(y_str);
			
			if(robotColor == 'c')
				this.grid.setSymbol(x, y, getSymbolValue(robotColor));
			
			else
				this.grid.initializeRobot(robotColor, x, y);
		}
	}
	
	private int getSymbolValue(char r){
		switch(r){
		case 'r':
			return 21;
		case 'b':
			return 31;
		case 'v':
			return 41;
		case 'j':
			return 51;
		case 'c':
			return 99;
		default:
			return 0;
		}
	}

	
	public void setBilanCurrentSession(String buffer){
		String playerName = "";
		int scorePlayer = 0;
		
		int tour = Integer.parseInt(buffer.substring(0, 1));
		this.gameState.setTour(tour);
		buffer = buffer.substring(2, buffer.length()-1);
		
		StringTokenizer tokenizer = new StringTokenizer(buffer, ")(");
		while(tokenizer.hasMoreElements()){
			String[] playerTokens = tokenizer.nextToken().split(",");
			playerName = playerTokens[0];
			scorePlayer = Integer.parseInt(playerTokens[1]);
			
			// Il faut mettre à jour la liste des joueurs qui participent a la partie courante
			players.updatePlayersScore(playerName, scorePlayer);
		}
	}
}
