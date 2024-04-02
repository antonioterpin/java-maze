/**
 * @author Antonio Terpin
 * @year 2016
 * 
 * Listener for the player's input.
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;


public class MovesListener implements KeyListener {

	public boolean left, right, up, down, a, w, d, s, ctrl;
	private Stack<Boolean> st = new Stack<Boolean>();
	public MovesListener() {
		left = right = up = down = a = w = d = s = ctrl = false;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key = arg0.getKeyCode();
		
		//arrows
		if (key == KeyEvent.VK_LEFT) {
//			System.out.println("left key");
			left = true;
		}
		if (key == KeyEvent.VK_RIGHT) {
//			System.out.println("right key");
			right = true;
		}
		if (key == KeyEvent.VK_UP) {
//			System.out.println("up key");
			up = true;
		}
		if (key == KeyEvent.VK_DOWN) {
//			System.out.println("down key");
			down = true;
		}
		
		//awds
		if (key == KeyEvent.VK_A) {
			a = true;
		}
		if (key == KeyEvent.VK_W) {
			w = true;
		}
		if (key == KeyEvent.VK_D) {
			d = true;
		}
		if (key == KeyEvent.VK_S) {
			s = true;
		}
		
		//ctrl
		if(key == KeyEvent.VK_CONTROL) {
			ctrl = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int key = arg0.getKeyCode();

		//arrows
		if (key == KeyEvent.VK_LEFT) {
			left = false;
		}
		if (key == KeyEvent.VK_RIGHT) {
			right = false;
		}
		if (key == KeyEvent.VK_UP) {
			up = false;
		}
		if (key == KeyEvent.VK_DOWN) {
			down = false;
		}
		
		//awds
		if (key == KeyEvent.VK_A) {
			a = false;
		}
		if (key == KeyEvent.VK_W) {
			w = false;
		}
		if (key == KeyEvent.VK_D) {
			d = false;
		}
		if (key == KeyEvent.VK_S) {
			s = false;
		}
		
		//ctrl
		if(key == KeyEvent.VK_CONTROL) {
			ctrl = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * Clear the input, store the input in the stack (clear interrupts)
	 */
	public void cli() {
		st.push(left);
		st.push(right);
		st.push(up);
		st.push(down);
		st.push(a);
		st.push(w);
		st.push(d);
		st.push(s);
		left = right = up = down = a = w = d = s = false;
	}
	
	/**
	 * Restore the input from the stack (set interrupts)
	 */
	public void sti() {
		s = st.pop();
		d = st.pop();
		w = st.pop();
		a = st.pop();
		down = st.pop();
		up = st.pop();
		right = st.pop();
		left = st.pop();
	}
}
