/*
 * Copyright (C) the Dynamix Framework Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambientdynamix.contextplugins.airpolutantsplugin;

import java.util.HashSet;
import java.util.Set;

import org.ambientdynamix.application.api.IContextInfo;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public class AirPolutantsPluginContextinfo implements IContextInfo 
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
    private String contextData;

    public String getSampleData() 
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
    	return contextData;
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

    public AirPolutantsPluginContextinfo(String contextData) 
    {
    	this.contextData = contextData;
    }

    private AirPolutantsPluginContextinfo(final Parcel in) 
    {
    	this.contextData = in.readString();
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
    	out.writeString(this.contextData);
    }
}