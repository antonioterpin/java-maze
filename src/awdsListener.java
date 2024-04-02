/**
 * @author Antonio Terpin
 * @year 2016
 * 
 * Listener for the player's input (awds).
 */

public class awdsListener implements Runnable {

	private Player p;
	private Maze m;
	private MovesListener mv;
	private MazeGame mg;
	
	/**
	 * Constructor
	 * @param mg MazeGame
	 */
	public awdsListener(MazeGame mg) {
		this.mg = mg;
		this.p = mg.p1;
		this.m = mg.lab;
		this.mv = mg.mv;
	}
	
	@Override
	public void run() {
		int inc_x = 0, inc_y = 0;
		boolean isColliding;
		while(!p.Finished() && !Thread.currentThread().isInterrupted()) {
			if(mv.a) {
				inc_x--;
			}
			if(mv.d) {
				inc_x++;
			}
			if(mv.w) {
				inc_y--;
			}
			if(mv.s) {
				inc_y++;
			}
			
			Player temp = new Player(p);
			
			// Remove invalid displacement components if any
			temp.Movement(inc_x, 0);
			isColliding = m.IsColliding(temp);
			if(!isColliding) {
				p.Movement(inc_x, 0);
			}
			temp.Movement(0-inc_x, inc_y);
			isColliding = m.IsColliding(temp);
			if(!isColliding) {
				p.Movement(0, inc_y);
			}
			mg.repaint();
			inc_x = 0;
			inc_y = 0;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				break;
			}
			
			try {
				mg.fsm.MaybeFinished();
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
