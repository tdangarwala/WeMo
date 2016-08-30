# WeMo


public void execSwitch() {
		//String IPADDR = "localhost";
		//String PORT = "8080";
		
		String IPADDR = "192.168.1.12";
		String PORT = "49153";
	
		try {
			
			
			HttpClient httpclient = new DefaultHttpClient();
			String strSOAPxml = getReqDataOFF();

			HttpPost httpPost = new HttpPost("http://" + IPADDR + ":" + PORT + "/upnp/control/basicevent1");
			
			StringEntity entity = new StringEntity(strSOAPxml, "UTF-8");
			//entity.setChunked(true);
			httpPost.setEntity(entity);
			httpPost.setHeader("SoapAction", "SOAPACTION:\"urn:Belkin:service:basicevent:1#SetBinaryState\"");
			httpPost.setHeader("Accept", "text/xml");
			
			HttpResponse response = httpclient.execute(httpPost);
			
			HttpEntity NewEntity = response.getEntity();

			String strResponse = null;
			if (NewEntity != null) {
				strResponse = EntityUtils.toString(NewEntity);
				System.out.println(strResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		public static String getReqDataOFF() {
		    StringBuilder requestData = new StringBuilder();

		  
		    requestData.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Belkin:service:basicevent:1\">"
		            + "<soapenv:Body>"
		            + "<urn:SetBinaryState>"
		            + "<BinaryState>0</BinaryState>"
		            + "</urn:SetBinaryState>"
		            + "</soapenv:Body>"
		            + "</soapenv:Envelope>");

		    return requestData.toString().trim();
		}
		
		public static void main (String args[]){
			
			ActivityClient ac = new ActivityClient();
			ac.execSwitch();
			
		}
		

	}

