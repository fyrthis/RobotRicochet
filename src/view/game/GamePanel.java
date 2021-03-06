package view.game;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import model.Model;
import model.Timer;
import utils.Phase;

public class GamePanel extends AbstractGamePanel {
	
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
	ChatPanel chat;
	
	Model model;
	
	public GamePanel(Model model) {
		this.model = model;
		initializeAllComponents();
		initializeObservers();
	}


	public void initializeObservers() {
		model.getPlayers().addObserver(round);
		model.getPlayers().addObserver(score);
		model.getGrid().addObserver(grid);
		model.getGameState().addObserver(interaction);
		model.getGameState().addObserver(state);
		Timer.getInstance().addObserver(timer);
		model.getChatModel().addObserver(chat);
	}


	public void initializeAllComponents() {
		// Note : Need to set preferred size of component otherwise conflict occur with grid constraints
		timer = new TimerPanel(model);
		timer.setPreferredSize(new Dimension(0, 0));
		state = new StatePanel(model);
		state.setPreferredSize(new Dimension(0, 0));
		playerName = new PlayerNamePanel();
		playerName.setPreferredSize(new Dimension(0, 0));
		round = new RoundBoardPanel(model);
		round.setPreferredSize(new Dimension(0, 0));
		grid = new GridPanel(model);
		grid.setPreferredSize(new Dimension(0, 0));
		score = new ScoreBoardPanel(model);
		score.setPreferredSize(new Dimension(0, 0));
		interaction = new InteractionPanel(model);
		interaction.setPanelByPhase(Phase.NOGAME);
		interaction.setPreferredSize(new Dimension(0, 0));
		chat = new ChatPanel(model.getChatModel());
		
		//LAYOUT
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//TOP
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=25;
		c.weighty=10;
		c.gridwidth = 25;
		c.gridheight = 10;

		this.add(timer, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 26;
		c.gridy = 0;
		c.weightx=60;
		c.weighty=10;
		c.gridwidth = 60;
		c.gridheight = 10;
		this.add(state, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 87;
		c.gridy = 0;
		c.weightx=25;
		c.weighty=10;
		c.gridwidth = 25;
		c.gridheight = 10;
		this.add(playerName, c);
		
		//MIDDLE
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 11;
		c.weightx=25;
		c.weighty=80;
		c.gridwidth = 25;
		c.gridheight = 80;
		this.add(round, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 26;
		c.gridy = 11;
		c.weightx=60;
		c.weighty=80;
		c.gridwidth = 60;
		c.gridheight = 80;
		this.add(grid, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 87;
		c.gridy = 11;
		c.weightx=25;
		c.weighty=80;
		c.gridwidth = 25;
		c.gridheight = 80;

		this.add(score, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 91;
		c.weightx=112;
		c.weighty=11;
		c.gridwidth = 112;
		c.gridheight = 11;

		this.add(interaction, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 103;
		c.weightx=112;
		c.weighty=20;
		c.gridwidth = 112;
		c.gridheight = 20;
		
		this.add(chat, c);
	}


	public void setNamePlayer(String name) {
		playerName.setNamePlayer(name);
		chat.setNamePlayer(name);
	}

}
