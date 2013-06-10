package org.ambientdynamix.contextplugins.context.info.environment;

import java.util.Set;

import org.ambientdynamix.api.application.IContextInfo;

public interface ILightContextInfo extends IContextInfo
{
	public abstract double[] getLuxValue();
	
	public abstract double[] getRedLevelValue();
	
	public abstract double[] getGreenLevelValue();
	
	public abstract double[] getBlueLevelValue();
}