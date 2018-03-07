package com.indra.sofia2.ssapandroid.kp.implementations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by mbriceno on 11/01/2018.
 */

public class KpRESTCommands {

    protected URL url = null;
    protected String token;
    protected String kpInstance;
    public String sessionKey;

    public KpRESTCommands(String url, String token, String kpInstance) throws MalformedURLException {
        this.url = new URL(url);
        this.token = token;
        this.kpInstance = kpInstance;

    }

    /**
     * SSAP JOIN MESSAGE
     *
     * @return responseCode from HTTP POST operation (OK=200)
     * @throws IOException
     * @throws JSONException
     */
    public int join() throws IOException, JSONException {
        int responseCode = -1;
        HttpURLConnection connection = post();
        DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
        dStream.writeBytes("{\"join\": true,\"instanceKP\": \"" + kpInstance + "\",\"token\": \"" + token + "\"}");
        dStream.flush();
        dStream.close();
        responseCode = connection.getResponseCode();
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            final StringBuilder output = new StringBuilder("Request URL " + url);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();
            connection.disconnect();

            JSONObject jsonJOIN = new JSONObject(responseOutput.toString());
            sessionKey = jsonJOIN.getString("sessionKey");
        }

        connection.disconnect();
        return responseCode;
    }

    /**
     * SSAP INSERT MESSAGE
     *
     * @param ontology
     * @param message
     * @return String with the insert response and the ObjectId returned from MongoDB, empty string otherwise
     * @throws IOException
     * @throws JSONException
     */
    public String insert(String ontology, String message) throws IOException, JSONException {
        String msg = "";
        String line = "";

        HttpURLConnection connection = post();

        DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
        String insertFrame = "{\"sessionKey\":\"" + sessionKey + "\", \"ontology\":\"" + ontology + "\", \"data\":\"" + message.replaceAll("\\\"","\\\\\"") + "\"}";
        dStream.writeBytes(insertFrame);
        dStream.flush();
        dStream.close();

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            DataInputStream iStream = new DataInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            while ((line = br.readLine()) != null) {
                msg = msg + line;
            }
        }
        return msg;
    }

    /**
     * SSAP LEAVE MESSAGE
     * @return HTTP return code, 200 if OK
     */
    public int leave() throws IOException {
        int responseCode = -1;
        HttpURLConnection connection = post();
        DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
        dStream.writeBytes("{\"leave\": true,\"sessionKey\": \"" + sessionKey + "\"}");
        dStream.flush();
        dStream.close();
        responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode;
    }

    /**
     * SSAP QUERY MESSAGE
     *
     * @param nativeQuery
     * @param ontology
     * @return String with the query output, empty otherwise
     * @throws IOException
     */
    public String query(String nativeQuery, String ontology) throws IOException{
        String msg = "";
        String line = "";
        URL url = null;

        url = new URL("http://sofia2.com/sib/services/ssap/v01/SSAPResource/?$sessionKey="
                    +sessionKey+"&$ontology="+ontology+"&$query="+nativeQuery+"&$queryType=NATIVE");

        HttpURLConnection connection = get(url);

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            DataInputStream is = new DataInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                msg = msg + line;
            }
        }
        connection.disconnect();
        return msg;

    }

    private HttpURLConnection get(URL url){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setAllowUserInteraction(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            return connection;
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection post(){
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setAllowUserInteraction(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            return connection;
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
