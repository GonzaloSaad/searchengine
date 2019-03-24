/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.io.util;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * @author Gonzalo
 */
public class ObjectReader<T> {

    public ObjectReader() {

    }

    public T read(String path) {
        Object o;

        try (FileInputStream istream = new FileInputStream(path)) {
            ObjectInputStream p = new ObjectInputStream(istream);

            o = p.readObject();

            p.close();
        } catch (Exception e) {
            o = null;
        }

        return (T) o;
    }
}
