package com.espresoh.memoboard.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.espresoh.memoboard.rcp.views.MemoboardView;

/**
 * 
 * @author cghita
 *
 */
public class RefreshMemosAction implements IViewActionDelegate
{

	// ====================== 2. Instance Fields =============================
	
	private MemoboardView view;
	

	// ==================== 4. Constructors ====================

	@Override
	public void init(IViewPart view)
	{
		this.view = (MemoboardView) view;
	}

	
	// ==================== 6. Action Methods ====================

	@Override
	public void run(IAction action)
	{
		view.refreshMemos(true);
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{

	}
	
}
