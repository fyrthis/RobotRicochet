package controller;

import java.util.Observable;

import players.Players.LocalPlayer;
import model.Model;
import utils.Phase;
import utils.Tools;

class Resolution extends Observable  {
	
	final double DELAY = 80;

	private Model model;
	
	public Resolution(Model model) {
		this.model = model;
	}

	//SASOLUTION/user/deplacements/
	//(S -> C) Signalement aux clients de la solution proposée
	void saSolution(String user, String deplacements) {
		model.getGameState().setSolutionMoves(deplacements);
	}
	
	//BONNE/
	//(S -> C) Solution acceptée (à tous les clients), fin du tour.
	void bonne() {
		//Update le score : Comme on est dans la phase finale, on sait que quelqu'un a trouvé la solution
		String moves = model.getGameState().getSolutionMoves();
		
		for(int i = 0; i < moves.length(); i+=2){
			char color = moves.charAt(i);
			char direction = moves.charAt(i+1);
			
			try {
				model.getGrid().moveRobot(color, direction);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//MAUVAISE/user/
	//(S -> C) Solution refusée (à tous les clients), nouvelle phase de résolution, 'user' joueur actif.
	void mauvaise(String user) {
		// Le client est le joueur actif : il est autorisé à envoyer sa réponse
		if(LocalPlayer.getInstance().getName().equals(user)){
			LocalPlayer.getInstance().isProposing();
			model.getGameState().setPhase(Phase.RESOLUTION_ACTIVE);
		}
		else
			model.getGameState().setPhase(Phase.RESOLUTION_PASSIVE);
	}
	
	//FINRESO/
	//(S -> C) Plus de joueurs restants, fin du tour.
	void finReso() {
		//Mettre GameState à aucun jeu.
		model.getGameState().setPhase(Phase.INITIALISATION);
	}
	
	//TROPLONG/user/
	//(S -> C) Temps dépassé, nouvelle phase de résolution, 'user' joueur actif.
	void tropLong(String user) {
		//idem que mauvaise
		mauvaise(user);
	}
	
}
