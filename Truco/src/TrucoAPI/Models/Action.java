package TrucoAPI.Models;

import TrucoAPI.Enums.PlayerAction;

public class Action {

	PlayerAction action;
	int cardIdx = -1;

	public Action(PlayerAction action){
		action = action;
	}

	public Action(PlayerAction action, int cardIdx){
		if(action != PlayerAction.PLAY_CARD) throw new IllegalArgumentException("Invalid action");
		this.action = action;
		this.cardIdx = cardIdx;
	}

	public PlayerAction getAction() {
		return action;
	}

	public static Action fold(){
		return new Action(PlayerAction.FOLD);
	}

	public static Action playCard(int cardIdx){
		return new Action(PlayerAction.PLAY_CARD, cardIdx);
	}

	public static Action callTruco(){
		return new Action(PlayerAction.CALL_TRUCO);
	}

}
