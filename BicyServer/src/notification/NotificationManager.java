package notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
  /*  public static void Senden(String zu,String Titel,String Nachricht) {
    	try{
	    	CloseableHttpClient httpclient = HttpClients.createDefault();
	    
	    	
	
	    	HttpPost httpPost = new HttpPost("http://steffchess.esy.es/fcm/FCM.php");
	    	List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    	nvps.add(new BasicNameValuePair("to", zu));
	    	nvps.add(new BasicNameValuePair("title", Titel));
	    	nvps.add(new BasicNameValuePair("content", Nachricht));
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
    */
		
}
