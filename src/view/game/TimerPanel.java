package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import model.Model;

public class TimerPanel extends JPanel {
	private static final long serialVersionUID = -650268772194032577L;
	//private JTextField timerField;
	Model model;


	/**
	 * 
	 */

	private Timer timer;
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
		
		int delay = 1000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//...Perform a task...
				update();
			}
		};
		timer = new Timer(delay, taskPerformer);
		timer.start();
	}

	public void update(){
		Integer currentTime = Integer.parseInt(time.getText());
		currentTime++;
		time.setText(currentTime.toString());
	}

}
