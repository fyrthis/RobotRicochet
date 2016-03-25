package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Model;

public class TimerPanel extends JPanel {
	private static final long serialVersionUID = -650268772194032577L;
	private JTextField timer;
	Model model;

	public TimerPanel(Model model) {
		super();
		this.model = model;
		setBackground(Color.blue);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		timer = new JTextField("30");
		timer.setEditable(false);
		timer.setHorizontalAlignment(JTextField.CENTER);
		add(timer, c);
	}

}
