package org.ambientdynamix.contextplugins.context.info.environment;

import org.ambientdynamix.api.application.IContextInfo;

public interface ITemperatureContextInfo extends IContextInfo
{
	public abstract double[] getCelciusValue();
}
