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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import org.ambientdynamix.api.contextplugin.AutoReactiveContextPluginRuntime;
import org.ambientdynamix.api.contextplugin.ContextPluginSettings;
import org.ambientdynamix.api.contextplugin.PowerScheme;
import org.ambientdynamix.api.contextplugin.security.PrivacyRiskLevel;
import org.ambientdynamix.api.contextplugin.security.SecuredContextInfo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Bla
 * 
 * @author Lukas Ruge
 */
public class UBAStationPluginRuntime extends AutoReactiveContextPluginRuntime 
{
    private final String TAG = "UBAStation";
	private boolean okToRun=true;
    private PowerScheme powerScheme;
    public static final String SAMPLE_DATA_KEY = "SAMPLE_DATA_KEY";
    private String sampleData;
    private static final int EVENT_VALID_MILLS = 5000;
	private LocationManager locationManager;
	private String provider;
	private ArrayList<UBAStation> stations = new ArrayList<UBAStation>(); //this list will probably replace all the other station lists
	private ArrayList codes = new ArrayList();
	private ArrayList units = new ArrayList();
	private ArrayList limits = new ArrayList();
	private ArrayList description = new ArrayList();
	private ArrayList states = new ArrayList();
	Measurement[] values= new Measurement[5]; //values[1] = ozone; values[2] = Particulates...
    double[] distances = new double[5];
    public static int throu =0;
    
	@Override
    public void init(PowerScheme scheme, ContextPluginSettings settings) throws Exception 
    {
    		// Setup new state
    		Log.i(TAG, "INIT");
    		this.setPowerScheme(scheme);
    		//set up values
    		for(int i=0; i<values.length; i++)
    		{
    			Measurement m1 = new Measurement();
    			values[i]=m1; //-999.9 is the dafault value.
    		}
    		Log.i(TAG, "whatup");
    		//Codes for Polutants
        	codes.add("PM1");//Particulates
        	units.add("mycro g/m^3");
        	limits.add(50.0);
        	description.add("Particulates smaler then 10 mycro m");
        	codes.add("O3"); //Ozone
        	units.add("Âµg/m^3");
        	limits.add(240.0);
        	codes.add("CO"); //Kohlenmonoxid 
        	units.add("mg/m^3");
        	limits.add(10.0);
        	codes.add("SO2");//Schwefeldioxid
        	units.add("Âµg/m^3");
        	limits.add(350.0);
        	codes.add("NO2");//Stickstoffdioxid
        	units.add("Âµg/m^3");
        	limits.add(200.0);
    		//set up station data
    		//states.add("BW");
    		//states.add("BY");
    		//states.add("BE");
    		//states.add("BB");
    		//states.add("HB");
    		states.add("HH");
    		//states.add("HE");
    		states.add("MV");
    		states.add("NI");
    		//states.add("RP");
    		//states.add("SL");
    		//states.add("SN");
    		//states.add("ST");
    		states.add("SH");
    		//states.add("TH");
    		states.add("UB");
    		new Thread(new Runnable() 
	    	{
	    	    public void run() 
	    	    {
	    	    	setupstaions();
	    	    }
	    	}).start();
			Thread t1 = new Thread( new BackendRunner());
			t1.start();
    }

	class BackendRunner implements Runnable
	{
		private Handler handler = new Handler();
		private int delay=10000;
		long counter=0;
		
		@Override
		public void run() 
		{
			
			Log.i(TAG, "okToRun");
    		locationManager = (LocationManager) getSecuredContext().getSystemService(Context.LOCATION_SERVICE);
    		Criteria crit = new Criteria();
    		crit.setAccuracy(Criteria.ACCURACY_FINE);
    		String provider = locationManager.getBestProvider(crit, true);
    		Location loc = locationManager.getLastKnownLocation(provider);
    		
    		ArrayList<UBAStation> stations2 = new ArrayList<UBAStation>();
    		stations2= (ArrayList<UBAStation>) stations.clone();
    		
    		int aNumber = 0;
    		double distance=0.0;
    		
    		for(int i=stations2.size()-1; i>0; i--)
    		{
    			Station stationx = (Station) stations2.get(i);
        		//Log.i(TAG, "b");
    			//String thestation = (String) stationx.getStationID();
        		//Log.i(TAG, thestation);
    			Location stationloc=(Location) stationx.getLocation();
    			//Log.i(TAG, "->");
    			//Log.i(TAG, "->"+stationloc.getLatitude());
    			double actualdistance = loc.distanceTo(stationloc);
    			//Log.i(TAG, ""+actualdistance);
    			if(actualdistance > 100000)
    			{
    				stations2.remove(i);
    			}
    		}
    		AmbientCarbonMonoxideContextInfo aci = new AmbientCarbonMonoxideContextInfo(stations2);
    		double[] cov = aci.getCOValue();
    		for(int i=0; i<cov.length; i++)
    		{
    			Log.i(TAG, cov[i]+" mg/m^3");
    		}
    		//SecuredContextInfo aci= new SecuredContextInfo(new AmbientCarbonMonoxideContextInfo(stations2), PrivacyRiskLevel.MEDIUM);
    		try 
    		{
    			Thread.sleep(60000);
    		}
    		catch(Exception e)
    		{
    			Log.i(TAG, "Exception");
    		}
			
			handler.removeCallbacks(this); // remove the old callback
			if(okToRun)
			{
				handler.postDelayed(this, delay); // register a new one
			}
		}
		
		public void onResume() 
		{
			handler.postDelayed(this, delay);
		}

		public void onPause() 
		{
			handler.removeCallbacks(this); // stop the map from updating
		}
	}

	@Override
    public void handleContextRequest(UUID requestId, String contextInfoType) 
    {
		/*
		 * Perform context scan without configuration.
		 */
		locationManager = (LocationManager) getSecuredContext().getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(crit, true);
		Location loc = locationManager.getLastKnownLocation(provider);
		
		ArrayList<UBAStation> stations2 = new ArrayList<UBAStation>();
		stations2= (ArrayList<UBAStation>) stations.clone();
		
		int aNumber = 0;
		double distance=0.0;
		//get the closes station
		for(int i=0; i<stations2.size(); i++)
		{
			Station stationx = (Station) stations2.get(i);
    		//Log.i(TAG, "b");
			//String thestation = (String) stationx.getStationID();
    		//Log.i(TAG, thestation);
			Location stationloc=(Location) stationx.getLocation();
			//Log.i(TAG, "->");
			//Log.i(TAG, "->"+stationloc.getLatitude());
			double actualdistance = loc.distanceTo(stationloc);
			//Log.i(TAG, ""+actualdistance);
			if(distance==0.0 || actualdistance < distance)
			{
				distance = actualdistance;
				aNumber=i;
			}
		}

		if(contextInfoType.equals("org.ambientdynamix.contextplugins.context.info.environment.carbonmonoxide"))
		{
			//SecuredContextInfo aci= new SecuredContextInfo(new AmbientCarbonMonoxideContextInfo(stations.get(aNumber)), PrivacyRiskLevel.MEDIUM);
			//sendContextEvent(requestId, aci, 1000000);
		}
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
		Log.d(TAG, "Stopped!111");
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
	if (loadSettings(settings)) 
	{
		getPluginFacade().storeContextPluginSettings(getSessionId(), settings);
		getPluginFacade().setPluginConfiguredStatus(getSessionId(), true);
	}
    }

    @Override
    public void setPowerScheme(PowerScheme scheme) 
    {
		Log.i(TAG, "Setting PowerScheme " + scheme);
		powerScheme = scheme;
    }

    /*
     * Simple context detection loop that generates push events.
     */
    private void doPushContextDetection() 
    {
		Log.i(TAG, "Entering doPushContextDetection");
	    // Send a sample broadcast event
	    pushEventHelper("", EVENT_VALID_MILLS);
	    Log.i(TAG, "Exiting doPushContextDetection");
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
		eventList.add(new SecuredContextInfo(new UBAStationPluginContextinfo(m), PrivacyRiskLevel.LOW));
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
		Log.i(TAG, "Set Up Stations");
		for(int i=0; i<states.size(); i++)
    	{
			Log.i(TAG, "outer for loop "+i);
    		String state=(String) states.get(i);
    		//Log.i(TAG, state);
    		int code=1;
    		boolean more=true;
	    	while(more && code<200)
	    	{
	    		Log.i(TAG, "while more "+code);
	    		throu++;
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
	    		Log.i(TAG, "stationcode "+stationcode);
	    		UBAStation station= new UBAStation(stationcode);
	    		//Log.i(TAG, stationcode);
		    	try
				{
		    		
		    		String url = "http://www.env-it.de/stationen/public/download.do?event=downloadStation&stationcodeForDownload="+stationcode;
		    		Log.i(TAG, "try "+url);
					URL theurl = new URL(url);
					URLConnection yc = theurl.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
					String inputLine="";
					int linenumber=0;
					boolean documentstart=false;
					while ((inputLine = in.readLine()) != null)
					{	
						//Log.i(TAG, "line "+inputLine);
						boolean newsensor=false;
						boolean relevant=true;
						if(inputLine.contains("No accepted data"))
						{
							relevant=false;
						}
						if(inputLine.contains("Stationsname"))
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
							inputLine= inputLine.replace("Times New Roman", "");
							inputLine= inputLine.replace("\\ansi", "");
							inputLine= inputLine.replace("\\rtf1", "");
							inputLine= inputLine.replace(";", "");
							inputLine= inputLine.replace("\\b", "");

							StringTokenizer tk = new StringTokenizer(inputLine, " ");
							while(tk.hasMoreTokens())
							{
								//Metainfo URL: url
								//Stationcode stationcode
								//
								String theline=tk.nextToken();
								if(theline.startsWith("\\"))
								{
									break;
								}
								//the specific device, but not a specific sensor of the device
								if((sensorx.equals("")))
								{
									if(theline.contains("Stationsname"))
									{
										Log.i(TAG, "name");
										String stationname="";
										while(tk.hasMoreTokens())
										{
											stationname=stationname+" "+tk.nextToken();
											
										}
										Log.i(TAG, stationcode+">"+stationname);
										station.setStationName(stationname);
										Log.i(TAG, "station "+stationname);
									}
									if(theline.contains("Aktuelle Aktivitätsperiode: von"))
									{
										Log.i(TAG, "aktivität von");
										tk.nextToken();
										tk.nextToken();
										tk.nextToken();
										String datum1=tk.nextToken();
										station.setfirstmeasurement(datum1);
									}
									if(theline.contains("Aktuelle Aktivitätsperiode: bis"))
									{
										Log.i(TAG, "aktivität bis");
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
											Log.i(TAG, stationcode+"> open");
										}
									}
									if(theline.contains("Straße"))
									{
										Log.i(TAG, "straße");
										String streetname="";
										while(tk.hasMoreTokens())
										{
											streetname=streetname+" "+tk.nextToken();
										}
										station.setstreetadress(streetname);
										Log.i(TAG, stationcode+">"+streetname);
									}
									if(theline.contains("PLZ"))
									{
										Log.i(TAG, "plz");
										String zip=tk.nextToken();
										station.setzipcode(zip);
										tk.nextToken();
										String city =tk.nextToken();
										station.setcity(city);
										Log.i(TAG, stationcode+">"+zip+","+city);
									}
									if(theline.contains("Dezimal"))
									{
										Log.i(TAG, "dezimal");
										String a= tk.nextToken();
										//Log.i(TAG,"a="+a);
										a = a.replace(",", ".");
									    vLogitude = Double.parseDouble(a);
									    String b = tk.nextToken();
										b = b.replace(",", ".");
									    vLatitude = Double.parseDouble(b);
										//vv= tk.nextToken();
									}
									if(theline.contains("Höhe") && !(inputLine.contains("Höhe über")))
									{
										Log.i(TAG, "höhe");
										String a= tk.nextToken();
										a = a.replace(",", ".");
										//Log.i(TAG,"a2="+a);
									    vAltitude = Double.parseDouble(a);
										//vv= tk.nextToken();
									}
									if(theline.contains("Ort"))
									{
										tk.nextToken();
										String type=tk.nextToken();
										station.setareatype(type);
									}
									if(theline.contains("Station") && inputLine.contains("Art der Station"))
									{
										tk.nextToken();
										String type=tk.nextToken();
										station.setstationtype(type);
									}
									if(theline.contains("Bemerkung des Netzbetreibers"))
									{
										String remark="";
										while(tk.hasMoreTokens())
										{
											remark=remark+" "+tk.nextToken();
										}
										station.setremark(remark);
										Log.i(TAG, stationcode+">"+remark);
									}
									
								}
								if(newsensor)
								{
									String s=theline;
									while(tk.hasMoreTokens())
									{
										s=s+" "+tk.nextToken();
									}
									Log.i(TAG, stationcode+">>"+s);
									sensorx=s;
								}
								if(!(sensorx.equals("")))
								{
									if(theline.contains("Messdauer") || inputLine.contains("Bezugszeitraum"))
									{
										Log.i(TAG, "Messdauer");
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
										
										Log.i(TAG, stationcode+">"+text);
									}
									if(theline.contains("Mess-/ Probenahmemethode"))
									{
										String text="";
										while(tk.hasMoreTokens())
										{
											text=text+" "+tk.nextToken();
										}
										Log.i(TAG, stationcode+">"+text);
									}
									if(theline.contains("Messfrequenz") || inputLine.contains("Probenahmefrequenz"))
									{
										String text="";
										while(tk.hasMoreTokens())
										{
											text=text+" "+tk.nextToken();
										}
										Log.i(TAG, stationcode+">"+text);
									}
								}
							}
						}
					}
					in.close();
				}
				catch(Exception e)
				{
					Log.i(TAG, "exception");
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
					Log.i(TAG, stationcode+" "+vLatitude+" "+vLogitude+" "+vAltitude);
				}
	    	}	
    	}
		Log.i(TAG, ""+stations.size());
		this.getPluginFacade().setPluginConfiguredStatus(getSessionId(), true);
	}

	@Override
	public void start() throws Exception 
	{
		// TODO Auto-generated method stub
		
	}
}