package utn.frc.dlc.core.io.cache;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.dlc.core.io.management.PostPackManagement;
import utn.frc.dlc.core.model.PostList;

import java.util.Map;

public class IntermediateCache extends Cache {

    private Thread persistingThread = new Thread();
    private static final Logger logger = LogManager.getLogger(IntermediateCache.class);

    public IntermediateCache(int size) {
        super(size);
    }

    @Override
    public Map<String, PostList> getPostPack(int file) {

        // TODO: Refactor here - 6. Hint: .map() in Optional.
        CachedPostPack c = get(file);

        if (c == null) {
            return null;
        }

        return c.getPostPack();
    }

    @Override
    public Map<String, PostList> putPostPack(Map<String, PostList> postPack, int file) {

        CachedPostPack out = get(file);
        CachedPostPack cpp = new CachedPostPack(file, postPack);
        set(cpp, cpp.getFile());

        if (out != null) {
            return out.getPostPack();
        }
        return null;
    }

    @Override
    public void dump(boolean parallel) {
        mergePostPacks(parallel);
        clean();
    }

    @Override
    public void update() {

    }

    private void mergePostPacks(boolean parallel) {

        try {
            logger.info("Waiting for persisting thread to finish. Status [{}].", persistingThread.getState());
            persistingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("Problem with thread.");
        }
        logger.info("Thread finished ok.");


        Runnable job = new PersistentJob(getCache());
        if (parallel) {
            persistingThread = new Thread(job);
            persistingThread.start();
        } else {
            job.run();
        }
    }

    private class PersistentJob implements Runnable {

        private CachedPostPack[] threadCache;

        public PersistentJob(CachedPostPack[] cache) {
            threadCache = cache;
        }


        public void run() {
            doMerge();
        }


        private void doMerge() {
            for (int i = 0; i < threadCache.length; i++) {

                CachedPostPack cachedPostPack = threadCache[i];

                if (cachedPostPack == null) {
                    continue;
                }
                Map<String, PostList> postPack = cachedPostPack.getPostPack();

                Map<String, PostList> diskPostPack = PostPackManagement.getInstance().getPostPack(i);
                if (diskPostPack == null || diskPostPack.size() == 0) {
                    PostPackManagement.getInstance().savePostPack(postPack, i);
                    continue;
                }

                for (String key : postPack.keySet()) {
                    PostList diskPostList = diskPostPack.get(key);
                    PostList cachedPostList = postPack.get(key);

                    if (diskPostList == null) {
                        diskPostPack.put(cachedPostList.getTerm(), cachedPostList);
                        continue;
                    }

                    diskPostList.mergePostEntry(cachedPostList);
                }
                PostPackManagement.getInstance().savePostPack(diskPostPack, i);
            }

        }


    }
}
