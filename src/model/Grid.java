package model;

import java.awt.Point;
import java.util.Observable;

public class Grid extends Observable {

	Integer[][][] grid;
	char target;

	int x_r, y_r;
	int x_b, y_b;
	int x_j, y_j;
	int x_v, y_v;
	
	Robot rouge;
	Robot bleu;
	Robot jaune;
	Robot vert;

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
			case 'r':
				grid[1][x_r][y_r] = null;
				break;
			case 'b':
				grid[1][x_b][y_b] = null;
				break;
			case 'j':
				grid[1][x_j][y_j] = null;
				break;
			case 'v':
				grid[1][x_v][y_v] = null;
				break;
			default:;
			}
		}
		switch(color){
		case 'r':
			x_r = x;
			y_r = y;
			rouge = new Robot(x_r, y_r);
			grid[1][x_r][y_r] = 21;
			break;
		case 'b':
			x_b = x;
			y_b = y;
			bleu = new Robot(x_b, y_b);
			grid[1][x_b][y_b] = 31;
			break;
		case 'j':
			x_j = x;
			y_j = y;
			jaune = new Robot(x_j, y_j);
			grid[1][x_j][y_j] = 51;
			break;
		case 'v':
			x_v = x;
			y_v = y;
			vert = new Robot(x_v, y_v);
			grid[1][x_v][y_v] = 41;
			break;
		default:;
		}
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
		
		robot.addPoint(new Point(robot.x, robot.y));
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
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+1;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+1;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+1;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == rouge.x && robot.y+1 == rouge.y)) {
							grid[1][robot.x][robot.y] = val+1;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.y--;
					grid[1][robot.x][robot.y] = val+1;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(100);
					update();
				}
			}
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
							grid[1][robot.x][robot.y] = val+5;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+5;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+5;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == rouge.x && robot.y+1 == rouge.y)) {
							grid[1][robot.x][robot.y] = val+5;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.y++;
					grid[1][robot.x][robot.y] = val+5;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(100);
					update();	
				}
			} 
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
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+7;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+7;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+7;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == rouge.x && robot.y+1 == rouge.y)) {
							grid[1][robot.x][robot.y] = val+7;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.x--;
					grid[1][robot.x][robot.y] = val+7;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(100);
					update();	
				}
			} 
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
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+3;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'B':
						if((robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+3;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'J':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == rouge.x && robot.y+1 == rouge.y) || (robot.x == vert.x && robot.y+1 == vert.y)) {
							grid[1][robot.x][robot.y] = val+3;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					case 'V':
						if((robot.x == bleu.x && robot.y+1 == bleu.y) || (robot.x == jaune.x && robot.y+1 == jaune.y) || (robot.x == rouge.x && robot.y+1 == rouge.y)) {
							grid[1][robot.x][robot.y] = val+3;
							System.out.println("\t- " + color + "- bloqué par un robot en bas");
							return;
						}
						break;
					}
					robot.x++;
					grid[1][robot.x][robot.y] = val+3;
				}
				for(int i_update = 0; i_update < 4; i_update++){
					Thread.sleep(100);
					update();	
				}
			}
			//System.out.println("... Mur bloquant : - " + val + " - grid["+x_r+"]["+y_r+"] = "+grid[robot.x][robot.y]+"\n");
			break;
		default:;
		}
	}
}
