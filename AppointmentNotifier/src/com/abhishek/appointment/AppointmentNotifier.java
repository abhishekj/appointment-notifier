package com.abhishek.appointment;


/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Abhishek jain
 */
public class AppointmentNotifier {

	  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	  /** Global instance of the JSON factory. */
	  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	  private static com.google.api.services.calendar.Calendar client;

  public static void main(String[] args) {
	    Connection conn = null;
	    String url = "jdbc:mysql://localhost:3306/";
	    String dbName = "";
	    String driver = "com.mysql.jdbc.Driver";
	    String dbUserName = ""; 
	    String dbPassword = "";
	    String clientId = null;
	    String clientSecret = null;
	    String smsUser = null;
	    String smsPassword = null;
	    Properties prop = new Properties();
	    
    	try {
               //load a properties file
    		prop.load(new FileInputStream("Application.properties"));
 
               //get the property value and print it out
    			dbName = prop.getProperty("dbName");
    			dbUserName = prop.getProperty("dbUserName");
    			dbPassword = prop.getProperty("dbPassword");
    			clientId=prop.getProperty("clientId");
    			clientSecret = prop.getProperty("clientSecret");
    			smsUser= prop.getProperty("smsUser");
    			smsPassword= prop.getProperty("smsPassword");
    			
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	    
	    try {
	    Class.forName(driver).newInstance();
	    conn = DriverManager.getConnection(url+dbName,dbUserName,dbPassword);
	    System.out.println("Connected to the database");
	    
	    Statement st = conn.createStatement();
	    ResultSet res = st.executeQuery("SELECT `d_id`,`d_number`,`d_calendar_id`,`d_refresh_token`,`d_message` FROM `doctors` WHERE `d_enabled`=1");
	    
	    int dId ;
	    String dNumber;
	    String calendarId;
	    String refreshToken;
	    String message;
	    
	    while(res.next()){
	    	dId = res.getInt("d_id");
	    	dNumber= res.getString("d_number");
	    	calendarId = res.getString("d_calendar_id");
	    	refreshToken= res.getString("d_refresh_token");
	    	message= res.getString("d_message");
	    	String accessToken="";
	        GoogleAccessProtectedResource requestInitializer =
	            new GoogleAccessProtectedResource(accessToken, HTTP_TRANSPORT, JSON_FACTORY, clientId,
	                clientSecret, refreshToken);

	        client =
	            com.google.api.services.calendar.Calendar.builder(HTTP_TRANSPORT, JSON_FACTORY)
	                .setApplicationName("Google-CalendarSample/1.0")
	                .setHttpRequestInitializer(requestInitializer).build();
	        	try{
	        		try{
	        	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+05:30'");
	        	        DateFormat dateFormatPrint = new SimpleDateFormat("dd-MM-yy h:mm a");
	        	        java.util.Calendar cal = java.util.Calendar.getInstance();
	        	        java.util.Calendar cal2 = java.util.Calendar.getInstance();
	        	        
	        	        cal = convertToIst(cal);
	        	        cal2 = convertToIst(cal2);
//	        	        
	        	        int hour = cal.getTime().getHours();
//	        	        hour=0;
	        	        System.out.println("Hour:" +hour);
	        	        
	        	        cal2.add(java.util.Calendar.HOUR, 24-hour);
	        	        System.out.println(dateFormat.format(cal.getTime()));
	        	        System.out.println(dateFormat.format(cal2.getTime()));
	        	        Events events =
	        	            client.events().list(calendarId).setTimeMin(dateFormat.format(cal.getTime()))
	        	                .setTimeMax(dateFormat.format(cal2.getTime())).execute();

	        	        if (events.toString().contains("\"items\"")) {
	        	        	try{
	        	        		
	        	        	
	        	          while (true) {
//	        	        	  System.out.println("Events"+events);
	        	            for (Event event : events.getItems()) {
	        	            	String eventMessage = message;
	        	              String location = event.getSummary();
	        	              if (location != null) {
	        	                String name = location.replaceAll("\\d+", "");
	        	                name = name.replaceAll(",", "");


	        	                String number = location.replaceAll("\\D+", "").replaceAll("^0", "");
	        	                if (number.length() == 10) {
	        	                  number = "91" + number;
	        	                }
	        	                EventDateTime time = event.getStart();
	        	                // ;
	        	                java.util.Calendar caltime = java.util.Calendar.getInstance();
	        	                caltime.setTimeInMillis(time.getDateTime().getValue());
	        	                caltime = convertToIst(caltime);

	        	                time.getDateTime().getValue();
	        	                
	        	                eventMessage = message.replaceAll("__patient__", name).replaceAll("__time__", dateFormatPrint.format(caltime.getTime())).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	        	                
	        	                
	        	                try{
	        	                	
//	        	                	String strUrl = "http://www.businesssms.co.in/SMS.aspx";
//	        	                	strUrl =strUrl+"?ID=" + URLEncoder.encode(smsUser) + "&Pwd=" + smsPassword+"&PhNo=" + number + "&Text=" + ""+ URLEncoder.encode(message)	+"";
	        	                		
	        	                	String strUrl = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp";
	        	                	strUrl+="?username=xxxxxxx&password=xxxxxx&sendername=DOCTOR&mobileno="+number+"&message="+URLEncoder.encode(eventMessage);
//	        	                	strUrl="http://localhost:9000"; // comment this
	        	                	
	        	                URL smsUrl = new URL(strUrl.trim());
	        	                BufferedReader in = new BufferedReader(
	        	                        new InputStreamReader(smsUrl.openStream()));

	        	                        String inputLines = null,inputLine;
	        	                        while ((inputLine = in.readLine()) != null)
	        	                            inputLines+=inputLine;
	        	                        in.close();
//	        	            	InputStream in = smsUrl.openStream();
	        	            	
	        	            	PreparedStatement pSMS = conn.prepareStatement("INSERT INTO `sms_request` (sr_to,`sr_doctor_id`,`sr_message`,sr_response) VALUES(?,?,?,?)");
	        	            	pSMS.setString(1, number);
	        	            	pSMS.setInt(2, dId);
	        	            	pSMS.setString(3, eventMessage);
	        	            	pSMS.setString(4, inputLines);
	        	            	try{
	        	            		pSMS.executeUpdate();
	        	            	}catch (Exception e) {
									// TODO: handle exception
	        	            		System.out.println("Cannt insert into mysql");
	        	            		e.printStackTrace();
								}
	        	                }catch(Exception e){
	        	                	System.out.println("Cannt connect to sms url");
	        	                	e.printStackTrace();
	        	                }
	        	                
//	        	                System.out.println("Dear " + name + ", You have an appointment at "
//	        	                    + dateFormatPrint.format(caltime.getTime()) + ". your number is " + number);
	        	              }
	        	            }
	        	            String pageToken = events.getNextPageToken();
	        	            if (pageToken != null && !pageToken.isEmpty()) {
	        	              events = client.events().list(calendarId).setPageToken(pageToken).execute();
	        	            } else {
	        	              break;
	        	            }
	        	          }
	        	        	}catch (NullPointerException e) {
								// TODO: handle exception
	        	        		System.out.println("Doctor level exception");
	        	        		e.printStackTrace();
	        	        		
							}
	        	        } else {
	        	          System.out.println("No event in next 24 hours");
	        	        }
	        	        break;
	        	      } catch (IOException e) {
	        	        System.err.println(e.getMessage());
	        	      }
	        	    } catch (Throwable t) {
	        	      t.printStackTrace();
	        	    }
	    	
	    }
	    conn.close();
	    System.out.println("Disconnected from database");
	    } catch (Exception e) {
	    e.printStackTrace();
	    }
	    
	    System.exit(1);
	    

	    
	    }
	  
	  
  

  public static Calendar convertToIst(Calendar calendar) {

//	  Calendar calendar = Calendar.getInstance();
      TimeZone fromTimeZone = calendar.getTimeZone();
      TimeZone toTimeZone = TimeZone.getTimeZone("IST");

      calendar.setTimeZone(fromTimeZone);
      calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
      if (fromTimeZone.inDaylightTime(calendar.getTime())) {
          calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
      }

      calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
      if (toTimeZone.inDaylightTime(calendar.getTime())) {
          calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
      }

	  
	  
      return calendar;
}
  
	  
}



