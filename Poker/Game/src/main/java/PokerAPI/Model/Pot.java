package PokerAPI.Model;

import lombok.Getter;

@Getter
public class Pot {

	private int value = 0;

	public void clear(){
		this.value = 0;
	}

	public void add(int addValue){
		this.value += addValue;
	}

}
