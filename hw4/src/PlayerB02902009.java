import foop.*;
class PlayerB02902009 extends Player{
	public PlayerB02902009(int chips){
		super(chips);
	}
	@Override
	public boolean buy_insurance(Card my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		return false;
	}
	@Override
	public boolean do_double(Hand my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		return false;
	}
	@Override
	public boolean do_split(java.util.ArrayList<Card> my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		return false;
	}
	@Override
	public boolean do_surrender(Card my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		return false;
	}
	@Override
	public boolean hit_me(Hand my_open, Card dealer_open, java.util.ArrayList<Hand> current_table){
		return false;
	}
	@Override
	public int 	make_bet(java.util.ArrayList<Hand> last_table, int total_player, int my_position){
		return 0;
	}
	@Override
	public java.lang.String toString(){
		return "";
	}
}