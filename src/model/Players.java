package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Players extends Observable {
	
	public class Player {
		String name;
		int nbPoints;
		int nbCoups;
		boolean isConnected;
		
		Player(String name) {
			this.name=name;
			nbPoints = -1;
			nbCoups = -1;
			isConnected = true;
		}

		public String getName() { return name; }
		public int getScore() { return nbPoints; }
		public int getCoups() { return nbCoups; }
		public boolean isConnected() { return isConnected; }
		
		public void setScore(int score) { this.nbPoints = score; }
		public void setNbCoups(int nbCoups) { this.nbCoups = nbCoups; }
	}

	List<Player> players;
	
	public Players() {
		players = new ArrayList<>();
	}
	
	public void add(String name) {
		//Recherche si on a un joueur de ce nom là.
		//Si oui -> le mettre à connecté, il est revenu
		//Si non -> le créer et l'ajouter
		Player p;
		if((p=get(name)) != null) {
			p.isConnected=true;
		} else {
			p = new Player(name);
			players.add(p);
		}
		this.setChanged();
		this.notifyObservers(players);
	}
	
	public Player get(String name) {
		for(Player p : players) {
			if(p.name == name)
				return p;
		}
		return null;
	}
	
	public void remove(String name) {
		Player p = get(name);
		p.isConnected = false;
		setChanged();
		notifyObservers(players);
	}
	
	//Nouveau round
	public void resetRound() {
		for(Player p : players)
			p.nbCoups=Integer.MAX_VALUE;
		setChanged();
		notifyObservers(players);
	}
	
	//Nouveau jeu/session
	public void resetSession() {
		for(Player p : players) {
			p.nbPoints = 0;
			if(!p.isConnected) {
				players.remove(p); //Cette fois, pas besoin de le garder pour son score, tout le monde est à zéro.
			}
		}
		setChanged();
		notifyObservers(players);
	}
	

}
