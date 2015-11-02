import java.lang.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

class Setting {
	public static boolean auto=false;
	public static boolean asciiart=true;
}
public class POOCasino{
	/**	Play the game.
	*	main function is here.
	*/
	private static String name;
	public static void main(String [] argv){
		int n;
		gameStart();
		for(n=1;round(n);n++){}
		gameEnd(n-1);
	}
	public static void gameStart(){
		System.out.println("POOCasino Jacks or better, written by b02902009 Yu-Wei Huang");
		System.out.print("Please enter your name: ");
		name = Player.getName();
		System.out.println("Welcome, "+name+".");
		Computer.initP();
		Computer.initDeck();
	}
	public static boolean round(int n){
		System.out.println("You have "+Computer.getP()+" P-dollars now.");
		Computer.shuffleDeck();
		int bet = Player.getBet(n);
		if(bet == 0)return false;
		Card[] desk = Computer.finalDesk(Player.chooseCard(Computer.initDesk()));
		Player.result(desk, Computer.bestHand(), bet);
		return true;
		}
	public static void gameEnd(int n){
		System.out.println("Good bye, "+name+". You played for "+n+" round"+((n>1)?"s":"")+" and have "+Computer.getP()+" P-dollars now.");
	}
}

class Player{
	private static String[] hands={"royal flush","straight flush","four of a kind","full house","flush","straight","three of a kind","two pair","Jacks or better","others"};
	private static int[][] payoff={{250,50,25,9,6,4,3,2,1,0},{500,100,50,18,12,8,6,4,2,0},{750,150,75,27,18,12,9,6,3,0},{1000,200,100,36,24,16,12,8,4,0},{4000,250,125,45,30,30,15,10,5,0}};
	public static String getName(){
		if(Setting.auto)return "";
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}
	public static int getBet(int n){
		if(Setting.auto)return 3;
		Scanner scanner = new Scanner(System.in);
		int tmp=-1;
		while(true){
			System.out.print("Please enter your P-dollar bet for round "+n+" (1-5 or 0 for quitting the game): ");
			try{
				tmp = Integer.parseInt(scanner.nextLine());
				if(tmp<6&&tmp>-1)break;
			} catch (NumberFormatException e){
				continue;
			}
		}
		return tmp;
	}
	public static int[] chooseCard(Card[] desk){
		if(Setting.auto)return new int[0];
		Scanner scanner = new Scanner(System.in);
		boolean[] tmp = new boolean[5];
		int[] ret;
		int n = 0;
		for (int i=0 ; i<5 ; i++)
			tmp[i] = true;
		String stmp;

		System.out.print("Your cards are");
		for (int i=0 ; i<5 ; i++)
			System.out.print(" ("+(char)('a'+i)+") "+desk[i].printCard());
		System.out.print("\nWhich cards do you want to keep? ");
		stmp = scanner.nextLine();
		for (int i=0 ; i<stmp.length() ; i++){
			int c = (int)(stmp.charAt(i)-'a');
			if(c>4||c<0)continue;
			else{
				if(tmp[c]){
					tmp[c] = false;
					n++;
				}
			}
		}
		ret = new int[n];
		System.out.print("Okay. I will discard");
		for (int i=0 ; i<5 ; i++){
			if(tmp[i]){
				System.out.print(" ("+(char)('a'+i)+") "+desk[i].printCard());
			}
			else{
				ret[ret.length-n] = i;
				n--;
			}
		}
		System.out.println();
		return ret;
	}
	public static void result(Card[] desk, int hand, int bet){
		Computer.setP(payoff[bet-1][hand]+Computer.getP());
		System.out.print("Your new cards are");
		for (int i=0 ; i<5 ; i++)
			System.out.print(" "+desk[i].printCard());
		System.out.println("\nYou get a "+hands[hand]+" hand. The payoff is "+payoff[bet-1][hand]);
	}
}

class Computer{
	private static int p;
	private static Card[] deck;
	private static Card[] desk;
	private static Shuffler shuffler;
	public static int getP(){return p;}
	public static void setP(int n){p=n;}
	public static void initP(){p=1000;}
	public static void initDeck(){
		deck = new Card [52];
		for (int i=0 ; i<4 ; i++)
			for (int j=0 ; j<13 ; j++)
				deck[i*13+j] = new Card(i, j);
	}
	public static void shuffleDeck(){
		shuffler = new Shuffler();
		shuffler.setSize(52);
	}
	public static Card [] getDeck(int n){
		Card[] temp = new Card[n];
		for (int i=0 ; i<n ; i++)
			temp[i] = deck[shuffler.getNext()];
		return temp;
	}
	public static Card[] initDesk(){
		desk = getDeck(5);
		return desk;
	}
	public static Card[] finalDesk(int[] keep){
		if(keep.length==0)return desk;
		Card[] tmp = new Card[5];
		for (int i=0 ; i<keep.length ; i++)
			tmp[i] = desk[keep[i]];
		for (int i=keep.length ; i<5 ; i++)
			tmp[i] = deck[shuffler.getNext()];
		System.arraycopy(tmp, 0, desk, 0, tmp.length);
		return tmp;
	}
	public static void sortDesk(){
		Arrays.sort(desk, new Comparator<Card>() {
			public int compare(Card s1, Card s2) {
				return Card.compareCards(s1, s2);
			}
		});
	}
	public static int bestHand(){
		sortDesk();
		int s = -2;
		int n = -1;
		boolean p = true;
		for (int i=1 ; i<5 ; i++){
			if(desk[i].getSuit()!=desk[i-1].getSuit())p=false;
			if(s<0&&desk[i].getRank()-desk[i-1].getRank()!=1&&!(i==1&&desk[i].getRank()==9&&desk[i-1].getRank()==0)){
				s*= -1;
				n*= -1;
			}
			if(desk[i].getRank()==desk[i-1].getRank()){
				s+=n;
			}
			else if(s*s>4)n = s;
		}
		switch(s){
			case -2:
				if(desk[0].getRank()==0&&desk[4].getRank()==12&&p)return 0;
				if(p)return 1;
				return 5;
			case 5:
				return 2;
			case 8:
			case 9:
				return 3;
			case 4:
				return 6;
			case 6:
				return 7;
			case 3: 
				for (int i=1 ; i<5 ; i++){
					if((desk[i].getRank()==0||(desk[i].getRank()<13&&desk[i].getRank()>9))&&
					(desk[i-1].getRank()==desk[i].getRank()))
						return 8;
				}
			case 2:
				if(p)return 4;
				return 9;
			default:
				return -1;
		}
	}
}

class Card{
	private static String[] suits = {"C", "D", "H", "S"};
	private static String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
	private int suit;
	private int rank;
	public Card(int s, int r){
		suit = s; rank = r;
	}
	public String printCard(){return suits[suit]+ranks[rank];}
	public int getSuit(){return suit;}
	public int getRank(){return rank;}
	public static int compareCards(Card s1, Card s2){
		return s1.rank==s2.rank?(s1.suit-s2.suit):(s1.rank-s2.rank);
	}
}
