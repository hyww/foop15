import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
class Player{
	private static String[] suits = {"C", "D", "H", "S", "R", "B"};
	private static String[] ranks = {"0", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
	private String[] cards;
	private int id;
	public boolean out;
	public Player(int i){
		id = i;
	}
	public void init(String[] dealtCards){
		out = false;
		cards = new String[15];
		System.arraycopy(dealtCards, 0, cards, 0, dealtCards.length);
		print();
	}
	public void print(){
		sort();
		System.out.print("Player"+id+":");
		for (String s: cards){
			if(s==null)break;
			System.out.print(" "+s);
		}
		System.out.println();
	}
	public void sort(){
		Arrays.sort(cards, new Comparator<String>() {
			public int compare(String s1, String s2) {
				if(s1 == null)return 1;
				if(s2 == null)return -1;
				int i1 = Arrays.asList(ranks).indexOf(s1.substring(1));
				int i2 = Arrays.asList(ranks).indexOf(s2.substring(1));
				return (i1 - i2 == 0)?Arrays.asList(suits).indexOf(s1.substring(0,1))-Arrays.asList(suits).indexOf(s2.substring(0,1)):i1-i2;
			}
		});
	}
	public void drop(){
		sort();
		int i = 0;
		while(i + 1 < 15 && cards[i+1] != null){
			int j = i + 1;
			if(cards[i].substring(1).equals("0")){
				i++;
				continue;
			}
			while(j < cards.length && cards[j] != null && cards[j].substring(1).equals(cards[i].substring(1)))j++;
			//System.out.println(id+" "+cards[i]+" "+cards[j]);
			switch(j-i){
				case 1:
					i++;
					break;
				case 4:
					cards[i+3] = null;
					cards[i+2] = null;
				case 3:
				case 2:
					cards[i] = null;
					cards[i+1] = null;
					break;
			}
			i = j;
		}
		print();
	}
	public void draw(String card, int from){
		System.out.println("Player"+id+" draws a card from Player"+from+" "+card);
		for(int i = 0 ; i < cards.length ; i++){
			if(cards[i] == null){
				cards[i] = card;
				break;
			}
		}
	}
	public String drawn(){
		Random rand = new Random();
		int length = 0;
		while(cards[length]!=null)length++;
		int drawn = rand.nextInt(length);
		String drawnCard = cards[drawn];
		cards[drawn] = null;
		return drawnCard;
	}
	public boolean win(){
		if(cards[0] == null){
			out = true;
			return true;
		}
		return false;
	}
	public boolean won(int i, boolean b){
		if(win()&&b){
			int a = id<i?id:i;
			int c = id<i?i:id;
			System.out.println("Player"+a+" and Player"+c+" win");
		}
		else if(b)
			System.out.println("Player"+i+" wins");
		else if(win())
			System.out.println("Player"+id+" wins");
		return win()||b;
	}
}
