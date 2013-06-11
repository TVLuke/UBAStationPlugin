package org.ambientdynamix.contextplugins.ubastationplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.ambientdynamix.contextplugins.context.info.environment.ICarbonMonoxideContextInfo;


import android.location.Location;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AmbientCarbonMonoxideContextInfo implements ICarbonMonoxideContextInfo
{
	 private final String TAG = "UBAStation";
	 double[] covalues= new double[1];
	
	public static Parcelable.Creator<AmbientCarbonMonoxideContextInfo> CREATOR = new Parcelable.Creator<AmbientCarbonMonoxideContextInfo>() 
		{
		public AmbientCarbonMonoxideContextInfo createFromParcel(Parcel in) 
		{
			return new AmbientCarbonMonoxideContextInfo(in);
		}

		public AmbientCarbonMonoxideContextInfo[] newArray(int size) 
		{
			return new AmbientCarbonMonoxideContextInfo[size];
		}
	};
	
	@Override
	public String toString() 
	{
		return this.getClass().getSimpleName();
	};
	
	public AmbientCarbonMonoxideContextInfo(Parcel in) 
	{
		covalues = in.createDoubleArray();
	}


	/* (non-Javadoc)
	 * @see org.ambientdynamix.contextplugins.ambientsound.IAmbientSoundContextInfo#getContextType()
	 */
	@Override
	public String getContextType() 
	{
		return "org.ambientdynamix.contextplugins.context.info.environment.carbonmonoxide";
	}


	/* (non-Javadoc)
	 * @see org.ambientdynamix.contextplugins.ambientsound.IAmbientSoundContextInfo#getStringRepresentation(java.lang.String)
	 */
	@Override
	public String getStringRepresentation(String format) 
	{
		String result="";
		for(int i=0; i<covalues.length; i++)
		{
			result=result+covalues[i]+" ";
		}
		if (format.equalsIgnoreCase("text/plain"))
			return result;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.ambientdynamix.contextplugins.ambientsound.IAmbientSoundContextInfo#getImplementingClassname()
	 */
	@Override
	public String getImplementingClassname() 
	{
		return this.getClass().getName();
	}

	/* (non-Javadoc)
	 * @see org.ambientdynamix.contextplugins.ambientsound.IAmbientSoundContextInfo#getStringRepresentationFormats()
	 */
	@Override
	public Set<String> getStringRepresentationFormats() 
	{
		Set<String> formats = new HashSet<String>();
		formats.add("text/plain");
		return formats;
	};
	
	public AmbientCarbonMonoxideContextInfo(ArrayList<UBAStation> stations)
	{
		Log.i(TAG, "CARBON CONTEXT");
		covalues = new double[stations.size()];
		for(int i=0; i<stations.size(); i++)
		{
			String code = "CO";
			covalues[i]=stations.get(i).sense(code);
		}
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
		out.writeDoubleArray(getCOValue());
	}


	@Override
	public double[] getCOValue()
	{
		return covalues;
	}
}
