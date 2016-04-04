package model;

import java.util.Observable;

public class Timer extends Observable {

	private static volatile Timer instance = null;
	static int time;

	//static Timer timer;

	private Timer(){
		// Thread qui va s'occuper de gérer le temps global pour tous les clients
		Thread t1 = new Thread(){
			public void run(){
				for(int i = 1;i <= 5000;i++) {
					updateTime();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 

			}
		};
		t1.start();

		/*
		int delay = 1000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//...Perform a task...
				updateTime();
			}
		};
		timer = new Timer(delay, taskPerformer);
		timer.start();*/
	}


	public int getTime(){ return time; }
	
	public final static Timer getInstance(){
		if(Timer.instance == null){
			synchronized (Timer.class) {
				if(Timer.instance == null)
					Timer.instance = new Timer();
			}
		}
		return Timer.instance;
	}
	
	
	// A chaque fois qu'on change la valeur du compteur time, on notify à tous les observers le nouveau gameState
	public void setTime(int t){
		Timer.time = t;
		this.setChanged();
		this.notifyObservers(Timer.time);
	}

	public void updateTime(){
		time--;
		if(time >= 0){
			this.setChanged();
			this.notifyObservers(Timer.time);
		}
	}

}
