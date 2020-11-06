package ir.ceit.search.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.ceit.search.R;
import ir.ceit.search.model.News;
import ir.ceit.search.view.activities.ResultActivity;

public class SimilarNewsAdapter extends RecyclerView.Adapter<SimilarNewsAdapter.ViewHolder> {

    private List<News> news;
    private Context mContext;


    public SimilarNewsAdapter(List<News> news, Context mContext) {
        this.news = news;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SimilarNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_news_item, parent, false);
        SimilarNewsAdapter.ViewHolder holder = new SimilarNewsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarNewsAdapter.ViewHolder holder, int position) {
        holder.newsTitle.setText(news.get(position).getTitle());
        holder.url.setText(news.get(position).getUrl());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: clicked on: " + news.get(position).getTitle());
                Intent intent = new Intent(mContext, ResultActivity.class);
                intent.putExtra("news", news.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView url;
        RelativeLayout parentLayout;

        public ViewHolder(View v) {
            super(v);
            newsTitle = v.findViewById(R.id.titleTV);
            url = v.findViewById(R.id.urlTV);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
