package controller;

import java.util.Observable;

import players.Players.LocalPlayer;
import model.Model;
import utils.Phase;

class Enchere extends Observable {


	private Model model;

	public Enchere(Model model) {
		this.model = model;
	}

	//VALIDATION/
	//(S -> C) Validation de l'enchère
	void validation() {
		model.getGameState().addEnchere(LocalPlayer.getInstance().getName(), LocalPlayer.getInstance().getCoups());
		model.getPlayers().updatePlayersNbCoups(LocalPlayer.getInstance().getName(), LocalPlayer.getInstance().getCoups());
	}
	
	//ECHEC/user/
	//(S -> C) Annulation de l'enchère car incohérente avec celle de 'user'
	void echec(String user) {
		
	}
	
	//NOUVELLEENCHERE/user/coups/
	//(S ->C) Signalement à un client d'une enchère.
	void nouvelleEnchere(String user, String coups) {
		model.getGameState().addEnchere(user, Integer.valueOf(coups));
		model.getPlayers().updatePlayersNbCoups(user, Integer.valueOf(coups));
	}
	
	//FINENCHERE/user/coups/
	//(S -> C) Fin des enchères, le joueur actif est user.
	void finEnchere(String user, String coups) {
		model.getGameState().setActivePlayer(user);
		if(LocalPlayer.getInstance().getName().equals(user)){
			LocalPlayer.getInstance().isProposing();
			model.getGameState().setPhase(Phase.RESOLUTION_ACTIVE);
		}
		else
			model.getGameState().setPhase(Phase.RESOLUTION_PASSIVE);
	}
}
