package com.espresoh.memoboard.rcp.components;

import java.util.Collection;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * Common block of add/remove button used everywhere we have to table from which we let the user choose elements.
 * 
 * If the top and bottom viewer are provided, dbclick listeners are added to add or removed selected elements.
 * Also selection listeners are added in order to enable/disable buttons.
 * 
 * @author cghita
 * 
 */
public abstract class AddRemoveButtonsBlock extends Composite
{

	// ====================== 2. Instance Fields =============================

	/**
	 * Show buttons horizontal or vertical.
	 */
	public enum Mode {HORIZONTAL, VERTICAL}
	
	private Button addButton;
	private Button addAllButton;
	private Button removeButton;
	private Button removeAllButton;

	private StructuredViewer allAvailableElements_viewer;
	private StructuredViewer selectedElements_viewer;

	/**
	 * Is used to avoid loop in selection changed listener.
	 */
	private boolean selectionChanging = false;

	// ==================== 4. Constructors ====================

	/**
	 * Show buttons horizontal.
	 * 
	 * @param parent
	 * @param style
	 */
	public AddRemoveButtonsBlock(final Composite parent, final int style)
	{
		this(parent, style, Mode.HORIZONTAL);
	}

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param mode: show buttons horizontal or vertical.
	 */
	public AddRemoveButtonsBlock(final Composite parent, final int style, final Mode mode)
	{
		super(parent, style);
		
		setLayout(new GridLayout(mode == Mode.HORIZONTAL ? 4 : 1, false));
//		setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		createButtons(this);

		addListenersToButtons();

	}


	public AddRemoveButtonsBlock setAllAvailableElements_viewer(final StructuredViewer allAvailableElements_viewer)
	{
		this.allAvailableElements_viewer = allAvailableElements_viewer;
		
		addListenersTo_allAvailableElements_viewer();
		
		return this;
	}


	public AddRemoveButtonsBlock setSelectedElements_viewer(final StructuredViewer selectedElements_viewer)
	{
		this.selectedElements_viewer = selectedElements_viewer;
		
		addListenersTo_selectedElements_viewer();
		
		return this;
	}


	// ==================== 5. Creators ====================

	private void createButtons(final Composite parent)
	{

		addButton = new Button(parent, SWT.NONE);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		addButton.setEnabled(false);

		addAllButton = new Button(parent, SWT.NONE);
		addAllButton.setText("Add all");
		addAllButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		removeButton = new Button(parent, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		removeButton.setEnabled(false);

		removeAllButton = new Button(parent, SWT.NONE);
		removeAllButton.setText("Remove all");
		removeAllButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		removeAllButton.setEnabled(false);

	}


	/**
	 * add listeners to add, remove... buttons
	 */
	private void addListenersToButtons()
	{

		addButton.addSelectionListener(new SelectionAdapter()
		{
			@Override public void widgetSelected(final SelectionEvent e)
			{
				addSelectedInternal();
			}
		});

		addAllButton.addSelectionListener(new SelectionAdapter()
		{
			@Override public void widgetSelected(final SelectionEvent e)
			{
				final Collection<?> changedElems = addAllToSelected();
				refreshViewers_Internal();
				applySelectionTo_selectedElements_viewer(changedElems);
			}
		});
		
		removeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override public void widgetSelected(final SelectionEvent e)
			{
				removeSelectedInternal();

			}
		});
		
		removeAllButton.addSelectionListener(new SelectionAdapter()
		{
			@Override public void widgetSelected(final SelectionEvent e)
			{
				final Collection<?> changedElems = removeAllFromSelected();
				refreshViewers_Internal();
				applySelectionTo_allAvailableElements_viewer(changedElems);
			}
		});
	}

	
	/**
	 * 
	 */
	private void addListenersTo_allAvailableElements_viewer()
	{
		allAvailableElements_viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override public void doubleClick(final DoubleClickEvent event)
			{
//				addToSelected();
//				
//				refreshViewers_Internal();
				
				addSelectedInternal();

			}
		});
		
		allAvailableElements_viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			
			@Override public void selectionChanged(final SelectionChangedEvent event)
			{
				if (selectionChanging)
					return;

				enableButtons();
			}
		});
		
	}


	/**
	 * 
	 */
	private void addListenersTo_selectedElements_viewer()
	{
		selectedElements_viewer.addDoubleClickListener(new IDoubleClickListener()
		{

			@Override
			public void doubleClick(final DoubleClickEvent event)
			{
				
				removeSelectedInternal();

//				removeFromSelected();
//				
//				refreshViewers_Internal();

			}
		});
		
		selectedElements_viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			
			@Override public void selectionChanged(final SelectionChangedEvent event)
			{
				if (selectionChanging)
					return;

				enableButtons();
			}
		});

	}

	
	/**
	 * enable or disable buttons according selections made in tables
	 */
	protected void enableButtons() {
		
		addAllButton.setEnabled(!getAllAvailable().isEmpty());
		addButton.setEnabled(!getSelectionFromAvailable().isEmpty());

		removeAllButton.setEnabled(!getAllSelected().isEmpty());
		removeButton.setEnabled(!getSelectionFromSelected().isEmpty());

	}

	/**
	 * This is called each time an add/remove action happens.
	 * Also adjust the button enablement.
	 */
	public void refreshViewers_Internal()
	{
		refreshViewers();
		enableButtons();
	}

	protected void applySelectionTo_allAvailableElements_viewer(final Collection<?> changedElems)
	{
		selectionChanging = true;
		allAvailableElements_viewer.setSelection(new StructuredSelection(changedElems.toArray()));
		selectionChanging = false;
		
		enableButtons();
	}

	
	protected void applySelectionTo_selectedElements_viewer(final Collection<?> changedElems)
	{
		selectionChanging = true;
		selectedElements_viewer.setSelection(new StructuredSelection(changedElems.toArray()));
		selectionChanging = false;
		
		enableButtons();

	}

	
	public AddRemoveButtonsBlock hide_AddAll_RemoveAll_buttons()
	{
		addAllButton.setVisible(false);
		removeAllButton.setVisible(false);
		return this;
	}
	
	/**
	 * This method is called from dbclick on available viewer and from add button. 
	 * Add selected elems from available to selected choices. 
	 */
	private void addSelectedInternal()
	{
		final Collection<?> changedElems = addToSelected();
		refreshViewers_Internal();
		applySelectionTo_selectedElements_viewer(changedElems);
	}

	/**
	 * This method is called from dbclick on selected viewer and from remove button.
	 * Removes from selected choices the selected elems.
	 */
	private void removeSelectedInternal()
	{
		final Collection<?> changedElems = removeFromSelected();
		refreshViewers_Internal();
		applySelectionTo_allAvailableElements_viewer(changedElems);
	}

	
	/**
	 * Clears the selected list.
	 * @return affected elements
	 */
	abstract protected Collection<?> removeAllFromSelected();


	/**
	 * Remove one or more elements from selected list.
	 * @return affected elements
	 */
	abstract protected Collection<?> removeFromSelected();


	/**
	 * Adds all available elements to selected list.
	 * @return affected elements
	 */
	abstract protected Collection<?> addAllToSelected();


	/**
	 * Adds one or more elements to selected list.
	 * @return affected elements
	 */
	abstract protected Collection<?> addToSelected();

	/**
	 * This is called each time an add/remove action happens.
	 */
	abstract protected void refreshViewers();
	
	/**
	 * @return The selection from the available elements viewer
	 */
	abstract protected Collection<?> getSelectionFromAvailable();
	
	/**
	 * @return The selection from the selected elements viewer
	 */
	abstract protected Collection<?> getSelectionFromSelected();

	/**
	 * @return All the available elements that are not added to the selected (unselected elements).
	 */
	abstract protected Collection<?> getAllAvailable();
	
	/**
	 * @return selected elements (selected elements in the selected viewer).
	 */
	abstract protected Collection<?> getAllSelected();


}
