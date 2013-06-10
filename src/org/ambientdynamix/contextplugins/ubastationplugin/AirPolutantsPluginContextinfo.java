/*
 * This Plugin is licensed under GNU GPL v3 
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 *Author: Luaks Ruge
 */
package org.ambientdynamix.contextplugins.ubastationplugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import org.ambientdynamix.contextplugins.context.info.environment.ICarbonMonoxideContextInfo;

public class AirPolutantsPluginContextinfo implements ICarbonMonoxideContextInfo
{
    public static Parcelable.Creator<AirPolutantsPluginContextinfo> CREATOR = new Parcelable.Creator<AirPolutantsPluginContextinfo>() 
    {
    	public AirPolutantsPluginContextinfo createFromParcel(Parcel in) 
    	{
    		return new AirPolutantsPluginContextinfo(in);
    	}

    	public AirPolutantsPluginContextinfo[] newArray(int size) 
    	{
    		return new AirPolutantsPluginContextinfo[size];
    	}
    };
    
    // Sample context data
    private MeasurementList contextData;

    public MeasurementList getSampleData() 
    {
    	return contextData;
    }

    @Override
    public String toString() 
    {
    	return this.getClass().getSimpleName();
    };

    @Override
    public String getContextType() 
    {
    	return "org.ambientdynamix.contextplugins.airpolutantsplugin";
    }

    @Override
    public String getStringRepresentation(String format) 
    {
    	
    	String result="";
    	List m = contextData.getMeasurements();
    	for(int i=0; i<m.size(); i++)
    	{
    		if(i>0)
    		{
    			result=result+"; ";
    		}
    		Measurement m1 = (Measurement) m.get(i);
    		result=result+m1.getName()+" "+m1.getValue()+" "+m1.getUnit()+" ("+m1.getDistance()+" meters)";
    	}
    	return result;
    }

    @Override
    public String getImplementingClassname() 
    {
    	return this.getClass().getName();
    }

    @Override
    public Set<String> getStringRepresentationFormats() 
    {
    	Set<String> formats = new HashSet<String>();
    	formats.add("text/plain");
    	return formats;
    };

    public AirPolutantsPluginContextinfo(MeasurementList m) 
    {
    	this.contextData = m;
    }

    private AirPolutantsPluginContextinfo(final Parcel in) 
    {
    	this.contextData = in.readParcelable(getClass().getClassLoader());
    }

    public IBinder asBinder() 
    {
    	return null;
    }

    public int describeContents() 
    {
    	return 0;
    }

    public void writeToParcel(Parcel out, int flags) 
    {
    	out.writeParcelable(this.contextData, flags);
    }

	@Override
	public double[] getCOValue() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}