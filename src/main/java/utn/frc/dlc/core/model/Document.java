/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utn.frc.dlc.core.model;

import java.io.Serializable;

/**
 *
 * @author gonzalo.saad
 */
public class Document implements Serializable {
    private int id;
    private final String name;

    public Document(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
