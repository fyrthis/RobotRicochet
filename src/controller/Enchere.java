package controller;

import model.Model;

class Enchere extends Controller {

	private Enchere(Model model) { super(model); }
	
	//VALIDATION/
	//(S -> C) Validation de l'enchère
	static void validation() {
		/**
		 * Validation enchère :
		 *  maj meilleure enchère
		 *  maj tableaux des joueurs
		 *  
		 */
		
	}
	
	//ECHEC/user/
	//(S -> C) Annulation de l'enchère car incohérente avec celle de 'user'
	static void echec(String user) {
		/**
		 * rien à faire, on refuse l'enchère
		 */
		
	}
	
	//NOUVELLEENCHERE/user/coups/
	//(S ->C) Signalement à un client d'une enchère.
	static void nouvelleEnchere(String user, String coups) {
		/**
		 * Une nouvelle enchère a été trouvé par un tiers 
		 * maj meilleure enchère
		 * maj tableaux des joueurs
		 */
	}
	
	//FINENCHERE/user/coups/
	//(S -> C) Fin des enchères, le joueur actif est user.
	static void finEnchere(String user, String coups) {
		/**
		 * fin des enchères
		 * maj panel intéraction, désactivé si on n'est pas le joueur actif.
		 * maj compteur
		 */
	}
}
