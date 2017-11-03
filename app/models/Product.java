package models;

//import com.mongodb.management.;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Created by niharika on 28-Jun-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {


    public long id;
    public String name;
    public CurrentPrice current_price;

    public Product() {
    }

    public Product(long id, CurrentPrice current_price) {
        this.id = id;
        this.current_price = current_price;
    }

    public DBObject createDBObject() {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

        docBuilder.append("id", id);
        docBuilder.append("current_price",  current_price.createDBObject());

        return docBuilder.get();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Product{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", current_price=").append(current_price);
        sb.append('}');
        return sb.toString();
    }
}
