package ir.ceit.search.services;

import java.util.ArrayList;
import java.util.HashMap;

import htz.ir.stemming.PersianStemmer;
import ir.ceit.search.SubApplication;
import ir.ceit.search.model.DocListElement;
import ir.ceit.search.model.Verb;
import ir.ceit.search.nlp.CaseFolder;
import ir.ceit.search.nlp.Normalizer;
import ir.ceit.search.nlp.Processor;
import ir.ceit.search.nlp.Stemmer;
import ir.ceit.search.nlp.Tokenizer;

public class QueryHandler {
    private double queryVectorSize;
    private int maxT;
    private HashMap<String, Integer> queries;
    private ArrayList<Verb> verbs;
    private String query;
    private ArrayList<String> simpleQueries;
    private ArrayList<String> notQueries;

    public String getCategory() {
        return category;
    }

    private String category;
    private HashMap<String, String> catQueries;
    private ArrayList<String> sourceQueries;
    private ArrayList<ArrayList<String>> quotedQueries;
    private boolean parsingQuotation;

    public double getQueryVectorSize() {
        return queryVectorSize;
    }

    public void setQueryVectorSize(double queryVectorSize) {
        this.queryVectorSize = queryVectorSize;
    }

    public int getMaxT() {
        return maxT;
    }

    public HashMap<String, Integer> getQueries() {
        return queries;
    }

    public ArrayList<String> getSimpleQueries() {
        return simpleQueries;
    }

    public ArrayList<String> getNotQueries() {
        return notQueries;
    }

    public ArrayList<ArrayList<String>> getQuotedQueries() {
        return quotedQueries;
    }

    public HashMap<String, String> getCatQueries() {
        return catQueries;
    }

    public QueryHandler(String query) {
        this.query = query;
        queryVectorSize = 0;
        this.maxT = 1;
        this.queries = new HashMap<>();
        simpleQueries = new ArrayList<>();
        notQueries = new ArrayList<>();
        catQueries = new HashMap<>();
        category = "";
        sourceQueries = new ArrayList<>();
        quotedQueries = new ArrayList<>();
        this.verbs = SubApplication.getVerbs();
        this.parsingQuotation = false;
    }

    //this is redundant
    public void tokenizeQuery() {
        CaseFolder caseFolder = new CaseFolder();
        PersianStemmer persianStemmer = new PersianStemmer();
        String[] strings = this.query.split(" ");
        for (String string : strings) {
            if (string.charAt(0) == '\"') {
                parsingQuotation = true;
                ArrayList<String> quotedQuery = new ArrayList<>();
                if (string.charAt(string.length() - 1) == '\"') {
                    string = string.substring(0, string.length() - 1);
                    parsingQuotation = false;
                }
                quotedQuery.add(caseFolder.matchWords(persianStemmer.run(string.substring(1))));
                quotedQueries.add(quotedQuery);
            } else if (string.charAt(string.length() - 1) == '\"') {
                quotedQueries.get(quotedQueries.size() - 1).add(caseFolder.matchWords(persianStemmer.run(string.substring(0, string.length() - 1))));
                parsingQuotation = false;
            } else {
                if (parsingQuotation) {
                    quotedQueries.get(quotedQueries.size() - 1).add(caseFolder.matchWords(persianStemmer.run(string)));
                } else {
                    if (string.contains("source:")) {
                        sourceQueries.add(string.substring(7));
                    } else if (string.contains("cat:")) {
                        String[] s = string.split(":");
                        catQueries.put(s[1], s[2]);
                    } else if (string.charAt(0) == '!') {
                        notQueries.add(caseFolder.matchWords(persianStemmer.run(string.substring(1))));
                    } else {
                        simpleQueries.add(caseFolder.matchWords(persianStemmer.run(string)));
                    }
                }
            }
        }
    }

    public void tokenize() {
        ArrayList<String> tokens = getTokens(query);
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).startsWith("\"")) {
                parsingQuotation = true;
                ArrayList<String> quotedQuery = new ArrayList<>();
                if (tokens.get(i).endsWith("\"")) {
                    tokens.set(i, process(tokens.get(i).substring(0, tokens.get(i).length() - 1)));
                    parsingQuotation = false;
                }
                quotedQuery.add(tokens.get(i).substring(1));
                quotedQueries.add(quotedQuery);
            } else if (tokens.get(i).endsWith("\"")) {
                quotedQueries.get(quotedQueries.size() - 1).add(process(tokens.get(i).substring(0, tokens.get(i).length() - 1)));
                parsingQuotation = false;
            } else {
                if (parsingQuotation) {
                    quotedQueries.get(quotedQueries.size() - 1).add(tokens.get(i));
                } else {
                    if (tokens.get(i).equals("source:")) {
                        sourceQueries.add(tokens.get(i + 1));
                        i++;
                    } else if (tokens.get(i).equals("cat:")) {
//                        catQueries.put(tokens.get(i + 2), tokens.get(i + 1));
                        category = tokens.get(i + 1);
                        i++;
                    } else if (tokens.get(i).charAt(0) == '!') {
                        notQueries.add(process(tokens.get(i).substring(1)));
                    }
                    else
                    {
                        if (queries.containsKey(tokens.get(i))) {
                            int tf = queries.get(tokens.get(i));
                            queries.replace(tokens.get(i), tf + 1);
                            if(tf + 1 > maxT)
                            {
                                maxT = tf + 1;
                            }
                        } else {
                            queries.put(tokens.get(i), 1);
                        }
                        simpleQueries.add(tokens.get(i));
                    }
                }
            }
        }
        System.out.println("LOG TOKEN QUERIES START");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i));
        }
        System.out.println("LOG TOKEN QUERIES FINISH");

    }

    public String process(String input) {
        Stemmer stemmer = new Stemmer(verbs);
        PersianStemmer persianStemmer = new PersianStemmer();
        return persianStemmer.run(stemmer.stem(input));
    }

    //my tokenize
    public ArrayList<String> getTokens(String input) {
        ArrayList<String> tokens;
        Stemmer stemmer = new Stemmer(verbs);
        PersianStemmer persianStemmer = new PersianStemmer();
        Tokenizer tokenizer = new Tokenizer(stemmer);
        Normalizer normalizer = new Normalizer();
        Processor processor = new Processor();
        CaseFolder caseFolder = new CaseFolder();
        String cleanText = caseFolder.matchWords(normalizer.replaceLetters(input));
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

}
