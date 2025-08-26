#include <bits/stdc++.h>

using namespace std;

bool IS_X_TURN = true;
int N_COLS = 7, N_ROWS = 6;
int MAX_DEPTH = 12;
vector<vector<char>> board(N_ROWS, vector<char>(N_COLS, ' '));
struct {
  int col;
  int row;
  char player;
} LAST_PLAY;

void displayLine(){
  for(int i = 0; i < 29; i++) cout << "-";
  cout << endl;
}

void displayCenter(string str){
  for(int i = 0; i < ((29 - str.size()) / 2); i++) cout << " ";
  cout << str << endl;
}

void displayBoard(string message = "", string warning = ""){
  system("cls");
  displayLine();
  displayCenter("CONNECT 4");
  displayLine();
  cout << endl;
  for(vector<char> row : board){
    cout << "|";
    for(char square : row) cout << " " << square << " |";
    cout << endl << "|";
    for(int j = 0; j < 7; j++) {
      for(int k = 0; k < 3; k++) cout << "-";
      cout << "|";
    };
    cout << endl;
  }
  cout << endl << " ";
  for(int i = 1; i < 8; i++){
    cout << " " << i << "  ";
  }
  cout << endl << endl;
  if(warning != "") {
    displayLine();
    displayCenter(warning);
  }
  if(message != "") {
    displayLine();
    cout << message;
  }
}

// X = 1, O = -1, DRAW = 0
int getWinner(){
  char player = LAST_PLAY.player;
  int row = LAST_PLAY.row;
  int col = LAST_PLAY.col;
  int amount;
  // HORIZONTAL
  amount = 0;
  for(int i = 0; i < N_COLS; i++){
    if(board[row][i] == player) amount++;
    else amount = 0;
    if(amount == 4) return player == 'X' ? 1 : -1;
  }

  // VERTICAL
  amount = 0;
  for(int i = 0; i < N_ROWS; i++){
    if(board[i][col] == player) amount++;
    else amount = 0;
    if(amount == 4) return player == 'X' ? 1 : -1;
  }
  // DIAGONAL TOP-LEFT to BOTTOM-RIGHT
  amount = 0;
  int startRow = row, startCol = col;
  while (startRow > 0 && startCol > 0) {
    startRow--;
    startCol--;
  }
  while (startRow < N_ROWS && startCol < N_COLS) {
    if (board[startRow][startCol] == player) amount++;
    else amount = 0;
    if (amount == 4) return player == 'X' ? 1 : -1;
    startRow++;
    startCol++;
  }
  // DIAGONAL TOP-RIGHT to BOTTOM-LEFT
  amount = 0;
  startRow = row;
  startCol = col;
  while (startRow > 0 && startCol < N_COLS - 1) {
    startRow--;
    startCol++;
  }
  while (startRow < N_ROWS && startCol >= 0) {
    if (board[startRow][startCol] == player) amount++;
    else amount = 0;
    if (amount == 4) return player == 'X' ? 1 : -1;
    startRow++;
    startCol--;
  }
  return 0;
}

bool isGameOver(){
  if(getWinner() != 0) return true;
  for(vector<char> row : board){
    for(char square : row) if(square == ' ') return false;
  }
  return true;
}

int getColumnNextRow(int col){
  int row = -1;
  while(row < N_ROWS - 1 && board[row+1][col] == ' ') row++;
  return row;
}

bool isColumnPlayable(int col){
  if(col < 0 || col > 6) return false;
  if(getColumnNextRow(col) == -1) return false;
  return true;
}

void playInColumn(int col){
  int row = getColumnNextRow(col);
  board[row][col] = IS_X_TURN? 'X' : 'O';
  LAST_PLAY.col = col;
  LAST_PLAY.row = row;
  LAST_PLAY.player = board[row][col];
}

int scoreWindow(vector<char>& window) {
  int x_count = 0;
  int o_count = 0;
  int empty_count = 0;
  for (char c : window) {
    if (c == 'X') x_count++;
    else if (c == 'O') o_count++;
    else empty_count++;
  }
  int score = 0;
  if ((x_count == 3 || o_count == 3) && empty_count == 1) score = 100;
  else if ((x_count == 2 || o_count == 2) && empty_count == 2) score = 30;
  return x_count > o_count? score : -score;
}

int evaluateBoard(int depth) {
  if(isGameOver()){
    int winner = getWinner();
    if(winner == 1) return 30 - depth;
    if(winner == -1) return depth - 30;
    return 0;
  }
  int score = 0;
  int centerCol = N_COLS / 2;
  int centerCount = 0;
  for (int i = 0; i < N_ROWS; i++) {
    if(board[i][centerCol] == 'X') centerCount++;
    else if(board[i][centerCol] == 'O') centerCount--; 
  }
  score += centerCount * 3;
  // Horizontal
  for (int row = 0; row < N_ROWS; row++) {
    for (int col = 0; col < N_COLS - 3; col++) {
      vector<char> window = {board[row][col], board[row][col+1], board[row][col+2], board[row][col+3]};
      score += scoreWindow(window);
    }
  }
  // Vertical
  for (int col = 0; col < N_COLS; col++) {
    for (int row = 0; row < N_ROWS - 3; row++) {
      vector<char> window = {board[row][col], board[row+1][col], board[row+2][col], board[row+3][col]};
      score += scoreWindow(window);
    }
  }
  // DIAGONAL TOP-LEFT to BOTTOM-RIGHT
  for (int row = 0; row < N_ROWS - 3; row++) {
    for (int col = 0; col < N_COLS - 3; col++) {
      vector<char> window = {board[row][col], board[row+1][col+1], board[row+2][col+2], board[row+3][col+3]};
      score += scoreWindow(window);
    }
  }
  // DIAGONAL TOP-RIGHT to BOTTOM-LEFT
  for (int row = 3; row < N_ROWS; row++) {
    for (int col = 0; col < N_COLS - 3; col++) {
      vector<char> window = {board[row][col], board[row-1][col+1], board[row-2][col+2], board[row-3][col+3]};
      score += scoreWindow(window);
    }
  }
  return score;
}

int minMax(bool isXTurn, int depth = 0, int alpha = INT_MIN, int beta = INT_MAX){
  if(isGameOver() || depth == MAX_DEPTH) return evaluateBoard(depth);
  char turnPiece = isXTurn? 'X' : 'O';
  int bestValue = isXTurn? INT_MIN : INT_MAX;
  for(int col = 0; col < N_COLS; col++){
    if(isColumnPlayable(col)){
      int row = getColumnNextRow(col);
      playInColumn(col);
      int value = minMax(!isXTurn, depth+1, alpha, beta);
      board[row][col] = ' ';
      bestValue = isXTurn? max(value, bestValue) : min(value, bestValue);
      if(isXTurn) alpha = max(alpha, bestValue);
      else beta = min(beta, bestValue);
      if(beta <= alpha) break;
    }
  }
  return bestValue;
}

int calculateNextMove(){
  int bestScore = IS_X_TURN? INT_MIN : INT_MAX;
  vector<int> bestIdxs(N_COLS);
  for(int col = 0; col < N_COLS; col++){
    if(!isColumnPlayable(col)) continue;
    int row = getColumnNextRow(col);
    playInColumn(col);
    int score = minMax(!IS_X_TURN);
    board[row][col] = ' ';
    if((IS_X_TURN && score > bestScore) || (!IS_X_TURN && score < bestScore)){
      bestScore = score;
      bestIdxs.clear();  
    }
    if(score >= bestScore) bestIdxs.push_back(col);
  }
  int randomIdx = rand() % bestIdxs.size();
  return bestIdxs[randomIdx];
}

int main(){
  srand(time(NULL));

  string warning = "";
  while(!isGameOver()){
    displayBoard("Escolha a coluna: ", warning);
    warning = "";

    int col;
    if(IS_X_TURN){
      cin >> col;
      col--;
      if(!isColumnPlayable(col)){
        warning = "COLUNA INVALIDA";
        continue;
      };
    } else col = calculateNextMove();
    playInColumn(col);

    IS_X_TURN = !IS_X_TURN;
  }

  int result = getWinner();
  string text = "DRAW";
  if(result == 1) text = "X WINS";
  else if(result == -1) text = "O WINS";
  displayBoard("", text);
  displayLine();

  return 0;
}