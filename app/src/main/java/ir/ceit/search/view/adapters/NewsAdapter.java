package ir.ceit.search.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.ceit.search.R;
import ir.ceit.search.SubApplication;
import ir.ceit.search.model.News;
import ir.ceit.search.nlp.Stemmer;
import ir.ceit.search.view.activities.ResultActivity;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> news;
    private Context mContext;
    private ArrayList<String> queryTokens;
    private RecyclerView recyclerView;
    private String query;

    public NewsAdapter(List<News> news, Context mContext, String query, ArrayList<String> queryTokens) {
        this.news = news;
        this.mContext = mContext;
        this.query = query;
        this.queryTokens = queryTokens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext).asBitmap().load(news.get(position).getThumbnail()).into(holder.image);
        holder.newsTitle.setText(news.get(position).getTitle());
        holder.newsContent.setText(highlightSearchKey(news.get(position).getBrief()));
        //holder.newsContent.setText(highlightString(news.get(position).getBrief()));
        holder.publishDate.setText(news.get(position).getPublishDate());
        int similarNewsSize = 0;
        try {
            similarNewsSize = news.get(position).getSimilarNews().size();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (similarNewsSize > 0) {
            SimilarNewsAdapter similarNewsAdapter = null;
            if (similarNewsSize > 5) {
                List<News> similarNews = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    try {
                        similarNews.add(news.get(position).getSimilarNews().get(i));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                similarNewsAdapter = new SimilarNewsAdapter(similarNews, mContext);
            } else {
                try {
                    similarNewsAdapter = new SimilarNewsAdapter(news.get(position).getSimilarNews(), mContext);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.recyclerView.setAdapter(similarNewsAdapter);
            holder.recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
            layoutManager.setReverseLayout(true);
            holder.recyclerView.setLayoutManager(layoutManager);
        } else holder.linearLayout.setVisibility(View.GONE);


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
        CircleImageView image;
        TextView newsTitle;
        TextView newsContent;
        TextView publishDate;
        RelativeLayout parentLayout;
        RecyclerView recyclerView;
        LinearLayout linearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            newsTitle = itemView.findViewById(R.id.titleTV);
            newsContent = itemView.findViewById(R.id.contentTV);
            publishDate = itemView.findViewById(R.id.publishDateTV);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            recyclerView = itemView.findViewById(R.id.recyclerViewSimilar);
            linearLayout = itemView.findViewById(R.id.linearLayoutSimilar);
        }
    }

    private Spannable highlightSearchKey(String brief) {
        Spannable highlight;
        Pattern pattern;
        Matcher matcher;
        int word_index;
        String title_str;
        Stemmer stemmer = new Stemmer(SubApplication.getVerbs());
        word_index = queryTokens.size();
        title_str = Html.fromHtml(brief).toString();
        highlight = (Spannable) Html.fromHtml(brief);
        for (int index = 0; index < word_index; index++) {
            pattern = Pattern.compile("(?i)" + queryTokens.get(index));
            matcher = pattern.matcher(title_str);
            while (matcher.find()) {
                highlight.setSpan(
                        new BackgroundColorSpan(0xD993d2b9),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return highlight;
    }
}
