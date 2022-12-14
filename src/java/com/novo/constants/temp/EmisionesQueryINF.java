/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants.temp;

/**
 *
 * @author jorojas
 */
public interface EmisionesQueryINF {
    
                        /** Venezuela **/
    String obtenerTarjEmitidasPersonaJurDiaVeQuery=""
            + "select sum(a.ncantregs)cant_plast "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia and a.ctipolote = '1' and a.cestatus = '4' "
            + "and year(a.dtfechorproceso) = '$YEAR' and month(a.dtfechorproceso) = '$MONTH' and day(a.dtfechorproceso) = '$DAY'";
    
    String obtenerTarjEmitidasPersonaJurMesVeQuery=""
            + "select sum(a.ncantregs)cant_plast "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia and a.ctipolote = '1' and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";

    String obtenerTarjEmitidasPersonaNatDiaVeQuery=""
            + "select count(*)cant_plast "
            + "from TEBCP_AFILIACION "
            + "where TO_CHAR(TIMESTAMP,'$FORMATO') = '$FECHA' "
            + "ORDER BY 1 ";
    
    String obtenerTarjEmitidasPersonaNatMesVeQuery=""
            + "select SUM(count(*))cant_plast "
            + "from TEBCP_AFILIACION "
            + "where TIMESTAMP "
            + "between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') "
            + "group by to_date(to_char(TIMESTAMP,'mm-yyyy'),'mm-yyyy') "
            + "ORDER BY 1 ";

                            /** Colombia **/
    String obtenerTarjEmitidasPersonaJurDiaCoQuery=""
            + "select sum(a.ncantregs)cant_plast "
            + "from teb_lote a "
            + "inner join empresas b on a.accodcia = b.accodcia "
            + "where (a.ctipolote = '3' and b.acrif IN ($ACRIF$) or a.ctipolote = '1' ) "
            + "and a.cestatus = '4' "
            + "and year(a.dtfechorproceso) = '$YEAR' "
            + "and month(a.dtfechorproceso) = '$MONTH' "
            + "and day (a.dtfechorproceso) = '$DAY' ";
    
    String obtenerTarjEmitidasPersonaJurMesCoQuery=""
            + "select sum(a.ncantregs)cant_plast "
            + "from teb_lote a "
            + "inner join empresas b on a.accodcia = b.accodcia "
            + "where (a.ctipolote = '3' and b.acrif IN ($ACRIF$) or a.ctipolote = '1' ) "
            + "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";

                            /** Per?? **/    
    String obtenerTarjEmitidasPersonaNatMesPeQuery=""
            + "SELECT COUNT(TARJETA)cant_plast,SUM(MONTO) "
            + "FROM NOVO_TRANSACCIONES_POST "
            + "WHERE TARJETA LIKE '54824200%' "
            + "AND DESCRIPCION = 'ACTIVACION LATODO' "
            + "AND COD_OPERACION = '20' "
            + "AND FECHA between to_date('$FECHAINI','$FORMATO') and to_date('$FECHAFIN','$FORMATO') ";
    
    String obtenerTarjEmitidasPersonaNatDiaPeQuery=""
            + "SELECT COUNT(TARJETA)cant_plast,SUM(MONTO) "
            + "FROM NOVO_TRANSACCIONES_POST "
            + "WHERE TARJETA LIKE '54824200%' "
            + "AND DESCRIPCION = 'ACTIVACION LATODO' "
            + "AND COD_OPERACION = '20' "
            + "AND TO_CHAR(FECHA,'$FORMATO') = '$FECHA' ";
                                                                                                    
    String obtenerTarjEmitidasPersonaJurDiaPeQuery=""
            + "select sum(a.ncantregs)cant_plast "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia "
            + "and a.ctipolote IN ('1','3') "
            + "and a.cestatus = '4' "
            + "and year(a.dtfechorproceso) = '$YEAR' "
            + "and month(a.dtfechorproceso) = '$MONTH' "
            + "and day (a.dtfechorproceso) = '$DAY' ";
    
    String obtenerTarjEmitidasPersonaJurMesPeQuery=""
            + "select sum(a.ncantregs)cant_plast "
            + "from teb_lote a, empresas b "
            + "where a.accodcia = b.accodcia "
            + "and a.ctipolote IN ('1','3') "
            + "and a.cestatus = '4' "
            + "and a.dtfechorproceso between TO_DATE('$FECHAINI','%Y-%m-%d %R') and TO_DATE('$FECHAFIN','%Y-%m-%d %R') ";
}
