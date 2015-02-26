package com.dev_4wardwv.android.audiblecodetest;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener {

    String nextMaxTagId;
    static Context context;

    private RecyclerView mRecyclerView;
    private CustomRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    static ImageView fullView;
    static HashMap<String,String> urlCache = new HashMap<>();

    //private EditText mText;
    //private EditText mColor;

    private ImageView mTopImage, mBottomImage, mLargeImage;

    private ArrayList<Data> mData = new ArrayList<>();

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 12;
    int firstVisibleItem, visibleItemCount, totalItemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        fullView = (ImageView)findViewById(R.id.fullImage);

        // Initializing views.

        mTopImage = (ImageView) findViewById(R.id.topImg);
        mBottomImage = (ImageView) findViewById(R.id.bottomImg);
        mLargeImage = (ImageView) findViewById(R.id.largeImg);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        // If the size of views will not change as the data changes.
        mRecyclerView.setHasFixedSize(true);

        // Setting the LayoutManager.
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Setting the adapter.
        mAdapter = new CustomRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               // Log.d("Scrolling", String.valueOf(dx));
                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                Log.d("ti-vic",String.valueOf(totalItemCount - visibleItemCount));
                Log.d("fvi",String.valueOf(firstVisibleItem));
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.d("...", "end called");

                    new LoadImages().execute("60",nextMaxTagId);

                    loading = true;
                }
            }
        });

        new LoadImages().execute("60");
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

    public void onClick(View V){
        ImageView fullView = (ImageView) findViewById(R.id.fullImage);
        ImageView clicked = (ImageView) V;
        fullView.setImageDrawable(clicked.getDrawable());
    }

    private class LoadImages extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        String req_url = "";

        @Override
        protected String doInBackground(String... params) {

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.d("url_count", params[0]);
                 req_url = "https://api.instagram.com/v1/tags/selfie/media/recent?count="+params[0]+"&client_id=" + getResources().getText(R.string.CLIENTID).toString();
                Log.d("url", req_url);
                if(params.length > 1)
                    req_url= req_url + "&MAX_TAG_ID="+params[1];
                HttpGet httpGet = new HttpGet(req_url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            //Log.d("resp", json);

            return json;

        }

        @Override
        protected void onPostExecute(String result) {
            //EditText txt = (EditText) findViewById(R.id.txtResult);
            //txt.setText(result);

            String tLargeImage = "", tTopImage = "";

            //Log.d("result", result);

            if(result.length() < 10)
                return; //request failed

            int count = 0;
            String thumb = "", lowRes = "", highRes = "";

            try {
                jObj = new JSONObject(result);
                JSONArray jArray = jObj.getJSONArray("data");

                //get next max id for further queries
                nextMaxTagId = jObj.getJSONObject("pagination").getString("next_max_tag_id");

                //JSONArray imgArray = jArray.JSONArray(2);
                //JSONArray tObjArray;

                //Log.d("jArray", jArray.toString());

                Log.d("jArray Len",String.valueOf(jArray.length()));

                for(int i = 0; i < jArray.length(); i++)

                {

                    //lowRes = jArray.getJSONObject(i).getJSONObject("images").getJSONObject(getResources().getText(R.string.TAG_LOWRESOLUTION).toString()).getString("url");
                    //thumb = jArray.getJSONObject(i).getJSONObject("images").getJSONObject(getResources().getText(R.string.TAG_THUMBNAIL).toString()).getString("url");
                    highRes = jArray.getJSONObject(i).getJSONObject("images").getJSONObject(getResources().getText(R.string.TAG_HIGHRESOLUTION).toString()).getString("url");

                    switch (i%3){
                        case 0:
                            tLargeImage = highRes;
                            break;
                        case 1:
                            tTopImage = highRes;
                            break;
                        case 2:
                            Data tData = new Data(tLargeImage, tTopImage, highRes);
                            mData.add(tData);
                            mAdapter.addItem(mData.size()-1, tData);
                            Log.d("Data Added", String.valueOf(count));
                            break;
                    }

                    count++;

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }


        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
