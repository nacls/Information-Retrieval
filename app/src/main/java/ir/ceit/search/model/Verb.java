package ir.ceit.search.model;

public class Verb {
    private String pastTense;
    private String presentTense;

    public Verb(String pastTense, String presentTense) {
        this.pastTense = pastTense;
        this.presentTense = presentTense;
    }

    public String getPastTense() {
        return pastTense;
    }

    public String getPresentTense() {
        return presentTense;
    }

    public void setPastTense(String pastTense) {
        this.pastTense = pastTense;
    }

    public void setPresentTense(String presentTense) {
        this.presentTense = presentTense;
    }
}
