package com.espresoh.memoboard.rcp;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}


	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return new ApplicationActionBarAdvisor(configurer);
	}


	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 800));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("MemoBoard");
	}


	@Override
	public void postWindowOpen()
	{
		super.postWindowOpen();
		
//		Shell workbenchWindowShell = getWindowConfigurer().getWindow().getShell();
//		workbenchWindowShell.setVisible(false);
//		workbenchWindowShell.setMinimized(true);

	}
	
	@Override
	public boolean preWindowShellClose()
	{
//		Shell workbenchWindowShell = getWindowConfigurer().getWindow().getShell();
//		workbenchWindowShell.setVisible(false);
//		workbenchWindowShell.setMinimized(true);

		return false;
	}

}
