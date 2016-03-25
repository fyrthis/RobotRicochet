package controller;

import model.Model;

class Reflexion extends Controller {

	private Reflexion(Model model) { super(model);	}

	//TOUR/enigme/bilan/
	//(S -> C) Bilan de la session, description de l'énigme courante et initialisation de la phase de réflexion
	static void tour(String enigme, String bilan) {
		/**
		 * On reçoit un nouvelle énigme et un bilan donc :
		 * - mise a jour de tous les scores
		 * - mise à jour du plateau
		 * - Est-ce qu'on annonce le vainqueur ?
		 * 
		 */
		model.setRobotsFromBuffer(enigme);
		if(bilan != null || bilan.length()>0)
			model.setBilanCurrentSession(bilan);
		model.getGrid().update();

	}
	
	
	//TUASTROUVE/
	//(S -> C) Validation de l'annonce par le serveur, fin de la phase de réflexion
	static void tuAsTrouve() {
		/**
		 * Mise a jour du nombre de coup à battre
		 * Passage en mode Enchere
		 * Maj du pnael intéraction
		 * Maj compteur
		 */
	}
	
	//ILATROUVE/user/coups/
	//(S -> C) Signalement à un client que 'user' a annoncé une solution,  fin de la phase de réflexion
	static void ilATrouve(String user, String coups) {
		/**
		 * Majdu nombre de coups à battre
		 * Passage en mode enchère
		 * Maj du panel intéraction
		 * Maj compteur
		 */
		
	}
	
	//FINREFLEXION/
	//(S -> C) Expiration du délai imparti à la réflexion, fin de la phase de réflexion
	static void finReflexion() {
		/**
		 * Délai imparti fini
		 * Comment on gère l'enchère ? Notamment si personne ne propose de solution d'ici à la fin ?
		 * passage en mode enchère
		 * maj compteur
		 * maj intéraction panel
		 */
	}
}
