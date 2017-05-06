package com.retailmanager.impl;

import com.retailmanager.dao.ShopDao;
import com.retailmanager.model.Shop;
import com.retailmanager.util.Haversine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by prashant on 5/2/17.
 */
@Component
public class ShopDaoImpl implements ShopDao{
    Map<String,Shop> shopMap = new ConcurrentHashMap<>();

    private final int NEARBYDISTANCE = 10;

    @Autowired
    Haversine haversine;

    @Override
    public boolean addShop(Shop shop) {
        for (;;) {
            Shop oldShop = shopMap.putIfAbsent(shop.getShopName(),shop);
            if (oldShop == null)
                return true;

            if (shopMap.replace(shop.getShopName(), oldShop, shop))
                return false;
        }


    }

    @Override
    public List<Shop> getNearByShops(Double latitude, Double longitude) {

        return shopMap.entrySet().stream()
                .filter(shops -> isValidLatLong(shops.getValue().getLat(), shops.getValue().getLng()))
                .filter(validShop ->
                        isNearByDistance(
                                haversine.distance(
                                        latitude, longitude,
                                        Double.parseDouble(validShop.getValue().getLat()),
                                        Double.parseDouble(validShop.getValue().getLng()))))
                .map(stringShopEntry -> stringShopEntry.getValue())
                .collect(Collectors.toList());

    }

    private boolean isNearByDistance(double distance){
        return distance <= NEARBYDISTANCE;
    }



    private boolean isValidLatLong(String latitude, String longitude){
        try{
            Double.parseDouble(latitude);
            Double.parseDouble(longitude);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }

}
