package org.ambientdynamix.contextplugins.info.meta;


public interface IDevice extends IContextSource
{
	public String diviceName();
	
	public String urlOfDeviceSymbol();
	
	public float energyLevel();
	
	public int hardwareVersionNumber();
	
	public int firmwareVersionNumber();
	
	public int firmwareRevisionNumber();
	
	public boolean isBatteryPowered();
	
	public boolean isRecharable();
	
	public boolean isCharging();
	
	public boolean isWireless();
	
	public boolean isConnected();
	
	public String ConnectionType();
	
	public String getMACAddress();
	
	public int remainingStorage();
}
