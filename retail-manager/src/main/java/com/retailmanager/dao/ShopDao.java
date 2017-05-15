package com.retailmanager.dao;

import com.retailmanager.model.Shop;

import java.util.List;
import java.util.Map;

/**
 * Created by prashant on 5/2/17.
 */
public interface ShopDao {
    Map addShop(Shop shop);
    List<Shop> getNearByShops(Double latitude, Double longitude);
}
