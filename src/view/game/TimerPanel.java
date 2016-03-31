package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTextField;

import model.GameState;
import model.Model;

public class TimerPanel extends JPanel implements Observer {
	private static final long serialVersionUID = -650268772194032577L;
	//private JTextField timerField;
	Model model;


	/**
	 * 
	 */

	private JTextField title = new JTextField("Timer : ");;
	private JTextField time;

	public TimerPanel(Model model){
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
		add(title);

		c.gridy = 1;
		time = new JTextField("0");
		time.setEditable(false);
		time.setHorizontalAlignment(JTextField.CENTER);
		add(time, c);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		GameState gameState = (GameState) arg;
		time.setText(Integer.toString(gameState.getTime()));
	}

}
