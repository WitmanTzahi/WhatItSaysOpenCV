package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.Defendant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by witman on 21/07/2017.
 */

public abstract class BaseHTTP {
    public boolean 						errorOccurred = false;
    public boolean 						didAbort = false;
    public boolean 						result = false;
    protected boolean 				    openFailed = false;
    protected RequestQueue              mQueue;
    protected AtomicBoolean isDone = new AtomicBoolean(false);
    protected String REQUEST_TAG;
    protected String TAG;
    private static final String theURL = "https://www.comax.co.il/Max2000MobilePC/HanitaNew/";

    public void baseAbort() {
        try {
            errorOccurred = true;
            doAbort();
        }
        catch (Exception e) {}
    }

    protected void doWait() {
        try {
            while(!isDone.get()) {
                try {
                    Thread.sleep(100);}catch (InterruptedException ie) {}
            }
        }
        catch(Exception e) {
            try {
                doAbort();
                result = false;
                errorOccurred = true;
            }
            catch(Exception ee) {}
        }
    }

    public void errorResponse(VolleyError error) {
        errorOccurred = true;
        isDone.set(true);
        result = false;
        try {mQueue.stop();}catch (Exception e) {}
        try {
            Log.i(TAG, "Date: "+ new Date() + " " + error.getMessage());}
        catch (Exception e) {
            Log.i(TAG, "Date: "+ new Date() + " null");
        }
        catch (Error e) {
            Log.i(TAG, "Date: "+ new Date() + " null");
        }
    }

    protected void doAbort() {
        try {
            if (!isDone.get())
                errorOccurred = true;
            didAbort = true;
            if (mQueue != null) {
                try {
                    mQueue.cancelAll(REQUEST_TAG);
                } catch (Exception e) {
                }
                try {
                    mQueue.stop();
                } catch (Exception e) {
                }
            }
            isDone.set(true);
        } catch (Exception e) {
        }
    }

    protected abstract void parseResponse(String response);

    protected void doRun(String command) {
        doRun(command, 30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
    }

    protected void doRun(String command, int initialTimeoutMs, int maxNumRetries) {
        try {
            openCommunication();
            String url;
            if(command.indexOf("https://") != -1 || command.indexOf("http://") != -1)
                url = command;
            else
                url = theURL+command;
            Log.i(TAG, url);
// Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                          Log.i(TAG, response);
                            Log.i(TAG, "Parse:" + new Date());
                            parseResponse(response);
                            try {mQueue.stop();} catch (Exception e) {}
                            Log.i(TAG, "End:" + new Date());
                            isDone.set(true);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            openFailed = true;
                            errorResponse(error);
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", " license/plain");
                    params.put("Accept-Charset", "Cp1255");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    initialTimeoutMs,
                    maxNumRetries,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            mQueue.add(stringRequest);
        }
        catch (Exception e) {
            errorOccurred = true;
            isDone.set(true);
            result = false;
            try {mQueue.stop();}catch (Exception ee) {}
            Log.i(TAG, e.getMessage());
        }
    }

    protected void openCommunication() {
        Log.i(TAG, "run()"+new Date());
        Cache cache = new DiskBasedCache(UCApp.gContext.getCacheDir(), 1024 * 1024); // Instantiate the cache
        Network network = new BasicNetwork(new HurlStack());// Set up the network to use HttpURLConnection as the HTTP client.
        mQueue = new RequestQueue(cache, network);// Instantiate the RequestQueue with the cache and network.
        mQueue.start();// Start the queue
    }

    public String ToHebrew(String tmp) {
//      tmp = PrepareTextForMobile(tmp);
        String ret = "";
        for(int i = 0;i < tmp.length();i++) {
            if(tmp.charAt(i) >= 224 && tmp.charAt(i) <= 250) {
                ret += (char)('א'+tmp.charAt(i)-224);
            }
            else {
                ret += tmp.charAt(i);
            }
        }
        return ret;
    }

    public String GetField(String field) {
        String ret = "";
        for (int i = 0; i + 1 < field.length() && !(field.charAt(i) == '<' && field.charAt(i + 1) == '/'); i++)
            ret += field.charAt(i);
        return ret;
    }

    public String PrepareTextForMobile(String text) {
        String mobileText = text;
        for (int i = 0; i < mobileText.length(); i++) {
			/**/if (mobileText.substring(i).startsWith("<![CDATA[")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + mobileText.substring(i + ("<![CDATA[").length());
                else
                    mobileText = mobileText.substring(i + ("<![CDATA[").length());
                i--;
            } else if (mobileText.substring(i).startsWith("]]>")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + mobileText.substring(i + ("]]>").length());
                else
                    mobileText = mobileText.substring(i + ("]]>").length());
                i--;
            } else if (mobileText.substring(i).startsWith("&lt;")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + '<' + mobileText.substring(i + ("&lt;").length());
                else
                    mobileText = '<' + mobileText.substring(i + ("&lt;").length());
                i--;
            } else if (mobileText.substring(i).startsWith("&gt;")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + '>' + mobileText.substring(i + ("&gt;").length());
                else
                    mobileText = '>' + mobileText.substring(i + ("&gt;").length());
                i--;
            } else if (mobileText.substring(i).startsWith("&amp;")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + '&' + mobileText.substring(i + ("&amp;").length());
                else
                    mobileText = '&' + mobileText.substring(i
                            + ("&amp;").length());
                i--;
            } else if (mobileText.substring(i).startsWith("&apos;")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + '\'' + mobileText.substring(i + ("&apos;").length());
                else
                    mobileText = '\'' + mobileText.substring(i + ("&apos;").length());
                i--;
            } else if (mobileText.substring(i).startsWith("&quot;")) {
                if (i > 0)
                    mobileText = mobileText.substring(0, i) + '"' + mobileText.substring(i + ("&quot;").length());
                else
                    mobileText = '"' + mobileText.substring(i + ("&quot;").length());
                i--;
            }
        }
        mobileText = RemoveExtraBlanks(mobileText);
        for (int i = 0; i < mobileText.length(); i++) {
            if ((mobileText.charAt(i) >= '0' && mobileText.charAt(i) <= '9')
                    || (mobileText.charAt(i) >= 'a' && mobileText.charAt(i) <= 'z')
                    || (mobileText.charAt(i) >= 'A' && mobileText.charAt(i) <= 'Z')) {
                int j;
                for (j = i + 1; j < mobileText.length()
                        && (mobileText.charAt(j) >= '0'
                        && (mobileText.charAt(j) <= '9')
                        || (mobileText.charAt(j) == '.'
                        || mobileText.charAt(j) == ',' ||
                        // mobileText.charAt(j) == '-' ||
                        // mobileText.charAt(j) == ':' ||
                        mobileText.charAt(j) == ':' || mobileText
                        .charAt(j) == '/')
                        || (mobileText.charAt(j) >= 'a' && mobileText
                        .charAt(j) <= 'z') || (mobileText
                        .charAt(j) >= 'A' && mobileText.charAt(j) <= 'Z')); j++)
                    ;
                String reverse = "";
                for (int k = j - 1; k >= i; k--)
                    reverse += mobileText.charAt(k);
                mobileText = mobileText.substring(0, i) + reverse
                        + mobileText.substring(j, mobileText.length());
                i = j - 1;
            }
        }
        // frame.AddLine("PrepareTextForMobile:Out::"+mobileText);
        mobileText = mobileText.replace('\t', ' ');
        mobileText = mobileText.replace('(', '\t');
        mobileText = mobileText.replace(')', '(');
        mobileText = mobileText.replace('\t', ')');
        mobileText = mobileText.replace('[', '\t');
        mobileText = mobileText.replace(']', '[');
        mobileText = mobileText.replace('\t', ']');
        return mobileText;
    }

    public String RemoveExtraBlanks(String text) {
        String mobileText = "";
        for (int i = 0; i < text.length(); i++) {
            if (i < text.length() - 1
                    && (text.charAt(i) == ' ' || text.charAt(i) == ',')) {
                if (text.charAt(i + 1) == ','
                        || text.charAt(i + 1) == ' '
                        || text.charAt(i + 1) == '.'
                        ||
                        // license.charAt(i+1) == ';' || license.charAt(i+1) == ':' ||
                        // license.charAt(i+1) == '-' ||
                        text.charAt(i + 1) == ';' || text.charAt(i + 1) == ':'
                        || text.charAt(i + 1) == '+'
                        || text.charAt(i + 1) == '!'
                        || text.charAt(i + 1) == '='
                        || text.charAt(i + 1) == '?')
                    continue;
            }
            mobileText += text.charAt(i);
        }
        String mobileText1 = "";
        for (int i = 0; i < mobileText.length(); i++) {
            if (i > 0 && mobileText.charAt(i) == ' ') {
                if (mobileText.charAt(i - 1) == ','
                        || mobileText.charAt(i - 1) == ' '
                        || mobileText.charAt(i - 1) == '.'
                        ||
                        // mobileText.charAt(i-1) == ';' ||
                        // mobileText.charAt(i-1) == ':' ||
                        // mobileText.charAt(i-1) == '-' ||
                        mobileText.charAt(i - 1) == ';'
                        || mobileText.charAt(i - 1) == ':'
                        || mobileText.charAt(i - 1) == '+'
                        || mobileText.charAt(i - 1) == '!'
                        || mobileText.charAt(i - 1) == '='
                        || mobileText.charAt(i - 1) == '?')
                    continue;
            }
            mobileText1 += mobileText.charAt(i);
        }
        return mobileText1;
    }

    protected String codeBlank(String s) {
        String o = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ')
                o += "%20";
            else
                o += s.charAt(i);
        }
        return o;
    }

    protected String codeData(String s) {
        return codeData(s,false);
    }

    protected String onlyNumeric(String f) {
        String r = "";
        for(int i = 0;i < f.length();i++) {
            if(Character.isDigit(f.charAt(i)))
                r += f.charAt(i);
        }
        return r;
    }

    protected String codedURL(String stringUrl) {
        String codedURL = "";
        for (short n = 0; n < stringUrl.length(); n++) {
            if (stringUrl.charAt(n) == ' ')
                codedURL += "%20";
            else if (stringUrl.charAt(n) == '\'')
                codedURL += "%27";
            else
                codedURL += stringUrl.charAt(n);
        }
        return codedURL;
    }

    protected String codeData(String s, boolean removeMinus) {
        if(removeMinus)
            s = s.replace("-", "");
        String o = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ')
                o += "%20";
            else if (s.charAt(i) == '\'')
                o += "%27";
            else if (s.charAt(i) == ',')
                o += "%2C";
            else if (s.charAt(i) == '~')
                o += "%7E";
            else if (s.charAt(i) == '`')
                o += "%60";
            else if (s.charAt(i) == '!')
                o += "%21";
            else if (s.charAt(i) == '@')
                o += "%40";
            else if (s.charAt(i) == '#')
                o += "%23";
            else if (s.charAt(i) == '$')
                o += "%24";
            else if (s.charAt(i) == '%')
                o += "%25";
            else if (s.charAt(i) == '^')
                o += "%5E";
            else if (s.charAt(i) == '&')
                o += "%26";
            else if (s.charAt(i) == '(')
                o += "%28";
            else if (s.charAt(i) == ')')
                o += "%29";
            else if (s.charAt(i) == '=')
                o += "%3D";
            else if (s.charAt(i) == '+')
                o += "%2B";
            else if (s.charAt(i) == '|')
                o += "%7C";
            else if (s.charAt(i) == '\\')
                o += "%5C";
            else if (s.charAt(i) == '}')
                o += "%7D";
            else if (s.charAt(i) == ']')
                o += "%5D";
            else if (s.charAt(i) == '{')
                o += "%7B";
            else if (s.charAt(i) == '[')
                o += "%5B";
            else if (s.charAt(i) == ';')
                o += "%3B";
            else if (s.charAt(i) == '"')
                o += "%22";
            else if (s.charAt(i) == '<')
                o += "%3C";
            else if (s.charAt(i) == '>')
                o += "%3E";
            else if (s.charAt(i) == '?')
                o += "%3F";
            else
                o += s.charAt(i);
        }
        return o;
    }

    protected void getDefendants(String response, Vector<Defendant> list) {
        while (response.length() > 0) {
            if (response.startsWith("<row>")) {
                String tz = "",last = "", name = "", streetCode = "", streetName = "", number = "", flat = "", cityCode = "", cityName = "", zipcode = "", packachName = "",validUpto = "",chip = "",box = "",
                       TxtPopUp = "",KeshelKod = "", KeshelMsg = "", RightSide = "", LeftSide = "", swExistHistory = "";
                response = response.substring("<row>".length());
                while (response.length() > 0) {
                    /**/ if (response.startsWith("</row>")) {
                        response = response.substring("</row>".length());
                        Defendant defendant = new Defendant(tz, last, name, streetCode, streetName, number, flat, cityCode, cityName, zipcode,packachName,validUpto,chip,box,TxtPopUp,KeshelKod, KeshelMsg, RightSide, LeftSide,swExistHistory);
                        list.addElement(defendant);
                        break;
                    }
                    else if (response.startsWith("<LeftSide>")) {
                        response = response.substring("<LeftSide>".length());
                        LeftSide = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</LeftSide>")+"</LeftSide>".length());
                    }
                    else if (response.startsWith("<RightSide>")) {
                        response = response.substring("<RightSide>".length());
                        RightSide = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</RightSide>")+"</RightSide>".length());
                    }
                    else if (response.startsWith("<KeshelMsg>")) {
                        response = response.substring("<KeshelMsg>".length());
                        KeshelMsg = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</KeshelMsg>")+"</KeshelMsg>".length());
                    }
                    else if (response.startsWith("<KeshelKod>")) {
                        response = response.substring("<KeshelKod>".length());
                        KeshelKod = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</KeshelKod>")+"</KeshelKod>".length());
                    }
                    else if (response.startsWith("<TxtPopUp>")) {
                        response = response.substring("<TxtPopUp>".length());
                        TxtPopUp = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</TxtPopUp>")+"</TxtPopUp>".length());
                    }
                    else if (response.startsWith("<NmF>")) {
                        response = response.substring("<NmF>".length());
                        last = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</NmF>")+ "</NmF>".length());
                    }
                    else if (response.startsWith ("<Nm>")){
                        response = response.substring("<Nm>".length());
                        name = ToHebrew(GetField(response));
                        response = response.substring (response.indexOf("</Nm>") + "</Nm>".length());
                    }
                    else if (response.startsWith ("<StreetKod>")){
                        response = response.substring("<StreetKod>".length());
                        streetCode = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</StreetKod>") + "</StreetKod>".length());
                    }
                    else if (response.startsWith ("<Street>")){
                        response = response.substring("<Street>".length());
                        streetName = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</Street>")+ "</Street>".length());
                    }
                    else if (response.startsWith ("<StreetNo>")){
                        response = response.substring("<StreetNo>".length());
                        number = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</StreetNo>")+ "</StreetNo>".length());
                    }
                    else if (response.startsWith ("<Dira>")){
                        response = response.substring("<Dira>".length());
                        flat = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</Dira>") + "</Dira>".length());
                    }
                    else if (response.startsWith ("<CityKod>")){
                        response = response.substring("<CityKod>".length());
                        cityCode = GetField(response);
                        response = response.substring(response.indexOf("</CityKod>")+ "</CityKod>".length());
                    }
                    else if (response.startsWith ("<CityNm>")){
                        response = response.substring("<CityNm>".length());
                        cityName = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</CityNm>")	+ "</CityNm>".length());
                    }
                    else if (response.startsWith ("<Mikod>")){
                        response = response.substring("<Mikod>".length());
                        zipcode = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</Mikod>")+ "</Mikod>".length());
                    }
                    else if (response.startsWith ("<PackachName>")){
                        response = response.substring("<PackachName>".length());
                        packachName = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</PackachName>")+ "</PackachName>".length());
                    }
                    else if (response.startsWith ("<ValidUpTo>")){
                        response = response.substring("<ValidUpTo>".length());
                        validUpto = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</ValidUpTo>")+ "</ValidUpTo>".length());
                    }
                    else if (response.startsWith ("<Chip>")){
                        response = response.substring("<Chip>".length());
                        chip = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</Chip>")+ "</Chip>".length());
                    }
                    else if (response.startsWith ("<Box>")){
                        response = response.substring("<Box>".length());
                        box = GetField(response);
                        response = response.substring(response.indexOf("</Box>")+ "</Box>".length());
                    }
                    else if (response.startsWith ("<TeudatZeut>")){
                        response = response.substring("<TeudatZeut>".length());
                        tz = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</TeudatZeut>")+ "</TeudatZeut>".length());
                    }
                    else if (response.startsWith ("<TeudatZeut>")){
                        response = response.substring("<TeudatZeut>".length());
                        tz = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</TeudatZeut>")+ "</TeudatZeut>".length());
                    }
                    else if (response.startsWith ("<TeudatZeut>")){
                        response = response.substring("<TeudatZeut>".length());
                        tz = ToHebrew(GetField(response));
                        response = response.substring(response.indexOf("</TeudatZeut>")+ "</TeudatZeut>".length());
                    }
                    else if (response.startsWith ("<SwExistHistory>")){
                        response = response.substring("<SwExistHistory>".length());
                        swExistHistory = GetField(response).trim();
                        response = response.substring(response.indexOf("</SwExistHistory>")+ "</SwExistHistory>".length());
                    }
                    else {
                        response = response.substring(1);
                    }
                }
            } else {
                response = response.substring(1);
            }
        }
    }

    public String reverseNumber(String reponse) {
        for(int i = 0; i < reponse.length(); i++) {
            if(reponse.charAt(i) >= '0' && reponse.charAt(i) <= '9') {
                int j;
                for (j = i+1; j < reponse.length() && reponse.charAt(j) >= '0' && reponse.charAt(j) <= '9'; j++);
                String reverse = "";
                for (int k = j - 1; k >= i; k--)
                    reverse += reponse.charAt(k);
                reponse = reponse.substring(0, i)+reverse+reponse.substring(j,reponse.length());
                i = j - 1;
            }
        }
        return reponse;
    }
}
