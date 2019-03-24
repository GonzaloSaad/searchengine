/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.io.management;


import utn.frc.dlc.core.common.Environment;
import utn.frc.dlc.core.io.util.ObjectReader;
import utn.frc.dlc.core.io.util.ObjectWriter;

import java.util.Map;

/**
 * @author Gonzalo
 */
public class DocumentMapManagement {

    private static final String DOCUMENT_MAP_PATH = Environment.getPathOfWorkspace() + "/dmap.dlc";
    private static DocumentMapManagement instance;

    private DocumentMapManagement() {

    }

    public static DocumentMapManagement getInstance() {
        if (instance == null) {
            instance = new DocumentMapManagement();
        }
        return instance;
    }

    public Map<String, Integer> getDocumentMap() {
        ObjectReader<Map<String, Integer>> or = new ObjectReader<>();
        return or.read(DOCUMENT_MAP_PATH);
    }

    public void saveDocumentMap(Map<String, Integer> map) {
        ObjectWriter<Map<String, Integer>> ow = new ObjectWriter<>();
        ow.write(map, DOCUMENT_MAP_PATH);
    }
}
