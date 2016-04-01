package model;

import java.util.Observable;

public class Grid extends Observable {
	
	final int DELAY = 80;

	Integer[][][] grid;
	char target;

	Robot rouge;
	Robot bleu;
	Robot jaune;
	Robot vert;

	public Grid(){
		this.grid = new Integer[2][][];
		this.target = 'n';
	}

	public void setGrid(Integer[][] grid){ this.grid[0] = grid; }
	public void setSymbolGrid(Integer[][] symbolGrid){ this.grid[1] = symbolGrid; }
	public void setSymbol(int x, int y, int s){ this.grid[1][x][y] = s; }
	public void setTarget(char c){ this.target = c; }
	public void setRobot(Robot r){
		if(r.getColor() == 2)
			this.rouge = r.clone();
		if(r.getColor() == 3)
			this.bleu = r.clone();
		if(r.getColor() == 4)
			this.vert = r;
		if(r.getColor() == 5)
			this.jaune = r;
	}
	public void initializeRobot(char color, int x, int y){
		switch(color){
		case 'r':
			rouge = new Robot(x, y, 2);
			grid[1][x][y] = 21;
			break;
		case 'b':
			bleu = new Robot(x, y, 3);
			grid[1][x][y] = 31;
			break;
		case 'j':
			jaune = new Robot(x, y, 5);
			grid[1][x][y] = 51;
			break;
		case 'v':
			vert = new Robot(x, y, 4);
			grid[1][x][y] = 41;
			break;
		default:;
		}
	}

	public Grid clone(){
		Grid clone = new Grid();
		Integer[][][] matrixClone = new Integer[2][grid[0].length][grid[0][0].length];
		
		for(int j = 0; j < grid[0][0].length; j++){
			for(int i = 0; i < grid[0].length; i++){
				matrixClone[0][i][j] = new Integer(grid[0][i][j]);
			}
		}
		for(int j = 0; j < grid[1][0].length; j++){
			for(int i = 0; i < grid[1].length; i++){
				matrixClone[1][i][j] = new Integer(grid[1][i][j]);
			}
		}// 4 3 5 1 2 3 3
		clone.setGrid(matrixClone[0]);
		clone.setSymbolGrid(matrixClone[1]);
		clone.setTarget(this.target);
		clone.initializeRobot('r', new Integer(rouge.x), new Integer(rouge.y));
		clone.initializeRobot('b', new Integer(bleu.x), new Integer(bleu.y));
		clone.initializeRobot('j', new Integer(jaune.x), new Integer(jaune.y));
		clone.initializeRobot('v', new Integer(vert.x), new Integer(vert.y));
		
		return clone;
	}

	public Integer[][][] getGrid(){ return this.grid; }
	public int getSizeX(){ return this.grid[0].length; }
	public int getSizeY(){ return this.grid[0][0].length; }
	public char getTarget(){ return this.target; }
	
	public Robot getRedRobot(){ return this.rouge; }
	public Robot getBlueRobot(){ return this.bleu; }
	public Robot getYellowRobot(){ return this.jaune; }
	public Robot getGreenRobot(){ return this.vert; }

	public void update() {
		this.setChanged();
		this.notifyObservers(grid);
	}
	
	public void moveRobot(char color, char direction) throws InterruptedException {
		Robot robot = null;
		int val = 0;
		switch(color){
		case 'R':
			robot = rouge;
			val = 21;
			break;
		case 'B':
			robot = bleu;
			val = 31;
			break;
		case 'J':
			robot = jaune;
			val = 51;
			break;
		case 'V':
			robot = vert;
			val = 41;
			break;
		}
		
		robot.addPointToPath(robot.x, robot.y);
		switch(direction) {
		case 'H':
			// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
			while(!(grid[0][robot.x][robot.y] == 1 || grid[0][robot.x][robot.y] ==  3 || grid[0][robot.x][robot.y] == 5 || grid[0][robot.x][robot.y] == 7
			|| grid[0][robot.x][robot.y] == 9 || grid[0][robot.x][robot.y] == 11 || grid[0][robot.x][robot.y] == 13 || grid[0][robot.x][robot.y] == 15))
			{
				grid[1][robot.x][robot.y] = 0;
				if(robot.y > 0) {
					switch(color){
					case 'R':
						if((robot.x == bleu.x && robot.y-1 == bleu.y) || (robot.x == jaune.x && robot.y-1 == jaune.y) || (robot.x == vert.x && robot.y-1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+2;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x == rouge.x && robot.y-1 == rouge.y) || (robot.x == jaune.x && robot.y-1 == jaune.y) || (robot.x == vert.x && robot.y-1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+2;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x == bleu.x && robot.y-1 == bleu.y) || (robot.x == rouge.x && robot.y-1 == rouge.y) || (robot.x == vert.x && robot.y-1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+2;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x == bleu.x && robot.y-1 == bleu.y) || (robot.x == jaune.x && robot.y-1 == jaune.y) || (robot.x == rouge.x && robot.y-1 == rouge.y)) {
							grid[1][robot.x][robot.y] = val+2;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.y--;
					grid[1][robot.x][robot.y] = val+1;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(DELAY);
					update();
				}
			}
			grid[1][robot.x][robot.y] = val+2;
			//System.out.println("... Mur bloquant : - r - grid["+robot.x+"]["+robot.y+"] = "+grid[robot.x][robot.y]+"\n");
			break;
		case 'B':
			// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
			while(!(grid[0][robot.x][robot.y] == 4 || grid[0][robot.x][robot.y] ==  5 || grid[0][robot.x][robot.y] == 6 || grid[0][robot.x][robot.y] == 7
			|| grid[0][robot.x][robot.y] == 12 || grid[0][robot.x][robot.y] == 13 || grid[0][robot.x][robot.y] == 14 || grid[0][robot.x][robot.y] == 15))
			{
				grid[1][robot.x][robot.y] = 0;
				if(robot.y < getSizeY()-1){
					switch(color){
					case 'R':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+6;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+6;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+6;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == rouge.x && robot.y+1 == rouge.y)) {
							grid[1][robot.x][robot.y] = val+6;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.y++;
					grid[1][robot.x][robot.y] = val+5;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(DELAY);
					update();	
				}
			} 
			grid[1][robot.x][robot.y] = val+6;
			//System.out.println("... Mur bloquant : - " + val + " - grid["+x_r+"]["+y_r+"] = "+grid[robot.x][robot.y]+"\n");
			break;
		case 'G':
			// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
			while(!(grid[0][robot.x][robot.y] == 8 || grid[0][robot.x][robot.y] ==  9 || grid[0][robot.x][robot.y] == 10 || grid[0][robot.x][robot.y] == 11
			|| grid[0][robot.x][robot.y] == 12 || grid[0][robot.x][robot.y] == 13 || grid[0][robot.x][robot.y] == 14 || grid[0][robot.x][robot.y] == 15))
			{
				grid[1][robot.x][robot.y] = 0;
				if(robot.x > 0) {
					switch(color){
					case 'R':
						if((robot.x-1 == bleu.x && robot.y == bleu.y) || (robot.x-1 == jaune.x && robot.y == jaune.y) || (robot.x-1 == vert.x && robot.y == vert.y)) {
							grid[1][robot.x][robot.y] = val+8;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x-1 == rouge.x && robot.y == rouge.y) || (robot.x-1 == jaune.x && robot.y == jaune.y) || (robot.x-1 == vert.x && robot.y == vert.y)) {
							grid[1][robot.x][robot.y] = val+8;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x-1 == bleu.x && robot.y == bleu.y) || (robot.x-1 == rouge.x && robot.y == rouge.y) || (robot.x == vert.x-1 && robot.y == vert.y)) {
							grid[1][robot.x][robot.y] = val+8;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x-1 == bleu.x && robot.y == bleu.y) || (robot.x-1 == jaune.x && robot.y == jaune.y) || (robot.x-1 == rouge.x && robot.y == rouge.y)) {
							grid[1][robot.x][robot.y] = val+8;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.x--;
					grid[1][robot.x][robot.y] = val+7;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(DELAY);
					update();	
				}
			} 
			grid[1][robot.x][robot.y] = val+8;
			//System.out.println("... Mur bloquant : - " + val + " - grid["+x_r+"]["+y_r+"] = "+grid[robot.x][robot.y]+"\n");
			break;
		case 'D':
			// 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
			while(!(grid[0][robot.x][robot.y] == 2 || grid[0][robot.x][robot.y] ==  3 || grid[0][robot.x][robot.y] == 6 || grid[0][robot.x][robot.y] == 7
			|| grid[0][robot.x][robot.y] == 10 || grid[0][robot.x][robot.y] == 11 || grid[0][robot.x][robot.y] == 14 || grid[0][robot.x][robot.y] == 15))
			{
				grid[1][robot.x][robot.y] = 0;
				if(robot.x < getSizeX()-1){
					switch(color){
					case 'R':
						if((robot.x+1 == bleu.x && robot.y == bleu.y) || (robot.x+1 == jaune.x && robot.y == jaune.y) || (robot.x+1 == vert.x && robot.y == vert.y)) {
							grid[1][robot.x][robot.y] = val+4;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x+1 == rouge.x && robot.y == rouge.y) || (robot.x+1 == jaune.x && robot.y == jaune.y) || (robot.x+1 == vert.x && robot.y == vert.y)) {
							grid[1][robot.x][robot.y] = val+4;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x+1 == bleu.x && robot.y == bleu.y) || (robot.x+1 == rouge.x && robot.y == rouge.y) || (robot.x+1 == vert.x && robot.y == vert.y)) {
							grid[1][robot.x][robot.y] = val+4;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x+1 == bleu.x && robot.y == bleu.y) || (robot.x+1 == jaune.x && robot.y == jaune.y) || (robot.x+1 == rouge.x && robot.y == rouge.y)) {
							grid[1][robot.x][robot.y] = val+4;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.x++;
					grid[1][robot.x][robot.y] = val+3;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(DELAY);
					update();	
				}
			}
			grid[1][robot.x][robot.y] = val+4;
			//System.out.println("... Mur bloquant : - " + val + " - grid["+x_r+"]["+y_r+"] = "+grid[robot.x][robot.y]+"\n");
			break;
		default:;
		}
	}
}
