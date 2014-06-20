package com.espresoh.memoboard.rcp.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.espresoh.memoboard.rcp.Activator;
import com.espresoh.memoboard.rcp.dialogs.AddEditMemoDialog;
import com.espresoh.memoboard.rcp.helpers.UIHelper;
import com.espresoh.memoboard.rcp.session.Session;
import com.espresoh.memoboard.rcp.views.MemoboardView;
import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.User;

/**
 * Display one memo message.
 * 
 * @author cghita
 *
 */
public class MemoComponent extends Composite
{

	private static final int MEMO_DUE_DATE_CRITICAL_HOURS = 8;

	private static final int MEMO_DUE_DATE_WARNING_HOURS = 24;

	final public static DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
	final public static PeriodFormatter formatter = new PeriodFormatterBuilder()
    .appendDays()
    .appendSuffix("d ")
    .appendHours()
    .appendSuffix("h ")
    .appendMinutes()
    .appendSuffix("m ")
    .appendSeconds()
    .appendSuffix("s")
    .toFormatter();


	// ==================== 1. Static Fields ========================

	public static final String NEW_LINE = "\n";
	
	public static Image dismissIcon = Activator.getImageDescriptor("/icons/dismiss.png").createImage();
	public static Image editIcon = Activator.getImageDescriptor("/icons/edit.jpg").createImage();
	
	
	// ====================== 2. Instance Fields =============================
	
	private Memo memo;


	// ==================== 4. Constructors ====================

	public MemoComponent(Composite parent, Memo memo)
	{
		super(parent, SWT.BORDER);
		
		this.memo = memo;
		
		GridLayout layout = UIHelper.getGridLayout_noSpancings(new GridLayout(2, false));
		setLayout(layout);
		
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layoutData.minimumHeight = 150;
		layoutData.heightHint = 150;
		setLayoutData(layoutData);
		
		createContent(this);
		
		createOwnerLabel(this);
		
		createDismissButton(this);
		
		colorByDueDate();
	}
	
	private void createOwnerLabel(Composite parent)
	{
		StringBuilder s = new StringBuilder();
		s.append("Sender: ")
		.append(memo.getOwner().toText());
		
		if (memo.getDueDate() != null)
		{
			s.append(" [Due Date: ")
			.append(df.format(memo.getDueDate()))
			.append(" | ETA: ");

			Interval interval = null;
			Date now = new Date();
			
			if (memo.getDueDate().before(now))
			{
				//Over deadline:
				interval = new Interval(memo.getDueDate().getTime(), now.getTime());
				s.append("-");
			}
			else
				interval = new Interval(now.getTime(), memo.getDueDate().getTime());
			
			String durationFormatted = formatter.print(interval.toDuration().toPeriod());
			s.append(durationFormatted).append("]");
		}
		
		new Label(parent, SWT.NONE).setText(s.toString());
	}

	private void createDismissButton(Composite parent)
	{
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		
		//Only owner can edit:
		if (memo.getOwner().equals(Session.getInstance().getUser()))
		{
			ToolItem toolItemEdit = new ToolItem(toolBar, SWT.FLAT);
			toolItemEdit.setImage(editIcon);
			toolItemEdit.setText("Edit");
			toolItemEdit.addSelectionListener(new SelectionAdapter() 
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					new AddEditMemoDialog(getShell(), memo).open();

				}
			});
		}
		
		ToolItem toolItemDismiss = new ToolItem(toolBar, SWT.FLAT);
		toolItemDismiss.setImage(dismissIcon);
		toolItemDismiss.setText(getDismissText());
		
		toolItemDismiss.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String title = getDismissText();
				if (!MessageDialog.openQuestion(getShell(), title, "Are you sure you want to " + title + "?"))
					return;
				
				boolean dismissed = Session.getInstance().dismiss(memo);
				if (dismissed)
				{
					MemoboardView.INSTANCE.refreshMemos(false);
				}
			}
		});
	}

	private String getDismissText()
	{
		return memo.isRequiresConfirmation() ? "Confirm" : "Dismiss";
	}

	/**
	 * 
	 * @param parent
	 */
	private void createContent(Composite parent)
	{
		
		List<StyleRange> styleRanges = new ArrayList<>();
		
		String memoDisplayText = memo.getTitle() == null ? "No Title" : memo.getTitle();
		
		
		final StyleRange styleTitle = new StyleRange();
		styleTitle.fontStyle = SWT.BOLD;
		styleTitle.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		styleTitle.start = 0;
		styleTitle.length = memoDisplayText.length();

		styleRanges.add(styleTitle);

		
		
		
		String contentText = NEW_LINE + (memo.getContent() == null ? "No Content" : memo.getContent());

		final StyleRange styleContent = new StyleRange();
		styleContent.fontStyle = SWT.BOLD;
		styleContent.foreground = Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		styleContent.start = memoDisplayText.length();
		styleContent.length = contentText.length();

		memoDisplayText += contentText;
		styleRanges.add(styleContent);
		

		if (!memo.getUnconfirmedUsers().isEmpty())
		{
			String unconfirmedUsersText = toTextUsers("Unconfirmed Users: ", memo.getUnconfirmedUsers());

			final StyleRange styleUnconfirmedUsers = new StyleRange();
			styleUnconfirmedUsers.fontStyle = SWT.ITALIC;
			styleUnconfirmedUsers.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
			styleUnconfirmedUsers.start = memoDisplayText.length();
			styleUnconfirmedUsers.length = unconfirmedUsersText.length();

			memoDisplayText += unconfirmedUsersText;
			
			styleRanges.add(styleUnconfirmedUsers);


		}
		
		if (!memo.getConfirmedUsers().isEmpty())
		{
			String confirmedUsersText = toTextUsers("Confirmed Users: ", memo.getConfirmedUsers());

			final StyleRange styleConfirmedUsers = new StyleRange();
			styleConfirmedUsers.fontStyle = SWT.ITALIC;
			styleConfirmedUsers.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
			styleConfirmedUsers.start = memoDisplayText.length();
			styleConfirmedUsers.length = confirmedUsersText.length();

			memoDisplayText += confirmedUsersText;
			
			styleRanges.add(styleConfirmedUsers);

		}
		
		Interval interval = new Interval(memo.getStartDate().getTime(), new Date().getTime());
		String durationFormatted = formatter.print(interval.toDuration().toPeriod());
	
		StringBuilder createdOnBuilder = new StringBuilder(); 
		createdOnBuilder.append(NEW_LINE)
		.append("Created ")
		.append(durationFormatted)
		.append(" ago [")
		.append(df.format(memo.getStartDate()))
		.append("]");
		
		
		String createdOnText = createdOnBuilder.toString() ;

		final StyleRange styleCreatedOn = new StyleRange();
		styleCreatedOn.start = memoDisplayText.length();
		styleCreatedOn.length = createdOnText.length();

		memoDisplayText += createdOnText;
		styleRanges.add(styleCreatedOn);


		createStyledText(parent, memoDisplayText, styleRanges.toArray(new StyleRange[0]));

		
	}

	private String toTextUsers(String title, List<User> users)
	{
		StringBuilder confirmedUsersBuilder = new StringBuilder();
		confirmedUsersBuilder.append(NEW_LINE)
		.append(title);

		for (User user : users)
		{
			if (confirmedUsersBuilder.length() > 0)
				confirmedUsersBuilder.append(NEW_LINE);
			
			confirmedUsersBuilder.append(user.getName()).append("  -  ").append(user.getEmail());
		}
		
		String affectedUsersText = confirmedUsersBuilder.toString();
		return affectedUsersText;
	}
	
	
	private static StyledText createStyledText(final Composite parent, final String text, final StyleRange... ranges)
	{
		final StyledText messageTextWidget = new StyledText(parent, SWT.WRAP | SWT.READ_ONLY);
		messageTextWidget.setCaret(null);
		messageTextWidget.setBackground(parent.getBackground());
		messageTextWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		messageTextWidget.setText(text);

		if (ranges != null)
			messageTextWidget.setStyleRanges(ranges);

		return messageTextWidget;
	}
	
	private void colorByDueDate()
	{
		if (memo.getDueDate() == null)
			return;
		
		LocalDateTime dueDate = new LocalDateTime(memo.getDueDate());
		dueDate.minusHours(MEMO_DUE_DATE_CRITICAL_HOURS);
		
		if (new Date().before(dueDate.toDate()))
		{
			setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
			return;
		}

		dueDate = new LocalDateTime(memo.getDueDate());
		dueDate.minusHours(MEMO_DUE_DATE_WARNING_HOURS);

		if (new Date().before(dueDate.toDate()))
		{
			setBackground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
			return;
		}
}

}
