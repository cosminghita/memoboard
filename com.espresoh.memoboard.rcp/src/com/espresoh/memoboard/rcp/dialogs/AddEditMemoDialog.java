package com.espresoh.memoboard.rcp.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.espresoh.memoboard.rcp.components.AddRemoveButtonsBlock;
import com.espresoh.memoboard.rcp.components.UsersTableViewer;
import com.espresoh.memoboard.rcp.session.Session;
import com.espresoh.memoboard.rcp.views.MemoboardView;
import com.espresoh.memoboard.rcp.widgets.DateWidget;
import com.espresoh.memoboard.rcp.widgets.TextWidget;
import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.User;

/**
 * 
 * @author cghita
 *
 */
public class AddEditMemoDialog extends TitleAreaDialog
{

	// ====================== 2. Instance Fields =============================
	
	private Memo memo;
	
	private TextWidget titleWidget;
	private TextWidget contentWidget;
	private Button confirmationWidget;
	private DateWidget dueDateWidget;

	private UsersTableViewer allAvailableUsers_viewer;
	private UsersTableViewer selectedUsers_viewer;
	
	private AddRemoveButtonsBlock addRemoveButtonsBlock;


	private Collection<User> allAvailableUsers;
	private Set<User> selectedUsers;

	// ==================== 4. Constructors ====================

	public AddEditMemoDialog(Shell parentShell, Memo memo)
	{
		super(parentShell);
		this.memo = memo;
		
		//Make sure this will will not be modified by mistake;
		this.allAvailableUsers = Collections.unmodifiableCollection(Session.getInstance().getUsers());
		//Also make a copy of the unique selected elements so the original is not modified:
		this.selectedUsers = new HashSet<>(memo.getTargetUsers());

	}


	// ==================== 5. Creators ====================

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogAreaContainer = (Composite) super.createDialogArea(parent);
		
		createWidgets(dialogAreaContainer);
		
		createTargetUsersWidgets(dialogAreaContainer);
		
		updateWidgetsFromModel();
		
		return dialogAreaContainer;
	}
	
	/**
	 * 
	 * @param parent
	 */
	private void createWidgets(Composite parent)
	{
		Composite widgetsContainer = new Composite(parent, SWT.NONE);
		widgetsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		widgetsContainer.setLayout(new GridLayout(2, false));
		
		new Label(widgetsContainer, SWT.NONE).setText("Title");
		titleWidget = new TextWidget(widgetsContainer, false);

		new Label(widgetsContainer, SWT.NONE).setText("Message");
		contentWidget = new TextWidget(widgetsContainer, false);
		
		new Label(widgetsContainer, SWT.NONE).setText("Due Date");
		dueDateWidget = new DateWidget(widgetsContainer, CDT.CLOCK_24_HOUR, true);
		
		new Label(widgetsContainer, SWT.NONE).setText("With confirmation");
		confirmationWidget = new Button(widgetsContainer, SWT.CHECK);
		confirmationWidget.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
	}
	
	
	private void createTargetUsersWidgets(Composite parent)
	{
		Composite container = new Composite(parent, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		layoutData.minimumHeight = 400;
		layoutData.heightHint = 400;
		container.setLayoutData(layoutData);
		container.setLayout(new GridLayout(1, false));
		
		// --------------------- Available Users -----------------------

		final Group allAvailableGroup = new Group(container, SWT.NONE);
		allAvailableGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		allAvailableGroup.setLayout(new GridLayout(1, false));
		allAvailableGroup.setText("Available Users");
		
		allAvailableUsers_viewer = new UsersTableViewer(allAvailableGroup);
		final GridData allAvailableElements_viewer_layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		allAvailableElements_viewer_layoutData.heightHint = 100;
		allAvailableUsers_viewer.getTableViewer().getTable().setLayoutData(allAvailableElements_viewer_layoutData);

		
		createAddRemoveButtonsBlock(container);
		
		
		// --------------------- Selected Users -----------------------

		final Group selectedGroup = new Group(container, SWT.NONE);
		selectedGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		selectedGroup.setLayout(new GridLayout(1, false));
		selectedGroup.setText("Selected Users");
		
		selectedUsers_viewer = new UsersTableViewer(selectedGroup);
		final GridData selectedElements_viewer_layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		selectedElements_viewer_layoutData.heightHint = 100;
		selectedUsers_viewer.getTableViewer().getTable().setLayoutData(selectedElements_viewer_layoutData);

		//Connect viewer with buttons:
		addRemoveButtonsBlock.setAllAvailableElements_viewer(allAvailableUsers_viewer.getTableViewer());
		addRemoveButtonsBlock.setSelectedElements_viewer(selectedUsers_viewer.getTableViewer());
		
		String action = memo.isNewBean() ? "Create" : "Edit";
		setTitle( action + " Memo");
		setMessage("Please add enough information so the memo purpose is clear.");
		
		
		//Load data in tables and enable buttons:
		addRemoveButtonsBlock.refreshViewers_Internal();
		

	}


	private void createAddRemoveButtonsBlock(Composite container)
	{
		addRemoveButtonsBlock = new AddRemoveButtonsBlock(container, SWT.NONE) 
		{
			
			@Override protected Collection<?> removeFromSelected()
			{
				final List<User> selected = selectedUsers_viewer.getSelected();
				selectedUsers.removeAll(selected);
				return selected;
			}
			
			@Override protected Collection<?> removeAllFromSelected()
			{
				final Collection<User> selected = new HashSet<>(selectedUsers);
				selectedUsers.clear();
				return selected;
			}
			
			@Override protected Collection<?> addToSelected()
			{
				final Collection<User> selected = allAvailableUsers_viewer.getSelected();
				selectedUsers.addAll(allAvailableUsers_viewer.getSelected());
				return selected;
			}
			
			@Override protected Collection<?> addAllToSelected()
			{
				selectedUsers.addAll(allAvailableUsers);
				return allAvailableUsers;
			}
			
			@Override protected void refreshViewers()
			{
				//Maybe we do not need to create an list each time just for the input. Might be expensive !!!!!!!
				allAvailableUsers_viewer.setInput(getAvailableUnselected());
				selectedUsers_viewer.setInput(new ArrayList<>(selectedUsers));
				
				if (getButton(OK) != null)
					getButton(OK).setEnabled(!selectedUsers.isEmpty());
				
			}

			@Override protected Collection<?> getSelectionFromAvailable()
			{
				return allAvailableUsers_viewer.getSelected();
			}

			@Override protected Collection<?> getSelectionFromSelected()
			{
				return selectedUsers_viewer.getSelected();
			}

			@Override protected Collection<?> getAllAvailable()
			{
				return getAvailableUnselected();
			}

			@Override protected Collection<?> getAllSelected()
			{
				return selectedUsers;
			}
		};
		
		addRemoveButtonsBlock.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}
	
	private List<User> getAvailableUnselected()
	{
		final List<User> available = new ArrayList<>(allAvailableUsers);
		available.removeAll(selectedUsers);
		return available;
	}

	// ==================== 6. Action Methods ====================

	@Override
	protected void okPressed()
	{
		updateModelFromWidgets();
		
		//save
		Session.getInstance().save(memo);

		//refresh
		MemoboardView.INSTANCE.refreshMemos(true);

		super.okPressed();
	}
	
	
	// ==================== 8. Business Methods ====================

	private void updateWidgetsFromModel()
	{
		titleWidget.setText(memo.getTitle());
		contentWidget.setText(memo.getContent());
		dueDateWidget.setDate(memo.getDueDate());
		confirmationWidget.setSelection(memo.isRequiresConfirmation());
	}
	
	private void updateModelFromWidgets()
	{
		memo.setTitle(titleWidget.getText());
		memo.setContent(contentWidget.getText());
		memo.setDueDate(dueDateWidget.getDate());
		memo.setRequiresConfirmation(confirmationWidget.getSelection());
		memo.setTargetUsers(selectedUsers);
	}
}
