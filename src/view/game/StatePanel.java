package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class StatePanel extends JPanel {
	private static final long serialVersionUID = -5838924958575253423L;
	private String reflexion = "Phase de réflexion";
	//private String enchere = "Phase d'enchères";
	//private String resolution = "Phase de résolution";
	private JTextField stateTitle;
	
	public StatePanel() {
		setBackground(Color.green);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		stateTitle = new JTextField(reflexion);
		stateTitle.setEditable(false);
		stateTitle.setHorizontalAlignment(JTextField.CENTER);
		add(stateTitle, c);
	}
}
