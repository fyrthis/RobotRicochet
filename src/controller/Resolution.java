package controller;

import java.util.Observable;

import launcher.Debug;
import model.Model;
import utils.Phase;

class Resolution extends Observable  {

	private Model model;
	
	public Resolution(Model model) {
		this.model = model;
	}

	//SASOLUTION/user/deplacements/
	//(S -> C) Signalement aux clients de la solution proposée
	void saSolution(String user, String deplacements) {
		model.getPlayers().get(user);
		model.getGameState().setSolutionMoves(deplacements);
		//TODO : Lancer l'animation des robots des cas déplacements.
		setChanged();
		notifyObservers(deplacements);
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
			model.getGrid().update();
		}
	}
	
	//MAUVAISE/user/
	//(S -> C) Solution refusée (à tous les clients), nouvelle phase de résolution, 'user' joueur actif.
	void mauvaise(String user) {
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
			model.getGrid().update();
		}
		model.getPlayers().get(user);
	}
	
	//FINRESO/
	//(S -> C) Plus de joueurs restants, fin du tour.
	void finReso() {
		//Mettre GameState à aucun jeu.
		model.getGameState().setPhase(Phase.NOGAME);
	}
	
	//TROPLONG/user/
	//(S -> C) Temps dépassé, nouvelle phase de résolution, 'user' joueur actif.
	void tropLong(String user) {
		//idem que mauvaise
	}
}
