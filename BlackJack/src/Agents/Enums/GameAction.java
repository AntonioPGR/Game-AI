package Agents.Enums;

import BlackJackAPI.Models.GameState;

public record GameAction(
	GameState state,
	int action
) {
}
