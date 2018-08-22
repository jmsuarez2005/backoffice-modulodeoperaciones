/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants;

import static com.novo.constants.BasicConfig.ANIO_RECONVERSION_VZLA;
import static com.novo.constants.BasicConfig.DIA_RECONVERSION_VZLA;
import static com.novo.constants.BasicConfig.MES_RECONVERSION_VZLA;

/**
 *
 * @author jorojas
 */
public interface RecargasQuery {
    
                        /** Venezuela **/    
    
    String recargasPersonaNatDiaVeQuery=""
            + "select  count(distinct(a.NRO_CUENTA))CANT_TARJ, "
            + "count(*) CANTIDAD, "
            +  "CASE " 
            +  "WHEN $YEAR$MONTH$DAY <= " + ANIO_RECONVERSION_VZLA + MES_RECONVERSION_VZLA + DIA_RECONVERSION_VZLA + " THEN ROUND(sum(a.MON_TRANSACCION/100)/100000, 2) " 
            +  "ELSE sum(a.MON_TRANSACCION/100) " 
            +  "END AS MONTO "
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            + "where a.NRO_CUENTA = b.NRO_CUENTA "
            + "and a.FEC_TRANSACCION "
            + "between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') "
            //+ "and  SUBSTR(a.NRO_CUENTA,1,12) in ('000042205000','000042205010','000042205011','000042205014','000062198414','000062198418','000062198419','000052674918','000060484200') "
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION'";
    
    String recargasPersonaNatMesVeQuery=""
            + "select sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            + "where a.NRO_CUENTA = b.NRO_CUENTA "
            + "and a.FEC_TRANSACCION "
            + "between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') "
            //+ "and  SUBSTR(a.NRO_CUENTA,1,12) in ('000042205000','000042205010','000042205011','000042205014','000062198414','000062198418','000062198419','000052674918','000060484200') "
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION'";
    
    String recargasPersonaNatMesVeReconversionQuery=""
            + "select ROUND(sum(a.MON_TRANSACCION/100)/100000, 2) as MONTO "
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            + "where a.NRO_CUENTA = b.NRO_CUENTA "
            + "and a.FEC_TRANSACCION "
            + "between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') "
            //+ "and  SUBSTR(a.NRO_CUENTA,1,12) in ('000042205000','000042205010','000042205011','000042205014','000062198414','000062198418','000062198419','000052674918','000060484200') "
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION'";
    
    
    String recargasPersonaNatMesVeReconversionUnionQuery=""
            + "SELECT CASE WHEN $DAY <= " + DIA_RECONVERSION_VZLA + " THEN \n"
            +  "(SELECT ROUND(sum(a.MON_TRANSACCION/100)/100000, 2) \n" 
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            +  "where a.NRO_CUENTA = b.NRO_CUENTA and a.FEC_TRANSACCION between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO')"
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION') \n"
            + "ELSE "
            +  "(SELECT ROUND(sum(a.MON_TRANSACCION/100)/100000, 2) \n" 
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            +  "where a.NRO_CUENTA = b.NRO_CUENTA and a.FEC_TRANSACCION between to_date('$FECHAINI','$FORMATO') and to_date('" + DIA_RECONVERSION_VZLA + "-" + MES_RECONVERSION_VZLA + "-" + ANIO_RECONVERSION_VZLA + " 23-59-59','$FORMATO')"
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION') \n"
            + " + "
            +  "(SELECT sum(a.MON_TRANSACCION/100) \n" 
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            +  "where a.NRO_CUENTA = b.NRO_CUENTA and a.FEC_TRANSACCION between to_date('" + (Integer.parseInt(DIA_RECONVERSION_VZLA) + 1) + "-" + MES_RECONVERSION_VZLA + "-" + ANIO_RECONVERSION_VZLA + " 00:00:00','$FORMATO') and to_date('$FECHAFIN','$FORMATO')"
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION') \n"
            + "END AS MONTO "
            + "FROM DUAL ";
                                                                                     
//    String recargasPersonaJurDiaVeQuery=""
//            + "select sum(a.nmonto)MONTO "
//            +  "from teb_lote a, empresas b "
//            +  "where a.accodcia = b.accodcia "
//            +  "and a.ctipolote IN (2,5) "
//            +  "and a.cestatus = '4' "
//            +  "and year(a.dtfechorproceso) = '$YEAR' "
//            +  "and month(a.dtfechorproceso) = '$MONTH' "
//            +  "and day (a.dtfechorproceso) = '$DAY' ";                                    
    String recargasPersonaJurDiaVeQuery=""
            + "select "
            +  "CASE " 
            +  "WHEN $YEAR$MONTH$DAY <= " + ANIO_RECONVERSION_VZLA + MES_RECONVERSION_VZLA + DIA_RECONVERSION_VZLA + " THEN ROUND(sum(a.nmonto/100)/100000, 2) " 
            +  "ELSE sum(a.nmonto/100) " 
            +  "END AS MONTO "
            +  "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            +  "where a.accodcia = b.COD_CLIENTE "
            +  "and a.ctipolote IN ('2','5') "
            +  "and a.cestatus = '4' "
            +  "and EXTRACT(YEAR FROM a.dtfechorproceso) = '$YEAR' "
            +  "and EXTRACT(MONTH FROM a.dtfechorproceso) = '$MONTH' "
            +  "and EXTRACT(DAY FROM a.dtfechorproceso) = '$DAY' ";
    
//    String recargasPersonaJurMesVeQuery=""
//            + "select sum(a.nmonto)MONTO "
//            +  "from teb_lote a, empresas b "
//            +  "where a.accodcia = b.accodcia "
//            +  "and a.ctipolote IN (2,5) "
//            +  "and a.cestatus = '4' "
//            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
//            +  "and year(a.dtfechorproceso) = '$YEAR' "
//            +  "and month(a.dtfechorproceso) = '$MONTH'";    
    String recargasPersonaJurMesVeQuery=""
            + "select sum(a.nmonto)MONTO "
            +  "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            +  "where a.accodcia = b.COD_CLIENTE "
            +  "and a.ctipolote IN ('2','5') "
            +  "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','YYYY-MM-DD HH24:MI') and TO_DATE('$FECHAFIN','YYYY-MM-DD HH24:MI') ";
    
    String recargasPersonaJurMesVeReconversionQuery=""
            + "select ROUND(sum(a.nmonto)/100000,2) as MONTO "
            +  "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            +  "where a.accodcia = b.COD_CLIENTE "
            +  "and a.ctipolote IN ('2','5') "
            +  "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','YYYY-MM-DD HH24:MI') and TO_DATE('$FECHAFIN','YYYY-MM-DD HH24:MI') ";
    
    
    
    String recargasPersonaJurMesVeReconversionUnionQuery=""
            + "SELECT CASE WHEN $DAY <= " + DIA_RECONVERSION_VZLA + " THEN \n"
            +  "(select ROUND(sum(a.nmonto)/100000,2) \n" 
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            +  "where a.accodcia = b.COD_CLIENTE and a.ctipolote IN ('2','5') and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','YYYY-MM-DD HH24:MI') and TO_DATE('$FECHAFIN','YYYY-MM-DD HH24:MI')) \n"
            + "ELSE "
            +  "(select ROUND(sum(a.nmonto)/100000,2) \n" 
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            +  "where a.accodcia = b.COD_CLIENTE and a.ctipolote IN ('2','5') and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','YYYY-MM-DD HH24:MI') and TO_DATE('" + ANIO_RECONVERSION_VZLA + "-" + MES_RECONVERSION_VZLA + "-" + DIA_RECONVERSION_VZLA + " 23:59','YYYY-MM-DD HH24:MI')) \n"
            + " + "
            +  "(select sum(a.nmonto) \n" 
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            +  "where a.accodcia = b.COD_CLIENTE and a.ctipolote IN ('2','5') and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('" + ANIO_RECONVERSION_VZLA + "-" + MES_RECONVERSION_VZLA + "-" + (Integer.parseInt(DIA_RECONVERSION_VZLA) + 1) + " 00:00','YYYY-MM-DD HH24:MI') and TO_DATE('$FECHAFIN','YYYY-MM-DD HH24:MI')) \n"
            + "END AS MONTO "
            + "FROM DUAL ";

                            /** Colombia **/    
//    String recargasPersonaJurDiaCoQuery=""
//            + "select  sum(a.nmonto)MONTO "
//            + "from teb_lote a, empresas b "
//            + "where a.accodcia = b.accodcia "
//            + "and a.ctipolote IN (2,5) "
//            + "and a.cestatus = '4' "
//            + "and year(a.dtfechorproceso) = '$YEAR' "
//            + "and month(a.dtfechorproceso) = '$MONTH' "
//            + "and day (a.dtfechorproceso) = '$DAY' ";
    String recargasPersonaJurDiaCoQuery=""
            + "select  sum(a.nmonto)MONTO "
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            + "where a.accodcia = b.COD_CLIENTE "
            + "and a.ctipolote IN ('2','5') "
            + "and a.cestatus = '4' "
            + "and EXTRACT(YEAR FROM a.dtfechorproceso) = '$YEAR' "
            + "and EXTRACT(MONTH FROM a.dtfechorproceso) = '$MONTH' "
            + "and EXTRACT(DAY FROM a.dtfechorproceso) = '$DAY' ";
    
//    String recargasPersonaJurMesCoQuery=""
//            + "select  sum(a.nmonto)MONTO "
//            + "from teb_lote a, empresas b "
//            + "where a.accodcia = b.accodcia "
//            + "and a.ctipolote IN (2,5) "
//            + "and a.cestatus = '4' "
//            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
//            + "and year(a.dtfechorproceso) = '$YEAR' "
//            + "and month(a.dtfechorproceso) = '$MONTH' ";
    String recargasPersonaJurMesCoQuery=""
            + "select  sum(a.nmonto)MONTO "
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            + "where a.accodcia = b.COD_CLIENTE "
            + "and a.ctipolote IN ('2','5') "
            + "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','YYYY-MM-DD HH24:MI') and TO_DATE('$FECHAFIN','YYYY-MM-DD HH24:MI') ";
    
    String recargasAbonosMaestroDiaCoQuery = ""
            + "select sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_ORG a "
            + "where to_char(a.FEC_TRANSACCION,'dd/mm/yyyy')='$FECHA' "
            //+ "and SUBSTR(a.NRO_CUENTA,1,12) in ('000048759765','000048759768','000048759778') "
            + "and SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION' AND UPPER(a.NOMBRE_CIUDAD_EDO) like ('RECARGA TRAN%')";
    
    String recargasAbonosMaestroMesCoQuery = ""
            + "select sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_ORG a "
            + "where a.FEC_TRANSACCION between TO_DATE('$FECHAINI','$FORMATO') and TO_DATE('$FECHAFIN','$FORMATO') "
            //+ "and SUBSTR(a.NRO_CUENTA,1,12) in ('000048759765','000048759768','000048759778') "
            + "and SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION' AND UPPER(a.NOMBRE_CIUDAD_EDO) like ('RECARGA TRAN%')";
    
                            /** Perú **/            
    String recargasPersonaNatPeQuery=""
            + "select to_char(a.FEC_TRANSACCION,'yyyy-mm')fecha, "
            + "SUBSTR(a.NRO_CUENTA,1,12) BIN, "
            + "count(distinct(a.NRO_CUENTA))CANT_TARJ, "
            + "count(*) CANTIDAD, "
            + "sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            + "where a.NRO_CUENTA = b.NRO_CUENTA "
            + "and a.FEC_TRANSACCION "
            + "between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') "
            //+ "and SUBSTR(a.NRO_CUENTA,1,12) in ('000054824200') "
            + "and SUBSTR(a.NRO_CUENTA,1,12) in ($BINES$) "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION' "
            + "group by to_char(a.FEC_TRANSACCION,'yyyy-mm'), SUBSTR(a.NRO_CUENTA,1,12) ";
    
//    String recargasPersonaJurDiaPeQuery=""
//            + "select sum(a.nmonto)MONTO "
//            + "from teb_lote a, empresas b "
//            + "where a.accodcia = b.accodcia "
//            + "and a.ctipolote IN (2,5) "
//            + "and a.cestatus = '4' "
//            + "and year(a.dtfechorproceso) = '$YEAR' "
//            + "and month(a.dtfechorproceso) = '$MONTH' "
//            + "and day (a.dtfechorproceso) = '$DAY' ";
    String recargasPersonaJurDiaPeQuery=""
            + "select sum(a.nmonto)MONTO "
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            + "where a.accodcia = b.COD_CLIENTE "
            + "and a.ctipolote IN ('2','5') "
            + "and a.cestatus = '4' "
            + "and EXTRACT(YEAR FROM a.dtfechorproceso) = '$YEAR' "
            + "and EXTRACT(MONTH FROM a.dtfechorproceso) = '$MONTH' "
            + "and EXTRACT(DAY FROM a.dtfechorproceso) = '$DAY' ";
    
//    String recargasPersonaJurMesPeQuery=""
//            + "select sum(a.nmonto)MONTO "
//            + "from teb_lote a, empresas b "
//            + "where a.accodcia = b.accodcia "
//            + "and a.ctipolote IN (2,5) "
//            + "and a.cestatus = '4' "
//            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
//            + "and year(a.dtfechorproceso) = '$YEAR' "
//            + "and month(a.dtfechorproceso) = '$MONTH' ";
    String recargasPersonaJurMesPeQuery=""
            + "select sum(a.nmonto)MONTO "
            + "from teb_lote a, MAESTRO_CLIENTES_TEBCA b "
            + "where a.accodcia = b.COD_CLIENTE "
            + "and a.ctipolote IN ('2','5') "
            + "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','YYYY-MM-DD HH24:MI') and TO_DATE('$FECHAFIN','YYYY-MM-DD HH24:MI') ";

}                                                                                                                   
              

