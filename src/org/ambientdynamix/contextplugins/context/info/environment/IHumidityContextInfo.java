package org.ambientdynamix.contextplugins.context.info.environment;

import org.ambientdynamix.api.application.IContextInfo;

public interface IHumidityContextInfo extends IContextInfo
{
	public abstract double[] getHumidityValue();
}
