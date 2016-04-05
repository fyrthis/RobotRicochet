package model;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;

import launcher.Debug;
import utils.Phase;
import utils.Tools;

public class GameState extends Observable {

	//static final int TIME_WAITING_GAME = 35;
	static final int TIME_REFLEXION = 300;
	static final int TIME_ENCHERE = 30;
	static final int TIME_RESOLUTION = 60;

	static int tour;
	static String activePlayer;
	static String solutionMoves;
	static Phase phase;
	static int animationTime;
	
	static HashMap<String, Integer> encheres;
	
	public GameState(){
		setPhase(Phase.NOGAME);
		encheres = new HashMap<String, Integer>();
	}

	public int getTour(){ return tour; }
	public String getSolutionMoves(){ return solutionMoves; }
	public String getActivePlayer(){ return activePlayer; }
	public Phase getPhase(){ return phase; }
	public int getAnimationTime(){ return animationTime; }
	public HashMap<String, Integer> getEncheres(){ return encheres; }

	public void setTour(int t){ GameState.tour = t; }
	public void setActivePlayer(String s){ GameState.activePlayer = s; }
	public void setSolutionMoves(String s){ GameState.solutionMoves = s; }
	public void setAnimationTime(int time){ GameState.animationTime = time; }
	
	public void addEnchere(String name, Integer nbCoups){
		if(!encheres.containsKey(name)){
			encheres.put(name, nbCoups);
		}
		else {
			for(Entry<String, Integer> enchere : encheres.entrySet()){
				if(enchere.getKey().equals(name))
					enchere.setValue(nbCoups);
			}
		}
		Tools.sortByComparator(encheres);
	}
	
	public void setPhase(Phase p){
		System.out.println("(Client:"+Debug.curName+")(GameState:setPhase) setting phase to : "+p);
		GameState.phase = p;
		if(phase == Phase.INITIALISATION)
			Model.getInstance().getPlayers().resetRound();
		if(phase == Phase.REFLEXION)
			Timer.getInstance().setTime(TIME_REFLEXION);
		if(phase == Phase.ENCHERE)
			Timer.getInstance().setTime(TIME_ENCHERE);
		if(phase == Phase.RESOLUTION_ACTIVE || phase == Phase.RESOLUTION_PASSIVE)
			Timer.getInstance().setTime(TIME_RESOLUTION);
		
		System.out.println(" ======== " + p.toString() + " ======== ");

		this.setChanged();
		this.notifyObservers(GameState.phase);
	}

}
