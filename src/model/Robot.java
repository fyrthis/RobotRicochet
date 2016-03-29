package model;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Robot {
	
	int x;
	int y;
	String moves;
	ArrayList<Line2D> path;
	
	public Robot(int x, int y){
		this.x = x;
		this.y = y;
		path = new ArrayList<Line2D>();
	}
	
	public void setXY(int x, int y){
		addPath(new Line2D.Double(new Point(this.x, this.y), new Point(x, y)));
		this.x = x;
		this.y = y; 
	}
	public void setMoves(String moves) { this.moves = moves; }
	public void addPath(Line2D line) { this.path.add(line); }
	
	public int getX(){ return this.x; }
	public int getY(){ return this.y; }
	public String getMoves(){ return this.moves; }
	public ArrayList<Line2D> getPath(){ return this.path; }

}
