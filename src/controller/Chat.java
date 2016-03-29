package controller;

import model.Model;

public class Chat {
	
	Model model;
	
	public Chat(Model model) {
		this.model = model;
	}
	
	public void receiveMessageFrom(String username, String message) {
		model.getChatModel().addMessageFrom(username, message);
	}

}
