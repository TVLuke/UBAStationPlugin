package org.ambientdynamix.contextplugins.context.info.environment;

import org.ambientdynamix.api.application.IContextInfo;

public interface IPressureContextInfo extends IContextInfo
{
	public abstract double[] getPaValue();
}
