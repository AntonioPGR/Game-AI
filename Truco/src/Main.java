import TrucoAPI.GameHandler;

public class Main{
	public static void main(String[] args){

		GameHandler gameHandler = new GameHandler();
		gameHandler.startGame();
		gameHandler.printTable();
		gameHandler.startHand();
		gameHandler.printTable();

	}
}
