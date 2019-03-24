package utn.frc.dlc.core.io.management;


import org.apache.commons.lang3.StringUtils;
import utn.frc.dlc.core.common.Environment;
import utn.frc.dlc.core.io.util.ObjectReader;
import utn.frc.dlc.core.io.util.ObjectWriter;
import utn.frc.dlc.core.model.PostList;

import java.util.Map;

public class PostPackManagement {


    private static final String POST_FOLDER_PATH = Environment.getPathOfWorkspace() + "/post/post";
    private static final String POST_FILE_EXTENSION = ".post";

    private static PostPackManagement instancePck;

    private PostPackManagement() {

    }

    public static PostPackManagement getInstance() {
        if (instancePck == null) {
            instancePck = new PostPackManagement();
        }
        return instancePck;
    }

    public Map<String, PostList> getPostPack(int postNumber) {
        ObjectReader<Map<String, PostList>> or = new ObjectReader<>();
        return or.read(createPath(postNumber));
    }

    public void savePostPack(Map<String, PostList> postPack, int postNumber) {
        ObjectWriter<Map<String, PostList>> ow = new ObjectWriter<>();
        ow.write(postPack, createPath(postNumber));
    }

    private String createPath(int postNumber) {
        return POST_FOLDER_PATH + StringUtils.leftPad(Integer.toString(postNumber), 3, "0") + POST_FILE_EXTENSION;
    }

}
