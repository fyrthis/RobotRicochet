package controller;

import model.Model;

public class Enchere extends Controller {

	private Enchere(Model model) { super(model);	}

	//ENCHERE/user/coups/
	//(C -> S) Enchère d'une solution trouvée par 'user' en 'coups' déplacements.
	static void enchere(String user, String coups) {
		
	}
	
	//VALIDATION/
	//(S -> C) Validation de l'enchère
	static void validation() {
		
	}
	
	//ECHEC/user/
	//(S -> C) Annulation de l'enchère car incohérente avec celle de 'user'
	static void echec(String user) {
		
	}
	
	//NOUVELLEENCHERE/user/coups/
	//(S ->C) Signalement à un client d'une enchère.
	static void nouvelleEnchere(String user, String coups) {
		
	}
	
	//FINENCHERE/user/coups/
	//(S -> C) Fin des enchères, le joueur actif est user.
	static void finEnchere(String user, String coups) {
		
	}
}
