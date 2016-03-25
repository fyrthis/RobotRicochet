package launcher;

import java.util.Observable;
import java.util.Observer;

import model.Model;

public class Debug implements Observer {

	private Model model;
	public static String curName;
	
	private Debug() {}

	private static class DebugHolder
	{		
		private final static Debug instance = new Debug(); 
	}
	public static Debug get()
	{
		return DebugHolder.instance;
	}

	@Override
	public void update(Observable o, Object arg) {
		Debug.curName = model.getPlayers().getlocalPlayer().getName();
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
