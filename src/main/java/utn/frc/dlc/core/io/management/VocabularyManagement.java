/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.io.management;

import utn.frc.dlc.core.common.Environment;
import utn.frc.dlc.core.io.util.ObjectReader;
import utn.frc.dlc.core.io.util.ObjectWriter;
import utn.frc.dlc.core.model.VocabularyEntry;

import java.util.Map;

/**
 * @author Gonzalo
 */
public class VocabularyManagement {

    private static final String VOCABULARY_FILE_PATH = Environment.getPathOfWorkspace() + "/vocabulary.dlc";
    private static VocabularyManagement instance;

    private VocabularyManagement() {

    }

    public static VocabularyManagement getInstance() {
        if (instance == null) {
            instance = new VocabularyManagement();
        }
        return instance;
    }

    public Map<String, VocabularyEntry> getVocabulary() {
        ObjectReader<Map<String, VocabularyEntry>> or = new ObjectReader<>();
        return or.read(VOCABULARY_FILE_PATH);
    }

    public void saveVocabulary(Map<String, VocabularyEntry> vocabulary) {
        ObjectWriter<Map<String, VocabularyEntry>> ow = new ObjectWriter<>();
        ow.write(vocabulary, VOCABULARY_FILE_PATH);
    }

}
