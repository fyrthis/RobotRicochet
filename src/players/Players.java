package players;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 
 * Holds every player in the game, including the local player.
 *
 */
public class Players extends Observable {
	
	/**
	 * 
	 * Holds a player in the game.
	 *
	 */
	public class Player extends AbstractPlayer {
		boolean isConnected;
		Player(String name) {
			super();
			this.name=name;
			score = -1;
			nbCoups = -1;
			isConnected = true;
		}
		@Override public boolean isConnected() { return isConnected; }
		@Override public void setConnected(boolean b) { isConnected = b; }
	}
	
	/**
	 * 
	 * Holds the local singleton player
	 *
	 */
	public static class LocalPlayer extends AbstractPlayer {
		
		private LocalPlayer(){ super(); }
		private static class LocalPlayerHolder
		{		
			private final static LocalPlayer instance = new LocalPlayer(); 
		}
		public static LocalPlayer getInstance()
		{
			return LocalPlayerHolder.instance;
		}
		
		public void setName(String n) {
			name = n;
			setChanged();
			notifyObservers();
		}
		
		@Override public boolean isConnected() { return true;	}
		@Override public void setConnected(boolean b) { System.err.println("Cannot change local player connection state"); }
	}

	ArrayList<AbstractPlayer> players;
	
	public Players() {
		players = new ArrayList<>();
	}
	
	public LocalPlayer getlocalPlayer() {
		return LocalPlayer.getInstance();
	}
	
	public ArrayList<AbstractPlayer> getPlayers() {
		return players;
	}
	
	public ArrayList<AbstractPlayer> getPlayersAndLocal() {
		ArrayList<AbstractPlayer> a = new ArrayList<>(players);
		a.add(LocalPlayer.getInstance());
		return a;
	}
	
	public void add(String name) {
		//Recherche si on a un joueur de ce nom là.
		//Si oui -> le mettre à connecté, il est revenu
		//Si non -> le créer et l'ajouter
		AbstractPlayer p;
		if((p = get(name)) != null) {
			p.setConnected(true);
		} else {
			p = new Player(name);
			players.add(p);
		}
		this.setChanged();
		this.notifyObservers(players);
	}
	
	public AbstractPlayer get(String name) {
		for(AbstractPlayer p : players) {
			if(p.name == name)
				return p;
		}
		return null;
	}
	
	public void remove(String name) {
		AbstractPlayer p = get(name);
		p.setConnected(false);
		setChanged();
		notifyObservers(players);
	}
	
	//Nouveau round
	public void resetRound() {
		for(AbstractPlayer p : players)
			p.nbCoups=Integer.MAX_VALUE;
		setChanged();
		notifyObservers(players);
	}
	
	//Nouveau jeu/session
	public void resetSession() {
		for(AbstractPlayer p : players) {
			p.score = 0;
			if(!p.isConnected()) {
				players.remove(p); //Cette fois, pas besoin de le garder pour son score, tout le monde est à zéro.
			}
		}
		setChanged();
		notifyObservers(players);
	}
	

}
