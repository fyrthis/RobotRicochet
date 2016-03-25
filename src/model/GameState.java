package model;

import java.util.Observable;

import utils.Phase;

public class GameState extends Observable {
	
	static int tour;
	static int currentSolution;
	static Phase phase;
	
	public GameState(){
	}
	
	public void setTour(int t){ GameState.tour = t; }
	public void setCurrentSolution(int s){ GameState.currentSolution = s; }
	public void setPhase(Phase p){
		GameState.phase = p;
		this.setChanged();
		this.notifyObservers(phase);
	}

}
