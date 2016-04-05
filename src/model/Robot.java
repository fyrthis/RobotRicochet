package model;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Robot {
	
	int x;
	int y;
	String moves;
	Color color;
	
	ArrayList<Point> path;
	
	public Robot(int x, int y, Color color){
		this.x = x;
		this.y = y;
		this.color = color;
		path = new ArrayList<Point>();
		addPointToPath(x,y);
	}
	
	public void setMoves(String moves) { this.moves = moves; }
	public void addPointToPath(int x, int y) {
		System.out.println("["+x+","+y+"]");
		this.path.add(new Point(x, y)); 
	}
	public void setX(int x){ this.x = x; }
	public void setY(int y){ this.y = y; }

	public int getX(){ return this.x; }
	public int getY(){ return this.y; }
	public Color getColor(){ return this.color; }
	public String getMoves(){ return this.moves; }
	public ArrayList<Point> getPath(){ return this.path; }

	@Override
	public Robot clone(){
		return new Robot(this.x, this.y, this.color);
	}
}
