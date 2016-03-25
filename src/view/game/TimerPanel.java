package view.game;
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
		/*
		setBackground(Color.blue);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		timerField = new JTextField("30");
		timerField.setEditable(false);
		timerField.setHorizontalAlignment(JTextField.CENTER);
		add(timerField, c);
		 */
		this.time = new JTextField("0");

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
		Integer currentTime = Integer.getInteger(time.getText());
		currentTime++;
		time.setText(currentTime.toString());
	}

}
