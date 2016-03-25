package model;

import java.util.Observable;

public class LocalPlayer extends Observable {
	
	public String name;
	public int score;
	
	private LocalPlayer(){}
	private static class LocalPlayerHolder
	{		
		private final static LocalPlayer instance = new LocalPlayer(); 
	}
	public static LocalPlayer getInstance()
	{
		return LocalPlayerHolder.instance;
	}
	
	public void setName(String n) {
		name = n;
		setChanged();
		notifyObservers();
	}
	
	public void addScore(int n) {
		score += n; 
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}
	

}
