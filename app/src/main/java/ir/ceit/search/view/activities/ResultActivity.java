package ir.ceit.search.view.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.ceit.search.R;
import ir.ceit.search.model.News;
import ir.ceit.search.services.Paginator;
import ir.ceit.search.view.adapters.SimilarNewsAdapter;

public class ResultActivity extends AppCompatActivity {
    private TextView publishDate;
    private TextView title;
    private TextView url;
    private TextView summary;
    private TextView metaTags;
    private TextView content;
    private CircleImageView thumbnail;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private SimilarNewsAdapter newsAdapter;
    private TextView brief;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        News news = (News) getIntent().getSerializableExtra("news");

        thumbnail = findViewById(R.id.image);
        title = findViewById(R.id.titleTV);
        content = findViewById(R.id.contentTV);
        publishDate = findViewById(R.id.publishDateTV);
        url = findViewById(R.id.urlTV);
        summary = findViewById(R.id.summaryTV);
        metaTags = findViewById(R.id.meta_tags_TV);

        Glide.with(this).asBitmap().load(news.getThumbnail()).into(thumbnail);
        title.setText(news.getTitle());
        content.setText(news.getContent());
        publishDate.setText(news.getPublishDate());

        String summaryString = "خلاصه خبر: " + news.getSummary() + "";
        String newString = summaryString.replaceAll("خلاصه خبر: ", "<b>" + "خلاصه خبر: " + "</b>");
        summary.setText(Html.fromHtml(newString));

        String urlString = "سایت منبع: " + news.getUrl() + "";
        newString = urlString.replaceAll("سایت منبع: ", "<b>" + "سایت منبع: " + "</b>");
        url.setText(Html.fromHtml(newString));

        String metaTagsString = "Tags: " + news.getMetaTags() + "";
        newString = metaTagsString.replaceAll("Tags: ", "<b>" + "Tags: " + "</b>");
        metaTags.setText(Html.fromHtml(newString));

        //similar news view
        linearLayout = findViewById(R.id.linearLayoutSimilar);
        try {
            if (news.getSimilarNews().size() > 0) {
                newsAdapter = new SimilarNewsAdapter(news.getSimilarNews(), this);
                initRecyclerView();
            } else linearLayout.setVisibility(View.GONE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSimilar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(newsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
    }
}
