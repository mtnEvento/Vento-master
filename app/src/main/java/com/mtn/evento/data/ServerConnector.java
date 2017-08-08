package com.mtn.evento.data;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

 public class ServerConnector{

    private String web_url;
    private String data = "";
    private Callback listener;

    private ServerConnector(String web_url){
        this.web_url = web_url;
    }

    @Contract("_ -> !null")
    public static ServerConnector newInstance(String web_url) {
        return new ServerConnector(web_url);
    }


    public ServerConnector setParameters( HashMap<String, String> parameters){
        int i = 1;
        for(Map.Entry<String, String> entry : parameters.entrySet()){

            try {
                data += URLEncoder.encode(entry.getKey(),"UTF-8")+"="+URLEncoder.encode(entry.getValue(),"UTF-8" );
                if (i < parameters.size())
                    data += "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        Log.d(LOGMESSAGE, "setParameters: " + data);
        return this;

    }

    public ServerConnector attachListener(Callback listener){
        this.listener = listener;
        return this;
    }

    public void connectToServer(){
        if(web_url != null){
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                   return connect();
                }

                @Override
                protected void onPostExecute(String result) {
                    if(listener != null){
                        listener.getResult(result);
                    }
                }
            }.execute();
        }
    }


    private String connect(){
        String result = "";
        try {

            URL url = new URL(web_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // configure the http for read and write
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //we connect to outputstream so we can write to web server
            OutputStream outputStream = httpURLConnection.getOutputStream();

            //Send data or write into the php file
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            //read response from the php file

            //connect to the input stream so we can read from server
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            // StringBuilder sb = new StringBuilder(result);
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public interface Callback{
        void getResult(String result);
    }


}
