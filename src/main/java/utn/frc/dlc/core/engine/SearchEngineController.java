/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.engine;


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.dlc.core.common.Constants;
import utn.frc.dlc.core.files.FileTextParser;
import utn.frc.dlc.core.files.FolderFileList;
import utn.frc.dlc.core.io.management.DocumentMapManagement;
import utn.frc.dlc.core.io.management.InternalFoldersManagement;
import utn.frc.dlc.core.io.management.VocabularyManagement;
import utn.frc.dlc.core.model.Document;
import utn.frc.dlc.core.model.PostList;
import utn.frc.dlc.core.model.VocabularyEntry;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gonzalo
 */
public class SearchEngineController {

    private static final Logger logger = LogManager.getLogger(SearchEngineController.class);
    private final SearchHelper searchHelper;
    private final EngineModel engineModel;


    public SearchEngineController() {
        this(Boolean.FALSE);
    }

    public SearchEngineController(boolean restart) {
        this(initEngineModel(restart));
    }

    private static EngineModel initEngineModel(boolean restart) {

        Map<String, Integer> dmap = null;
        Map<String, VocabularyEntry> voc = null;

        if (!restart) {
            dmap = DocumentMapManagement.getInstance().getDocumentMap();
            voc = VocabularyManagement.getInstance().getVocabulary();
        }


        if (restart || voc == null || dmap == null) {

            InternalFoldersManagement.getInstance().clearAll();

            voc = new HashMap<>();
            dmap = new HashMap<>();

            logger.info("No data recovered, vocabulary and doc map initialized.");
        } else {
            logger.info("Vocabulary recovered with [{}] terms. Doc map recovered with [{}] docs.", voc.size(), dmap.size());
        }
        return new EngineModel(voc, dmap);
    }

    public SearchEngineController(EngineModel engineModel) {
        this(new SearchHelper(engineModel), engineModel);
    }

    public SearchEngineController(SearchHelper searchHelper, EngineModel engineModel) {
        this.engineModel = engineModel;
        this.searchHelper = searchHelper;
    }

    public List<Document> getDocumentsForQuery(String query) {
        logger.info("Query: {}", query);
        return searchHelper.handle(query);
    }

    public void runIndexation(String path) throws IOException {

        logger.info("Starting indexing.");

        IndexHelper indexHelper = new IndexHelper(engineModel);

        int indexedTerms = 0;
        long sizeOfIndexed = 0;

        FolderFileList drive = new FolderFileList(path);

        for (File f : drive) {
            long sizeOfFile = f.length();
            sizeOfIndexed += sizeOfFile;
            logger.info("Document to ingest: [{}] \tSize: {} Bytes, \tTotal: {} Bytes.", f.getName(), sizeOfFile, sizeOfIndexed);


            boolean shouldSave = false;
            int termsRed = 0;

            Integer docID = indexHelper.getDocumentID(f);

            String text = read(f);
            FileTextParser fp = new FileTextParser(text);

            for (String term : fp) {
                if (!term.trim().isEmpty()) {

                    termsRed++;
                    indexedTerms++;

                    if (indexedTerms % Constants.LIMIT_WITHOUT_SAVE == 0) {
                        shouldSave = true;
                    }

                    VocabularyEntry ve = indexHelper.getVocabularyEntryForTerm(term);
                    PostList pl = indexHelper.getPostList(ve);

                    ve.addTermOcurrance();
                    pl.addDocument(docID);

                }
            }
            logger.info("Terms read for document [{}]. Total terms indexed [{}]", termsRed, indexedTerms);
            if (shouldSave) {
                indexHelper.commit();
            }
        }
        logger.info("Terms read [{}].", indexedTerms);
        indexHelper.finishIndexing();
        searchHelper.update();

    }

    private String read(File file) throws IOException {
        return FileUtils.readFileToString(file, Constants.ENCODING);
    }


}
