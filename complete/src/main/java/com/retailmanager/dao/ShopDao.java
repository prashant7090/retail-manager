package com.retailmanager.dao;

import com.retailmanager.model.Shop;

import java.util.List;

/**
 * Created by prashant on 5/2/17.
 */
public interface ShopDao {
    boolean addShop(Shop shop);
    List<Shop> getShops();
}
