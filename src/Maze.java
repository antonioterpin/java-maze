/**
 * @author Antonio Terpin
 * @year 2016
 * 
 * This class represents the maze in the game.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Random;
import java.util.Stack;


public class Maze {
	
	/**
	 * The maze is a matrix of cells, where each cell can be a wall or a path.
	 * The maze is built using a depth-first search algorithm, so at the 
	 * beginning each cell is isolated from the others.
	 * When a cell is connected to another, the cell in between becomes a path.
	 * The only cells that will certainly remain walls are (x+1,y+1), 
	 * from which the wall pieces will be drawn (i.e. sets of 1, 2, 3 cells).
	 * In fact, the maze is seen by this algorithm as a N*N matrix, 
	 * where each cell is composed of 4 subcells, one of which (x,y) 
	 * is a path, the others walls, at least at the beginning.
	 */
	
	private static int MAX_ATTEMPTS = 15;
	private int N; // side of the maze
	private Cell maze[][]; // matrix of cells, the maze
	private Polygon walls[][];
	private Polygon LeftTopWall;
	private int side; // side of the cell
	
	public Maze() {
		N = 3;
		side = 50;
		LeftTopWall = new Polygon();
		LeftTopWall.addPoint(0, 0);
		LeftTopWall.addPoint(2 * N * side + side + 1, 0);
		LeftTopWall.addPoint(2 * N * side + side + 1, side);
		LeftTopWall.addPoint(side, side);
		LeftTopWall.addPoint(side, 2 * N * side + side + 1);
		LeftTopWall.addPoint(0, 2 * N * side + side + 1);
	}
	
	public Maze(int N, int side) {
		this.N = N;
		this.side = side;
		LeftTopWall = new Polygon();
		LeftTopWall.addPoint(0, 0);
		LeftTopWall.addPoint(2 * N * side + side + 1, 0);
		LeftTopWall.addPoint(2 * N * side + side + 1, side);
		LeftTopWall.addPoint(side, side);
		LeftTopWall.addPoint(side, 2 * N * side + side + 1);
		LeftTopWall.addPoint(0, 2 * N * side + side + 1);
	}
	
	private int randInt(int min, int max) {
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public void Draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(
			0, 0, 2 * N * side + side, 2 * N * side + side);
		g.setColor(Color.BLACK);
		g.fillPolygon(LeftTopWall);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
//				g.drawPolygon(walls[i][j]);
				g.fillPolygon(walls[i][j]);
			}
		}
	}

	public void Build() {
		// delete and reconstruct
		maze = new Cell[2 * N][2 * N];
		walls = new Polygon[N][N];
		Stack<Cell> nodiVisitati = new Stack<Cell>();
		
		// init
		for(int i = 0; i < 2 * N; i++) {
			for(int j = 0; j < 2 * N; j++) {
				int cont = 0;
				if(i % 2 == 0 && j % 2 == 0) {
					for(int k = -1; k <= 1; k++) {
						for(int h = -1; h <= 1; h++) {
							if(k == h || k == 0-h) continue;
							if(i + (k * 2) >= 0 
								&& i + (k * 2) < 2 * N 
								&& j + (h * 2) >= 0 
								&& j + (h * 2) < 2 * N) {
								cont++;
							}
						}
					}
				}
				maze[i][j] = new Cell(
					!(i % 2 == 0 && j % 2 == 0), 
					false, i, j, cont);
			}
		}
		
		// Find the paths (DFS)
		Cell current = maze[0][0];
		
		while (true) {
			
			// Decrease counter of neighboring cells to the one visited
			// if not already visited
			if (!current.GetVisited())
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == j || i == (0 - j)) continue;
					try {
						maze[current.GetX() + (i * 2)]
							[current.GetY() + ( j * 2)].someNeighbour--;
					}
					catch (IndexOutOfBoundsException ex) {}
				}
			}
			
			current.SetVisited(true);
			
			// If there are neighbors not visited, visit one of them.
			if(current.someNeighbour > 0) {
				int x = current.GetX();
				int y = current.GetY();
				int inc_x = 0, inc_y = 0;
				int attempts = 0;
				boolean validMove = false;
						
				// random decision, avoiding infinite loops
				while (attempts < MAX_ATTEMPTS && !validMove) {
					inc_x = randInt(-1, 1);
					if (inc_x == 0) {
						inc_y = randInt(-1, 1);
						if(inc_y == 0)  inc_y = -1;
					}
					try {
						// if it is a valid index and has not already been visited, done
						maze[x + inc_x][y + inc_y].SetWall(
							maze[x + inc_x][y + inc_y].GetWall());
						// Check this is a valid attempt
						if(maze[x + (inc_x * 2)][y + (inc_y * 2)].GetVisited());

						// No exception thrown, so it is a valid
						break;
					}
					catch (IndexOutOfBoundsException ex) {}

					// An exception was thrown, continue
					attempts++;
				}
				
				// check we did not exit because of max attempts reached
				int cont = 1;
				while (!validMove) {
					// We did not find a valid move yet
					if (cont % 4 != 0) {
						if(inc_x == 1) {
							inc_x = -1;
						} else {
							inc_x++;
						}
					} else if(inc_y == 1) {
						inc_y = -1;
					} else {
						inc_y++;
					}
					cont++;
					if(!(inc_x == inc_y || inc_x == (0 - inc_y)))
					try {
						// if it is a valid index and it was not already visited we stop
						// if no exception is thrown in these instructions, we are good
						maze[x + inc_x][y + inc_y].SetWall(
							maze[x + inc_x][y + inc_y].GetWall());

						 validMove = !maze[x + (inc_x * 2)][y + (inc_y * 2)].GetVisited();
					}
					catch (IndexOutOfBoundsException ex) {}
				}
				
				nodiVisitati.push(current);
				
				// move to next cell and mark the one in between as path
				maze[x + inc_x][y + inc_y].SetWall(false);
				current = maze[x + (inc_x * 2)][y + (inc_y * 2)];
			}
			else if(!nodiVisitati.empty()) {
				current = nodiVisitati.pop();
			}
			else {
				break;
			}
		}
		
		// compute polygons
		// note: maze[x+1][y+1] is always a wall
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				walls[i][j] = new Polygon();
				// + side to shift in order to add the border
				walls[i][j].addPoint(
					(j * 2 + 1) * side + side, 
					(i * 2 + 1) * side + side);

				if(maze[i * 2][j * 2 + 1].GetWall()) {
					walls[i][j].addPoint(
						(j * 2 + 1) * side + side, 
						i * 2 * side + side);
					walls[i][j].addPoint(
						(j * 2 + 2) * side + side, 
						i * 2 * side + side);
				}
				
				walls[i][j].addPoint(
					(j * 2 + 2) * side + side, 
					(i * 2 + 1) * side + side);
				walls[i][j].addPoint(
					(j * 2 + 2) * side + side, 
					(i * 2 + 2) * side + side);
				walls[i][j].addPoint(
					(j * 2 + 1) * side + side, 
					(i * 2 + 2) * side + side);
				
				if(maze[i * 2 + 1][j * 2].GetWall()) {
					walls[i][j].addPoint(
						j * 2 * side + side, 
						(i * 2 + 2) * side + side);
					walls[i][j].addPoint(
						j * 2 * side + side, 
						(i * 2 + 1) * side + side);
				}

				// close the polygon
				walls[i][j].addPoint(
					(j * 2 + 1) * side + side, 
					(i * 2 + 1) * side + side);
			}
		}
	}

	public boolean IsColliding(Player p) {
		for (int k = 0; k < N; k++) {
			for (int j = 0; j < N; j++) {
				for (int i = 0; i < p.shape.npoints; i++) {
					if (walls[k][j].contains(p.shape.xpoints[i], p.shape.ypoints[i])) {
						return true;
					}
				}
			}
		}
		for (int i = 0; i < p.shape.npoints; i++) {
			if (LeftTopWall.contains(p.shape.xpoints[i], p.shape.ypoints[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compute the solution of the maze
	 * 
	 * Starting from the starting cells of the two players, 
	 * we depth first visit the maze.
	 */
	public void computeSolution() {
		// compute neighbors
		for (int i = 0; i < 2 * N; i++) {
			for (int j = 0; j < 2 * N; j++) {
				if (maze[i][j].GetWall()) {
					continue;
				}
				int cont = 0;
				for (int k = -1; k <= 1; k++) {
					for (int h = -1; h <= 1; h++) {
						if (k == h || k == (0-h)) {
							continue;
						}
						try {
							if (maze[i + k][j + h].GetWall() == false) {
								cont++;
							}
								
						}
						catch (IndexOutOfBoundsException ex) { }
					}
				}
				maze[i][j] = new Cell(
					maze[i][j].GetWall(), false, i, j, cont);
			}
		}
		
		
		Stack<Cell> nodiVisitati = new Stack<Cell>();
		// starting from 2 * N - 2, 2 * N - 2
		Cell current = maze[2 * N - 2][2 * N - 2];
		
		// until the exit is reached (which is granted by construction)
		while(true) {
			int x = current.GetX();
			int y = current.GetY();
			if (!current.GetVisited()) {
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						if (i == j || i == (0 - j)) {
							continue;
						}
						try {
							if (!maze[x + i][y + j].GetWall()) {
								maze[x + i][y + j].someNeighbour--;
							}
						}
						catch (IndexOutOfBoundsException ex) {}
					}
				}
			}
			
			current.SetVisited(true);
			current.isPartOfSolution = true;
			
			if (current.someNeighbour > 0) {
				int inc_x = 0, inc_y = 0;
				int attempts = 0;
				boolean validMove = false;
				
				while (attempts < MAX_ATTEMPTS && !validMove) {
					inc_x = randInt(-1, 1);
					if (inc_x == 0) {
						inc_y = randInt(-1, 1);
						if(inc_y == 0)  inc_y = -1;
					}
					try {
						// check no exception is generated 
						// to make sure the move is valid
						if (maze[x][y].GetVisited());
						break;
					}
					catch (IndexOutOfBoundsException ex) {}
					attempts++;
				}
				
				int cont = 1;
				while (!validMove) {
					if (cont % 4 != 0) {
						if (inc_x == 1) {
							inc_x = -1;
						} else {
							inc_x++;
						}
					} else if (inc_y == 1) {
							inc_y = -1;
					} else {
						inc_y++;
					}
					cont++;
					if (!(inc_x == inc_y || inc_x == (0 - inc_y)))
					try {
						if (!maze[x + inc_x][y + inc_y].GetWall()) {
							validMove = !maze[x + inc_x][y + inc_y].GetVisited();
						}
					}
					catch (IndexOutOfBoundsException ex) {}
				}
				
				nodiVisitati.push(current);
				
				current = maze[x + inc_x][y + inc_y];
			} else if (current != maze[0][0] && !nodiVisitati.isEmpty()) {
				current.isPartOfSolution = false;
				current = nodiVisitati.pop();
			} else {
				break;
			}
		}
	}

	/**
	 * Draw the solution of the maze
	 * @param g
	 * @param N
	 * @param side
	 */
	public void drawSolution(Graphics g, int N, int side) {
		g.setColor(Color.RED);
		for (int i = 0; i < 2 * N; i++) {
			for (int j = 0; j < 2 * N; j++) {
				if (maze[i][j].isPartOfSolution) {
					g.fillRect(j * side + side, i * side + side, side, side);
				}
			}
		}
		
	}

}

/**
 * This class represents a cell in the maze.
 * A cell can be a wall or a path, can be visited or not.
 * The cell is also a point in the maze.
 */
class Cell {
	private Point coordinates;
	private boolean wall; // is or not a wall
	private boolean visited; // has or not been visited
	public int someNeighbour;
	public boolean isPartOfSolution; // if true is on the exit path
	
	public Cell() {
		wall = true;
		visited = false;
		coordinates = new Point(0,0);
		someNeighbour = 0;
		isPartOfSolution = false;
	}
	
	public Cell(boolean wall, boolean visited, int x, int y, int neighbours) {
		this.wall = wall;
		this.visited = visited;
		this.coordinates = new Point(x, y);
		someNeighbour = neighbours;
		isPartOfSolution = false;
	}
	
	public void SetVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean GetVisited() {
		return visited;
	}
	
	public void SetWall(boolean wall) {
		this.wall = wall;
	}
	
	public boolean GetWall() {
		return wall;
	}
	
	public int GetX() {
		return coordinates.x;
	}
	
	public int GetY() {
		return coordinates.y;
	}
}