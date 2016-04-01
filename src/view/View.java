package view;
import java.awt.Component;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import launcher.Debug;
import model.Model;
import utils.AskNameDialog;
import utils.Phase;
import utils.WaitNextGameDialog;
import view.game.AbstractGamePanel;
import view.game.GamePanel;
import view.game.NonGamerPanel;
import view.welcome.HomePagePanel;
import controller.Controller;

public class View extends JFrame {
	private static final long serialVersionUID = -3880026026104218593L;

	JPanel connectionWindow;
	AbstractGamePanel gamePane;
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
		AskNameDialog askNamedialog = new AskNameDialog();
		String name = askNamedialog.getName();
		if(name == null) return; //user a annulé.

		gamePane = new GamePanel(model);
		model.getGameState().setTour(1);
		model.getGameState().setActiveSolution(-1);

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
		System.out.println("(Client:"+Debug.curName+")(View:homeSignal)home signal");
		for(Component c : getContentPane().getComponents()) {
			if(c!=connectionWindow)
				remove(c);
		}
		controller.disconnect(model.getPlayers().getlocalPlayer().getName());
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
		controller.sendSolution(model.getPlayers().getlocalPlayer().getName(), solution);
	}
	
	public void betSignal(int stroke) {
		controller.sendEnchere(model.getPlayers().getlocalPlayer().getName(), stroke);
	}
	
	public void resolveMovesSignal(String moves) {
		boolean withAnimation = true;
		if(withAnimation)
			controller.sendDeplacementsWithAnimationTime(model.getPlayers().getlocalPlayer().getName(), moves);
		else
			controller.sendDeplacements(model.getPlayers().getlocalPlayer().getName(), moves);
	}
	
	public void sendMessageSignal(String messages) {
		controller.sendMessages(model.getPlayers().getlocalPlayer().getName(), messages);
	}

}
