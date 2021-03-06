package view.welcome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import controller.Controller;
import launcher.Debug;
import view.View;

public class HomePagePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 4745105620276261056L;
	JButton signIn, signUp, playAsGuest;
	
	public HomePagePanel(Controller controller) {
		super();
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
			window.signInSignal();
		}else if(e.getSource() == signUp) {
			window.signUpSignal();
		}else if(e.getSource() == playAsGuest) {
			window.playAsGuestSignal();
		}else
			System.out.println("(Client:"+Debug.curName+")(HomePagePanel:actionPerformed)Unknow event occured");
		
	}
}
