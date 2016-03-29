package model;

import java.util.ArrayList;
import java.util.Observable;

public class ChatModel extends Observable {
	
	ArrayList<String> messageList;
	
	public ChatModel(){
		messageList = new ArrayList<String>();
	}
	
	public void addMessageFrom(String username, String s){
		messageList.add(username + " : " + s);
		this.setChanged();
		this.notifyObservers(messageList);
	}

}
