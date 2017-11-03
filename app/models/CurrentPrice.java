package models;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Created by niharika on 29-Jun-17.
 */
public class CurrentPrice {
    public double value;
    public String currency_code;

    public CurrentPrice() {
    }

    public CurrentPrice(double value, String currency_code) {
        this.value = value;
        this.currency_code = currency_code;
    }

    public DBObject createDBObject() {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();
        docBuilder.append("value", value);
        docBuilder.append("currency_code", currency_code);
        return docBuilder.get();
    }
}
