package com.espresoh.memoboard.rcp.google;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class GoogleSigninDialog extends TitleAreaDialog
{

	public GoogleSigninDialog(Shell parentShell)
	{
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite createDialogArea = (Composite) super.createDialogArea(parent);
		
		Browser browser = new Browser(createDialogArea, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl(Signin.LOGIN_SERVER_URL);
		
		
		return createDialogArea;
	}

}
