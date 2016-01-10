import foop.*;
class PlayerB02902009 extends Player{
	private static void printTable(java.util.ArrayList<Hand> table){
		for(int i = 0 ; i < table.size() ; i++){
			for(int j = 0 ; j < table.get(i).getCards().size() ; j++){
				System.out.println("Hand "+i+"\tCard "+j+" "+table.get(i).getCards().get(j).getSuit()+" "+table.get(i).getCards().get(j).getValue());
			}
		}
	}
	private static int bet;
	public PlayerB02902009(int chips){
		super(chips);
	}
	@Override
	public boolean buy_insurance(Card my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		return false;
	}
	@Override
	public boolean do_double(Hand my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		if(get_chips()<bet+1)return false;
		if(my_open.getCards().size() != 2)return false;
		int a = my_open.getCards().get(0).getValue();
		int b = my_open.getCards().get(1).getValue();
		int tmp;
		int d = dealer_open.getValue();
		if(b == 1){
			tmp = a;
			a = b;
			b = tmp;
		}

		//with ace
		if(a == 1){
			if(b >= 2 && b <= 7 && d >= 4 && d<= 6)return true;
			if(b == 6 && d >= 2 && d <= 3)return true;
			if(b == 7 && d == 3)return true;
			if(b == 8 && d == 6)return true;
			return false;
		}

		//with pair
		if(a == b && b == 5 && d < 10 && d != 1)return true;
		if(a == b && b == 4 &&(d == 5 || d == 6))return true;

		// else
		int t = a+b;	// hard total
		if(t >= 9 && t <= 11 && d >= 2 && d <= 6)return true;
		if(t >= 10 && t <= 11 && d >= 7 && d <= 9)return true;
		if(t == 8 && d == 5)return true;
		if(t == 11 && (d >= 10 || d == 1))return true;
		return false;
	}
	@Override
	public boolean do_split(java.util.ArrayList<Card> my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		if(get_chips()<bet+1)return false;
		if(my_open.size() != 2)return false;
		int a = my_open.get(0).getValue();
		int b = my_open.get(1).getValue();
		int d = dealer_open.getValue();
		if(a != b)return false;
		if(a == 1)return true;
		if(a == 2 && d >= 3 && d <= 8)return true;
		if(a == 3 && d >= 4 && d <= 7)return true;
		if(a == 8)return true; 
		if(a >= 6 && a <= 9 && d >= 2 && d <= 6)return true;
		if(a == 7 && d == 7)return true;
		if(a == 9 &&(d == 8 || d == 9))return true;
		return false;
	}
	@Override
	public boolean do_surrender(Card my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		if(my_open.getValue() == 1)return false;
		return true;
	}
	@Override
	public boolean hit_me(Hand my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
			int d = dealer_open.getValue();
		if(my_open.getCards().size() == 2){
			int a = my_open.getCards().get(0).getValue();
			int b = my_open.getCards().get(1).getValue();
			int tmp;
			if(b == 1){
				tmp = a;
				a = b;
				b = tmp;
			}

			//with ace
			if(a == 1){
				if(b >= 2 && b <= 5 && (d >= 7 || d <= 3))return true;
				if(b == 6 && (d >= 7 || d == 1))return true;
				if(b == 7 && (d >= 9 || d == 1))return true;
				return false;
			}

			//with pair
			if(a == b){
				if(b == 2 &&(d <= 2 || d >= 8))return true;
				if(b == 3 &&(d <= 3 || d >= 8))return true;
				if(b == 4 &&(d <= 4 || d >= 7))return true;
				if(b == 5 &&(d == 1 || d >= 10))return true;
				if(b == 6 &&(d == 1 || d >= 7))return true;
				if(b == 7 &&(d == 8 || d == 9 || d == 1))return true;
			}
		}
		int t = 0;	//hard total
		for(int i = 0 ; i < my_open.getCards().size() ; i++){
			if(my_open.getCards().get(i).getValue() > 10)t+= 10;
			else t+= my_open.getCards().get(i).getValue();
		}
		if(t >= 5 && t <= 7)return true;
		if(t == 8 && d != 5)return true;
		if(t == 9 &&(d == 1 || d >= 7))return true;
		if(t == 10 &&(d == 1 || d >= 10))return true;
		return false;
	}
	@Override
	public int make_bet(java.util.ArrayList<Hand> last_table, int total_player, int my_position){
		double chips = get_chips();
		bet = chips>5?5:1;
		return bet;
	}
	@Override
	public java.lang.String toString(){
		return "";
	}
}
