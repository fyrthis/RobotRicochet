package controller;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import players.Players.LocalPlayer;
import utils.Tools;
import communication.Client;
import communication.ProtocolException;
import launcher.Debug;
import model.Model;

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
		Client.getInstance().sendMessage("CONNEXION/"+name+"/");
	}
	
	public void disconnect(String name) {
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
		try {
			// On va enregistrer le contexte de l'état courant du jeu au moment où un joueur envoie une solution
			// durant la phase de reflexion
			LocalPlayer.getInstance().setNbCoups(solutionInt);
			Client.getInstance().sendMessage("SOLUTION/"+name+"/"+solution+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//ENCHERE/user/coups/
	//(C -> S) Enchère d'une solution trouvée par 'user' en 'coups' déplacements.
	public void sendEnchere(String name, int solutionInt){
		String solution = String.valueOf(solutionInt);
		try {
			if(LocalPlayer.getInstance().getCoups() > -1 && solutionInt >= LocalPlayer.getInstance().getCoups()){
				System.err.println("(Client:"+Debug.curName+")(Controller:sendBet) cannot send : ENCHERE/"+name+"/"+solution+"/ because the new solution is not better than the current one !");
			}
			else {
				LocalPlayer.getInstance().setNbCoups(solutionInt);
				Client.getInstance().sendMessage("ENCHERE/"+name+"/"+solution+"/");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// On considère que le client qui envoie le deplacement est forcément le joueur actif
	public void sendDeplacements(String name, String moves){
		try {
			model.getGameState().setActivePlayer(name);
			Client.getInstance().sendMessage("ENVOISOLUTION/"+name+"/"+moves+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.getGameState().setSolutionMoves(moves);
	}
	
	/***************
	 *  EXTENSION  *
	 ***************/
	
	// On envoie le temps que va mettre l'animation de la bonne réponse au serveur pour qu'il attende de son côté
	// la fin de l'animation
	// C -> S : ENVOISOLUTION/user/deplacements/animationTime
	public void sendDeplacementsWithAnimationTime(String name, String moves){
		// On va calculer le temps que va mettre les deplacements pour l'animation
		int animationTime = Tools.computeAnimationTime(moves);
		try {
			model.getGameState().setActivePlayer(name);
			Client.getInstance().sendMessage("ENVOISOLUTION/"+name+"/"+moves+"/"+animationTime+"/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.getGameState().setSolutionMoves(moves);
	}

	public void sendMessages(String name, String message) {
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
