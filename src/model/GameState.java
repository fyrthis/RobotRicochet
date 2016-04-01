package model;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import launcher.Debug;
import utils.Phase;
import utils.Tools;

public class GameState extends Observable implements Observer {

	//static final int TIME_WAITING_GAME = 35;
	static final int TIME_REFLEXION = 300;
	static final int TIME_ENCHERE = 30;
	static final int TIME_RESOLUTION = 60;

	static int tour;
	static int activeSolution;
	static String activePlayer;
	static String solutionMoves;
	static Phase phase;
	static double animationTime;
	
	static HashMap<String, Integer> encheres;
	
	public GameState(){
		setPhase(Phase.INITIALISATION);
		encheres = new HashMap<String, Integer>();
	}

	public int getActiveSolution(){ return activeSolution; }
	public String getActivePlayer(){ return activePlayer; }
	public int getTour(){ return tour; }
	public String getSolutionMoves(){ return solutionMoves; }
	public Phase getPhase(){ return phase; }
	public double getAnimationTime(){ return animationTime; }
	public HashMap<String, Integer> getEncheres(){ return encheres; }

	public void setTour(int t){ GameState.tour = t; }
	public void setSolutionMoves(String s){ GameState.solutionMoves =s; }
	public void setAnimationTime(double time){ GameState.animationTime = time; }
	public void setActiveSolution(int s){ GameState.activeSolution = s; }
	public void setActivePlayer(String name){ GameState.activePlayer = name; }
	
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
		
		for(Entry<String, Integer> enchere : encheres.entrySet()){
			if(enchere.getValue() < activeSolution){
				activeSolution = enchere.getValue();
				activePlayer = enchere.getKey();
			}
		}
	}
	
	public void setPhase(Phase p){
		System.out.println("(Client:"+Debug.curName+")(GameState:setPhase) setting phase to : "+p);
		GameState.phase = p;
		if(phase == Phase.REFLEXION)
			Timer.getInstance().setTime(TIME_REFLEXION);
		if(phase == Phase.ENCHERE)
			Timer.getInstance().setTime(TIME_ENCHERE);
		if(phase == Phase.RESOLUTION)
			Timer.getInstance().setTime(TIME_RESOLUTION);

		this.setChanged();
		this.notifyObservers(GameState.phase);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		int time = (int) arg;
		if(time == 0 && phase == Phase.INITIALISATION){
			Timer.getInstance().setTime(TIME_REFLEXION);
			setPhase(Phase.REFLEXION);
		}
		if(time == 0 && phase == Phase.REFLEXION){
			Timer.getInstance().setTime(TIME_ENCHERE);
			setPhase(Phase.ENCHERE);
		}
		if(time == 0 && phase == Phase.ENCHERE){
			Timer.getInstance().setTime(TIME_RESOLUTION);
			setPhase(Phase.RESOLUTION);
		}
		if(time == 0 && phase == Phase.RESOLUTION){
			Timer.getInstance().setTime(0);
			setPhase(Phase.INITIALISATION);
		}
	}

}
