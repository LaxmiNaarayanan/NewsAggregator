package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsaggregator.model.News;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity"; private static RequestQueue queue, queue1;
    private DrawerLayout draw_layout;private ListView draw_List;
    private ArrayAdapter<String> array_adpt;private ActionBarDrawerToggle draw_toggle;
    private News[] news_source;private String[] sources;
    ArrayList<News> articles; public News news1;
    private String key ="dc4972e1241c4edc9e5cccb70a9a7bd1";private NewsAdapter news_Adapter;
    RecyclerView recycler_View;private static List<News> news = new ArrayList<>();
    private static List<News> newsArticle = new ArrayList<>();private Menu topics;
    Map<Integer, String> filterMap = new HashMap<>();


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setNewsSource(List<News> newsSources) {
        newsArticle = new ArrayList<>(newsSources);
        sources = new String[newsSources.size()];
        News[] newsData;
        newsData = new News[newsSources.size()];

        for (int i = 0; i < sources.length; i++)
            sources[i] = newsSources.get(i).getName();

        this.setTitle("News Gateway " + "(" +sources.length+")");

        array_adpt = new ArrayAdapter<String>(this, R.layout.drawer_list, sources) {
            @NonNull
            @Override
            // we are getting the view of all the sources from the network
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view1;
                view1 = super.getView(position, convertView, parent);
                ((TextView)view1.findViewById(android.R.id.text1)).
                        setTextColor(news1.getColor(newsSources.get(position).getCategory()));
                return view1;
            }
        };
        // creating the list of array
        draw_List.setAdapter(array_adpt);

        for(int i = 0; i < newsSources.size(); i++){
            newsData[i] = newsSources.get(i);
        }

        this.news_source = newsData;
        updateMenu(newsData); // update the data everytime we update the list

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void assignStart() {
        //calling the layout for assignment and generating the list
        draw_layout = findViewById(R.id.drawer_layout);draw_List = findViewById(R.id.left_drawer);
        // recyclerView and filter map call to have the all the list in proper format
        recycler_View = findViewById(R.id.newslist);filterMap.put(1, "all");
        // Calling rhe source data and storing it
        fetchSourceData();
        // calling the news model to get the details and storing it in local variable
        news1 = new News();
        news1.setColor(this);
    }

    // setting the articles in the array list
    public void setArticles(ArrayList<News> articles) {
        this.articles = articles;news_Adapter = new NewsAdapter(this);

        news_Adapter.setNewsList(articles);

        recycler_View.setAdapter(news_Adapter);
        recycler_View.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        assignStart();

        draw_List.setOnItemClickListener((parent, view, position, id) -> selectItem(position));
        draw_toggle = new ActionBarDrawerToggle(this, draw_layout, R.string.drawer_open, R.string.drawer_close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    // select the specific items required and framing it as per the requirement

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectItem(int position) {
        // getting the name and id
        this.setTitle(newsArticle.get(position).getName());fetchArticleData(newsArticle.get(position).getId ());
        // showing it in frame
        findViewById(R.id.content_frame).setBackgroundColor(Color.parseColor("#ffffff"));
        draw_layout.closeDrawer(draw_List);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateData(String item, int id) {
        if (id == 0) return;

        String prev;
        prev = filterMap.get(id);
        filterMap.put(id, item);

        News[] filteredSources;

        filteredSources = Arrays.stream(news_source)
                .filter(source -> (source.getCategory().
                        equals(filterMap.get(1)) || filterMap.get(1).equals("all")))
                .toArray(News[]::new);

        newsArticle = new ArrayList<>();

        for(int r = 0; r < filteredSources.length; r++){
            newsArticle.add(filteredSources[r]);
        }

        String []nSources;
        nSources= Arrays.stream(filteredSources).map(News::getName).toArray(String[]::new);

        if (nSources.length == 0) {

            new AlertDialog.Builder(this)
                    .setTitle("No News Sources")
                    .setMessage("no news sources exist that match the specified")
                    .setPositiveButton("OK", (dialog, which) -> filterMap.put(id, prev)).show();

        } else {

            sources = nSources;
            array_adpt = new ArrayAdapter<String>(this, R.layout.drawer_list, sources) {

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view;
                    view = super.getView(position, convertView, parent);

                    ((TextView)view.findViewById(android.R.id.text1)).
                            setTextColor(news1.getColor(filteredSources[position].getCategory()));
                    return view;
                }
            };

            draw_List.setAdapter(array_adpt);
            array_adpt.notifyDataSetChanged();

            setTitle("News Gateway (" + sources.length + ")");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        draw_toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        draw_toggle.onConfigurationChanged(newConfig);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMenu(News[] newsSources) {

        Arrays.stream(newsSources).map(News::getCategory).distinct().forEach((topic) -> {
            SpannableString str;
            str = new SpannableString(topic);
            str.setSpan(new ForegroundColorSpan(news1.getColor(topic)), 0, str.length(), 0);

            topics.add(1, 0, 0, str);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        topics = menu;
        topics.add(1, 0, 0, "all");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (draw_toggle.onOptionsItemSelected(item)) {

            Log.d(TAG, "onOptionsItemSelected: DrawerToggle " + item);
            return true;
        }

        updateData(item.getTitle().toString(), item.getGroupId());
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {

        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View v) {

        int position;
        position = recycler_View.getChildAdapterPosition(v);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(articles.get(position).getNewsUrl())));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void fetchSourceData() {

        queue = Volley.newRequestQueue(this);

        Uri.Builder builder;
        builder = Uri.parse("https://newsapi.org/v2/top-headlines/sources").buildUpon();
        builder.appendQueryParameter("apiKey", key);

        String url;
        url = builder.build().toString();

        Response.Listener<JSONObject> jsonObjectListener;
        jsonObjectListener= response -> parseJSON(response.toString());

        Response.ErrorListener errorListener = error -> {
            setNewsSource(null);
        };
        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }

        };
        queue.add(jsonObjectRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void parseJSON(String toString) {
        try {

            JSONArray jSources;
            jSources = new JSONObject(toString).getJSONArray("sources");

            for(int i = 0; i < jSources.length(); i++) {
                Map<String, String> map = new HashMap<>();

                JSONObject News;
                News= (JSONObject) jSources.get(i);
                news.add(new News(News.getString("id"),News.getString("name"),News.getString("category")));

            }
            setNewsSource(news);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchArticleData(String source){

        queue1 = Volley.newRequestQueue(this);
        System.out.println("Sources:::"+source);

        Uri.Builder builder;
        builder = Uri.parse("https://newsapi.org/v2/top-headlines").buildUpon();

        builder.appendQueryParameter("sources", source);
        builder.appendQueryParameter("apiKey", key);

        String url;
        url = builder.build().toString();

        Response.Listener<JSONObject> jsonObjectListener;
        jsonObjectListener = response -> parseArticleJSON(response.toString());

        Response.ErrorListener errorListener = error -> {
            setArticles(null);
        };

        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queue1.add(jsonObjectRequest);
    }

    private void parseArticleJSON(String toString) {

        articles = new ArrayList<>();
        try {
            JSONArray newsSources;
            newsSources = new JSONObject(toString).getJSONArray("articles");
            for(int i = 0; i < newsSources.length(); i++) {

                JSONObject article;
                article = (JSONObject) newsSources.get(i);
                articles.add(new News(article.getString("title"),article.getString("author"),article.getString("description"),
                        article.getString("publishedAt"),article.getString("urlToImage"),article.getString("url")));

            }
            setArticles(articles);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}