package com.Guess_Who.storage;

import java.util.HashMap;
import java.util.Map;

import com.Guess_Who.model.Game;

public class GameStorage {
	private static Map<String, Game> games;
	private static GameStorage instance;
	
	private GameStorage() {
		games = new HashMap<>();
	}
	
	// creates singleton
	public static synchronized GameStorage getInstance() {
		if (instance == null) {
			instance = new GameStorage();
		}
		return instance;
	}
	
	// return all the games
	public Map<String, Game> getGames() {
		return games;
	}
	
	public void setGame(Game game) {
		games.put(game.getGameId(), game);
	}
}
