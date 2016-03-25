package controller;

import utils.Phase;
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
		// si on passe dans cette fonction, alors c'est que c'est que le client concerné est le joueur actif :
		// le serveur a validé sa solution donc on peut simplement changer l'affichage de l'interactionPanel
		// en passant à la phase d'enchère
		// ( par opposition a la fonction ilATrouve qui sera recu par tous les autres clients )
		System.out.println("sending notifyObserver in tuAsTrouve function...");
		model.getGameState().setPhase(Phase.ENCHERE);
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
		// si on passe dans cette fonction, alors c'est que le client concerné n'est pas le joueur qui a proposé
		// une solution : le serveur lui envoie donc les infos sur le contexte courant du jeu qu'il faut mettre à
		// jour
		// => normalement la solution courante à cet instant précis est toujours à -1
		int activePlayerSolution = Integer.valueOf(coups);
		model.getGameState().setCurrentSolution(activePlayerSolution);
		System.out.println("sending notifyObserver in ilATrouve function...");
		model.getGameState().setPhase(Phase.ENCHERE);
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
