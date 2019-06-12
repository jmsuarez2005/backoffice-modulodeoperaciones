/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.trans;

import com.novo.objects.util.Utils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.log4j.Logger;

/**
 *
 * @author ggutierrez
 */
public class TransactionHandler {

    private String numbt = "";
    private String msg = "";
    private String respCode = "";
    private String authCode = "";
    private String nombreCliente = "";
    private String emailCliente = "";
    private String msgText = "";
    private String nroOrganizacion = "";
    private String saldoActual = "";
    private String saldoBloqueado = "";
    private String saldoDisponible = "";
    private String ref = "";
    private String md5 = "";
    private String systraceCobro = "";
    private String fecexpCobro = "";
    private String comercioCobro = "";
    private String ntarjetaCobro = "";
    private String fechahora = "";
    private Client cl = null;
    private String ip = "";
    private int port = 0;
    private int timeout = 0;
    private Logger log = Logger.getLogger(TransactionHandler.class);

    public TransactionHandler(String ip, int port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
        cl = new Client(ip, port, timeout);
        log.debug("TebcatranInterface: ip[" + ip + "], port[" + port + "], timeout[" + timeout + "]");
    }

    private int setResponse(String sResp) {
        int rc = 0;
//		 0000000000111111111122222222223333333333
//		 0123456789012345678901234567890123456789
//		 + + + + + +
//		 0000103000 004006111002862C000000000000
//		 0000103000 030000001002862C000000000000
//		 ERROR : Connection timed out: connect

        authCode = "";
        if (sResp == null) {
            rc = -1;
            msg = "ERROR: not response";
        } else if (sResp.length() <= 4) {
            rc = -2;
            msg = "ERROR: invalid response length";
        } else if (sResp.substring(0, 5).equalsIgnoreCase("ERROR")) {
            rc = -3;
            msg = sResp;
        } else if (!sResp.substring(0, 4).equalsIgnoreCase("0000")) {
            rc = -4;
            msg = "TEBCATRAN ERROR. Operación no realizada";
        } else {
            String responseCode = sResp.substring(11, 13);
            setRespCode(responseCode);
            if (!responseCode.equals("00")) {
                rc = -5;
                msg = "ATENCION RESPONSE[" + responseCode + "]. Operación no realizada";
                setRespCode(responseCode);
            } else {
                rc = 0;
                msg = "Transaccion Realizada";
                authCode = sResp.substring(13, 19);
            }
        }
        log.info("sResp[" + sResp + "](" + sResp.length() + ")");
        log.info("rc[" + rc + "] msg[" + msg + "] autCode[" + authCode + "]");

        return (rc);
    }

    private int setResponse(String trn, String sResp) {
        int rc = 0;
        // 0000000000111111111122222222223333333333
        // 0123456789012345678901234567890123456789
        // + + + + + +
        // 0000103000 004006111002862C000000000000
        // 0000103000 030000001002862C000000000000
        // ERROR : Connection timed out: connect

        log.debug("trn[" + trn + "]" + trn.substring(0, 3) + " sResp[" + sResp + "]");
        authCode = "";

        if (sResp == null) {
            rc = -1; // "ERROR: not response";
            msg = "ERROR: not response";
            setMsgText(msg);
        } else if (sResp.length() <= 4) {
            rc = -2; // "ERROR: invalid response length";
            msg = "ERROR: invalid response length";
            setMsgText(msg);
        } else if (sResp.substring(0, 5).equalsIgnoreCase("ERROR")) {
            rc = -3; // error en resp
            msg = "TEBCATRAN " + sResp;
            setMsgText(msg);
        } else if (!sResp.substring(0, 4).equalsIgnoreCase("0000")) {
            rc = -4; // "TEBCATRAN ERROR. Operaci�n no realizada";
            msg = "TEBCATRAN ERROR. Operación no realizada";
            setMsgText(msg);
        } else {
            setRespCode(sResp.substring(11, 13));

            if (!respCode.equals("00")) {
                rc = -5; // "ATENCION RESPONSE["+respCode+"] NOT ZERO.
                // Operacion no realizada";
                msg = "ATENCION RESPONSE[" + respCode + "] NOT ZERO. Operación no realizada";
            } else {
                rc = 0; // "Transaccion Realizada";
                msg = "Transaccion Realizada";
            }

            authCode = sResp.substring(13, 19);
            log.info("Response - Auth Code:               [" + authCode + "]");

            if (trn.substring(0, 3).equals("108")) { // Consulta de Saldo
                saldoActual = sResp.substring(19, 34);
                log.info("Response - Saldo Actual:            [" + saldoActual + "]");
                saldoBloqueado = sResp.substring(34, 49);
                log.info("Response - Saldo Bloqueado:         [" + saldoBloqueado + "]");
                saldoDisponible = sResp.substring(49, 64);
                log.info("Response - Saldo Disponible:        [" + saldoDisponible + "]");
                msgText = sResp.substring(64);
            } else if (trn.substring(0, 3).equals("109")) { // Transferencia
                // Plata-Plata
                ref = sResp.substring(19, 39);
                log.info("Response - REF:                     [" + ref + "]");
                /*md5 = sResp.substring(39, 71);
                 log.info("Response - MD5:                     [" + md5 + "]");
                 msgText = sResp.substring(71);
                 log.info("Response - MSG TXT:                 [" + msgText + "]");*/
            }
        }

        log.info("sResp[" + sResp + "](" + sResp.length() + ")");

        return (rc);
    }

    private String makeMonto(String monto) {
        String sMonto = "";
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0;0");
        sMonto = df.format(Float.parseFloat(Double.toString(Utils.desFormatMonto(monto) * 100)));
        return (sMonto);
    }

    private String cs(String so, boolean r, String c, int len) {
        String s = "";
        s = so;

        if (s.length() < len) {
            for (int i = s.length(); i < len; i++) {
                if (r) {
                    s = s + c;
                } else {
                    s = c + s;
                }
            }

        } else if (s.length() > len) {
            s = s.substring(0, len);
        }

        return (s);
    }

    private String getdtbtrans(boolean b) {
        // n10, MMDDhhmmss
        SimpleDateFormat df = new SimpleDateFormat("MMddHHmmss");
        if (b) {
//            df.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return df.format(new Date());
    }

    private String getLocalDate(boolean b) {
        SimpleDateFormat df = new SimpleDateFormat("MMdd");
        if (b) {
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return df.format(new Date());
    }

    private String getLocalTime(boolean b) {
        SimpleDateFormat df = new SimpleDateFormat("HHmmss");
        if (b) {
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return df.format(new Date());
    }

    private String getSysTrace() {
        String s = "8";
        return (cs(s, false, "0", 6));
    }

    private String mkSendBuff(String trn, String sTerminal, String noTarjeta, String monto, String nf, String systrace) {
        return (mkSendBuff(trn, sTerminal, noTarjeta, monto, nf, systrace, "0000"));
    }

    /**
     *
     * @param trn
     * @param sTerminal
     * @param noTarjeta
     * @param monto
     * @param nf
     * @param systrace
     * @param expTarjeta
     * @return
     */
    private String mkSendBuff(String trn, String sTerminal, String noTarjeta, String monto, String nf, String systrace, String expTarjeta) {
        String sComercio = "000000719" + sTerminal.substring(0, 6);
        return (mkSendBuff(trn, sComercio, sTerminal, noTarjeta, "", monto, nf, systrace, expTarjeta));
    }

    /**
     * Buffer de envio para el serve tebcatran
     *
     * @param trn codigo de transaccion TEBCATRAN
     * @param sTerminal identificador del terminal
     * @param noTarjeta numero de tarjeta
     * @param cedula numero de c�dula
     * @param expTarjeta fecha de expiraci�n de la tarjeta
     * @param monto monto del pago
     * @param montoOp costo de la operacion
     * @param nf nombre del comercio o fecha de faturacion
     * @param systrace unique system trace number (len 6)
     * @param noTelefono numero de telefono
     * @param fechaFactura fecha de facturacion
     *
     * @param noTelefono numero de telefono
     * @throws java.lang.Exception
     */
    private String mkSendBuff(String trn, String sTerminal, String noTarjeta, String cedula, String monto, String montoOp, String nf, String systrace) {

        String s = "";

        // se utiliza para el reverso automatico por timeout
        fechahora = getdtbtrans(true);

        String nombre = "";

        if (trn.substring(0, 4).equals("107")) {
            fechahora = nf;
        } else {
            nombre = nf;
            numbt = cs(systrace, false, "0", 6) + fechahora;
            log.info("set numbt to [" + numbt + "]");
        }

        s = trn;
        s = s + cs(systrace, false, "0", 6);
        s = s + fechahora;
        s = s + cs(noTarjeta, true, " ", 16);
        s = s + "    ";
        s = s + sTerminal;
        s = s + "000000";
        s = s + nroOrganizacion + sTerminal.substring(0, 6);
        s = s + cs(nombre, true, " ", 37) + " PE";
        s = s + cs(monto, false, "0", 15);
        s = s + getdtbtrans(false);
        s = s + cs("0", false, "0", 10);
        s = s + "00" + sTerminal;
        s = s + cs(" ", false, " ", 18);
        s = s + cs(" ", false, " ", 3);
        s = s + cs(cedula, false, " ", 10);
        s = s + cs(montoOp, false, " ", 15);


        return (s);
    }

    private String mkSendBuff(String trn, String sComercio, String sTerminal, String noTarjeta, String cedula, String monto, String nf, String systrace, String expTarjeta) {
        String s;
        fechahora = getdtbtrans(true);
        String nombre = "";
        if (trn.matches("104[02]")) {
            // fechahora = nf;
        } else {
            nombre = nf;
            numbt = cs(systrace, false, "0", 6) + fechahora;
            log.info("set numbt to [" + numbt + "]");
        }
        s = trn;
        s = s + cs(systrace, false, "0", 6);
        s = s + fechahora;
        s = s + cs(noTarjeta, true, " ", 16);
        s = s + "    ";
        s = s + sTerminal;
        s = s + "000000";
        s = s + sComercio + sTerminal.substring(0, 6);
        //s = s + cs(sComercio,false,"0",15);
        s = s + cs(nombre, true, " ", 37) + " PE";
        s = s + cs(monto, false, "0", 15);
        s = s + getdtbtrans(false);
        s = s + cs("0", false, "0", 10);
        s = s + "00" + sTerminal;
        s = s + cs(" ", false, " ", 18);
        s = s + cs(expTarjeta, true, " ", 4);
        return (s);
    }

    /**
     * Sub-buffer de transferencias para el server tebcatran via SMS
     *
     * @param fecExpOrigen length[4] pos[190]
     * @param tarjetaDestino length[20] pos[194]
     * @param cedulaDestino length[15] pos[214]
     * @param fecExpDestino length[4] pos[229]
     */
    private String mkTransferenciaBuffer(String fecExpOrigen, String tarjetaDestino, String cedulaDestino, String fecExpDestino) {
        String s;
        s = cs(fecExpDestino, false, "0", 4);
        s += cs(tarjetaDestino, true, "0", 20);
        s += cs(cedulaDestino, true, " ", 15);
        s += cs(fecExpOrigen, true, "0", 4);
        return (s);
    }

    /**
     * Interface de consulta de saldo (108X)
     *
     * @param systemtrace length[6] pos[0]
     * @param tarjeta length[20] pos[16]
     * @param terminal length[8] pos[36]
     * @param nomcomercio length[40] pos[59]
     * @param terminalSerial length[28] pos[134]
     * @param cedula length[10] pos[165]
     * @param montoComision length[15] pos[175]
     *
     * @return 0: process OK
     * @return -1; //"ERROR: not response";
     * @return -2; //"ERROR: invalid response length";
     * @return -3; // error en resp
     * @return -4; //"TEBCATRAN ERROR. Operaci�n no realizada";
     * @return -5; //"ATENCION RESPONSE["+respCode+"] NOT ZERO. Operacion no
     * realizada";
     */
    public int execSaldo(String idCanal, String systemtrace, String tarjeta, String terminal, String nomcomercio, String cedula, String montoComision, String expTarjeta) {
        int rc = 0;
        log.info("execSaldo [Start]");
        log.info("execSaldo idCanal         : " + idCanal);
        log.info("execSaldo systemtrace     : " + systemtrace);
        log.info("execSaldo tarjeta         : " + tarjeta);
        log.info("execSaldo terminal        : " + terminal);
        log.info("execSaldo nomcomercio     : " + nomcomercio);
        log.info("execSaldo cedula          : " + cedula);
        log.info("execSaldo montoComision   : " + montoComision);
        log.info("execSaldo Fecha expiracion tarjeta : " + expTarjeta);

        String sSendBuff = mkSendBuff(idCanal, cedula, terminal, tarjeta, makeMonto("0"), makeMonto(montoComision), nomcomercio, systemtrace, expTarjeta);
        log.debug("Trama Enviada al Novotrans: " + sSendBuff);
        String sResp = cl.sendReceive(sSendBuff);

        if (cl.getERRORTCP()) {
            log.error("ATENCION: ERROR de comunicaciones. No se puede determinar resultado de la operación. Se envia reverso automaticamente");
        }

        rc = setResponse("108" + idCanal, sResp);

        log.info("execSaldo [End] rc=" + rc + " msg=" + msg + " AuthCode=" + getAuthCode() + " Numbt=" + getNumbt());

        return (rc);
    }

    /**
     * Interface de Transferencia Plata-Plata (109X)
     *
     * @param systemtrace length[6] pos[0]
     * @param tarjeta length[20] pos[16]
     * @param terminal length[8] pos[36]
     * @param codcomercio length[15] pos[44]
     * @param nomcomercio length[40] pos[59]
     * @param monto length[12] pos[102]
     * @param terminalSerial length[28] pos[134]
     * @param cedula length[10] pos[165]
     * @param montoComision length[15] pos[175]
     *
     * @return 0: process OK
     * @return -1; //"ERROR: not response";
     * @return -2; //"ERROR: invalid response length";
     * @return -3; // error en resp
     * @return -4; //"TEBCATRAN ERROR. Operaci�n no realizada";
     * @return -5; //"ATENCION RESPONSE["+respCode+"] NOT ZERO. Operacion no
     * realizada";
     */
    public int execTransferencia(String idCanal, String systemtrace, String tarjeta, String terminal, String nomcomercio, String monto, String cedula, String montoComision, String fecExpOrigen, String tarjetaDestino, String cedulaDestino, String fecExpDestino) {
        int rc = 0;
        int rev = 0;
        log.info("execTransferencia [Start]");
        log.info("execTransferencia idCanal         : " + idCanal);
        log.info("execTransferencia systemtrace     : " + systemtrace);
        log.info("execTransferencia tarjeta         : " + tarjeta);
        log.info("execTransferencia terminal        : " + terminal);
        log.info("execTransferencia nomcomercio     : " + nomcomercio);
        log.info("execTransferencia monto           : " + monto);
        log.info("execTransferencia cedula          : " + cedula);
        log.info("execTransferencia montoComision   : " + montoComision);
        log.info("execTransferencia fecExpOrigen    : " + fecExpOrigen);
        log.info("execTransferencia tarjetaDestino  : " + tarjetaDestino);
        log.info("execTransferencia cedulaDestino   : " + cedulaDestino);
        log.info("execTransferencia fecExpDestino   : " + fecExpDestino);

        String sSendBuff = mkSendBuff("109" + idCanal, terminal, tarjeta, cedula, makeMonto(monto), makeMonto(montoComision), nomcomercio, systemtrace);
        sSendBuff += mkTransferenciaBuffer(fecExpOrigen, tarjetaDestino, cedulaDestino, fecExpDestino);
        String sResp = cl.sendReceive(sSendBuff);

        if (cl.getERRORTCP()) {
//                if(cl.getErrTimeout()){
//                    rev=this.execReversoTransferencia(idCanal, tarjeta, tarjeta, nomcomercio, monto, monto, systemtrace, terminal, fecExpOrigen, tarjetaDestino, cedulaDestino, fecExpDestino, fechahora, numbt);
//                    log.info("Enviando peticion de reverso de transferencia a novotrans. Rc="+rev);
//                    if(rev!=0) {//enviando de nuevo peticion de reverso
//                         log.error("Enviando de nuevo peticion de reverso");
//                        rev=this.execReversoTransferencia(idCanal, tarjeta, tarjeta, nomcomercio, monto, monto, systemtrace, terminal, fecExpOrigen, tarjetaDestino, cedulaDestino, fecExpDestino, fechahora, numbt);
//                    }
//                }
//			log.error("ATENCION: ERROR de comunicaciones. No se puede determinar resultado de la operacion. Se envia reverso automaticamente");
        }

        rc = setResponse("109" + idCanal, sResp);
        if (rc != 0) {
//            rev=this.execReversoTransferencia(idCanal, tarjeta, tarjeta, nomcomercio, monto, monto, systemtrace, terminal, fecExpOrigen, tarjetaDestino, cedulaDestino, fecExpDestino, fechahora, numbt);
//            log.info("Enviando peticion de reverso de transferencia a novotrans. Rc="+rev);
//            if(rev!=0) {//enviando de nuevo peticion de reverso
//                 log.error("Enviando de nuevo peticion de reverso");
//                rev=this.execReversoTransferencia(idCanal, tarjeta, tarjeta, nomcomercio, monto, monto, systemtrace, terminal, fecExpOrigen, tarjetaDestino, cedulaDestino, fecExpDestino, fechahora, numbt);
//            }
        }
//		log.info("execTransferencia [End] rc=" + rc + " msg=" + msg + " AuthCode=" + getAuthCode() + " Numbt=" + getNumbt());

        return (rc);
    }

    public int execReversoTransferencia(String idCanal, String noTarjeta, String expTarjeta, String noTelefono, String monto, String montoOp, String systrace, String sTerminal, String fecExpOrigen, String tarjetaDestino, String cedulaDestino, String fecExpDestino, String fechahora, String numbt) {
        int rc = 0;
//        log.info("Ejecutando [Start] Reverso de Transferecia="+noTelefono+" Tarjeta="+noTarjeta+" Expiraci�n="+expTarjeta+" monto="+monto+" numbt="+numbt);
        String codRevTransferencia = "110";

        // String sSendBuff = mkSendBuff("107"+idCanal, sTerminal, noTarjeta, expTarjeta, makeMonto(monto), makeMonto(montoOp), fechahora, systrace,noTelefono,fechaFactura);
        String sSendBuff = mkSendBuff(codRevTransferencia + idCanal, sTerminal, noTarjeta, expTarjeta, makeMonto(monto), makeMonto(montoOp), fechahora, systrace);
        sSendBuff += mkTransferenciaBuffer(fecExpOrigen, tarjetaDestino, cedulaDestino, fecExpDestino);
        String sResp = cl.sendReceive(sSendBuff);

        rc = setResponse(codRevTransferencia + idCanal, sResp);
//        log.info("Ejecutando reverso [End] msg="+msg+" autCode="+getAuthCode()+" numbt="+getNumbt());
        return (rc);
    }

    public int execCobro(String op, String notarjeta, String monto, String systrace, String sComercio, String sTerminal, String nombre, String fecexp, boolean update, String opReverse) {
        int rc = 0;
        log.info("execCobro [Start]");
        log.info("execCobro op       : " + op);
        log.info("execCobro notarjeta: " + notarjeta);
        log.info("execCobro monto    : " + monto);
        log.info("execCobro systrace : " + systrace);
        log.info("execCobro sComercio: " + sComercio);
        log.info("execCobro sTerminal: " + sTerminal);
        log.info("execCobro nombre   : " + nombre);
        log.info("execCobro fecexp   : " + fecexp);
        log.info("execCobro update   : " + update);

        String sSendBuff = mkSendBuff(op, sComercio, sTerminal, notarjeta, "", makeMonto(monto), nombre, systrace, fecexp);
        log.debug(sSendBuff);
        String sResp = "";

        if (update) {
            sResp = cl.sendReceive(sSendBuff);
        } else {
//                    log.debug("sSendBuff["+sSendBuff+"]");
            sResp = "00000000000000000000000000000000000000000000000000000";
            msg = "modo no update (false)";
        }

        if (cl.getERRORTCP()) {
            /*Client clRev = new Client(ip, port, timeout);
             log.error("ATENCION: ERROR de comunicaciones. No se puede determinar resultado de la operacion. Se envia reverso automaticamente");
             String sSendRevBuff = mkSendBuff("1021",sComercio, sTerminal, notarjeta,"", makeMonto(monto), fechahora, systrace,"0000");
             log.error("TRY REVERSO: sSendRevBuff[" + sSendRevBuff + "]");
             String sRevResp = clRev.sendReceive(sSendRevBuff);
             int rcRev = setResponse(sRevResp);
             log.error("REVERSO RESPONSE: rc=" + rcRev + " sRevResp[" + sRevResp + "]");*/
            execReversoCargo(opReverse, sComercio, sTerminal, notarjeta, monto, systrace, fecexp);
        }
        rc = setResponse(sResp);
//        if (rc != 0) {
//            execReversoCargo(opReverse,sComercio, sTerminal, notarjeta, monto, systrace, fecexp);
//        }
        log.info("Ejecutando [End] rc=" + rc + " msg=" + msg + " autCode=" + authCode + " numbt=" + numbt);

        return (rc);
    }

    public int execReversoCargo(String idCanal, String sComercio, String sTerminal, String notarjeta, String monto, String systrace, String fecExpCobro) {
        log.debug("idCanal: " + idCanal);
        log.debug("sComercio: " + sComercio);
        log.debug("sTerminal: " + sTerminal);
        log.debug("#tarjeta: " + notarjeta);
        log.debug("monto: " + monto);
        log.debug("systrace: " + systrace);
        log.debug("fecExpCobro: " + fecExpCobro);
        Client clientDAO = new Client(ip, port, timeout);
        log.debug("Llamando TebcatranInterfaceDAO.mkSendBuff");
        String sSendRevBuff = mkSendBuff(idCanal, sComercio, sTerminal, notarjeta, "", makeMonto(monto), "REVERSO TRANSFERECIA", systrace, fecExpCobro);
        log.debug(sSendRevBuff);
        log.debug("Llegando TebcatranInterfaceDAO.mkSendBuff");
        String sRevResp = clientDAO.sendReceive(sSendRevBuff);
        int rcRev = setResponse(sRevResp);
        log.debug("REVERSO RESPONSE: rc=" + rcRev + " sRevResp[" + sRevResp + "]");
        return rcRev;
    }

    public int execRecarga(String op, String notarjeta, String monto, String systrace, String sComercio, String sTerminal, String nombre, String fecexp, boolean update, String opReverse) {
        int rc;
        log.debug("execRecarga [Start]");
        log.debug("execRecarga op       : " + op);
        log.debug("execRecarga notarjeta: " + notarjeta);
        log.debug("execRecarga monto    : " + monto);
        log.debug("execRecarga systrace : " + systrace);
        log.debug("execRecarga sComercio: " + sComercio);
        log.debug("execRecarga sTerminal: " + sTerminal);
        log.debug("execRecarga nombre   : " + nombre);
        log.debug("execRecarga fecexp   : " + fecexp);
        log.debug("execRecarga update   : " + update);

        String sSendBuff = mkSendBuff(op, sComercio, sTerminal, notarjeta, "", makeMonto(monto), nombre, systrace, fecexp);
        log.debug(sSendBuff);

        String sResp = "";

        if (update) {
            sResp = cl.sendReceive(sSendBuff);
        } else {
            log.debug("sSendBuff[" + sSendBuff + "]");
            sResp = "00000000000000000000000000000000000000000000000000000";
            msg = "modo no update (false)";
        }
        if (update && cl.getERRORTCP()) {
            Client clRev = new Client(ip, port, timeout);
            log.error("ATENCION: ERROR de comunicaciones. No se puede determinar resultado de la operación. Se envia reverso automaticamente");
            String sSendRevBuff = mkSendBuff("1040", sTerminal, notarjeta, makeMonto(monto), fechahora, systrace);
            log.error("TRY REVERSO: sSendRevBuff[" + sSendRevBuff + "]");
            String sRevResp = clRev.sendReceive(sSendRevBuff);
            int rcRev = setResponse(sRevResp);
            log.error("REVERSO RESPONSE: rc=" + rcRev + " sRevResp[" + sRevResp + "]");
        }
        rc = setResponse(sResp);
//        if (rc != 0) {
//            execReversoRecarga(opReverse,sComercio, comercioCobro, ntarjetaCobro, monto, systraceCobro, fecexpCobro);
//        }
        log.info("Ejecutando [End] rc=" + rc + " msg=" + msg + " autCode=" + authCode + " numbt=" + numbt);

        return (rc);
    }

    public int execReversoRecarga(String op, String sComercio, String sTerminal,
            String notarjeta, String monto, String systrace, String fecexp) {

        log.info("execReversoRecarga [Start]");
        log.info("execReversoRecarga op       : " + op);
        log.info("execReversoRecarga notarjeta: " + notarjeta);
        log.info("execReversoRecarga monto    : " + monto);
        log.info("execReversoRecarga systrace : " + systrace);
        log.info("execReversoRecarga sComercio: " + sComercio);
        log.info("execReversoRecarga sTerminal: " + sTerminal);
        log.info("execReversoRecarga fecexp   : " + fecexp);
        String sSendRevBuff = mkSendBuff(op, sComercio, sTerminal, notarjeta, "", makeMonto(monto), fechahora, systrace, "");
        log.debug("sSendRevBuff: " + sSendRevBuff);
        String sRevResp = cl.sendReceive(sSendRevBuff);
        log.debug("sRevResp: " + sRevResp);
        int rcRev = setResponse(sRevResp);
        if (rcRev != 0) {
            log.error("ERROR REVERSO");
        } else {
            log.info("REVERSO RESPONSE: rc=" + rcRev + " RespCode[" + getRespCode() + "] AuthCode[" + getAuthCode() + "]");
        }
        return rcRev;
    }

    /**
     * Interface de actualizacion de tarjetas (4000)
     *
     * @param systemtrace length[6] pos[0]
     * @param tarjeta length[20] pos[16]
     * @param terminal length[8] pos[36]
     * @param nomcomercio length[40] pos[59]
     * @param cedula length[10] pos[165]
     *
     * @return 0: process OK EAR
     * @return -1; //"ERROR: not response";
     * @return -2; //"ERROR: invalid response length";
     * @return -3; // error en resp
     * @return -4; //"TEBCATRAN ERROR. Operaci�n no realizada";
     * @return -5; //"ATENCION RESPONSE["+respCode+"] NOT ZERO. Operacion no
     * realizada";
     */
    public int execUpdateCard(String systemtrace, String tarjeta, String terminal, String nomcomercio, String cedula, String tipo) {
        int rc = 0;
        log.info("execUpdateCard [Start]");
        log.info("execUpdateCard systemtrace     : " + systemtrace);
        log.info("execUpdateCard tarjeta         : " + tarjeta);
        log.info("execUpdateCard terminal        : " + terminal);
        log.info("execUpdateCard nomcomercio     : " + nomcomercio);
        log.info("execUpdateCard cedula          : " + cedula);
        log.info("execUpdateCard tipo          : " + tipo);
        //log.info("execUpdateCard nro_organizacion: " + this.nro_organizacion);
        if (systemtrace != null) {
            systemtrace = systemtrace.trim();
        }

        if (tipo.equalsIgnoreCase("1")) {
            tipo = "4500";
        }
        if (tipo.equalsIgnoreCase("2")) {
            tipo = "4600";
        }
        if (tipo.equalsIgnoreCase("3")) {
            tipo = "4000";
        }

        String sSendBuff = mkSendBuff(tipo, terminal, tarjeta, cedula, makeMonto("0"), makeMonto("0"), nomcomercio, systemtrace);

        String sResp = cl.sendReceive(sSendBuff);

        if (cl.getERRORTCP()) {
            log.error("execUpdateCard: ATENCION: ERROR de comunicaciones. No se puede determinar resultado de la operación. sResp[" + sResp + "]");
        }

        if (sResp == null) {
            sResp = "-1";
        }
        try {
            if (sResp.length() < 10) {
                log.info("execUpdateCard: invalid tebcatran response [" + sResp + "]");
            } else {
                rc = Integer.parseInt(sResp.substring(11, 13));
                if (sResp.length() > 13) {
                    authCode = sResp.substring(13, 19);
                }
                if (sResp.length() > 19) {
                    msg = sResp.substring(19).trim();
                }
            }

            rc = Integer.parseInt(sResp.substring(11, 13));
        } catch (Exception e) {
            log.info("execUpdateCard: Invalid response [" + sResp + "] RC=" + sResp.substring(11, 13));
            rc = -2;
            msg = "Exception [" + e.getMessage() + "]";
        }

        log.info("execUpdateCard [End] tipo [" + tipo + "] tarjeta[" + tarjeta + "]ci[" + cedula + "] rc=" + rc + " msg=" + msg + " AuthCode=" + getAuthCode());

        return (rc);
    }

    public int execBloqueo(String idCanal, String systemtrace, String tarjeta, String terminal, String nombre, String cedula, String sComercio, String idBloqueo, String expTarjeta) {
        int rc = 0;
        log.info("execBloqueo [Start]");
        log.info("execBloqueo idCanal         : " + idCanal);
        log.info("execBloqueo systemtrace     : " + systemtrace);
        log.info("execBloqueo tarjeta         : " + tarjeta);
        log.info("execBloqueo terminal        : " + terminal);
        log.info("execBloqueo nombre          : " + nombre);
        log.info("execBloqueo cedula          : " + cedula);
        log.info("execBloqueoa sComercio      : " + sComercio);
        log.info("execBloqueo idBloqueo       : " + idBloqueo);
        log.info("execBloqueo Fecha expiracion tarjeta : " + expTarjeta);

        nroOrganizacion = sComercio;

        String sSendBuff = mkSendBuff("460" + idCanal, sComercio ,terminal, tarjeta, cedula, idBloqueo, nombre, systemtrace, expTarjeta);

        String sResp = cl.sendReceive(sSendBuff);

        if (cl.getERRORTCP()) {
            log.error("ATENCION: ERROR de comunicaciones. No se puede determinar resultado de la operación. Se envia reverso automaticamente");
        }

        rc = setResponse("460" + idCanal, sResp);

        log.info("execSaldo [End] rc=" + rc + " msg=" + msg + " AuthCode=" + getAuthCode() + " Numbt=" + getNumbt());

        return (rc);
    }

    public String getNumbt() {
        return numbt;
    }

    public void setNumbt(String numbt) {
        this.numbt = numbt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getFechahora() {
        return fechahora;
    }

    public void setFechahora(String fechahora) {
        this.fechahora = fechahora;
    }

    public String getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(String saldoActual) {
        this.saldoActual = saldoActual;
    }

    public String getSaldoBloqueado() {
        return saldoBloqueado;
    }

    public void setSaldoBloqueado(String saldoBloqueado) {
        this.saldoBloqueado = saldoBloqueado;
    }

    public String getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(String saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    /**
     * @return the nroOrganizacion
     */
    public String getNroOrganizacion() {
        return nroOrganizacion;
    }

    /**
     * @param nroOrganizacion the nroOrganizacion to set
     */
    public void setNroOrganizacion(String nroOrganizacion) {
        this.nroOrganizacion = nroOrganizacion;
    }
}
