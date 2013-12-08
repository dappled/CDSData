package bloomSuck;

import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;

import dataWrapper.SingleName;

/**
 * NOT USED I DONT HAVE MONEY TO BUY BLOOMBERG TERMINAL SOMEONE PLEASE GIVE ME MONEY!
 * @author Zhenghong Dong
 */
public class HistoricalDataGrabber {

	    public static SingleName getData(String name, String startDate, String endDate) throws Exception{
	        String serverHost = "localhost";
	        int serverPort = 8194;

	        SessionOptions sessionOptions = new SessionOptions();
	        sessionOptions.setServerHost(serverHost);
	        sessionOptions.setServerPort(serverPort);

	        System.out.println("Connecting to " + serverHost + ":" + serverPort);
	        Session session = new Session(sessionOptions);
	        if (!session.start()) {
	            System.err.println("Failed to start session.");
	            return null;
	        }
	        if (!session.openService("//blp/refdata")) {
	            System.err.println("Failed to open //blp/refdata");
	            return null;
	        }
	        Service refDataService = session.getService("//blp/refdata");
	        Request request = refDataService.createRequest("HistoricalDataRequest");

	        Element securities = request.getElement("securities");
	        securities.appendValue("name");
	        
	        Element fields = request.getElement("fields");
	        fields.appendValue("PX_LAST");
	        
	        request.set("periodicityAdjustment", "ACTUAL");
	        request.set("periodicitySelection", "WEEKLY");
	        request.set("startDate", startDate);
	        request.set("endDate", endDate);
	        request.set("maxDataPoints", 300000);
	        request.set("returnEids", true);

	        System.out.println("Sending Request: " + request);
	        session.sendRequest(request, null);

	        while (true) {
	            Event event = session.nextEvent();
	            MessageIterator msgIter = event.messageIterator();
	            while (msgIter.hasNext()) {
	                Message msg = msgIter.next();
	                System.out.println(msg);
	            }
	            if (event.eventType() == Event.EventType.RESPONSE) {
	                break;
	            }
	        }
	        return null;
	    }
	    
	    public static void main(String[] args) throws Exception {
			getData("ACE", "20080101", "20130101");
		}
	}


