/**
 * @author Antonio Terpin
 * @year 2016
 * 
 * Graphics of the game
 */

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class MazeGame extends JFrame {
	// TODO future features: 
	// 1. Menu
	// 2. Timing
	// 3. Ranking
	// 4. Change maze without restarting the game
	
	Maze lab;
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	
	// n_cell = 2 * N
	int N;
	// cell side
	int side;
	
	// Players
	Player p1, p2;
	ArrowListener listener_of_player_2;
	awdsListener listener_of_player_1;
	Thread thread_player_1, thread_player_2, t;
	MovesListener mv;
	// player's names
	String p1Name = "Player1", p2Name = "Player2";
	
	// graphics
	int leftOffset;
	int verticalOffset;
	int f1Size, f2Size;
	int wr1Size, wr2Size;
	int yImage = 100;
	Image labImage, leftDisplay;
	Graphics labImageGraphics, leftDisplayGraphics;
	
	// results
	String msgWP = "Congrats ", msgYW = ", you won!",
			msgDraw = "It's a draw."; //mooooooolto improbabile
	Label lblResults;
	
	// ctrl
	int ctrl_time = 2000, ctrl_n = 3; 
		//tempo di esecuzione delle funzioni di ctrl
		//e numero di cerchi disegnati
	
	// settings
	JPanel pnlRightDisplay;
	// rules
	TextArea txtaRules;
	// player's settings
	Label lblName1, lblName2;
	//	JFormattedTextField txtP1, txtP2;
	TextField txtP1, txtP2;
	Choice chP1, chP2;
	// buttons
	Button btnSubmit, btnRestart, btnStart, btnStop, btnPause, btnExit, btnResume, btnDrawSolution;
	Font b_f = new Font("TimesRoman",Font.BOLD,16);
	// N
	JSpinner diff;
	
	// finish square
	FinishSquareMonitor fsm;

	public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> {
			// Pick maze size
			int mazeSize = 10; // default
			if (args.length > 0) {
				try {
					// Attempt to parse the first argument into an integer
					mazeSize = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					// Handle the case where the first argument is not a valid integer
					System.out.println("The provided argument is not a valid integer.");
				}
			}
            MazeGame run = new MazeGame(mazeSize);
			run.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            run.setVisible(true);
        });
    }

	public MazeGame (int mazeSize) {
		this.N = mazeSize;
        initUI();
    }
	
	public void initUI () {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setResizable(false);
		this.setUndecorated(false);
		this.setSize(1000, 800);
		this.setVisible(true);
		
		// keybord listener
		mv = new MovesListener();
		addKeyListener(mv);
		
		// maze size
		side = (this.getWidth() / 2) / (2 * (N + 1));
		
		// graphic settings
		leftOffset = this.getWidth() / 20;
		verticalOffset = this.getHeight() / 32;
		
		f1Size = 2 * verticalOffset;
		f2Size = verticalOffset / 2;
		wr1Size = getWR1Size();
		wr2Size = getWR2Size();
		
		labImage = createImage(2 * N * side + side, 2 * N * side + side);
		labImageGraphics = labImage.getGraphics();
		leftDisplay = createImage(
			this.getWidth() / 2 + leftOffset, 
			this.getHeight());
		leftDisplayGraphics = leftDisplay.getGraphics();
		
		// Right side of the screen
		initRightDisplay();
		
		// create players and maze
		p1 = new Player(2 * N * side - side, 
						2 * N * side - side , side / 2, Color.RED);
		p2 = new Player(2 * N * side - side, 
						2 * N * side - side , side / 2, Color.BLUE);
		lab = new Maze(N, side);
		lab.Build();
		
		//game name and credits
		leftDisplayGraphics.setFont(
			new Font(Font.MONOSPACED, Font.ITALIC, f1Size));
		leftDisplayGraphics.drawString(
			getGameName(), 
			leftOffset + N * side - wr1Size / 4, 
			verticalOffset + 3 * f1Size / 4);
		leftDisplayGraphics.setFont(
			new Font(Font.MONOSPACED, Font.ITALIC, f2Size));
		leftDisplayGraphics.drawString(
			getCredits(), 
			leftOffset + N * side - wr2Size / 4, 
			3 * verticalOffset + f1Size + 2 * N * side + side);
		
		// start listening..
		requestFocus();
		
		// finish square
		fsm = new FinishSquareMonitor(this);
	}
	
	public void paint(Graphics g) {
		pnlRightDisplay.setBounds(
			this.getWidth() / 4, 
			0, 
			pnlRightDisplay.getWidth(), 
			pnlRightDisplay.getHeight());
		
		// update the maze
		lab.Draw(labImageGraphics);
		p1.Draw(labImageGraphics);
		p2.Draw(labImageGraphics);
		labImageGraphics.setColor(Color.red);
		labImageGraphics.drawPolygon(p1.finishSquare);
		
		// if ctrl is pressed, pause the threads for a few seconds
		// highlight where players are
		if(mv.ctrl) {
			mv.cli(); // block the players
			for (int i = 0; i < ctrl_n; i++) {
				// draw circles of radius ((i+1)*side)
				labImageGraphics.setColor(p1.getColor());
				labImageGraphics.drawOval(
					p1.getX() - (i + 1) * side / 2 + side / 4, 
					p1.getY() - (i + 1) * side / 2 + side / 4, 
					(i + 1) * side, 
					(i + 1) * side);
				labImageGraphics.setColor(p2.getColor());
				labImageGraphics.drawOval(
					p2.getX() - (i + 1) * side / 2 + side / 4, 
					p2.getY() - (i + 1) * side / 2  + side / 4, 
					(i + 1) * side, 
					(i + 1) * side);
				leftDisplayGraphics.drawImage(
					labImage, leftOffset, f1Size + 2 * verticalOffset, this);
				g.drawImage(leftDisplay, 0, yImage, this);
				try {
					Thread.sleep(ctrl_time/ctrl_n);
				} catch (InterruptedException e) {}
				lab.Draw(labImageGraphics);
				p1.Draw(labImageGraphics);
				p2.Draw(labImageGraphics);
				labImageGraphics.setColor(Color.RED);
				labImageGraphics.drawPolygon(p1.finishSquare);
				leftDisplayGraphics.drawImage(labImage, leftOffset, f1Size + 2*verticalOffset, this);
				g.drawImage(leftDisplay, 0, yImage, this);
			}
			mv.sti(); // resume the operations (set interrupts)
		}
		
		
		// update left screen, right one stay the same
		leftDisplayGraphics.drawImage(labImage, leftOffset, f1Size + 2*verticalOffset, this);
		
		// draw left screen
		g.drawImage(leftDisplay, 0, yImage, this);
	}

	private void doStart () {
		if(listener_of_player_1 == null) {
			listener_of_player_1 = new awdsListener(this);
		}
		if(listener_of_player_2 == null) {
			listener_of_player_2 = new ArrowListener(this);
		}
		
		if(thread_player_1 == null) {
			thread_player_1 = new Thread(listener_of_player_1);
			thread_player_1.start();
		}
		if(thread_player_2 == null) {
			thread_player_2 = new Thread(listener_of_player_2);
			thread_player_2.start();
		}	
		
		this.requestFocus();
	}
	
	public void stop () {
		if (thread_player_1 != null) {
			thread_player_1.interrupt();
		}
		if(thread_player_2 != null) {
			thread_player_2.interrupt();
		}
		if(t != null) {
			t.interrupt();
		}
		t = thread_player_1 = thread_player_2 = null;
	}
	
	private String getGameName () {
		return "T H E  M A Z E";
	}
	
	private int getWR1Size () {
		String s = getGameName();
		return s.length() * f1Size;
	}
	
	private String getCredits () {
		return "The Maze, Copyright 2016 by Antonio Terpin.";
	}
	
	private int getWR2Size () {
		String s = getCredits();
		return s.length() * f2Size;
	}

	public boolean clk_submit () {
		if (txtP1.getText().equals("") || txtP2.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
				"Pick a name for the two players!", 
				"Name missing!", JOptionPane.OK_OPTION);
			return false;
		}
		// check names satisfy the correct pattern
		Pattern p = Pattern.compile("[a-zA-Z0-9]{1,10}");
		Matcher m1, m2;
		m1 = p.matcher(txtP1.getText());
		m2 = p.matcher(txtP2.getText());
		if (!m1.matches()
			|| !m2.matches()
			|| txtP1.getText().equals(txtP2.getText())) {	 
			JOptionPane.showMessageDialog(null,
				"Pick a distinct name for the two players,\n" +
				"it must be made by letters and or numbers" + 
				" (length min:1 max:10)", 
				"Wrong name!", JOptionPane.OK_OPTION);
			return false;
		} 
		// else if(chP1.getSelectedIndex() == chP2.getSelectedIndex()) {
		// 	JOptionPane.showMessageDialog(null,
		// 			"Scegli un colore diverso per ciascun giocatore..", 
		// 			"Attenzione..", JOptionPane.OK_OPTION);
		// 	return;
		// }
			 
		//Player 1
		p1Name = txtP1.getText();
		p1.setColor(Color.BLUE);
		// switch(chP1.getSelectedIndex()) {
		// 	case 0: p1.setColor(Color.RED); break;
		// 	case 1: p1.setColor(Color.BLUE); break;
		// 	case 2: p1.setColor(Color.GREEN); break;
		// 	default: break;
		// }
			 
		//Player 2
		p2Name = txtP2.getText();
		p2.setColor(Color.GREEN);
		// switch(chP2.getSelectedIndex()) {
		// 	case 0: p2.setColor(Color.RED); break;
		// 	case 1: p2.setColor(Color.BLUE); break;
		// 	case 2: p2.setColor(Color.GREEN); break;
		// 	default: break;
		// }
			 
		btnSubmit.setEnabled(false);
		btnStart.setEnabled(true);
		btnExit.setEnabled(true);
		txtP1.setEnabled(false);
		txtP2.setEnabled(false);
		chP1.setEnabled(false);
		chP2.setEnabled(false);

		return true;
	}

	public void clk_start () {
		doStart();

		btnStart.setEnabled(false);
		btnPause.setEnabled(true);
		btnStop.setEnabled(true);
		btnRestart.setEnabled(true);
		btnExit.setEnabled(false);
	}

	public void clk_pause () {
		thread_player_1.interrupt();
		thread_player_2.interrupt();

		btnPause.setEnabled(false);
		btnRestart.setEnabled(false);
		btnStop.setEnabled(false);
		btnStart.setEnabled(false);
		btnResume.setEnabled(true);
		btnExit.setEnabled(true);
	}

	public void clk_resume () {
		btnResume.setEnabled(false);
		btnPause.setEnabled(true);
		btnRestart.setEnabled(true);
		btnStop.setEnabled(true);
		btnExit.setEnabled(false);
		
		thread_player_1 = new Thread(listener_of_player_1);
		thread_player_2 = new Thread(listener_of_player_2);
		
		thread_player_1.start();
		thread_player_2.start();
		
		this.requestFocus();
	}

	public void clk_stop () {
		stop();
		
		p1.setXY(2 * N * side - side, 2 * N * side - side);
		p2.setXY(2 * N * side - side, 2 * N * side - side);
		repaint();
		
		btnRestart.setEnabled(false);
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnResume.setEnabled(false);
		btnPause.setEnabled(false);
		btnExit.setEnabled(true);
	}

	public void clk_restart () {
		p1.setXY(2 * N * side - side, 2 * N * side - side);
		p2.setXY(2 * N * side - side, 2 * N * side - side);
		
		this.requestFocus();
	}

	public void clk_exit () {
		stop();
		System.exit(0);
	}

	public void clk_drawSolution () {
		// end game (players gave up.. naturally.. lol)
		stop();
		
		// only exit is enabled (TODO: allow to restart the game)
		btnDrawSolution.setEnabled(false);
		btnSubmit.setEnabled(false);
		btnRestart.setEnabled(false);
		btnStart.setEnabled(false);
		btnStop.setEnabled(false);
		btnResume.setEnabled(false);
		btnPause.setEnabled(false);
		btnExit.setEnabled(true);
		
		// compute the solution and display it
		lab.computeSolution();
		lab.drawSolution(labImageGraphics, N, side);
		leftDisplayGraphics.drawImage(
		   labImage, leftOffset, f1Size + 2 * verticalOffset, this);
		 getGraphics().drawImage(leftDisplay, 0, yImage, this);
	}
	
	private String getRules() {
		return "Welcome to The Maze!\nThe rules are easy:\n" +
				"\nPlayer 1 moves using the letters:\n-) a (left)\n" +
				"-) w (up)\n-) d (right)\n-) s (down)\n\nPlayer 2 uses " +
				"the arrows.\n\nThe goal is to reach the red square on the top left. Both players start in the same location, who gets to the end first wins."
				+ "\n\nIf you lose track of your player, press ctrl."
				+ "\n\nIf you really cannot manage to find the solution, " +
				"press DrawSolution to see the solution, but the game will end.";
	}
	
	private void initRightDisplay() {
		pnlRightDisplay = new JPanel();
		pnlRightDisplay.setLayout(new GridBagLayout());
		GridBagConstraints constraints;
		
		pnlRightDisplay.setSize(
			this.getWidth() / 2 - 4 * leftOffset, 
			this.getHeight() - 2 * verticalOffset - f1Size);
		
		//Rules
		txtaRules = new TextArea(getRules(), 20, 40, TextArea.SCROLLBARS_NONE);
		txtaRules.setEditable(false);
		txtaRules.setVisible(true);
		txtaRules.setVisible(true);
		// layout
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(40, 0, 10, 0);
		pnlRightDisplay.add(txtaRules, constraints);
		pnlRightDisplay.revalidate();
		pnlRightDisplay.setVisible(true);
		
		//player settings
		lblName1 = new Label("Player 1:");
		lblName2 = new Label("Player 2:");
		txtP1 = new TextField(30);
		txtP2 = new TextField(30);
		String[] colors={"red", "blue", "green"};
		chP1 = new Choice();
		chP2 = new Choice();
		for (String color : colors) {
			chP1.add(color);
			chP2.add(color);
		}
		
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(lblName1, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(txtP1, constraints);
		
		// TODO: make this work again...
		// constraints = new GridBagConstraints();
		// constraints.gridx = 0;
		// constraints.gridy = 1;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// pnlRightDisplay.add(chP1, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(lblName2, constraints);
		
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(txtP2, constraints);
		
		// TODO: make this work again...
		// constraints = new GridBagConstraints();
		// constraints.gridx = 4;
		// constraints.gridy = 2;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// pnlRightDisplay.add(chP2, constraints);
		
		// buttons
		btnSubmit = new Button("Ready");
		btnSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_submit();
			}
		});
		btnRestart = new Button("Restart");
		btnRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_restart();
			}
		});
		btnStart = new Button("Start");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (clk_submit()) {
					clk_start();
				}
			}
		});
		btnStop = new Button("Stop");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_stop();
			}
		});
		btnPause = new Button("Pause");
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_pause();
			}
		});
		btnResume = new Button("Resume");
		btnResume.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_resume();
			}
		});
		btnExit = new Button("Exit");
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_exit();
			}
		});
		btnDrawSolution = new Button("DrawSolution");
		btnDrawSolution.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clk_drawSolution();
			}
		});
		
		// background
		btnSubmit.setBackground(Color.GRAY);
		btnRestart.setBackground(Color.YELLOW);
		btnStart.setBackground(Color.GREEN);
		btnStop.setBackground(Color.RED);
		btnPause.setBackground(Color.ORANGE);
		btnResume.setBackground(Color.BLUE);
		btnExit.setBackground(Color.BLACK);
		btnDrawSolution.setBackground(Color.RED);
		
		// font
		btnSubmit.setFont(b_f);
		btnRestart.setFont(b_f);
		btnStart.setFont(b_f);
		btnStop.setFont(b_f);
		btnPause.setFont(b_f);
		btnResume.setFont(b_f);
		btnExit.setFont(b_f);
		btnDrawSolution.setFont(b_f);
		
		// foreground color
		btnSubmit.setForeground(Color.BLACK);
		btnRestart.setForeground(Color.WHITE);
		btnStart.setForeground(Color.BLACK);
		btnStop.setForeground(Color.WHITE);
		btnPause.setForeground(Color.WHITE);
		btnResume.setForeground(Color.WHITE);
		btnExit.setForeground(Color.WHITE);
		btnDrawSolution.setForeground(Color.BLACK);
		
		// submit, exit and drawsolution are allowed to be pressed
		// also at the beginning
		btnStop.setEnabled(false);
		btnPause.setEnabled(false);
		btnResume.setEnabled(false);
		btnStart.setEnabled(true);
		btnRestart.setEnabled(false);
		
		// layout
		// No need at the moment, given the others functionalities are not ready
		// // btnSubmit
		// constraints = new GridBagConstraints();
		// constraints.gridx = 0;
		// constraints.gridy = 3;
		// constraints.gridwidth = 5;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// // pnlRightDisplay.add(btnSubmit, constraints);
		// btnStart
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(btnStart, constraints);
		// // btnPause
		// constraints = new GridBagConstraints();
		// constraints.gridx = 1;
		// constraints.gridy = 3;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// pnlRightDisplay.add(btnPause, constraints);
		// // btnResume
		// constraints = new GridBagConstraints();
		// constraints.gridx = 2;
		// constraints.gridy = 4;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// pnlRightDisplay.add(btnResume, constraints);
		// // btnStop
		// constraints = new GridBagConstraints();
		// constraints.gridx = 3;
		// constraints.gridy = 4;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// pnlRightDisplay.add(btnStop, constraints);
		// // btnRestart
		// constraints = new GridBagConstraints();
		// constraints.gridx = 4;
		// constraints.gridy = 4;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 0, 10, 0);
		// pnlRightDisplay.add(btnRestart, constraints);
		// btnDrawSolution
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(btnDrawSolution, constraints);
		
		// btnExit
		constraints = new GridBagConstraints();
		constraints.gridx = 4;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(10, 0, 10, 0);
		pnlRightDisplay.add(btnExit, constraints);
		
		// // TODO: timer
		// constraints = new GridBagConstraints();
		// constraints.gridx = 0;
		// constraints.gridy = 6;
		// constraints.gridwidth = 5;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// constraints.insets = new Insets(10, 10, 0, 0);
		
		//add
		add(pnlRightDisplay);
	}
}
