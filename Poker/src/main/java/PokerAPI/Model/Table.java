package PokerAPI.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {

	private List<Card> table = new ArrayList<>();

	public void clear(){
		table.clear();
	}

	public void add(Card card){
		table.add(card);
	}

	public boolean isEmpty(){return table.isEmpty();}

	public List<Card> getCards(){
		return Collections.unmodifiableList(table);
	}

}
