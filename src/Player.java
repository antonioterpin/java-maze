/**
 * @author Antonio Terpin
 * @year 2016
 * 
 * This class represents the player in the maze.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Player {
	// Small square representing the player in the maze
	
	private int x, y, side;
	Color player_color;
	Polygon shape;
	Polygon finishSquare;
	
	public Player() {
		x = y = 0;
		side = 1;
		player_color = Color.BLACK;
		InitShape();
		finishSquare = new Polygon();
		finishSquare.addPoint(side, side);
		finishSquare.addPoint(2*side, side);
		finishSquare.addPoint(2*side, 2*side);
		finishSquare.addPoint(side, 2*side);
	}
	
	/**
	 * Constructor
	 * @param x x coordinate of the player
	 * @param y y coordinate of the player
	 * @param side side of the square representing the player
	 * @param player_color color of the player
	 */
	public Player(int x, int y, int side, Color player_color) {
		this.x = x;
		this.y = y;
		this.side = side;
		this.player_color = player_color;
		InitShape();
		finishSquare = new Polygon();
		finishSquare.addPoint(2*side, 2*side);
		finishSquare.addPoint(4*side, 2*side);
		finishSquare.addPoint(4*side, 4*side);
		finishSquare.addPoint(2*side, 4*side);
	}
	
	/**
	 * Copy constructor
	 * @param p player to copy
	 */
	public Player(Player p) {
		this.shape = p.shape;
		this.side = p.shape.xpoints[1]-p.shape.xpoints[0]; //x + side - x
		this.x = p.shape.xpoints[0];
		this.y = p.shape.ypoints[0];
		player_color = Color.BLACK;
	}
	
	/**
	 * Initialize the shape of the player
	 */
	private void InitShape() {
		shape = new Polygon();
		shape.addPoint(x, y);
		shape.addPoint(x + side, y);
		shape.addPoint(x + side, y + side);
		shape.addPoint(x, y + side);
	}
	
	/**
	 * Draw the player
	 * @param g graphics object
	 */
	public void Draw(Graphics g) {
		g.setColor(player_color);
		g.fillPolygon(shape);
	}
	
	/**
	 * Move the player
	 * @param inc_x increment on x
	 * @param inc_y increment on y
	 */
	public void Movement(int inc_x, int inc_y) {
		x += inc_x;
		y += inc_y;
		InitShape();
	}
	
	/**
	 * Check if the player is inside the finish square
	 * @return true if the player is inside the finish square, false otherwise
	 */
	public boolean Finished() {
		return finishSquare.contains(shape.xpoints[0], shape.ypoints[0]) &&
				finishSquare.contains(shape.xpoints[1], shape.ypoints[1]) &&
				finishSquare.contains(shape.xpoints[2], shape.ypoints[2]) &&
				finishSquare.contains(shape.xpoints[3], shape.ypoints[3]);
	}
	
	/**
	 * Set the color of the player
	 * @param c color
	 */
	public void setColor(Color c) {
		player_color = c;
	}
	
	/**
	 * Set the position of the player
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get the x coordinate of the player
	 * @return x coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the y coordinate of the player
	 * @return y coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Get the color of the player
	 * @return color
	 */
	public Color getColor() {
		return player_color;
	}
}
