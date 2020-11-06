package ir.ceit.search.model.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import ir.ceit.search.model.DocInfo;
import ir.ceit.search.model.Node;

public class Cluster {
    private int dictionarySize;
    private int k;
    private static int iterationTotal = 3;
    private Vector<HashMap<Node, Integer>> docList;
    private Vector<Seed> seeds;


    public Vector<Seed> getSeeds() {
        return seeds;
    }


    public Cluster(int dictionarySize, int k, Vector<HashMap<Node, Integer>> docList) {
        this.dictionarySize = dictionarySize;
        this.k = k;
        this.docList = new Vector<>(docList);
        this.seeds = new Vector<>();
    }

    private void initiateSeedsN() {
        for (int i = 0; i < k; i++) {
            int index = seeds.get(i).getDocumentIDs().get(0);
            HashMap<Node, Double> centroidVectorSpace = new HashMap<>();
            for (Node node : this.docList.get(index).keySet()) {
                double tf_idf = (1 + (Math.log10(docList.get(index).get(node)))) * Math.log10((double) this.dictionarySize / node.getDf());
                centroidVectorSpace.put(node, tf_idf);
            }
            seeds.get(i).setVectorSpace(centroidVectorSpace);
        }
    }

    public void runKMeansN(ArrayList<DocInfo> docInfos) {
        for(int i=0; i<5; i++)
        {
//            this.seeds.add(new Seed());
        }
        int social = 0;
        int multi = 0;
        int politics = 0;
        int sport = 0;
        int economy = 0;
        for (int i = 0; i < docList.size(); i++) {
            if (docInfos.get(i).getCategory().equals("science") || docInfos.get(i).getCategory().equals("social")) {
                social ++;
                seeds.get(0).getDocumentIDs().add(i);
            } else if (docInfos.get(i).getCategory().equals("culture-art") || docInfos.get(i).getCategory().equals("multimedia")) {
                multi ++;
                seeds.get(1).getDocumentIDs().add(i);
            } else if (docInfos.get(i).getCategory().equals("politics") || docInfos.get(i).getCategory().equals("international")) {
                politics ++;
                seeds.get(2).getDocumentIDs().add(i);
            } else if (docInfos.get(i).getCategory().equals("sport")) {
                sport ++;
                seeds.get(3).getDocumentIDs().add(i);
            } else {
                economy ++;
                seeds.get(4).getDocumentIDs().add(i);
            }
        }
        System.out.println("social: " + social);
        System.out.println("multi: " + multi);
        System.out.println("politics: " + politics);
        System.out.println("sport: " + sport);
        System.out.println("economy: " + economy);
        initiateSeeds();
        for (int i = 0; i < k; i++) {
            HashMap<Node, Double> newCentroidVectorSpace = new HashMap<>();
            for (Integer docID : seeds.get(i).getDocumentIDs()) {
                HashMap<Node, Integer> docVectorSpace = docList.get(docID);
                for (Node node : docVectorSpace.keySet()) {
                    if (!newCentroidVectorSpace.containsKey(node)) {
                        double tf_idf = (1 + Math.log10(docVectorSpace.get(node))) * Math.log10(node.getDf());
                        newCentroidVectorSpace.put(node, tf_idf / seeds.get(i).getDocumentIDs().size());
                    } else {
                        double tf_idf = newCentroidVectorSpace.get(node) * seeds.get(i).getDocumentIDs().size();
                        tf_idf = tf_idf + (1 + Math.log10(docVectorSpace.get(node))) * Math.log10(node.getDf());
                        newCentroidVectorSpace.replace(node, tf_idf / seeds.get(i).getDocumentIDs().size());
                    }
                }
            }
            seeds.get(i).setVectorSpace(newCentroidVectorSpace);
        }
        System.out.println(RSS());
    }

    private void initiateSeeds() {
        int partition = docList.size() / this.k;
        int random = ((int) (Math.random() * 100000)) % partition;
        for (int i = 0; i < k; i++) {
            int index = partition * i + random;
            HashMap<Node, Double> centroidVectorSpace = new HashMap<>();
            for (Node node : this.docList.get(index).keySet()) {
                double tf_idf = (1 + (Math.log10(docList.get(index).get(node)))) * Math.log10((double) this.dictionarySize / node.getDf());
                centroidVectorSpace.put(node, tf_idf);
            }
            this.seeds.add(new Seed(centroidVectorSpace));
        }
    }

    public void runKMeans() {
        initiateSeeds();
        for (int iteration = 0; iteration < iterationTotal; iteration++) {
            System.out.println("iteration " + iteration);
            for (int i = 0; i < docList.size(); i++) {
                HashMap<Node, Integer> docVectorSpace = docList.get(i);
                ArrayList<Double> distances = new ArrayList<>();
                for (int j = 0; j < k; j++) {
                    if (seeds.get(j).getDocumentIDs().contains(i)) {
                        seeds.get(j).getDocumentIDs().remove((new Integer(i)));
                    }
                    HashMap<Node, Double> centroidVectorSpace = seeds.get(j).getVectorSpace();
                    double distance = calculateDistance(centroidVectorSpace, docVectorSpace);
                    distances.add(distance);
                }
                int nearestCentroidIndex = 0;
                for (int index = 1; index < k; index++) {
                    if (distances.get(index) < distances.get(nearestCentroidIndex)) {
                        nearestCentroidIndex = index;
                    }
                }
                seeds.get(nearestCentroidIndex).getDocumentIDs().add(i);
            }
            //find centroids
            for (int i = 0; i < k; i++) {
                HashMap<Node, Double> newCentroidVectorSpace = new HashMap<>();
                for (Integer docID : seeds.get(i).getDocumentIDs()) {
                    HashMap<Node, Integer> docVectorSpace = docList.get(docID);
                    for (Node node : docVectorSpace.keySet()) {
                        if (!newCentroidVectorSpace.containsKey(node)) {
                            double tf_idf = (1 + Math.log10(docVectorSpace.get(node))) * Math.log10(node.getDf());
                            newCentroidVectorSpace.put(node, tf_idf / seeds.get(i).getDocumentIDs().size());
                        } else {
                            double tf_idf = newCentroidVectorSpace.get(node) * seeds.get(i).getDocumentIDs().size();
                            tf_idf = tf_idf + (1 + Math.log10(docVectorSpace.get(node))) * Math.log10(node.getDf());
                            newCentroidVectorSpace.replace(node, tf_idf / seeds.get(i).getDocumentIDs().size());
                        }
                    }
                }
                seeds.get(i).setVectorSpace(newCentroidVectorSpace);
            }
            System.out.println(RSS());
        }
        for (Seed seed : seeds) {
            System.out.println("&");
            ;
        }
    }

    private double calculateDistance(HashMap<Node, Double> v1, HashMap<Node, Integer> v2) {
        double distance = 0;
        for (Node node : v1.keySet()) {
            double tf_idf = v1.get(node);
            if (v2.containsKey(node)) {
                int tf = v2.get(node);
                distance += Math.pow((1 + Math.log10(tf)) * Math.log10((double) this.dictionarySize / node.getDf()) - tf_idf, 2);
            } else {
                distance += Math.pow(tf_idf, 2);
            }
        }
        for (Node node : v2.keySet()) {
            double tf_idf = (1 + Math.log10(v2.get(node))) * Math.log10((double) this.dictionarySize / node.getDf());
            if (!v1.containsKey(node)) {
                distance += Math.pow(tf_idf, 2);
            }
        }
        return Math.sqrt(distance);
    }

    public double RSS() {
        double distance = 0;
        for (Seed seed : seeds) {
            for (Integer docID : seed.getDocumentIDs()) {
                distance += Math.pow(calculateDistance(seed.getVectorSpace(), docList.get(docID)), 2);
            }
        }
        return distance;
    }
}
