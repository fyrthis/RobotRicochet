package controller;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import communication.Client;
import communication.ProtocolException;
import launcher.Debug;
import model.Model;
import utils.Phase;

public class Controller implements Observer {
	//FIELDS
	protected static Model model;
	
	//Sous-traitants
	DebutSession debutSession;
	DeconnexionConnexion deconnexionConnexion;
	Enchere enchere;
	Reflexion reflexion;
	Resolution resolution;
	Chat chat;

	public Controller(Model model, int port) {
		Client.getInstance().setPort(port);
		Controller.model=model;
		debutSession = new DebutSession(model);
		deconnexionConnexion = new DeconnexionConnexion(model);
		enchere = new Enchere(model);
		reflexion = new Reflexion(model);
		resolution = new Resolution(model);
		chat = new Chat(model);
	}

	public void connect(String name) throws ConnectException, UnknownHostException, IOException {
		//TODO : Classe de connexion (avec name etc..)
		Client.getInstance().addObserver(this);
		Client.getInstance().connect();
		System.out.println("(Client:"+Debug.curName+")(Controller:connect) sent : CONNEXION/"+name+"/");
		Client.getInstance().sendMessage("CONNEXION/"+name+"/");
		model.getPlayers().getlocalPlayer().setName(name);
		model.getPlayers().add(name);
	}
	
	public void disconnect(String name) {
		System.out.println("(Client:"+Debug.curName+")(Controller:disconnect) sent : SORT/"+name+"/");
		try {
			Client.getInstance().sendMessage("SORT/"+name+"/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Client.getInstance().deleteObserver(this);
		Client.getInstance().disconnect();
	}
	
	//SOLUTION/user/coups/
	//(C -> S) Annonce d'une solution trouvée par 'user' en 'coups' déplacements
	public void sendSolution(String name, int solutionInt){
		String solution = String.valueOf(solutionInt);
		System.out.println("(Client:"+Debug.curName+")(Controller:sendSolution) sent : SOLUTION/"+name+"/"+solution+"/");
		try {
			Client.getInstance().sendMessage("SOLUTION/"+name+"/"+solution+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// On va enregistrer le contexte de l'état courant du jeu au moment où un joueur envoie une solution
		// durant la phase de reflexion
		model.getGameState().setCurrentSolution(solutionInt);
	}
	
	//ENCHERE/user/coups/
	//(C -> S) Enchère d'une solution trouvée par 'user' en 'coups' déplacements.
	public void sendBet(String name, int solutionInt){
		String solution = String.valueOf(solutionInt);
		System.out.println("(Client:"+Debug.curName+")(Controller:sendBet) sent : ENCHERE/"+name+"/"+solution+"/");
		try {
			if(solutionInt >= model.getGameState().getCurrentSolution()){
				System.err.println("(Client:"+Debug.curName+")(Controller:sendBet) cannot send : ENCHERE/"+name+"/"+solution+"/ because the new solution is not better than the current one !");
			}
			else
				Client.getInstance().sendMessage("ENCHERE/"+name+"/"+solution+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// On considère que le client qui envoie le deplacement est forcément le joueur actif
	public void sendMoves(String name, String moves){
		System.out.println("(Client:"+Debug.curName+")(Controller:sendMoves) sent : ENVOISOLUTION/"+name+"/"+moves+"/");
		try {
			Client.getInstance().sendMessage("ENVOISOLUTION/"+name+"/"+moves+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.getGameState().setSolutionMoves(moves);
	}
	

	public void sendMessages(String name, String message) {
		System.out.println("(Client:"+Debug.curName+")(Controller:sendMessages) sent : MESSAGE/"+name+"/"+message+"/");
		try {
			Client.getInstance().sendMessage("MESSAGE/"+name+"/"+message+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		String message = (String) arg;
		String[] tokens = message.split("/");
		System.out.println("(Client:"+Debug.curName+")(Controller:update) received : "+message);

		//S->C : BIENVENUE/user/
		if (tokens.length>1 && tokens[0].compareTo("BIENVENUE")==0) {
			deconnexionConnexion.bienvenue(tokens[1]);

			//S->C : CONNECTE/user/
		} else if (tokens.length>1 && tokens[0].compareTo("CONNECTE")==0) {
			deconnexionConnexion.connecte(tokens[1]);
			
			//S->C :DECONNEXION/user/
		} else if (tokens.length>1 && tokens[0].compareTo("DECONNEXION")==0) {
			deconnexionConnexion.deconnexion(tokens[1]);
			
			//S->C : SESSION/plateau/size_x/size_y
		} else if (tokens.length>1 && tokens[0].compareTo("SESSION")==0) {
			if(tokens.length==2)
				debutSession.session(tokens[1]);
			else if(tokens.length > 3)
				try {
					debutSession.session(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				}catch(NumberFormatException e){ System.err.println("(Client:"+Debug.curName+")(Controller:update) : received wrong Session/plateau protocol"); }
		
		//S->C : VAINQUEUR/bilan/
		} else if (tokens.length>1 && tokens[0].compareTo("VAINQUEUR")==0){
			debutSession.vainqueur(tokens[1]);
			
		//S->C : TOUR/enigme/bilan/
		} else if (tokens.length>1 && tokens[0].compareTo("TOUR")==0){
			reflexion.tour(tokens[1], tokens[2]);
			
		//S->C : TUASTROUVE/
		} else if (tokens.length>0 && tokens[0].compareTo("TUASTROUVE")==0) {
			reflexion.tuAsTrouve();
			
		//S->C : ILATROUVE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("ILATROUVE")==0) {
			reflexion.ilATrouve(tokens[1], tokens[2]);
			
		//S->C : FINREFLEXION/
		} else if (tokens.length>0 && tokens[0].compareTo("FINREFLEXION")==0) {
			reflexion.finReflexion();
	
		//S->C : VALIDATION/
		} else if (tokens.length>0 && tokens[0].compareTo("VALIDATION")==0) {
			enchere.validation();
			
		//S->C : ECHEC/user/
		} else if (tokens.length>1 && tokens[0].compareTo("ECHEC")==0) {
			enchere.echec(tokens[1]);
			
		//S->C : NOUVELLEENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("NOUVELLEENCHERE")==0) {
			enchere.nouvelleEnchere(tokens[1], tokens[2]);
			
		//S->C : FINENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("FINENCHERE")==0) {
			enchere.finEnchere(tokens[1], tokens[2]);
			
		//S->C : SASOLUTION/user/deplacements/
		} else if (tokens.length>2 && tokens[0].compareTo("SASOLUTION")==0) {
			resolution.saSolution(tokens[1], tokens[2]);
			
		//S->C : BONNE/
		} else if (tokens.length>0 && tokens[0].compareTo("BONNE")==0) {
			resolution.bonne();
			
		//S->C : MAUVAISE/
		} else if (tokens.length>0 && tokens[0].compareTo("MAUVAISE")==0) {
			resolution.mauvaise(tokens[1]);
			
		//S->C : FINRESO/
		} else if (tokens.length>0 && tokens[0].compareTo("FINRESO")==0) {
			resolution.finReso();
			
		//S->C : TROPLONG/user/
		} else if (tokens.length>1 && tokens[0].compareTo("TROPLONG")==0) {
			resolution.tropLong(tokens[1]);
		
		//S->C : MESSAGE/user/message/	
		} else if (tokens.length>2 && tokens[0].compareTo("MESSAGE")==0) {
			chat.receiveMessageFrom(tokens[1], tokens[2]);
		} else {
			//DO NOTHING
			try {
				throw new ProtocolException("(Client:"+Debug.curName+")(Controller:update) : received unknown protocol : "+message);
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
		}
	}

}
