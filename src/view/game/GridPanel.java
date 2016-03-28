package view.game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import launcher.Debug;
import model.Model;
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

	private Image redPawnSprite;
	private Image redTargetSprite;
	
	private Image bluePawnSprite;
	private Image blueTargetSprite;
	
	private Image greenPawnSprite;
	private Image greenTargetSprite;
	
	private Image yellowPawnSprite;
	private Image yellowTargetSprite;
	
	private Image mainTargetSprite;
	
	Model model;
	
	public GridPanel(Model model){
		super();
		this.model=model;
		this.addComponentListener(this);
		setBackground(Color.gray);
		initGrid("res/BasicGrid.txt");
		readSprites();
	}
	
	public GridPanel(String filename, Model model){
		super();
		this.model=model;
		this.addComponentListener(this);
		initGrid(filename);
	}

	private void initGrid(String filename){
		//parseFile(filename);
		//placeColorRoles();
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
			
			redPawnSprite = ImageIO.read(new File("res/redPawnSprite.png"));
			bluePawnSprite = ImageIO.read(new File("res/bluePawnSprite.png"));
			greenPawnSprite = ImageIO.read(new File("res/greenPawnSprite.png"));
			yellowPawnSprite = ImageIO.read(new File("res/yellowPawnSprite.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g)
	{
		System.out.println("(Client:"+Debug.curName+")(GridPanel:paint) ---> call paint");
		Graphics2D g2 = (Graphics2D)g;
		super.paint(g2);
		
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
		if(symbolsGrid != null){
			for ( int x = 0; x < symbolsGrid[0].length; x++ ){
				for ( int y = 0; y < symbolsGrid.length; y++){
					int targetMargin = (spriteLength-targetSpriteLength)/2;
					if(this.symbolsGrid[x][y] != null && this.symbolsGrid[x][y].getValue() != 0){
						System.out.print("(Client:"+Debug.curName+")(GridPanel:paint) ["+x+","+y+"] = "+this.symbolsGrid[x][y] + " | ");
						switch(this.symbolsGrid[x][y].getValue()){
						// TARGET
						case 99:
							g2.drawImage(mainTargetSprite, spriteLength*x+targetMargin, spriteLength*y+targetMargin, targetSpriteLength, targetSpriteLength, this);
							break;
						// ROUGE
						case 21:
							g2.drawImage(redPawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 22:
							g2.drawImage(redTargetSprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						// BLEU
						case 31:
							g2.drawImage(bluePawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 32:
							g2.drawImage(blueTargetSprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						// VERT
						case 41:
							g2.drawImage(greenPawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 42:
							g2.drawImage(greenTargetSprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						// JAUNE
						case 51:
							g2.drawImage(yellowPawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 52:
							g2.drawImage(yellowTargetSprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						default:;
						}
					}
				}
			}
		}

		System.out.println("(Client:"+Debug.curName+")(GridPanel:paint)---> end call paint");
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
		System.out.println("(Client:"+Debug.curName+")(GridPanel:update)updating...");
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
		
		System.out.println("AFTER function update... ");
		if(newGrid[1] != null){
			for(int ligne = 0; ligne < newGrid[1][0].length; ligne++){
				for(int colonne = 0; colonne < newGrid[1].length; colonne++){
					System.out.print(this.symbolsGrid[colonne][ligne].getValue()+" ");
				}
				System.out.println();
			}
		}

		System.out.println("(Client:"+Debug.curName+")(GridPanel:update) END updating");
		//placeColorRoles();
		readSprites();
		this.repaint();
		revalidate();
	}
	
}
