package controller;

import java.util.Observable;

import model.Model;

class DeconnexionConnexion extends Observable {

	private Model model;
	
	public DeconnexionConnexion(Model model) {
		this.model = model;
	}

	//BIENVENUE/user/
	//(S -> C) Validation de la connexion 'user'
	void bienvenue(String user) {
		model.getPlayers().getlocalPlayer().setName(user);
	}
	
	//CONNECTE/user/
	//(S -> C) Signalement de la connexion de 'user' aux autres clients.
	void connecte(String user) {
		model.playerConnected(user);
	}
	
	//DECONNEXION/user/
	//(S -> C) Signalement de la déconnexion de 'user' aux autres clients.
	void deconnexion(String user) {
		model.playerLeaved(user);
	}

}
