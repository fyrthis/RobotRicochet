package controller;

import java.util.Observable;

import model.Model;
import model.Players;

class Resolution extends Observable  {

	private Players.Player activeUser;

	private Model model;
	
	public Resolution(Model model) {
		this.model = model;
	}

	//SASOLUTION/user/deplacements/
	//(S -> C) Signalement aux clients de la solution proposée
	void saSolution(String user, String deplacements) {
		activeUser = model.getPlayers().get(user);
		//TODO : Lancer l'animation des robots des cas déplacements.
		setChanged();
		notifyObservers();
	}
	
	//BONNE/
	//(S -> C) Solution acceptée (à tous les clients), fin du tour.
	void bonne() {
		
	}
	
	//MAUVAISE/user/
	//(S -> C) Solution refusée (à tous les clients), nouvelle phase de résolution, 'user' joueur actif.
	void mauvaise(String user) {
		
	}
	
	//FINRESO/
	//(S -> C) Plus de joueurs restants, fin du tour.
	void finReso() {
		
	}
	
	//TROPLONG/user/
	//(S -> C) Temps dépassé, nouvelle phase de résolution, 'user' joueur actif.
	void tropLong(String user) {
		
	}
}
