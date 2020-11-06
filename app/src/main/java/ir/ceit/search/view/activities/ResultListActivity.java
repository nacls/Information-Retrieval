package ir.ceit.search.view.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import htz.ir.stemming.PersianStemmer;
import ir.ceit.search.R;
import ir.ceit.search.SubApplication;
import ir.ceit.search.model.Dictionary;
import ir.ceit.search.model.DocInfo;
import ir.ceit.search.model.News;
import ir.ceit.search.model.SimilarNews;
import ir.ceit.search.model.Verb;
import ir.ceit.search.nlp.Processor;
import ir.ceit.search.nlp.Stemmer;
import ir.ceit.search.nlp.Tokenizer;
import ir.ceit.search.services.Paginator;
import ir.ceit.search.services.Searcher;
import ir.ceit.search.services.TextCleaner;
import ir.ceit.search.view.adapters.NewsAdapter;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class ResultListActivity extends AppCompatActivity {
    //logic
    private Dictionary dictionary;
    private ArrayList<Verb> verbs;
    private Searcher searcher;
    private String query;
    private List<News> resultNews;
    ArrayList<SimilarNews> results;
    private String[] paths = SubApplication.getPaths();
    private ArrayList<DocInfo> docInfos = SubApplication.getDocInfo();
    private ArrayList<String> queryTokens;
    private boolean sortTimeClicked = false;

    //view
    private TextView resultTV;
    private TextView resultCountTime;
    private RecyclerView recyclerView;
    private TextView emptyTV;
    private NewsAdapter newsAdapter;
    private NewsAdapter sortedNewsAdapter;
    private ImageView sortIV;
    private int resultCount;
    //pagination
    Button nextBtn, prevBtn;
    Paginator p;
    private int totalPages;
    private int currentPage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        Bundle bundle = getIntent().getExtras();
        query = bundle.getString("message");
        //view
        resultTV = (TextView) findViewById(R.id.resultTextView);
        String resultTitle = query;
        String newString = resultTitle.replaceAll(query, "<font color='blue'>" + query + "</font>");
        resultTV.setText(Html.fromHtml("نتایج جست و جو برای: " + newString));
        //logic
        dictionary = SubApplication.getDictionary();
        verbs = SubApplication.getVerbs();
        searcher = SubApplication.getSearcher();
        //logic
        long startTime = System.currentTimeMillis();
        results = new ArrayList<>();
        search(query);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Search time : " + duration + " milliseconds");
        //logic
        queryTokens = getTokensBrief(query);
        resultNews = loadResults(results);
        for (int i = 0; i < resultNews.size(); i++) {
            resultNews.get(i).setBrief(getBriefNews(resultNews.get(i).getContent()));
        }
        //view
        resultCountTime = (TextView) findViewById(R.id.resultCountTime);
        resultCountTime.setText("زمان جست و جو: " + duration + " میلی ثانیه" + "، تعداد نتایج: " + resultCount);
        //build recycler view
        p = new Paginator(resultNews.size(), resultNews);
        totalPages = p.TOTAL_NUM_ITEMS / p.ITEMS_PER_PAGE;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        emptyTV = findViewById(R.id.empty_view);
        if (resultNews.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTV.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTV.setVisibility(View.GONE);
            nextBtn = (Button) findViewById(R.id.nextBtn);
            prevBtn = (Button) findViewById(R.id.previousBtn);
            System.out.println("LOG TOTAL PAGES:" + totalPages);
            if (totalPages == 0)
                nextBtn.setEnabled(false);
            prevBtn.setEnabled(false);
            newsAdapter = new NewsAdapter(p.generatePage(currentPage), this, query, queryTokens);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(newsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPage += 1;
                    // enableDisableButtons();
                    recyclerView.setAdapter(new NewsAdapter(p.generatePage(currentPage), ResultListActivity.this, query, queryTokens));
                    toggleButtons();
                }
            });
            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPage -= 1;
                    recyclerView.setAdapter(new NewsAdapter(p.generatePage(currentPage), ResultListActivity.this, query, queryTokens));
                    toggleButtons();
                }
            });
            //sorts the news on time and similarity
            sortIV = findViewById(R.id.sortIV);
            sortIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //recyclerView.setAdapter(newsAdapter);
                    if (!sortTimeClicked) {
                        try {
                            recyclerView.setAdapter(new NewsAdapter(sortResult(p.generatePage(currentPage)), ResultListActivity.this, query, queryTokens));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        sortTimeClicked = true;
                        System.out.println("IM PRINTING SORTED");
                    } else {
                        recyclerView.setAdapter(new NewsAdapter(p.generatePage(currentPage), ResultListActivity.this, query, queryTokens));
                        sortTimeClicked = false;
                        System.out.println("IM PRINTING NOOOOOT SORTED");

                    }
                }
            });


        }

    }


//    public List<News> loadResults(ArrayList<SimilarNews> docIDs) {
//        List<News> resultsNews = new ArrayList<News>();
//        for (int i = 0; i < docIDs.size(); i++) {
//            int docID = results.get(i).getDocListElement().getDocID();
//            News temp = readNewsFromFile(docID);
//            for (int j = 0; j < results.get(i).getSimilarNews().size(); j++) {
//                int docIDSimilar = results.get(i).getSimilarNews().get(j);
//                News tempSim = readNewsFromFile(docIDSimilar);
//                temp.addSimilarNews(tempSim);
//            }
//            resultsNews.add(temp);
//        }
//
//        return resultsNews;
//    }

    public List<News> loadResults(ArrayList<SimilarNews> docIDs) {
        List<News> resultsNews = new ArrayList<News>();
        try {
            AssetManager am = getAssets();
            InputStream is = am.open(paths[0]);
            Workbook wb = Workbook.getWorkbook(is);
            Sheet sheet = wb.getSheet(0);
            int rowNum = sheet.getRows();
            int colNum = sheet.getColumns();
            int contentColNum = 0;
            int publishDateColNum = 0;
            int titleColNum = 0;
            int urlColNum = 0;
            int summaryColNum = 0;
            int metaTagsColNum = 0;
            int thumbnailColNum = 0;
            for (int i = 0; i < colNum; i++) {
                Cell temp = sheet.getCell(i, 0);
                if (temp.getContents().equals("content")) {
                    contentColNum = i;
                }
                if (temp.getContents().equals("publish_date")) {
                    publishDateColNum = i;
                }
                if (temp.getContents().equals("title")) {
                    titleColNum = i;
                }
                if (temp.getContents().equals("url")) {
                    urlColNum = i;
                }
                if (temp.getContents().equals("summary")) {
                    summaryColNum = i;
                }
                if (temp.getContents().equals("meta_tags")) {
                    metaTagsColNum = i;
                }
                if (temp.getContents().equals("thumbnail")) {
                    thumbnailColNum = i;
                }
            }
            for (int i = 0; i < docIDs.size(); i++) {
                int docID = results.get(i).getDocListElement().getDocID();
                Cell publishDate = sheet.getCell(publishDateColNum, docID);
                Cell title = sheet.getCell(titleColNum, docID);
                Cell url = sheet.getCell(urlColNum, docID);
                Cell summary = sheet.getCell(summaryColNum, docID);
                Cell metaTags = sheet.getCell(metaTagsColNum, docID);
                Cell content = sheet.getCell(contentColNum, docID);
                Cell thumbnail = sheet.getCell(thumbnailColNum, docID);
                News temp = new News(publishDate.getContents(), title.getContents(), url.getContents(), summary.getContents(), metaTags.getContents(), cleanupText(content.getContents(), "%,;&"), thumbnail.getContents());
                for (int j = 0; j < results.get(i).getSimilarNews().size(); j++) {
                    int docIDSimilar = results.get(i).getSimilarNews().get(j);
                    Cell publishDateSim = sheet.getCell(publishDateColNum, docIDSimilar);
                    Cell titleSim = sheet.getCell(titleColNum, docIDSimilar);
                    Cell urlSim = sheet.getCell(urlColNum, docIDSimilar);
                    Cell summarySim = sheet.getCell(summaryColNum, docIDSimilar);
                    Cell metaTagsSim = sheet.getCell(metaTagsColNum, docIDSimilar);
                    Cell contentSim = sheet.getCell(contentColNum, docIDSimilar);
                    Cell thumbnailSim = sheet.getCell(thumbnailColNum, docIDSimilar);
                    News tempSim = new News(publishDateSim.getContents(), titleSim.getContents(), urlSim.getContents(), summarySim.getContents(), metaTagsSim.getContents(), cleanupText(contentSim.getContents(), "%,;&"), thumbnailSim.getContents());
                    temp.addSimilarNews(tempSim);
                }
                resultsNews.add(temp);
            }
            wb.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return resultsNews;
    }


    public News readNewsFromFile(int docID) {
        News news = new News("", "", "", "", "", "", "");
        try {
            AssetManager am = getAssets();
            InputStream is = am.open(docInfos.get(docID).getPath());
            Workbook wb = Workbook.getWorkbook(is);
            Sheet sheet = wb.getSheet(0);
            int rowNum = sheet.getRows();
            int colNum = sheet.getColumns();
            int contentColNum = 0;
            int publishDateColNum = 0;
            int titleColNum = 0;
            int urlColNum = 0;
            int summaryColNum = 0;
            int metaTagsColNum = 0;
            int thumbnailColNum = 0;
            for (int i = 0; i < colNum; i++) {
                Cell temp = sheet.getCell(i, 0);
                if (temp.getContents().equals("content")) {
                    contentColNum = i;
                }
                if (temp.getContents().equals("publish_date")) {
                    publishDateColNum = i;
                }
                if (temp.getContents().equals("title")) {
                    titleColNum = i;
                }
                if (temp.getContents().equals("url")) {
                    urlColNum = i;
                }
                if (temp.getContents().equals("summary")) {
                    summaryColNum = i;
                }
                if (temp.getContents().equals("meta_tags")) {
                    metaTagsColNum = i;
                }
                if (temp.getContents().equals("thumbnail")) {
                    thumbnailColNum = i;
                }
            }
            int row = docInfos.get(docID).getRow();
            Cell publishDate = sheet.getCell(publishDateColNum, row);
            Cell title = sheet.getCell(titleColNum, row);
            Cell url = sheet.getCell(urlColNum, row);
            Cell summary = sheet.getCell(summaryColNum, row);
            Cell metaTags = sheet.getCell(metaTagsColNum, row);
            Cell content = sheet.getCell(contentColNum, row);
            Cell thumbnail = sheet.getCell(thumbnailColNum, row);
            news = new News(publishDate.getContents(), title.getContents(), url.getContents(), summary.getContents(), metaTags.getContents(), cleanupText(content.getContents(), "%,;&"), thumbnail.getContents());
        } catch (Exception e) {
            System.out.println(e);
        }
        return news;
    }

    public void search(String query) {
        searcher.searchQuery(query);
        results = searcher.getResults();
        resultCount = results.size();
        System.out.println("LOG Result count: " + resultCount);
    }

    public String cleanupText(String string, String charsToDelete) {
        TextCleaner textCleaner = new TextCleaner();
        return textCleaner.removeChars(textCleaner.removeHTMLTags(textCleaner.removeWords(string)), charsToDelete);
    }

    public ArrayList<String> getTokensBrief(String input) {
        ArrayList<String> tokens;
        Stemmer stemmer = new Stemmer(verbs);
        PersianStemmer persianStemmer = new PersianStemmer();
        Tokenizer tokenizer = new Tokenizer(stemmer);
        Processor processor = new Processor();
        String cleanText = processor.process(input);
        tokens = tokenizer.tokenize(cleanText);
        for (int i = 0; i < tokens.size(); i++) {
            tokens.set(i, stemmer.stem(tokens.get(i)));
            tokens.set(i, persianStemmer.run(tokens.get(i)));
            if (processor.isStopWord(tokens.get(i)) || tokens.get(i).equals("cat") || tokens.get(i).equals("source")) {
                tokens.remove(i);
                i--;
            }
        }
        return tokens;
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

    private void toggleButtons() {
        if (currentPage == totalPages) {
            nextBtn.setEnabled(false);
            prevBtn.setEnabled(true);
        } else if (currentPage == 0) {
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(true);
        } else if (currentPage >= 1 && currentPage <= totalPages) {
            nextBtn.setEnabled(true);
            prevBtn.setEnabled(true);
        }
    }

    public String getBriefNews(String news) {
        String[] newsSentences = news.split("[(?<=!)(?<=.)(?<=؟)]");
        int[] score = new int[newsSentences.length];
        String brief = "";
        int maxSentence = 1;
        int sentenceCounter = 0;
        for (int i = 0; i < newsSentences.length; i++) {
            ArrayList<String> sentenceTokens = getTokensBrief(newsSentences[i]);
            if (sentenceCounter == maxSentence)
                break;
            loop:
            for (int j = 0; j < queryTokens.size(); j++) {
                for (int k = 0; k < sentenceTokens.size(); k++) {
                    if (queryTokens.get(j).equals(sentenceTokens.get(k)) && queryTokens.get(j) != null) {
                        score[i]++;
                    }
                }
            }
        }
        int maxIndex = 0;
        int maxScore = 0;
        for (int i = 0; i < score.length; i++) {
            if (score[i] > maxScore) {
                maxIndex = i;
                maxScore = score[i];
            }
        }
        brief = newsSentences[maxIndex] + "...";
        if (brief.equals("") && newsSentences.length > 0) {
            brief = brief + "..." + newsSentences[0];
            System.out.println("LOG NO BRIEF IN RESULT LIST ACTIVITY");
        }
        return brief;
    }

    public String NumToPersian(String a) {
        String[] pNum = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
        a = a.replace("0", pNum[0]);
        a = a.replace("1", pNum[1]);
        a = a.replace("2", pNum[2]);
        a = a.replace("3", pNum[3]);
        a = a.replace("4", pNum[4]);
        a = a.replace("5", pNum[5]);
        a = a.replace("6", pNum[6]);
        a = a.replace("7", pNum[7]);
        a = a.replace("8", pNum[8]);
        a = a.replace("9", pNum[9]);
        return a;
    }

    /* test similar news
        News news1 = new News("25-Jan-2020 6:49 PM", "نوکیا از بازار رفت", "isna.ir", "the news is everything is terrible", "", "So today, like always, everything still was terrible!", "");
        News news2 = new News("25-Jan-2020 6:49 PM", "آیفون جدید خیلی گرونه همه هم میگن", "isna.ir", "the news is everything is terrible", "", "So today, like always, everything still was terrible!", "");
        News news3 = new News("25-Jan-2020 6:49 PM", "سامسونگ ولی همون معمولی همیشگیه", "isna.ir", "the news is everything is terrible", "", "So today, like always, everything still was terrible!", "");
        news1.addSimilarNews(news2);
        news1.addSimilarNews(news3);
        news1.addSimilarNews(news1);
        news1.addSimilarNews(news3);
        news1.addSimilarNews(news3);
        news1.addSimilarNews(news3);
        news2.addSimilarNews(news3);
        resultNews = new ArrayList<>();
        resultNews.add(news1);
        resultNews.add(news2);
        resultNews.add(news3);
     */
}