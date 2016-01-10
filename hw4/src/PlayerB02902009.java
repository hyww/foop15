import foop.*;
class PlayerB02902009 extends Player{
	private static void printTable(java.util.ArrayList<Hand> table){
		for(int i = 0 ; i < table.size() ; i++){
			for(int j = 0 ; j < table.get(i).getCards().size() ; j++){
				System.out.println("Hand "+i+"\tCard "+j+" "+table.get(i).getCards().get(j).getSuit()+" "+table.get(i).getCards().get(j).getValue());
			}
		}
	}
	private static int basic(java.util.ArrayList<Card> p, Card d_o, java.util.ArrayList<Hand> t){
		int P = 2, D = 3, H = 5;
		int d = d_o.getValue();
		int a;
		if(p.size() == 2 && p.get(0).getValue() == p.get(1).getValue()){
			a = p.get(0).getValue();
			if(a == 1){
				if(d == 5 || d == 6)return P*D*H;
				else return P*H;
			}
			else if(a == 2){
				if(d >= 3 && d <= 7)return P*H;
				else return H;
			}
			else if(a == 3){
				if(d >= 4 && d <= 7)return P*H;
				else return H;
			}
			else if(a == 4){
				if(d >= 5 && d <= 6)return D*H;
				else return H;
			}
			else if(a == 5){
				if(d >= 10 || d == 1)return H;
				else return D*H;
			}
			else if(a == 6){
				if(d >= 7 || d == 1)return H;
				else return P;
			}
			else if(a == 7){
				if(d >= 10)return 1;
				else if(d == 7)return P*H;
				else if(d >= 8 || d == 1)return H;
				else return P;
			}
			else if(a == 8){
				if(d >= 2 && d <= 6)return P;
				else return P*H;
			}
			else if(a == 9 ){
				if(d == 7 || d >= 10 || d == 1)return 1;
				else return P;
			}
			else{
				return 1;
			}
		}
		int total = 0;
		int aces = 0;
		int hard = 1;
		for(int i = 0 ; i < p.size() ; i++){
			int v = p.get(i).getValue();
			if(v == 1){
				aces++;
				total+= 1;
			}
			else if(v > 10)total+= 10;
			else total+= v;
		}
		while(aces > 0 && total < 12){
			hard = 0;
			aces--;
			total+= 10;
		}
		if(hard == 1){
			a = total;
			if(a <= 7)return H;
			else if(a == 8){
				if(d == 5)return D*H;
				else return H;
			}
			else if(a == 9){
				if(d >= 2 && d <= 6)return D*H;
				else return H;
			}
			else if(a == 10){
				if(d >= 2 && d <= 9)return D*H;
				else return H;
			}
			else if(a == 11){
				return H;
			} 
			else if(a == 12){
				if(d == 5 || d == 6)return 1;
				else return H;
			}
			else if(a >= 13 && a <= 16){
				if(d >= 7 || d == 1)return H;
				else return 1;
			}
			else return 1;
		}
		else{
			a = total - 10;
			if(a >= 2 && a <= 5){
				if(d >= 4 && d <= 6)return D*H;
				else return H;
			}
			else if(a == 6){
				if(d >= 2 && d <= 6)return D*H;
				else return H;
			}
			else if(a == 7){
				if(d >= 3 && d <= 6)return D;
				else if(d == 2 || d == 7 || d == 8) return 1;
				else return H;
			}
			else if(a == 8 && d == 6)return D;
			else return 1;
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
		if(basic(my_open.getCards(), dealer_open, current_table)%3 == 0)return true;
		return false;
	}
	@Override
	public boolean do_split(java.util.ArrayList<Card> my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		if(basic(my_open, dealer_open, current_table)%2 == 0)return true;
		return false;
	}
	@Override
	public boolean do_surrender(Card my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		//if(dealer_open.getValue() == 1 && my_open.getValue() != 1)return true;
		return false;
	}
	@Override
	public boolean hit_me(Hand my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		if(basic(my_open.getCards(), dealer_open, current_table)%5 == 0)return true;
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
		return "Player name = PlayerB02902009, current chips = "+String.valueOf(get_chips());
	}
}
