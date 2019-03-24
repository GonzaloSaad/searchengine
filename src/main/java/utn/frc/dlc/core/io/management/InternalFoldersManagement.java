/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.io.management;


import org.apache.logging.log4j.LogManager;
import utn.frc.dlc.core.common.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Gonzalo
 */
public class InternalFoldersManagement {
    private static InternalFoldersManagement instance;
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(InternalFoldersManagement.class);
    private static final File ROOT_FILE = new File(Environment.getPathOfWorkspace());

    private InternalFoldersManagement() {

    }

    public static InternalFoldersManagement getInstance() {
        if (instance == null) {
            instance = new InternalFoldersManagement();
        }
        return instance;
    }

    public void clearAll() {

        try {
            delete(ROOT_FILE);
            logger.info("All files erased.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear files, system will not be consistent");
        }
    }

    private void delete(File f) throws IOException {

        if (f.exists()) {
            if (f.isDirectory()) {
                logger.info("Cleaning [{}]", f.getName());
                for (File c : f.listFiles()) {
                    delete(c);
                }
            } else {
                if (!f.delete()) {
                    throw new FileNotFoundException("Failed to delete file: " + f);
                }
            }

        } else {
            f.getParentFile().mkdirs();
            f.mkdir();
        }

    }
}
