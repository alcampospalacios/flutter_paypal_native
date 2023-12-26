package com.alcampospalacios.paypal.flutter_paypal_native.models;

import com.paypal.android.corepayments.Environment;
;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentHelper {
    Map<String, Environment> data =new HashMap<String, Environment>();

    public  EnvironmentHelper(){
        data.put("sandbox",Environment.SANDBOX);
        data.put("live",Environment.LIVE);
    }

    public Environment getEnumFromString(String which){
        if(data.get(which)!=null){
            return data.get(which);
        }
        return Environment.SANDBOX;
    }

}
