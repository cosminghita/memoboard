package com.espresoh.memoboard.rcp.widgets;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DateWidget extends Composite
{

	// ====================== 2. Instance Fields =============================

	private CDateTime dateTime;
	private ControlDecoration decorator;
	private boolean nullable;
	private boolean valid = true;
	private Button nullDateButton;

	// ==================== 4. Constructors ====================

	public DateWidget(final Composite parent, final boolean nullable)
	{
		this(parent, SWT.DATE | SWT.DROP_DOWN, nullable);
	}
	
	
	public DateWidget(final Composite parent, final int style, final boolean nullable)
	{
		super(parent, SWT.BORDER);
		setBackground(parent.getBackground());
		final GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);

		final GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.widthHint = 135;
		setLayoutData(gridData);

		this.nullable = nullable;
		createWidget(this, style);
		createInvalidDecorator();

		dateTime.setEnabled(nullDateButton.getSelection());

	}

	private void createWidget(final Composite parent, final int style)
	{

		nullDateButton = new Button(parent, SWT.CHECK);
		nullDateButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		nullDateButton.setBackground(getBackground());
		nullDateButton.addSelectionListener(new SelectionListener()
		{

			@Override public void widgetSelected(final SelectionEvent e)
			{
				dateTime.setEnabled(nullDateButton.getSelection());
				dateChanged(getDate());
			}

			@Override public void widgetDefaultSelected(final SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		dateTime = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_SHORT | CDT.TIME_SHORT | style);
		final GridData depasitGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		depasitGridData.widthHint = 105;
		dateTime.setLayoutData(depasitGridData);
		// dateTime.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		dateTime.addSelectionListener(new SelectionListener()
		{

			@Override public void widgetSelected(final SelectionEvent e)
			{

				dateChanged(getDate());

				updateValidFlag();

			}

			@Override public void widgetDefaultSelected(final SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

	}

	protected void dateChanged(final Date date)
	{

	}

	private void createInvalidDecorator()
	{

		// Set the layout data for Decorators:
		 final GridData decoGridData = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH, SWT.DEFAULT);
		 decoGridData.horizontalIndent = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();

		decorator = new ControlDecoration(this, SWT.LEFT | SWT.CENTER);
		final FieldDecoration errorFieldIndicator = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		decorator.setImage(errorFieldIndicator.getImage());
		 this.setLayoutData(decoGridData);

		updateValidFlag();

	}

	public void updateValidFlag()
	{
		// Update invalid decorator:
		if (decorator != null)
		{
			if (!isNullable() && getDate() == null)
			{

				decorator.show();
				setValid(false);

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

				// decorator.show();
				//
				//				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
				updateValidFlag();
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

	// ==================== 7. Getters & Setters ====================

	public boolean isValid()
	{
		return valid;
	}

	public void setValid(final boolean valid)
	{
		this.valid = valid;
	}

	public CDateTime getTheWidget()
	{
		return dateTime;
	}

	public Button getNullDateButton()
	{
		return nullDateButton;
	}

	public Date getDate()
	{
		if (!nullDateButton.getSelection())
			return null;

		return dateTime.getSelection();
//		return getCalendarFromWidget(dateTime).getTime();
	}

	public Calendar getCal()
	{
		if (!nullDateButton.getSelection())
			return null;

		return getCalendarFromWidget(dateTime);
	}

	public void setDate(final Date date)
	{
		if (date != null)
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			setDateCal(cal);
			nullDateButton.setSelection(true);
		} else
			nullDateButton.setSelection(false);

		dateTime.setEnabled(nullDateButton.getSelection());

	}

	public void setDateCal(final Calendar cal)
	{
		if (cal != null)
		{
			setCalendarToWidget(cal, dateTime);
			nullDateButton.setSelection(true);

		} else
			nullDateButton.setSelection(false);

		dateTime.setEnabled(nullDateButton.getSelection());
	}

	public static Calendar getCalendarFromWidget(final CDateTime dateTime)
	{
		Date date = dateTime.getSelection();
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());

//		cal.set(Calendar.YEAR, dateTime.getYear());
//		cal.set(Calendar.MONTH, dateTime.getMonth());
//		cal.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
//		cal.set(Calendar.HOUR, dateTime.getHours());
//		cal.set(Calendar.MINUTE, dateTime.getMinutes());
//		cal.set(Calendar.SECOND, dateTime.getSeconds());
		return cal;
	}

	/**
	 * update given widget value with give calendar
	 * 
	 * @param cal
	 * @param widget
	 */
	public static void setCalendarToWidget(final Calendar cal, final CDateTime widget)
	{
		widget.setSelection(cal.getTime());
//		widget.setTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
	}

	public void addSelectionListener(final SelectionListener listener)
	{
		dateTime.addSelectionListener(listener);
		nullDateButton.addSelectionListener(listener);

	}

	public void removeSelectionListener(final SelectionListener listener)
	{
		dateTime.removeSelectionListener(listener);
		nullDateButton.removeSelectionListener(listener);
	}

}
