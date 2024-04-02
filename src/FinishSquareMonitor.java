/**
 * @author Antonio Terpin
 * @year 2016
 * 
 * Monitor that checks the access to the possible victory message.
 */

import java.awt.Color;
import java.awt.Label;


public class FinishSquareMonitor {
	/*
	 * Monitor that checks the access to the possible victory message.
	 * It is necessary because if one arrives one instant after the other 
	 * the two results could mix (victory message of one with the color of the
	 * other).
	 * 
	 * With a monitor the possibility of arrival is evaluated once (the
	 * draw is still possible).
	 * Mutual exclusion is guaranteed by the synchronized keyword, since there
	 * is only one critical event (competition), the implicit semaphore of the
	 * class is sufficient.
	 */
	
	private MazeGame mg;
	
	public FinishSquareMonitor() {
		mg = null;
	}
	
	public FinishSquareMonitor(MazeGame mg) {
		this.mg = mg;
	}

	public synchronized void MaybeFinished() throws InterruptedException {
		mg.lblResults = new Label("", Label.CENTER);
		if (mg.p1.Finished() && mg.p2.Finished()) {
			mg.lblResults.setText(mg.msgDraw);
			mg.lblResults.setBackground(Color.GREEN);
		} else if (mg.p1.Finished()) {
			mg.lblResults.setText(mg.msgWP + mg.p1Name + mg.msgYW);
			mg.lblResults.setBackground(mg.p1.player_color);
		} else if (mg.p2.Finished()) {
			mg.lblResults.setText(mg.msgWP + mg.p2Name + mg.msgYW);
			mg.lblResults.setBackground(mg.p2.player_color);
		}
		
		if(mg.p1.Finished() || mg.p2.Finished()) {
			mg.lblResults.setLocation(
				mg.leftOffset + mg.N * mg.side + mg.side / 2 - 100, 
				3 * mg.f1Size / 4 + 2 * mg.verticalOffset + mg.N * mg.side);
			mg.lblResults.setForeground(Color.BLACK);
			mg.lblResults.setVisible(false);
			mg.lblResults.setSize(200, 50);
			mg.add(mg.lblResults);
			
			mg.btnRestart.setEnabled(false);
			mg.btnPause.setEnabled(false);
			mg.btnStop.setEnabled(false);
			mg.btnDrawSolution.setEnabled(false);
			mg.btnExit.setEnabled(true);
			
			for (int i = 0; i < 5; i++) {
				mg.lblResults.setVisible(false);
				Thread.sleep(400);
				mg.lblResults.setVisible(true);
				Thread.sleep(400);
			}
			// When one player reaches the end, stop both players
			mg.thread_player_1.interrupt();
			mg.thread_player_2.interrupt();
			mg.thread_player_1 = mg.thread_player_2 = null;
			
		}
	}
}
