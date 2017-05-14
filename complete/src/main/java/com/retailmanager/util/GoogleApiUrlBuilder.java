package com.retailmanager.util;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * Created by prashant on 5/7/17.
 */
@Component
public class GoogleApiUrlBuilder {
    private  String apiUrl;
    private  String address;
    private  String postalCode;
    private String googleApiKey;

    public GoogleApiUrlBuilder withApiUrl(String apiUrl){
        this.apiUrl = apiUrl;
        return  this;
    }

    public GoogleApiUrlBuilder withAddress(String address){
        this.address = address;
        return  this;
    }

    public GoogleApiUrlBuilder withPostalCode(String postalCode){
        this.postalCode = postalCode;
        return  this;
    }

    public GoogleApiUrlBuilder withGoogleApiKey(String googleApiKey){
        this.googleApiKey = googleApiKey;
        return this;
    }

    /**
     * Returns url for google map API
     * @return <p>Returns url for google map API</p>
     */
    public String build(){
       String url = "";

        if(hasValue(apiUrl)){
            url = apiUrl + "?address=";
        }

        if (hasValue(address)){
            url = url + URLEncoder.encode(address + " ");
        }

        if (hasValue(postalCode)){
            url = url + URLEncoder.encode(postalCode) + "&key=";
        }

        if (hasValue(googleApiKey)){
            url = url+ URLEncoder.encode(googleApiKey);
        }

        url = url + "&sensor=true";
        System.out.println("URL: " + url);
        return url;
    }
    /**
     * Returns true if string is not null and not a empty
     * @return <tt> true </tt> if string has some value
     * @return <tt> false </tt> if string is null or empty
     */
    private boolean hasValue(String value) {
        return value != null && value.trim().length() > 0;
    }
}
