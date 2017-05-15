package com.retailmanager.impl;

import com.retailmanager.dao.ShopDao;
import com.retailmanager.model.Shop;
import com.retailmanager.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
     * Returns map with message as <b>New shop is Created</b> along with shop information if shop is created first time.
     * Returns map with message as <b>Shop address is Updated</b> along with previous and new shop information if same shop is updated. More formally, The shop name is unique
     * so, if another/same user tries to add shop with the same name, we update the shop info.
     * @param shop shop to be added .
     * @return returns map with message as <b>New shop is Created</b> along with shop information if shop is created and
     * Returns map with message as <b>Shop address is Updated</b> along with previous and new shop information if same shop is updated
     */
    @Override
    public Map addShop(Shop shop) {
        for (;;) {
            Shop oldShop = shopMap.putIfAbsent(shop.getShopName(),shop);
            if (oldShop == null) {
                Map responseMap = new HashMap();
                responseMap.put("message", "New shop is Created");
                responseMap.put("shop", shop);
                return responseMap;
            }

            if (shopMap.replace(shop.getShopName(), oldShop, shop)){
                Map responseMap = new HashMap();
                responseMap.put("message", "Shop address is Updated");
                Map newShop = new HashMap();
                newShop.put("shop", shop);
                //Surprisingly new HashMap().put("shop",shop) not working
                responseMap.put("CurrentAddress", newShop);
                Map prevShop = new HashMap();
                prevShop.put("shop", oldShop);
                responseMap.put("PreviousAddress",prevShop);
                return responseMap;
            }
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
