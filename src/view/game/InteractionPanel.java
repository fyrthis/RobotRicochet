package view.game;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import launcher.Debug;
import model.GameState;
import model.Model;
import utils.Phase;
import view.View;

public class InteractionPanel extends JPanel implements ActionListener, Observer {
	private static final long serialVersionUID = 7052828873819286307L;
	

	GroupLayout layout;
	Phase phase;

	JButton backButton;
	
	// Phase de reflexion
	JButton sendSolutionButton;
	JTextField solutionEntry;
	JLabel setSolutionLabel;
	
	// Phase d'enchere
	JButton betButton;
	JTextField betEntry;
	JLabel betLabel;
	
	// Phase de resolution
	JButton resolveButton;
	JTextField movesEntry;
	JLabel movesLabel;
	
	Model model;
	
	public InteractionPanel(Model model) {
		super();
		this.model=model;
		setBackground(Color.green);
		layout = new GroupLayout(this);
		setLayout(layout);
   	}
	
	
	public void setPanelByPhase(Phase phase){
		// pour le moment aucune distinction entre la phase d'initialisation et la phase de reflexion
		// plus tard peut etre faire une sorte de chargement/initialisation de la map avant de passer
		// a la phase de reflexion
		if(phase == Phase.INITIALISATION){
	        
	        setSolutionLabel = new JLabel("Annoncez votre solution : ");
			solutionEntry = new JTextField(5);
			sendSolutionButton = new JButton("Send Solution");
			backButton = new JButton("Quit game");
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(setSolutionLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(solutionEntry))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(sendSolutionButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(backButton)));
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(setSolutionLabel)
							.addComponent(solutionEntry)
							.addComponent(sendSolutionButton)
							.addComponent(backButton)));
		
			backButton.addActionListener(this);
			sendSolutionButton.addActionListener(this);
		}
		else if(phase == Phase.ENCHERE){
			this.remove(setSolutionLabel);
			this.remove(solutionEntry);
			this.remove(sendSolutionButton);

	        betLabel = new JLabel("Proposez une meilleure solution : ");
			betEntry = new JTextField(5);
			betButton = new JButton("Bet a better solution");
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(betLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(betEntry))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(betButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(backButton)));
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(betLabel)
							.addComponent(betEntry)
							.addComponent(betButton)
							.addComponent(backButton)));
		
			betButton.addActionListener(this);
		}
		else if(phase == Phase.RESOLUTION){
			this.remove(betLabel);
			this.remove(betEntry);
			this.remove(betButton);

	        movesLabel = new JLabel("Veuillez envoyer votre solution décrivant les déplacements des robots : ");
			movesEntry = new JTextField(50);
			resolveButton = new JButton("Send your moves");
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(movesLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(movesEntry))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(resolveButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(backButton)));
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(movesLabel)
							.addComponent(movesEntry)
							.addComponent(resolveButton)
							.addComponent(backButton)));
		
			System.out.println("======== PHASE DE RESOLUTION ========");
			resolveButton.addActionListener(this);
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==backButton) {
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			//System.out.println(this.getParent().getParent().getParent().getParent().getParent());
			window.homeSignal();
		}
		else if(e.getSource() == sendSolutionButton) {
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			String solutionStr = solutionEntry.getText();
			if(solutionStr == null){
				System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed) Erreur: vous tentez d'envoyer une solution vide !");
			}
			else {
				try {
					int solution = Integer.valueOf(solutionStr);
					window.sendSolutionSignal(solution);
					//setPanelByPhase(Phase.ENCHERE);
				}
				catch(NumberFormatException e1){
					System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed)Erreur: vous essayez d'insérer autre chose qu'un nombre !");
				}
			}
		}
		else if(e.getSource() == betButton) {
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			String betStr = betEntry.getText();
			if(betStr == null){
				System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed)Erreur: vous n'avez rien encheri !");
			}
			else {
				try {
					int stroke = Integer.valueOf(betStr);
					window.betSignal(stroke);
				}
				catch(NumberFormatException e1){
					System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed)Erreur: vous essayez d'insérer autre chose qu'un nombre !");
				}
			}
		}
		else if(e.getSource() == resolveButton) {
			System.out.println("\tresolveButton pushed");
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			String movesStr = movesEntry.getText();
			System.out.println("\tMOVE : " + movesStr);
			if(movesStr == null){
				System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed)Erreur: vous n'avez rien déplacé !");
			}
			else {
				try {
					window.resolveMovesSignal(movesStr);
				}
				catch(NumberFormatException e1){
					System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed)Erreur: vous essayez d'insérer autre chose qu'un nombre !");
				}
			}
		}
		
	}


	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		Phase phase = (Phase) arg;
		this.setPanelByPhase(phase);
		System.out.println("(Client:"+Debug.curName+")(InteractionPanel:update)receive notifyObserver from the GameState...");
	}
}
