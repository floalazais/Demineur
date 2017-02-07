import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;

public class Window extends JFrame implements ActionListener, MouseListener, ComponentListener
{
	/* --- Members --- */

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("Menu");
	private JMenuItem quitMenuItem = new JMenuItem("Quit");
	private JMenuItem easyMenuItem = new JMenuItem("Easy : 10 mines in 9x9 grid");
	private JMenuItem normalMenuItem = new JMenuItem("Normal : 40 mines in 16x16 grid");
	private JMenuItem hardMenuItem = new JMenuItem("Hard : 99 mines in 30x16 grid");
	private JMenuItem customMenuItem = new JMenuItem("Custom : Create your own grid");
	private JLabel timerLabel = new JLabel("00:00:000");
	private JLabel highscoreLabel = new JLabel("Choosing difficulty");
	private JButton easyButton = new JButton("Easy : 10 mines in 9x9 grid");
	private JButton normalButton = new JButton("Normal : 40 mines in 16x16 grid");
	private JButton hardButton = new JButton("Hard : 99 mines in 30x16 grid");
	private JButton customButton = new JButton("Custom : Create your own grid");
	private Panel panel;

	/* --- Constructor(s) --- */

	// Frame part

	public Window()
	{
		super("Minesweeper");

		fillComponent();
		pack();
        fileMenu.requestFocusInWindow();

		setSize(800,600);
		setMinimumSize(new Dimension(500,150));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// Components part

	private void fillComponent()
	{
		// Menu bar

		menuBar.setLayout(new GridLayout(1, 3));

			// File menu

			fileMenu.addMouseListener(this);
			fileMenu.setHorizontalTextPosition(SwingConstants.CENTER);
			fileMenu.setVerticalTextPosition(SwingConstants.BOTTOM);
			fileMenu.setBackground(new Color(236, 240, 241));
			fileMenu.setOpaque(true);

				// File menu items

				easyMenuItem.addActionListener(this);
				easyMenuItem.setAccelerator(KeyStroke.getKeyStroke(49, ActionEvent.ALT_MASK));
				easyMenuItem.setBackground(new Color(236, 240, 241));
				easyMenuItem.setForeground(new Color(39, 174, 96));
				fileMenu.add(easyMenuItem);

				normalMenuItem.addActionListener(this);
				normalMenuItem.setAccelerator(KeyStroke.getKeyStroke(50, ActionEvent.ALT_MASK));
				normalMenuItem.setBackground(new Color(236, 240, 241));
				normalMenuItem.setForeground(new Color(243, 156, 18));
				fileMenu.add(normalMenuItem);

				hardMenuItem.addActionListener(this);
				hardMenuItem.setAccelerator(KeyStroke.getKeyStroke(51, ActionEvent.ALT_MASK));
				hardMenuItem.setBackground(new Color(236, 240, 241));
				hardMenuItem.setForeground(new Color(231, 76, 60));
				fileMenu.add(hardMenuItem);

				customMenuItem.addActionListener(this);
				customMenuItem.setAccelerator(KeyStroke.getKeyStroke(52, ActionEvent.ALT_MASK));
				customMenuItem.setBackground(new Color(236, 240, 241));
				customMenuItem.setForeground(new Color(52, 152, 219));
				fileMenu.add(customMenuItem);

				fileMenu.addSeparator();

				quitMenuItem.addActionListener(this);
				quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(115, ActionEvent.ALT_MASK));
				quitMenuItem.setBackground(new Color(236, 240, 241));
				fileMenu.add(quitMenuItem);

			// Timer label

			timerLabel.setHorizontalAlignment(JLabel.CENTER);
			timerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			timerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			timerLabel.setBackground(new Color(236, 240, 241));
			timerLabel.setOpaque(true);

			// Highscore label

			highscoreLabel.setHorizontalAlignment(JLabel.CENTER);
			highscoreLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			highscoreLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
			highscoreLabel.setBackground(new Color(236, 240, 241));
			highscoreLabel.setOpaque(true);

		// Piecing menu bar elements together

		menuBar.add(fileMenu);
		menuBar.add(timerLabel);
		menuBar.add(highscoreLabel);

		setJMenuBar(menuBar);

		// Buttons container

		getContentPane().setLayout(new GridLayout(2, 2));

			// Buttons

			easyButton.addActionListener(this);
			easyButton.addMouseListener(this);
			easyButton.addComponentListener(this);
			easyButton.setBackground(new Color(236, 240, 241));
			easyButton.setForeground(new Color(39, 174, 96));
			easyButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			easyButton.setOpaque(true);
			add(easyButton);

			normalButton.addActionListener(this);
			normalButton.addMouseListener(this);
			normalButton.addComponentListener(this);
			normalButton.setBackground(new Color(236, 240, 241));
			normalButton.setForeground(new Color(243, 156, 18));
			normalButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			normalButton.setOpaque(true);
			add(normalButton);

			hardButton.addActionListener(this);
			hardButton.addMouseListener(this);
			hardButton.addComponentListener(this);
			hardButton.setBackground(new Color(236, 240, 241));
			hardButton.setForeground(new Color(231, 76, 60));
			hardButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			hardButton.setOpaque(true);
			add(hardButton);

			customButton.addActionListener(this);
			customButton.addMouseListener(this);
			customButton.addComponentListener(this);
			customButton.setBackground(new Color(236, 240, 241));
			customButton.setForeground(new Color(52, 152, 219));
			customButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			customButton.setOpaque(true);
			add(customButton);
	}

	/* --- Interface(s) implementation(s) --- */

	/* ActionListener implementation */

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == easyMenuItem || event.getSource() == easyButton)
		{
			startGame(GameType.EASY);
		}
		else if(event.getSource() == normalMenuItem || event.getSource() == normalButton)
		{
			startGame(GameType.NORMAL);
		}
		else if(event.getSource() == hardMenuItem || event.getSource() == hardButton)
		{
			startGame(GameType.HARD);
		}
		else if(event.getSource() == customMenuItem || event.getSource() == customButton)
		{
			startGame(GameType.CUSTOM);
		}
		else if(event.getSource() == quitMenuItem)
		{
			System.exit(0);
		}
	}

	/* MouseListener implementation */

	public void mouseClicked(MouseEvent event){}

	// Change color according to which button is overflew

	public void mouseEntered(MouseEvent event)
	{
		if(event.getSource() == fileMenu)
		{
			fileMenu.setBackground(new Color(189, 195, 199));
		}
		else if(event.getSource() == easyButton)
		{
			easyButton.setForeground(new Color(236, 240, 241));
			easyButton.setBackground(new Color(39, 174, 96));
		}
		else if(event.getSource() == normalButton)
		{
			normalButton.setForeground(new Color(236, 240, 241));
			normalButton.setBackground(new Color(243, 156, 18));
		}
		else if(event.getSource() == hardButton)
		{
			hardButton.setForeground(new Color(236, 240, 241));
			hardButton.setBackground(new Color(231, 76, 60));
		}
		else if(event.getSource() == customButton)
		{
			customButton.setForeground(new Color(236, 240, 241));
			customButton.setBackground(new Color(52, 152, 219));
		}
	}

	public void mouseExited(MouseEvent event)
	{
		if(event.getSource() == fileMenu)
		{
			fileMenu.setBackground(new Color(236, 240, 241));
		}
		else if(event.getSource() == easyButton)
		{
			easyButton.setForeground(new Color(39, 174, 96));
			easyButton.setBackground(new Color(236, 240, 241));
		}
		else if(event.getSource() == normalButton)
		{
			normalButton.setForeground(new Color(243, 156, 18));
			normalButton.setBackground(new Color(236, 240, 241));
		}
		else if(event.getSource() == hardButton)
		{
			hardButton.setForeground(new Color(231, 76, 60));
			hardButton.setBackground(new Color(236, 240, 241));
		}
		else if(event.getSource() == customButton)
		{
			customButton.setForeground(new Color(52, 152, 219));
			customButton.setBackground(new Color(236, 240, 241));
		}
	}

	public void mousePressed(MouseEvent event)
	{
		if(event.getSource() == fileMenu)
		{
			fileMenu.setBackground(new Color(236, 240, 241));
		}
	}

	public void mouseReleased(MouseEvent event){}

	/* ComponentListener implementation */

	public void componentHidden(ComponentEvent event){}

	public void componentShown(ComponentEvent event){}

	public void componentMoved(ComponentEvent event){}

	// Resizing font according to window size

	public void componentResized(ComponentEvent event)
	{
		if(event.getSource() == easyButton || event.getSource() == normalButton || event.getSource() == hardButton || event.getSource() == customButton)
		{
			int newFontSize = (int)Math.min(easyButton.getSize().getWidth()*1.5/easyButton.getText().length(), easyButton.getSize().getHeight());
			Font newFont = new Font("Data/Font/Roboto-Regular.ttf", Font.BOLD, newFontSize);
			easyButton.setFont(newFont);
			normalButton.setFont(newFont);
			hardButton.setFont(newFont);
			customButton.setFont(newFont);
		}
	}

	/* --- Start game --- */

	/* Start a game */

	private void startGame(GameType gametype)
	{
		// If the user was playing

		if(panel != null)
		{
			// Cancel the game to start a new one (in custom the game start after the game settings are fixed)

			if(gametype != GameType.CUSTOM)
			{
				cancelGame();
			}
		}

		// Creating a new panel matching with the chosen gametype, and setting new minimum size for the window

		switch(gametype)
		{
			case EASY:
				panel = new Panel(this, gametype, 9, 9, 10);
				setMinimumSize(new Dimension(9 * 16 + 20, 9 * 16 + menuBar.getHeight() + getInsets().top + 20));
				break;

			case NORMAL:
				panel = new Panel(this, gametype, 16, 16, 40);
				setMinimumSize(new Dimension(16 * 16 + 20, 16 * 16 + menuBar.getHeight() + getInsets().top + 20));
				break;

			case HARD:
				panel = new Panel(this, gametype, 30, 16, 99);
				setMinimumSize(new Dimension(30 * 16 + 20, 16 * 16 + menuBar.getHeight() + getInsets().top + 20));
				break;

			case CUSTOM:

				// Calling a CustomDialog to fix the game settings, if settings not fixed, no panel created

				CustomDialog customDialog = new CustomDialog(this, "Customize the grid", true);
				if(customDialog.isValid())
				{
					cancelGame();

					panel = new Panel(this, gametype, customDialog.getColumns(), customDialog.getRows(), customDialog.getMines());
					setMinimumSize(new Dimension(customDialog.getColumns() * 16 + 20, customDialog.getRows() * 16 + menuBar.getHeight() + getInsets().top + 20));
				}
				break;
		}

		// If a new game was started, display the created panel

		if(panel != null)
		{
			getContentPane().removeAll();
			repaint();
			getContentPane().setLayout(new GridBagLayout());
			getContentPane().add(panel, GridBagConstraints.NONE);
			setVisible(true);
			panel.requestFocus();
		}
	}

	private void cancelGame()
	{
		// Finish current game if it isn't the case

		if(!panel.isGameFinished())
		{
			panel.finishGame(false);
		}

		// Delete panel

		remove(panel);
		repaint();
		panel = null;

		// Reset menu bar labels color

		timerLabel.setForeground(Color.BLACK);
		highscoreLabel.setForeground(Color.BLACK);
	}

	/* Restart a game */

	public void restartGame(GameType gametype)
	{
		startGame(gametype);
	}

	/* --- Display highscore --- */

	public void displayHighscore(long highscore, GameType gametype)
	{
		highscoreLabel.setText("<html><center>Highscore ("+gametypeToString(gametype)+") : "+timeToString(highscore));
	}

	public void displayNoHighscore(GameType gametype)
	{
		highscoreLabel.setText("<html><center>Highscore ("+gametypeToString(gametype)+") : "+"No highscore");
	}

	public void displayNewHighscore(GameType gametype)
	{
		highscoreLabel.setText("New highscore ! ("+gametypeToString(gametype)+")");
		highscoreLabel.setForeground(new Color(39, 174, 96));
	}

	/* GameType to string converter */

	private String gametypeToString(GameType gametype)
	{
		switch(gametype)
		{
			case EASY:
				return "Easy";
			case NORMAL:
				return "Normal";
			case HARD:
				return "Hard";
			case CUSTOM:
				return "Custom";
			default:
				return "";
		}
	}

	/* --- Time display --- */

	/* Update time label */

	public void updateTimerLabel(long elapsedTime)
	{
		timerLabel.setText(timeToString(elapsedTime));
	}

	/* Update time label color and tell the player if he lost/won */

	public void win()
	{
		timerLabel.setForeground(new Color(39, 174, 96));
		timerLabel.setText("Victory "+timerLabel.getText());
	}

	public void lose()
	{
		timerLabel.setForeground(new Color(231, 76, 60));
		timerLabel.setText("Defeat "+timerLabel.getText());
	}

	/* Time to string converter */

	private String timeToString(long elapsedTime)
	{
		// String parts

		String minutesString;
		String secondsString;
		String millisecondsString;

			// Minutes part

			int minutesInt = (int)(elapsedTime/60000);
			if(minutesInt < 10)
			{
				minutesString = new String("0"+Integer.toString(minutesInt));
			}
			else
			{
				minutesString = new String(Integer.toString(minutesInt));
			}

			// Seconds part

			int secondsInt = (int)((elapsedTime%60000)/1000);
			if(secondsInt < 10)
			{
				secondsString = new String("0"+Integer.toString(secondsInt));
			}
			else
			{
				secondsString = new String(Integer.toString(secondsInt));
			}

			// Milliseconds part

			int millisecondsInt = (int)(elapsedTime%1000);
			if(millisecondsInt < 10)
			{
				millisecondsString = new String("00"+Integer.toString(millisecondsInt));
			}
			else if(millisecondsInt < 100)
			{
				millisecondsString = new String("0"+Integer.toString(millisecondsInt));
			}
			else
			{
				millisecondsString = new String(Integer.toString(millisecondsInt));
			}

		return minutesString+":"+secondsString+":"+millisecondsString;
	}

	/* --- Other methods --- */

	/* Get menu bar height */

	public int getMenuBarHeigth()
	{
		return menuBar.getHeight();
	}
}
