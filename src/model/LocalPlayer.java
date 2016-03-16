package model;

public class LocalPlayer {
	
	public static String name;
	public static int score;
	
	private LocalPlayer(){}
	
	public static void setName(String n){	name = n; }
	public static void addScore(int n){	score += n; }
	

}
