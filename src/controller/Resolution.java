package controller;

import model.Model;

public class Resolution extends Controller  {

	private Resolution(Model model) { super(model);	}

	//SOLUTION/user/deplacements/
	//(C -> S) Envoi de la solution proposée par le joueur actif.
	static void solution(String user, String deplacements) {
		
	}
	
	//SASOLUTION/user/deplacements/
	//(S -> C) Signalement aux clients de la solution proposée
	static void saSolution(String user, String deplacements) {
		
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
