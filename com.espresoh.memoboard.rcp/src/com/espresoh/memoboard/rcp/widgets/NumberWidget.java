package com.espresoh.memoboard.rcp.widgets;

import java.math.BigDecimal;

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



public class NumberWidget extends Composite {

	private ControlDecoration decorator;
	private Text textControl;
	private final boolean nullable;
	private ModifyListener modifyListener;
	private boolean bigDecimal = false;
	private int maxLength = 0;
	private int minLength = 0;
	private boolean valid = true;

	public boolean isValid() {
		return valid;
	}

	public void setValid(final boolean valid) {
		this.valid = valid;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(final int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(final int minLength) {
		this.minLength = minLength;
	}


	public NumberWidget(final Composite parent, final int style, final boolean nullable) {
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
	public NumberWidget(final Composite parent, final boolean nullable) {
		this(parent, SWT.NONE, nullable);

	}

	protected void createTextWidget() {
		textControl = new Text(this, SWT.BORDER | SWT.RIGHT);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gridData.widthHint = 120;
		textControl.setLayoutData(gridData);

		ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(final ModifyEvent e) {

				dirtyHappened();
				updateValidFlag();
			}

		};

		addListeners();
		textControl.addModifyListener(modifyListener);
	}

	private void addListeners() {
		modifyListener = new ModifyListener() {

			/**
			 * If we get here, verification was successful:
			 */
			public void modifyText(final ModifyEvent e) {

				BigDecimal widgetValue = null;

				// If a user types a Decimal Point we put a Zero in front of it:
				String result = textControl.getText();
				
				result = result.replace(",", ".");
				
				if (result.contains(".")) {//$NON-NLS-1$ //$NON-NLS-2$

					textControl.removeModifyListener(modifyListener);

					int caretporsition = textControl.getCaretPosition();
					
					if (".".equals(result) || ",".equals(result)) {
						textControl.setText("0" + result); //$NON-NLS-1$
					} else
						textControl.setText(result); //$NON-NLS-1$

					textControl.setSelection(caretporsition);


					textControl.addModifyListener(modifyListener);
				}

				try {

					widgetValue = new BigDecimal(textControl.getText()).stripTrailingZeros();

				} catch (NumberFormatException nfe) {

					// if exception has been thrown not because the control
					// returned

					// the empty string, but because the string inconvertible,
					// then throw away event and do nothing:

					if (!textControl.getText().equals(""))

						return;
				}

				dirtyHappened();
			}

		};

		textControl.addModifyListener(modifyListener);

		textControl.addVerifyListener(new VerifyListener() {

			/**
			 * Verifies general number related rules: 1 - no alpha character
			 * allowed 2 - no minus sign after a number 3 - enforce number of
			 * decimal digits
			 */
			public void verifyText(final VerifyEvent e) {
				// Assemble the current content of the widget:
				String text = ((Text) e.widget).getText();
				String result = text.substring(0, e.start) + e.text + text.substring(e.end);
				// Allow these characters anyway:
				switch (e.character) {
				case SWT.BS:
				case SWT.DEL:
				case SWT.ESC:
					return;
				}
				// Step 1a: Empty value is OK:
				if (result.length() == 0)

					return;
				if (result.length() > maxLength && maxLength != 0)
					e.doit = false;

				result = result.replace(",", ".");
				if (".".equals(result) || ",".equals(result)) //$NON-NLS-1$ //$NON-NLS-2$

					result = "0" + result; //$NON-NLS-1$

				try {
					if (!bigDecimal) {
						new Long(result);
					} else {

						if ("-".equals(result)) { //$NON-NLS-1$

							e.doit = false;
						}
						new BigDecimal(result);
					}
				} catch (NumberFormatException nfe) {
					e.doit = false;
				}

			}

		});
	}

	protected void dirtyHappened() {

	}

	private void createInvalidDecorator() {

		// Set the layout data for Decorators:
		GridData decoGridData = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH, SWT.DEFAULT);
		decoGridData.horizontalIndent = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();

		decorator = new ControlDecoration(this, SWT.LEFT | SWT.CENTER);
		final FieldDecoration errorFieldIndicator = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		decorator.setImage(errorFieldIndicator.getImage());
		this.setLayoutData(decoGridData);

	}

	public void hideDecoratorTemprarly(final boolean hide){
		if (decorator != null) {
			if (!hide) {

				decorator.show();

				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
			} else {

				decorator.hide();
			}
		}
	}

	public Text getTheWidget() {
		return textControl;
	}

	public void updateValidFlag() {
		// Update invalid decorator:
		if (decorator != null) {
			if (!isNullable() && textControl.getText().length() == 0 || (minLength != 0 && textControl.getText().length() < minLength)) {

				decorator.show();
				setValid(false);
				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
			} else {

				decorator.hide();
				setValid(true);
				// decorator.setDescriptionText(model.getValidationManager().
				// getViolationsSummary());
			}
		}
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNumber(final BigDecimal number) {
		if (number != null)
			textControl.setText(number.stripTrailingZeros().toPlainString());
	}

	public void setNumber(final long number) {
		textControl.setText(number + "");
	}

	public BigDecimal getNumberBigDecimal() {
		try {
			BigDecimal number = new BigDecimal(textControl.getText()).stripTrailingZeros();
			return number;
		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
		return BigDecimal.ZERO;
	}

	public long getNumber() {
		try {
			return new Long(textControl.getText());
		} catch (NumberFormatException e) {
		}
		return new Long(0);
	}

	public boolean isBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(final boolean bigDecimal) {
		this.bigDecimal = bigDecimal;
	}
}
