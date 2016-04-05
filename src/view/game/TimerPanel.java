package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Model;
//import model.Timer;

public class TimerPanel extends JPanel implements Observer {
	private static final long serialVersionUID = -650268772194032577L;
	//private JTextField timerField;
	Model model;

	private JLabel title = new JLabel("Timer : ");
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
		int timeCpt = (int) arg;
		if(timeCpt<=0) {
			this.time.setText("Time out !");
		} else {
			if(timeCpt<60)
				this.time.setText(Integer.toString(timeCpt)+"sec");
			else
				if(timeCpt%60!=0)
					this.time.setText(Integer.toString(timeCpt/60)+"min "+Integer.toString(timeCpt%60)+"sec");
				else
					this.time.setText(Integer.toString(timeCpt/60)+"min");
		}
	}

}
