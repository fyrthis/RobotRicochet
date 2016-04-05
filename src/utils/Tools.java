package utils;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Grid;
import model.Model;
import model.Robot;

public class Tools {

	final static int DELAY = 80;

	// ANIMATION SPRITES
	public static int computeAnimationTime(String moves){
		int moveTime = 0;
		Grid gridClone = Model.getInstance().getGrid().clone();
		Integer[][][] matrixClone = gridClone.getGrid().clone();
		
		for(int i = 0; i < moves.length(); i+=2){
			char color = moves.charAt(i);
			char direction = moves.charAt(i+1);

			int nbMoves = simulateMove(color, direction, matrixClone);
			System.out.println("Simulation : " + nbMoves);
			moveTime += 4*DELAY*nbMoves;
		}
		return moveTime;
	}

	public static int simulateMove(char color, char direction, Integer[][][] matrixClone) {
		int nbMoves = 0;
		int val = 0;
		Robot robot = null;
		Robot rouge = null;
		Robot bleu = null;
		Robot jaune = null;
		Robot vert = null;
		
		for(int i = 0; i < matrixClone[1].length; i++){
			for(int j = 0; j < matrixClone[1][0].length; j++){
				if(matrixClone[1][i][j] == 21)
					rouge = new Robot(i,j,Color.red);
				if(matrixClone[1][i][j] == 31)
					bleu = new Robot(i,j,Color.blue);
				if(matrixClone[1][i][j] == 41)
					vert = new Robot(i,j,Color.green);
				if(matrixClone[1][i][j] == 51)
					jaune = new Robot(i,j,Color.yellow);
			}
		}
		
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

		System.out.println("debut simulation moves : - "+color+" - ["+robot.getX()+","+robot.getY()+"]");
		
		switch(direction) {
		case 'H':
			// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
			while(!(matrixClone[0][robot.getX()][robot.getY()] == 1 || matrixClone[0][robot.getX()][robot.getY()] ==  3 || matrixClone[0][robot.getX()][robot.getY()] == 5 || matrixClone[0][robot.getX()][robot.getY()] == 7
			|| matrixClone[0][robot.getX()][robot.getY()] == 9 || matrixClone[0][robot.getX()][robot.getY()] == 11 || matrixClone[0][robot.getX()][robot.getY()] == 13 || matrixClone[0][robot.getX()][robot.getY()] == 15))
			{
				matrixClone[1][robot.getX()][robot.getY()] = 0;
				if(robot.getY() > 0) {
					System.out.println("\t- "+color+" - ["+robot.getX()+","+robot.getY()+"]");
					switch(color){
					case 'R':
						if((robot.getX() == bleu.getX() && robot.getY()-1 == bleu.getY()) || (robot.getX() == jaune.getX() && robot.getY()-1 == jaune.getY()) || (robot.getX() == vert.getX() && robot.getY()-1 == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX() == rouge.getX() && robot.getY()-1 == rouge.getY()) || (robot.getX() == jaune.getX() && robot.getY()-1 == jaune.getY()) || (robot.getX() == vert.getX() && robot.getY()-1 == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX() == bleu.getX() && robot.getY()-1 == bleu.getY()) || (robot.getX() == rouge.getX() && robot.getY()-1 == rouge.getY()) || (robot.getX() == vert.getX() && robot.getY()-1 == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX() == bleu.getX() && robot.getY()-1 == bleu.getY()) || (robot.getX() == jaune.getX() && robot.getY()-1 == jaune.getY()) || (robot.getX() == rouge.getX() && robot.getY()-1 == rouge.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setY(robot.getY()-1);
					matrixClone[1][robot.getX()][robot.getY()] = val;
				}
			}
			//System.out.println("... Mur bloquant : - r - grid["+robot.getX()+"]["+robot.getY()+"] = "+grid[robot.getX()][robot.getY()]+"\n");
			break;
		case 'B':
			// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
			while(!(matrixClone[0][robot.getX()][robot.getY()] == 4 || matrixClone[0][robot.getX()][robot.getY()] ==  5 || matrixClone[0][robot.getX()][robot.getY()] == 6 || matrixClone[0][robot.getX()][robot.getY()] == 7
			|| matrixClone[0][robot.getX()][robot.getY()] == 12 || matrixClone[0][robot.getX()][robot.getY()] == 13 || matrixClone[0][robot.getX()][robot.getY()] == 14 || matrixClone[0][robot.getX()][robot.getY()] == 15))
			{
				matrixClone[1][robot.getX()][robot.getY()] = 0;
				if(robot.getY() < matrixClone[0][0].length-1){
					switch(color){
					case 'R':
						if((robot.getX() == bleu.getX() && robot.getY()+1 == bleu.getY()) || (robot.getX() == jaune.getX() && robot.getY()+1 == jaune.getY()) || (robot.getX() == vert.getX() && robot.getY()+1 == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX() == rouge.getX() && robot.getY()+1 == rouge.getY()) || (robot.getX() == jaune.getX() && robot.getY()+1 == jaune.getY()) || (robot.getX() == vert.getX() && robot.getY()+1 == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX() == bleu.getX() && robot.getY()+1 == bleu.getY()) || (robot.getX() == rouge.getX() && robot.getY()+1 == rouge.getY()) || (robot.getX() == vert.getX() && robot.getY()+1 == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX() == bleu.getX() && robot.getY()+1 == bleu.getY()) || (robot.getX() == jaune.getX() && robot.getY()+1 == jaune.getY()) || (robot.getX() == rouge.getX() && robot.getY()+1 == rouge.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setY(robot.getY()+1);
					matrixClone[1][robot.getX()][robot.getY()] = val;
				}
			} 
			//System.out.println("... Mur bloquant : - " + val + " - grid["+x_r+"]["+y_r+"] = "+grid[robot.getX()][robot.getY()]+"\n");
			break;
		case 'G':
			// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
			while(!(matrixClone[0][robot.getX()][robot.getY()] == 8 || matrixClone[0][robot.getX()][robot.getY()] ==  9 || matrixClone[0][robot.getX()][robot.getY()] == 10 || matrixClone[0][robot.getX()][robot.getY()] == 11
			|| matrixClone[0][robot.getX()][robot.getY()] == 12 || matrixClone[0][robot.getX()][robot.getY()] == 13 || matrixClone[0][robot.getX()][robot.getY()] == 14 || matrixClone[0][robot.getX()][robot.getY()] == 15))
			{
				matrixClone[1][robot.getX()][robot.getY()] = 0;
				if(robot.getX() > 0) {
					switch(color){
					case 'R':
						if((robot.getX()-1 == bleu.getX() && robot.getY() == bleu.getY()) || (robot.getX()-1 == jaune.getX() && robot.getY() == jaune.getY()) || (robot.getX()-1 == vert.getX() && robot.getY() == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX()-1 == rouge.getX() && robot.getY() == rouge.getY()) || (robot.getX()-1 == jaune.getX() && robot.getY() == jaune.getY()) || (robot.getX()-1 == vert.getX() && robot.getY() == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX()-1 == bleu.getX() && robot.getY() == bleu.getY()) || (robot.getX()-1 == rouge.getX() && robot.getY() == rouge.getY()) || (robot.getX() == vert.getX()-1 && robot.getY() == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX()-1 == bleu.getX() && robot.getY() == bleu.getY()) || (robot.getX()-1 == jaune.getX() && robot.getY() == jaune.getY()) || (robot.getX()-1 == rouge.getX() && robot.getY() == rouge.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setX(robot.getX()-1);
					matrixClone[1][robot.getX()][robot.getY()] = val;
				}
			}
			break;
		case 'D':
			// 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
			while(!(matrixClone[0][robot.getX()][robot.getY()] == 2 || matrixClone[0][robot.getX()][robot.getY()] ==  3 || matrixClone[0][robot.getX()][robot.getY()] == 6 || matrixClone[0][robot.getX()][robot.getY()] == 7
			|| matrixClone[0][robot.getX()][robot.getY()] == 10 || matrixClone[0][robot.getX()][robot.getY()] == 11 || matrixClone[0][robot.getX()][robot.getY()] == 14 || matrixClone[0][robot.getX()][robot.getY()] == 15))
			{
				matrixClone[1][robot.getX()][robot.getY()] = 0;
				if(robot.getX() < matrixClone[0].length-1){
					switch(color){
					case 'R':
						if((robot.getX()+1 == bleu.getX() && robot.getY() == bleu.getY()) || (robot.getX()+1 == jaune.getX() && robot.getY() == jaune.getY()) || (robot.getX()+1 == vert.getX() && robot.getY() == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX()+1 == rouge.getX() && robot.getY() == rouge.getY()) || (robot.getX()+1 == jaune.getX() && robot.getY() == jaune.getY()) || (robot.getX()+1 == vert.getX() && robot.getY() == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX()+1 == bleu.getX() && robot.getY() == bleu.getY()) || (robot.getX()+1 == rouge.getX() && robot.getY() == rouge.getY()) || (robot.getX()+1 == vert.getX() && robot.getY() == vert.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX()+1 == bleu.getX() && robot.getY() == bleu.getY()) || (robot.getX()+1 == jaune.getX() && robot.getY() == jaune.getY()) || (robot.getX()+1 == rouge.getX() && robot.getY() == rouge.getY())) {
							matrixClone[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setX(robot.getX()+1);
					matrixClone[1][robot.getX()][robot.getY()] = val;
				}
			}
			break;
		default:;
		}
		System.out.println("====> nbMoves : "+nbMoves);
		return nbMoves;
	}
	
	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
}
