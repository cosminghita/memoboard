package com.espresoh.memoboard.rcp.helpers;

import org.eclipse.swt.layout.GridLayout;

public class UIHelper
{

	public static GridLayout getGridLayout_noSpancings(GridLayout layout)
	{
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;

		return layout;
	}
}
