package com.espresoh.memoboard.rcp.widgets;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;



public class ComboWidget extends Composite 
{
	private ControlDecoration decorator;
	private ComboViewer comboControl;
	private final boolean nullable;
	private boolean valid = true;

	public boolean isValid() {
		return valid;
	}

	public void setValid(final boolean valid) {
		this.valid = valid;
	}


	public ComboWidget(final Composite parent, final int style, final boolean nullable) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);
		this.nullable = nullable;
		createTextWidget();
		createInvalidDecorator();

	}

	protected void createTextWidget() {
		comboControl = new ComboViewer(this, SWT.BORDER | SWT.READ_ONLY);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gridData.widthHint = 120;
		comboControl.getCombo().setLayoutData(gridData);

		// ModifyListener modifyListener = new ModifyListener() {
		//
		// public void modifyText(ModifyEvent e) {
		//
		// dirtyHappened();
		// updateValidFlag();
		// }
		//
		// };
		//
		// comboControl.getCombo().addModifyListener(modifyListener);

		comboControl.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				dirtyHappened();
				updateValidFlag();

			}

		});

		comboControl.setContentProvider(new ArrayContentProvider());
	}

	protected void dirtyHappened() {

	}

	private void createInvalidDecorator() {

		// Set the layout data for Decorators:
		final GridData decoGridData = new GridData(IDialogConstants.ENTRY_FIELD_WIDTH, SWT.DEFAULT);
		decoGridData.horizontalIndent = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth();

		decorator = new ControlDecoration(this, SWT.LEFT | SWT.CENTER);
		final FieldDecoration errorFieldIndicator = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		decorator.setImage(errorFieldIndicator.getImage());
		this.setLayoutData(decoGridData);

		updateValidFlag();

	}

	public ComboViewer getTheWidget() {
		return comboControl;
	}

	public void updateValidFlag() {
		// Update invalid decorator:
		if (decorator != null) {
			if (!isNullable() && (comboControl.getSelection().isEmpty() && comboControl.getCombo().getSelectionIndex() < 0)) {

				decorator.show();
				setValid(false);

				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
			} else {

				decorator.hide();
				setValid(true);
				// decorator.setDescriptionText(model.getValidationManager().getViolationsSummary());
			}
		}
	}

	public void hideDecoratorTemprarly(final boolean hide){
		if (decorator != null) {
			if (!hide) {

				//				decorator.show();
				//
				//				decorator.setDescriptionText("Campul nu poate fi gol"); //$NON-NLS-1$
				updateValidFlag();
			} else {

				decorator.hide();
			}
		}
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setSelection(final IStructuredSelection selection) {
		comboControl.setSelection(selection, true);
	}

	public StructuredSelection getSelection() {
		return (StructuredSelection)comboControl.getSelection();
	}

	public void setLabelProvider(final IBaseLabelProvider labelProvider) {
		comboControl.setLabelProvider(labelProvider);
	}
	
	public void setModel(final Object model) {
		if (model != null)
			setSelection(new StructuredSelection(model));
	}
	
	public Object getModel() {
		return getSelection().getFirstElement();
	}

	public void setInput(final Object input) {
		comboControl.setInput(input);

	}

	public Combo getCombo() {
		return comboControl.getCombo();
	}

	public ComboViewer getComboControl() {
		return comboControl;
	}
}
