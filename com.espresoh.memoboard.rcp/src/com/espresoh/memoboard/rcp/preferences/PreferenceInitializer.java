package com.espresoh.memoboard.rcp.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.espresoh.memoboard.rcp.Activator;

/**
 * 
 * @author cghita
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	// ==================== 4. Constructors ====================

	public PreferenceInitializer()
	{
		super();
	}


	@Override
	public void initializeDefaultPreferences()
	{
		final IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		defaults.put(IPreferenceKeys.SERVER_URL, "http://192.168.1.50:8080/com.espresoh.memoboard.server/rest");
	}
}
