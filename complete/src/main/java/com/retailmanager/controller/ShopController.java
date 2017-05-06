package com.retailmanager.controller;

import com.retailmanager.dao.ShopDao;
import com.retailmanager.model.Customer;
import com.retailmanager.model.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by prashant on 5/2/17.
 */
@Controller
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    ShopDao shopDao;

    /**
     * Returns near by shops if resides in 10KM.
     * @param customer customer POJO class has latitude and longitude.
     * @return <p> List of near by shops with HttpStatus.FOUND status code, if shops are found within 10KM radius </p>
     * @return <p> Empty list with HttpStatus.NO_CONTENT status code, if shops are not found. </p>
     */
    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getAllNearByShops(@Valid Customer customer) {
        //Make getNearByShops Lazy!
        Supplier<List<Shop>> shops = () -> shopDao.getNearByShops(customer.getLatitude(),customer.getLongitude());
        if(shops.get().isEmpty()){
            return new ResponseEntity<Object>("No shop found", HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<Object>(shops.get(), HttpStatus.FOUND);
        }
    }

    /**
     * Returns shop is added message with shop information if new shop is added and
     * shop is updated message with shop information if shop is updated!
     * @param shop shop POJO class has all the shop attributes.
     * @return <p>Added new shop as message with shop information and HttpStatus.CREATED status code.</p>
     * @return <P>Updated Shop Address as message with shop information and HttpStatus.OK status code.</P>
     * @throws Exception if google map API could not able to find the latitude and longitude by address and postal code
     */
    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> addShops(@RequestBody Shop shop) throws Exception {
        Map<String,String> latLong = getLatLongFromAddress(shop.getShopAddress(),shop.getShopPostCode());
        shop.setLat(latLong.getOrDefault("latitude", "No Latitude Found"));
        shop.setLng(latLong.getOrDefault("longitude", "No Longitude Found"));
        if(shopDao.addShop(shop)){
            return new ResponseEntity<Object>("Added new shop: " + shop, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<Object>("Updated Shop Address: " + shop, HttpStatus.OK);
        }
    }

    /**
     * Returns latitude and longitude by using google map API
     * @param address shop address.
     * @param  postalCode the postal code of the address
     * @return <p>latitude and longitude returns by google map API</p>
     * @throws Exception if google map API could not able to find the
     * latitude and longitude by address and postal code
     */
    private Map<String,String> getLatLongFromAddress(String address, String postalCode) throws Exception {
        Map<String, String> latLong = new HashMap<>();
        String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address + " " + postalCode, "UTF-8") + "&sensor=true";
        URL url = new URL(api);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();
        if (httpConnection.getResponseCode() == 200) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(httpConnection.getInputStream());
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/GeocodeResponse/status");
            String status = (String) expr.evaluate(document, XPathConstants.STRING);
            if (status.equals("OK")) {
                expr = xpath.compile("//geometry/location/lat");
                latLong.put("latitude", expr.evaluate(document, XPathConstants.STRING).toString());
                expr = xpath.compile("//geometry/location/lng");
                latLong.put("longitude", expr.evaluate(document, XPathConstants.STRING).toString());
            }

        }
        return latLong;
    }
}
