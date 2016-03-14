package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import model.Players.Player;

public class RoundBoardPanel extends JPanel implements Observer {
	private static final long serialVersionUID = 6809209443540036320L;
	JTextField title;
    String[] columnNames = {"Pos.",
            "Player",
            "Score"};
    GridBagConstraints c = new GridBagConstraints();
    JTable table;;
    JScrollPane scrollPane;


	public RoundBoardPanel() {
		setBackground(Color.yellow);
		
		setLayout(new GridBagLayout());
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.1;
		
		title = new JTextField("Round players' solutions :");
		title.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);
		add(title, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;

		Object[][] data = {{"","",""}};
		table = new JTable(data, columnNames);
		
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
		add(scrollPane, c);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//receives players as arg ; sort and display
		List<Player> players = (List<Player>)arg;
		Object[][] data = new Object[players.size()][3];
		for(int i = 0; i<players.size() ; i++) {
			Player p = players.get(i);
			Object[] obj = {i, p.getName(), p.getCoups() }; 
			data[i] = obj;
		}
		
		//TODO : pas propre du tout, du tout, du tout
		this.remove(scrollPane);
		table = new JTable(data, columnNames);
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
		add(scrollPane, c);
		this.revalidate();
		this.repaint();

	}
}
