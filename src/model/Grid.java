package model;

import java.util.Observable;

public class Grid extends Observable {
	
	int[][] grid;
	
	public void setGrid(int[][] grid){
		this.grid = grid;
	}
	
	public void update() {
		System.out.println(this.countObservers());
		this.setChanged();
		this.notifyObservers(grid);
		System.out.println("notifyObserver");
	}

}
