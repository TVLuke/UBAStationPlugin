/*
 * This Plugin is licensed under GNU GPL v3 
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 *Author: Luaks Ruge
 */
package org.ambientdynamix.contextplugins.airpolutantsplugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import org.ambientdynamix.contextplugin.api.ContextPluginSettings;
import org.ambientdynamix.contextplugin.api.PluginPowerScheme;
import org.ambientdynamix.contextplugin.api.PluginState;
import org.ambientdynamix.contextplugin.api.PushPullContextPluginRuntime;
import org.ambientdynamix.contextplugin.api.security.PrivacyRiskLevel;
import org.ambientdynamix.contextplugin.api.security.SecuredContextInfo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Bla
 * 
 * @author Lukas Ruge
 */
public class AirPolutantsPluginRuntime extends PushPullContextPluginRuntime {
    private final String TAG = this.getClass().getSimpleName();
	private boolean okToRun=true;
    private PluginPowerScheme powerScheme;
    public static final String SAMPLE_DATA_KEY = "SAMPLE_DATA_KEY";
    private String sampleData;
    private static final int EVENT_VALID_MILLS = 5000;
	private LocationManager locationManager;
	private String provider;
	private ArrayList stations = new ArrayList(); //this list will probably eplace all the other station lists
	private ArrayList codes = new ArrayList();
	private ArrayList units = new ArrayList();
	private ArrayList limits = new ArrayList();
	private ArrayList description = new ArrayList();
	private ArrayList states = new ArrayList();
	Measurement[] values= new Measurement[5]; //values[1] = ozone; values[2] = Particulates...
    double[] distances = new double[5];
    
	@Override
    public void init(PluginPowerScheme scheme, ContextPluginSettings settings) throws Exception 
    {
    		// Setup new state
    		Log.i("Muhaha", "INIT");
    		this.setPowerScheme(scheme);
    		//set up values
    		for(int i=0; i<values.length; i++)
    		{
    			Measurement m1 = new Measurement();
    			values[i]=m1; //-999.9 is the dafault value.
    		}
    		Log.i("Muhaha", "whatup");
    		//Codes for Polutants
        	codes.add("PM1");//Particulates
        	units.add("µg/m^3");
        	limits.add(50.0);
        	description.add("Particulates smaler then 10 µm");
        	codes.add("O3"); //Ozone
        	units.add("µg/m^3");
        	limits.add(240.0);
        	codes.add("CO"); //Kohlenmonoxid 
        	units.add("mg/m^3");
        	limits.add(10.0);
        	codes.add("SO2");//Schwefeldioxid
        	units.add("µg/m^3");
        	limits.add(350.0);
        	codes.add("NO2");//Stickstoffdioxid
        	units.add("µg/m^3");
        	limits.add(200.0);
    		//set up station data
    		states.add("BW");
    		states.add("BY");
    		states.add("BE");
    		states.add("BB");
    		states.add("HB");
    		states.add("HH");
    		states.add("HE");
    		states.add("MV");
    		states.add("NI");
    		states.add("RP");
    		states.add("SL");
    		states.add("SN");
    		states.add("ST");
    		states.add("SH");
    		states.add("TH");
    		states.add("UB");
        	setupstaions();    
		/*
		 * Try to load our settings. Note: init can be called when we're NEW and INITIALIZED (during updates)
		 */
		if (loadSettings(settings)) 
		{
		    // Since we successfully loaded settings, tell Dynamix we're configured.
		    getAndroidFacade().setPluginConfiguredStatus(getSessionId(), true);
		}
		else
		{
		    // Since failed to load our settings, tell Dynamix we're not configured.
		    getAndroidFacade().setPluginConfiguredStatus(getSessionId(), false);
		}
		//Log.i("Muhaha", "done with init");
    }

	@Override
    public void start() 
    {
		Log.i("Muhaha", "STAAAAAAARTS");
   		try 
		{
			Thread.sleep(5000); //one minute
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		okToRun=true;
		Log.i("Muhaha", "The wait is over");
    	while(okToRun)
    	{
    		Log.i("Muhaha", "OK TO RUN");
			boolean incomplete=true;
    		Log.i("Muhaha", "OKTORUN="+okToRun);
    		// Get the location manager
    		locationManager = (LocationManager) getSecuredContext().getSystemService(Context.LOCATION_SERVICE);
    		//Define the criteria how to select the locatioin provider -> use
    		// default
    		Criteria criteria = new Criteria();
    		provider = locationManager.getBestProvider(criteria, false);
    		Location location=new Location("Airpolutantsplugin");
    		int sn=0;
    		while((location==null && sn<500) || (location.getLatitude()==0.0 && location.getLongitude()==0.0))
    		{
    			sn++;
    			Log.i("Muhaha", sn+" search for location");
    			location = locationManager.getLastKnownLocation(provider);
    			if(sn==499)
    			{
    				location.setLatitude(53.83);
    				location.setLongitude(10.69);
    				Log.i("Muhaha", "unable to get actual location.");
    			}
    		}
    		Log.i("Muhaha", "OKTORUNÖÖÖLLLKKK="+okToRun);
    		//get the station that is closest
    		Log.i("Muhaha", "got location"+location.getLatitude());
    		double distance=0.0;
    		int aNumber =0;
    		ArrayList stations2 = new ArrayList();
    		stations2=(ArrayList) stations.clone();
    		Station station=new Station("FakeStation");
    		incomplete=true;
    		double[] newvalues = new double[values.length];
    		for(int i=0; i<newvalues.length; i++)
    		{
    			newvalues[i]=-999.9; //default value
    		}
    		//the process is incomplete as long as there are values unset (if there are no stations left, the process is complete to)
    		Log.i("Muhaha", "go");
    		Log.i("Muhaha", "OKTORUNNNNN="+okToRun);
    		while(incomplete)
    		{
    			aNumber=0;
    			distance=0.0;
        		//Log.i("Muhaha", "a");
	    		for(int i=0; i<stations2.size(); i++)
	    		{
	    			Station stationx = (Station) stations2.get(i);
	        		//Log.i("Muhaha", "b");
	    			//String thestation = (String) stationx.getStationID();
	        		//Log.i("Muhaha", thestation);
	    			Location stationloc=(Location) stationx.getLocation();
	    			//Log.i("Muhaha", "->");
	    			//Log.i("Muhaha", "->"+stationloc.getLatitude());
	    			double actualdistance = location.distanceTo(stationloc);
	    			//Log.i("Muhaha", ""+actualdistance);
	    			if(distance==0.0 || actualdistance < distance)
	    			{
	    				distance = actualdistance;
	    				station = stationx;
	    				aNumber=i;
	    			}
	    		}
	    		//Log.i("Muhaha", ""+station+" "+distance);
    			//if the closest station is more then bla away, just stop looking
    			if(distance>60000)
    			{
    				incomplete=false;
    	    		//Log.i("Muhaha", "fffffaaaaarrrr"+station);
    			}
	    		//Here the readout happens
	    		//Log.i("Muhaha", "readout"+station);
	    		readout(newvalues, station, distance);
	    		//then the stationname is deleted out of names2
	    		stations2.remove(aNumber);
	    		//check if there is an unfilled value in the newvalues array
	    		boolean unfilled = false;
	    		for(int j=0; j<newvalues.length; j++)
	    		{
	    			if(newvalues[j]==-999.9)
	    			{
	    				unfilled=true;
	    			}
	    			else
	    			{
		    			if(distances[j]==0)
		    			{
		    				distances[j]=distance;
		    			}
	    			}
	    		}
	    		if(stations2.size()==0 || !unfilled)
	    		{
	    			incomplete=false;
	    		}
    		}
    		//put newvalues into values (unless its -999.9)
    		Log.i("Muhaha", "OKTORUNaaaaa="+okToRun);
    		for(int i=0; i<newvalues.length; i++)
    		{
    			if(!(newvalues[i]==-999.9))
    			{
    				Date d = new Date();
    				String ax=(String)codes.get(i);
    				String bx = (String) units.get(i);
    				//Here should be a check if a limit was exeeded, if so, there should be a push context, if not, not.
    				values[i].update(ax, newvalues[i], bx, station, d, distances[i]);
    			}
    		}
    		doPushContextDetection();
    		try 
    		{
    			Thread.sleep(5000); //one minute
    			Log.i("Muhaha", "high sleep");
    			if(powerScheme == PluginPowerScheme.BALANCED || powerScheme == PluginPowerScheme.MANUAL)
    			{
    				Thread.sleep(295000); //five minutes
    				Log.i("Muhaha", "balanced sleep");
    			}
    			if(powerScheme == PluginPowerScheme.POWER_SAVER)
    			{
    				Thread.sleep(10500000); //three hours
    				Log.i("Muhaha", "saver sleep");
    			}
        		Log.i("Muhaha", "OKTORUNererer="+okToRun);
    		}
    		catch (Exception e) 
    		{
				e.printStackTrace();
				Log.i("Muhaha", "W00t Error");
			}
    		Log.i("Muhaha", "OKTORUN="+okToRun);
    		Log.i("Muhaha", "and..."+stations.size());
    	}
    	Log.i("Muhaha", "out");
    }

    private void readout(double[] newvalues, Station station, double distance) 
    {
		newvalues= station.sense(newvalues, codes);
    	//find the parts where it sais -999.9 in the distances but not in newvalues, change that to distance
	}

	@Override
    public void handleContextRequest(UUID requestId, String contextDataType) 
    {
		/*
		 * Perform context scan without configuration.
		 */
	    pullEventHelper("", requestId, EVENT_VALID_MILLS);
    }

    @Override
    public void handleConfiguredContextRequest(UUID requestId, String contextInfoType, Bundle scanConfig) 
    {
	/*
	 * Use the incoming scanConfig Bundle to control how we perform the context scan.
	 */
	    Log.i(TAG, "handleConfiguredContextRequest for requestId: " + requestId);
    }

    @Override
    public void stop() 
    {
		okToRun = false;
		Thread t=Thread.currentThread();
		t.interrupt();  
		Log.d(TAG, "Stopped!");
		Log.d("Muhaha", "Stopped!111");
    }

    @Override
    public void destroy() 
    {
		stop();
		Log.i(TAG, this + " is Destroyed!");
    }

    @Override
    public void doManualContextScan() 
    {
	    pushEventHelper("", EVENT_VALID_MILLS);
    }

    @Override
    public void updateSettings(ContextPluginSettings settings) 
    {
	if (loadSettings(settings)) {
	    getAndroidFacade().storeContextPluginSettings(getSessionId(), settings);
	    getAndroidFacade().setPluginConfiguredStatus(getSessionId(), true);
	}
    }

    @Override
    public void setPowerScheme(PluginPowerScheme scheme) 
    {
		Log.i(TAG, "Setting PowerScheme " + scheme);
		powerScheme = scheme;
    }

    /*
     * Simple context detection loop that generates push events.
     */
    private void doPushContextDetection() 
    {
		Log.i("Muhaha", "Entering doPushContextDetection");
	    // Send a sample broadcast event
	    pushEventHelper("", EVENT_VALID_MILLS);
	    Log.i("Muhaha", "Exiting doPushContextDetection");
    }

	/*
     * Utility for responding to pull requests.
     */
    private void pullEventHelper(String message, UUID requestId, int validMills) 
    {
    	sendContextEvent(requestId, constructEventList(message), validMills);
    }

    /*
     * Utility for sending push events.
     */
    private void pushEventHelper(String message, int validMills) 
    {
    	sendBroadcastContextEvent(constructEventList(message), validMills);
    }

    /*
     * Utility that constructs a list of SecuredContextInfo containing each different FidelityLevel.
     */
    private List<SecuredContextInfo> constructEventList(String message) 
    {
    	MeasurementList m = new MeasurementList(values);
		List<SecuredContextInfo> eventList = new Vector<SecuredContextInfo>();
		eventList.add(new SecuredContextInfo(new AirPolutantsPluginContextinfo(m), PrivacyRiskLevel.LOW));
		return eventList;
    }

    /*
     * Utility for loading settings.
     */
    private boolean loadSettings(ContextPluginSettings settings) 
    {
	// Check settings type and store
	if (settings != null) 
	{
	    Log.i(TAG, "Received previously stored settings: " + settings);
	    try 
	    {
	    	sampleData = settings.get(SAMPLE_DATA_KEY);
	    	return true;
	    }
	    	catch (Exception e) {
	    		Log.w(TAG, "Failed to parse settings: " + e.getMessage());
	    }
	}
	else
	    if (settings == null) 
	    {
		// Create default settings
		Log.i(TAG, "No settings found!");
	    }
	return false;
    }
    
    private void setupstaions() 
    {
		Log.i("Muhaha", "Set Up Stations");
		for(int i=0; i<states.size(); i++)
    	{
    		String state=(String) states.get(i);
    		//Log.i("Muhaha", state);
    		int code=1;
    		boolean more=true;
	    	while(more && code<200)
	    	{
	    		String stationcode="";
	    		double vLogitude=0.0;
	    		double vLatitude=0.0;
	    		double vAltitude=0.0;
	    		String sensorx="";
	    		if(code<10)
	    		{
	    			stationcode="DE"+state+"00"+code;
	    		}
	    		if(code>9 && code<100)
	    		{
	    			stationcode="DE"+state+"0"+code;
	    		}
	    		if(code>99)
	    		{
	    			stationcode="DE"+state+""+code;
	    		}
	    		code++;
	    		BUDStation station= new BUDStation(stationcode);
	    		//Log.i("Muhaha", stationcode);
		    	try
				{
		    		String url = "http://www.env-it.de/stationen/public/download.do?event=downloadStation&stationcodeForDownload="+stationcode;
					URL theurl = new URL(url);
					URLConnection yc = theurl.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
					String inputLine="";
					int linenumber=0;
					boolean documentstart=false;
					while ((inputLine = in.readLine()) != null)
					{	
						boolean newsensor=false;
						boolean relevant=true;
						if(inputLine.contains("No accepted data"))
						{
							relevant=false;
						}
						if(inputLine.contains("Stationname"))
						{
							documentstart=true;
						}
						if(inputLine.startsWith("\\par") ||inputLine.startsWith("\\trowd") || inputLine.startsWith("trowd") || inputLine.contains("trgraph") || inputLine.contains("No accepted data"))
						{
							relevant=false;
						}
						if(documentstart && relevant)
						{
							if(inputLine.startsWith("\\intbl \\cell {\\b"))
							{
								newsensor=true;
							}
							inputLine= inputLine.replace("\\intbl", "");
							inputLine= inputLine.replace("}", "");
							inputLine= inputLine.replace("{", "");
							inputLine= inputLine.replace("\\b ", "");
							inputLine= inputLine.replace("\\fs20", "");
							inputLine= inputLine.replace("\\cell", "");
							inputLine= inputLine.replace("\\row", "");
							inputLine= inputLine.replace("\\pard", "");
							StringTokenizer tk = new StringTokenizer(inputLine, " ");
							while(tk.hasMoreTokens())
							{
								//Metainfo URL: url
								//Stationcode stationcode
								//
								String theline=tk.nextToken();
								//the specific device, but not a specific sensor of the device
								if((sensorx.equals("")))
								{
									if(theline.contains("Stationname"))
									{
										String stationname="";
										while(tk.hasMoreTokens())
										{
											stationname=stationname+" "+tk.nextToken();
										}
										Log.i("Muhaha", stationcode+">"+stationname);
										station.setStationName(stationname);
									}
									if(theline.contains("First"))
									{
										tk.nextToken();
										tk.nextToken();
										tk.nextToken();
										String datum1=tk.nextToken();
										station.setfirstmeasurement(datum1);
									}
									if(theline.contains("Last"))
									{
										tk.nextToken();
										tk.nextToken();
										tk.nextToken();
										boolean open=false;
										String datum2=tk.nextToken();
										datum2=datum2.trim();
										station.setlastemeasurement(datum2);
										if(datum2.equals("-"))
										{
											open=true;
											station.setactive(1);
											Log.i("Muhaha", stationcode+"> open");
										}
									}
									if(theline.contains("Street"))
									{
										String streetname="";
										while(tk.hasMoreTokens())
										{
											streetname=streetname+" "+tk.nextToken();
										}
										station.setstreetadress(streetname);
										Log.i("Muhaha", stationcode+">"+streetname);
									}
									if(theline.contains("ZIP"))
									{
										String zip=tk.nextToken();
										station.setzipcode(zip);
										tk.nextToken();
										String city =tk.nextToken();
										station.setcity(city);
										Log.i("Muhaha", stationcode+">"+zip+","+city);
									}
									if(theline.contains("Decimal"))
									{
										String a= tk.nextToken();
										//Log.i("Muhaha","a="+a);
									    vLogitude = Double.parseDouble(a);
									    String b = tk.nextToken();
									    //Log.i("Muhaha","b="+b);
									    vLatitude = Double.parseDouble(b);
										//vv= tk.nextToken();
									}
									if(theline.contains("Altitude") && !(inputLine.contains("Altitude above")))
									{
										String a= tk.nextToken();
										//Log.i("Muhaha","a2="+a);
									    vAltitude = Double.parseDouble(a);
										//vv= tk.nextToken();
									}
									if(theline.contains("area"))
									{
										tk.nextToken();
										String type=tk.nextToken();
										station.setareatype(type);
									}
									if(theline.contains("station") && inputLine.contains("Type of the station"))
									{
										tk.nextToken();
										String type=tk.nextToken();
										station.setstationtype(type);
									}
									if(theline.contains("carrier"))
									{
										String remark="";
										while(tk.hasMoreTokens())
										{
											remark=remark+" "+tk.nextToken();
										}
										station.setremark(remark);
										Log.i("Muhaha", stationcode+">"+remark);
									}
									
								}
								if(newsensor)
								{
									String s=theline;
									while(tk.hasMoreTokens())
									{
										s=s+" "+tk.nextToken();
									}
									Log.i("Muhaha", stationcode+">"+s);
									sensorx=s;
								}
								if(!(sensorx.equals("")))
								{
									if(theline.contains("period") && inputLine.contains("Referenced period"))
									{
										String text="";
										double period=0.0;
										String aggregationMethod="";
										while(tk.hasMoreTokens())
										{
											text=text+" "+tk.nextToken();
										}
										if(text.equals("monthly mean"))
										{
											
										}
										if(text.equals("daily mean"))
										{
											
										}
										
										Log.i("Muhaha", stationcode+">"+text);
									}
									if(theline.contains("method") && inputLine.contains("Sampling method"))
									{
										String text="";
										while(tk.hasMoreTokens())
										{
											text=text+" "+tk.nextToken();
										}
										Log.i("Muhaha", stationcode+">"+text);
									}
									if(theline.contains("frequency") && inputLine.contains("Sampling frequency"))
									{
										String text="";
										while(tk.hasMoreTokens())
										{
											text=text+" "+tk.nextToken();
										}
										Log.i("Muhaha", stationcode+">"+text);
									}
								}
							}
						}
					}
					in.close();
				}
				catch(Exception e)
				{
					Log.i("Muhaha", "exception");
					vAltitude =-999;
				}
				if(more && vLatitude>0.0 && vLogitude>0.0 && !(vAltitude ==-999))
				{
					String newcode= new String(stationcode);
					Double newlat = new Double(vLatitude);
					Double newlog = new Double(vLogitude);
					Double newalt = new Double(vAltitude);
			    	//stationnames.add(newcode);
					Location loc = new Location("Airpolutantsplugin");
					loc.setLatitude(newlat);
					loc.setLongitude(newlog);
					loc.setAltitude(newalt);
					//stationlocation.add(loc);
					station.setLocation(loc);
					stations.add(station);
					Log.i("Muhaha", stationcode+" "+vLatitude+" "+vLogitude+" "+vAltitude);
				}
	    	}	
    	}
		Log.i("Muhaha", ""+stations.size());
	}
}