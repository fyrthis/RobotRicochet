package controller;

import java.util.Observable;

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
	}
	
	//VAINQUEUR/bilan/
	//(S -> C) Fin de la session courante, scores finaux de la session
	void vainqueur(String bilan) {
		
	}

}
