/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author jorojas
 */
public class Parametro {
    private String acname;
    private String acvalue;
    private String acprofile;
    private String acgroup;

    public Parametro() {
    }

    public Parametro(String acname, String acvalue, String acprofile, String acgroup) {
        this.acname = acname;
        this.acvalue = acvalue;
        this.acprofile = acprofile;
        this.acgroup = acgroup;
    }

    public String getAcname() {
        return acname;
    }

    public void setAcname(String acname) {
        this.acname = acname;
    }

    public String getAcvalue() {
        return acvalue;
    }

    public void setAcvalue(String acvalue) {
        this.acvalue = acvalue;
    }

    public String getAcprofile() {
        return acprofile;
    }

    public void setAcprofile(String acprofile) {
        this.acprofile = acprofile;
    }

    public String getAcgroup() {
        return acgroup;
    }

    public void setAcgroup(String acgroup) {
        this.acgroup = acgroup;
    }
    
    
}
