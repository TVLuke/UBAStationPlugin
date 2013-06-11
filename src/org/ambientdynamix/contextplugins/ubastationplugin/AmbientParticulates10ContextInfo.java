package org.ambientdynamix.contextplugins.ubastationplugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.ambientdynamix.contextplugins.context.info.environment.IParticulates10ContextInfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AmbientParticulates10ContextInfo implements IParticulates10ContextInfo {

	 private final String TAG = "UBAStation";
	 double[] pm10values= new double[1];
	 
		public static Parcelable.Creator<AmbientParticulates10ContextInfo> CREATOR = new Parcelable.Creator<AmbientParticulates10ContextInfo>() 
				{
				public AmbientParticulates10ContextInfo createFromParcel(Parcel in) 
				{
					return new AmbientParticulates10ContextInfo(in);
				}

				public AmbientParticulates10ContextInfo[] newArray(int size) 
				{
					return new AmbientParticulates10ContextInfo[size];
				}
			};
			
	public AmbientParticulates10ContextInfo(ArrayList<UBAStation> stations)
	{
		Log.i(TAG, "PM10 CONTEXT");
		pm10values = new double[stations.size()];
		for(int i=0; i<stations.size(); i++)
		{
			String code = "CO";
			pm10values[i]=stations.get(i).sense(code);
		}
	}
	
	public AmbientParticulates10ContextInfo(Parcel in) 
	{
		pm10values = in.createDoubleArray();
	}
	
	@Override
	public String getContextType() 
	{
		return "org.ambientdynamix.contextplugins.context.info.environment.particulates10";
	}

	@Override
	public String getImplementingClassname() 
	{
		return this.getClass().getName();
	}

	@Override
	public String getStringRepresentation(String format) 
	{
		String result="";
		for(int i=0; i<pm10values.length; i++)
		{
			result=result+pm10values[i]+" ";
		}
		if (format.equalsIgnoreCase("text/plain"))
			return result;
		else
			return null;
	}

	@Override
	public Set<String> getStringRepresentationFormats() 
	{
		Set<String> formats = new HashSet<String>();
		formats.add("text/plain");
		return formats;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) 
	{
		out.writeDoubleArray(getPM10Value());
		
	}

	@Override
	public double[] getPM10Value() 
	{
		return pm10values;
	}

	@Override
	public String getUnit() 
	{
		return "mycro g/m^3";
	}

}
