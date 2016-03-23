package model;

import java.util.Observable;

public class Grid extends Observable {
	
	Integer[][][] grid;
	char target;
	
	public Grid(){
		this.grid = new Integer[2][][];
		this.target = 'n';
	}
	
	public void setGrid(Integer[][] grid){ this.grid[0] = grid; }
	public void setSymbolGrid(Integer[][] symbolGrid){ this.grid[1] = symbolGrid; }
	public void setSymbol(int x, int y, int s){ this.grid[1][x][y] = s; }
	public void setTarget(char c){ this.target = c; }
	
	public Integer[][][] getGrid(){ return this.grid; }
	public int getSizeX(){ return this.grid[0].length; }
	public int getSizeY(){ return this.grid[0][0].length; }
	
	public void update() {
		this.setChanged();
		this.notifyObservers(grid);
	}

}
