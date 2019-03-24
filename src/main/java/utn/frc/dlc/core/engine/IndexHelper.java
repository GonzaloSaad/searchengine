/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.engine;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.dlc.core.common.Constants;
import utn.frc.dlc.core.io.EngineModelPersistence;
import utn.frc.dlc.core.io.cache.Cache;
import utn.frc.dlc.core.io.cache.IntermediateCache;
import utn.frc.dlc.core.io.management.PostPackManagement;
import utn.frc.dlc.core.model.PostList;
import utn.frc.dlc.core.model.VocabularyEntry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gonzalo
 */
public class IndexHelper {

    private static final Logger logger = LogManager.getLogger(IndexHelper.class);
    private final Cache cache;
    private final EngineModel engineModel;
    private final EngineModelPersistence persistence;

    public IndexHelper(Cache cache, EngineModel engineModel) {
        this.cache = cache;
        this.engineModel = engineModel;
        this.persistence = new EngineModelPersistence(engineModel);
    }

    public IndexHelper(EngineModel engineModel) {
        this(new IntermediateCache(Constants.INDEX_CACHE_SIZE), engineModel);
    }

    private int getNextFileIndex() {
        return (engineModel.getVocabulary().size() % Constants.POST_FILES);
    }

    private int getNextDocumentID() {
        return engineModel.getDocMap().size();
    }

    public PostList getPostList(VocabularyEntry ve) {

        if (ve == null) {
            return null;
        }

        PostList pl = null;

        String term = ve.getTerm();
        int file = ve.getPostFile();

        Map<String, PostList> postPack = getPostPack(file);

        if (postPack != null) {
            pl = postPack.get(term);
            if (pl == null) {
                pl = new PostList(term);
                postPack.put(term, pl);
            }
        }

        return pl;
    }

    public VocabularyEntry getVocabularyEntryForTerm(String term) {


        // TODO: Refactor here - 1. Hint: .computeIfAbsent() in Map.

        VocabularyEntry ve = engineModel.getFromVocabulary(term);

        if (ve == null) {
            int postFile = getNextFileIndex();
            ve = new VocabularyEntry(term, postFile);
            engineModel.addToVocabulary(ve);
        }

        return ve;
    }

    public int getDocumentID(File file) {
        Integer docID = engineModel.getFromDocMap(file);
        if (docID == null) {
            docID = getNextDocumentID();
            engineModel.addToDocMap(file, docID);
            persistence.persistDocument(file, docID);
            logger.info("Document [{}] did not exist. Created entry with [{}] sequence number.", file.getName(), docID);
        } else {
            logger.info("Document [{}] did exist, with [{}] sequence number.", file.getName(), docID);
        }
        return docID;
    }

    private Map<String, PostList> getPostPack(int file) {
        Map<String, PostList> postPack = cache.getPostPack(file);

        if (postPack == null) {
            postPack = new HashMap<>();
            cache.putPostPack(postPack, file);
        }
        return postPack;
    }

    private void commit(boolean parallel) {
        logger.info("Saving indexes and cache to storage.");
        cache.dump(parallel);
        persistence.persistEngineModel();
    }


    public void commit() {
        commit(true);
    }

    public void finishIndexing() {

        commit(false);
        logger.info("Finalize indexing...");

        Runnable job1 = new UpdatingNrJob(0, Constants.POST_FILES / 2);
        Runnable job2 = new UpdatingNrJob(Constants.POST_FILES / 2, Constants.POST_FILES);


        Thread thread = new Thread(job1);
        thread.start();

        job2.run();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        persistence.persistEngineModel();
        logger.info("Done.");

    }

    private class UpdatingNrJob implements Runnable {
        private int start;
        private int end;


        public UpdatingNrJob(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            doUpdate();

        }

        public void doUpdate() {
            for (int i = start; i < end; i++) {
                Map<String, PostList> postPack = PostPackManagement.getInstance().getPostPack(i);
                if (postPack == null) {
                    throw new RuntimeException("The model was not consistent.");
                }

                for (String term : postPack.keySet()) {
                    VocabularyEntry ve = engineModel.getFromVocabulary(term);
                    if (ve == null) {
                        throw new RuntimeException("The model was not consistent.");
                    }
                    ve.updateNrValue(postPack.get(term));
                }

            }
        }
    }


}
