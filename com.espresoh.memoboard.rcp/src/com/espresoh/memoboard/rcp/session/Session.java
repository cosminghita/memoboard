package com.espresoh.memoboard.rcp.session;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.PlatformUI;

import com.espresoh.memoboard.rcp.Activator;
import com.espresoh.memoboard.rcp.google.Signin;
import com.espresoh.memoboard.rcp.preferences.IPreferenceKeys;
import com.espresoh.memoboard.rcp.views.MemoboardView;
import com.espresoh.memoboard.server.model.Memo;
import com.espresoh.memoboard.server.model.User;
import com.espresoh.memoboard.server.po.UserPO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;

public class Session
{

	// ==================== 1. Static Fields ========================

	private static final String MEMOBOARD_ICON = "/icons/memoboard.png";
	private static Session instance;
	
	
	// ====================== 2. Instance Fields =============================

	private ToolTip trayItemTooltip;
	protected TrayItem trayItem;

	private User user;


	// ==================== 3. Static Methods ====================

	public static Session getInstance()
	{
		if (instance == null)
		{
			instance = new Session();

			instance.createTrayItem();

		}
		
		if (instance.user == null)
		{
			//restore prefs so we do not need to login each time (for now it is enough that we logged once with google)
			String email = Activator.getDefault().getPreferenceStore().getString(IPreferenceKeys.USER_GOOGLE_EMAIL);
			if (email != null && !email.isEmpty())
			{
				UserPO userPO = new UserPO();
				userPO.setEmail(email);
				userPO.setName(Activator.getDefault().getPreferenceStore().getString(IPreferenceKeys.USER_GOGLE_NAME));
				userPO.setAvatar(Activator.getDefault().getPreferenceStore().getString(IPreferenceKeys.USER_GOGLE_AVATAR));

				//Try to login with user credentials restored from prefs:
				instance.login(userPO);
			}
			
			//If user could not login with credentials restored from prefs, than sign in with Google:
			if (instance.user == null)
				instance.initializeSession();
		}		
		
		return instance;
	}
	
	private void initializeSession()
	{
		try
		{
			Signin.login();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public void login(UserPO userPO)
	{
		if (user == null)
		{
			try
			{
				String jsonUser = RESTSession.getWebResource().path("/login").type(MediaType.APPLICATION_JSON).post(String.class, new Gson().toJson(userPO));

				System.out.println("Response status: " + jsonUser);

				user = new Gson().fromJson(jsonUser, User.class);

				//save to prefs so we do not need to login each time (for now it is enough that we logged once with google)
				saveUserToPrefs();

				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run()
					{
						MemoboardView.INSTANCE.setPartName("Memos for " + user.toText());

//						showBalloon("Succesufully logged in", user.toText(), SWT.ICON_INFORMATION);

					}
				});


					

				
			} catch (Exception e)
			{
				showBalloon("Error while loginng", "", SWT.ICON_ERROR);
				
				//force re-login:
				user = null;
				e.printStackTrace();
			}

		}
	}

	private void saveUserToPrefs()
	{
		Activator.getDefault().getPreferenceStore().putValue(IPreferenceKeys.USER_GOOGLE_EMAIL, user.getEmail());
		Activator.getDefault().getPreferenceStore().putValue(IPreferenceKeys.USER_GOGLE_NAME, user.getName());
		Activator.getDefault().getPreferenceStore().putValue(IPreferenceKeys.USER_GOGLE_AVATAR, user.getAvatar());
	}
	
	
	public List<Memo> loadActiveMemos()
	{
		if (user == null)
			return new ArrayList<>();
		
        Type listType = new TypeToken<List<Memo>>(){}.getType();

		String jsonMemoList = RESTSession.getWebResource().path("/memoboard/activememos/" + getUserId() + "/").accept(MediaType.TEXT_PLAIN).get(String.class);

		final List<Memo> activeMemoList = new Gson().fromJson(jsonMemoList, listType);
		
//		Display.getDefault().asyncExec(new Runnable() {
//			
//			@Override
//			public void run()
//			{
//				showBalloon(activeMemoList.size() + " memos loaded", "", SWT.ICON_INFORMATION);
//				
//			}
//		});

		
		return activeMemoList;
//		return new ArrayList<>();
	}
	
	public boolean save(Memo memo)
	{
		ClientResponse response = RESTSession.getWebResource().path("/memoboard/savememo/" + getUserId() + "/").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, new Gson().toJson(memo));
		
		System.out.println(response.getStatus());

		boolean saved = response.getStatus() == 200;
		
		if (saved)
			showBalloon("Sucesfully saved memo", memo.getTitle(), SWT.ICON_INFORMATION);
		else
			showBalloon("Memo save failed", memo.getTitle(), SWT.ICON_WARNING);

		return saved;
	}

	public boolean dismiss(Memo memo)
	{
		ClientResponse response = RESTSession.getWebResource().path("/memoboard/dismiss/" + getUserId() + "/" + memo.getId() + "/").put(ClientResponse.class);
		
		boolean dismissed = response.getStatus() == 200;
		
		if (dismissed)
			showBalloon("Dismissed", memo.getTitle(), SWT.ICON_INFORMATION);
		else
			showBalloon("Dismiss failed", memo.getTitle(), SWT.ICON_WARNING);

		return dismissed;
	}
	
	public List<User> getUsers()
	{
		if (user == null)
			return new ArrayList<>();
		
        Type listType = new TypeToken<List<User>>(){}.getType();

		String jsonUserList = RESTSession.getWebResource().path("/login/users/" + getUserId() + "/").accept(MediaType.TEXT_PLAIN).get(String.class);

		final List<User> usersList = new Gson().fromJson(jsonUserList, listType);
		
		return usersList;
	}

	
	/**
	 * 
	 */
	private void createTrayItem()
	{

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run()
			{

				if (trayItemTooltip != null && !trayItemTooltip.isDisposed())
					trayItemTooltip.dispose();
				
				trayItemTooltip = new ToolTip(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
				trayItemTooltip.setMessage("MemoBoard is running!");
				trayItemTooltip.setText("MemoBoard");
				final Tray tray = PlatformUI.getWorkbench().getDisplay().getSystemTray();
				if (tray == null)
					return;
				trayItem = new TrayItem(tray, SWT.NONE);

				if (trayItem != null)
				{
					trayItem.setImage(Activator.getImageDescriptor(MEMOBOARD_ICON).createImage());
					trayItem.setToolTip(trayItemTooltip);
				}
				
				final Shell workbenchWindowShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				trayItem.addSelectionListener(new SelectionAdapter() 
				{
					public void widgetDefaultSelected(SelectionEvent e)
					{
						workbenchWindowShell.setVisible(true);
						workbenchWindowShell.setActive();
						workbenchWindowShell.setFocus();
						workbenchWindowShell.setMinimized(false);
					}
				});

				
				// Create a Menu
				final Menu menu = new Menu(workbenchWindowShell, SWT.POP_UP);
				
				trayItem.addListener(SWT.MenuDetect, new Listener() {
					public void handleEvent(Event event)
					{
						menu.setVisible(true);
					}
				});
				
				// Create the open menu item.
				final MenuItem open = new MenuItem(menu, SWT.PUSH);
				open.setText("Open");
				// make the workbench visible in the event handler for exit menu item.
				open.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event)
					{
						workbenchWindowShell.setVisible(true);
						workbenchWindowShell.setActive();
						workbenchWindowShell.setFocus();
						workbenchWindowShell.setMinimized(false);
					}
				});


				// Create the exit menu item.
				final MenuItem exit = new MenuItem(menu, SWT.PUSH);
				exit.setText("Exit");
				
				// Do a workbench close in the event handler for exit menu item.
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event)
					{
//						System.exit(0);;
						 PlatformUI.getWorkbench().getActiveWorkbenchWindow().close();
					}
				});


			}
		});
	}


	public void showBalloon(final String title, final String msg, final int style)
	{

		trayItemTooltip = new ToolTip(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.BALLOON | style);
		trayItemTooltip.setText(title);
		if (msg != null)
			trayItemTooltip.setMessage(msg);
		trayItem.setToolTip(trayItemTooltip);
		trayItemTooltip.setVisible(true);
	}
	
	
	// ==================== 7. Getters & Setters ====================

	public User getUser()
	{
		return user;
	}

	public int getUserId()
	{
//		return 2;
		return user.getId();
	}

}
