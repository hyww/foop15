import java.lang.*;
import java.lang.reflect.Constructor;
import foop.*;

public class POOCasino{
	/**	Play the game.
	*	main function is here.
	*/
	public static void main(String [] argv){
		if(argv.length != 6)
			System.out.println("USAGE: java POOCasino <nRound> <nChip> <Player1> <Player2> <Player3> <Player4>");

		int i = 0;
		try{
			Class<?> c = null;
			Constructor constructor = null;
			Player[] player = new Player[4];
			for(i = 0 ; i < 4 ; i++){
				c = Class.forName(argv[2+i]);
				constructor = c.getConstructor(Integer.TYPE);
				player[i] = (Player)constructor.newInstance(0);
				//player[i] = (Player)Class.forName(argv[2]).newInstance(0);
			}
		}
		catch(ClassNotFoundException e){
			System.out.println("Unknown Player Class \""+argv[2+i]+"\".");
		}
		catch(Exception e){
			System.out.println("Unknown Error.");
		}
	}
}

