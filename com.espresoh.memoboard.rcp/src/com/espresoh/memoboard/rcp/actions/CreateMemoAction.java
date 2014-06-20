package com.espresoh.memoboard.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.espresoh.memoboard.rcp.dialogs.AddEditMemoDialog;
import com.espresoh.memoboard.rcp.session.Session;
import com.espresoh.memoboard.rcp.views.MemoboardView;
import com.espresoh.memoboard.server.model.Memo;

/**
 * 
 * @author cghita
 *
 */
public class CreateMemoAction implements IViewActionDelegate
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
		Memo memo = new Memo();
		memo.setOwner(Session.getInstance().getUser());
		new AddEditMemoDialog(view.getSite().getShell(), memo).open();
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{

	}
	
}
