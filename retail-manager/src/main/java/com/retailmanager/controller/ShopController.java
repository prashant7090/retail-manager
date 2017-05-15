package com.retailmanager.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.retailmanager.dao.ShopDao;
import com.retailmanager.model.Customer;
import com.retailmanager.model.Shop;
import com.retailmanager.util.GoogleApiUrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by prashant on 5/2/17.
 */
@Controller
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    ShopDao shopDao;

    @Autowired
    GoogleApiUrlBuilder googleApiUrlBuilder;

    @Value("${google.geocode.url}")
    private String googleGeoCodeUrl;

    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${default.latitude.message}")
    private String defaultLatitudeMessage;

    @Value("${default.longitude.message}")
    private String defaultLongitudeMessage;

    /**
     * Returns near by shops if resides in NEARBYDISTANCE.
     * @param customer customer POJO class has latitude and longitude.
     * @return <p> List of near by shops with HttpStatus.FOUND status code, if shops are found within 10KM radius </p>
     * <p> Empty list with HttpStatus.NO_CONTENT status code, if shops are not found. </p>
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
     * Return JSON object with parameter <b>message</b> as, New shop is Added and parameter <b>shop</b> as, shop information, if, new shop added.
     * return JSON object with parameter <b>message</b> as, Shop Address is updated and parameter <b>shop</b> as, shop information, if, shop is already present.
     * @param shop shop POJO class has all the shop attributes.
     * @return <p>return JSON object with parameter <b>message</b> as, New shop is Added and parameter <b>shop</b> as, shop information, with HttpStatus.CREATED status code.</p>
     * <P>return JSON object with parameter <b>message</b> as, Shop Address is updated and <b>shop</b> as, shop information, with HttpStatus.OK status code.</P>
     */
    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> addShops(@RequestBody Shop shop) {
        Map<String,String> latLong = new HashMap<>();
        try{
            latLong = getLatLongFromAddress(shop.getShopAddress(),shop.getShopPostCode());
        }catch (Exception ex){
            Logger.getLogger("ShopController.class").log(Level.SEVERE,"Exception occurred with Google API: " + ex + " while serving shop: " + shop);
        }
        shop.setLatitude(latLong.getOrDefault("latitude", defaultLatitudeMessage));
        shop.setLongitude(latLong.getOrDefault("longitude", defaultLongitudeMessage));
        Map responseMap = shopDao.addShop(shop);
        if(responseMap.containsKey("PreviousAddress")){
            return new ResponseEntity<Object>(responseMap, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
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
        String api = googleApiUrlBuilder.withApiUrl(googleGeoCodeUrl)
                .withAddress(address)
                .withPostalCode(postalCode)
                .withGoogleApiKey(googleApiKey)
                .build();
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
