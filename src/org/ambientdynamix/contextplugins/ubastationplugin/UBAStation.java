/*
 * This Plugin is licensed under GNU GPL v3 
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 *Author: Luaks Ruge
 */
package org.ambientdynamix.contextplugins.ubastationplugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.util.Log;

public class UBAStation extends Station
{

    private final String TAG = "UBAStation";
    
	public UBAStation(String id) 
	{
		super(id);
	}

	@Override
	public double sense(String code)
	{
    	//All the different codes for measurements
    	String state=getState(stationid);
    	//determine the State String based on the name of the station
	    String vv="";
   		try
   		{
    		String url = "http://www.env-it.de/luftdaten/statedata.csv?comp="+code+"&state="+state;
    		Log.i(TAG, "URL="+url);
			URL theurl = new URL(url);
			URLConnection yc = theurl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			int linenumber=0;
			while ((inputLine = in.readLine()) != null)
			{	
				StringTokenizer tk = new StringTokenizer(inputLine, ";");
				while(tk.hasMoreTokens())
				{
					if(tk.nextToken().equals(stationid))
					{
						tk.nextToken();
						vv= tk.nextToken();
					}
				}
				//System.out.println(inputLine);
			}
			in.close();
   		}
   		catch(Exception e)
   		{
   			
   		}
   		if(!(vv.equals(""))) //some value has been detected
   		{
			Log.i(TAG, "New Value");
			double vx= Double.parseDouble(vv);
			Log.i(TAG, "New Value: "+vx);
			return vx;
   		}
   		return -999.0;
	}
	
	private String getState(String station) 
	{
		if(station.startsWith("DEBW")) //Baden Württemberg
    	{
    		return "BW";
    	}
		if(station.startsWith("DEBY")) //Bayern
    	{
    		return "BY";
    	}
		if(station.startsWith("DEBE")) //Berin
    	{
    		return "BE";
    	}
		if(station.startsWith("DEBB")) //Brandenburg
    	{
    		return "BB";
    	}		
		if(station.startsWith("DEHB")) //Bremen
    	{
    		return "HB";
    	}		
    	if(station.startsWith("DEHH")) //Hamburg
    	{
    		return "HH";
    	}
		if(station.startsWith("DEHE")) //Hessen
    	{
    		return "HE";
    	}		
		if(station.startsWith("DEMV"))
    	{
    		return "MV";
    	}		
		if(station.startsWith("DENI"))
    	{
    		return "NI";
    	}
		if(station.startsWith("DERP"))
    	{
    		return "RP";
    	}
		if(station.startsWith("DESL"))
    	{
    		return "SL";
    	}
		if(station.startsWith("DESN"))
    	{
    		return "SN";
    	}
		if(station.startsWith("DEST"))
    	{
    		return "ST";
    	}
    	if(station.startsWith("DESH")) //Schleswig Holstein
    	{
    		return "SH";
    	}
    	if(station.startsWith("DETH")) //Thüringen
    	{
    		return "TH";
    	}
    	if(station.startsWith("DEUB")) //Umweltbundesamt
    	{
    		return "UB";
    	}
    	return "";
	}
}
