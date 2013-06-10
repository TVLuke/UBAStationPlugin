/*
 * This Plugin is licensed under GNU GPL v3 
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 *Author: Luaks Ruge
 */

package org.ambientdynamix.contextplugins.ubastationplugin;

import org.ambientdynamix.api.contextplugin.*;

public class PluginFactory extends ContextPluginRuntimeFactory 
{
    public PluginFactory() 
    {
    	super(AirPolutantsPluginRuntime.class, null, null);
    }
}