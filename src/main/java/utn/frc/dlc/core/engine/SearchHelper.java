package utn.frc.dlc.core.engine;


import utn.frc.dlc.core.common.Constants;
import utn.frc.dlc.core.files.FileTextParser;
import utn.frc.dlc.core.io.cache.Cache;
import utn.frc.dlc.core.io.cache.SearchCache;
import utn.frc.dlc.core.io.management.DocumentManagement;
import utn.frc.dlc.core.model.Document;
import utn.frc.dlc.core.model.PostList;
import utn.frc.dlc.core.model.PostListItem;
import utn.frc.dlc.core.model.VocabularyEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SearchHelper {

    private final Cache cache;
    private final EngineModel engineModel;

    public SearchHelper(Cache cache, EngineModel engineModel) {
        this.cache = cache;
        this.engineModel = engineModel;
    }

    public SearchHelper(EngineModel engineModel) {
        this(new SearchCache(Constants.SEARCH_CACHE_SIZE), engineModel);
    }

    public List<Document> handle(String query) {
        Set<DocumentResult> docSet = getOrderedDocumentsForQuery(query);
        List<Document> documentList = new ArrayList<>();
        int documents = 0;

        if (docSet != null && !docSet.isEmpty()) {
            for (DocumentResult dr : docSet) {
                Document doc = DocumentManagement.getInstance().getDocument(dr.getDocID());
                if (doc != null) {
                    documentList.add(doc);
                    documents++;
                }

                if (documents == Constants.R) {
                    break;
                }

            }
        }
        return documentList;

    }

    private List<VocabularyEntry> getTermsForVocabulary(String query) {
        List<VocabularyEntry> set = new ArrayList<>();

        FileTextParser fp = new FileTextParser(query);
        for (String term : fp) {
            VocabularyEntry ve = engineModel.getFromVocabulary(term);
            if (ve == null) {
                continue;
            }
            set.add(ve);
        }
        return set;
    }

    private Set<DocumentResult> getOrderedDocumentsForQuery(String query) {

        int N = engineModel.getDocMap().size();

        Map<Integer, DocumentResult> docMap = new HashMap<>();
        List<VocabularyEntry> terms = getTermsForVocabulary(query);

        if (terms.isEmpty()) {
            return null;
        }

        for (VocabularyEntry ve : terms) {

            // TODO: Refactor here - 1. Hint: .isPresent() in Optional.
            PostList pl = getPostList(ve);
            if (pl == null) {
                continue;
            }

            int Nr = ve.getNr();
            double idf = Math.log((double) N / (double) Nr);

            for (PostListItem pli : pl.getListOfDocument()) {

                DocumentResult dr = docMap.get(pli.getDocID());
                if (dr == null) {
                    dr = new DocumentResult(pli.getDocID());
                    docMap.put(dr.getDocID(), dr);
                }
                double valueOfTermInDoc = pli.getTf() * idf;
                dr.addToValue(valueOfTermInDoc);
            }
        }

        Set<DocumentResult> orderedDocuments = new TreeSet<>(new DocumentResultComparator());
        orderedDocuments.addAll(docMap.values());
        return orderedDocuments;


    }

    private PostList getPostList(VocabularyEntry ve) {


        Map<String, PostList> postPack = cache.getPostPack(ve.getPostFile());

        if (postPack == null) {
            return null;
        }

        return postPack.get(ve.getTerm());
    }

    public void update() {
        cache.update();
    }

    private class DocumentResult implements Comparable<DocumentResult> {
        private final int docid;
        private double value;

        public DocumentResult(int docID) {
            docid = docID;
            value = 0;
        }

        public void addToValue(double toAdd) {
            value += toAdd;
        }

        public int getDocID() {
            return docid;
        }

        public double getValue() {
            return value;
        }

        @Override
        public int compareTo(DocumentResult otherDoc) {
            return (getValue() < otherDoc.getValue() ? 1 : -1);
        }
    }

    private class DocumentResultComparator implements Comparator<DocumentResult> {

        @Override
        public int compare(DocumentResult d1, DocumentResult d2) {
            return d1.compareTo(d2);
        }
    }


}
