package com.loc.wayne.libraryofcongressandroid;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * Created by wayne on 12/18/14.
 */
public class GetLibraryRecordsTask extends AsyncTask<String, Integer, Hashtable<Integer, LibraryOfCongressRecord>> {

    private final static String LOC_URL = "http://www.loc.gov/pictures/search/?q=congress&fo=json";

    @Override
    protected Hashtable<Integer, LibraryOfCongressRecord> doInBackground(String... params) {
        Hashtable<Integer, LibraryOfCongressRecord> locResults = null;
        try {
            String responseString = getHttpResponse();
            if (responseString != null)
                locResults = hashTheResponse(responseString);
        } catch (final Exception ex) {
            Log.d(String.format("%s.%s", PeriodCycleFragment.class.getSimpleName(), "doInBackground"), ex.toString());
        }
        return locResults;
    }

    private String getHttpResponse() throws IOException {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet(LOC_URL);
        final HttpResponse resp = client.execute(get);
        final HttpEntity entity = resp.getEntity();
        final InputStream is = entity.getContent();
        final String responseString = readToEnd(is);
        is.close();
        return responseString;
    }

    private Hashtable<Integer, LibraryOfCongressRecord> hashTheResponse(String response) throws JSONException {
        final JSONObject json = new JSONObject(response);
        final JSONArray results = json.getJSONArray("results");

        final Hashtable<Integer, LibraryOfCongressRecord> locResults = new Hashtable<Integer, LibraryOfCongressRecord>();

        for(int i = 0; i < results.length(); i++) {
            final JSONObject resultJO = results.getJSONObject(i);
            final int id = resultJO.optInt("index", -1);
            final LibraryOfCongressRecord result = LibraryOfCongressRecord.locRecordFromJSONObject(resultJO);
            locResults.put(id, result);
        }

        return locResults;
    }

    private static String readToEnd(InputStream input) throws IOException {
        final DataInputStream dis = new DataInputStream(input);
        final byte[] stuff = new byte[1024];
        final ByteArrayOutputStream buff = new ByteArrayOutputStream();
        int read = 0;
        while ((read = dis.read(stuff)) != -1) {
            buff.write(stuff, 0, read);
        }

        return new String(buff.toByteArray());
    }
}
