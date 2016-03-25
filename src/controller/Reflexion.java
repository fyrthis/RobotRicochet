package controller;

import model.Model;

class Reflexion extends Controller {

	private Reflexion(Model model) { super(model);	}

	//TOUR/enigme/bilan/
	//(S -> C) Bilan de la session, description de l'énigme courante et initialisation de la phase de réflexion
	static void tour(String enigme, String bilan) {
		model.setRobotsFromBuffer(enigme);
		if(bilan != null || bilan.length()>0)
			model.setBilanCurrentSession(bilan);
		model.getGrid().update();

	}
	
	//SOLUTION/user/coups/
	//(C -> S) Annonce d'une solution trouvée par 'user' en 'coups' déplacements
	static void solution(String user, String coups) {
		
	}
	
	//TUASTROUVE/
	//(S -> C) Validation de l'annonce par le serveur, fin de la phase de réflexion
	static void tuAsTrouve() {
		
	}
	
	//ILATROUVE/user/coups/
	//(S -> C) Signalement à un client que 'user' a annoncé une solution,  fin de la phase de réflexion
	static void ilATrouve(String user, String coups) {
		
	}
	
	//FINREFLEXION/
	//(S -> C) Expiration du délai imparti à la réflexion, fin de la phase de réflexion
	static void finReflexion() {
		
	}
}
