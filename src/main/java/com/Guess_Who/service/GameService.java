package com.Guess_Who.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.Guess_Who.exception.*;
import com.Guess_Who.model.Game;
import com.Guess_Who.model.GamePlay;
import com.Guess_Who.model.Player;
import com.Guess_Who.model.TicToe;
import com.Guess_Who.storage.GameStorage;
import com.Guess_Who.model.GameStatus;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameService {

	public Game createGame(Player player) {
		Game game = new Game();
		game.setBoard(new int[3][3]);
		game.setGameId(UUID.randomUUID().toString());
		game.setPlayer1(player);
		game.setStatus(GameStatus.NEW);
		GameStorage.getInstance().setGame(game);
		return game;
	}
	
	public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGameException {
		if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
			throw new InvalidParamException("Game with provided id doesn't exist.");
		}
		Game game = GameStorage.getInstance().getGames().get(gameId);
		if(game.getPlayer2() != null) {
			throw new InvalidGameException("Game has max number of players.");
		}
		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		GameStorage.getInstance().setGame(game);
		return game;
	}
	
	public Game connectToRandomGame(Player player2) throws GameNotFoundException {
		Game game = GameStorage.getInstance().getGames().values().stream()
			.filter(it->it.getStatus().equals(GameStatus.NEW))
			.findFirst().orElseThrow(()-> new GameNotFoundException("Game not found"));
		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		GameStorage.getInstance().setGame(game);
		return game;		
	}
	
	// Play in a location
	public Game gamePlay(GamePlay gamePlay) throws GameNotFoundException, InvalidGameException {
		if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
			throw new GameNotFoundException("Game not found");
		}
		
		Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
		if (game.getStatus().equals(GameStatus.FINISHED)) {
			throw new InvalidGameException("Game has already finished.");
		}
		int[][] board = game.getBoard();
		board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();
		
		Boolean winnerX = checkWinner(game.getBoard(), TicToe.X);
		Boolean winnerO = checkWinner(game.getBoard(), TicToe.O);
		
		if (winnerX) {
			game.setWinner(TicToe.X);
			game.setStatus(GameStatus.FINISHED);
		} else if (winnerO) {
			game.setWinner(TicToe.O);
			game.setStatus(GameStatus.FINISHED);
		}
		GameStorage.getInstance().setGame(game);
		
		return game;
	}
	
	// Check if a certain character wins and return true if yes
	private Boolean checkWinner(int[][] board, TicToe ticToe) {
		int[] boardArray = new int[9];
		int counterIndex = 0;
		for (int i = 0; i <board.length; i++) {
			for (int j = 0; j< board[i].length; j++) {
				boardArray[counterIndex] = board[i][j];
				counterIndex++;
			}
		}
		
		int[][] winCombinations = {{0,1,2}, {3,4,5}, {6,7,8},
									{0,3,6}, {1,4,7}, {2,5,8},
									{0,4,8}, {2,4,6}};
		
		for (int i=0; i < winCombinations.length; i++) {
			int counter = 0;
			for (int j=0; j < winCombinations[i].length; j++) {
				if (boardArray[winCombinations[i][j]] == ticToe.getValue()) {
					counter++;
					if (counter == 3) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
