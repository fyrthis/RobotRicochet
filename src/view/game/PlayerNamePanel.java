package view.game;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerNamePanel extends JPanel {
	private static final long serialVersionUID = -3456497639330063809L;
	JLabel playerName;

	public PlayerNamePanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		playerName = new JLabel();
		playerName.setHorizontalAlignment(JLabel.CENTER);
		add(playerName, c);
		
	}

	public void setNamePlayer(String name) {
		playerName.setText(name);
	}

}
