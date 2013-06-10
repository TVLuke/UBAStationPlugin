package org.ambientdynamix.contextplugins.context.info.device;

/**
 * DeviceContextInfo is supposed to provide basic information on connected devices. It returns an Array of objects implementing the IDevice Interface or something...
 * 
 *  ... not sure how this is going to work but its kinda relevant to have a context type that provides meta-info on the conected devices, I kinda think this 
 *  should be included on every kind of context, providing data on where the info comes from. This shouldn't even be the root, there should be a sourceInfo, which 
 *  can be the Internet or just reasoned, logical data or whatever or, as another subclass, a device. All Sensor data should have to have a source! 
 * @author lukas
 *
 */
public interface IDeviceContextInfo {

}
