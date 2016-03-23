package view;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Model;
import utils.Phase;
import view.game.GamePanel;
import view.game.InteractionPanel;
import view.welcome.HomePagePanel;
import view.welcome.SignIn;
import view.welcome.SignUp;
import controller.Controller;

public class View extends JFrame {
	private static final long serialVersionUID = -3880026026104218593L;

	JPanel connectionWindow;
	GamePanel gamePane;
	Model model;
	Controller controller ;
	
	public View(Model model, Controller controller) {
		super("Robot Ricochet");
		this.model = model;
		this.controller = controller;
		connectionWindow = new HomePagePanel(controller);
		this.add(connectionWindow);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setVisible(true);
	}

	public void playAsGuestSignal() {
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow)
				remove(c);
		}
		this.getContentPane().getComponent(0).setVisible(false);
		gamePane = new GamePanel(model);
		add(gamePane);
		gamePane.setVisible(true);
		this.revalidate();
		this.repaint();
	}

	public void homeSignal() {
		System.out.println("home signal");
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow)
				remove(c);
		}
		controller.disconnect(gamePane.getPlayerNamePanel().getPlayerName().getText());
		connectionWindow.setVisible(true);
	}

	public void signUpSignal() {
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow)
				remove(c);
		}
		add(new SignUp());
		connectionWindow.setVisible(false);
	}

	public void signInSignal() {
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow)
				remove(c);
		}
		add(new SignIn());
		connectionWindow.setVisible(false);
	}
	
	// TO CONTINUE...
	public void sendSolutionSignal(int solution) {
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow)
				remove(c);
		}
		controller.sendSolution("toto", solution);
		connectionWindow.setVisible(false);
		
	}

}
