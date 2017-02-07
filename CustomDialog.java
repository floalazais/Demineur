import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomDialog extends JDialog implements ActionListener
{
	/* --- Members --- */

	private int rows;
	private int columns;
	private int mines;
	private boolean valid = false;
	private IntField rowsField = new IntField(this, 9, 24);
	private IntField columnsField = new IntField(this, 9, 30);
	private IntField minesField = new IntField(this);
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");

	/* --- Constructors --- */

	/* Forbidden default constructor */

	private CustomDialog(){}

	/* To-use constructor */

	// Dialog part

	public CustomDialog(JFrame _parent, String _title, boolean _modal)
	{
		super(_parent, _title, _modal);

		fillComponent();
        pack();
        cancelButton.requestFocusInWindow();

        setSize(300, 150);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setVisible(true);
	}

	// Components part

	private void fillComponent()
	{
		// Fields container

		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridLayout(3, 2));

			//Fields

			fieldsPanel.add(new JLabel("Rows : ", JLabel.CENTER));
			fieldsPanel.add(rowsField);

			fieldsPanel.add(new JLabel("Columns : ", JLabel.CENTER));
			fieldsPanel.add(columnsField);

			fieldsPanel.add(new JLabel("Mines : ", JLabel.CENTER));
			minesField.setEditable(false);
			fieldsPanel.add(minesField);

		// Buttons container

		JPanel buttonsPanel = new JPanel();

			// Buttons

			okButton.setEnabled(false);
			okButton.addActionListener(this);
			buttonsPanel.add(okButton, BorderLayout.WEST);

			cancelButton.addActionListener(this);
			buttonsPanel.add(cancelButton, BorderLayout.EAST);

		// Piecing both parts together

		getContentPane().add(fieldsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}

	/* --- Getters --- */

		public int getRows()
		{
			return rows;
		}

		public int getColumns()
		{
			return columns;
		}

		public int getMines()
		{
			return mines;
		}

		public boolean isValid()
		{
			return valid;
		}

	/* --- Interface(s) implementation(s) --- */

	/* ActionListener implementation */

	public void actionPerformed(ActionEvent event)
	{
		// If 'OK' button pressed, test if all parameters are OK, then get values and close dialog

		if(event.getSource() == okButton)
		{
			rows = rowsField.getContent();
            columns = columnsField.getContent();
            mines = minesField.getContent();
			valid = true;
            setVisible(false);
		}

		// If 'Cancel' button pressed, close dialog and inform that values aren't valid

		else if(event.getSource() == cancelButton)
		{
			valid = false;
            setVisible(false);
		}
	}

	/* --- Update components method --- */

    public void updateIntFields(IntField field)
    {
		// When rows or columns are modified, test if they're valid to let the user use minesField (minesField has a min/max matching with rows/columns)

		if(field == rowsField || field == columnsField)
		{
			if(rowsField.isValid() && columnsField.isValid())
			{
				minesField.setEditable(true);
				minesField.setMin(10);
				minesField.setMax((int)(rowsField.getContent()*columnsField.getContent()*0.8));
			}
			else
			{
				minesField.setEditable(false);
				minesField.clear();
			}
		}

		// When mines are modified, test if they're valid to let the user use the 'OK' button (mines are valid involves that rows and columns too)

		else if(field == minesField)
		{
			if(minesField.isValid())
			{
				okButton.setEnabled(true);
			}
			else
			{
				okButton.setEnabled(false);
			}
		}
	}
}
