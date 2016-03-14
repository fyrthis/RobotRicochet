package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ScoreBoardPanel extends JPanel implements Observer {
	private static final long serialVersionUID = -8480066475856062513L;
	JTextField title;

	public ScoreBoardPanel() {
		setBackground(Color.yellow);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.1;
		
		title = new JTextField("Game players' score :");
		title.setEditable(false);
		title.setHorizontalAlignment(JTextField.CENTER);
		add(title, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
        String[] columnNames = {"Pos.",
                "Player",
                "Score"};

		Object[][] data = {
		{"1.", "Smith", new Integer(28)},
		{"2.", "John", new Integer(21)},
		{"3.", "Sue", new Integer(19)},
		{"4.", "Jane", new Integer(6)},
		{"5.", "Smith", new Integer(3)},
		{"6.", "John", new Integer(3)},
		{"7.", "Sue", new Integer(4)},
		{"8.", "Jane", new Integer(6)},
		{"9.", "Smith", new Integer(3)},
		{"10.", "John", new Integer(3)},
		{"11.", "Sue", new Integer(4)},
		{"12.", "Jane", new Integer(6)},
		{"13.", "Smith", new Integer(3)},
		{"14.", "John", new Integer(3)},
		{"15.", "Sue", new Integer(4)},
		{"16.", "Jane", new Integer(6)},
		{"17.", "Joe", new Integer(7)}
		};
		
		final JTable table = new JTable(data, columnNames);
		//table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, c);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//receives players as arg ; sort and display
		
	}
}
