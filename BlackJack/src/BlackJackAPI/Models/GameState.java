package BlackJackAPI.Models;

public record GameState(
	int playerTotal,
	boolean isSoft,
	int dealerCard
)
{}
