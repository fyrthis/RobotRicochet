package controller;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import utils.Phase;
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
		model.getPlayers().add(name);
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
		// On va enregistrer le contexte de l'état courant du jeu au moment où un joueur envoie une solution
		// durant la phase de reflexion
		model.getGameState().setPhase(Phase.REFLEXION);
		model.getGameState().setCurrentSolution(solutionInt);
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
		System.out.print("(Client:"+LocalPlayer.getInstance().getName()+")(Controller) tokens : ");
		for(int i =0; i<tokens.length;i++) {
			System.out.print("["+i+"]"+tokens[i]+"\t");
		}
		System.out.println();

		//S->C : BIENVENUE/user/
		if (tokens.length>1 && tokens[0].compareTo("BIENVENUE")==0) {
			DeconnexionConnexion.bienvenue(tokens[1]);

			//S->C : CONNECTE/user/
		} else if (tokens.length>1 && tokens[0].compareTo("CONNECTE")==0) {
			DeconnexionConnexion.connecte(tokens[1]);
			
			//S->C :DECONNEXION/user/
		} else if (tokens.length>1 && tokens[0].compareTo("DECONNEXION")==0) {
			DeconnexionConnexion.deconnexion(tokens[1]);
			
			//S->C : SESSION/plateau/size_x/size_y
		} else if (tokens.length>1 && tokens[0].compareTo("SESSION")==0) {
			if(tokens.length==2)
				DebutSession.session(tokens[1]);
			else if(tokens.length > 3)
				try {
					DebutSession.session(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				}catch(NumberFormatException e){ System.err.println("(Client:"+LocalPlayer.getInstance().getName()+")received wrong Session/plateau protocol"); }
		
		//S->C : VAINQUEUR/bilan/
		} else if (tokens.length>1 && tokens[0].compareTo("VAINQUEUR")==0){
			DebutSession.vainqueur(tokens[1]);
			
		//S->C : TOUR/enigme/bilan/
		} else if (tokens.length>1 && tokens[0].compareTo("TOUR")==0){
			Reflexion.tour(tokens[1], tokens[2]);
			
		//S->C : TUASTROUVE/
		} else if (tokens.length>0 && tokens[0].compareTo("TUASTROUVE")==0) {
			Reflexion.tuAsTrouve();
			
		//S->C : ILATROUVE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("ILATROUVE")==0) {
			Reflexion.ilATrouve(tokens[1], tokens[2]);
			
		//S->C : FINREFLEXION/
		} else if (tokens.length>0 && tokens[0].compareTo("FINREFLEXION")==0) {
			Reflexion.finReflexion();
	
		//S->C : VALIDATION/
		} else if (tokens.length>0 && tokens[0].compareTo("VALIDATION")==0) {
			Enchere.validation();
			
		//S->C : ECHEC/user/
		} else if (tokens.length>1 && tokens[0].compareTo("ECHEC")==0) {
			Enchere.echec(tokens[1]);
			
		//S->C : NOUVELLEENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("NOUVELLEENCHERE")==0) {
			Enchere.nouvelleEnchere(tokens[1], tokens[2]);
			
		//S->C : FINENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("FINENCHERE")==0) {
			Enchere.finEnchere(tokens[1], tokens[2]);
			
		//S->C : SASOLUTION/user/deplacements/
		} else if (tokens.length>2 && tokens[0].compareTo("SASOLUTION")==0) {
			Resolution.saSolution(tokens[1], tokens[2]);
			
		//S->C : BONNE/
		} else if (tokens.length>0 && tokens[0].compareTo("BONNE")==0) {
			Resolution.bonne();
			
		//S->C : MAUVAISE/
		} else if (tokens.length>0 && tokens[0].compareTo("MAUVAISE")==0) {
			Resolution.mauvaise(tokens[1]);
			
		//S->C : FINRESO/
		} else if (tokens.length>0 && tokens[0].compareTo("FINRESO")==0) {
			Resolution.finReso();
			
		//S->C : TROPLONG/user/
		} else if (tokens.length>1 && tokens[0].compareTo("TROPLONG")==0) {
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
