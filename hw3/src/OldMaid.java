import java.lang.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
public class OldMaid{
	/**	Play the game.
	*	main function is here.
	*/
	public static String[] suits = {"C", "D", "H", "S", "R", "B"};
	public static String[] ranks = {"0", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
	private String[] cards;
	private int dealt;
	public int[] deals;
	public String Name(){
		return "Original";
	}
	public void setCards(int n, String s){
		cards[n] = s;
	}
	public void InitCard(int n){
		cards = new String [n];
		dealt = 0;
	}
	public void Init(){
		deals = new int[]{14,14,13,13};
		InitCard(54);
		setCards(0, "R0");
		setCards(1, "B0");
		int i = 2;
		for (int j = 0 ; j < 4 ; j++){
			for (int k = 1 ; k < 14 ; k++){
				setCards(i, suits[j] + ranks[k]);
				//System.out.println(i+" "+j+" "+k);
				i++;
			}
		}
		Shuffle();
	}
	public void Shuffle(){
		Collections.shuffle(Arrays.asList(cards));
	}
	public String[] Deal(int n){
		int start = dealt;
		dealt+= n;
		return Arrays.copyOfRange(cards, start, dealt);
	}
	public static void main(String [] argv){
		OldMaid game;
		if(argv.length<1||argv[0].equals("0"))
			game = new OldMaid();
		else if(argv[0].equals("1"))
			game = new VariantOne();
		else
			game = new VariantTwo();
		game.Play();
	}
	public void Play(){
		System.out.println("Old Maid - "+Name());
		System.out.println("Deal cards");
		Init();
		Player[] player = new Player[]{new Player(0), new Player(1), new Player(2), new Player(3)};
		player[0].init(Deal(deals[0]));
		player[1].init(Deal(deals[1]));
		player[2].init(Deal(deals[2]));
		player[3].init(Deal(deals[3]));
		System.out.println("Drop cards");
		for(int i = 0 ; i < 4 ; i++) player[i].drop();
		System.out.println("Game start");
		int j, i = 0;
		while(true){
			while(player[i%4].out)i++;
			i %= 4;
			j = i + 1;
			while(player[j%4].out)j++;
			j %= 4;
			if(i == j){
				break;
			}
			player[i].draw(player[j].drawn(), j);
			player[i].drop();
			player[j].drop();
			player[i].won(j, player[j].win());
			i++;
		}
		while(player[i%4].out)i++;
		End(i%4);
	}
	public void End(int n){
		System.out.println("Player"+n+" loses");
		System.out.println("Game over");
	}
}

class VariantOne extends OldMaid{
	public String Name(){
		return "Ungguy-Unggyuan";
	}
	public void Init(){
		deals = new int[]{13,13,13,12};
		InitCard(52);
		int i = 0;
		for (int j = 0 ; j < 4 ; j++){
			for (int k = 1 ; k < 14 ; k++){
				setCards(i, suits[j] + ranks[k]);
				i++;
			}
		}
		Shuffle();
	}
}
class VariantTwo extends OldMaid{
	public String Name(){
		return "Scabby Queen";
	}
	public void Init(){
		deals = new int[]{13,13,13,12};
		InitCard(51);
		int i = 0;
		for (int j = 0 ; j < 4 ; j++){
			for (int k = 1 ; k < 14 ; k++){
				if(k==11&&j==0)continue;
				setCards(i, suits[j] + ranks[k]);
				i++;
			}
		}
		Shuffle();
	}
	public void End(int n){
		Init();
		String punish = Deal(1)[0];
		int times = Arrays.asList(ranks).indexOf(punish.substring(1))+1;
		if(times == 11||times == 13)times = 10;
		else if(times == 12)times = 21;
		else if(times == 14)times = 11;
		System.out.println("Player"+n+" loses");
		if(punish.substring(0,1).equals("H")||punish.substring(0,1).equals("D")){
			System.out.println("Player"+n+" is rapped on the back of the hand with the deck "+times+" times");
		}
		else
			System.out.println("Player"+n+" has the entire deck scraped across his/her knuckles "+times+" times");
		System.out.println("Game over");
	}
}
