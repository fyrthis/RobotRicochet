package utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Model;
import model.Robot;

public class Tools {

	final static int DELAY = 80;

	// ANIMATION SPRITES
	public static int computeAnimationTime(String moves){
		int moveTime = 0;
		Integer[][][] gridClone = Model.getInstance().getGrid().getGrid().clone();

		for(int i = 0; i < moves.length(); i+=2){
			char color = moves.charAt(i);
			char direction = moves.charAt(i+1);

			int nbMoves = simulateMove(color, direction, gridClone);
			System.out.println("Simulation : " + nbMoves);
			moveTime += 4*DELAY*nbMoves;
		}
		return moveTime;
	}

	public static int simulateMove(char color, char direction, Integer[][][] gridSimulation) {
		Robot robot = null;
		int nbMoves = 0;
		int val = 0;

		Model model = Model.getInstance();

		switch(color){
		case 'R':
			robot = model.getGrid().getRedRobot().clone();
			val = 21;
			break;
		case 'B':
			robot = model.getGrid().getBlueRobot().clone();
			val = 31;
			break;
		case 'J':
			robot = model.getGrid().getYellowRobot().clone();
			val = 51;
			break;
		case 'V':
			robot = model.getGrid().getGreenRobot().clone();
			val = 41;
			break;
		default:;
		}

		switch(direction) {
		case 'H':
			// 1, 3, 5, 7, 9, 11, 13, 15 sont les valeurs des cases où il y a déjà un mur en haut
			while(!(gridSimulation[0][robot.getX()][robot.getY()] == 1 || gridSimulation[0][robot.getX()][robot.getY()] ==  3 || gridSimulation[0][robot.getX()][robot.getY()] == 5 || gridSimulation[0][robot.getX()][robot.getY()] == 7
			|| gridSimulation[0][robot.getX()][robot.getY()] == 9 || gridSimulation[0][robot.getX()][robot.getY()] == 11 || gridSimulation[0][robot.getX()][robot.getY()] == 13 || gridSimulation[0][robot.getX()][robot.getY()] == 15))
			{
				gridSimulation[1][robot.getX()][robot.getY()] = 0;
				if(robot.getY() > 0) {
					switch(color){
					case 'R':
						if((robot.getX() == model.getGrid().getBlueRobot().getX() && robot.getY()-1 == model.getGrid().getBlueRobot().getY()) || (robot.getX() == model.getGrid().getYellowRobot().getX() && robot.getY()-1 == model.getGrid().getYellowRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX() && robot.getY()-1 == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX() == model.getGrid().getRedRobot().getX() && robot.getY()-1 == model.getGrid().getRedRobot().getY()) || (robot.getX() == model.getGrid().getYellowRobot().getX() && robot.getY()-1 == model.getGrid().getYellowRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX() && robot.getY()-1 == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX() == model.getGrid().getBlueRobot().getX() && robot.getY()-1 == model.getGrid().getBlueRobot().getY()) || (robot.getX() == model.getGrid().getRedRobot().getX() && robot.getY()-1 == model.getGrid().getRedRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX() && robot.getY()-1 == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX() == model.getGrid().getBlueRobot().getX() && robot.getY()-1 == model.getGrid().getBlueRobot().getY()) || (robot.getX() == model.getGrid().getYellowRobot().getX() && robot.getY()-1 == model.getGrid().getYellowRobot().getY()) || (robot.getX() == model.getGrid().getRedRobot().getX() && robot.getY()-1 == model.getGrid().getRedRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setY(robot.getY()-1);
					gridSimulation[1][robot.getX()][robot.getY()] = val;
				}
			}
			//System.out.println("... Mur bloquant : - r - grid["+robot.getX()+"]["+robot.getY()+"] = "+grid[robot.getX()][robot.getY()]+"\n");
			break;
		case 'B':
			// 4, 5, 6, 7, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur en bas
			while(!(gridSimulation[0][robot.getX()][robot.getY()] == 4 || gridSimulation[0][robot.getX()][robot.getY()] ==  5 || gridSimulation[0][robot.getX()][robot.getY()] == 6 || gridSimulation[0][robot.getX()][robot.getY()] == 7
			|| gridSimulation[0][robot.getX()][robot.getY()] == 12 || gridSimulation[0][robot.getX()][robot.getY()] == 13 || gridSimulation[0][robot.getX()][robot.getY()] == 14 || gridSimulation[0][robot.getX()][robot.getY()] == 15))
			{
				gridSimulation[1][robot.getX()][robot.getY()] = 0;
				if(robot.getY() < gridSimulation[0][0].length-1){
					switch(color){
					case 'R':
						if((robot.getX() == model.getGrid().getBlueRobot().getX() && robot.getY()+1 == model.getGrid().getBlueRobot().getY()) || (robot.getX() == model.getGrid().getYellowRobot().getX() && robot.getY()+1 == model.getGrid().getYellowRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX() && robot.getY()+1 == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX() == model.getGrid().getRedRobot().getX() && robot.getY()+1 == model.getGrid().getRedRobot().getY()) || (robot.getX() == model.getGrid().getYellowRobot().getX() && robot.getY()+1 == model.getGrid().getYellowRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX() && robot.getY()+1 == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX() == model.getGrid().getBlueRobot().getX() && robot.getY()+1 == model.getGrid().getBlueRobot().getY()) || (robot.getX() == model.getGrid().getRedRobot().getX() && robot.getY()+1 == model.getGrid().getRedRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX() && robot.getY()+1 == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX() == model.getGrid().getBlueRobot().getX() && robot.getY()+1 == model.getGrid().getBlueRobot().getY()) || (robot.getX() == model.getGrid().getYellowRobot().getX() && robot.getY()+1 == model.getGrid().getYellowRobot().getY()) || (robot.getX() == model.getGrid().getRedRobot().getX() && robot.getY()+1 == model.getGrid().getRedRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setY(robot.getY()+1);
					gridSimulation[1][robot.getX()][robot.getY()] = val;
				}
			} 
			//System.out.println("... Mur bloquant : - " + val + " - grid["+x_r+"]["+y_r+"] = "+grid[robot.getX()][robot.getY()]+"\n");
			break;
		case 'G':
			// 8, 9, 10, 11, 12, 13, 14, 15 sont les valeurs des cases où il y a déjà un mur à gauche
			while(!(gridSimulation[0][robot.getX()][robot.getY()] == 8 || gridSimulation[0][robot.getX()][robot.getY()] ==  9 || gridSimulation[0][robot.getX()][robot.getY()] == 10 || gridSimulation[0][robot.getX()][robot.getY()] == 11
			|| gridSimulation[0][robot.getX()][robot.getY()] == 12 || gridSimulation[0][robot.getX()][robot.getY()] == 13 || gridSimulation[0][robot.getX()][robot.getY()] == 14 || gridSimulation[0][robot.getX()][robot.getY()] == 15))
			{
				gridSimulation[1][robot.getX()][robot.getY()] = 0;
				if(robot.getX() > 0) {
					switch(color){
					case 'R':
						if((robot.getX()-1 == model.getGrid().getBlueRobot().getX() && robot.getY() == model.getGrid().getBlueRobot().getY()) || (robot.getX()-1 == model.getGrid().getYellowRobot().getX() && robot.getY() == model.getGrid().getYellowRobot().getY()) || (robot.getX()-1 == model.getGrid().getGreenRobot().getX() && robot.getY() == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX()-1 == model.getGrid().getRedRobot().getX() && robot.getY() == model.getGrid().getRedRobot().getY()) || (robot.getX()-1 == model.getGrid().getYellowRobot().getX() && robot.getY() == model.getGrid().getYellowRobot().getY()) || (robot.getX()-1 == model.getGrid().getGreenRobot().getX() && robot.getY() == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX()-1 == model.getGrid().getBlueRobot().getX() && robot.getY() == model.getGrid().getBlueRobot().getY()) || (robot.getX()-1 == model.getGrid().getRedRobot().getX() && robot.getY() == model.getGrid().getRedRobot().getY()) || (robot.getX() == model.getGrid().getGreenRobot().getX()-1 && robot.getY() == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX()-1 == model.getGrid().getBlueRobot().getX() && robot.getY() == model.getGrid().getBlueRobot().getY()) || (robot.getX()-1 == model.getGrid().getYellowRobot().getX() && robot.getY() == model.getGrid().getYellowRobot().getY()) || (robot.getX()-1 == model.getGrid().getRedRobot().getX() && robot.getY() == model.getGrid().getRedRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setX(robot.getX()-1);
					gridSimulation[1][robot.getX()][robot.getY()] = val;
				}
			}
			break;
		case 'D':
			// 2, 3, 6, 7, 10, 11, 14, 15 son les valeurs des cases où il y a déjà un mur à droite
			while(!(gridSimulation[0][robot.getX()][robot.getY()] == 2 || gridSimulation[0][robot.getX()][robot.getY()] ==  3 || gridSimulation[0][robot.getX()][robot.getY()] == 6 || gridSimulation[0][robot.getX()][robot.getY()] == 7
			|| gridSimulation[0][robot.getX()][robot.getY()] == 10 || gridSimulation[0][robot.getX()][robot.getY()] == 11 || gridSimulation[0][robot.getX()][robot.getY()] == 14 || gridSimulation[0][robot.getX()][robot.getY()] == 15))
			{
				gridSimulation[1][robot.getX()][robot.getY()] = 0;
				if(robot.getX() < gridSimulation[0].length-1){
					switch(color){
					case 'R':
						if((robot.getX()+1 == model.getGrid().getBlueRobot().getX() && robot.getY() == model.getGrid().getBlueRobot().getY()) || (robot.getX()+1 == model.getGrid().getYellowRobot().getX() && robot.getY() == model.getGrid().getYellowRobot().getY()) || (robot.getX()+1 == model.getGrid().getGreenRobot().getX() && robot.getY() == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'B':
						if((robot.getX()+1 == model.getGrid().getRedRobot().getX() && robot.getY() == model.getGrid().getRedRobot().getY()) || (robot.getX()+1 == model.getGrid().getYellowRobot().getX() && robot.getY() == model.getGrid().getYellowRobot().getY()) || (robot.getX()+1 == model.getGrid().getGreenRobot().getX() && robot.getY() == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'J':
						if((robot.getX()+1 == model.getGrid().getBlueRobot().getX() && robot.getY() == model.getGrid().getBlueRobot().getY()) || (robot.getX()+1 == model.getGrid().getRedRobot().getX() && robot.getY() == model.getGrid().getRedRobot().getY()) || (robot.getX()+1 == model.getGrid().getGreenRobot().getX() && robot.getY() == model.getGrid().getGreenRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					case 'V':
						if((robot.getX()+1 == model.getGrid().getBlueRobot().getX() && robot.getY() == model.getGrid().getBlueRobot().getY()) || (robot.getX()+1 == model.getGrid().getYellowRobot().getX() && robot.getY() == model.getGrid().getYellowRobot().getY()) || (robot.getX()+1 == model.getGrid().getRedRobot().getX() && robot.getY() == model.getGrid().getRedRobot().getY())) {
							gridSimulation[1][robot.getX()][robot.getY()] = val;
							return nbMoves;
						}
						break;
					}
					nbMoves++;
					robot.setX(robot.getX()+1);
					gridSimulation[1][robot.getX()][robot.getY()] = val;
				}
			}
			break;
		default:;
		}
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
