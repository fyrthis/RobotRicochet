package model;

import java.util.Observable;

import launcher.Debug;
import utils.Phase;

public class GameState extends Observable {

	//static final int TIME_WAITING_GAME = 35;
	static final int TIME_REFLEXION = 300;
	static final int TIME_ENCHERE = 30;
	static final int TIME_RESOLUTION = 60;

	static int tour;
	static int currentSolution;
	static String solutionMoves;
	static Phase phase;
	static int time;
	
	//static Timer timer;
	
	public GameState(){
		setPhase(Phase.INITIALISATION);
		
		// Thread qui va s'occuper de gérer le temps global pour tous les clients
		Thread t1 = new Thread(){
			public void run(){
				for(int i = 1;i <= 5000;i++) {
					updateTime();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 

			}
		};
		t1.start();
		

		/*
		int delay = 1000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//...Perform a task...
				updateTime();
			}
		};
		timer = new Timer(delay, taskPerformer);
		timer.start();*/
		
	}

	public int getCurrentSolution(){ return currentSolution; }
	public int getTour(){ return tour; }
	public int getTime(){ return time; }
	public String getSolutionMoves(){ return solutionMoves; }
	public Phase getPhase(){ return phase; }

	public void setTour(int t){ GameState.tour = t; }
	public void setCurrentSolution(int s){ GameState.currentSolution = s; }
	public void setSolutionMoves(String s){ GameState.solutionMoves =s; }
	
	public void setPhase(Phase p){
		System.out.println("(Client:"+Debug.curName+")(GameState:setPhase) setting phase to : "+p);
		GameState.phase = p;
		if(p == Phase.REFLEXION)
			setTime(TIME_REFLEXION);
		if(p == Phase.ENCHERE)
			setTime(TIME_ENCHERE);
		if(p == Phase.RESOLUTION)
			setTime(TIME_RESOLUTION);
	}
	
	// A chaque fois qu'on change la valeur du compteur time, on notify à tous les observers le nouveau gameState
	public void setTime(int t){
		GameState.time = t;
		this.setChanged();
		this.notifyObservers(this);
	}

	public void updateTime(){
		setTime(time-1);
	}

}
