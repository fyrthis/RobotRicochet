package view.game;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import view.View;

public class InteractionPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 7052828873819286307L;
	JButton button;
	public InteractionPanel() {
		setBackground(Color.green);
		button = new JButton("Back to home page");
		add(button);
		button.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==button) {
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			//System.out.println(this.getParent().getParent().getParent().getParent().getParent());
			window.homeSignal();
		}
		
	}
}
