package controller;

import model.Model;

public class DeconnexionConnexion extends Controller {

	private DeconnexionConnexion(Model model) {	super(model); }
	
	//BIENVENUE/user/
	//(S -> C) Validation de la connexion 'user'
	static void bienvenue(String user) {
		
	}
	
	//CONNECTE/user/
	//(S -> C) Signalement de la connexion de 'user' aux autres clients.
	static void connecte(String user) {
		model.playerConnected(user);
	}
	
	//DECONNEXION/user/
	//(S -> C) Signalement de la déconnexion de 'user' aux autres clients.
	static void deconnexion(String user) {
		model.playerLeaved(user);
	}
	

}
