package view.game;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import launcher.Debug;
import model.ChatModel;
import view.View;

public class ChatPanel extends JPanel implements ActionListener, Observer {

	private static final long serialVersionUID = 7052828873819286307L;

	JTextArea msgArea;
	JTextField msgEntry;
	JButton sendMsgButton;
	JScrollPane scrollPane;
	
	ChatModel chatModel;
	String username;
	
	public ChatPanel(ChatModel chatModel) {
		super(new GridBagLayout());
		
		this.chatModel = chatModel;
		
		msgArea = new JTextArea(3, 20);
		msgArea.setEditable(false);
		scrollPane = new JScrollPane(msgArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		msgEntry = new JTextField();
		sendMsgButton = new JButton("Envoyer");
		
		//Add Components to this panel
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 100;
		c.weighty = 20;
		c.gridwidth = 100;
		c.gridheight = 20;
		add(scrollPane, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 21;
		c.weightx = 90;
		c.weighty = 7;
		c.gridwidth = 90;
		c.gridheight = 7;
		add(msgEntry, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 91;
		c.gridy = 21;
		c.weightx = 9;
		c.weighty = 7;
		c.gridwidth = 9;
		c.gridheight = 7;
		add(sendMsgButton, c);
		
		setBackground(Color.red);

		sendMsgButton.addActionListener(this);
	}
	
	public void setNamePlayer(String name) { this.username = name; }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==sendMsgButton) {
			View window = (View) this.getParent().getParent().getParent().getParent().getParent();
			System.out.println("sending message: " + msgEntry.getText()  + " from " + username);
			//chatModel.addMessageFrom(username, msgEntry.getText());
			
			window.sendMessageSignal(msgEntry.getText());
			chatModel.addMessageFrom("Me", msgEntry.getText());
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		String message = "";
		for(String msg : (ArrayList<String>) arg){
			message += "\n" + msg;
		}
		msgArea.setText(message);
		System.out.println("(Client:"+Debug.curName+")(InteractionPanel:update)receive notifyObserver from the GameState...");
	}
}
