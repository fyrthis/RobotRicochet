package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitNextGameDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5020160951408381474L;
	JButton ok = new JButton("OK");
	
	public WaitNextGameDialog() {
		JPanel panneau ;

		Box boite = Box.createVerticalBox();
		setModal(true);
		setResizable(false);
		panneau = new JPanel();
		panneau.add(new JLabel("Une partie est déjà en cours... Veuillez attendre le prochain tour."));
		boite.add(panneau);

		panneau = new JPanel();
		panneau.add(ok);
		boite.add(panneau);

		add(boite) ;

		ok.addActionListener(this);
		pack();
		setLocation(400, 200);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == ok) {
			setVisible(false);
			dispose();
		}
	}
}