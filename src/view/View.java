package view;
import java.awt.Component;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.Controller;
import model.LocalPlayer;
import model.Model;
import utils.AskNameDialog;
import view.game.GamePanel;
import view.welcome.HomePagePanel;

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
		AskNameDialog dialog = new AskNameDialog();
		String name = dialog.getName();
		if(name == null) return; //user a annulé.
		
		gamePane = new GamePanel(model);
		add(gamePane);
		gamePane.setVisible(false);
		try {
			controller.connect(name);
			gamePane.setNamePlayer(name);
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(this, "Server seems to be offline.");
			return;
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(this, "Server not found.");
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "I/O error...");
			return;
		}
		
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow && c!=gamePane)
				remove(c);
		}
		this.getContentPane().getComponent(0).setVisible(false);
		
		


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
		controller.disconnect(LocalPlayer.getInstance().getName());
		connectionWindow.setVisible(true);
	}

	public void signUpSignal() {
//		for(Component c : getContentPane().getComponents()) {
//			if(c!=connectionWindow)
//				remove(c);
//		}
//		add(new SignUp());
//		connectionWindow.setVisible(false);
		JOptionPane.showMessageDialog(this, "Not implemented yet.");
	}

	public void signInSignal() {
//		for(Component c : getContentPane().getComponents()) {
//			if(c!=connectionWindow)
//				remove(c);
//		}
//		add(new SignIn());
//		connectionWindow.setVisible(false);
		JOptionPane.showMessageDialog(this, "Not implemented yet.");
	}
	
	// TO CONTINUE...
	public void sendSolutionSignal(int solution) {
		controller.sendSolution("toto", solution);
	}
	
	public void bet(int stroke) {
		controller.sendBetSignal("toto", stroke);
	}

}
