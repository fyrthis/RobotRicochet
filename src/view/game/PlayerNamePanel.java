package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class PlayerNamePanel extends JPanel {
	private static final long serialVersionUID = -3456497639330063809L;
	JTextField playerName;

	public PlayerNamePanel() {
		setBackground(Color.blue);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		playerName = new JTextField("Bob Dylan");
		playerName.setEditable(false);
		playerName.setHorizontalAlignment(JTextField.CENTER);
		add(playerName, c);
		
	}
	
	public JTextField getPlayerName(){ return this.playerName; }
}
