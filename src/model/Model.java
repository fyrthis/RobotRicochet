package model;

import java.util.StringTokenizer;

import players.AbstractPlayer;
import players.Players;

public class Model {
	
	Players players;
	Grid grid;
	GameState gameState;
	
	public Model() {
		players = new Players();
		grid = new Grid();
		gameState = new GameState();
	}

	public Players getPlayers() { return players; }
	public Grid getGrid() { return grid; }
	public GameState getGameState() { return gameState; }

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
		
		System.out.println("GRID AFTER RECEIVING FROM SERVEUR");
		for(int ligne = 0; ligne < size_y; ligne++){
			for(int colonne = 0; colonne < size_x; colonne++){
				System.out.print(mat[colonne][ligne]+" ");
			}
			System.out.println();
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

			this.grid.setSymbol(x, y, getSymbolValue(robotColor));
			System.out.println(robotColor+"["+x+","+y+"]" );
		}
		System.out.println("Target: "+tokens[tokens.length-1].charAt(0));
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
		System.out.println(buffer);
		
		StringTokenizer tokenizer = new StringTokenizer(buffer, ")(");
		while(tokenizer.hasMoreElements()){
			String[] playerTokens = tokenizer.nextToken().split(",");
			playerName = playerTokens[0];
			scorePlayer = Integer.parseInt(playerTokens[1]);
			for(AbstractPlayer p : players.getPlayers()){
				if(p.getName().equals(playerName))
					p.setScore(scorePlayer);
			}
		}
	}
}
