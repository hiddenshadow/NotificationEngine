package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.*;
import models.CurrentPrice;
import models.MailReq;
import models.Product;
import play.Logger;
import play.libs.Json;
import play.data.Form;

import play.mvc.Controller;
import play.mvc.Result;
import services.QueuingService;
import services.QueuingServiceImpl;
import utils.Constants;
import utils.ResponseObj;
//import views.html.index;



import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static utils.Constants.INTERNAL_ERROR;
import static utils.Constants.MULTIPLE_DB_ENTRIES;
import static utils.Constants.SUCCESS;

//import javax.inject.*;
//import play.api.Configuration

//class MyController  {
//        // ...
//        }

/**
 * Created by niharika on 29-Jun-17.
 */
public class MongoController  extends Controller {

    static Form<Product> employeeForm = Form.form(Product.class);

    static Form<MailReq> sendMailReqForm = Form.form(MailReq.class);

    public Result sendMail() {
        Form<MailReq> mailReqForm = sendMailReqForm.bindFromRequest();

        if (mailReqForm.hasErrors()) {
            return jsonResult(badRequest(mailReqForm.errorsAsJson()));
        }

        MailReq req = mailReqForm.get();

        Logger.debug(""+req.content+", "+req.mailTo+", req: "+req);

        try {
            QueuingServiceImpl.publishMessage("SampleMessage");
        } catch (Exception e) {
            e.printStackTrace();
            return jsonResult(badRequest());
        }

        return jsonResult(ok(ResponseObj.successObj(Json.newObject(), SUCCESS)));
    }

    public Result getProductsInPriceRange(Integer fromPrice , Integer toPrice ) {

        DBCollection col = getCollection(Constants.PRODUCTS_COLLECTION);

//        DBObject query = BasicDBObjectBuilder.start().add("id", id).get();

//        BasicDBObject query = new BasicDBObject("current_price.value",  new BasicDBObject("$gt", Double.parseDouble(fromPrice)).append("$lte", Double.parseDouble(toPrice)));
        BasicDBObject query = new BasicDBObject("current_price.value",  new BasicDBObject("$gt", fromPrice).append("$lte", toPrice));

        DBCursor cursor = col.find(query);

        ArrayList<DBObject> arr = new ArrayList<>();

        while(cursor.hasNext()){
            DBObject cur = cursor.next();
            System.out.println("cur - "+cur);
            arr.add(cur);
        }


        return jsonResult(ok(ResponseObj.successObj(Json.toJson(arr), SUCCESS)));
    }

    public Result getById(Integer id){
        JsonNode pJson;
        JsonNode allProductInfo ;
        try {
            String pinfo = getProductInfo(id.toString());
            pJson = play.libs.Json.parse(pinfo);

            DBCollection col = getCollection(Constants.PRODUCTS_COLLECTION);

            DBObject query = BasicDBObjectBuilder.start().add("id", id).get();
            DBCursor cursor = col.find(query);

            if(cursor.count() == 1){
                DBObject prodFromDb = cursor.next();
                Product prodObj = Json.fromJson(Json.toJson(prodFromDb), Product.class);
                getRequiredInfo(pJson, prodObj);
                allProductInfo = Json.toJson(prodObj);

            } else if(cursor.count() > 1){
                return jsonResult(badRequest( ResponseObj.badRequestObj(Json.newObject(), Constants.MULTIPLE_DB_ENTRIES)) );
            } else {
                return jsonResult(badRequest( ResponseObj.badRequestObj(Json.newObject(), Constants.NO_DB_ENTRY)) );
            }

        } catch (Exception e) {
            e.printStackTrace();
            return jsonResult(badRequest( ResponseObj.badRequestObj(Json.newObject(), Constants.INTERNAL_ERROR)));
        }

//        return jsonResult(created(allProductInfo));

        return jsonResult(created(ResponseObj.successObj(allProductInfo, SUCCESS)));
    }

    public Result updateById(Integer id){
        Form<Product> prod = employeeForm.bindFromRequest();
        if (prod.hasErrors()) {
            return jsonResult(badRequest(prod.errorsAsJson()));
        }

        Product reqProd = prod.get();
        DBCollection col = getCollection( Constants.PRODUCTS_COLLECTION);

        DBObject query = BasicDBObjectBuilder.start().add("id", id).get();
        DBCursor cursor = col.find(query);

        if(cursor.count() == 1){
            DBObject oldProdDbObj = cursor.next();
            Product prodObj = Json.fromJson(Json.toJson(oldProdDbObj), Product.class);

            prodObj.current_price = reqProd.current_price;

            DBObject updatedProdDBObj = prodObj.createDBObject();
            col.update(query, updatedProdDBObj);
            return jsonResult(created(Json.toJson(updatedProdDBObj)));
        } else {
            return jsonResult(badRequest());
        }
    }

    private static String getProductInfo(String id) throws Exception {
        String url = "https://api.target.com/products/v3/"+id+"?fields=descriptions&id_type=TCIN&key=43cJWpLjH8Z8oR18KdrZDBKAgLLQKJjz";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String result = response.toString();
        return result;
    }


    private static void getRequiredInfo(JsonNode productData, Product prod) {
        JsonNode product_composite_response = productData.get("product_composite_response");
        ArrayNode itemsArray = (ArrayNode) product_composite_response.get("items");
        JsonNode firstItem = itemsArray.get(0);
        String desc = firstItem.get("general_description").asText();
        prod.name = desc;
        System.out.println("productData - "+prod);
        return;
    }




    /********/
    /**** Util functions for DB Accessing ****/


    /*
* Adding indexes.
* Creating collections.*/
    public Result initDb() {
        Product p1 = new Product(13860428, new CurrentPrice(12.45, "USD"));
        DBCollection prodCol = getCollection(Constants.PRODUCTS_COLLECTION);
        prodCol.insert(p1.createDBObject());
        return ok();
    }

    private DBCollection getCollection(String colName) {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 ); // should use this always
        DB db = mongoClient.getDB("local");
        DBCollection col = db.getCollection(colName);
        return col;
    }


    public Result createProduct() {
        Form<Product> prod = employeeForm.bindFromRequest();
        if (prod.hasErrors()) {
            return jsonResult(badRequest(prod.errorsAsJson()));
        }

        Product reqProd = prod.get();
        DBObject prodDbObject = reqProd.createDBObject();
        DBCollection col = getCollection( Constants.PRODUCTS_COLLECTION);
        col.insert(prodDbObject);

        return jsonResult(created(Json.toJson(prod.get())));
    }

    public Result insertNew() {
        long time = System.currentTimeMillis();
        String name = "name"+time;
        Product newProd = new Product(time, new CurrentPrice(0d, "USD"));

        DBCollection col = getCollection( Constants.PRODUCTS_COLLECTION);
        DBObject doc = newProd.createDBObject();
        ObjectMapper mapperObj = new ObjectMapper();

        try {
            // get Employee object as a json string
            String jsonStr = mapperObj.writeValueAsString(newProd);
             JsonNode empJsonNode = Json.toJson(newProd);
            String stringify = Json.stringify(empJsonNode);

             JsonNode parsedJsonNode = Json.parse(stringify);
            Product gotBack = Json.fromJson(parsedJsonNode, Product.class);


            System.out.println("newProd - "+newProd);
            System.out.println("empJsonNode - "+empJsonNode);
            System.out.println("jsonStr - "+jsonStr);
            System.out.println("stringify - "+stringify);
            System.out.println("parsedJsonNode - "+parsedJsonNode);
            System.out.println("gotBack - "+gotBack);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonResult(created(Json.toJson(newProd)));
    }

    /**
     * Add the content-type json to response
     *
     * @param Result httpResponse
     *
     * @return Result
     */
    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=utf-8");
        return httpResponse;
    }


/*
    public Result getAll() {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 ); // should use this always
        DB db = mongoClient.getDB("local");
        DBCollection col = db.getCollection( Constants.PRODUCTS_COLLECTION);

        DBCursor cursor = col.find();
        int i = 1;
        ArrayList<Product> prds = new ArrayList<>();

        while (cursor.hasNext()) {
            System.out.println("Inserted Document: "+i);
            DBObject cur = cursor.next();

            System.out.println();

            i++;
        }

        return ok(index.render("API REST for JAVA Play Framework"));
    }
*/

}
