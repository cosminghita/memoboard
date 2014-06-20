package com.espresoh.memoboard.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
//		layout.addView(MemoboardView.ID, IPageLayout.TOP, 0.95f, layout.getEditorArea());
	}

}
