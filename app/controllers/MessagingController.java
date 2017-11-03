package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.*;
import models.CurrentPrice;
import models.MailReq;
import models.Product;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.QueuingServiceImpl;
import utils.Constants;
import utils.ResponseObj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static utils.Constants.SUCCESS;

//import views.html.index;

//import javax.inject.*;
//import play.api.Configuration

//class MyController  {
//        // ...
//        }

/**
 * Created by niharika on 29-Jun-17.
 */
public class MessagingController extends Controller {

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
}
