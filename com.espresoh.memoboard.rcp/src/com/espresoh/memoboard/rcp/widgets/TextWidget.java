package com.espresoh.memoboard.rcp.widgets;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextWidget extends Composite
{

	private ControlDecoration decorator;
	private Text textControl;
	private final boolean nullable;
	private int maxLength = 0;
	private int minLength = 0;
	private boolean valid = true;

	private Object model;

	public boolean isValid()
	{
		return valid;
	}

	public void setValid(final boolean valid)
	{
		this.valid = valid;
	}

	public int getMaxLength()
	{
		return maxLength;
	}

	public void setMaxLength(final int maxLength)
	{
		this.maxLength = maxLength;
		updateValidFlag();

	}

	public TextWidget(final Composite parent, final int style, final boolean nullable)
	{
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);
		
		this.nullable = nullable;
		
		createTextWidget();
		createInvalidDecorator();
		updateValidFlag();
	}

	public TextWidget(final Composite parent, final boolean nullable)
	{
		this(parent, SWT.NONE, nullable);
	}

	protected void createTextWidget()
	{
		textControl = new Text(this, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		// gridData.widthHint = 120;
		textControl.setLayoutData(gridData);

		ModifyListener modifyListener = new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{

				dirtyHappened();
				updateValidFlag();
			}

		};

		textControl.addModifyListener(modifyListener);
		textControl.addVerifyListener(new VerifyListener()
		{

			/**
			 * Verifies general number related rules: 1 - no alpha character allowed 2 - no minus sign after a number 3 - enforce number of decimal digits
			 */
			@Override
			public void verifyText(final VerifyEvent e)
			{
				// Assemble the current content of the widget:
				String text = ((Text) e.widget).getText();
				String result = text.substring(0, e.start) + e.text + text.substring(e.end);

				// Step 1a: Empty value is OK:
				if (result.length() == 0 && maxLength == 0)

					return;
				if (result.length() > maxLength && maxLength != 0)
					e.doit = false;

			}

		});

	}

	protected void dirtyHappened()
	{

	}

	private void createInvalidDecorator()
	{

		// Set the layout data for Decorators:
		GridData decoGridData = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH, SWT.DEFAULT);
		decoGridData.horizontalIndent = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();

		decorator = new ControlDecoration(this, SWT.LEFT | SWT.CENTER);
		final FieldDecoration errorFieldIndicator = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		decorator.setImage(errorFieldIndicator.getImage());
		this.setLayoutData(decoGridData);

	}

	public Text getTheWidget()
	{
		return textControl;
	}

	public void updateValidFlag()
	{
		// Update invalid decorator:
		if (decorator != null)
		{
			if (!isNullable() && textControl.getText().length() == 0 || (minLength != 0 && textControl.getText().length() < minLength))
			{

				setValid(false);
				decorator.show();

				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
			} else
			{

				decorator.hide();
				setValid(true);

				// decorator.setDescriptionText(model.getValidationManager().getViolationsSummary());
			}
		}
	}

	public void hideDecoratorTemprarly(final boolean hide)
	{
		if (decorator != null)
		{
			if (!hide)
			{

				decorator.show();

				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
			} else
			{

				decorator.hide();
			}
		}
	}

	public boolean isNullable()
	{
		return nullable;
	}

	public void setText(final String text)
	{
		if (text != null)
			textControl.setText(text);
	}

	public String getText()
	{
		return textControl.getText();
	}

	public int getMinLength()
	{
		return minLength;
	}

	public void setMinLength(final int minLength)
	{
		this.minLength = minLength;
		updateValidFlag();
	}

	public Text getTextControl()
	{
		return textControl;
	}

	public void setModel(final Object model)
	{
		this.model = model;
	}

	public Object getModel()
	{
		return model;
	}
}
