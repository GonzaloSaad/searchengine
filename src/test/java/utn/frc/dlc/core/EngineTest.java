package utn.frc.dlc.core;

import org.junit.Test;
import utn.frc.dlc.core.engine.SearchEngineController;
import utn.frc.dlc.core.model.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class EngineTest {

    @Test
    public void testEngineWithDocumentsSmallSet() throws IOException {

        boolean smallSet = Boolean.TRUE;

        String pathOfFile;

        if(smallSet){
            pathOfFile = "./documents/smallset";
        } else {
            pathOfFile = "./documents/bigset";
        }
        SearchEngineController controller = new SearchEngineController(Boolean.TRUE);

        controller.runIndexation(pathOfFile);


        Map<String, Integer> wordsAndFrequency = getMapForWords(smallSet);

        for(String word: wordsAndFrequency.keySet()){
            Integer expectedAmountOfDocuments = wordsAndFrequency.get(word);
            List<Document> documentsForQuery = controller.getDocumentsForQuery(word);
            assertEquals(expectedAmountOfDocuments.intValue(), documentsForQuery.size());
        }

    }


    private Map<String, Integer> getMapForWords(boolean smallSet) {
        Map<String, Integer> map = new HashMap<>();
        if (smallSet) {
            map.put("kind", 8);
            map.put("awesome", 0);
            map.put("likely", 6);
            map.put("hot", 7);
            map.put("car", 1);
        } else {
            map.put("otherwise", 30);
            map.put("cow", 14);
            map.put("windmill", 5);
            map.put("sadly", 15);
            map.put("biggest", 6);
        }
        return map;
    }
}
