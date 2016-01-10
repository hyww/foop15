import java.lang.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
		int[] bets = {0, 0, 0, 0};
		Hand dealer;
		Hand[] hands = new Hand[4];
		// (1) make a bet
		for(int i = 0 ; i < 4 ; i++){
			if(player[i] == null)continue;
			try{
				bets[i] = player[i].make_bet(last_table, total_player, i);
				if(bets[i] <= 0){
					System.out.println("Round "+round+"\tPlayer"+(i+1)+" bets <0 chips and is out of the game.");
					player[i] = null;
					continue;
				}
				player[i].decrease_chips(bets[i]);
				chips[i]-= bets[i];
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" bets "+bets[i]+" chips.");
			}
			catch(Player.BrokeException e){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" bets <0 chips and is out of the game.");
				player[i] = null;
				continue;
			}
			catch(Player.NegativeException e){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
				player[i] = null;
			}
			if(chips[i] < 0){
				System.out.println("Round "+round+"\tPlayer"+(i+1)+" is broke and out of the game.");
				chips[i] = 0;
			}
		}
		// (2) assign card to player and dealer
	}
}
