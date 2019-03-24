package utn.frc.dlc.core.engine;


import utn.frc.dlc.core.model.VocabularyEntry;

import java.io.File;
import java.util.Map;

public class EngineModel {
    private final Map<String, VocabularyEntry> vocabulary;
    private final Map<String, Integer> docIdMap;

    public EngineModel(Map<String, VocabularyEntry> vocabulary, Map<String, Integer> docIdMap) {
        this.vocabulary = vocabulary;
        this.docIdMap = docIdMap;
    }

    public Map<String, VocabularyEntry> getVocabulary() {
        return vocabulary;
    }

    public VocabularyEntry getFromVocabulary(String term) {
        return getVocabulary().get(term);
    }

    public void addToVocabulary(VocabularyEntry ve) {
        getVocabulary().put(ve.getTerm(), ve);
    }

    public Map<String, Integer> getDocMap() {
        return docIdMap;
    }

    public Integer getFromDocMap(File file) {
        return getDocMap().get(file.getName());
    }

    public void addToDocMap(File file, int docID) {
        getDocMap().put(file.getName(), docID);
    }
}
