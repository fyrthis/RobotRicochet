package controller;

import java.util.Observable;

import utils.Phase;
import model.Model;

class Enchere extends Observable {


	private Model model;

	public Enchere(Model model) {
		this.model = model;
	}

	//ENCHERE/user/coups/
	//(C -> S) Enchère d'une solution trouvée par 'user' en 'coups' déplacements.
	void enchere(String user, String coups) {
		
	}
	
	//VALIDATION/
	//(S -> C) Validation de l'enchère
	void validation() {
		// Permet de changer de la phase d'enchere a la phase de résolution
		// MAIS NE RESPECTE ABSOLUMENT PAS LE PROTOCOLE DONC A CHANGER DES QUE POSSIBLE
		model.getGameState().setPhase(Phase.RESOLUTION);
	}
	
	//ECHEC/user/
	//(S -> C) Annulation de l'enchère car incohérente avec celle de 'user'
	void echec(String user) {
		
	}
	
	//NOUVELLEENCHERE/user/coups/
	//(S ->C) Signalement à un client d'une enchère.
	void nouvelleEnchere(String user, String coups) {
		
	}
	
	//FINENCHERE/user/coups/
	//(S -> C) Fin des enchères, le joueur actif est user.
	void finEnchere(String user, String coups) {
		
	}
}
