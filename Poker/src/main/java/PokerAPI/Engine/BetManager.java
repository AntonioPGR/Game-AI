package PokerAPI.Engine;

import PokerAPI.Config.PokerConfig;
import PokerAPI.Enums.PlayerRole;
import PokerAPI.Enums.PlayerState;
import PokerAPI.Model.Card;
import PokerAPI.Model.Player;
import PokerAPI.Model.Pot;
import PokerAPI.Model.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter(AccessLevel.PRIVATE)
@Getter
public class BetManager {

	private final Pot pot;
	private final Table table;
	private final List<Player> allPlayers;
	private final List<Player> activePlayers;

	private int currentBet = 0;
	private int smallBlindId = -1;
	private int lastAggressorId = -1;
	private int minRaise = PokerConfig.MIN_RAISE;

	public BetManager(List<Player> allPlayers, List<Player> activePlayerIds, Pot pot, Table table) {
		this.allPlayers = allPlayers;
		this.activePlayers = activePlayerIds;
		this.pot = pot;
		this.table = table;
	}

	// PUBLIC
	public void restoreHandAttributes(){
		this.restoreStreetAttributes();
		pot.clear();
		setSmallBlindId(-1);
	}

	public void restoreStreetAttributes(){
		currentBet = 0;
		setLastAggressorId(-1);
		minRaise = PokerConfig.MIN_RAISE;
	}

	public void takeSmallBlind(int currentPlayerIdx){
		Player player = activePlayers.get(currentPlayerIdx);
		if(player.getRole() != PlayerRole.SMALL_BLIND) throw new IllegalArgumentException("O player atual não é o smallBlind");
		player.bet(PokerConfig.SMALL_BET);
		pot.add(PokerConfig.SMALL_BET);
		setCurrentBet(PokerConfig.SMALL_BET);
	}

	public void takeBigBlind(int currentPlayerIdx){
		Player player =  activePlayers.get(currentPlayerIdx);
		if(player.getRole() != PlayerRole.BIG_BLIND) throw new IllegalArgumentException("O player atual não é o BigBlind");
		player.bet(PokerConfig.BIG_BET);
		pot.add(PokerConfig.BIG_BET);
		setCurrentBet(PokerConfig.BIG_BET);
	}

	public void callBet(int currentPlayerIdx){
		Player player = activePlayers.get(currentPlayerIdx);
		if(
			getCurrentBet() == player.getCurrentBet() ||
			getCurrentBet() == 0
		) throw new IllegalArgumentException("Cannot call - Calling with same bet is checking!");
		int toCall = getCurrentBet() - player.getCurrentBet();
		if(toCall == player.getStack()) throw new IllegalArgumentException("Cannot call - Calling with all stack value requires all in!");
		player.bet(toCall);
		pot.add(toCall);
	}

	public void raiseBet(int amount, int currentPlayerIdx){
		Player player = activePlayers.get(currentPlayerIdx);
		if(amount < minRaise)
			throw new IllegalArgumentException("Amount must be at least " + minRaise);
		int toCall = currentBet - player.getCurrentBet();
		int total = toCall + amount;
		player.bet(total);
		pot.add(total);
		setCurrentBet(getCurrentBet() + amount);
		setMinRaise(currentBet);
		setLastAggressorId(activePlayers.get(currentPlayerIdx).getId());
	}

	public void allInBet(int currentPlayerIdx){
		Player player = activePlayers.get(currentPlayerIdx);
		int amount = player.getStack();
		player.goAllIn();
		pot.add(amount);
		if (player.getCurrentBet() > getCurrentBet()) {
			setCurrentBet(player.getCurrentBet());
			setLastAggressorId(activePlayers.get(currentPlayerIdx).getId());
		}
	}

	public void postSmallBlind(){
		if(smallBlindId == -1){
			setSmallBlindId(activePlayers.getFirst().getId());
		} else {
			int newSmIdx = (getSmallBlindIdx() + 1) % activePlayers.size();
			setSmallBlindId(activePlayers.get(newSmIdx).getId());
		}
	}

	public boolean allBetsMatched(){
		boolean all_bets_equal = true;
		for(Player player : activePlayers){
			if(player.getState() == PlayerState.INGAME && player.getCurrentBet() != currentBet){
				all_bets_equal = false;
				break;
			}
		}
		return all_bets_equal;
	}

	public void dealRoles(){
		for (Player player : allPlayers) player.clearRole();
		int smIdx = getSmallBlindIdx();
		activePlayers.get(smIdx).setAsSmallBlind();
		activePlayers.get((smIdx + 1) % activePlayers.size()).setAsBigBlind();
	}

	public int resolvePot(int winnersAmount){
		return pot.getValue() / winnersAmount;
	}

	public int getSmallBlindIdx() {
		for(int idx = 0; idx < activePlayers.size(); idx++){
			if(activePlayers.get(idx).getId() == smallBlindId) return idx;
		}
		return -1;
	}

	public int getLastAggressorIdx() {
		for(int idx = 0; idx < activePlayers.size(); idx++){
			if(activePlayers.get(idx).getId() == lastAggressorId) return idx;
		}
		return -1;
	}

//	void increaseSmallBlindIdx(){
//		setSmallBlindIdx((smallBlindIdx + 1) % currentPlayers.size());
//	}
//
//	void resolvePot(){
//		int WinnerIdx = getWinnerIdx(); // Consider half pot
//		currentPlayers.get(WinnerIdx).earn(pot.getValue());
//		this.startHand();
//	}
//
//	int getWinnerIdx(){
//		if(currentPlayers.size() == 1) return 0;
//
//		int bestIdx = -1;
//		int bestValue = -1;
//		for(int idx = 0; idx < currentPlayers.size(); idx++){
//			Player p = currentPlayers.get(idx);
//			List<Card> sevenCards = new ArrayList<>();
//			sevenCards.addAll(p.getHoleCards());
//			sevenCards.addAll(table.getCards());
//			int handValue = new HandResolver(sevenCards).getValue();
//			if(bestIdx == -1 || handValue > bestValue){
//				bestIdx = idx;
//				bestValue = handValue;
//			}
//		}
//
//		return bestIdx;
//	}



}
