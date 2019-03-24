package utn.frc.dlc.core.io.cache;


import utn.frc.dlc.core.model.PostList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Cache {

    private CachedPostPack[] cache;
    private final int sizeOfCache;

    public Cache(int size) {
        cache = new CachedPostPack[size];
        sizeOfCache = size;
    }

    public abstract Map<String, PostList> getPostPack(int file);

    public abstract Map<String, PostList> putPostPack(Map<String, PostList> postPack, int file);

    public abstract void dump(boolean parallel);

    protected CachedPostPack[] getCache() {
        return cache;
    }

    protected CachedPostPack get(int index) {
        return cache[index];
    }

    protected void set(CachedPostPack c, int index) {
        cache[index] = c;
    }

    public void clean() {
        cache = new CachedPostPack[sizeOfCache];
    }

    public List<Map<String, PostList>> getCachedPost() {

        List<Map<String, PostList>> list = new ArrayList<>();

        // TODO: Refactor here - 5. Hint: .ifPresent() in Optional.
        for (CachedPostPack c : cache) {
            if (c != null) {
                list.add(c.getPostPack());
            }
        }
        return list;
    }

    public int size() {
        return cache.length;
    }


    public abstract void update();
}
