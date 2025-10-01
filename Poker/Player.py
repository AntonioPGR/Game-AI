from enum import Enum
from Card import Card

class PlayerFunctions(Enum):
  DEALER = 0
  SMALL_BLIND = 1
  BIG_BLIND = 2
  NONE = 3

class Player:
  
  def __init__(self, name:str, bank):
    self.name = name
    self.bank = bank
    self.hand = []
    self.function = PlayerFunctions.NONE

  def setFunction(self, function:PlayerFunctions):
    self.function = function
  
  def addToHand(self, card:Card):
    self.hand.append(card)
  
  def resetHand(self):
    self.hand = []
  
  def __str__(self):
    return f"{self.name}: ${self.bank} - {self.function.name} - {self.hand}"