package com.alcampospalacios.paypal.paypal_native_checkout.models;

import com.paypal.checkout.config.Environment;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentHelper {
    Map<String, Environment> data =new HashMap<String, Environment>();

    public  EnvironmentHelper(){
        data.put("sandbox",Environment.SANDBOX);
        data.put("stage",Environment.STAGE);
        data.put("local",Environment.LOCAL);
        data.put("live",Environment.LIVE);
    }

    public Environment getEnumFromString(String which){
        if(data.get(which)!=null){
            return data.get(which);
        }
        return Environment.SANDBOX;
    }

}
