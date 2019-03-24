/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.io.management;


import org.apache.commons.lang3.StringUtils;
import utn.frc.dlc.core.common.Environment;
import utn.frc.dlc.core.io.util.ObjectReader;
import utn.frc.dlc.core.io.util.ObjectWriter;
import utn.frc.dlc.core.model.Document;

/**
 * @author Gonzalo
 */
public class DocumentManagement {

    private static final String DOCUMENTS_PATH = Environment.getPathOfWorkspace() + "/docs/doc";
    private static final String DOCUMENT_EXTENSION = ".dlc";
    private static DocumentManagement instance;

    private DocumentManagement() {

    }

    public static DocumentManagement getInstance() {
        if (instance == null) {
            instance = new DocumentManagement();
        }
        return instance;
    }

    public Document getDocument(int docId) {
        ObjectReader<Document> or = new ObjectReader<>();
        return or.read(createPath(docId));
    }

    public void saveDocument(Document doc) {
        ObjectWriter<Document> ow = new ObjectWriter<>();
        ow.write(doc, createPath(doc.getId()));
    }

    private String createPath(int docId) {
        return DOCUMENTS_PATH + StringUtils.leftPad(Integer.toString(docId), 3, "0") + DOCUMENT_EXTENSION;
    }
}
