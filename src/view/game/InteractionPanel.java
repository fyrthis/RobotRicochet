package view.game;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.Phase;
import view.View;

public class InteractionPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 7052828873819286307L;
	
	JButton backButton;
	JButton sendSolutionButton;
	JTextField solutionEntry;
	JLabel setSolutionLabel;
	
	Phase phase;
	
	public InteractionPanel(Phase p) {
		this.phase = p;
		setPanelByPhase();
		setBackground(Color.green);
		
	}
	
	
	public void setPanelByPhase(){
		// pour le moment aucune distinction entre la phase d'initialisation et la phase de reflexion
		// plus tard peut etre faire une sorte de chargement/initialisation de la map avant de passer
		// a la phase de reflexion
		if(this.phase == Phase.INITIALISATION){
			GroupLayout layout = new GroupLayout(this);
	        setLayout(layout);
	        
	        setSolutionLabel = new JLabel("Annoncez votre solution : ");
			solutionEntry = new JTextField(5);
			sendSolutionButton = new JButton("Send Solution");
			backButton = new JButton("Back to home page");
			
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
		else if(this.phase == Phase.ENCHERE){
			
		}
		else if(this.phase == Phase.RESOLUTION){
			
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
				System.err.println("Erreur: vous tentez d'envoyer une solution vide !");
			}
			else {
				try {
					int solution = Integer.valueOf(solutionStr);
					window.sendSolutionSignal(solution);
				}
				catch(NumberFormatException e1){
					System.err.println("Erreur: vous essayez d'insérer autre chose qu'un nombre !");
				}
			}
		}
		
	}
}
