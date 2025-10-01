from enum import Enum


class CardSuit(Enum):
  DIAMONDS = 0
  SPADES = 1
  HEARTS = 2
  CLUBS = 3


class CardValue(Enum):
  TWO = 0
  THREE = 1
  FOUR = 2
  FIVE = 3
  SIX = 4
  SEVEN = 5
  EIGHT = 6
  NINE = 7
  TEN = 8
  JACK = 9
  QUEEN = 10
  KING = 11
  ACE = 12


class Card:
  def __init__(self, suit:CardSuit, value:CardValue):
    self.naipe = suit
    self.value = value
  
  def __str__(self):
    return f"{self.value.name} of {self.naipe.name}"
  
  def __repr__(self):
    return self.__str__()
    
    