package com.Guess_Who.controller.dto;

import com.Guess_Who.model.Player;

import lombok.Data;

@Data
public class ConnectRequest {
	private Player player;
	private String gameId;
}
