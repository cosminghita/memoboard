package com.espresoh.memoboard.rcp.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.espresoh.memoboard.rcp.components.MemoComponent;
import com.espresoh.memoboard.rcp.session.Session;
import com.espresoh.memoboard.server.model.Memo;

/**
 * 
 * @author cghita
 * 
 */
public class MemoboardView extends ViewPart
{

	private static final int REFRESH_MEMOS_DELAY = 1000 * 5;

	// ==================== 1. Static Fields ========================

	public static final String ID = "com.espresoh.memoboard.rcp.memoboard.view";

	/**
	 * For now we assume that we have only one instance view. Later this view can also
	 * display sender memos so he can supervise the process better.
	 */
	public static MemoboardView INSTANCE;
	
	// ====================== 2. Instance Fields =============================

	private Composite container;
	private Composite parent;


	private ScrolledComposite scrolledComposite;

	private List<Memo> activeMemosList_current = new ArrayList<>(); 

	// ==================== 4. Constructors ====================

	@Override
	public void createPartControl(Composite parent)
	{
		INSTANCE = this;
		
		this.parent = parent;
		parent.setLayout(new GridLayout());
		
		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setExpandHorizontal(true);;
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(new Point(400, 400));
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setLayout(new GridLayout(1, false));

		refreshMemos(false);
	}


	// ==================== 5. Creators ====================

	private void createContainer()
	{
		if (container != null && !container.isDisposed())
			container.dispose();

		container = new Composite(scrolledComposite, SWT.BORDER);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 10;
		container.setLayout(layout);
		
		scrolledComposite.setContent(container);

	}


	// ==================== 6. Action Methods ====================

	/**
	 * 
	 */
	public void refreshMemos(boolean force)
	{
		activeMemosList_current.clear();
		
		new Job("Loading memoboard...") 
		{
			protected IStatus run(IProgressMonitor monitor)
			{
//				Display.getDefault().syncExec(new Runnable() {
//
//					public void run()
//					{
//						createContainer();
//
//						showLoadingMessage();
//
//						container.layout();
//					};
//				});

				final List<Memo> activeMemosList = Session.getInstance().loadActiveMemos();

				Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run()
					{
						setContentDescription("Last Refresh: " + MemoComponent.df.format(new Date()));
						
					}
				});
				
				//If the current list is empty, force anyway to refresh ui:
				boolean same = !activeMemosList_current.isEmpty() && activeMemosList.size() == activeMemosList_current.size();
				if (same)
				{
					
					for (Memo memo : activeMemosList)
						if (!activeMemosList_current.contains(memo))
						{
							same = false;
							break;
						}
					
				}

				if (!same)
				{
					activeMemosList_current.clear();
					activeMemosList_current.addAll(activeMemosList);

					Display.getDefault().syncExec(new Runnable() {

						public void run()
						{

							Session.getInstance().showBalloon(activeMemosList.size() + " memos loaded", "", SWT.ICON_INFORMATION);

							if (activeMemosList.isEmpty())
							{
								showNoActiveMemos();
							}
							else
							{
								createContainer();

								for (Memo memo : activeMemosList)
									new MemoComponent(container, memo);
							}

							container.redraw();
							container.layout();

							scrolledComposite.setMinSize(container.computeSize(700, SWT.DEFAULT));
						};
					});

				}

				//					refreshJob.schedule(REFRESH_MEMOS_DELAY);
				if (getState() != Job.WAITING)
					schedule(REFRESH_MEMOS_DELAY);
				
				return Status.OK_STATUS;
			};

		}.schedule();

	}

	private void showLoadingMessage()
	{
		
	}

	private void showNoActiveMemos()
	{
		Label noActiveMemosLabel = new Label(container, SWT.NONE);
		GridData gridData = new GridData(SWT.CENTER, SWT.TOP, true, true);
		noActiveMemosLabel.setLayoutData(gridData);
		noActiveMemosLabel.setText("No Active Memos");
	}

	@Override
	public void setFocus()
	{

	}
	
	@Override
	public void setPartName(String partName)
	{
		super.setPartName(partName);
	}

}
