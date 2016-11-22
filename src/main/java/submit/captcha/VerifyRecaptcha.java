package submit.captcha;

// $Id: VerifyRecaptcha.java,v 1.12 2016-10-21 09:38:11-04 ericholp Exp $

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VerifyRecaptcha {
    private final static Logger LOGGER = Logger.getLogger(VerifyRecaptcha.class.getCanonicalName());

    public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    
    private final static String USER_AGENT = "Mozilla/5.0";

    public static boolean verify(String gRecaptchaResponse, String privatekey) {
	if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
	    LOGGER.log(Level.SEVERE, "gRecaptchaResponse null or blank");
	    return false;
	}
	
	try{
	    URL obj = new URL(url);
	    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	    
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    
	    String postParams = "secret=" + privatekey + "&response=" + gRecaptchaResponse;
	    
	    con.setDoOutput(true);
	    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	    wr.writeBytes(postParams);
	    wr.flush();
	    wr.close();
	    
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    StringBuffer response = new StringBuffer();
	    
	    String inputLine;
	    while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	    }
	    in.close();
	    
	    JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
	    JsonObject jsonObject = jsonReader.readObject();
	    jsonReader.close();
	    
	    return jsonObject.getBoolean("success");
	}catch(Exception ex){
	    LOGGER.log(Level.SEVERE, null, ex);
	    return false;
	}
    }
}
