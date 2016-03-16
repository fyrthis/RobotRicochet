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
	
	private Image wallV;
	private Image wallH;
	private Image emptySprite;

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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g)
	{
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
		for ( int xloop = 0 ; xloop < size_x ; xloop++ ){
			for ( int yloop = 0 ; yloop < size_y ; yloop++ ){
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
		
		//receives players as arg ; sort and display
		System.out.println("updating...");
		int[][] newGrid = (int[][]) arg;
		this.size_x = newGrid.length;
		this.size_y = newGrid[0].length;
		this.grid = new Tile[size_x][size_y];
		for(int i = 0; i < newGrid.length; i++){
			for(int j = 0; j < newGrid[0].length; j++){
				this.grid[i][j] = new Tile(i, j, newGrid[i][j]);
			}
		}

		System.out.println("END updating");
		//placeColorRoles();
		readSprites();
		System.out.println("repaint");
		this.repaint();
		System.out.println("revalidate");
		revalidate();
	}
	
}
