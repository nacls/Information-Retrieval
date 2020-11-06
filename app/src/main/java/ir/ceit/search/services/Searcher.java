package ir.ceit.search.services;

import java.util.ArrayList;
import java.util.HashMap;

import ir.ceit.search.model.Dictionary;
import ir.ceit.search.model.DocInfo;
import ir.ceit.search.model.DocListElement;
import ir.ceit.search.model.Heap;
import ir.ceit.search.model.Node;
import ir.ceit.search.model.SimilarNews;
import ir.ceit.search.model.clustering.Cluster;
import ir.ceit.search.model.clustering.Seed;

public class Searcher {
    private DocListElement docList;
    private Dictionary dictionary;
    private Heap docHeap;
    private static int k = 50;
    private static double threshold = 0.5;
    private int similarSeedIndex;
    private QueryHandler queryHandler;
    private Cluster cluster;
    private String category;
    private ArrayList<DocInfo> docInfo;
    private ArrayList<SimilarNews> results;


    public ArrayList<SimilarNews> getResults() {
        return results;
    }


    public Searcher(Dictionary dictionary, Cluster cluster, ArrayList<DocInfo> docInfo) {
        this.docList = null;
        this.dictionary = dictionary;
        this.cluster = cluster;
        this.similarSeedIndex = -1;
        this.docInfo = docInfo;
        results = new ArrayList<>();
    }

    public void searchQuery(String query) {
        results.clear();
        this.queryHandler = new QueryHandler(query);
        queryHandler.tokenize();
        this.docList = new DocListElement(1);
        DocListElement currentDoc = this.docList;
        for (int i = 2; i < dictionary.getDocSize(); i++) {
            DocListElement docListElement = new DocListElement(i);
            currentDoc.setNextDoc(docListElement);
            currentDoc = currentDoc.getNextDoc();
        }
        if (queryHandler.getNotQueries().size() != 0) {
            searchNotQueries();
        }
        if (queryHandler.getQuotedQueries().size() != 0) {
            searchQuotedQueries();
        }
        if (queryHandler.getQueries().size() != 0) {
            searchSimpleQueries();
        }
        if (!queryHandler.getCategory().equals("")) {
            searchCatQueries();
        }
        if(queryHandler.getQueries().size() == 0)
        {
            returnDocElements();
        }
        else
        {
            findSimilarNews(similarSeedIndex, this.docList);
        }
    }

    private HashMap<Node, Integer> calculateSimpleQueryVectorSpace() {
        HashMap<Node, Integer> simpleQueryVectorSpace = new HashMap<>();
        for (String word : queryHandler.getQueries().keySet()) {
            int tf = queryHandler.getQueries().get(word);
            Node node = dictionary.searchWord(word);
            if (node.getWord() != "") {
                simpleQueryVectorSpace.put(node, tf);
            }
        }
        return simpleQueryVectorSpace;
    }

    private int findSimpleQueryMostSimilarCluster(HashMap<Node, Integer> simpleQueryVectorSpace) {
        double maxCosineSimilarity = 0;
        int mostSimilarClusterIndex = 0;
        for(int index=0; index<cluster.getSeeds().size(); index++)
        {
            if(cluster.getSeeds().get(index).getDocumentIDs().size() > cluster.getSeeds().get(mostSimilarClusterIndex).getDocumentIDs().size())
            {
                mostSimilarClusterIndex = index;
            }
        }
        System.out.println("GET SEED SIZE: " + cluster.getSeeds().size());
        for (int i = 0; i < cluster.getSeeds().size(); i++) {
            Seed seed = cluster.getSeeds().get(i);
            double cosineSimilarity = 0;
            double queryVectorSize = 0;
            double seedVectorSize = 0;
            for (Node node : seed.getVectorSpace().keySet()) {
                seedVectorSize += Math.pow(seed.getVectorSpace().get(node), 2);
                if (simpleQueryVectorSpace.containsKey(node)) {
                    cosineSimilarity += seed.getVectorSpace().get(node) * simpleQueryVectorSpace.get(node) * Math.log10((double) dictionary.getDocSize() / node.getDf());
                    queryVectorSize += Math.pow((1 + Math.log10(simpleQueryVectorSpace.get(node))) * Math.log10((double) dictionary.getDocSize() / node.getDf()), 2);

                }
            }
            for (Node node : simpleQueryVectorSpace.keySet()) {
                if (!seed.getVectorSpace().containsKey(node)) {
                    queryVectorSize += Math.pow((1 + Math.log10(simpleQueryVectorSpace.get(node))) * Math.log10((double) dictionary.getDocSize() / node.getDf()), 2);
                }
            }
            cosineSimilarity /= Math.sqrt(seedVectorSize) * Math.sqrt(queryVectorSize);
            System.out.println("LOG cosine similarity: " + cosineSimilarity);
            if (cosineSimilarity > maxCosineSimilarity) {
                mostSimilarClusterIndex = i;
            }
        }
        return mostSimilarClusterIndex;
    }

    private double calculateSimilarity(HashMap<Node, Integer> simpleQueryVectorSpace, HashMap<Node, Integer> docVectorSpace) {
        double cosineSimilarity = 0;
        double queryVectorSize = 0;
        double seedVectorSize = 0;
        for (Node node : docVectorSpace.keySet()) {
            seedVectorSize += Math.pow((1 + Math.log10(docVectorSpace.get(node))) * Math.log10((double) dictionary.getDocSize() / node.getDf()), 2);
            if (simpleQueryVectorSpace.containsKey(node)) {
                cosineSimilarity += (1 + Math.log10(docVectorSpace.get(node))) * Math.log10((double) dictionary.getDocSize() / node.getDf()) * simpleQueryVectorSpace.get(node) * Math.log10((double) dictionary.getDocSize() / node.getDf());
                queryVectorSize += Math.pow((1 + Math.log10(simpleQueryVectorSpace.get(node))) * Math.log10((double) dictionary.getDocSize() / node.getDf()), 2);

            }
        }
        for (Node node : simpleQueryVectorSpace.keySet()) {
            if (!docVectorSpace.containsKey(node)) {
                queryVectorSize += Math.pow((1 + Math.log10(simpleQueryVectorSpace.get(node))) * Math.log10((double) dictionary.getDocSize() / node.getDf()), 2);
            }
        }
        System.out.println("LOG query vector size: " + queryVectorSize);
        System.out.println("LOG seed vector size: " + seedVectorSize);
        if (queryVectorSize == 0) {
            queryVectorSize = 0.00001;
        }
        if (seedVectorSize == 0) {
            seedVectorSize = 0.00001;
        }
        cosineSimilarity /= Math.sqrt(seedVectorSize) * Math.sqrt(queryVectorSize);
        return cosineSimilarity;
    }

    private void calculateClusterDocSimilarities(HashMap<Node, Integer> simpleQueryVectorSpace, int seedIndex) {
        Seed seed1 = cluster.getSeeds().get(seedIndex);
        for (Integer docID : seed1.getDocumentIDs()) {
            HashMap<Node, Integer> docVectorSpace = dictionary.getDocList().get(docID);

            this.docHeap.getDocHeap().get(docID).setSimilarity(calculateSimilarity(simpleQueryVectorSpace, docVectorSpace));
        }
    }

    private void searchSimpleQueries() {
        HashMap<Node, Integer> simpleQueryVectorSpace = calculateSimpleQueryVectorSpace();
        this.docHeap = new Heap(dictionary.getDocHeap());
        similarSeedIndex = findSimpleQueryMostSimilarCluster(simpleQueryVectorSpace);
        calculateClusterDocSimilarities(simpleQueryVectorSpace, similarSeedIndex);
        docHeap.removeZeroSimilarities();
        docHeap.maxHeapIFy();
        ArrayList<Integer> docs = new ArrayList<>();
        DocListElement docHead = this.docList;
        while (docHead != null) {
            docs.add(docHead.getDocID());
            docHead = docHead.getNextDoc();
        }
        docHead = new DocListElement(0);
        DocListElement currentDoc = docHead;
        int results = 0;
        while (results < docs.size() && results < docHeap.getDocHeap().size() - 1 && results < this.k && results < docHeap.getDocHeap().size()) {
            DocListElement docListElement = new DocListElement(docHeap.extractMax().getDocID());
            if (docs.contains(docListElement.getDocID())) {
                currentDoc.setNextDoc(docListElement);
                currentDoc = docListElement;
                results++;
            }
        }
        if (results < docHeap.getDocHeap().size() - 1 && results < k && results < docHeap.getDocHeap().size()) {
            while (results < k && results < docHeap.getDocHeap().size()) {
                DocListElement docListElement = new DocListElement(docHeap.extractMax().getDocID());
                currentDoc.setNextDoc(docListElement);
                currentDoc = docListElement;
                results++;
            }
        }
        this.docList = docHead.getNextDoc();
    }

    private void searchNotQueries() {
        ArrayList<String> notQueries = queryHandler.getNotQueries();
        for (String word : notQueries) {
            Node node = dictionary.searchWord(word);
            intersectWithNot(node.getDocHead());
        }
    }

    private void intersectWithNot(DocListElement docList) {
        DocListElement currentDocElement = this.docList;
        DocListElement previousDocElement = null;
        while (currentDocElement != null && docList != null) {
            if (currentDocElement.getDocID() == docList.getDocID()) {
                if (previousDocElement == null) {
                    this.docList = this.docList.getNextDoc();
                    currentDocElement = currentDocElement.getNextDoc();
                } else {
                    previousDocElement.setNextDoc(currentDocElement.getNextDoc());
                    currentDocElement = currentDocElement.getNextDoc();
                }
                docList = docList.getNextDoc();
            } else if (currentDocElement.getDocID() < docList.getDocID()) {
                previousDocElement = currentDocElement;
                currentDocElement = currentDocElement.getNextDoc();
            } else {
                docList = docList.getNextDoc();
            }
        }
    }

    public void searchCatQueries() {
        DocListElement docListElement = this.docList;
        DocListElement previousDoc = null;
        while (docListElement != null) {
            boolean flag = true;
            System.out.println("LOG doclist element: " + docListElement.getDocID() + ", category:  " + docInfo.get(docListElement.getDocID()).getCategory());
            if (!docInfo.get(docListElement.getDocID()).getCategory().equals(queryHandler.getCategory())) {
                if (previousDoc == null) {
                    this.docList = this.docList.getNextDoc();
                    docListElement = docListElement.getNextDoc();
                } else {
                    flag = false;
                    previousDoc.setNextDoc(docListElement.getNextDoc());
                    docListElement = docListElement.getNextDoc();
                }
            }
            if (flag) {
                previousDoc = docListElement;
            }
            if (docListElement != null) {
                docListElement = docListElement.getNextDoc();
            }
        }
    }

    private void searchQuotedQueries() {
        ArrayList<ArrayList<String>> quotedQueries = queryHandler.getQuotedQueries();
        for (ArrayList<String> quotedQuery : quotedQueries) {
            DocListElement docListHead = null;
            DocListElement firstWordDocList = dictionary.searchWord(quotedQuery.get(0)).getDocHead();
            while (firstWordDocList != null) {
                int docID = firstWordDocList.getDocID();
                boolean positionsMatch = true;
                //for each word in a quoted query
                for (int index = 1; index < quotedQuery.size(); index++) {
                    boolean sameDocIDExists = false;
                    DocListElement wordDocList = dictionary.searchWord(quotedQuery.get(index)).getDocHead();
                    while (wordDocList != null) {
                        if (wordDocList.getDocID() == docID) {
                            sameDocIDExists = true;
                            ArrayList<Integer> firstWordDocPositionList = firstWordDocList.getPositionList();
                            boolean matchedPositionsExists = false;
                            for (int position : firstWordDocPositionList) {
                                if (wordDocList.getPositionList().contains(position + index)) {
                                    matchedPositionsExists = true;
                                }
                            }
                            if (!matchedPositionsExists) {
                                positionsMatch = false;
                            }
                        }
                        wordDocList = wordDocList.getNextDoc();
                    }
                    if (!sameDocIDExists) {
                        positionsMatch = false;
                    }
                }
                if (positionsMatch) {
                    DocListElement newDoc = new DocListElement(docID);
                    DocListElement currentDoc = docListHead;
                    if (currentDoc == null) {
                        docListHead = newDoc;
                    } else {
                        while (currentDoc.getNextDoc() != null) {
                            currentDoc = currentDoc.getNextDoc();
                        }
                        currentDoc.setNextDoc(newDoc);
                    }
                }
                firstWordDocList = firstWordDocList.getNextDoc();
            }
            intersect(docListHead);
        }
    }

    private void intersect(DocListElement docList) {
        if (docList == null) {
            this.docList = null;
            return;
        }
        DocListElement currentDocElement = this.docList;
        DocListElement previousDocElement = null;
        while (currentDocElement != null && docList != null) {
            if (currentDocElement.getDocID() == docList.getDocID()) {
                previousDocElement = currentDocElement;
                currentDocElement = currentDocElement.getNextDoc();
                docList = docList.getNextDoc();
            } else if (currentDocElement.getDocID() < docList.getDocID()) {
                if (previousDocElement == null) {
                    this.docList = this.docList.getNextDoc();
                } else {
                    previousDocElement.setNextDoc(currentDocElement.getNextDoc());
                }
                currentDocElement = currentDocElement.getNextDoc();
            } else {
                docList = docList.getNextDoc();
            }
        }
        if (currentDocElement != null) {
            if (previousDocElement != null) {
                previousDocElement.setNextDoc(null);
            }
        }
    }

    public void returnDocElements()
    {
        ArrayList<Integer> docIDs = new ArrayList();
        DocListElement doc = this.docList;
        while(doc != null)
        {
            docIDs.add(doc.getDocID());
            doc = doc.getNextDoc();
        }
        DocListElement docListElement = this.docList;
        while(docListElement != null)
        {
            HashMap<Node, Integer> docVectorSpace = dictionary.getDocList().get(docListElement.getDocID());
            SimilarNews news = new SimilarNews(docListElement);
            for (Integer docID : docIDs) {
                if(docID != docListElement.getDocID())
                {
                    double cosineSimilarity = calculateSimilarity(docVectorSpace, dictionary.getDocList().get(docID));
                    if (cosineSimilarity > threshold && docID != docListElement.getDocID()) {
                        news.getSimilarNews().add(docID);
                    }
                }
            }
            docListElement = docListElement.getNextDoc();
            results.add(news);
        }

    }


    public void findSimilarNews(int seedIndex, DocListElement docListElement) {
        while (docListElement != null) {
            HashMap<Node, Integer> docVectorSpace = dictionary.getDocList().get(docListElement.getDocID());
            SimilarNews news = new SimilarNews(docListElement);
            for (Integer docID : cluster.getSeeds().get(seedIndex).getDocumentIDs()) {
                double cosineSimilarity = calculateSimilarity(docVectorSpace, dictionary.getDocList().get(docID));
                if (cosineSimilarity > threshold && docID != docListElement.getDocID()) {
                    news.getSimilarNews().add(docID);
                }
            }
            results.add(news);
            docListElement = docListElement.getNextDoc();
        }
    }
}
