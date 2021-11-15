package com.hubex.learningsystem.security.constants;

import com.hubex.learningsystem.SpringApplicationContext;
import com.hubex.learningsystem.security.properties.AppProperties;

public class SecurityConstants {
    public static final long TOKEN_EXPIRATION_TIME = 1000*60*60*24*7; // 7 days

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
