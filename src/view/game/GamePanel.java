package view.game;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import utils.Phase;
import model.LocalPlayer;
import model.Model;

public class GamePanel extends JPanel {
	
	private static final long serialVersionUID = -2942785356832417025L;
	//TOP
	TimerPanel timer;
	StatePanel state;
	PlayerNamePanel playerName;
	
	//MIDDLE
	RoundBoardPanel round;
	GridPanel grid;
	ScoreBoardPanel score;
	
	//BOTTOM
	InteractionPanel interaction;
	
	Model model;
	
	public GamePanel(Model model) {
		this.model = model;
		initializeAllComponents();
		initializeObservers();
		
	}


	private void initializeObservers() {
		model.getPlayers().addObserver(round);
		model.getPlayers().addObserver(score);
		model.getGrid().addObserver(grid);
		model.getGameState().addObserver(interaction);
		System.out.println("initializeObservers Done");
		
	}


	private void initializeAllComponents() {
		// Note : Need to set preferred size of component otherwise conflict occur with grid constraints
		timer = new TimerPanel();
		timer.setPreferredSize(new Dimension(0, 0));
		state = new StatePanel();
		state.setPreferredSize(new Dimension(0, 0));
		playerName = new PlayerNamePanel(LocalPlayer.getInstance().getName());
		playerName.setPreferredSize(new Dimension(0, 0));
		round = new RoundBoardPanel();
		round.setPreferredSize(new Dimension(0, 0));
		grid = new GridPanel();
		grid.setPreferredSize(new Dimension(0, 0));
		score = new ScoreBoardPanel();
		score.setPreferredSize(new Dimension(0, 0));
		interaction = new InteractionPanel();
		interaction.setPanelByPhase(Phase.INITIALISATION);
		interaction.setPreferredSize(new Dimension(0, 0));
		
		//LAYOUT
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//TOP
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=15;
		c.weighty=15;
		c.gridwidth = 15;
		c.gridheight = 15;

		this.add(timer, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 16;
		c.gridy = 0;
		c.weightx=60;
		c.weighty=15;
		c.gridwidth = 60;
		c.gridheight = 15;
		this.add(state, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 77;
		c.gridy = 0;
		c.weightx=15;
		c.weighty=15;
		c.gridwidth = 15;
		c.gridheight = 15;
		this.add(playerName, c);
		
		//MIDDLE
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 21;
		c.weightx=15;
		c.weighty=60;
		c.gridwidth = 15;
		c.gridheight = 60;
		this.add(round, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 16;
		c.gridy = 21;
		c.weightx=60;
		c.weighty=60;
		c.gridwidth = 60;
		c.gridheight = 60;
		this.add(grid, c);
		//System.out.println(grid.getSize());
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 77;
		c.gridy = 16;
		c.weightx=15;
		c.weighty=84;
		c.gridwidth = 15;
		c.gridheight = 84;

		this.add(score, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 81;
		c.weightx=85;
		c.weighty=20;
		c.gridwidth = 85;
		c.gridheight = 20;

		this.add(interaction, c);
	}


	public void setNamePlayer(String name) {
		playerName.setNamePlayer(name);
	}

}
