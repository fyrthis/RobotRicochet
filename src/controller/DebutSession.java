package controller;

import java.util.Observable;

import players.Players.LocalPlayer;
import utils.Phase;
import model.Model;

class DebutSession extends Observable {

	Model model;
	
	public DebutSession(Model model) {
		this.model = model;
	}

	//SESSION/plateau/
	//(S -> C) Plateau de la session courante et initialisation de la session pour le client.
	void session(String plateau) {
		session(plateau, 16, 16);
	}
	
	void session(String plateau, int sizeX, int sizeY) {
		model.getGrid().setGrid(model.getGridFromBuffer(sizeX, sizeY, plateau));
		model.getGrid().update();
		model.getGameState().setPhase(Phase.INITIALISATION);
	}
	
	//VAINQUEUR/bilan/
	//(S -> C) Fin de la session courante, scores finaux de la session
	void vainqueur(String bilan) {
		model.setBilanCurrentSession(bilan);
		
		// On peut considérer que le vainqueur de la derniere partie est le vainqueur de la session
		// puisqu'il faut atteindre un score objectif pour arrêter la session
		String winner = model.getGameState().getActivePlayer();
		if(winner.equals(LocalPlayer.getInstance().getName())){
			// On peut afficher un JDialog au gagnant pour lui signifier sa victoire
			
		}
		else {
			// On peut aussi afficher un JDialog aux perdants pour leur signier leur défaite
			
		}
	}

}
