package controller;

import model.Model;

class DebutSession extends Controller {

	private DebutSession(Model model) { super(model); }
	
	//SESSION/plateau/
	//(S -> C) Plateau de la session courante et initialisation de la session pour le client.
	static void session(String plateau) {
		/**
		 * Le client reçoit le plateau donc :
		 *  - mise à jour de la grille
		 */
		session(plateau, 16, 16);
	}
	
	static void session(String plateau, int sizeX, int sizeY) {
		model.getGrid().setGrid(model.getGridFromBuffer(sizeX, sizeY, plateau));
		model.getGrid().update();
	}
	
	//VAINQUEUR/bilan/
	//(S -> C) Fin de la session courante, scores finaux de la session
	static void vainqueur(String bilan) {
		/**
		 * Fin de la SESSION donc :
		 * - JDialog qui affiche le 10 premiers par exemple
		 * - Compteur avant la prochaine partie (tant qu'il reste au moins 2 joueurs)
		 */
	}

}
