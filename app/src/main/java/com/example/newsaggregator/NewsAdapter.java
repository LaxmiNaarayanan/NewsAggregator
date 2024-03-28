package com.example.newsaggregator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsaggregator.model.News;
//import com.example.newsaggregator.services.ImageService;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<News> newsList;private MainActivity mainActivity;


    public NewsAdapter(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    public void setNewsList(ArrayList<News> newsList) {

        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news, parent, false);
        view.setOnClickListener(mainActivity);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        News news;String get_date;
        news = newsList.get(position);
        holder.title.setText(news.getTitle());holder.desc.setText(news.getDesc());
        holder.author.setText(news.getAuthor());

        // getting the date for the format or changing from zolo time zone to specified time zone

        get_date = getFormattedDateFromString(news.getTime());

        holder.time.setText(get_date);holder.pageCount.setText((position + 1) + " of " + newsList.size());


        if (!news.getUrlImage().equals("null")) {
            holder.imageView.setImageResource(R.drawable.loading);
            new ImageService(holder.imageView, mainActivity).execute(news.getUrlImage());
        }

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView author,time,title,desc,pageCount;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.author);title = itemView.findViewById(R.id.newstitle);
            time = itemView.findViewById(R.id.date);desc = itemView.findViewById(R.id.desc);
            pageCount = itemView.findViewById(R.id.pagecount);imageView = itemView.findViewById(R.id.newsimage);

        }

    }

    private class ImageService extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        MainActivity mainActivity;

        public ImageService(ImageView imageView, MainActivity mainActivity) {
            this.imageView = imageView;this.mainActivity = mainActivity;

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap mIcon11;
            try {
                InputStream in;
                in = new java.net.URL(strings[0]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {

                Log.e("Error", e.getMessage());
                mIcon11 = null;
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                imageView.setImageResource(mainActivity.getResources().
                        getIdentifier("brokenimage", "drawable", mainActivity.getPackageName()));
            } else {
                imageView.setImageBitmap(result);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFormattedDateFromString(String publishedAt) {
        String get_date = "";
        try {
            DateTimeFormatter timeFormatter,dateTimeFormatter;TemporalAccessor accessor;
            LocalDateTime localDateTime;

            timeFormatter= DateTimeFormatter.ISO_INSTANT;
            accessor = timeFormatter.parse(publishedAt);

            dateTimeFormatter  = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");

            localDateTime = LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
            get_date = localDateTime.format(dateTimeFormatter);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(get_date.isEmpty()) {
            try {
                DateTimeFormatter timeFormatter,dateTimeFormatter;TemporalAccessor accessor;
                LocalDateTime localDateTime;

                timeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                accessor = timeFormatter.parse(publishedAt);

                dateTimeFormatter  = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
                localDateTime= LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());

                get_date = localDateTime.format(dateTimeFormatter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return get_date;
    }
}
