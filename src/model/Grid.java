package model;

import java.util.Observable;

public class Grid extends Observable {
	
	Integer[][][] grid;
	char target;
	
	int x_r, y_r;
	int x_b, y_b;
	int x_j, y_j;
	int x_v, y_v;
	
	public Grid(){
		this.grid = new Integer[2][][];
		this.target = 'n';
		x_r = y_r = x_b = y_b = x_j = y_j = x_v = y_v = -1;
	}
	
	public void setGrid(Integer[][] grid){ this.grid[0] = grid; }
	public void setSymbolGrid(Integer[][] symbolGrid){ this.grid[1] = symbolGrid; }
	public void setSymbol(int x, int y, int s){ this.grid[1][x][y] = s; }
	public void setTarget(char c){ this.target = c; }
	public void setRobot(char color, int x, int y){
		if(!(x_r == -1 || y_r == -1 || x_b == -1 || y_b == -1 || x_j == -1 || y_j == -1 || x_v == -1 || y_v == -1)){
			switch(color){
			case 'R':
				grid[1][x_r][y_r] = null;
				break;
			case 'B':
				grid[1][x_b][y_b] = null;
				break;
			case 'J':
				grid[1][x_j][y_j] = null;
				break;
			case 'V':
				grid[1][x_v][y_v] = null;
				break;
			default:;
			}
		}
		switch(color){
		case 'R':
			x_r = x;
			y_r = y;
			break;
		case 'B':
			x_b = x;
			y_b = y;
			break;
		case 'J':
			x_j = x;
			y_j = y;
			break;
		case 'V':
			x_v = x;
			y_v = y;
			break;
		default:;
		}
	}
	
	
	public Integer[][][] getGrid(){ return this.grid; }
	public int getSizeX(){ return this.grid[0].length; }
	public int getSizeY(){ return this.grid[0][0].length; }
	
	public void update() {
		this.setChanged();
		this.notifyObservers(grid);
	}
	
	
	public void moveRobot(char color, char direction) {
		int initial_x = -1;
		int initial_y = -1;
	    int tmp_x = -1;
	    int tmp_y = -1;
	    switch(color){
	        case 'R':
            	initial_x = x_r;
            	initial_y = y_r;
	            switch(direction) {
	                case 'H':
	                    tmp_x = x_r;
	                    for(tmp_y = y_r; tmp_y >= 0; tmp_y--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_r = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'B':
	                    tmp_x = x_r;
	                    for(tmp_y = y_r; tmp_y < grid[0][0].length; tmp_y++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_r = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'G':
	                    tmp_y = y_r;
	                    for(tmp_x = x_r; tmp_x >= 0; tmp_x--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_r = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                case 'D':
	                    tmp_y = y_r;
	                    for(tmp_x = x_r; tmp_x < grid[0].length; tmp_x++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_r = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                default:;
	            }
	    	    grid[1][x_r][y_r] = 21;
	            break;
	        case 'B':
            	initial_x = x_b;
            	initial_y = y_b;
	            switch(direction) {
	                case 'H':
	                    tmp_x = x_b;
	                    for(tmp_y = y_b; tmp_y >= 0; tmp_y--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_b = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'B':
	                    tmp_x = x_b;
	                    for(tmp_y = y_b; tmp_y < grid[0][0].length; tmp_y++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_b = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'G':
	                    tmp_y = y_b;
	                    for(tmp_x = x_b; tmp_x >= 0; tmp_x--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_b = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                case 'D':
	                    tmp_y = y_b;
	                    for(tmp_x = x_b; tmp_x < grid[0].length; tmp_x++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_b = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                default:;
	            }
	    	    grid[1][x_b][y_b] = 31;
	            break;
	        case 'J':
            	initial_x = x_j;
            	initial_y = y_j;
	            switch(direction) {
	                case 'H':
	                    tmp_x = x_j;
	                    for(tmp_y = y_j; tmp_y >= 0; tmp_y--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_j = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'B':
	                    tmp_x = x_j;
	                    for(tmp_y = y_j; tmp_y < grid[0][0].length; tmp_y++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_j = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'G':
	                    tmp_y = y_j;
	                    for(tmp_x = x_j; tmp_x >= 0; tmp_x--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_j = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                case 'D':
	                    tmp_y = y_j;
	                    for(tmp_x = x_j; tmp_x < grid[0].length; tmp_x++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_j = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                default:;
	            }
	    	    grid[1][x_j][y_j] = 51;
	            break;
	        case 'V':
            	initial_x = x_v;
            	initial_y = y_v;
	            switch(direction) {
	                case 'H':
	                    tmp_x = x_v;
	                    for(tmp_y = y_v; tmp_y >= 0; tmp_y--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_v = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'B':
	                    tmp_x = x_v;
	                    for(tmp_y = y_v; tmp_y < grid[0][0].length; tmp_y++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            y_v = tmp_y;
	                            break;
	                        }
	                    }
	                    break;
	                case 'G':
	                    tmp_y = y_v;
	                    for(tmp_x = x_v; tmp_x >= 0; tmp_x--) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_v = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                case 'D':
	                    tmp_y = y_v;
	                    for(tmp_x = x_v; tmp_x < grid[0].length; tmp_x++) {
	                        if(grid[0][tmp_x][tmp_y] != 0) {
	                            x_v = tmp_x;
	                            break;
	                        }
	                    }
	                    break;
	                default:;
	            }
	    	    grid[1][x_v][y_v] = 41;
	            break;
	        default:;
	    }
	    grid[1][initial_x][initial_y] = null;
	}
	

}
