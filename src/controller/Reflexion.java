package controller;

import java.util.Observable;

import launcher.Debug;
import model.Model;
import players.Players.LocalPlayer;
import utils.Phase;

class Reflexion extends Observable {

	private Model model;

	public Reflexion(Model model) {
		this.model = model;
	}

	//TOUR/enigme/bilan/
	//(S -> C) Bilan de la session, description de l'énigme courante et initialisation de la phase de réflexion
	void tour(String enigme, String bilan) {
		LocalPlayer.getInstance().notProposedYet();
		model.getGameState().setPhase(Phase.REFLEXION);
		model.setRobotsFromBuffer(enigme);
		if(bilan != null && bilan.length()>0)
			model.setBilanCurrentSession(bilan);
		model.getGrid().update();
	}
	
	//TUASTROUVE/
	//(S -> C) Validation de l'annonce par le serveur, fin de la phase de réflexion
	void tuAsTrouve() {
		model.getGameState().addEnchere(LocalPlayer.getInstance().getName(), LocalPlayer.getInstance().getCoups());
		model.getPlayers().updatePlayersNbCoups(LocalPlayer.getInstance().getName(), LocalPlayer.getInstance().getCoups());
		model.getGameState().setPhase(Phase.ENCHERE);
	}
	
	//ILATROUVE/user/coups/
	//(S -> C) Signalement à un client que 'user' a annoncé une solution,  fin de la phase de réflexion
	void ilATrouve(String user, String coups) {
		// il faut aussi initialiser ses propres valeurs d'enchères
		LocalPlayer.getInstance().setNbCoups(-1);
		model.getGameState().addEnchere(user, Integer.valueOf(coups));
		model.getPlayers().updatePlayersNbCoups(user, Integer.valueOf(coups));
		model.getGameState().setPhase(Phase.ENCHERE);
	}
	
	//FINREFLEXION/
	//(S -> C) Expiration du délai imparti à la réflexion, fin de la phase de réflexion
	void finReflexion() {
		model.getGameState().setPhase(Phase.ENCHERE);
	}
}
