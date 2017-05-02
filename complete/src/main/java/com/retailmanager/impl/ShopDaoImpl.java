package com.retailmanager.impl;

import com.retailmanager.dao.ShopDao;
import com.retailmanager.model.Shop;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by prashant on 5/2/17.
 */
@Component
public class ShopDaoImpl implements ShopDao{
    Map<String,Shop> shopMap = new ConcurrentHashMap<>();


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
    public List<Shop> getShops() {
        return new ArrayList<>(shopMap.values());
    }

}
