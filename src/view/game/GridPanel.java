package view.game;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import model.Model;
import model.Robot;
import utils.Phase;
import view.game.map.Tile;

public class GridPanel extends JPanel implements ComponentListener, Observer {
//Component listener useful when window is resized
	
	
	private static final long serialVersionUID = 1L;
	
	private Tile[][] grid;
	private Tile[][] symbolsGrid;
	private int size_x, size_y;
	private int spriteLength;
	private int wallSpriteLength;
	private int targetSpriteLength;
	
	private Image wallV;
	private Image wallH;
	private Image emptySprite;

	private Image redRobotSprite;
	private Image redRobotRightSprite;
	private Image redRobotLeftSprite;
	private Image redRobotBackSprite;
	
	private Image blueRobotSprite;
	private Image blueRobotRightSprite;
	private Image blueRobotLeftSprite;
	private Image blueRobotBackSprite;
	
	private Image greenRobotSprite;
	private Image greenRobotRightSprite;
	private Image greenRobotLeftSprite;
	private Image greenRobotBackSprite;
	
	private Image yellowRobotSprite;
	private Image yellowRobotRightSprite;
	private Image yellowRobotLeftSprite;
	private Image yellowRobotBackSprite;
	
	private Image mainTargetSprite;
	
	int[] update = new int[4];
	boolean[] isChanged = new boolean[4];
	boolean[] isFirst = new boolean[4];
	
	Model model;
	
	
	public GridPanel(Model model){
		super();
		this.model=model;
		this.addComponentListener(this);
		readSprites();
		
		update[0] = update[1] = update[2] = update[3] = 1;
		isChanged[0] = isChanged[1] = isChanged[2] = isChanged[3] = false;
		isFirst[0] = isFirst[1] = isFirst[2] = isFirst[3] = true;
	}
	
	public GridPanel(String filename, Model model){
		super();
		this.model=model;
		this.addComponentListener(this);
	}
	
	public void readSprites(){
		try {
			emptySprite = ImageIO.read(new File("res/emptySprite.png"));
			
			wallH = ImageIO.read(new File("res/horizontalWallSprite.png"));
			wallV = ImageIO.read(new File("res/verticalWallSprite.png"));
			
			if(model.getGrid().getTarget() == 'r')
				mainTargetSprite = ImageIO.read(new File("res/redTargetSprite.png"));
			else if(model.getGrid().getTarget() == 'b')
				mainTargetSprite = ImageIO.read(new File("res/blueTargetSprite.png"));
			else if(model.getGrid().getTarget() == 'j')
				mainTargetSprite = ImageIO.read(new File("res/yellowTargetSprite.png"));
			else if(model.getGrid().getTarget() == 'v')
				mainTargetSprite = ImageIO.read(new File("res/greenTargetSprite.png"));
			else
				mainTargetSprite = ImageIO.read(new File("res/mainTargetSprite.png"));
			
			for(int updateN : update){
				switch(updateN){
				case 0:
					redRobotSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_front.png"));
					redRobotRightSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_right.png"));
					redRobotLeftSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_left.png"));
					redRobotBackSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_back.png"));
					
					blueRobotSprite = ImageIO.read(new File("res/TortankSprites/tortank_front.png"));
					blueRobotRightSprite = ImageIO.read(new File("res/TortankSprites/tortank_right.png"));
					blueRobotLeftSprite = ImageIO.read(new File("res/TortankSprites/tortank_left.png"));
					blueRobotBackSprite = ImageIO.read(new File("res/TortankSprites/tortank_back.png"));
					
					yellowRobotSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_front.png"));
					yellowRobotRightSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_right.png"));
					yellowRobotLeftSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_left.png"));
					yellowRobotBackSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_back.png"));
					
					greenRobotSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_front.png"));
					greenRobotRightSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_right.png"));
					greenRobotLeftSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_left.png"));
					greenRobotBackSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_back.png"));
					
					break;
				case 1:
					redRobotSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_front_2.png"));
					redRobotRightSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_right_2.png"));
					redRobotLeftSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_left_2.png"));
					redRobotBackSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_back_2.png"));
					
					blueRobotSprite = ImageIO.read(new File("res/TortankSprites/tortank_front_2.png"));
					blueRobotRightSprite = ImageIO.read(new File("res/TortankSprites/tortank_right_2.png"));
					blueRobotLeftSprite = ImageIO.read(new File("res/TortankSprites/tortank_left_2.png"));
					blueRobotBackSprite = ImageIO.read(new File("res/TortankSprites/tortank_back_2.png"));
					
					yellowRobotSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_front_2.png"));
					yellowRobotRightSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_right_2.png"));
					yellowRobotLeftSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_left_2.png"));
					yellowRobotBackSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_back_2.png"));
					
					greenRobotSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_front_2.png"));
					greenRobotRightSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_right_2.png"));
					greenRobotLeftSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_left_2.png"));
					greenRobotBackSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_back_2.png"));
					
					break;
				case 2:
					redRobotSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_front_3.png"));
					redRobotRightSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_right_3.png"));
					redRobotLeftSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_left_3.png"));
					redRobotBackSprite = ImageIO.read(new File("res/DracofeuSprites/dracofeu_back_3.png"));
					
					blueRobotSprite = ImageIO.read(new File("res/TortankSprites/tortank_front_3.png"));
					blueRobotRightSprite = ImageIO.read(new File("res/TortankSprites/tortank_right_3.png"));
					blueRobotLeftSprite = ImageIO.read(new File("res/TortankSprites/tortank_left_3.png"));
					blueRobotBackSprite = ImageIO.read(new File("res/TortankSprites/tortank_back_3.png"));
					
					yellowRobotSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_front_3.png"));
					yellowRobotRightSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_right_3.png"));
					yellowRobotLeftSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_left_3.png"));
					yellowRobotBackSprite = ImageIO.read(new File("res/PikachuSprites/pikachu_back_3.png"));
					
					greenRobotSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_front_3.png"));
					greenRobotRightSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_right_3.png"));
					greenRobotLeftSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_left_3.png"));
					greenRobotBackSprite = ImageIO.read(new File("res/FlorizarreSprites/florizarre_back_3.png"));
					
					break;
				default:;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		super.paint(g2);
		
		
		/****************
		 *  GRID TILES  *
		 ****************/
		
		// On remplit toute la map avec un emptySprite
		for ( int x = 0 ; x < size_x ; x++ ){
			for ( int y = 0 ; y < size_y ; y++ ){
				g2.drawImage(emptySprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
			}
		}
		// On remplit toute la bordure de la map avec des murs
		for ( int x = 0 ; x < size_x ; x++ ){
			g2.drawImage(wallH, spriteLength*x, 0, spriteLength, wallSpriteLength, this);
			g2.drawImage(wallH, spriteLength*x, spriteLength*(size_y)-wallSpriteLength, spriteLength, wallSpriteLength, this);
		}
		for ( int y = 0 ; y < size_y ; y++ ){
			g2.drawImage(wallV, 0, spriteLength*y, wallSpriteLength, spriteLength, this);
			g2.drawImage(wallV, spriteLength*(size_x)-wallSpriteLength, spriteLength*y, wallSpriteLength, spriteLength, this);
		}

		// On va remplir les murs en fonction de la valeur de grid[][]
		for ( int colonne = 0 ; colonne < size_x ; colonne++ ){
			for ( int ligne = 0 ; ligne < size_y ; ligne++ ){
				int y = spriteLength*ligne;
				int x = spriteLength*colonne;
				int side1 = spriteLength;
				int side2 = wallSpriteLength;
				switch( this.grid[colonne][ligne].getValue() ){
				case 3: //angle en haut à droite
					g2.drawImage(wallH,side1*colonne,y,side1,side2, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					break;
				case 6: //angle en bas à droite
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallH,x,side1*(ligne+1)-side2,side1,side2, this);
					break;
				case 7: //murs haut/droite/bas
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallH,x,side1*(ligne+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					break;
				case 9: //angle en haut à gauche
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				case 11: //murs gauche/haut/droite
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					g2.drawImage(wallH,x,y,side1,side2, this);
					break;
				case 12: //angle en bas à gauche
					g2.drawImage(wallH,x,side1*(ligne+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				case 13: //murs haut/gauche/bas
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallH,x,side1*(ligne+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				case 14: //murs gauche/bas/droit
					g2.drawImage(wallV,x,y,side2,side1, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallH,x,side1*(ligne+1)-side2,side1,side2, this);
					break;
				case 15: //murs partout
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallH,x,side1*(ligne+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				}
			}
		}
		
		/**********
		 *  PATH  *
		 **********/
		if(model.getGameState().getPhase() == Phase.RESOLUTION_ACTIVE 
		|| model.getGameState().getPhase() == Phase.RESOLUTION_PASSIVE){
			paintPath(g2, model.getGrid().getRedRobot());
			paintPath(g2, model.getGrid().getBlueRobot());
			paintPath(g2, model.getGrid().getGreenRobot());
			paintPath(g2, model.getGrid().getYellowRobot());
		}
		
		/******************
		 *  ROBOT/TARGET  *
		 ******************/
		
		if(symbolsGrid != null){
			for ( int x = 0; x < symbolsGrid[0].length; x++ ){
				for ( int y = 0; y < symbolsGrid.length; y++){
					int targetMargin = (spriteLength-targetSpriteLength)/2;
					if(this.symbolsGrid[x][y] != null && this.symbolsGrid[x][y].getValue() != 0){
						switch(this.symbolsGrid[x][y].getValue()){
						// TARGET
						case 99:
							g2.drawImage(mainTargetSprite, spriteLength*x+targetMargin, spriteLength*y+targetMargin, targetSpriteLength, targetSpriteLength, this);
							break;
						// ROUGE
						case 21:
							g2.drawImage(redRobotSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						// BLEU
						case 31:
							g2.drawImage(blueRobotSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						// VERT
						case 41:
							g2.drawImage(greenRobotSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						// JAUNE
						case 51:
							g2.drawImage(yellowRobotSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
							
						
						// HANDLE THE ORIENTATION
						
						// ROUGE:
						// HAUT
						case 22:
							paintAnimationUp(g2, redRobotBackSprite, x, y, spriteLength, spriteLength, 0);
							break;
						case 23:
							g2.drawImage(redRobotBackSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// DROITE
						case 24:
							paintAnimationRight(g2, redRobotRightSprite, x, y, spriteLength, spriteLength, 0);
							break;
						case 25:
							g2.drawImage(redRobotRightSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// BAS
						case 26:
							paintAnimationDown(g2, redRobotSprite, x, y, spriteLength, spriteLength, 0);
							break;
						case 27:
							g2.drawImage(redRobotSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// GAUCHE
						case 28:
							paintAnimationLeft(g2, redRobotLeftSprite, x, y, spriteLength, spriteLength, 0);
							break;
						case 29:
							g2.drawImage(redRobotLeftSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
							
						// BLEU:
						// HAUT
						case 32:
							paintAnimationUp(g2, blueRobotBackSprite, x, y, spriteLength, spriteLength, 1);
							break;
						case 33:
							g2.drawImage(blueRobotBackSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// DROITE
						case 34:
							paintAnimationRight(g2, blueRobotRightSprite, x, y, spriteLength, spriteLength, 1);
							break;
						case 35:
							g2.drawImage(blueRobotRightSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// BAS
						case 36:
							paintAnimationDown(g2, blueRobotSprite, x, y, spriteLength, spriteLength, 1);
							break;
						case 37:
							g2.drawImage(blueRobotSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// GAUCHE
						case 38:
							paintAnimationLeft(g2, blueRobotLeftSprite, x, y, spriteLength, spriteLength, 1);
							break;
						case 39:
							g2.drawImage(blueRobotLeftSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
								
						// VERT
						// HAUT
						case 42:
							paintAnimationUp(g2, greenRobotBackSprite, x, y, spriteLength, spriteLength, 2);
							break;
						case 43:
							g2.drawImage(greenRobotBackSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// DROITE
						case 44:
							paintAnimationRight(g2, greenRobotRightSprite, x, y, spriteLength, spriteLength, 2);
							break;
						case 45:
							g2.drawImage(greenRobotRightSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// BAS
						case 46:
							paintAnimationDown(g2, greenRobotSprite, x, y, spriteLength, spriteLength, 2);
							break;
						case 47:
							g2.drawImage(greenRobotSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// GAUCHE
						case 48:
							paintAnimationLeft(g2, greenRobotLeftSprite, x, y, spriteLength, spriteLength, 2);
							break;
						case 49:
							g2.drawImage(greenRobotLeftSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
							
							
						// JAUNE
						// HAUT
						case 52:
							paintAnimationUp(g2, yellowRobotBackSprite, x, y, spriteLength, spriteLength, 3);
							break;
						case 53:
							g2.drawImage(yellowRobotBackSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// DROITE
						case 54:
							paintAnimationRight(g2, yellowRobotRightSprite, x, y, spriteLength, spriteLength, 3);
							break;
						case 55:
							g2.drawImage(yellowRobotRightSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// BAS
						case 56:
							paintAnimationDown(g2, yellowRobotSprite, x, y, spriteLength, spriteLength, 3);
							break;
						case 57:
							g2.drawImage(yellowRobotSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// GAUCHE
						case 58:
							paintAnimationLeft(g2, yellowRobotLeftSprite, x, y, spriteLength, spriteLength, 3);
							break;
						case 59:
							g2.drawImage(yellowRobotLeftSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
							
						default:;
						}
					}
				}
			}
		}
	}
	
	
	
	public void paintAnimationUp(Graphics2D g2, Image sprite, int x, int y, int width, int height, int c) {
		if(update[c] == 0){
			if(isFirst[c])
				g2.drawImage(sprite,spriteLength*x,(int)(spriteLength*(y+0.5)),width,height, this);
			else
				g2.drawImage(sprite,spriteLength*x,spriteLength*y,width,height, this);
				
			if(isChanged[c])
				update[c] = 2;
			else
				update[c] = 1;
			isChanged[c] = !isChanged[c];
			isFirst[c] = !isFirst[c];
		}
		else {
			if(isFirst[c])
				g2.drawImage(sprite,spriteLength*x,(int)(spriteLength*(y+0.75)),width,height, this);
			else
				g2.drawImage(sprite,spriteLength*x,(int)(spriteLength*(y+0.25)),width,height, this);
			update[c] = 0;
		}
	}
	
	public void paintAnimationDown(Graphics2D g2, Image sprite, int x, int y, int width, int height, int c) {
		if(update[c] == 0){
			if(isFirst[c])
				g2.drawImage(sprite,spriteLength*x,(int)(spriteLength*(y-0.5)),width,height, this);
			else
				g2.drawImage(sprite,spriteLength*x,spriteLength*y,width,height, this);
			
			if(isChanged[c])
				update[c] = 2;
			else
				update[c] = 1;
			isChanged[c] = !isChanged[c];
			isFirst[c] = !isFirst[c];
		}
		else {
			if(isFirst[c])
				g2.drawImage(sprite,spriteLength*x,(int)(spriteLength*(y-0.75)),width,height, this);
			else
				g2.drawImage(sprite,spriteLength*x,(int)(spriteLength*(y-0.25)),width,height, this);
			update[c] = 0;
		}
	}
	
	public void paintAnimationLeft(Graphics2D g2, Image sprite, int x, int y, int width, int height, int c) {
		if(update[c] == 0){
			if(isFirst[c])
				g2.drawImage(sprite,(int)(spriteLength*(x+0.5)),spriteLength*y,width,height, this);
			else
				g2.drawImage(sprite,spriteLength*x,spriteLength*y,width,height, this);
			
			if(isChanged[c])
				update[c] = 2;
			else
				update[c] = 1;
			isChanged[c] = !isChanged[c];
			isFirst[c] = !isFirst[c];
		}
		else {
			if(isFirst[c])
				g2.drawImage(sprite,(int)(spriteLength*(x+0.75)),spriteLength*y,width,height, this);
			else
				g2.drawImage(sprite,(int)(spriteLength*(x+0.25)),spriteLength*y,width,height, this);
			update[c] = 0;
		}
	}
	
	public void paintAnimationRight(Graphics2D g2, Image sprite, int x, int y, int width, int height, int c) {
		if(update[c] == 0){
			if(isFirst[c])
				g2.drawImage(sprite,(int)(spriteLength*(x-0.5)),spriteLength*y,width,height, this);
			else
				g2.drawImage(sprite,spriteLength*x,spriteLength*y,width,height, this);
			
			if(isChanged[c])
				update[c] = 2;
			else
				update[c] = 1;
			isChanged[c] = !isChanged[c];
			isFirst[c] = !isFirst[c];
		}
		else {
			if(isFirst[c])
				g2.drawImage(sprite,(int)(spriteLength*(x-0.75)),spriteLength*y,width,height, this);
			else
				g2.drawImage(sprite,(int)(spriteLength*(x-0.25)),spriteLength*y,width,height, this);
			update[c] = 0;
		}
	}
	
	public void paintPath(Graphics2D g2, Robot robot) {
		ArrayList<Point> path = robot.getPath();
		if(path.size() > 1){
			for(int i_point = 0; i_point < path.size()-1; i_point++){
				Point p1 = path.get(i_point);
				Point p2 = path.get(i_point+1);
				g2.setColor(robot.getColor());
				g2.drawLine((int)((p1.x+0.5)*spriteLength), (int)((p1.y+0.5)*spriteLength),
						(int)((p2.x+0.5)*spriteLength), (int)((p2.y+0.5)*spriteLength));
			}
		}
		if(path.size() != 0)
			g2.drawLine((int)((path.get(path.size()-1).x+0.5)*spriteLength), (int)((path.get(path.size()-1).y+0.5)*spriteLength),
					(int)((robot.getX()+0.5)*spriteLength), (int)((robot.getY()+0.5)*spriteLength));
	}
	
	public String toString(){
		String rslt = "";

		for(int y = 0; y < size_y; y++){
			for(int x = 0; x < size_x; x++){
				rslt += this.grid[x][y];
				rslt += " ";
			}
			rslt += "\n";
		}
		
		return rslt;
	}

	@Override public void componentResized(ComponentEvent e) {
		spriteLength = (int) Math.min((int)(this.getSize().height/16.0), (int)(this.getSize().width/16.0));
		wallSpriteLength = (int) (spriteLength*7/32.0);
		targetSpriteLength = (int) (spriteLength*0.75);
		this.repaint();
	}

	@Override public void componentMoved(ComponentEvent e) {}
	@Override public void componentShown(ComponentEvent e) {}
	@Override public void componentHidden(ComponentEvent e) {}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//Should receives a map as arg, then computes it and displays it
		Integer[][][] newGrid = (Integer[][][]) arg;
		this.size_x = newGrid[0].length;
		this.size_y = newGrid[0][0].length;
		this.grid = new Tile[size_x][size_y];
		
		for(int ligne = 0; ligne < newGrid[0][0].length; ligne++){
			for(int colonne = 0; colonne < newGrid[0].length; colonne++){
				this.grid[colonne][ligne] = new Tile(colonne, ligne, newGrid[0][colonne][ligne]);
			}
		}

		if(newGrid[1] != null){
			this.symbolsGrid = new Tile[size_x][size_y];
			for(int ligne = 0; ligne < newGrid[1][0].length; ligne++){
				for(int colonne = 0; colonne < newGrid[1].length; colonne++){
					this.symbolsGrid[colonne][ligne] = new Tile(colonne, ligne, newGrid[1][colonne][ligne]);
				}
			}
		}
		//placeColorRoles();
		readSprites();
		this.repaint();
		revalidate();
	}
	
}
