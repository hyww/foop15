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
	public static void playRound(Player[] player, int[] chips, ArrayList<Hand> last_table, int round){
		double[] bet = {0, 0, 0, 0};
		Hand[] face_up = new Hand[5];	//0~3: player, 4: dealer
		Hand[] face_down = new Hand[5];
		// (1) make a bet
		for(int i = 0 ; i < 4 ; i++){
			if(player[i] == null)continue;
			try{
				bet[i] = player[i].make_bet(last_table, total_player, i);
				if(bet[i] <= 0){
					System.out.println("Round "+round+"\tPlayer"+(i+1)+" bet <=0 chips and is out of the game.");
					player[i] = null;
					continue;
				}
				player[i].decrease_chips(bet[i]);
				chips[i]-= bet[i];
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" bet "+bet[i]+" chips.");
			}
			catch(Player.NegativeException e){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" bet <=0 chips and is out of the game.");
				player[i] = null;
				continue;
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
			if(i != 4 && player[i] == null)continue;
			face_up[i] = new Hand(new ArrayList<Card>(deck.subList(cardn++, cardn)));
			face_down[i] = new Hand(new ArrayList<Card>(deck.subList(cardn++, cardn)));
			if(i != 4) current_table.add(face_up[i]);
			//System.out.println(face_up[i].getCards().get(0).getValue());
		}
		// (3) if dealer's faceup is ACE, ask each player whether to buy insurance of 0.5 bet
		boolean[] insurance = {false, false, false, false};
		if(face_up[4].getCards().get(0).getValue() == 1){
			for(int i = 0 ; i < 4 ; i++){
				if(player[i] == null)continue;
				current_table.remove(current_table.indexOf(face_up[i]));
				if(player[i].buy_insurance(face_up[i].getCards().get(0), face_up[4].getCards().get(0), current_table)){
					insurance[i] = true;
					try{
						player[i].decrease_chips((double)(0.5*bet[i]));
						chips[i]-= 0.5*bet[i];
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" bought insurance ("+(0.5*bet[i])+" chips).");
					}
					catch(Player.NegativeException e){
						System.out.println("Round "+round+"\tPlayer"+(i+1)+" bought insurance using <=0 chips and is out of the game.");
						player[i] = null;
						continue;
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
	}
}

