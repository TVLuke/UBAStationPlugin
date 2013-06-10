/*
 * This Plugin is licensed under GNU GPL v3 
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 *Author: Luaks Ruge
 */
package org.ambientdynamix.contextplugins.ubastationplugin;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Measurement implements Parcelable 
{
	String name="";
	double value=-999.9;
	String unit="";
	Station station;
	long date = 0;
	double distance=0.0;
	
	/**
     * Static Creator factory for Parcelable.
     */
    public static Parcelable.Creator<Measurement> CREATOR = new Parcelable.Creator<Measurement>() 
    {
		public Measurement createFromParcel(Parcel in) 
		{
		    return new Measurement(in);
		}
	
		public Measurement[] newArray(int size) 
		{
		    return new Measurement[size];
		}
    };

    public Measurement(String n, double v, String u, Station s, Date d, double di)
    {
    	name=n;
    	value=v;
    	unit=u;
    	station=s;
    	date=d.getTime();
    	distance=di;
    }

    public Measurement()
    {
    	
    }
    
    public String getName()
    {
    	return name;
    }
    
    public String getUnit()
    {
    	return unit;
    }
    
    public Station getStation()
    {
    	return station;
    }
    
    public double getValue()
    {
    	return value;
    }
    
    public Date getDate()
    {
    	Date d = new Date(date);
    	return d;
    }
    
    public double getDistance()
    {
    	return distance;
    }
    
    public void update(String n, double v, String u, Station s, Date d, double di)
    {
    	name=n;
    	value=v;
    	unit=u;
    	station=s;
    	date =d.getTime();
    	distance =di;
    }
    
	private Measurement(Parcel in) 
	{
		name = in.readString();
		value= in.readDouble();
		unit=in.readString();
		station=in.readParcelable(getClass().getClassLoader());
		date=in.readLong();
		distance=in.readDouble();
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) 
	{
		parcel.writeString(name);
		parcel.writeDouble(value);
		parcel.writeString(unit);
		parcel.writeParcelable(station, flags);
		parcel.writeLong(date);
		parcel.writeDouble(distance);
	}
}
