package controller;

import java.rmi.activation.Activatable;

import model.Model;
import model.Players;

class Resolution extends Controller  {

	private static Players.Player activeUser;
	private Resolution(Model model) { super(model);	}
	
	//SASOLUTION/user/deplacements/
	//(S -> C) Signalement aux clients de la solution proposée
	static void saSolution(String user, String deplacements) {
		activeUser = model.getPlayers().get(user);
	}
	
	//BONNE/
	//(S -> C) Solution acceptée (à tous les clients), fin du tour.
	static void bonne() {
		
	}
	
	//MAUVAISE/user/
	//(S -> C) Solution refusée (à tous les clients), nouvelle phase de résolution, 'user' joueur actif.
	static void mauvaise(String user) {
		
	}
	
	//FINRESO/
	//(S -> C) Plus de joueurs restants, fin du tour.
	static void finReso() {
		
	}
	
	//TROPLONG/user/
	//(S -> C) Temps dépassé, nouvelle phase de résolution, 'user' joueur actif.
	static void tropLong(String user) {
		
	}
}
