package controller;

import utils.Phase;

import java.util.Observable;

import launcher.Debug;
import model.Model;

class Reflexion extends Observable {

	private Model model;

	public Reflexion(Model model) {
		this.model = model;
	}

	//TOUR/enigme/bilan/
	//(S -> C) Bilan de la session, description de l'énigme courante et initialisation de la phase de réflexion
	void tour(String enigme, String bilan) {
		model.getGameState().setPhase(Phase.REFLEXION);
		model.setRobotsFromBuffer(enigme);
		if(bilan != null && bilan.length()>0)
			model.setBilanCurrentSession(bilan);
		model.getGrid().update();
	}
	
	//SOLUTION/user/coups/
	//(C -> S) Annonce d'une solution trouvée par 'user' en 'coups' déplacements
	void solution(String user, String coups) {
		
	}
	
	//TUASTROUVE/
	//(S -> C) Validation de l'annonce par le serveur, fin de la phase de réflexion
	void tuAsTrouve() {
		// si on passe dans cette fonction, alors c'est que c'est que le client concerné est le joueur actif :
		// le serveur a validé sa solution donc on peut simplement changer l'affichage de l'interactionPanel
		// en passant à la phase d'enchère
		// ( par opposition a la fonction ilATrouve qui sera recu par tous les autres clients )
		System.out.println("(Client:"+Debug.curName+")(Reflexion:tuAsTrouve) sending notifyObserver in tuAsTrouve function...");
		model.getGameState().setPhase(Phase.ENCHERE);
	}
	
	//ILATROUVE/user/coups/
	//(S -> C) Signalement à un client que 'user' a annoncé une solution,  fin de la phase de réflexion
	void ilATrouve(String user, String coups) {
		// si on passe dans cette fonction, alors c'est que le client concerné n'est pas le joueur qui a proposé
		// une solution : le serveur lui envoie donc les infos sur le contexte courant du jeu qu'il faut mettre à
		// jour
		// => normalement la solution courante à cet instant précis est toujours à -1
		int activePlayerSolution = Integer.valueOf(coups);
		model.getGameState().setCurrentSolution(activePlayerSolution);
		System.out.println("(Client:"+Debug.curName+")(Reflexion:ilATrouve)sending notifyObserver in ilATrouve function...");
		model.getGameState().setPhase(Phase.ENCHERE);
	}
	
	//FINREFLEXION/
	//(S -> C) Expiration du délai imparti à la réflexion, fin de la phase de réflexion
	void finReflexion() {
		
	}
}
