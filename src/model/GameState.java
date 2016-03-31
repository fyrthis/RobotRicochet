package model;

import java.util.Observable;

import launcher.Debug;
import utils.Phase;

public class GameState extends Observable {
	
	static final int TIME_WAITING_GAME = 10;
	static final int TIME_REFLEXION = 300;
	static final int TIME_ENCHERE = 30;
	static final int TIME_RESOLUTION = 60;
	
	static int tour;
	static int currentSolution;
	static String solutionMoves;
	static Phase phase;
	static int time;
	
	public GameState(){
	}
	
	public int getCurrentSolution(){ return currentSolution; }
	public int getTout(){ return tour; }
	public String getSolutionMoves(){ return solutionMoves; }
	public Phase getPhase(){ return phase; }
	
	public void setTour(int t){ GameState.tour = t; }
	public void setCurrentSolution(int s){ GameState.currentSolution = s; }
	public void setSolutionMoves(String s){ GameState.solutionMoves =s; }
	public void setPhase(Phase p){
		System.out.println("(Client:"+Debug.curName+")(GameState:setPhase) setting phase to : "+p);
		GameState.phase = p;
		
		if(p == Phase.INITIALISATION)
			time = TIME_WAITING_GAME;
		if(p == Phase.REFLEXION)
			time = TIME_REFLEXION;
		if(p == Phase.ENCHERE)
			time = TIME_ENCHERE;
		if(p == Phase.RESOLUTION)
			time = TIME_RESOLUTION;
		
		this.setChanged();
		this.notifyObservers(phase);
	}

}
