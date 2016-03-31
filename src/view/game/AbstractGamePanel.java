package view.game;

import javax.swing.JPanel;

public abstract class AbstractGamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected abstract void initializeAllComponents();
	protected abstract void initializeObservers();
	
	public abstract void setNamePlayer(String name);
	
}
