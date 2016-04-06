package view.game;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Model;
import utils.Phase;

public class StatePanel extends JPanel implements Observer {
	private static final long serialVersionUID = -5838924958575253423L;
	private String txt = "Phase de réflexion";
	//private String enchere = "Phase d'enchères";
	//private String resolution = "Phase de résolution";
	private JLabel stateTitle;
	Model model;
	
	public StatePanel(Model model) {
		super();
		this.model=model;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		stateTitle = new JLabel(txt);
		stateTitle.setHorizontalAlignment(JLabel.CENTER);
		add(stateTitle, c);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		Phase phase = (Phase) arg;
		if(phase == Phase.NOGAME)
			txt = "En attente de joueurs...";
		else if(phase == Phase.INITIALISATION)
			txt = "Phase d'initialisation";
		else if(phase == Phase.REFLEXION)
			txt = "Phase de reflexion";
		else if(phase == Phase.ENCHERE)
			txt = "Phase d'enchère";
		else if(phase == Phase.RESOLUTION_ACTIVE)
			txt = "Phase de résolution - C'est à vous de jouer !";
		else if(phase == Phase.RESOLUTION_PASSIVE)
			txt = "Phase de résolution - ...c'est à " + model.getGameState().getActivePlayer() + " de jouer!";
		stateTitle.setText(txt);
	}
}
