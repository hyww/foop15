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
	private static void playRound(Player[] player, int[] chips, ArrayList<Hand> last_table, int round){
		int[] bet = {0, 0, 0, 0};
		boolean[] insurance = {false, false, false, false};
		boolean[] surrender = {false, false, false, false};
		Hand[] face_up = new Hand[5];	//0~3: player, 4: dealer
		Hand[] face_down = new Hand[5];

		// (1) make a bet
		for(int i = 0 ; i < 4 ; i++){
			if(player[i] == null || surrender[i])continue;
			try{
				bet[i] = player[i].make_bet(last_table, total_player, i);
				if(bet[i] < 1){
					System.out.println("Round "+round+"\tPlayer"+(i+1)+" bet <1 chips and is out of the game.");
					player[i] = null;
					continue;
				}
				player[i].decrease_chips(bet[i]);
				chips[i]-= bet[i];
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" bet "+bet[i]+" chips.");
			}
			catch(Player.NegativeException e){
				System.out.println("ERROR: Player"+(i+1)+" tries to decrease negative number of chips.");
				System.exit(1);
			}
			catch(Player.BrokeException e){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
				player[i] = null;
			}
			if(chips[i] < 0){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
				player[i] = null;
			}
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
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" bought insurance (cost"+(0.5*bet[i])+" chips).");
					}
					catch(Player.NegativeException e){
						System.out.println("ERROR: Player"+(i+1)+" tries to decrease negative number of chips.");
						System.exit(1);
					}
					catch(Player.BrokeException e){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
						player[i] = null;
					}
					if(chips[i] < 0){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
						chips[i] = 0;
					}
				}
				else System.out.println("Round "+round+"\tPlayer"+(i+1)+" didn't buy insurance.");
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
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" surrendered (got "+(0.5*bet[i])+" chips back).");
					}
					catch(Player.NegativeException e){
						System.out.println("ERROR: Player"+(i+1)+" tries to increase negative number of chips.");
						System.exit(1);
					}
					if(chips[i] < 0){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
						chips[i] = 0;
					}
				}
				else System.out.println("Round "+round+"\tPlayer"+(i+1)+" didn't surrender.");
				current_table.add(face_up[i]);
			}
		}

	}
}

