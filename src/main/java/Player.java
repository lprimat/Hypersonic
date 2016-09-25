package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Player {
	
	public static final int BOMB_RANGE = 3;
	public static final int BOMB_TIMER = 8;
	public static final int SCAN_RANGE = 4;
		
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();
        in.nextLine();
        Game game = new Game(width, height);
        Boolean isExploded = true;
        Bomb bombToBePlaced = null;
        
        while (true) {
            initTurn(in, game);
            Players myPlayer = game.players.get(myId);
            System.err.println("My Player is : "+ myPlayer.id);
            if (isExploded) {
                bombToBePlaced = getBestBombPosition(myPlayer, game);
                isExploded = false;
            }
            
            if (bombToBePlaced.posX == myPlayer.posX && bombToBePlaced.posY == myPlayer.posY) {
            	System.out.println("BOMB " + bombToBePlaced.posX + " " + bombToBePlaced.posY);
            	isExploded = true;
            } else {
            	System.out.println("MOVE " + bombToBePlaced.posX + " " + bombToBePlaced.posY);
            }
        }
    }

    private static void initTurn(Scanner in, Game game) {
		for (int y = 0; y < game.height; y++) {
		    String row = in.nextLine();
		    for (int x = 0; x < row.length(); x++) {
		    	game.map[y][x] = row.charAt(x);                	
		    }
		}
		
		int entitiesNb = in.nextInt();
		for (int i = 0; i < entitiesNb; i++) {
		    int entityType = in.nextInt();
		    int owner = in.nextInt();
		    int x = in.nextInt();
		    int y = in.nextInt();
		    int param1 = in.nextInt();
		    int param2 = in.nextInt();
		    
		    if (entityType == 0) {
		    	System.err.println("New player created : " + owner);
		    	game.players.put(owner, new Players(owner, x, y, param1, param2));
		    } else {
		    	game.bombs.add(new Bomb(owner, x, y, param1, param2));
		    }
		}
		in.nextLine();
	}
    
	private static Bomb getBestBombPosition(Players myPlayer, Game game) {
		int maxNbBoxHit = 0;
		int bombinitX = Math.abs(myPlayer.id * 12 - 12);
		int bombinitY = Math.abs(myPlayer.id * 10 - 10);
		Bomb bombToBePlaced = new Bomb(myPlayer.id, bombinitX, bombinitY, BOMB_TIMER, BOMB_RANGE);
		for (int y = 0; y < game.height; y++) {
			for (int x = 0; x < game.width; x++) {
				int dist = Math.abs(x - myPlayer.posX) + Math.abs(y - myPlayer.posY);
				if (game.map[y][x] == '0' || 		//Bomb cannot be placed on a box
					dist > SCAN_RANGE) continue; 	//If the case is too far away from our player
				int nbBoxHit = getNbBoxHitByBomb(x, y, game);
				System.err.println("Bomb : " + x + ";" + y + " hit : " + nbBoxHit + " box");
				if (nbBoxHit > maxNbBoxHit) {
					maxNbBoxHit = nbBoxHit;
					bombToBePlaced.posX = x;
					bombToBePlaced.posY = y;
					System.err.println("New Bomb pos : " + x + ";" + y);
				}
			}
		}
		System.err.println("bombToBePlaced pos : " + bombToBePlaced.posX + ";" + bombToBePlaced.posY);
		return bombToBePlaced;
	}

	private static int getNbBoxHitByBomb(int bombX, int bombY, Game game) {
		int nbBoxHit = 0;
		System.err.println("*************");
		for (int y = 0; y < game.height; y++) {
			for (int x = 0; x < game.width; x++) {
				int dist = Math.abs(x - bombX) + Math.abs(y - bombY);
				if ((x != bombX) && (y != bombY) || 
					dist >= BOMB_RANGE) continue; //Box are not on the same X/Y of our bomb
				System.err.print(game.map[y][x]);
				nbBoxHit = game.map[y][x] == '0' ? nbBoxHit + 1 : nbBoxHit;
			}
			System.err.println();
		}
		System.err.println("*************");
		return nbBoxHit;
	}

}

class Game {
	int width;
	int height;
	char[][] map;
	Map<Integer, Players> players;
	List<Bomb> bombs;
	
	public Game (int width, int height) {
		this.width = width;
		this.height = height;
		this.map = new char[height][width];
		this.players = new HashMap<>();
		this.bombs = new ArrayList<>();
	}
}

class Entity {
	
	int entityType;
    int id;
    int posX;
    int posY;
    
    public Entity(int entityType, int owner, int x, int y) {
    	this.entityType = entityType;
    	this.id = owner; 
    	this.posX = x;
    	this.posY = y;
	}
}

class Players extends Entity {
	int nbBombs;
	int bombRange;

	public Players(int owner, int x, int y, int param1, int param2) {
		super(0, owner, x, y);
		this.nbBombs = param1;
		this.bombRange = param2;
	}
}

class Bomb extends Entity{
	int timer;
	int bombRange;
	
	public Bomb(int owner, int x, int y, int param1, int param2) {
		super(1, owner, x, y);
		this.timer = param1;
		this.bombRange = param2;
	}
}