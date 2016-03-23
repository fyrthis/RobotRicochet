package view.game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import model.Players.Player;
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
	private Image redTarget1Sprite;
	private Image redTarget2Sprite;
	private Image redTarget3Sprite;
	private Image redTarget4Sprite;
	
	private Image bluePawnSprite;
	private Image blueTarget1Sprite;
	private Image blueTarget2Sprite;
	private Image blueTarget3Sprite;
	private Image blueTarget4Sprite;
	
	private Image greenPawnSprite;
	private Image greenTarget1Sprite;
	private Image greenTarget2Sprite;
	private Image greenTarget3Sprite;
	private Image greenTarget4Sprite;
	
	private Image yellowPawnSprite;
	private Image yellowTarget1Sprite;
	private Image yellowTarget2Sprite;
	private Image yellowTarget3Sprite;
	private Image yellowTarget4Sprite;
	
	private Image mainTargetSprite;
	
	public GridPanel(){
		this.addComponentListener(this);
		setBackground(Color.gray);
		initGrid("res/BasicGrid.txt");
		readSprites();
	}
	
	public GridPanel(String filename){
		this.addComponentListener(this);
		initGrid(filename);
	}

	private void initGrid(String filename){
		//parseFile(filename);
		//placeColorRoles();
	}

	private void parseFile(String filename){
		boolean isSizeLine = true;
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int y = 0;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null){
				if(!strLine.contains("##") && !strLine.equals("END")){
					int space = strLine.indexOf(" ");
					int end = strLine.length();
					int nextSpace = strLine.indexOf(" ", space+1);
					if(isSizeLine){
						this.size_x = Integer.parseInt(strLine.substring(0, space));
						this.size_y = Integer.parseInt(strLine.substring(space+1, end));
						isSizeLine = false;

						this.grid = new Tile[this.size_x][this.size_y];
					}
					else {
						this.grid[0][y] = new Tile(0, 0, Integer.parseInt(strLine.substring(0, space)));
						for(int x = 1; x < this.size_x-1; x++){
							this.grid[x][y] = new Tile(x, y, Integer.parseInt(strLine.substring(space+1, nextSpace)));
							space = strLine.indexOf(" ", space+1);
							nextSpace = strLine.indexOf(" ", space+1);
						}
						this.grid[size_x-1][y] = new Tile(size_x-1, 0, Integer.parseInt(strLine.substring(space+1, end)));
						y++;
					}
				}
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void placeColorRoles(){
		/* ## X1 : PLAYER
		 * ## X2 : CIRCLE
		 * ## X3 : TRIANGLE
		 * ## X4 : CROSS
		 * ## X5 : PLANET
		 */
		symbolsGrid = new Tile[size_x][size_y];
		// ROUGE
		symbolsGrid[0][0] = new Tile(0, 0, 21);
		symbolsGrid[14][4] = new Tile(14, 4, 22);
		symbolsGrid[7][5] = new Tile(7, 5, 23);
		symbolsGrid[1][13] = new Tile(1, 13, 24);
		symbolsGrid[14][13] = new Tile(14, 13, 25);
		// BLEU
		symbolsGrid[1][2] = new Tile(1, 2, 31);
		symbolsGrid[6][10] = new Tile(6, 10, 32);
		symbolsGrid[13][9] = new Tile(13, 9, 33);
		symbolsGrid[5][2] = new Tile(5, 2, 34);
		symbolsGrid[9][4] = new Tile(9, 4, 35);
		// VERT
		symbolsGrid[12][5] = new Tile(12, 5, 41);
		symbolsGrid[2][4] = new Tile(2, 4, 42);
		symbolsGrid[13][1] = new Tile(13, 1, 43);
		symbolsGrid[10][14] = new Tile(10, 14, 44);
		symbolsGrid[3][14] = new Tile(3, 14, 45);
		// JAUNE
		symbolsGrid[7][4] = new Tile(7, 4, 51);
		symbolsGrid[9][11] = new Tile(9, 11, 52);
		symbolsGrid[4][9] = new Tile(4, 9, 53);
		symbolsGrid[12][6] = new Tile(12, 6, 54);
		symbolsGrid[1][6] = new Tile(1, 6, 55);
		// MIX
		symbolsGrid[7][12] = new Tile(7, 12, 88);

	}
	
	public void readSprites(){
		try {
			emptySprite = ImageIO.read(new File("res/emptySprite.png"));
			
			wallH = ImageIO.read(new File("res/horizontalWallSprite.png"));
			wallV = ImageIO.read(new File("res/verticalWallSprite.png"));
			
			mainTargetSprite = ImageIO.read(new File("res/mainTargetSprite.png"));
			
			redPawnSprite = ImageIO.read(new File("res/redPawnSprite.png"));
			redTarget1Sprite = ImageIO.read(new File("res/redTarget1Sprite.png"));
			redTarget2Sprite = ImageIO.read(new File("res/redTarget2Sprite.png"));
			redTarget3Sprite = ImageIO.read(new File("res/redTarget3Sprite.png"));
			redTarget4Sprite = ImageIO.read(new File("res/redTarget4Sprite.png"));
			
			bluePawnSprite = ImageIO.read(new File("res/bluePawnSprite.png"));
			blueTarget1Sprite = ImageIO.read(new File("res/blueTarget1Sprite.png"));
			blueTarget2Sprite = ImageIO.read(new File("res/blueTarget2Sprite.png"));
			blueTarget3Sprite = ImageIO.read(new File("res/blueTarget3Sprite.png"));
			blueTarget4Sprite = ImageIO.read(new File("res/blueTarget4Sprite.png"));
			
			greenPawnSprite = ImageIO.read(new File("res/greenPawnSprite.png"));
			greenTarget1Sprite = ImageIO.read(new File("res/greenTarget1Sprite.png"));
			greenTarget2Sprite = ImageIO.read(new File("res/greenTarget2Sprite.png"));
			greenTarget3Sprite = ImageIO.read(new File("res/greenTarget3Sprite.png"));
			greenTarget4Sprite = ImageIO.read(new File("res/greenTarget4Sprite.png"));
			
			yellowPawnSprite = ImageIO.read(new File("res/yellowPawnSprite.png"));
			yellowTarget1Sprite = ImageIO.read(new File("res/yellowTarget1Sprite.png"));
			yellowTarget2Sprite = ImageIO.read(new File("res/yellowTarget2Sprite.png"));
			yellowTarget3Sprite = ImageIO.read(new File("res/yellowTarget3Sprite.png"));
			yellowTarget4Sprite = ImageIO.read(new File("res/yellowTarget4Sprite.png"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g)
	{
		System.out.println("---> call paint");
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
		for ( int yloop = 0 ; yloop < size_y ; yloop++ ){
			for ( int xloop = 0 ; xloop < size_x ; xloop++ ){
				int y = spriteLength*yloop;
				int x = spriteLength*xloop;
				int side1 = spriteLength;
				int side2 = wallSpriteLength;
				switch( this.grid[xloop][yloop].getValue() ){
				case 3: //angle en haut à droite
					g2.drawImage(wallH,side1*xloop,y,side1,side2, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					break;
				case 6: //angle en bas à droite
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallH,x,side1*(yloop+1)-side2,side1,side2, this);
					break;
				case 7: //murs haut/droite/bas
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallH,x,side1*(yloop+1)-side2,side1,side2, this);
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
					g2.drawImage(wallH,x,side1*(yloop+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				case 13: //murs haut/gauche/bas
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallH,x,side1*(yloop+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				case 14: //murs gauche/bas/droit
					g2.drawImage(wallV,x,y,side2,side1, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallH,x,side1*(yloop+1)-side2,side1,side2, this);
					break;
				case 15: //murs partout
					g2.drawImage(wallH,x,y,side1,side2, this);
					g2.drawImage(wallV,x+side1-side2,y,side2,side1, this);
					g2.drawImage(wallH,x,side1*(yloop+1)-side2,side1,side2, this);
					g2.drawImage(wallV,x,y,side2,side1, this);
					break;
				}
			}
		}
		if(symbolsGrid != null){
			for ( int y = 0; y < symbolsGrid.length; y++){
				for ( int x = 0; x < symbolsGrid.length; x++ ){
					int targetMargin = (spriteLength-targetSpriteLength)/2;
					if(this.symbolsGrid[x][y] != null && this.symbolsGrid[x][y].getValue() != 0){
						System.out.print("["+x+","+y+"] = "+this.symbolsGrid[x][y] + " | ");
						switch(this.symbolsGrid[x][y].getValue()){
						// TARGET
						case 99:
							g2.drawImage(mainTargetSprite, spriteLength*x, spriteLength*y, spriteLength, spriteLength, this);
							break;
						// ROUGE
						case 21:
							g2.drawImage(redPawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 22:
							g2.drawImage(redTarget1Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 23:
							g2.drawImage(redTarget2Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 24:
							g2.drawImage(redTarget3Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 25:
							g2.drawImage(redTarget4Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						// BLEU
						case 31:
							g2.drawImage(bluePawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 32:
							g2.drawImage(blueTarget1Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 33:
							g2.drawImage(blueTarget2Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 34:
							g2.drawImage(blueTarget3Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 35:
							g2.drawImage(blueTarget4Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						// VERT
						case 41:
							g2.drawImage(greenPawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 42:
							g2.drawImage(greenTarget1Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 43:
							g2.drawImage(greenTarget2Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 44:
							g2.drawImage(greenTarget3Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 45:
							g2.drawImage(greenTarget4Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						// JAUNE
						case 51:
							g2.drawImage(yellowPawnSprite,spriteLength*x,spriteLength*y,spriteLength,spriteLength, this);
							break;
						case 52:
							g2.drawImage(yellowTarget1Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 53:
							g2.drawImage(yellowTarget2Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 54:
							g2.drawImage(yellowTarget3Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						case 55:
							g2.drawImage(yellowTarget4Sprite,spriteLength*x+targetMargin,spriteLength*y+targetMargin,spriteLength,spriteLength, this);
							break;
						default:;
						}
					}
				}
			}
		}

		System.out.println("---> end call paint");
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
		this.repaint();
	}

	@Override public void componentMoved(ComponentEvent e) {}
	@Override public void componentShown(ComponentEvent e) {}
	@Override public void componentHidden(ComponentEvent e) {}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//Should receives a map as arg, then computes it and displays it
		System.out.println("updating...");
		Integer[][][] newGrid = (Integer[][][]) arg;
		this.size_x = newGrid[0].length;
		this.size_y = newGrid[0][0].length;
		this.grid = new Tile[size_x][size_y];
		for(int i = 0; i < newGrid[0].length; i++){
			for(int j = 0; j < newGrid[0][0].length; j++){
				this.grid[i][j] = new Tile(i, j, newGrid[0][i][j]);
			}
		}

		if(newGrid[1] != null){
			this.symbolsGrid = new Tile[size_x][size_y];
			for(int i = 0; i < newGrid[1].length; i++){
				for(int j = 0; j < newGrid[1][1].length; j++){
					this.symbolsGrid[i][j] = new Tile(i, j, newGrid[1][i][j]);
				}
			}
		}
		System.out.println("END updating");
		//placeColorRoles();
		readSprites();
		this.repaint();
		revalidate();
	}
	
}
