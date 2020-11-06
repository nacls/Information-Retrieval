package ir.ceit.search.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SimilarNews {
    private DocListElement docListElement;
    private ArrayList<Integer> similarNews;

    public SimilarNews(DocListElement docListElement)
    {
        this.docListElement = docListElement;
        this.similarNews = new ArrayList<>();
    }

    public DocListElement getDocListElement() {
        return docListElement;
    }

    public void setDocListElement(DocListElement docListElement) {
        this.docListElement = docListElement;
    }

    public ArrayList<Integer> getSimilarNews() {
        return similarNews;
    }

    public void setSimilarNews(ArrayList<Integer> similarNews) {
        this.similarNews = similarNews;
    }
}
