package view.game.map;

public class Tile {
	
	/*
	 * ## X  || GBDH
	 * =============
	 * ## 0  || 0000
	 * ## 1  || 0001
	 * ## 2  || 0010
	 * ## 3  || 0011
	 * ## 4  || 0100
	 * ## 5  || 0101
	 * ## 6  || 0110
	 * ## 7  || 0111
	 * ## 8  || 1000
	 * ## 9  || 1001
	 * ## 10 || 1010
	 * ## 11 || 1011
	 * ## 12 || 1100
	 * ## 13 || 1101
	 * ## 14 || 1110
	 * ## 15 || 1111
	 * 
	 */
	
	public int x;
	public int y;
	public int value;
	
	public Tile(int x, int y, int v){
		this.x = x;
		this.y = y;
		this.value = v;
	}
	
	public int getValue(){ return this.value; }
	
	public String toString(){
		String rslt = "";
		switch(this.value){
		case 0:
			rslt = " ";
			break;
		case 1:
			rslt = Character.toString((char) 8254);
			break;
		case 2:
			rslt = "|";
			break;
		case 3:
			rslt = "⅂";
			break;
		case 4:
			rslt = "_";
			break;
		case 5:
			rslt = "=";
			break;
		case 6:
			rslt = "⅃";
			break;
		case 7:
			rslt = "X";
			break;
		case 8:
			rslt = "|";
			break;
		case 9:
			rslt = "Γ";
			break;
		case 10:
			rslt = "‖";
			break;
		case 11:
			rslt = "X";
			break;
		case 12:
			rslt = "L";			
			break;
		case 13:
			rslt = "X";
			break;
		case 14:
			rslt = "X";
			break;
		case 15:
			rslt = "O";
			break;
		case 21:
			rslt = "R";
			break;
		case 22:
			rslt = "R";
			break;
		case 23:
			rslt = "R";
			break;
		case 24:
			rslt = "R";
			break;
		case 25:
			rslt = "R";
			break;
		case 31:
			rslt = "B";
			break;
		case 32:
			rslt = "B";
			break;
		case 33:
			rslt = "B";
			break;
		case 34:
			rslt = "B";
			break;
		case 35:
			rslt = "B";
			break;
		case 41:
			rslt = "V";
			break;
		case 42:
			rslt = "V";
			break;
		case 43:
			rslt = "V";
			break;
		case 44:
			rslt = "V";
			break;
		case 45:
			rslt = "V";
			break;
		case 51:
			rslt = "J";
			break;
		case 52:
			rslt = "J";
			break;
		case 53:
			rslt = "J";
			break;
		case 54:
			rslt = "J";
			break;
		case 55:
			rslt = "J";
			break;
		case 99:
			rslt = "T";
			break;
		default:;
		}
		return rslt;
//		return String.valueOf(this.value);
	}

}
