package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AskNameDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5020160951408381474L;
	JButton valider = new JButton("Valider");
	JButton annuler = new JButton("Annuler");
	JTextField champNom = new JTextField(10);
	boolean annule = false;
	
	
	public AskNameDialog() {
		JPanel panneau ;

		Box boite = Box.createVerticalBox();
		setModal(true);
		setResizable(false);
		panneau = new JPanel();
		panneau.add(new JLabel("pseudonyme : "));
		panneau.add(champNom);
		boite.add(panneau);

		panneau = new JPanel();
		panneau.add(valider);
		panneau.add(annuler);
		boite.add(panneau);

		add(boite) ;

		valider.addActionListener(this);
		annuler.addActionListener(this);
		pack();
		setLocation(400, 200);
		setVisible(true);
	}

	public String getName() {
		if(annule == true) return null;
		return champNom.getText();
	}
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == valider) {
			if(champNom.getText() != null && champNom.getText().matches("[A-Za-z0-9]*") && champNom.getText().length()>=3)
				dispose();
			else
				JOptionPane.showMessageDialog(this, "Minimum three alphanumeric caracters !");
		}
		else if (source == annuler) { 
			annule = true;
			dispose();
		}
	}
}