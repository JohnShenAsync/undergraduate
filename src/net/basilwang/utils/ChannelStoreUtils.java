package net.basilwang.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ChannelStoreUtils {
	public static String getChannel(Context ctx){  
        String CHANNELID="000000";  
        try {  
               ApplicationInfo  ai = ctx.getPackageManager().getApplicationInfo(  
                       ctx.getPackageName(), PackageManager.GET_META_DATA);  
               Object value = ai.metaData.get("");  
               if (value != null) {  
                   CHANNELID= value.toString();  
               }  
           } catch (Exception e) {  
               //  
           }  
          
        return CHANNELID;  
    } 
}
