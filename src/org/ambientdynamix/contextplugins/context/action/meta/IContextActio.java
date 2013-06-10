package org.ambientdynamix.contextplugins.context.action.meta;

import org.ambientdynamix.api.application.IContextInfo;

/**
 * For now this is a dummy interface that allows for actions to implement their own interface even though this interface doesn't do anything.
 * 
 * The intent is to later have IContext with some commom methods and split that up into IContextInfo and IContextActio being two interfaces that
 * entail methoids specific to Informing on Context or Acting in Context.
 * 
 * @author lukas
 *
 */
public interface IContextActio extends IContextInfo
{
	
}
