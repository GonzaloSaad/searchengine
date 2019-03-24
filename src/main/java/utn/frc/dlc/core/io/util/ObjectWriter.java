/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.io.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author Gonzalo
 */
public class ObjectWriter<T> {


    public ObjectWriter() {

    }


    public boolean write(T o, String path) {

        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream ostream = new FileOutputStream(path);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(o);
            p.flush();

        } catch (Exception e) {
            return false;
        } finally {

        }
        return true;
    }
}
