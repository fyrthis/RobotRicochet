package view.game;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import launcher.Debug;
import model.Model;
import players.Players.LocalPlayer;
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
		layout = new GroupLayout(this);
		setLayout(layout);
   	}
	
	
	public void setPanelByPhase(Phase phase){
		// pour le moment aucune distinction entre la phase d'initialisation et la phase de reflexion
		// plus tard peut etre faire une sorte de chargement/initialisation de la map avant de passer
		// a la phase de reflexion
		if(phase == Phase.NOGAME){
			JOptionPane.showMessageDialog(this, "Démarrage de la partie... ");
		}
		else if(phase == Phase.INITIALISATION){
			if(backButton != null)
				remove(backButton);
			
			// Phase de reflexion
			if(sendSolutionButton != null)
				remove(sendSolutionButton);
			if(solutionEntry != null)
				remove(solutionEntry);
			if(setSolutionLabel != null)
				remove(setSolutionLabel);
			
			// Phase d'enchere
			if(betButton != null)
				remove(betButton);
			if(betEntry != null)
				remove(betEntry);
			if(betLabel != null)
				remove(betLabel);
			
			// Phase de resolution
			if(resolveButton != null)
				remove(resolveButton);
			if(movesEntry != null)
				remove(movesEntry);
			if(movesLabel != null)
				remove(movesLabel);

	        setSolutionLabel = new JLabel("Annoncez votre solution : ");
			solutionEntry = new JTextField(5);
			sendSolutionButton = new JButton("Send Solution");
			backButton = new JButton("Quit game");
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(setSolutionLabel)
							.addComponent(solutionEntry)
							.addComponent(sendSolutionButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(backButton)));
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(setSolutionLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(solutionEntry))
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
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
							.addComponent(betLabel)
							.addComponent(betEntry)
							.addComponent(betButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(backButton)));
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(betLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(betEntry))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(betButton)
							.addComponent(backButton)));
		
			betButton.addActionListener(this);
		}
		else if(phase == Phase.RESOLUTION_ACTIVE || phase == Phase.RESOLUTION_PASSIVE){
			this.remove(betLabel);
			this.remove(betEntry);
			this.remove(betButton);
			
			if(movesLabel != null)
				this.remove(movesLabel);
			if(movesEntry != null)
				this.remove(movesEntry);
			if(resolveButton != null)
				this.remove(resolveButton);

	        movesLabel = new JLabel("Veuillez envoyer vos déplacements : ");
			movesEntry = new JTextField(50);
			resolveButton = new JButton("Send your moves");
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(movesLabel)
							.addComponent(movesEntry)
							.addComponent(resolveButton))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(backButton)));
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(movesLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(movesEntry))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(resolveButton)
							.addComponent(backButton)));
		
			resolveButton.addActionListener(this);
			
			if(phase == Phase.RESOLUTION_PASSIVE){
				movesLabel.setEnabled(false);
				movesEntry.setEnabled(false);
				resolveButton.setEnabled(false);
				
				if(LocalPlayer.getInstance().hasAlreadyProposed()){
					JOptionPane.showMessageDialog(this, "Votre solution est mauvaise...\nVous êtes mauvais...\nVous êtes exclu du tour parce que vous êtes mauvais...\n");
				}
			}
		}
		else if(phase == Phase.WIN){
			final JOptionPane optionPane = new JOptionPane("Vous avez gagné !",
					JOptionPane.INFORMATION_MESSAGE);

			final JDialog dialog = new JDialog();
			dialog.setContentPane(optionPane);
			dialog.setDefaultCloseOperation(
					JDialog.DO_NOTHING_ON_CLOSE);
			optionPane.addPropertyChangeListener(
				    new PropertyChangeListener() {
				        public void propertyChange(PropertyChangeEvent e) {
				            String prop = e.getPropertyName();

				            if (dialog.isVisible() 
				             && (e.getSource() == optionPane)
				             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
				                //If you were going to check something
				                //before closing the window, you'd do
				                //it here.
				                dialog.setVisible(false);
				            }
				        }
				    });
			dialog.pack();
			dialog.setVisible(true);

			int value = ((Integer)optionPane.getValue()).intValue();
			if (value == JOptionPane.INFORMATION_MESSAGE) {
				model.getGameState().setPhase(Phase.INITIALISATION);
			}
		}
		else if(phase == Phase.LOSE){
			final JOptionPane optionPane = new JOptionPane("Vous avez perdu !",
					JOptionPane.INFORMATION_MESSAGE);

			final JDialog dialog = new JDialog();
			dialog.setContentPane(optionPane);
			dialog.setDefaultCloseOperation(
					JDialog.DO_NOTHING_ON_CLOSE);
			optionPane.addPropertyChangeListener(
				    new PropertyChangeListener() {
				        public void propertyChange(PropertyChangeEvent e) {
				            String prop = e.getPropertyName();

				            if (dialog.isVisible() 
				             && (e.getSource() == optionPane)
				             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
				                //If you were going to check something
				                //before closing the window, you'd do
				                //it here.
				                dialog.setVisible(false);
				            }
				        }
				    });
			dialog.pack();
			dialog.setVisible(true);

			int value = ((Integer)optionPane.getValue()).intValue();
			if (value == JOptionPane.INFORMATION_MESSAGE) {
				model.getGameState().setPhase(Phase.INITIALISATION);
			}
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==backButton) {
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
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
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			String movesStr = movesEntry.getText();
			if(movesStr == null){
				System.err.println("(Client:"+Debug.curName+")(InteractionPanel:actionPerformed) Erreur: vous tentez d'envoyer une solution vide !");
			}
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
	}
}
