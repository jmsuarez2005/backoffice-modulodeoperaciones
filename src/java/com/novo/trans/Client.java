/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.trans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author ggutierrez
 */
public class Client {
    
    private String ip = "127.0.0.1";
    private int port = 8080;
    private int timeOut = 2000;
    private boolean ERRORTCP;
    private boolean errTimeout=false;
    private boolean err=false;
    /** Creates a new instance of client */
    
    public Client(String ip, int port, int timeOut) {
        this.ip = ip;
        this.port = port;
        this.timeOut = timeOut;
        this.ERRORTCP=false;
    }
        
//    public String sendReceive(String sendData) {
//        String sRec = "";
//        try {
//            Socket server = new Socket(ip, port );
//            
//            log.info("connect to ip["+ip+"] port["+port+"]");
//            log.info("send["+sendData+"]");
//            
//            server.setSoTimeout(timeOut );
//            //server.setTcpNoDelay(false);
//            //server.setSoLinger(false, 0);
//            
//            InputStream sin = server.getInputStream( );
//            OutputStream sout = server.getOutputStream( );
//            
//            PrintWriter psout = new PrintWriter( sout, true );
//            
//            
//            ERRORTCP=true;
//            psout.println(sendData);
//            psout.println();
//            
//            // read a newline or carriage return delimited string
//            BufferedReader bsin = new BufferedReader( new InputStreamReader( sin ) );
//            String respData = bsin.readLine( );
//            
//            sRec = respData;
//            log.info("receive["+sRec+"]");
//            
//            server.close( );
//            ERRORTCP=false;
//            
//        //} catch (IOException e ) {
//        } catch (Exception e ) {
//            String sErr = e.getMessage();
//            log.error("Socket ERROR -->["+sErr+"]");
//            sRec = "ERROR : "+sErr;
//            
//        }
//        
//        return sRec;
//    }
    
    public String sendReceive(String sendData) {
        String sRec = "";
        
        try {
            Socket server = new Socket(ip, port);
            this.errTimeout=false;
            this.err=false;

            server.setSoTimeout(timeOut);
            //server.setTcpNoDelay(false);
            //server.setSoLinger(false, 0);
            
            InputStream sin = server.getInputStream( );
            OutputStream sout = server.getOutputStream( );
            
            PrintWriter psout = new PrintWriter( sout, true );
            
            ERRORTCP=true;
            psout.println(sendData);
            psout.println();
            // read a newline or carriage return delimited string
            BufferedReader bsin = new BufferedReader( new InputStreamReader( sin ) );
            String s = null;
            String   respData=null;
            Thread.sleep(2000);
           
            java.nio.CharBuffer charBufferRead = null ;
            charBufferRead = java.nio.CharBuffer.allocate(5150);
            int readCount =bsin.read(charBufferRead);
            if (readCount>0) {
                charBufferRead.flip();
                respData = charBufferRead.toString();
                respData = respData.replaceAll("\r", "").replaceAll("\n", "");                 
            }
            else {
                
                respData = null;
            }
            
            sRec = respData;
            
            server.close( );
            ERRORTCP=false;
//            } catch (Exception e ) {
//                String sErr = e.getMessage();
//                 sRec = "ERROR : "+sErr;
//            }

        } catch (SocketTimeoutException e ) {
            String sErr = e.getMessage();
            //log.error("Socket ERROR -->["+sErr+"]");
            this.errTimeout=true;
            sRec = "ERROR : "+sErr;

        }catch(InterruptedException ie){
             String sErr = ie.getMessage();
             this.err=true;
             sRec = "ERROR : "+sErr;
        }catch(IOException ioe){
             String sErr = ioe.getMessage();
             this.err=true;
             sRec = "ERROR : "+sErr;
        }
        
        return sRec;
    }
    
    public boolean getERRORTCP() {
        return ERRORTCP;
    }

    public boolean getErrTimeout(){
        return errTimeout;
    }
     public boolean getErr(){
        return err;
    }
    
}

