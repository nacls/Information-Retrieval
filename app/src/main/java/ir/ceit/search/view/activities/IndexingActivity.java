package ir.ceit.search.view.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;

import htz.ir.stemming.PersianStemmer;
import ir.ceit.search.R;
import ir.ceit.search.SubApplication;
import ir.ceit.search.model.Dictionary;
import ir.ceit.search.model.DocInfo;
import ir.ceit.search.model.Verb;
import ir.ceit.search.model.clustering.Cluster;
import ir.ceit.search.nlp.Processor;
import ir.ceit.search.nlp.Stemmer;
import ir.ceit.search.nlp.Tokenizer;
import ir.ceit.search.services.Searcher;
import ir.ceit.search.services.TextCleaner;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class IndexingActivity extends AppCompatActivity {
    private Dictionary dictionary;
    private ArrayList<Verb> verbs;
    private ArrayList<DocInfo> docInfo;
    private String[] paths = SubApplication.getPaths();
    private String verbPath = "verbs.xls";
    private TextView tv;
    private LinearLayout linearLayout;
    private Cluster cluster;
    private TextView tv2;
    private int docSize;
    //heap's law
    private int halfTokenNum = 0;
    private int halfVocabularySize = 0;
    private int tokenNum = 0;
    private int vocabularySize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indexing);

        tv = (TextView) findViewById(R.id.buildingDic);
        tv.setText("Processing...");

        tv2 = findViewById(R.id.tv2);
        tv2.setText("Processed docs: " + 0);

        linearLayout = (LinearLayout) findViewById(R.id.loadingPanel);
        linearLayout.setVisibility(View.VISIBLE);

        verbs = new ArrayList<>();
        dictionary = new Dictionary();
        docInfo = new ArrayList<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv2.setText("Processed docs: "+docSize);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();

        new Thread() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                buildVerbDictionary();
                buildDictionary();
                System.out.println("LOG DIC SIZE: " + dictionary.getDocSize());
                cluster();
                Searcher searcher = new Searcher(dictionary, cluster, docInfo);
                SubApplication.setSearcher(searcher);
                long endTime = System.currentTimeMillis();
                System.out.println("Duration of building dictionaries: " + (endTime - startTime) / 60000 + " minutes");
                openQueryActivity();
            }
        }.start();

    }

    public void cluster() {
        cluster = new Cluster(dictionary.getDocSize(), 5, dictionary.getDocList());
        cluster.runKMeans();
        SubApplication.setCluster(cluster);
    }

    public void buildDictionary() {
        dictionary.addDocHeapElement(0);
        dictionary.addDocList();
        DocInfo docInfoFirst = new DocInfo(0, "first", "nothing", 0);
        docInfo.add(docInfoFirst);
        docSize = 1;
        for (int n = 0; n < paths.length; n++) {
            AssetManager am = getAssets();
            try {
                //
                InputStream is = am.open(paths[n]);
                Workbook wb = Workbook.getWorkbook(is);
                Sheet sheet = wb.getSheet(0);
                int rowNum = sheet.getRows();
                int colNum = sheet.getColumns();
                int contentColNum = 0;
                int categoryColNum = 0;
                for (int i = 0; i < colNum; i++) {
                    Cell temp = sheet.getCell(i, 0);
                    if (temp.getContents().equals("content")) {
                        contentColNum = i;
                    } else if (temp.getContents().equals("category")) {
                        categoryColNum = i;
                    }
                }
                //going through the content cell
                for (int i = 1; i < rowNum; i++) {
                    dictionary.addDocHeapElement(docSize);
                    dictionary.addDocList();
                    dictionary.setDocSize(dictionary.getDocSize() + 1);
                    Cell cellToBeRead = sheet.getCell(contentColNum, i);
                    Cell categoryCell = sheet.getCell(categoryColNum, i);
                    String category = categoryCell.getContents();
                    DocInfo docInfoTemp = new DocInfo(docSize, paths[n], category, i);
                    docInfo.add(docInfoTemp);
                    String cellContent = cellToBeRead.getContents();
                    String content = cleanupText(cellContent, "&");
                    ArrayList<String> tokens = getTokens(content);
                    for (int index = 0; index < tokens.size(); index++) {
                        dictionary.addWord(dictionary.getRoot(), tokens.get(index), docSize, index);
                    }
                    docSize++;
                }
                wb.close();
            } catch (Exception e) {
                System.out.println("ERROR IN BUILD DICTIONARY: " + e);
                e.printStackTrace();
            }

        }
        System.out.println("Dictionary is built");
        SubApplication.setDictionary(dictionary);
        SubApplication.setDocInfo(docInfo);
    }

    public void calculateHeapLaw() {
        double b = 0;
        double k = 0;
        System.out.println("vocab size: " + vocabularySize + ", half vocab: " + halfVocabularySize + ", token num: " + tokenNum + ", half token num: " + halfTokenNum);
        b = Math.log((double) vocabularySize / (double) halfVocabularySize) / Math.log((double) tokenNum / (double) halfTokenNum);
        k = vocabularySize / Math.pow(tokenNum, b);
        System.out.println("LOG HEAP's LAW: b is " + b + " and k is " + k);
        /*if (i < halfRowNum) {
            halfTokenNum++;
            tokenNum++;
            if (dictionary.searchWord(tokens.get(index)).getWord() == "") {
                halfVocabularySize++;
                vocabularySize++;
            }
        }
        if (i >= halfRowNum) {
            tokenNum++;
            if (dictionary.searchWord(tokens.get(index)).getWord().equals("")) {
                vocabularySize++;
            }
        }*/
    }

    public void calculateZipfLaw(int[] frequencies) {
        double[] ranksLog = new double[frequencies.length];
        for (int i = 0; i < ranksLog.length; i++) {
            ranksLog[i] = Math.log(i + 1);
        }
        double[] frequenciesLog = new double[frequencies.length];
        for (int i = 0; i < frequenciesLog.length; i++) {
            frequenciesLog[i] = Math.log(frequencies[i]);
        }
    }

    public void buildVerbDictionary() {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open(verbPath);
            Workbook wb = Workbook.getWorkbook(is);
            Sheet sheet = wb.getSheet(0);
            int rowNum = sheet.getRows();
            int colNum = sheet.getColumns();
            int pastColNum = 0;
            int presentColNum = 0;
            for (int i = 0; i < colNum; i++) {
                Cell temp = sheet.getCell(i, 0);
                if (temp.getContents().equals("past")) {
                    pastColNum = i;
                    break;
                }
            }
            for (int i = 0; i < colNum; i++) {
                Cell temp = sheet.getCell(i, 0);
                if (temp.getContents().equals("present")) {
                    presentColNum = i;
                    break;
                }
            }
            for (int i = 1; i < rowNum; i++) {
                Cell presentCell = sheet.getCell(presentColNum, i);
                Cell pastCell = sheet.getCell(pastColNum, i);
                String pastVerb = pastCell.getContents();
                String presentVerb = presentCell.getContents();
                Verb verbToBeAdded = new Verb(pastVerb, presentVerb);
                verbs.add(verbToBeAdded);
            }
            System.out.println("Verb dictionary is built");
            SubApplication.setVerbs(verbs);
        } catch (Exception e) {
            System.out.println("ERROR IN BUILD VERB DICTIONARY: " + e);
        }
    }

    public ArrayList<String> getTokens(String input) throws Exception {
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
            if (processor.isStopWord(tokens.get(i))) {
                tokens.remove(i);
                i--;
            }
        }
        return tokens;
    }

    public String cleanupText(String string, String charsToDelete) {
        TextCleaner textCleaner = new TextCleaner();
        return textCleaner.removeChars(textCleaner.removeHTMLTags(textCleaner.removeWords(string)), charsToDelete);
    }

    public void openQueryActivity() {
        //this opens the query activity
        Intent intent = new Intent(this, QueryActivity.class);
        startActivity(intent);
    }
}
