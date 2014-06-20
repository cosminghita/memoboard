package com.espresoh.memoboard.rcp.components;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.espresoh.memoboard.server.model.User;

/**
 * 
 * @author cghita
 *
 */
public class UsersTableViewer
{

	
	// ====================== 2. Instance Fields =============================

	private enum UserColumns 
	{
		NAME("Name", SWT.LEFT, 250),
		EMAIL("Email", SWT.LEFT, 200);

		private String column;
		private int style;
		private int width;

		private UserColumns(final String column, final int style, final int width)
		{
			this.column = column;
			this.style = style;
			this.width = width;
		}

		public String getColumn()
		{
			return column;
		}

		public int getStyle()
		{
			return style;
		}

		public int getWidth()
		{
			return width;
		}

		public static UserColumns getColumnByString(final String column)
		{
			for (final UserColumns pr : values())
			{
				if (pr.getColumn().equals(column))
					return pr;
			}
			// We should never get here - if yes, return first value:
			return values()[0];
		}

	}

	private TableViewer tableViewer;

	private List<User> input;

	// ==================== 4. Constructors ====================

	public UsersTableViewer(final Composite parent)
	{
		createTableViewer(parent);
	}

	// ==================== 5. Creators ====================

	private void createTableViewer(final Composite parent)
	{
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		for (final UserColumns col : UserColumns.values())
		{
			final TableColumn column = new TableColumn(tableViewer.getTable(), col.getStyle());
			column.setText(col.getColumn());
			column.setWidth(col.getWidth());


			column.addSelectionListener(new SelectionAdapter()
			{
				@Override public void widgetSelected(final SelectionEvent e)
				{

					// determine new sort column and direction
					final TableColumn sortColumn = tableViewer.getTable().getSortColumn();
					final TableColumn currentColumn = (TableColumn) e.widget;

					int dir = tableViewer.getTable().getSortDirection();

					if (sortColumn == currentColumn)
					{
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					} else
					{
						tableViewer.getTable().setSortColumn(currentColumn);
						dir = SWT.UP;
					}

					// sort the data based on column and direction
					tableViewer.getTable().setSortDirection(dir);
					tableViewer.setComparator(new UsersViewSorter(UserColumns.getColumnByString(currentColumn.getText()), dir));

				}
			});
		}

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new UsersLabelProvider());

		tableViewer.getTable().setTouchEnabled(true);

	}

	// ==================== 7. Getters & Setters ====================

	public TableViewer getTableViewer()
	{
		return tableViewer;
	}

	public void refresh()
	{
		tableViewer.refresh();
	}
	
	public void setInput(final List<User> input)
	{
		this.input = input;
		tableViewer.setInput(input.toArray());
	}

	public List<User> getInput()
	{
		return input;
	}

	public List<User> getSelected()
	{
		return ((StructuredSelection) tableViewer.getSelection()).toList();
	}

	public User getSelectedItem()
	{
		return (User) ((StructuredSelection) tableViewer.getSelection()).getFirstElement();
	}

	// =======================================================
	// 19. Inline Classes
	// =======================================================

	private class UsersViewSorter extends ViewerComparator
	{

		// default sort column:
		private UserColumns column = UserColumns.NAME;

		private int dir = SWT.DOWN;

		private UsersLabelProvider labelProvider;

		/**
		 * @param column
		 * @param dir
		 */
		public UsersViewSorter(final UserColumns column, final int dir)
		{
			super();
			this.column = column;
			this.dir = dir;
			labelProvider = new UsersLabelProvider();
		}

		@Override public int compare(final Viewer viewer, final Object e1, final Object e2)
		{

			final int result = labelProvider.getColumnText(e1, column.ordinal()).compareToIgnoreCase(labelProvider.getColumnText(e2, column.ordinal()));

			return dir == SWT.DOWN ? result * -1 : result;
		}

	}

	private class UsersLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		@Override public Image getColumnImage(final Object element, final int columnIndex)
		{
			return null;
		}

		@Override public String getColumnText(final Object element, final int columnIndex)
		{

			final User concediu = (User) element;

			switch (UserColumns.values()[columnIndex])
			{
			case EMAIL:
					return concediu.getEmail();
			case NAME:
					return concediu.getName();
			}
			return "";
		}
	}


}
