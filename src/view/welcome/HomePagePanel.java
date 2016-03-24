package view.welcome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import controller.Controller;
import view.View;

public class HomePagePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 4745105620276261056L;
	JButton signIn, signUp, playAsGuest;
	private Controller controller;
	
	
	public HomePagePanel(Controller controller) {
		super();
		this.controller = controller;
		signIn = new JButton("Sign In");
		signUp = new JButton("Sign Up");
		playAsGuest = new JButton("Play as guest");
		
		add(signIn);
		add(signUp);
		add(playAsGuest);
		
		signIn.addActionListener(this);
		signUp.addActionListener(this);
		playAsGuest.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		View window = (View)this.getParent().getParent().getParent().getParent();
		if(e.getSource() == signIn) {
			System.out.println("Catch Sign In Event");
			window.signInSignal();
		}else if(e.getSource() == signUp) {
			System.out.println("Catch Sign Up Event");
			window.signUpSignal();
		}else if(e.getSource() == playAsGuest) {
			System.out.println("Catch Play As Guest Event");
			window.playAsGuestSignal();
			controller.connect("yaya");
		}else
			System.out.println("Unknow event occured");
		
	}
}
