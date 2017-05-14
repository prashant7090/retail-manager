package com.retailmanager.impl;

import com.retailmanager.dao.ShopDao;
import com.retailmanager.model.Shop;
import com.retailmanager.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${nearby.distance}")
    private int NEARBYDISTANCE;

    @Autowired
    DistanceCalculator distanceCalculator;

    /**
     * Returns true if shop is added or false if updated. More formally, The shop name is unique
     * so, if another/same user tries to add shop with the same name, we update the shop info.
     * @param shop shop to be added .
     * @return <tt>true</tt> if new shop is added
     * @return <tt>false</tt> if shop information is updated
     */
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

    /**
     * Returns all the near by shops within NEARBYDISTANCE. More formally, It finds the shops which are in
     * NEARBYDISTANCE radius. The distance is calculated by <b>haversin</b> formula by
     * specifying customer's latitude, customer's longitude, shop's latitude and shop's longitude.
     * @param latitude Customer's latitude.
     * @param longitude Customer's longitude.
     * @return <tt>List if Shops </tt>
     */
    @Override
    public List<Shop> getNearByShops(Double latitude, Double longitude) {
        return shopMap.entrySet().stream()
                .filter(shops -> isValidLatLong(shops.getValue().getLatitude(), shops.getValue().getLongitude()))
                .filter(validShop ->
                        isNearByDistance(
                                distanceCalculator.getDistance(
                                        latitude, longitude,
                                        Double.parseDouble(validShop.getValue().getLatitude()),
                                        Double.parseDouble(validShop.getValue().getLongitude()))))
                .map(stringShopEntry -> stringShopEntry.getValue())
                .collect(Collectors.toList());

    }

    /**
     * Returns <tt>true</tt> if distance is less than or equals
     * to NEARBYDISTANCE.
     * @param distance distance to be tested for nearby
     * @return <tt>true</tt> if distance is less than or equals
     * to NEARBYDISTANCE.
     */
    private boolean isNearByDistance(double distance){
        return distance <= NEARBYDISTANCE;
    }


    /**
     * Returns <tt>true</tt> if this latitude and longitude is parsable to
     * double, More Formally, Some shop may not have latitude and longitude.
     * @param latitude latitude to test parsable to double.
     * @param longitude longitude to test parsable to double.
     * @return <tt>true</tt> if parameters are parsable to
     * double
     */
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
