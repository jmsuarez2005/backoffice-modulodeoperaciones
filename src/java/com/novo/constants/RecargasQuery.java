/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants;

/**
 *
 * @author jorojas
 */
public interface RecargasQuery {
    
                        /** Venezuela **/    
    String recargasPersonaNatVeQuery=""
            + "select  count(distinct(a.NRO_CUENTA))CANT_TARJ, "
            + "count(*) CANTIDAD, "
            + "sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_TEBCA a, MAESTRO_CONSOLIDADO_TEBCA b "
            + "where a.NRO_CUENTA = b.NRO_CUENTA "
            + "and a.FEC_TRANSACCION "
            + "between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') "
            + "and  SUBSTR(a.NRO_CUENTA,1,12) in ('000042205000','000042205010','000042205011','000042205014','000062198414','000062198418','000062198419','000052674918') "
            + "and b.NRO_CORPORA_AFINI in ('0','1000000000','9000000000') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION'";
                                                                                     
    String recargasPersonaJurDiaVeQuery=""
            + "select sum(a.nmonto)MONTO "
            +  "from teb_lote a, empresas b "
            +  "where a.accodcia = b.accodcia "
            +  "and a.ctipolote IN (2,5) "
            +  "and a.cestatus = '4' "
            +  "and year(a.dtfechorproceso) = '$YEAR' "
            +  "and month(a.dtfechorproceso) = '$MONTH' "
            +  "and day (a.dtfechorproceso) = '$DAY' ";
    
    String recargasPersonaJurMesVeQuery=""
            + "select sum(a.nmonto)MONTO "
            +  "from teb_lote a, empresas b "
            +  "where a.accodcia = b.accodcia "
            +  "and a.ctipolote IN (2,5) "
            +  "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
//            +  "and year(a.dtfechorproceso) = '$YEAR' "
//            +  "and month(a.dtfechorproceso) = '$MONTH'";

                            /** Colombia **/    
    String recargasPersonaJurDiaCoQuery=""
            + "select  sum(a.nmonto)MONTO "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia "
            + "and a.ctipolote IN (2,5) "
            + "and a.cestatus = '4' "
            + "and year(a.dtfechorproceso) = '$YEAR' "
            + "and month(a.dtfechorproceso) = '$MONTH' "
            + "and day (a.dtfechorproceso) = '$DAY' ";
    
    String recargasPersonaJurMesCoQuery=""
            + "select  sum(a.nmonto)MONTO "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia "
            + "and a.ctipolote IN (2,5) "
            + "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
//            + "and year(a.dtfechorproceso) = '$YEAR' "
//            + "and month(a.dtfechorproceso) = '$MONTH' ";
    
    String recargasAbonosMaestroDiaCoQuery = ""
            + "select sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_Tebca a "
            + "where to_char(a.FEC_TRANSACCION,'dd/mm/yyyy')='$FECHA' "
            + "and SUBSTR(a.NRO_CUENTA,1,12) in ('000048759765','000048759768','000048759778') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION' AND UPPER(a.NOMBRE_CIUDAD_EDO) like ('RECARGA TRAN%')";
    
    String recargasAbonosMaestroMesCoQuery = ""
            + "select sum(a.MON_TRANSACCION/100) MONTO "
            + "from DETALLE_TRANSACCIONES_Tebca a "
            + "where a.FEC_TRANSACCION between TO_DATE('$FECHAINI','$FORMATO') and TO_DATE('$FECHAFIN','$FORMATO') "
            + "and SUBSTR(a.NRO_CUENTA,1,12) in ('000048759765','000048759768','000048759778') "
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
            + "and SUBSTR(a.NRO_CUENTA,1,12) in ('000054824200') "
            + "and a.COD_TRANSACCION = '$CODTRANSACCION' "
            + "group by to_char(a.FEC_TRANSACCION,'yyyy-mm'), SUBSTR(a.NRO_CUENTA,1,12) ";
    
    String recargasPersonaJurDiaPeQuery=""
            + "select sum(a.nmonto)MONTO "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia "
            + "and a.ctipolote IN (2,5) "
            + "and a.cestatus = '4' "
            + "and year(a.dtfechorproceso) = '$YEAR' "
            + "and month(a.dtfechorproceso) = '$MONTH' "
            + "and day (a.dtfechorproceso) = '$DAY' ";
    
    String recargasPersonaJurMesPeQuery=""
            + "select sum(a.nmonto)MONTO "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia "
            + "and a.ctipolote IN (2,5) "
            + "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
//            + "and year(a.dtfechorproceso) = '$YEAR' "
//            + "and month(a.dtfechorproceso) = '$MONTH' ";

}                                                                                                                   
              

