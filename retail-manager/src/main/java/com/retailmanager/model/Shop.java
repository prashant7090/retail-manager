package com.retailmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by prashant on 5/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shop {
    private String shopName;
    private String shopAddress;
    private String shopPostCode;
    private String latitude;
    private String longitude;
    public String getShopName() {
        return shopName;
    }
    @Required
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    @Required
    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPostCode() {
        return shopPostCode;
    }

    @Required
    public void setShopPostCode(String shopPostCode) {
        this.shopPostCode = shopPostCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return "Shop{" +
                "shopName='" + shopName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", shopPostCode='" + shopPostCode + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
