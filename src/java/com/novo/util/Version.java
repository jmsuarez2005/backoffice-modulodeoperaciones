/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author jorojas
 */
public class Version {
    
    public String getVersion(){
        return this.getProperties("version.properties").getProperty("version");
    }
    
    public Properties getProperties(String propName){
        Properties prop = new Properties();
        
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(propName);
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return prop;
    }
    
    public static void main(String[] args){
        Version version = new Version();
        System.out.println("Version: "+version.getVersion());
    }
}
