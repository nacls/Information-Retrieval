package ir.ceit.search.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class News implements Serializable {
    private String publishDate;
    private String title;
    private String url;
    private String summary;
    private String metaTags;
    private String content;
    private String thumbnail;
    private String brief;
    private List<News> similarNews;

    public News(String publishDate, String title, String url, String summary, String metaTags, String content, String thumbnail) {
        this.publishDate = publishDate;
        this.title = title;
        this.url = url;
        this.summary = summary;
        this.metaTags = metaTags;
        this.content = content;
        this.thumbnail = thumbnail;
        similarNews = new ArrayList<>();
    }


    public List<News> getSimilarNews() throws ParseException {
        return sortResult(similarNews);
    }

    public void addSimilarNews(News mSimilarNews) {
        similarNews.add(mSimilarNews);
    }

    public int compareTo(@NonNull News a) throws ParseException {
        String dateA = a.getPublishDate();
        String dateB = this.getPublishDate();
        if (dateA.compareTo(dateB) == 0)
            return 0;
        else if (dateA.compareTo(dateB) < 0)
            return 1;
        else
            return -1;

    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getBrief() {
        return brief;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }

    public String getMetaTags() {
        return metaTags;
    }

    public String getContent() {
        return content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    private List<News> sortResult(List<News> notSorted) throws ParseException {
        List<News> results = new ArrayList<News>(notSorted);
        for (int i = 0; i < results.size(); i++) {
            for (int j = 0; j < results.size(); j++) {
                if (results.get(i).compareTo(results.get(j)) == 1) {
                    Collections.swap(results, i, j);
                }
            }
        }
        return results;
    }

}
