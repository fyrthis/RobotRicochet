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
			grid[1][x_r][y_r] = 21;
			break;
		case 'b':
			x_b = x;
			y_b = y;
			grid[1][x_b][y_b] = 31;
			break;
		case 'j':
			x_j = x;
			y_j = y;
			grid[1][x_j][y_j] = 51;
			break;
		case 'v':
			x_v = x;
			y_v = y;
			grid[1][x_v][y_v] = 41;
			break;
		default:;
		}
	}


	public Integer[][][] getGrid(){ return this.grid; }
	public int getSizeX(){ return this.grid[0].length; }
	public int getSizeY(){ return this.grid[0][0].length; }
	public char getTarget(){ return this.target; }

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
				// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
				if(grid[0][x_r][y_r] == 1 || grid[0][x_r][y_r] ==  3 || grid[0][x_r][y_r] == 5 || grid[0][x_r][y_r] == 7
				|| grid[0][x_r][y_r] == 9 || grid[0][x_r][y_r] == 11 || grid[0][x_r][y_r] == 13 || grid[0][x_r][y_r] == 15){
					System.out.println("Case interdite : - r - grid["+x_r+"]["+y_r+"] = "+grid[x_r][y_r]+"\n");
					return;
				}
				tmp_x = x_r;
				for(tmp_y = y_r-1; tmp_y >= 0; tmp_y--) {
					System.out.println("\t testing ["+tmp_x+","+tmp_y+"]...");
					if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						y_r = tmp_y+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 1 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 15) {
						y_r = tmp_y;
						break;
					}
				}
				break;
			case 'B':
				// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
				if(grid[0][x_r][y_r] == 4 || grid[0][x_r][y_r] ==  5 || grid[0][x_r][y_r] == 6 || grid[0][x_r][y_r] == 7
				|| grid[0][x_r][y_r] == 12 || grid[0][x_r][y_r] == 13 || grid[0][x_r][y_r] == 14 || grid[0][x_r][y_r] == 15)
				{
					System.out.println("Case interdite : - r - grid["+x_r+"]["+y_r+"] = "+grid[x_r][y_r]+"\n");
					return;
				}                    
				tmp_x = x_r;
				// on teste toutes les cases à partir de la case en dessous du robot
				for(tmp_y = y_r+1; tmp_y < getSizeY(); tmp_y++) {
					if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						y_r = tmp_y-1;
					}
					else if(grid[0][tmp_x][tmp_y] == 4 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						y_r = tmp_y;
						break;
					}
				}
				break;
			case 'G':
				// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
				if(grid[0][x_r][y_r] == 8 || grid[0][x_r][y_r] ==  9 || grid[0][x_r][y_r] == 10 || grid[0][x_r][y_r] == 11
				|| grid[0][x_r][y_r] == 12 || grid[0][x_r][y_r] == 13 || grid[0][x_r][y_r] == 14 || grid[0][x_r][y_r] == 15)
				{
					System.out.println("Case interdite : - r - grid["+x_r+"]["+y_r+"] = "+grid[x_r][y_r]+"\n");
					return;
				}          
				tmp_y = y_r;
				// on teste toutes les cases à partir de la case à gauche du robot
				for(tmp_x = x_r-1; tmp_x >= 0; tmp_x--) {
					if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						x_r = tmp_x+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 8 || grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						x_r = tmp_x;
						break;
					}
				}
				break;
			case 'D':
				// 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
				if(grid[0][x_r][y_r] == 2 || grid[0][x_r][y_r] ==  3 || grid[0][x_r][y_r] == 6 || grid[0][x_r][y_r] == 7
				|| grid[0][x_r][y_r] == 10 || grid[0][x_r][y_r] == 11 || grid[0][x_r][y_r] == 14 || grid[0][x_r][y_r] == 15)
				{
					System.out.println("Case interdite : - r - grid["+x_r+"]["+y_r+"] = "+grid[x_r][y_r]+"\n");
					return;
				}          
				tmp_y = y_r;
				// on teste toutes les cases à partir de la case à droite du robot
				for(tmp_x = x_r+1; tmp_x < getSizeX(); tmp_x++) {
					if((tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						x_r = tmp_x-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 2 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
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
				// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
				if(grid[0][x_b][y_b] == 1 || grid[0][x_b][y_b] ==  3 || grid[0][x_b][y_b] == 5 || grid[0][x_b][y_b] == 7
				|| grid[0][x_b][y_b] == 9 || grid[0][x_b][y_b] == 11 || grid[0][x_b][y_b] == 13 || grid[0][x_b][y_b] == 15)
				{
					System.out.println("Case interdite : - b - grid["+x_b+"]["+y_b+"] = "+grid[x_b][y_b]+"\n");
					return;
				}         
				tmp_x = x_b;
				// on teste toutes les cases à partir de la case au dessus du robot
				for(tmp_y = y_b-1; tmp_y >= 0; tmp_y--) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						y_b = tmp_y+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 1 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 15) {
						y_b = tmp_y;
						break;
					}
				}
				break;
			case 'B':
				// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
				if(grid[0][x_b][y_b] == 4 || grid[0][x_b][y_b] ==  5 || grid[0][x_b][y_b] == 6 || grid[0][x_b][y_b] == 7
				|| grid[0][x_b][y_b] == 12 || grid[0][x_b][y_b] == 13 || grid[0][x_b][y_b] == 14 || grid[0][x_b][y_b] == 15)
				{
					System.out.println("Case interdite : - b - grid["+x_b+"]["+y_b+"] = "+grid[x_b][y_b]+"\n");
					return;
				}
				tmp_x = x_b;
				// on teste toutes les cases à partir de la case en dessous du robot
				for(tmp_y = y_b+1; tmp_y < getSizeY(); tmp_y++) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						y_b = tmp_y-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 4 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						y_b = tmp_y;
						break;
					}
				}
				break;
			case 'G':
				// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
				if(grid[0][x_b][y_b] == 8 || grid[0][x_b][y_b] ==  9 || grid[0][x_b][y_b] == 10 || grid[0][x_b][y_b] == 11
				|| grid[0][x_b][y_b] == 12 || grid[0][x_b][y_b] == 13 || grid[0][x_b][y_b] == 14 || grid[0][x_b][y_b] == 15)
				{
					System.out.println("Case interdite : - b - grid["+x_b+"]["+y_b+"] = "+grid[x_b][y_b]+"\n");
					return;
				}
				tmp_y = y_b;
				// on teste toutes les cases à partir de la case à gauche du robot
				for(tmp_x = x_b-1; tmp_x >= 0; tmp_x--) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						x_b = tmp_x+1;
					}
					else if(grid[0][tmp_x][tmp_y] == 8 || grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						x_b = tmp_x;
						break;
					}
				}
				break;
			case 'D':
				// 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
				if(grid[0][x_b][y_b] == 2 || grid[0][x_b][y_b] ==  3 || grid[0][x_b][y_b] == 6 || grid[0][x_b][y_b] == 7
				|| grid[0][x_b][y_b] == 10 || grid[0][x_b][y_b] == 11 || grid[0][x_b][y_b] == 14 || grid[0][x_b][y_b] == 15)
				{
					System.out.println("Case interdite : - b - grid["+x_b+"]["+y_b+"] = "+grid[x_b][y_b]+"\n");
					return;
				}
				tmp_y = y_b;
				// on teste toutes les cases à partir de la case à droite du robot
				for(tmp_x = x_b+1; tmp_x < getSizeX(); tmp_x++) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_j && tmp_y == y_j) || (tmp_x == x_v && tmp_y == y_v)) {
						x_b = tmp_x-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 2 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
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
				// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
				if(grid[0][x_j][y_j] == 1 || grid[0][x_j][y_j] ==  3 || grid[0][x_j][y_j] == 5 || grid[0][x_j][y_j] == 7
				|| grid[0][x_j][y_j] == 9 || grid[0][x_j][y_j] == 11 || grid[0][x_j][y_j] == 13 || grid[0][x_j][y_j] == 15)
				{
					System.out.println("Case interdite : - j - grid["+x_j+"]["+y_j+"] = "+grid[x_j][y_j]+"\n");
					return;
				}
				tmp_x = x_j;
				// on teste toutes les cases à partir de la case au dessus du robot
				for(tmp_y = y_j-1; tmp_y >= 0; tmp_y--) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
						y_j = tmp_y+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 1 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 15) {
						y_j = tmp_y;
						break;
					}
				}
				break;
			case 'B':
				// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
				if(grid[0][x_j][y_j] == 4 || grid[0][x_j][y_j] ==  5 || grid[0][x_j][y_j] == 6 || grid[0][x_j][y_j] == 7
				|| grid[0][x_j][y_j] == 12 || grid[0][x_j][y_j] == 13 || grid[0][x_j][y_j] == 14 || grid[0][x_j][y_j] == 15)
				{
					System.out.println("Case interdite : - j - grid["+x_j+"]["+y_j+"] = "+grid[x_j][y_j]+"\n");
					return;
				}
				tmp_x = x_j;
				// on teste toutes les cases à partir de la case en dessous du robot
				for(tmp_y = y_j+1; tmp_y < getSizeY(); tmp_y++) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
						y_j = tmp_y-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 4 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						y_j = tmp_y;
						break;
					}
				}
				break;
			case 'G':
				// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
				if(grid[0][x_j][y_j] == 8 || grid[0][x_j][y_j] ==  9 || grid[0][x_j][y_j] == 10 || grid[0][x_j][y_j] == 11
				|| grid[0][x_j][y_j] == 12 || grid[0][x_j][y_j] == 13 || grid[0][x_j][y_j] == 14 || grid[0][x_j][y_j] == 15)
				{
					System.out.println("Case interdite : - j - grid["+x_j+"]["+y_j+"] = "+grid[x_j][y_j]+"\n");
					return;
				}
				tmp_y = y_j;
				// on teste toutes les cases à partir de la case à gauche du robot
				for(tmp_x = x_j-1; tmp_x >= 0; tmp_x--) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
						x_j = tmp_x+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 8 || grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						x_j = tmp_x;
						break;
					}
				}
				break;
			case 'D':
				// 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
				if(grid[0][x_j][y_j] == 2 || grid[0][x_j][y_j] ==  3 || grid[0][x_j][y_j] == 6 || grid[0][x_j][y_j] == 7
				|| grid[0][x_j][y_j] == 10 || grid[0][x_j][y_j] == 11 || grid[0][x_j][y_j] == 14 || grid[0][x_j][y_j] == 15)
				{
					System.out.println("Case interdite : - j - grid["+x_j+"]["+y_j+"] = "+grid[x_j][y_j]+"\n");
					return;
				}
				tmp_y = y_j;
				// on teste toutes les cases à partir de la case à droite du robot
				for(tmp_x = x_j+1; tmp_x < getSizeX(); tmp_x++) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_v && tmp_y == y_v)) {
						x_j = tmp_x-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 2 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
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
				// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
				if(grid[0][x_v][y_v] == 1 || grid[0][x_v][y_v] ==  3 || grid[0][x_v][y_v] == 5 || grid[0][x_v][y_v] == 7
				|| grid[0][x_v][y_v] == 9 || grid[0][x_v][y_v] == 11 || grid[0][x_v][y_v] == 13 || grid[0][x_v][y_v] == 15)
				{
					System.out.println("Case interdite : - v - grid["+x_v+"]["+y_v+"] = "+grid[x_v][y_v]+"\n");
					return;
				}
				tmp_x = x_v;
				// on teste toutes les cases à partir de la case au dessus du robot
				for(tmp_y = y_v-1; tmp_y >= 0; tmp_y--) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
						y_v = tmp_y+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 1 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 15) {
						y_v = tmp_y;
						break;
					}
				}
				break;
			case 'B':
				// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
				if(grid[0][x_v][y_v] == 4 || grid[0][x_v][y_v] ==  5 || grid[0][x_v][y_v] == 6 || grid[0][x_v][y_v] == 7
				|| grid[0][x_v][y_v] == 12 || grid[0][x_v][y_v] == 13 || grid[0][x_v][y_v] == 14 || grid[0][x_v][y_v] == 15)
				{
					System.out.println("Case interdite : - v - grid["+x_v+"]["+y_v+"] = "+grid[x_v][y_v]+"\n");
					return;
				}
				tmp_x = x_v;
				// on teste toutes les cases à partir de la case en dessous du robot
				for(tmp_y = y_v+1; tmp_y < getSizeY(); tmp_y++) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
						y_v = tmp_y-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 4 || grid[0][tmp_x][tmp_y] == 5 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						y_v = tmp_y;
						break;
					}
				}
				break;
			case 'G':
				// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
				if(grid[0][x_v][y_v] == 8 || grid[0][x_v][y_v] ==  9 || grid[0][x_v][y_v] == 10 || grid[0][x_v][y_v] == 11
				|| grid[0][x_v][y_v] == 12 || grid[0][x_v][y_v] == 13 || grid[0][x_v][y_v] == 14 || grid[0][x_v][y_v] == 15)
				{
					System.out.println("Case interdite : - v - grid["+x_v+"]["+y_v+"] = "+grid[x_v][y_v]+"\n");
					return;
				}
				tmp_y = y_v;
				// on teste toutes les cases à partir de la case à gauche du robot
				for(tmp_x = x_v-1; tmp_x >= 0; tmp_x--) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
						x_v = tmp_x+1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 8 || grid[0][tmp_x][tmp_y] == 9 || grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11
							|| grid[0][tmp_x][tmp_y] == 12 || grid[0][tmp_x][tmp_y] == 13 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
						x_v = tmp_x;
						break;
					}
				}
				break;
			case 'D':
				// 2, 3, 6, 7, 10, 11, 14, 15 sont les valeurs des cases où il y a déjà un mur à droite
				if(grid[0][x_v][y_v] == 2 || grid[0][x_v][y_v] ==  3 || grid[0][x_v][y_v] == 6 || grid[0][x_v][y_v] == 7
				|| grid[0][x_v][y_v] == 10 || grid[0][x_v][y_v] == 11 || grid[0][x_v][y_v] == 14 || grid[0][x_v][y_v] == 15)
				{
					System.out.println("Case interdite : - v - grid["+x_v+"]["+y_v+"] = "+grid[x_v][y_v]+"\n");
					return;
				}
				tmp_y = y_v;
				// on teste toutes les cases à partir de la case à droite du robot
				for(tmp_x = x_v+1; tmp_x < getSizeX(); tmp_x++) {
					if((tmp_x == x_r && tmp_y == y_r) || (tmp_x == x_b && tmp_y == y_b) || (tmp_x == x_j && tmp_y == y_j)) {
						x_v = tmp_x-1;
						break;
					}
					else if(grid[0][tmp_x][tmp_y] == 2 || grid[0][tmp_x][tmp_y] == 3 || grid[0][tmp_x][tmp_y] == 6 || grid[0][tmp_x][tmp_y] == 7
							|| grid[0][tmp_x][tmp_y] == 10 || grid[0][tmp_x][tmp_y] == 11 || grid[0][tmp_x][tmp_y] == 14 || grid[0][tmp_x][tmp_y] == 15) {
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

		grid[1][initial_x][initial_y] = 0;
		System.out.println("END OF MOVEROBOT");
	}


}
