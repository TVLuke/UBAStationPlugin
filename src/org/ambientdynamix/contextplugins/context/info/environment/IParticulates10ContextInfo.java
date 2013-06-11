package org.ambientdynamix.contextplugins.context.info.environment;

import org.ambientdynamix.api.application.IContextInfo;

/**
 * Interface describing the Ambient measurement of particulates smaler then 10 mycro g/m^3 (commonly known as fine dust)
 * @author lukas
 *
 */
public interface IParticulates10ContextInfo  extends IContextInfo
{

	public abstract double[] getPM10Value();
	
	public abstract String getUnit();
}
