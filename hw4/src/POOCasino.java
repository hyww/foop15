import java.lang.*;
import java.lang.reflect.Constructor;
import java.util.*;
import foop.*;

public class POOCasino{
	/**	Play the game.
	*	main function is here.
	*/
	private static int total_player;
	public static void main(String [] argv){
		if(argv.length != 6){
			System.out.println("USAGE: java POOCasino <nRound> <nChip> <Player1> <Player2> <Player3> <Player4>");
			System.exit(1);
		}

		int round = 0, chip = 0, i = 0;
		int[] chips = {0, 0, 0, 0};
		Player[] player = new Player[4];
		ArrayList<Hand> last_table = new ArrayList<Hand>();
		try{
			round = Integer.parseInt(argv[0]);
		} catch (NumberFormatException e){
			System.out.println("ERROR: round should be an integer.");
			System.exit(1);
		}
		try{
			chip = Integer.parseInt(argv[1]);
		} catch (NumberFormatException e){
			System.out.println("ERROR: nChip should be an integer.");
			System.exit(1);
		}
		try{
			Class<?> c = null;
			Constructor constructor = null;
			for(i = 0 ; i < 4 ; i++){
				chips[i] = chip;
				c = Class.forName(argv[2+i]);
				constructor = c.getConstructor(Integer.TYPE);
				player[i] = (Player)constructor.newInstance(chip);
			}
		}
		catch(ClassNotFoundException e){
			System.out.println("ERROR: Unknown Player Class \""+argv[2+i]+"\".");
			System.exit(1);
		}
		catch(Exception e){
			System.out.println("ERROR: Unknown Error Initializing Player"+(i+1)+".");
			System.exit(1);
		}
		
		total_player = 4;
		for(i = 0 ; i < round ; i++){
			playRound(player, chips, last_table, i);
		}
	}
	private static boolean Blackjack(Card a, Card b){
		if(a.getValue()==1 && b.getValue() >= 10)return true;
		if(b.getValue()==1 && a.getValue() >= 10)return true;
		return false;
	}
	private static Hand addCard(Hand h, Card c){
		ArrayList<Card> tmp;
		tmp = new ArrayList<Card>(h.getCards());
		tmp.add(c);
		return new Hand(tmp);
	}
	private static boolean busted(Hand h){
		int total = 0;
		for(int i = 0 ; i < h.getCards().size() ; i++){
			total+= h.getCards().get(i).getValue();
		}
		if(total > 21)return true;
		return false;
	}
	private static int total(Hand h){
		int total = 0;
		int aces = 0;
		for(int i = 0 ; i < h.getCards().size() ; i++){
			if(h.getCards().get(i).getValue() == 1)aces++;
			total+= h.getCards().get(i).getValue();
		}
		while(aces > 0){
			if(total > 12)return total;
			else{
				aces--;
				total+= 9;
			}
		}
		return total;
	}
	private static boolean soft17(Hand h){
		int st = total(h);
		int ht = 0;
		if(st != 17)return false;
		for(int i = 0 ; i < h.getCards().size() ; i++){
			ht+= h.getCards().get(i).getValue();
		}
		if(ht < st)return true;
		return false;
	}
	private static void playRound(Player[] player, int[] chips, ArrayList<Hand> last_table, int round){
		int[] bet = {0, 0, 0, 0};
		boolean[] insurance = {false, false, false, false};
		boolean[] surrender = {false, false, false, false};
		boolean[] split = {false, false, false, false};
		boolean[] stand = {false, false, false, false, false, false, false, false, false};
		Hand[] face_up = new Hand[9];	//0~3: player, 4: dealer, 5~8: split player
		Hand[] face_down = new Hand[5];

		// (1) make a bet
		for(int i = 0 ; i < 4 ; i++){
			if(player[i] == null || surrender[i])continue;
			try{
				bet[i] = player[i].make_bet(last_table, total_player, i);
				if(bet[i] < 1){
					System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tbet <1 chips and is out of the game.");
					player[i] = null;
					continue;
				}
				player[i].decrease_chips(bet[i]);
				chips[i]-= bet[i];
			}
			catch(Player.NegativeException e){
				System.out.println("ERROR: Player"+(i+1)+" tries to decrease negative number of chips.");
				System.exit(1);
			}
			catch(Player.BrokeException e){
				player[i] = null;
			}
			if(chips[i] < 0 || player[i] == null){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tis broke while making bet and out of the game.");
				player[i] = null;
			}
			else System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tbet "+bet[i]+" chips.");
		}

		// (2) assign cards to player and dealer
		ArrayList<Card> deck = new ArrayList<Card>();
		ArrayList<Hand> current_table = new ArrayList<Hand>();
		Card tmp;
		int cardn = 0;
		for(int i = 0 ; i < 52 ; i++){
			tmp = new Card((byte)(i/13+1), (byte)(i%13+1));
			deck.add(tmp);
			//System.out.println(tmp.getSuit()+ " "+ tmp.getValue());
		}
		Collections.shuffle(deck);
		for(int i = 0 ; i < 52 ; i++)
			System.out.println(deck.get(i).getSuit()+" "+deck.get(i).getValue());
		for(int i = 0 ; i < 5 ; i++){
			if(i != 4 && (player[i] == null || surrender[i]))continue;
			face_up[i] = new Hand(new ArrayList<Card>(deck.subList(cardn++, cardn)));
			face_down[i] = new Hand(new ArrayList<Card>(deck.subList(cardn++, cardn)));
			if(i != 4) current_table.add(face_up[i]);
			//System.out.println(face_up[i].getCards().get(0).getValue());
		}

		// (3) if dealer's faceup is ACE, ask each player whether to buy insurance of 0.5 bet
		if(face_up[4].getCards().get(0).getValue() == 1){
			System.out.println("Round "+round+"\tDealer's face-up card is ACE.");
			for(int i = 0 ; i < 4 ; i++){
				if(player[i] == null || surrender[i])continue;
				current_table.remove(current_table.indexOf(face_up[i]));
				if(player[i].buy_insurance(face_up[i].getCards().get(0), face_up[4].getCards().get(0), current_table)){
					insurance[i] = true;
					try{
						player[i].decrease_chips((double)(0.5*bet[i]));
						chips[i]-= 0.5*bet[i];
					}
					catch(Player.NegativeException e){
						System.out.println("ERROR: Player"+(i+1)+" tries to decrease negative number of chips.");
						System.exit(1);
					}
					catch(Player.BrokeException e){
						player[i] = null;
					}
					if(chips[i] < 0 || player[i] == null){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tis broke while buying insurance and out of the game.");
						player[i] = null;
					}
					else System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tbought insurance (cost "+(0.5*bet[i])+" chips).");
				}
				else System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tdidn't buy insurance.");
				current_table.add(face_up[i]);
			}
		}

		// (4) if dealer got a Blackjack, ask each player whether to surrender
		if(Blackjack(face_up[4].getCards().get(0), face_down[4].getCards().get(0))){
			System.out.println("Round "+round+"\tDealer Blackjack.");
			for(int i = 0 ; i < 4 ; i++){
				if(player[i] == null || surrender[i])continue;
				current_table.remove(current_table.indexOf(face_up[i]));
				if(player[i].do_surrender(face_up[i].getCards().get(0), face_up[4].getCards().get(0), current_table)){
					surrender[i] = true;
					try{
						player[i].increase_chips((double)(0.5*bet[i]));
						chips[i]+= 0.5*bet[i];
					}
					catch(Player.NegativeException e){
						System.out.println("ERROR: Player"+(i+1)+" tries to increase negative number of chips.");
						System.exit(1);
					}
					if(chips[i] < 0){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tis broke during surrender and out of the game.");
						player[i] = null;
					}
					else System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tsurrendered (got "+(0.5*bet[i])+" chips back).");
				}
				else System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tdidn't surrender.");
				current_table.add(face_up[i]);
			}
		}

		// (5) for each player who did not surrender: flip up, split?, double down?, hit until stand/busted
		current_table = new ArrayList<Hand>();
		for(int i = 0 ; i < 4 ; i++){
			if(player[i] == null || surrender[i])continue;
			face_up[i] = addCard(face_up[i], face_down[i].getCards().get(0));
			current_table.add(face_up[i]);
		}
		for(int i = 0 ; i < 4 ; i++){
			if(player[i] == null || surrender[i])continue;
			if(face_up[i].getCards().get(0).getValue() == face_up[i].getCards().get(1).getValue()){
				current_table.remove(current_table.indexOf(face_up[i]));
				if(player[i].do_split(face_up[i].getCards(), face_up[4].getCards().get(0), current_table)){
					try{
						player[i].decrease_chips(bet[i]);
						chips[i]-= bet[i];
					}
					catch(Player.NegativeException e){
						System.out.println("ERROR: Player"+(i+1)+" tries to decrease negative number of chips.");
						System.exit(1);
					}
					catch(Player.BrokeException e){
						player[i] = null;
					}
					if(chips[i] < 0 || player[i] == null){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tis broke during surrender and out of the game.");
						player[i] = null;
					}
					else{
						split[i] = true;
						face_up[i+5] = new Hand(new ArrayList<Card>(face_up[i].getCards().subList(0, 1)));
						face_up[i] = new Hand(new ArrayList<Card>(face_up[i].getCards().subList(1, 2)));
						System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tsplit (cost "+(bet[i])+" chips).");
					}
				}
				else System.out.println("Round "+round+"\tPlayer"+(i+1)+"\tdidn't split.");
				current_table.add(face_up[i]);
				if(split[i])current_table.add(face_up[i+5]);
			}
		}
		for(int i = 0 ; i < 9 ; i++){
			if(i == 4)continue;	//dealer
			int p = (i>4)?i-5:i;
			String h = (i>4)?"-2":"";
			if(player[p] == null || surrender[p])continue;	//broke or surrendered
			if(i > 4 && !split[p])continue;	//player p didn't split
			current_table.remove(current_table.indexOf(face_up[i]));
			if(player[p].do_double(face_up[i], face_up[4].getCards().get(0), current_table)){
				try{
					player[p].decrease_chips(bet[p]);
					chips[p]-= bet[p];
				}
				catch(Player.NegativeException e){
					System.out.println("ERROR: Player"+(p+1)+" tries to decrease negative number of chips.");
					System.exit(1);
				}
				catch(Player.BrokeException e){
					player[p] = null;
				}
				if(chips[p] < 0 || player[p] == null){
					System.out.println("Round "+round+"\tPlayer"+(p+1)+"\tis broke during double down and out of the game.");
					player[p] = null;
				}
				else{
					stand[i] = true;
					face_up[p] = addCard(face_up[i], deck.get(cardn++));
					System.out.println("Round "+round+"\tPlayer"+(p+1)+h+"\tdoubled down (cost "+(bet[p])+" chips).");
					bet[p]*= 2;
				}
			}
			else System.out.println("Round "+round+"\tPlayer"+(p+1)+h+"\tdidn't double down.");
			int hit = 0;
			while(!stand[i]){
				if(player[p].hit_me(face_up[i], face_up[4].getCards().get(0), current_table)){
					hit++;
					face_up[i] = addCard(face_up[i], deck.get(cardn++));
					if(busted(face_up[i]))break;
				}
				else stand[i] = true;
			}
			System.out.println("Round "+round+"\tPlayer"+(p+1)+h+"\thit "+hit+" times and then "+(stand[i]?"stand.":"busted."));
			current_table.add(face_up[i]);
		}

		// (6) dealer actions
		face_up[4] = addCard(face_up[4], face_down[4].getCards().get(0));
		int hit = 0;
		while(total(face_up[4])<17 || soft17(face_up[4])){
			face_up[4] = addCard(face_up[4], deck.get(cardn++));
			hit++;
		}
		if(!busted(face_up[4]))stand[4] = true;
		System.out.println("Round "+round+"\tDealer\thit "+hit+" times and then "+(stand[4]?"stand.":"busted."));
	}
}

