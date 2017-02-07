import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.Date;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

import java.io.File;
import java.io.BufferedWriter;

public class Panel extends JPanel implements KeyListener
{
	/* --- Members --- */

	private Window parent;
	private GameType gametype;
	private int columns;
	private int rows;
	private int totalMineNumber;

	private Tile[][] tiles;
	private int numberFlag = 0;
	private boolean gridGenerated = false;
	private boolean finished = false;
	private boolean won = false;

	private int selectedColumn;
	private int selectedRow;

	private SwingWorker<Void, Void> thread;
	private long timeStart;
	private long timeStop;

	private double gridRatio;
	private long highscore;
	private boolean highscoreValid = false;

	/* --- Constructor(s) --- */

	/* Forbidden default constructor */

	private Panel(){}

	/* To-use constructor */

	public Panel(Window _parent, GameType _gametype, int _columns, int _rows, int _totalMineNumber)
	{
		parent = _parent;
		columns = _columns;
		rows = _rows;
		totalMineNumber = _totalMineNumber;
		gametype = _gametype;

		displayHighscore();
		fillComponent();
		requestFocus();
		addKeyListener(this);

		// Initialize timer label

		parent.updateTimerLabel(0);

		// Select first tile for keyboard control

		selectedColumn = columns/2;
		selectedRow = rows/2;

		// Initialize size

		gridRatio = (double) columns/rows;
		setPreferredSize(new Dimension(columns * 64, rows * 64));
		updateSize();

	}

	/* Fill panel with tiles */

	private void fillComponent()
	{
		setLayout(new GridLayout(rows, columns));
		tiles = new Tile[rows][columns];
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				tiles[i][j] = new Tile(this);
				add(tiles[i][j]);
			}
		}
	}

	/* Display highscore */

	private void displayHighscore()
	{
		// Try to read highscore

		try
		{
			switch(gametype)
			{
				case EASY:
					highscore = Long.parseLong((Files.readAllLines(Paths.get("Data/Texts/highscores.txt"))).get(0));
					highscoreValid = true;
					break;
				case NORMAL:
					highscore = Long.parseLong((Files.readAllLines(Paths.get("Data/Texts/highscores.txt"))).get(1));
					highscoreValid = true;
					break;
				case HARD:
					highscore = Long.parseLong((Files.readAllLines(Paths.get("Data/Texts/highscores.txt"))).get(2));
					highscoreValid = true;
					break;
			}
		}

		// If highscore couldn't be read, highscore invalidated

		catch(Exception exception)
		{
			highscoreValid = false;
		}

		// Display highscore, or something else if invalidated

		if(highscoreValid)
		{
			parent.displayHighscore(highscore, gametype);
		}
		else
		{
			parent.displayNoHighscore(gametype);
		}
	}

	/* --- Getter(s) --- */

	// Called by a tile to know if it's the first clicked tile

	public boolean isGridGenerated()
	{
		return gridGenerated;
	}

	// Called by a tile to know if there isn't too much flags on the grid

	public boolean tooMuchFlags()
	{
		return(numberFlag == totalMineNumber);
	}

	// Called by window to know if the game is finished before starting another

	public boolean isGameFinished()
	{
		return finished;
	}

	/* --- Generate Mines --- */

	private void putMines()
	{
		gridGenerated = true;

		int mineRow = 0;
		int mineColumn = 0;

		int minePut = 0;
		while(minePut < totalMineNumber)
		{
			// Pick a random tile

			mineRow = (int)(Math.random() * rows);
			mineColumn = (int)(Math.random() * columns);

			//If tile insn't already mined and is not the first clicked tile

			if(!tiles[mineRow][mineColumn].isMined() && !(tiles[mineRow][mineColumn].state() == State.SAFE))
			{
				// Mining tile

				tiles[mineRow][mineColumn].mine();
				minePut++;

				// If tile is in the grid and is not mined -> information that a mine was put next to it

				for(int i = -1; i < 2; i++)
				{
					for(int j = -1; j < 2; j++)
					{
						if((mineRow + i > -1) && (mineRow + i < rows) && (mineColumn + j > -1) && (mineColumn + j < columns))
						{
							tiles[mineRow+i][mineColumn+j].incrementMineAround();
						}
					}
				}
			}
		}

		// Active the first clicked tile again, because the grid wasn't generated at the first click

		boolean foundFirstClicked = false;
		for(int i = 0; i < rows && !foundFirstClicked; i++)
		{
			for(int j = 0; j < columns && !foundFirstClicked; j++)
			{
				if(tiles[i][j].state() == State.SAFE)
				{
					tiles[i][j].reveal();
					foundFirstClicked = true;
				}
			}
		}

		// Start the time thread

		timeStart = System.currentTimeMillis();

		thread = new SwingWorker<Void, Void>()
		{
	    	protected Void doInBackground() throws Exception
			{
				// While game not finished, display time in menu bar all 50 milliseconds

				while(!finished)
				{
					parent.updateTimerLabel(System.currentTimeMillis() - timeStart);
					Thread.sleep(50);
				}
				return null;
	     	};
	    	protected void done()
			{
				// At the end, "return" in timeStop the total elapsed time, and call the method to test if the highscore was beaten

				timeStop = System.currentTimeMillis();
				displayNewHighscore();
			};
	  	};
		thread.execute();
	}

	/* --- Win/Lose Tests --- */

	private boolean testForWin()
	{
		// If all safe tiles were revealed

		if(numberSafe() == ((columns * rows) - totalMineNumber))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean testForLose()
	{
		// If a mine was revealed

		boolean mineExploded = false;
		for(int i = 0; i < rows && !mineExploded; i++)
		{
			for(int j = 0; j < columns && !mineExploded; j++)
			{
				if(tiles[i][j].state() == State.MINE)
				{
					mineExploded = true;
				}
			}
		}
		return mineExploded;
	}

	/* --- Number Safe --- */

	private int numberSafe()
	{
		int numberSafe = 0;
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				if(tiles[i][j].state() == State.SAFE)
				{
					numberSafe++;
				}
			}
		}
		return numberSafe;
	}

	/* --- Update flags around --- */

	private void updateFlagsAround()
	{
		// Set flagsAround for all tiles to 0

		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				tiles[i][j].resetFlagsAround();
			}
		}

		// For each flag increment flagsAround of 1 for all tiles next to it, reset and increment numberFlag of 1 for each flag

		numberFlag = 0;

		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				if(tiles[i][j].state() == State.FLAG)
				{
					numberFlag++;
					for(int k = -1; k < 2; k++)
					{
						for(int l = -1; l < 2; l++)
						{
							if((i + k) > -1 && (i + k) < rows && (j + l) > -1 && (j + l) < columns)
							{
								tiles[i + k][j + l].incrementFlagAround();
							}
						}
					}
				}
			}
		}
	}

	/* --- Clear Around --- */

	private void clearAround()
	{
		// If a tile wants to be cleared around (0 minesAround OR clicked and flagsAround == minesAround) and is active

		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				if(tiles[i][j].wantsClear() && tiles[i][j].isActive())
				{
					// Deactivate the tile and reveal all hidden tiles around

					tiles[i][j].deactive();
					for(int k = -1; k < 2; k++)
					{
						for(int l = -1; l < 2; l++)
						{
							if((i + k) > -1 && (i + k) < rows && (j + l) > -1 && (j + l) < columns && tiles[i + k][j + l].state() == State.HIDDEN)
							{
								tiles[i + k][j + l].reveal();
							}
						}
					}
				}
			}
		}
	}

	/* --- Update Grid --- */

	/* Logic loop */

	// Called each time a tile is modified

	public void updateGrid()
	{
		// Request focus for keyboard control

		requestFocusInWindow();

		// No operations if the game is finished

		if(!finished)
		{
			// If the mines aren't put yet, and 1 tile was revealed

			if(!gridGenerated && numberSafe()==1)
			{
				putMines();
			}

			// Test if the game was lost / won

			else if(testForWin())
			{
				finishGame(true);
			}
			else if(testForLose())
			{
				finishGame(false);
			}

			// If the game is started and continues

			else
			{
				updateFlagsAround();
				clearAround();
			}
			repaint();
		}
	}

	/* End game */

	// Called if the game has to be finished (won / lost game OR restart using 'R' OR restart using menu bar)

	public void finishGame(boolean win)
	{
		// If lost, invalidating highscore (time won't be used for new best time) and call window to do operations

		if(!win)
		{
			won = false;
			parent.lose();
		}

		// If won, call window to do operations

		else
		{
			won = true;
			parent.win();
		}

		// Signal for time thread to stop

		finished = true;

		// Deactive keyboard control

		tiles[selectedRow][selectedColumn].keyExited();

		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				if(win)
				{
					// If won, put flags on all unrevealed tiles (they're all mined tiles)

					if(tiles[i][j].state() == State.HIDDEN)
					{
						tiles[i][j].flag();
					}
				}
				else
				{
					// If lost, reveal hidden mines and reveal which flags were wrong (if there was wrong flags)

					if(tiles[i][j].state() == State.FLAG && !tiles[i][j].isMined())
					{
						tiles[i][j].wasWrong();
					}
					else if(tiles[i][j].state() == State.HIDDEN && tiles[i][j].isMined())
					{
						tiles[i][j].unexplodedMine();
					}
				}

				// Deactive all tiles

				tiles[i][j].deactive();
			}
		}
		repaint();
	}

	/* --- Keyboard control --- */

	/* KeyListener implementation */

	public void keyPressed(KeyEvent e)
	{
		// If the game is finished, the tile have no more behavior with the keyboard

		if(!finished)
		{
			// Press 'SPACE' OR 'SHIFT' on a tile : it will looks like it was pressed

			if(e.getKeyCode()==32 || e.getKeyCode()==16) // 'SPACE' OR 'SHIFT'
			{
				tiles[selectedRow][selectedColumn].keyPressed();
			}

			// Type on arrows OR 'ZQSD' to change the keyboard-selected tile

			else if(e.getKeyCode()==90 || e.getKeyCode()==38) // 'Z' OR 'UP'
			{
				tiles[selectedRow][selectedColumn].keyExited();
				if(selectedRow != 0)
				{
					selectedRow--;
				}
				else
				{
					selectedRow = rows-1;
				}
				tiles[selectedRow][selectedColumn].selected();
			}
			else if(e.getKeyCode()==81 || e.getKeyCode()==37) // 'Q' OR 'LEFT'
			{
				tiles[selectedRow][selectedColumn].keyExited();
				if(selectedColumn != 0)
				{
					selectedColumn--;
				}
				else
				{
					selectedColumn = columns-1;
				}
				tiles[selectedRow][selectedColumn].selected();
			}
			else if(e.getKeyCode()==83 || e.getKeyCode()==40) // 'S' OR 'DOWN'
			{
				tiles[selectedRow][selectedColumn].keyExited();
				if(selectedRow != rows-1)
				{
					selectedRow++;
				}
				else
				{
					selectedRow = 0;
				}
				tiles[selectedRow][selectedColumn].selected();
			}
			else if(e.getKeyCode()==68 || e.getKeyCode()==39) // 'D' OR 'RIGHT'
			{
				tiles[selectedRow][selectedColumn].keyExited();
				if(selectedColumn != columns-1)
				{
					selectedColumn++;
				}
				else
				{
					selectedColumn = 0;
				}
				tiles[selectedRow][selectedColumn].selected();
			}
		}
   	}

	public void keyReleased(KeyEvent e)
	{
		// Release 'R' to restart a game with the same difficulty, if the game started

		if(e.getKeyCode()==82 && gridGenerated) // 'R'
	   	{
			parent.restartGame(gametype);
	   	}

		// Release 'SPACE' OR 'SHIFT' to reveal or flag the selected tile (on the same tile you pressed the key)

		else if(!finished)
		{
		   	if(e.getKeyCode() == 32) // 'SPACE'
		   	{
			   	tiles[selectedRow][selectedColumn].keyReveal();
		   	}
			else if(e.getKeyCode() == 16) // 'SHIFT'
			{
				tiles[selectedRow][selectedColumn].keyFlag();
			}
		}
	}

	public void keyTyped(KeyEvent e)
	{}

	/* --- Graphic loop --- */

	/* Paint method */

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		repaintAll();
		updateSize();
	}

	/* Repaint all tiles */

	public void repaintAll()
	{
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				if(tiles[i][j].wantsRepaint())
				{
					tiles[i][j].repaint();
				}
			}
		}
	}

	/* Update size */

	// Update size of the grid, choosing the biggest size as possible, keeping the same ratio between rows and columns

	private void updateSize()
	{
		double frameRatio = (double)(parent.getWidth()-20)/(double)(parent.getHeight()-parent.getMenuBarHeigth()-parent.getInsets().top-20);
		if(gridRatio >= frameRatio)
		{
			setMinimumSize(new Dimension(parent.getWidth()-20, (int)((parent.getWidth()-20)/gridRatio)));
		}
		else
		{
			setMinimumSize(new Dimension((int)((parent.getHeight()-parent.getMenuBarHeigth()-parent.getInsets().top-20)*gridRatio), parent.getHeight()-parent.getMenuBarHeigth()-parent.getInsets().top-20));
		}
	}

	/* --- Other method(s) --- */

	// Called at the end of time thread

	public void displayNewHighscore()
	{
		// Get total elapsed time

		long elapsedTime = timeStop-timeStart;

		// If new best time

		if((elapsedTime < highscore || !highscoreValid) && won)
		{
			// Display elapsed time as new highscore

			parent.updateTimerLabel(elapsedTime);
			parent.displayNewHighscore(gametype);

			// Save new highscore

			try
			{
				List<String> lines = Files.readAllLines(Paths.get("Data/Texts/highscores.txt"));
				switch(gametype)
				{
					case EASY:
						lines.set(0 , Long.toString(elapsedTime));
						break;
					case NORMAL:
						lines.set(1 , Long.toString(elapsedTime));
						break;
					case HARD:
						lines.set(2 , Long.toString(elapsedTime));
						break;
				}
				Files.write(Paths.get("Data/Texts/highscores.txt"), lines);
			}
			catch(Exception exception){}
		}
	}
}
