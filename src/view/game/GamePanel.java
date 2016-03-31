package view.game;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import launcher.Debug;
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
		System.out.println("(Client:"+Debug.curName+")(GamePanel:initializeObservers) initializeObservers Done");
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
		interaction.setPanelByPhase(Phase.INITIALISATION);
		interaction.setPreferredSize(new Dimension(0, 0));
		chat = new ChatPanel(model.getChatModel());
		
		//LAYOUT
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//TOP
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx=30;
		c.weighty=15;
		c.gridwidth = 30;
		c.gridheight = 15;

		this.add(timer, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 31;
		c.gridy = 0;
		c.weightx=60;
		c.weighty=15;
		c.gridwidth = 60;
		c.gridheight = 15;
		this.add(state, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 91;
		c.gridy = 0;
		c.weightx=15;
		c.weighty=15;
		c.gridwidth = 15;
		c.gridheight = 15;
		this.add(playerName, c);
		
		//MIDDLE
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 16;
		c.weightx=30;
		c.weighty=60;
		c.gridwidth = 30;
		c.gridheight = 60;
		this.add(round, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 31;
		c.gridy = 16;
		c.weightx=60;
		c.weighty=60;
		c.gridwidth = 60;
		c.gridheight = 60;
		this.add(grid, c);
		//System.out.println(grid.getSize());
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 91;
		c.gridy = 16;
		c.weightx=15;
		c.weighty=60;
		c.gridwidth = 15;
		c.gridheight = 60;

		this.add(score, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 81;
		c.weightx=30;
		c.weighty=20;
		c.gridwidth = 30;
		c.gridheight = 20;

		this.add(interaction, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 31;
		c.gridy = 81;
		c.weightx=75;
		c.weighty=20;
		c.gridwidth = 75;
		c.gridheight = 20;
		
		this.add(chat, c);
	}


	public void setNamePlayer(String name) {
		playerName.setNamePlayer(name);
		chat.setNamePlayer(name);
	}

}
