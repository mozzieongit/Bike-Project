package zeug;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class NotificationManager {
	
	public static void main(String[] args){
		//Senden("dC8JTAhW-4k:APA91bFif49Xy0q56u-7C3czFwAV0ZegWdFnoiEDbLOu_lphLMptgpg9VdyizMuZbnh1QW8NV9BEmHzZUZI-hBnFQzNPWXZOqm3y2Aj5X6R7JpdYkkiSE5GnmOhXI39kQ6QW7M1_JCqF","Test","nachricht");
		Senden("epFf6uybkxE:APA91bHH1Evr_s5FdZFBEf1nzFRjEgVS6ucH_tuhf0im46iTbBr5KNI7ppunvQmWU8stmrIPkcOIFVJkNMGQX_p5L_MEXzRmGiQKHowhDGW7Qa5r06S3UlDj0zGUGJCr-MPWJPyyE5R_","test","nachricht");
		System.out.println("Gesendet!");
	}
	
    public static void Senden(String zu,String Titel,String Nachricht) {
    	try{
	    	CloseableHttpClient httpclient = HttpClients.createDefault();
	
	    	HttpPost httpPost = new HttpPost("http://llololl904.000webhostapp.com/Bike-Project/bisafee_fcm.php");
	    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    	nvps.add(new BasicNameValuePair("to", zu));
	    	nvps.add(new BasicNameValuePair("title", Titel));
	    	nvps.add(new BasicNameValuePair("content", Nachricht));
	    	nvps.add(new BasicNameValuePair("time", String.valueOf(new Date().getTime())));
	    	httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	    	CloseableHttpResponse response2 = httpclient.execute(httpPost);
	
	    	try {
	    	    //Erfolg?
	    		System.out.println(response2.getStatusLine());
	    	    
	    	    HttpEntity entity2 = response2.getEntity();

	    	    EntityUtils.consume(entity2);
	    	} finally {
	    	    response2.close();
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
		
}
