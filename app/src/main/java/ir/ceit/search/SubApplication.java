package ir.ceit.search;

import android.app.Application;

import java.util.ArrayList;

import ir.ceit.search.model.Dictionary;
import ir.ceit.search.model.DocInfo;
import ir.ceit.search.model.Verb;
import ir.ceit.search.model.clustering.Cluster;
import ir.ceit.search.services.Searcher;

public class SubApplication extends Application {
    static Dictionary dictionary;
    static ArrayList<Verb> verbs;
    static Searcher searcher;
    static ArrayList<DocInfo> docInfo;
    static Cluster cluster;
    //IR-F19-Project01-Input-2k
    //Book1
    //,"ir-news-8-10-labeled.xls"
    static String[] paths = new String[]{"IR-F19-Project02-14k-labeled.xls"};

    public static Cluster getCluster() {
        return cluster;
    }

    public static void setCluster(Cluster cluster) {
        synchronized (SubApplication.class) {
            SubApplication.cluster = cluster;
        }
    }

    public static ArrayList<DocInfo> getDocInfo() {
        return docInfo;
    }

    public static void setDocInfo(ArrayList<DocInfo> docInfo) {
        synchronized (SubApplication.class) {
            SubApplication.docInfo = docInfo;
        }
    }

    public static String[] getPaths() {
        return paths;
    }

    public static ArrayList<Verb> getVerbs() {
        return verbs;
    }

    public static void setVerbs(ArrayList<Verb> verbs) {
        synchronized (SubApplication.class) {
            SubApplication.verbs = verbs;
        }
    }

    public static Searcher getSearcher() {
        return searcher;
    }

    public static void setSearcher(Searcher searcher) {
        synchronized (SubApplication.class) {
            SubApplication.searcher = searcher;
        }
    }

    public static Dictionary getDictionary() {
        return dictionary;
    }

    public static void setDictionary(Dictionary dictionary) {
        synchronized (SubApplication.class) {
            SubApplication.dictionary = dictionary;
        }
    }
}
