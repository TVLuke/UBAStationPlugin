package org.ambientdynamix.contextplugins.ubastationplugin;

import org.ambientdynamix.api.contextplugin.ContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.IContextPluginConfigurationViewFactory;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class UBAStationPluginConfigurationActivity extends Activity implements IContextPluginConfigurationViewFactory
{

	@Override
	public void destroyView() throws Exception 
	{
		
		
	}

	@Override
	public View initializeView(Context arg0, ContextPluginRuntime arg1, int arg2) throws Exception 
	{

		return null;
	}

}
