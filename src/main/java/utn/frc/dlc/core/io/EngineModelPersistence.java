package utn.frc.dlc.core.io;

import utn.frc.dlc.core.engine.EngineModel;
import utn.frc.dlc.core.io.management.DocumentManagement;
import utn.frc.dlc.core.io.management.DocumentMapManagement;
import utn.frc.dlc.core.io.management.VocabularyManagement;
import utn.frc.dlc.core.model.Document;

import java.io.File;

public class EngineModelPersistence {

    private final EngineModel engineModel;

    public EngineModelPersistence(EngineModel engineModel) {
        this.engineModel = engineModel;
    }

    public void persistEngineModel(){
        persistVocabulary();
        persistDocMap();
    }

    private void persistVocabulary() {
        VocabularyManagement.getInstance().saveVocabulary(engineModel.getVocabulary());
    }

    private void persistDocMap() {
        DocumentMapManagement.getInstance().saveDocumentMap(engineModel.getDocMap());
    }

    public void persistDocument(File file, int docID) {
        new DocumentPersistingThread(file, docID).start();
    }

    private class DocumentPersistingThread extends Thread {

        private Document doc;

        public DocumentPersistingThread(File file, int docID) {
            doc = new Document(docID, file.getName());

        }

        @Override
        public void run() {
            DocumentManagement.getInstance().saveDocument(doc);
        }
    }
}
