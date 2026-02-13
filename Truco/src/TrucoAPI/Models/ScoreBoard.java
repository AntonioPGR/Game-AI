package TrucoAPI.Models;

public class Placar {

	private int timeA;
	private int timeB;
	private final int limite;

	public Placar(int limite) {
		this.limite = limite;
		this.timeA = 0;
		this.timeB = 0;
	}

	// PLUBLIC
	public int placarTimeA() {
		return timeA;
	}

	public int placarTimeB() {
		return timeB;
	}

	public boolean terminou(){
		return timeA > limite || timeB > limite;
	}

	public int vencedor(){
		if(!terminou()) return 0;
		if(timeA > timeB) return 1;
		return 0;
	}

	public void pontuarTimeA(int pontos) {
		validarPontos(pontos);
		timeA += pontos;
	}

	public void pontuarTimeB(int pontos) {
		validarPontos(pontos);
		timeB += pontos;
	}


	// PRIVATE
	private void validarPontos(int pontos) {
		if (pontos <= 0)
			throw new IllegalArgumentException("Pontuação inválida");
	}

}
