/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.model;

import java.io.Serializable;

/**
 *
 * @author Gonzalo
 */
public class VocabularyEntry implements Serializable, Comparable<VocabularyEntry> {

    private final String term;
    private final int postFile;
    private int tf;
    private int nr;

    public int getPostFile() {
        return postFile;
    }

    public VocabularyEntry(String term, int file) {
        this.term = term;
        tf = 1;
        nr = 1;
        postFile = file;

    }

    public String getTerm() {
        return term;
    }

    public void addTermOcurrance() {
        tf++;
    }

    public int getTf() {
        return tf;
    }

    public int getNr() {
        return nr;
    }

    public void updateNrValue(PostList postList){
        this.nr = postList.getNr();
    }

    @Override
    public int compareTo(VocabularyEntry otherEntry) {
        return (this.getNr()< otherEntry.getNr()? -1 : 1);
    }
}
