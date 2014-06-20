package com.espresoh.memoboard.rcp.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.espresoh.memoboard.rcp.Activator;

/**
 * Contains the general, user-specific preferences for iREMS.
 * 
 * @author akozma
 * 
 */
public class MemoboardPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  
{

	// ==================== 2. Instance Fields ============================

	private Composite container;

	
	// ==================== 4. Constructors ====================

	public MemoboardPreferencePage()
	{
		super(GRID);

		// setPrefStoreAndDesc();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("MemoBoard preferences.");
	}

	@Override
	public void init(IWorkbench workbench)
	{
		
	}

	
	// ==================== 5. Creators ===================================

	@Override
	protected void createFieldEditors() 
	{
		container = new Composite(getFieldEditorParent(), SWT.NONE);

		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		addField(new StringFieldEditor(IPreferenceKeys.SERVER_URL, "Server", container));
	}

	

}
