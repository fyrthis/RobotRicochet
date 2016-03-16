package model;

public class Model {
	
	Players players;
	Grid grid;
	
	public Model() {
		players = new Players();
		grid = new Grid();
	}

	public Players getPlayers() {
		return players;
	}

	public Grid getGrid() {
		return grid;
	}

	public void playerConnected(String name) {
		players.add(name);	
	}

	public void playerLeaved(String name) {
		players.remove(name);
	}
	
	public int[][] getGridFromBuffer(int size_x, int size_y, String buffer) {
		int[][] mat = new int[size_x][size_y];
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
	
	private int getValueWall(String w){
		int v = 0;
		switch(w){
		case "H":
			v = 8;
			break;
		case "D":
			v = 4;
			break;
		case "B":
			v = 2;
			break;
		case "G":
			v = 1;
			break;
		default:;
		}
		return v;
	}

}
