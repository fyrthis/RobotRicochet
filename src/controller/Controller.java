package controller;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import communication.Client;
import communication.ProtocolException;
import model.LocalPlayer;
import model.Model;

public class Controller implements Observer {
	//FIELDS
	protected static Model model;

	public Controller(Model model) {
		Controller.model=model;
	}

	public void connect(String name) throws ConnectException, UnknownHostException, IOException {
		//TODO : Classe de connexion (avec name etc..)
		Client.getInstance().addObserver(this);
		Client.getInstance().connect();
		System.out.println("(Client:"+LocalPlayer.getInstance().getName()+")(Controller) sent : CONNEXION/"+name+"/");
		Client.getInstance().sendMessage("CONNEXION/"+name+"/");
		LocalPlayer.getInstance().setName(name);

	}
	public void disconnect(String name) {
		System.out.println("(Client:"+LocalPlayer.getInstance().getName()+")(Controller) sent : SORT/"+name+"/");
		try {
			Client.getInstance().sendMessage("SORT/"+name+"/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Client.getInstance().deleteObserver(this);
		Client.getInstance().disconnect();
	}
	
	public void sendSolution(String name, int solutionInt){
		String solution = String.valueOf(solutionInt);
		System.out.println("(Client:"+LocalPlayer.getInstance().getName()+")(Controller) sent : SOLUTION/"+name+"/"+solution+"/");
		try {
			Client.getInstance().sendMessage("SOLUTION/"+name+"/"+solution+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendBet(String name, int solutionInt){
		String solution = String.valueOf(solutionInt);
		System.out.println("(Client:"+LocalPlayer.getInstance().getName()+")(Controller) sent : ENCHERE/"+name+"/"+solution+"/");
		try {
			Client.getInstance().sendMessage("ENCHERE/"+name+"/"+solution+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		String message = (String) arg;
		String[] tokens = message.split("/");
		System.out.println("(Client:"+LocalPlayer.getInstance().getName()+")(Controller) received : "+message);
		
		//S->C : BIENVENUE/user/
		if (tokens.length>1 && tokens[0].equals("BIENVENUE")) {
			DeconnexionConnexion.bienvenue(tokens[1]);

			//S->C : CONNECTE/user/
		} else if (tokens.length>1 && tokens[0].equals("CONNECTE")) {
			DeconnexionConnexion.connecte(tokens[1]);
			
			//S->C :DECONNEXION/user/
		} else if (tokens.length>1 && tokens[0].equals("DECONNEXION")) {
			DeconnexionConnexion.deconnexion(tokens[1]);
			
			//S->C : SESSION/plateau/size_x/size_y
		} else if (tokens.length>1 && tokens[0].equals("SESSION")) {
			if(tokens.length==2)
				DebutSession.session(tokens[1]);
			else if(tokens.length > 3)
				try {
					DebutSession.session(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				}catch(NumberFormatException e){ System.err.println("(Client:"+LocalPlayer.getInstance().getName()+")received wrong Session/plateau protocol"); }
		}
		
		//S->C : VAINQUEUR/bilan/
		else if (tokens.length>1 && tokens[0].equals("VAINQUEUR")){
			DebutSession.vainqueur(tokens[1]);
			
		//S->C : TOUR/enigme/bilan/
		} else if (tokens.length>1 && tokens[0].equals("TOUR")){
			Reflexion.tour(tokens[1], tokens[2]);
			
		//S->C : TUASTROUVE/
		} else if (tokens.length>0 && tokens[0].equals("TUASTROUVE")) {
			Reflexion.tuAsTrouve();
			
		//S->C : ILATROUVE/user/coups/
		} else if (tokens.length>2 && tokens[0].equals("ILATROUVE")) {
			Reflexion.ilATrouve(tokens[1], tokens[2]);
			
		//S->C : FINREFLEXION/
		} else if (tokens.length>0 && tokens[0].equals("FINREFLEXION")) {
			Reflexion.finReflexion();
	
		//S->C : VALIDATION/
		} else if (tokens.length>0 && tokens[0].equals("VALIDATION")) {
			Enchere.validation();
			
		//S->C : ECHEC/user/
		} else if (tokens.length>1 && tokens[0].equals("ECHEC")) {
			Enchere.echec(tokens[1]);
			
		//S->C : NOUVELLEENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].equals("NOUVELLEENCHERE")) {
			Enchere.nouvelleEnchere(tokens[1], tokens[2]);
			
		//S->C : FINENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].equals("FINENCHERE")) {
			Enchere.finEnchere(tokens[1], tokens[2]);
			
		//S->C : SASOLUTION/user/deplacements/
		} else if (tokens.length>2 && tokens[0].equals("SASOLUTION")) {
			Resolution.saSolution(tokens[1], tokens[2]);
			
		//S->C : BONNE/
		} else if (tokens.length>0 && tokens[0].equals("BONNE")) {
			Resolution.bonne();
			
		//S->C : MAUVAISE/
		} else if (tokens.length>0 && tokens[0].equals("MAUVAISE")) {
			Resolution.mauvaise(tokens[1]);
			
		//S->C : FINRESO/
		} else if (tokens.length>0 && tokens[0].equals("FINRESO")) {
			Resolution.finReso();
			
		//S->C : TROPLONG/user/
		} else if (tokens.length>1 && tokens[0].equals("TROPLONG")) {
			Resolution.tropLong(tokens[1]);
			
		} else {
			//DO NOTHING
			try {
				throw new ProtocolException("(Client:"+LocalPlayer.getInstance().getName()+")received unknown protocol : "+message);
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
		}
	}

}
