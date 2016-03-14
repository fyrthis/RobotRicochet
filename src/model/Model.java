package model;

public class Model {
	
	Players players;
	Map map;
	
	public Model() {
		players = new Players();
		map = new Map();
	}

	public Players getPlayers() {
		return players;
	}

	public Map getMap() {
		return map;
	}

	public void playerConnected(String name) {
		players.add(name);	
	}

	public void playerLeaved(String name) {
		players.remove(name);
	}

}
