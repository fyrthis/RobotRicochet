package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import model.Model;
import players.AbstractPlayer;
import players.Players.LocalPlayer;

public class RoundBoardPanel extends JPanel implements Observer {
	private static final long serialVersionUID = 6809209443540036320L;
	JTextField title;
	String[] columnNames = {"Pos.",
			"Player",
	"Nombre de Coups"};
	GridBagConstraints c = new GridBagConstraints();
	JTable table;;
	JScrollPane scrollPane;
	private Model model;


	public RoundBoardPanel(Model model) {
		super();
		this.model=model;
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

	@Override
	public void update(Observable o, Object arg) {
		ArrayList<AbstractPlayer> players = model.getPlayers().getPlayersAndLocal();
		ArrayList<AbstractPlayer> sortConnectedPlayers = new ArrayList<>();

		//On retranche les joueurs qui ne sont plus connectés.
		for(AbstractPlayer p : players) {
			if(p.isConnected()) {
				sortConnectedPlayers.add(p);
			}
		}
		//On trie les joueurs.
		sortByCoups(sortConnectedPlayers);

		//On remplit le JTable
		Object[][] data = new Object[sortConnectedPlayers.size()][3];
		int i=0;
		int index_localPlayer = 0;
		for(AbstractPlayer p : sortConnectedPlayers) {
			if(p.getName().equals(LocalPlayer.getInstance().getName()))
				index_localPlayer = i;
			data[i][0] = (i+1)+".";
			data[i][1] = p.getName();
			data[i][2] = p.getCoups();
			i++;
		}

		//On enlève l'ancien
		JViewport viewport = scrollPane.getViewport(); 
		viewport.remove(table);

		//On ajoute le nouveau
		table = new JTable(data, columnNames);
		
		//table.prepareRenderer(table.getCellRenderer(index_localPlayer, 1), index_localPlayer, 1).setBackground(Color.GRAY);
		table.setFillsViewportHeight(true);
		viewport.add(table);
	}

	private void sortByCoups(ArrayList<AbstractPlayer> list) {
		int n = list.size();
		for(int i = 2; i<n; i++)
			for (int k = i; k > 1 && list.get(k).getCoups() < list.get(k-1).getCoups(); k--) 
				swap(list, k, k-1);
	}

	private void swap(ArrayList<AbstractPlayer> list, int i, int j) {
		AbstractPlayer p = list.get(i);
		list.set(i, list.get(j));
		list.set(j, p);
	}
}
