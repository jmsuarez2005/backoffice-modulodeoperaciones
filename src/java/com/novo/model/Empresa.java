/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author ggutierrez
 */
public class Empresa {
    
    private String accodcia;
    private String acnomcia;
    private String rif;
    private String nombreCorto;    

    public Empresa() {
    }
       
    public String getAccodcia() {
        return accodcia;
    }

    public void setAccodcia(String accodcia) {
        this.accodcia = accodcia;
    }

    public String getAcnomcia() {
        return acnomcia;
    }

    public void setAcnomcia(String acnomcia) {
        this.acnomcia = acnomcia;
    }

    public String getRif() {
        return rif;
    }

    public void setRif(String rif) {
        this.rif = rif;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }
    
    
}
