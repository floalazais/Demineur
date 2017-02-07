import javax.swing.JTextField;

import java.awt.Color;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class IntField extends JTextField implements FocusListener, DocumentListener
{
    /* --- Members --- */

    private CustomDialog parent;
    private int minimum;
    private int maximum;
    private int content;
    private boolean valid = false;
    private String ghostText;

    /* --- Constructors --- */

    /* Forbidden default constructor */

    private IntField(){}

    /* To-use constructor when unknown min/max */

    public IntField(CustomDialog _parent)
    {
        super();

        parent = _parent;
        minimum = 0;
        maximum = 0;
        ghostText = "";

        getDocument().addDocumentListener(this);
        addFocusListener(this);
    }

    /* To-use constructor when known min/max */

    public IntField(CustomDialog _parent, int _minimum, int _maximum)
    {
        super();

        parent = _parent;
        minimum = _minimum;
        maximum = _maximum;
        ghostText = "min "+minimum+", max "+maximum;

        setText(ghostText);
        setForeground(Color.LIGHT_GRAY);

        getDocument().addDocumentListener(this);
        addFocusListener(this);
    }

    /* --- Getters --- */

    /* WARNING : use getContent() only if isValid() returns TRUE */

    public int getContent()
    {
        return content;
    }

    public boolean isValid()
    {
        return valid;
    }

    /* --- Setters --- */

    public void setMax(int _maximum)
    {
        maximum = _maximum;
        ghostText = "min "+minimum+", max "+maximum;

        setText(ghostText);
        setForeground(Color.LIGHT_GRAY);
    }

    public void setMin(int _minimum)
    {
        minimum = _minimum;
        ghostText = "min "+minimum+", max "+maximum;

        setText(ghostText);
        setForeground(Color.LIGHT_GRAY);
    }

    /* --- Interface(s) implementation(s) --- */

    /* FocusListener implementation */

    public void focusGained(FocusEvent event)
    {
        // If the user want to change a numeric value, it keeps it, if it isn't a valid value, it'll be cleared

        if(!valid)
        {
            setText("");
            setForeground(Color.BLACK);
        }
    }

    public void focusLost(FocusEvent event)
    {
        // If nothing was entered, gray ghost text is displayed

        if(getText().isEmpty())
        {
            setText(ghostText);
            setForeground(Color.LIGHT_GRAY);
        }
        // If something not valid was entered, red ghost text is displayed

        else if(!valid)
        {
            setText(ghostText);
            setForeground(Color.RED);
        }
    }

    /* DocumentListener implementation */

    // Always check if the content became valid or not
    // Always inform CustomDialog that an IntField was modified

    public void changedUpdate(DocumentEvent event)
    {
        updateContent();
        parent.updateIntFields(this);
    }

    public void insertUpdate(DocumentEvent event)
    {
        updateContent();
        parent.updateIntFields(this);
    }

    public void removeUpdate(DocumentEvent event)
    {
        updateContent();
        parent.updateIntFields(this);
    }

    public void updateContent()
    {
        // If new content is numeric and between min and max, then it's valid

        try
        {
            if(Integer.parseInt(getText()) >= minimum && Integer.parseInt(getText()) <= maximum)
            {
                content = Integer.parseInt(getText());
                valid = true;
            }
            else
            {
                valid = false;
            }
        }
        catch(NumberFormatException exception)
        {
            valid = false;
        }
    }

    /* --- Clear method --- */

    // Called when an IntField is set unusable

    public void clear()
    {
        minimum = 0;
        maximum = 0;
        valid = false;
        ghostText = "";

        setText(ghostText);
    }
}
