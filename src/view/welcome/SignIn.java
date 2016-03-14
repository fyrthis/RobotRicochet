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

public class SignIn extends JPanel implements ActionListener {
	private static final long serialVersionUID = 6645330582951100817L;
	JTextField username;
	JPasswordField password;
	JButton back, submit;
	
	public SignIn() {
		intializeAllComponents();
		back.addActionListener(this);
		submit.addActionListener(this);
	}
	
	private void intializeAllComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.CENTER;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel label = new JLabel("Login : ", JLabel.TRAILING);
		add(label, c);
		c.gridx = 1;
		c.gridy = 0;
		username = new JTextField(10);

		label.setLabelFor(username);
		add(username, c);
		
		c.gridx = 0;
		c.gridy = 1;
		label = new JLabel("Password : ", JLabel.TRAILING);
		add(label, c);
		c.gridx = 1;
		c.gridy = 1;
		password = new JPasswordField(10);
		label.setLabelFor(password);
		add(password, c);
		
		
		c.gridx = 0;
		c.gridy = 3;
		back = new JButton("Back");
		add(back, c);
		c.gridx = 1;
		c.gridy = 3;
		submit = new JButton("Sign In");
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
