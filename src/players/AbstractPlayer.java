package players;

import java.util.Observable;

public abstract class AbstractPlayer extends Observable {

	protected String name;
	protected int score;
	protected int nbCoups;

	public String getName() { return name; }
	public int getScore() { return score; }
	public int getCoups() { return nbCoups; }
	public abstract boolean isConnected();
	public abstract void setConnected(boolean b);
	public void setScore(int score) { this.score = score; }
	public void setNbCoups(int nbCoups) { this.nbCoups = nbCoups; }

	public String toString(){
		return "\t" + name + " | score : " + score + " | nbCoups : " + nbCoups;
	}

}
