package com.tapandangarwala.upnphourlypricingapiwemo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.cybergarage.http.HTTPResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.xml.*;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MainActivity extends Activity  {

    private EditText devicesDisp = null;
    double hourlyPriceLimit = 2.0;
    public double hourlyPrice;
    private EditText hourlyPriceDisp = null;
    double parseDouble;
    public String price;
    public int ON = 1;
    public int OFF = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button onButton = (Button)findViewById(R.id.button);
        Button offButton = (Button)findViewById(R.id.button2);
        devicesDisp = (EditText)findViewById(R.id.textView2);
        hourlyPriceDisp = (EditText) findViewById(R.id.textView4);
        TextView device = (TextView) findViewById(R.id.textView);
        TextView priceDisp = (TextView) findViewById(R.id.textView3);

        new TextTask().execute("https://hourlypricing.comed.com/api?type=currenthouraverage");

       //AsyncTask<Void, Void, Integer> getStatus = new MyGetStatusClass().execute();

       /* if(hourlyPriceLimit <= hourlyPrice){
            //turn WeMo on
            try {
                if(getStatus.equals(1)){
                    new MySetSwitchONClass().execute();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        else{
            //turn WeMo off
                new MySetSwitchOFFClass().execute();
        }*/
    }

    public class TextTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(in));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                String webInfo = buffer.toString();
                JSONArray parentArray = new JSONArray(webInfo);
                String webPrice = "";

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject parentObject = parentArray.getJSONObject(i);
                    webPrice = parentObject.getString("price");
                }
                price = webPrice;
                return price;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            finally{
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                try{
                    if(reader != null) {
                        reader.close();
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(price);
            //hourlyPriceDisp.setText(price);
            parseDouble = Double.parseDouble(price);
            hourlyPrice = parseDouble;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void turnON(View view)  {
       new MySetSwitchONClass().execute();
    }

    public void turnOFF(View view) {
        try {
            new MySetSwitchOFFClass().execute();
        }
        catch(Throwable e){
            e.printStackTrace();
        }
    }


    protected void onDestroy() {
        super.onDestroy();

    }

   /* class MyGetStatusClass extends AsyncTask <Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... Params) {
            String IPADDR = "192.168.1.12";
            String PORT = "49153";
            int stat = -1;
            String xmlResp = "";

            OutputStream out = null;
            int respCode = -1;
            boolean isSuccess = false;
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                String strSOAPxml = getReqDataSTATUS();

                HttpPost httpPost = new HttpPost("http://" + IPADDR + ":" + PORT + "/upnp/control/basicevent1");

                String SoapAction = "http://" + IPADDR + ":" + PORT + "/upnp/control/basicevent1";

                StringEntity entity = new StringEntity(strSOAPxml, "UTF-8");
                entity.setChunked(true);

                RequestWrapper requestWrapper=new RequestWrapper(httpPost);
                httpPost.setEntity(entity);
                httpPost.setHeader("Header", "SOAPACTION:\"urn:Belkin:service:basicevent:1#SetBinaryState\"");
                httpPost.setHeader("Accept", "text/xml");
                httpPost.setHeader("SOAPAction", SoapAction);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity NewEntity = response.getEntity();

                String strResponse = null;
                if (NewEntity != null) {
                    strResponse = EntityUtils.toString(NewEntity);
                    System.out.println(strResponse);
                    xmlResp = strResponse;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                    //read xml into a string before parsing
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(xmlResp));
                    Document doc = db.parse(is);
                    NodeList nodes = doc.getElementsByTagName("BinaryState");
                    Element title = (Element) nodes.item(0);
                    stat = Integer.parseInt(title.getTextContent());
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            return stat;
        }
    }*/

    class MySetSwitchOFFClass extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... params) {
        String IPADDR = "192.168.1.12";
        String PORT = "49153";
        String TARGETSTATUS = "0";
        OutputStream out = null;
        int respCode = -1;
        boolean isSuccess = false;
        URL url = null;
        HttpURLConnection httpURLConnection = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            String strSOAPxml = getReqDataOFF();

            HttpPost httpPost = new HttpPost("http://" + IPADDR + ":" + PORT + "/upnp/control/basicevent1");
            String SoapAction = "urn:Belkin:service:basicevent:1#SetBinaryState\"";
            StringEntity entity = new StringEntity(strSOAPxml, "UTF-8");
            entity.setChunked(true);

            RequestWrapper requestWrapper = new RequestWrapper(httpPost);
            httpPost.setEntity(entity);

            httpPost.setHeader("SoapAction", "SOAPACTION:\"urn:Belkin:service:basicevent:1#SetBinaryState\"");
            httpPost.setHeader("Accept", "text/xml");
            //httpPost.setHeader("SOAPAction", SoapAction);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity NewEntity = response.getEntity();

            String strResponse = null;
            if (NewEntity != null) {
                strResponse = EntityUtils.toString(NewEntity);
                System.out.println(strResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    }


    class MySetSwitchONClass extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            String IPADDR = "192.168.1.12";
            String PORT = "49153";
            String TARGETSTATUS = "1";
            OutputStream out = null;
            int respCode = -1;
            boolean isSuccess = false;
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                String strSOAPxml = getReqDataON();

                HttpPost httpPost = new HttpPost("http://" + IPADDR + ":" + PORT + "/upnp/control/basicevent1");
                String SoapAction = "urn:Belkin:service:basicevent:1#SetBinaryState\"";
                StringEntity entity = new StringEntity(strSOAPxml, "UTF-8");
                entity.setChunked(true);
                RequestWrapper requestWrapper=new RequestWrapper(httpPost);
                httpPost.setEntity(entity);
                httpPost.setHeader("SoapAction", "SOAPACTION:\"urn:Belkin:service:basicevent:1#SetBinaryState\"");
                httpPost.setHeader("Accept", "text/xml");
                httpPost.setHeader("SOAPAction", SoapAction);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity NewEntity = response.getEntity();

                String strResponse = null;
                if (NewEntity != null) {
                    strResponse = EntityUtils.toString(NewEntity);
                    System.out.println(strResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static String getReqDataON() {
        StringBuilder requestData = new StringBuilder();
        requestData
                .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                        + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
                        + "<s:Body>"
                        + "<u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\">"
                        + "<BinaryState>1</BinaryState>"
                        + "</u:SetBinaryState>"
                        + "</s:Body>"
                        + "</s:Envelope>");

        return requestData.toString().trim();
    }

    public static String getReqDataOFF() {
        StringBuilder requestData = new StringBuilder();

        requestData
        .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
                + "<s:Body>"
                + "<u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\">"
                + "<BinaryState>0</BinaryState>"
                + "</u:SetBinaryState>"
                + "</s:Body>"
                + "</s:Envelope>");

        return requestData.toString().trim();
    }

    public static String getReqDataSTATUS() {
        StringBuilder requestData = new StringBuilder();

        requestData
                .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                        + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
                        + "<s:Body>"
                        + "<u:GetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\">"
                        + "<BinaryState>1</BinaryState>"
                        + "</u:GetBinaryState>"
                        + "</s:Body>"
                        + "</s:Envelope>");

        return requestData.toString().trim();
    }

    private static String convertStreamToString(InputStream is)
            throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }
}
























