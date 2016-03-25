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
	DebutSession ds;
	DeconnexionConnexion dc;
	Enchere enc;
	Reflexion ref;
	Resolution res;

	public Controller(Model model) {
		Controller.model=model;
		ds = new DebutSession(model);
		dc = new DeconnexionConnexion(model);
		enc = new Enchere(model);
		ref = new Reflexion(model);
		res = new Resolution(model);
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
		model.getGameState().setPhase(Phase.REFLEXION);
		model.getGameState().setCurrentSolution(solutionInt);
	}
	
	public void sendBet(String name, int solutionInt){
		String solution = String.valueOf(solutionInt);
		System.out.println("(Client:"+Debug.curName+")(Controller:sendBet) sent : ENCHERE/"+name+"/"+solution+"/");
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
		System.out.println("(Client:"+Debug.curName+")(Controller:update) received : "+message);

		//S->C : BIENVENUE/user/
		if (tokens.length>1 && tokens[0].compareTo("BIENVENUE")==0) {
			dc.bienvenue(tokens[1]);

			//S->C : CONNECTE/user/
		} else if (tokens.length>1 && tokens[0].compareTo("CONNECTE")==0) {
			dc.connecte(tokens[1]);
			
			//S->C :DECONNEXION/user/
		} else if (tokens.length>1 && tokens[0].compareTo("DECONNEXION")==0) {
			dc.deconnexion(tokens[1]);
			
			//S->C : SESSION/plateau/size_x/size_y
		} else if (tokens.length>1 && tokens[0].compareTo("SESSION")==0) {
			if(tokens.length==2)
				ds.session(tokens[1]);
			else if(tokens.length > 3)
				try {
					ds.session(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				}catch(NumberFormatException e){ System.err.println("(Client:"+Debug.curName+")(Controller:update) : received wrong Session/plateau protocol"); }
		
		//S->C : VAINQUEUR/bilan/
		} else if (tokens.length>1 && tokens[0].compareTo("VAINQUEUR")==0){
			ds.vainqueur(tokens[1]);
			
		//S->C : TOUR/enigme/bilan/
		} else if (tokens.length>1 && tokens[0].compareTo("TOUR")==0){
			ref.tour(tokens[1], tokens[2]);
			
		//S->C : TUASTROUVE/
		} else if (tokens.length>0 && tokens[0].compareTo("TUASTROUVE")==0) {
			ref.tuAsTrouve();
			
		//S->C : ILATROUVE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("ILATROUVE")==0) {
			ref.ilATrouve(tokens[1], tokens[2]);
			
		//S->C : FINREFLEXION/
		} else if (tokens.length>0 && tokens[0].compareTo("FINREFLEXION")==0) {
			ref.finReflexion();
	
		//S->C : VALIDATION/
		} else if (tokens.length>0 && tokens[0].compareTo("VALIDATION")==0) {
			enc.validation();
			
		//S->C : ECHEC/user/
		} else if (tokens.length>1 && tokens[0].compareTo("ECHEC")==0) {
			enc.echec(tokens[1]);
			
		//S->C : NOUVELLEENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("NOUVELLEENCHERE")==0) {
			enc.nouvelleEnchere(tokens[1], tokens[2]);
			
		//S->C : FINENCHERE/user/coups/
		} else if (tokens.length>2 && tokens[0].compareTo("FINENCHERE")==0) {
			enc.finEnchere(tokens[1], tokens[2]);
			
		//S->C : SASOLUTION/user/deplacements/
		} else if (tokens.length>2 && tokens[0].compareTo("SASOLUTION")==0) {
			res.saSolution(tokens[1], tokens[2]);
			
		//S->C : BONNE/
		} else if (tokens.length>0 && tokens[0].compareTo("BONNE")==0) {
			res.bonne();
			
		//S->C : MAUVAISE/
		} else if (tokens.length>0 && tokens[0].compareTo("MAUVAISE")==0) {
			res.mauvaise(tokens[1]);
			
		//S->C : FINRESO/
		} else if (tokens.length>0 && tokens[0].compareTo("FINRESO")==0) {
			res.finReso();
			
		//S->C : TROPLONG/user/
		} else if (tokens.length>1 && tokens[0].compareTo("TROPLONG")==0) {
			res.tropLong(tokens[1]);
			
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
