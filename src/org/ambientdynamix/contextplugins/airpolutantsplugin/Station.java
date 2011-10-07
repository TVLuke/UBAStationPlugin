package org.ambientdynamix.contextplugins.airpolutantsplugin;

import android.os.Parcel;
import android.os.Parcelable;

import android.location.Location;

public class Station implements Parcelable 
{
	private String stationid = "";
	private String stationname="";
	private double stationlatitude=0.0;
	private double stationlongitude=0.0;
	private double stationaltitude = 0.0;
	private String streetadress="";
	private String zipcode="";
	private String city="";
	private String areatype ="";
	private String stationtype="";
	private String remark="";
	private String firstmeasurement="";
	private String lastemeasurement="";
	private int active = 0;
	
    /**
     * Static Creator factory for Parcelable.
     */
    public static Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() 
    {
		public Station createFromParcel(Parcel in) 
		{
		    return new Station(in);
		}
	
		public Station[] newArray(int size) 
		{
		    return new Station[size];
		}
    };

    public Station(String id)
    {
    	stationid=id;
    }

    public void setStationName(String a)
    {
    	stationname=a;
    }
    
    public void setstreetadress(String a)
    {
    	streetadress=a;
    }
    public void setzipcode(String a)
    {
    	zipcode=a;
    }
    public void setcity(String a)
    {
    	city=a;
    }
    public void setareatype(String a)
    {
    	areatype=a;
    }
    public void setstationtype(String a)
    {
    	stationtype=a;
    }
    public void setremark(String a)
    {
    	remark=a;
    }
    public void setfirstmeasurement(String a)
    {
    	firstmeasurement=a;
    }    
    public void setlastemeasurement(String a)
    {
    	lastemeasurement=a;
    }   
    public void setactive(int a)
    {
    	active=a;
    }
    public void setLocation(Location location)
    {
    	stationlatitude=location.getLatitude();
		stationlongitude=location.getLongitude();
		stationaltitude=location.getAltitude();
    }
    
    
    
	@Override
	public int describeContents() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) 
	{
		parcel.writeString(stationid);
		parcel.writeString(stationname);
		parcel.writeDouble(stationlatitude);
		parcel.writeDouble(stationlongitude);
		parcel.writeDouble(stationaltitude);
		parcel.writeString(streetadress);
		parcel.writeString(zipcode);
		parcel.writeString(city);
		parcel.writeString(areatype);
		parcel.writeString(stationtype);
		parcel.writeString(remark);
		parcel.writeString(firstmeasurement);
		parcel.writeString(lastemeasurement);
		parcel.writeInt(active);
	}
	
	   /**
     * Private constructor for Parcelable.
     */
    private Station(Parcel in) 
    {
		stationid = in.readString();
		stationname= in.readString();
		stationlatitude=in.readDouble();
		stationlongitude=in.readDouble();
		stationaltitude=in.readDouble();
		streetadress= in.readString();
		zipcode= in.readString();
		city= in.readString();
		areatype= in.readString();
		stationtype= in.readString();
		remark= in.readString();
		firstmeasurement= in.readString();
		lastemeasurement= in.readString();
		active= in.readInt();
    }

	public String getStationID() 
	{
		return stationid;
	}

	public Location getLocation() 
	{
		Location location = new Location("Station");
		location.setAltitude(stationaltitude);
		location.setLatitude(stationlatitude);
		location.setLongitude(stationlongitude);
		return location;
	}
	
	public String getStationName()
	{
		return stationname;
	}
	
	public String getAdress()
	{
		return streetadress;
	}

}
    
