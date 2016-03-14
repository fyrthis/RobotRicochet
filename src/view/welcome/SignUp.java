package view.welcome;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import view.View;

public class SignUp extends JPanel implements ActionListener {
	private static final long serialVersionUID = 6397972706738697407L;
	JTextField username;
	JPasswordField password, confirm;
	JButton back, submit;
	
	public SignUp() {
		
		initializeAllComponents();
		
		
		back.addActionListener(this);
		submit.addActionListener(this);
		
	}

	private void initializeAllComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel l = new JLabel("Username : ", JLabel.TRAILING);
		add(l, c);
		c.gridx = 1;
		c.gridy = 0;
		username = new JTextField(10);

		l.setLabelFor(username);
		add(username, c);
		
		c.gridx = 0;
		c.gridy = 1;
		l = new JLabel("Password : ", JLabel.TRAILING);
		add(l, c);
		c.gridx = 1;
		c.gridy = 1;
		password = new JPasswordField(10);
		l.setLabelFor(password);
		add(password, c);
		
		c.gridx = 0;
		c.gridy = 2;
		l = new JLabel("Confirm : ", JLabel.TRAILING);
		add(l, c);
		c.gridx = 1;
		c.gridy = 2;
		confirm = new JPasswordField(10);
		l.setLabelFor(confirm);
		add(confirm, c);
		
		
		c.gridx = 0;
		c.gridy = 3;
		back = new JButton("Back");
		add(back, c);
		c.gridx = 1;
		c.gridy = 3;
		submit = new JButton("submit");
		add(submit, c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==back) {
			View window = (View)this.getParent().getParent().getParent().getParent();
			window.homeSignal();
		}else if(e.getSource()==submit) {
			View window = (View)this.getParent().getParent().getParent().getParent();
			window.playAsGuestSignal(); //TODO
		}
	}

}
