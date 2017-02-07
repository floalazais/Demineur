import javax.swing.JComponent;

import javax.imageio.ImageIO;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;

public class Tile extends JComponent implements MouseListener
{
	/* --- Static members --- */

	private static final Image IMG_FLAG;
	private static final Image IMG_FLAG_PRESSED;
	private static final Image IMG_FLAG_SELECTED;
	private static final Image IMG_FLAG_BOTH;

	private static final Image IMG_SAFE;
	private static final Image IMG_SAFE_PRESSED;
	private static final Image IMG_SAFE_SELECTED;
	private static final Image IMG_SAFE_BOTH;

	private static final Image IMG_HIDDEN;
	private static final Image IMG_HIDDEN_PRESSED;
	private static final Image IMG_HIDDEN_SELECTED;
	private static final Image IMG_HIDDEN_BOTH;

	private static final Image IMG_WRONG_FLAG;
	private static final Image IMG_UNEXPLODED;
	private static final Image IMG_MINE;

	/* --- Load images --- */

	static
	{
		Image imgFlag = null;
		Image imgFlagPressed = null;
		Image imgFlagSelected = null;
		Image imgFlagBoth = null;

		Image imgSafe = null;
		Image imgSafePressed = null;
		Image imgSafeSelected = null;
		Image imgSafeBoth = null;

		Image imgHidden = null;
		Image imgHiddenPressed = null;
		Image imgHiddenSelected = null;
		Image imgHiddenBoth = null;

		Image imgWrongFlag = null;
		Image imgUnexploded = null;
		Image imgMine = null;

		try
		{
			imgFlag = ImageIO.read(new File("Data/Sprites/flag.png"));
			imgFlagPressed = ImageIO.read(new File("Data/Sprites/flagPressed.png"));
			imgFlagSelected = ImageIO.read(new File("Data/Sprites/flagSelected.png"));
			imgFlagBoth = ImageIO.read(new File("Data/Sprites/flagBoth.png"));

			imgSafe = ImageIO.read(new File("Data/Sprites/safe.png"));
			imgSafePressed = ImageIO.read(new File("Data/Sprites/safePressed.png"));
			imgSafeSelected = ImageIO.read(new File("Data/Sprites/safeSelected.png"));
			imgSafeBoth = ImageIO.read(new File("Data/Sprites/safeBoth.png"));

			imgHidden = ImageIO.read(new File("Data/Sprites/hidden.png"));
			imgHiddenPressed = ImageIO.read(new File("Data/Sprites/hiddenPressed.png"));
			imgHiddenSelected = ImageIO.read(new File("Data/Sprites/hiddenSelected.png"));
			imgHiddenBoth = ImageIO.read(new File("Data/Sprites/hiddenBoth.png"));

			imgWrongFlag = ImageIO.read(new File("Data/Sprites/wrongFlag.png"));
			imgUnexploded = ImageIO.read(new File("Data/Sprites/unexploded.png"));
			imgMine = ImageIO.read(new File("Data/Sprites/mine.png"));
		}
		catch(IOException e){}

		IMG_FLAG = imgFlag;
		IMG_FLAG_PRESSED = imgFlagPressed;
		IMG_FLAG_SELECTED = imgFlagSelected;
		IMG_FLAG_BOTH = imgFlagBoth;

		IMG_SAFE = imgSafe;
		IMG_SAFE_PRESSED = imgSafePressed;
		IMG_SAFE_SELECTED = imgSafeSelected;
		IMG_SAFE_BOTH = imgSafeBoth;

		IMG_HIDDEN = imgHidden;
		IMG_HIDDEN_PRESSED = imgHiddenPressed;
		IMG_HIDDEN_SELECTED = imgHiddenSelected;
		IMG_HIDDEN_BOTH = imgHiddenBoth;

		IMG_WRONG_FLAG = imgWrongFlag;
		IMG_UNEXPLODED = imgUnexploded;
		IMG_MINE = imgMine;
	}

	/* --- Members --- */

	private Font font;
	private Panel parent;
	private int minesAround = 0;
	private int flagsAround = 0;
	private State state = State.HIDDEN;
	private boolean mined = false;
	private boolean active = true;
	private boolean wantsClear = false;
	private boolean wantsRepaint = false;
	private boolean pressed = false;
	private boolean selected = false;

	/* --- Constructors --- */

	/* Forbidden default constructor */

	private Tile(){}

	/* To-use constructor */

	public Tile(Panel _parent)
	{
		parent = _parent;
		addMouseListener(this);
	}

	/* --- Mine --- */

	public boolean isMined()
	{
		return mined;
	}

	public void mine()
	{
		mined = true;
	}

	public int minesAround()
	{
		return minesAround;
	}

	public void incrementMineAround()
	{
		minesAround++;
	}

	/* --- Flags --- */

	public int flagsAround()
	{
		return flagsAround;
	}

	public void resetFlagsAround()
	{
		flagsAround = 0;
	}

	public void incrementFlagAround()
	{
		flagsAround++;
	}

	/* --- State --- */

	public State state()
	{
		return state;
	}

	public void wasWrong()
	{
		state = State.WRONG;
		wantsRepaint = true;
	}

	public void unexplodedMine()
	{
		state = State.UNEXPLODED;
		wantsRepaint = true;
	}

	/* --- Active --- */

	public boolean isActive()
	{
		return active;
	}

	public void deactive()
	{
		active = false;
	}

	/* --- Wants clear --- */

	public boolean wantsClear()
	{
		return wantsClear;
	}

	/* --- Wants Repaint --- */

	public boolean wantsRepaint()
	{
		return wantsRepaint;
	}

	/* --- Selected --- */

	public void selected()
	{
		selected = true;
		repaint();
	}

	/* --- Controls --- */

	// Boolean 'pressed' permit tile to know if a click/type started on it or on another tile
	// Boolean 'active' permit to know if the tile still can do something in the grid
	// Boolean 'selected' permit to know if the tile is selected by the keyboard, if mouse is used, the red select square will be hidden

	/* MouseListener implementation */

	public void mouseClicked(MouseEvent event){}

	public void mouseEntered(MouseEvent event){}

	public void mouseExited(MouseEvent event)
	{
		pressed = false;
		repaint();
	}

	public void mousePressed(MouseEvent event)
	{
		selected = false;
		if(active)
		{
			pressed = true;
			repaint();
		}
	}

	public void mouseReleased(MouseEvent event)
	{
		selected = false;
		if(active && pressed)
		{
			if(event.getButton() == MouseEvent.BUTTON3)
			{
				flag();
			}
			else if(event.getButton() == MouseEvent.BUTTON1)
			{
				reveal();
			}
		}
	}

	/* Keyboard control */

	public void keyExited()
	{
		selected = false;
		pressed = false;
		repaint();
	}

	public void keyPressed()
	{
		if(active)
		{
			pressed = true;
			repaint();
		}
	}

	public void keyReveal()
	{
		if(active && pressed)
		{
			reveal();
		}
	}

	public void keyFlag()
	{
		if(active && pressed)
		{
			flag();
		}
	}

	/* Actions */

	public void reveal()
	{
		pressed = false;

		// If the tile wasn't revealed and not flagged

		if(state == State.HIDDEN)
		{
			// And not mined

			if(mined == false)
			{
				state = State.SAFE;

				// And has no mines around (if there is mines on the grid) ask for a clear

				if(minesAround == 0 && parent.isGridGenerated())
				{
					wantsClear = true;
				}
			}
			else
			{
				state = State.MINE;
			}
		}
		// If the tile was revealed and has the same amount of minesAround and flagsAround, ask for a clear

		else if(state == State.SAFE && flagsAround == minesAround)
		{
			wantsClear = true;
		}
		// Say to the grid that a tile changed logically and graphically

		parent.updateGrid();
		wantsRepaint = true;
	}

	public void flag()
	{
		pressed = false;

		// Switch the tile state : flagged/not flagged

		if(state == State.HIDDEN && parent.isGridGenerated() && !parent.tooMuchFlags())
		{
			state = State.FLAG;
		}
		else if(state == State.FLAG)
		{
			state = State.HIDDEN;
		}
		// Say to the grid that a tile changed logically and graphically

		parent.updateGrid();
		wantsRepaint = true;
	}

	/* --- Graphical loop --- */

	public void paintComponent(Graphics g)
	{
		wantsRepaint = false;
		super.paintComponent(g);

		if(state == State.FLAG)
		{
			if(pressed && selected)
			{
				g.drawImage(IMG_FLAG_BOTH, 0, 0, getWidth(), getHeight(), this);
			}
			else if(pressed)
			{
				g.drawImage(IMG_FLAG_PRESSED, 0, 0, getWidth(), getHeight(), this);
			}
			else if(selected)
			{
				g.drawImage(IMG_FLAG_SELECTED, 0, 0, getWidth(), getHeight(), this);
			}
			else
			{
				g.drawImage(IMG_FLAG, 0, 0, getWidth(), getHeight(), this);
			}
		}
		else if(state == State.MINE)
		{
			g.drawImage(IMG_MINE, 0, 0, getWidth(), getHeight(), this);
		}
		else if(state == State.HIDDEN)
		{
			if(pressed && selected)
			{
				g.drawImage(IMG_HIDDEN_BOTH, 0, 0, getWidth(), getHeight(), this);
			}
			else if(pressed)
			{
				g.drawImage(IMG_HIDDEN_PRESSED, 0, 0, getWidth(), getHeight(), this);
			}
			else if(selected)
			{
				g.drawImage(IMG_HIDDEN_SELECTED, 0, 0, getWidth(), getHeight(), this);
			}
			else
			{
				g.drawImage(IMG_HIDDEN, 0, 0, getWidth(), getHeight(), this);
			}
		}
		else if(state == State.UNEXPLODED)
		{
			g.drawImage(IMG_UNEXPLODED, 0, 0, getWidth(), getHeight(), this);
		}
		else if(state == State.SAFE)
		{
			if(pressed && selected)
			{
				g.drawImage(IMG_SAFE_BOTH, 0, 0, getWidth(), getHeight(), this);
			}
			else if(selected)
			{
				g.drawImage(IMG_SAFE_SELECTED, 0, 0, getWidth(), getHeight(), this);
			}
			else if(pressed)
			{
				g.drawImage(IMG_SAFE_PRESSED, 0, 0, getWidth(), getHeight(), this);
			}
			else
			{
				g.drawImage(IMG_SAFE, 0, 0, getWidth(), getHeight(), this);
			}
			if(minesAround > 0)
			{
				switch(minesAround)
				{
					case 1:
						g.setColor(Color.blue);
						break;
					case 2:
						g.setColor(Color.cyan);
						break;
					case 3:
						g.setColor(Color.green);
						break;
					case 4:
						g.setColor(Color.yellow);
						break;
					case 5:
						g.setColor(Color.orange);
						break;
					case 6:
						g.setColor(Color.red);
						break;
					case 7:
						g.setColor(Color.magenta);
						break;
					case 8:
						g.setColor(Color.black);
						break;
				}
				font = new Font("Data/Font/Roboto-Regular.ttf", Font.BOLD, getWidth()/2);
				g.setFont(font);
				g.drawString("" + minesAround, (int)(getWidth()/2.5), (int)(getHeight()/1.5));
			}
		}
		else if(state == State.WRONG)
		{
			g.drawImage(IMG_WRONG_FLAG, 0, 0, getWidth(), getHeight(), this);
		}
	}
}
