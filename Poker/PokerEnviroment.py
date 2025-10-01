from Card import Card, CardSuit, CardValue
from Player import Player, PlayerFunctions
from random import shuffle
from typing import List

class PokerEnviroment:
  def __init__(self, players:List[Player]):
    if len(players) < 3: raise Exception("O jogo nao pode iniciar com menos de 3 jogadores")
    self.deck = self.generateDeck()
    self.players = players
    self.table = []
  
  def startGame(self):
    self.generateDeck()
    self.distributeCards()
    
    # WAIT ACTION
    self.turnNextTableCards()
    # WAIT ACTION
    self.turnNextTableCards()
    # WAIT ACTION
    self.turnNextTableCards()
    # WAIT ACTION
    self.resetGame()
  
  def generateDeck(self):
    deck = []
    for value in CardValue:
      for suit in CardSuit:
        deck.append(Card(suit, value))
    shuffle(deck)
    self.deck = deck
  
  def distributeFunctions(self):
    for i in range(0, len(self.players)):
      if i == 0: self.players[i].setFunction(PlayerFunctions.DEALER)
      elif i == 1: self.players[i].setFunction(PlayerFunctions.SMALL_BLIND)
      elif i == 2: self.players[i].setFunction(PlayerFunctions.BIG_BLIND)
      else: self.players[i].setFunction(PlayerFunctions.NONE)
  
  def distributeCards(self):
    dealer_index = -1
    for i in range(0, len(self.players)):
      if self.players[i].function == PlayerFunctions.DEALER:
        dealer_index = i
    for i in range(len(self.players)):
      player_index = (dealer_index + 1 + i) % len(self.players)
      for _ in range(0, 2):
        card = self.deck.pop(0)
        self.players[player_index].addToHand(card)
        
  def turnNextTableCards(self):
    if len(self.table) == 0:
      self.table.extend([self.deck.pop(0) for _ in range(3)])
    elif len(self.table) < 5:
      self.table.append(self.deck.pop(0))
    else:
      raise Exception("Não é possivel adicionar a mesa mais de 5 cartas")
  
  def resetGame(self):
    self.generateDeck()
    self
    self.table = []
    
  
