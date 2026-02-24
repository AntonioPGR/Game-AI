package TrucoAPI.Models;

import TrucoAPI.Enums.ScoreType;

public class ScoreBoard {

	private int teamA;
	private int teamB;

	private final int maxPoints;
	private final ScoreType scoreType;

	public ScoreBoard(ScoreType scoreType, int maxPoints) {
		this.maxPoints = maxPoints;
		this.scoreType = scoreType;
		this.reset();
	}

	// PLUBLIC
	public void reset(){
		this.teamA = 0;
		this.teamB = 0;
	}

	public int getTeamAScore() {
		return teamA;
	}

	public int getTeamBScore() {
		return teamB;
	}

	public boolean finished(){
		if(scoreType == ScoreType.BEST_OF){
			return teamA > (maxPoints/2) || teamB > (maxPoints/2);
		}
		return teamA >= maxPoints || teamB >= maxPoints;
	}

	public int getWinner(){
		if(!finished()) return 0;
		if(teamA > teamB) return 1;
		return 0;
	}

	public void scoreTeamA(int pontos) {
		validPoints(pontos);
		teamA += pontos;
	}

	public void scoreTeamB(int pontos) {
		validPoints(pontos);
		teamB += pontos;
	}

	// PRIVATE
	private void validPoints(int pontos) {
		if (pontos <= 0)
			throw new IllegalArgumentException("Pontuação inválida");
	}

}
