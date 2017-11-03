package models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MailReq {

    public String mailTo;
    public String subject;
    public String orderId;
    public String mailType;
    public String sender;
    public String content;

    public MailReq() {
    }




    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MailReq{");
        sb.append("mailTo='").append(mailTo).append('\'');
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", orderId='").append(orderId).append('\'');
        sb.append(", mailType='").append(mailType).append('\'');
        sb.append(", sender='").append(sender).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
