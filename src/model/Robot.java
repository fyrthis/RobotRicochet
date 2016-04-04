package model;

import java.awt.Point;
import java.util.ArrayList;

public class Robot {
	
	int x;
	int y;
	String moves;
	int color;
	
	ArrayList<Point> path;
	
	public Robot(int x, int y, int color){
		this.x = x;
		this.y = y;
		this.color = color;
		path = new ArrayList<Point>();
		addPointToPath(x,y);
	}
	
	public void setMoves(String moves) { this.moves = moves; }
	public void addPointToPath(int x, int y) { this.path.add(new Point(x, y)); }
	public void setX(int x){ this.x = x; }
	public void setY(int y){ this.y = y; }

	public int getX(){ return this.x; }
	public int getY(){ return this.y; }
	public int getColor(){ return this.color; }
	public String getMoves(){ return this.moves; }
	public ArrayList<Point> getPath(){ return this.path; }

	public Robot clone(){
		return new Robot(this.x, this.y, this.color);
	}
}
