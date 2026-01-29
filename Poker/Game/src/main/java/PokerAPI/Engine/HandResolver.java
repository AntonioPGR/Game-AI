package PokerAPI.Engine;

import PokerAPI.Enums.HandEnum;
import PokerAPI.Enums.RankEnum;
import PokerAPI.Enums.SuitEnum;
import PokerAPI.Model.Card;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandResolver {

	public HandEnum hand = null;
	public List<RankEnum> kickers;
	public final List<Card> cards;

	public HandResolver(List<Card> cards){
		if(cards.size() != 7) throw new IllegalArgumentException("Cards in a hand must be seven");
		this.cards = cards;
		evaluateHand();
	}

	public int getValue(){
		if(hand != null) return hand.getValue();
		else return 0;
	}

	private Map<RankEnum, Long> rankCount() {
		return cards.stream().collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));
	}

	private Map<SuitEnum, List<Card>> suitGroups() {
		return cards.stream().collect(Collectors.groupingBy(Card::getSuit));
	}

	private void evaluateHand() {
		var rankCount = rankCount();
		var suitGroups = suitGroups();

		// 1. Straight Flush
		for (var suitCards : suitGroups.values()) {
			if (suitCards.size() >= 5) {
				RankEnum high = getStraightHigh(suitCards);
				if (high != null){
					this.hand = HandEnum.STRAIGHT_FLUSH;
					this.kickers = List.of(high);
					return;
				}
			}
		}

		// 2. Four of a Kind
		for (var e : rankCount.entrySet()) {
			if (e.getValue() == 4) {
				RankEnum kicker = rankCount.keySet().stream()
					.filter(r -> r != e.getKey())
					.max(Comparator.naturalOrder()).get();
				this.hand = HandEnum.FOUR_OF_A_KIND;
				this.kickers = List.of(e.getKey(), kicker);
			}
		}

		// 3. Full House
		RankEnum three = rankCount.entrySet().stream()
			.filter(e -> e.getValue() == 3)
			.map(Map.Entry::getKey)
			.max(Comparator.naturalOrder())
			.orElse(null);

		if (three != null) {
			RankEnum pair = rankCount.entrySet().stream()
				.filter(e -> e.getValue() >= 2 && e.getKey() != three)
				.map(Map.Entry::getKey)
				.max(Comparator.naturalOrder())
				.orElse(null);

			if (pair != null) {
				this.hand = HandEnum.FULL_HOUSE;
				this.kickers = List.of(three, pair);
			}
		}

		// 4. Flush
		for (var suitCards : suitGroups.values()) {
			if (suitCards.size() >= 5) {
				List<RankEnum> kickers = suitCards.stream()
					.map(Card::getRank)
					.sorted(Comparator.reverseOrder())
					.limit(5)
					.toList();
				this.hand = HandEnum.FLUSH;
				this.kickers = kickers;
			}
		}

		// 5. Straight
		RankEnum straight = getStraightHigh(cards);
		if (straight != null){
			this.hand = HandEnum.STRAIGHT;
			this.kickers = List.of(straight);
		}

		// 6. Three of a kind
		if (three != null) {
			List<RankEnum> kickers = rankCount.keySet().stream()
				.filter(r -> r != three)
				.sorted(Comparator.reverseOrder())
				.limit(2)
				.toList();
			this.hand = HandEnum.THREE_OF_A_KIND;
			this.kickers = Stream.concat(Stream.of(three), kickers.stream()).toList();
		}

		// 7. Two Pair
		List<RankEnum> pairs = rankCount.entrySet().stream()
			.filter(e -> e.getValue() == 2)
			.map(Map.Entry::getKey)
			.sorted(Comparator.reverseOrder())
			.toList();
		if (pairs.size() >= 2) {
			RankEnum kicker = rankCount.keySet().stream()
					.filter(r -> !pairs.contains(r))
					.max(Comparator.naturalOrder()).get();
			this.hand = HandEnum.TWO_PAIR;
			this.kickers = List.of(pairs.get(0), pairs.get(1), kicker);
		}

		// 8. One Pair
		if (pairs.size() == 1) {
			List<RankEnum> kickers = rankCount.keySet().stream()
				.filter(r -> r != pairs.getFirst())
				.sorted(Comparator.reverseOrder())
				.limit(3)
				.toList();
			this.hand = HandEnum.ONE_PAIR;
			this.kickers = Stream.concat(Stream.of(pairs.getFirst()), kickers.stream()).toList();
		}

		// 9. High PokerAPI.Model.Card
		List<RankEnum> kickers = rankCount.keySet().stream()
			.sorted(Comparator.reverseOrder())
			.limit(5)
			.toList();
		this.hand = HandEnum.HIGH_CARD;
		this.kickers = kickers;
	}

	private RankEnum getStraightHigh(List<Card> cards) {
		List<Integer> values = cards.stream().map(c -> c.getRank().getPower()).distinct().sorted().toList();
		for (int i = values.size() - 1; i >= 4; i--) {
			boolean straight = true;
			for (int j = 0; j < 4; j++) {
				if (values.get(i - j) - 1 != values.get(i - j - 1)) {
					straight = false;
					break;
				}
			}
			if (straight) return RankEnum.fromValue(values.get(i));
		}
		if (new HashSet<>(values).containsAll(List.of(14, 2, 3, 4, 5)))
			return RankEnum.FIVE;
		return null;
	}

}
