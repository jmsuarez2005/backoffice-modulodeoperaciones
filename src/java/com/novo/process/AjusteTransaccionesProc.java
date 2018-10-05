/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process;

import com.novo.constants.BasicConfig;
import com.novo.model.ReporteEmisionRecarga;
import com.novo.model.Tarjeta;
import com.novo.util.DateUtil;
import com.novo.util.ExcelUtil;
import com.novo.util.TextUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author etabban
 */
public class AjusteTransaccionesProc implements BasicConfig {

    private static Logger log = Logger.getLogger(AjusteTransaccionesProc.class);

    private Date fecha;
    private String usuarioSesion;

    public AjusteTransaccionesProc(Date date, String user) {
        this.fecha = date;
        this.usuarioSesion = user;
    }

    public HSSFWorkbook crearWorkBookExcel(List<Tarjeta> tarjetasList) {
        // Se crea el libro
        HSSFWorkbook myWorkBook = new HSSFWorkbook();

        // Se crea una hoja dentro del libro
        HSSFSheet mySheet = myWorkBook.createSheet();
        mySheet.setGridsPrinted(false);
        myWorkBook.setSheetName(0, "Resultado");

        // Se crea una fila dentro de la hoja
        //HSSFRow row = mySheet.createRow(0);
        // Se crea una celda dentro de la fila
        //HSSFCell celda = fila.createCell((short) 0);
        // Se crea el contenido de la celda y se mete en ella.
        //HSSFRichTextString texto = new HSSFRichTextString(tarjetas.get(0).getNroTarjeta());
        //celda.setCellValue(texto);
        int rownum = 0;

        Iterator iteratorTarjetas = tarjetasList.iterator();

        //AJUSTO LA COLUMNA AL TEXTO        
        mySheet.setColumnWidth(0, 4500);
        mySheet.setColumnWidth(1, 3000);
        mySheet.setColumnWidth(2, 7000);
        mySheet.setColumnWidth(3, 7000);
        mySheet.setColumnWidth(4, 9000);

        HSSFRow row = mySheet.createRow(0);

        //CABECERA
        ExcelUtil.crearCeldaEncabezado(row, 0, myWorkBook, mySheet, "Tarjeta");
        ExcelUtil.crearCeldaEncabezado(row, 1, myWorkBook, mySheet, "DNI");
        ExcelUtil.crearCeldaEncabezado(row, 2, myWorkBook, mySheet, "Producto");
        ExcelUtil.crearCeldaEncabezado(row, 3, myWorkBook, mySheet, "Nombre");
        ExcelUtil.crearCeldaEncabezado(row, 4, myWorkBook, mySheet, "Empresa");

        rownum = 1;

        while (iteratorTarjetas.hasNext()) {
            Tarjeta tarjetas = (Tarjeta) iteratorTarjetas.next();

            HSSFRow row1 = mySheet.createRow(rownum++);

            //NUMERO TARJETAS
            String tarjetaEncrypt = tarjetas.getNroTarjeta().substring(0, 6) + "******" + tarjetas.getNroTarjeta().substring(12, tarjetas.getNroTarjeta().length());
            ExcelUtil.crearCeldaAjuste(row1, 0, myWorkBook, mySheet, tarjetaEncrypt);

            //DNI
            ExcelUtil.crearCeldaAjuste(row1, 1, myWorkBook, mySheet, tarjetas.getIdExtPer());

            //PRODUCTO
            ExcelUtil.crearCeldaAjuste(row1, 2, myWorkBook, mySheet, tarjetas.getCardProgram());

            //NOMBRE CLIENTE
            ExcelUtil.crearCeldaAjuste(row1, 3, myWorkBook, mySheet, tarjetas.getNombreCliente());

            //NOMBRE EMPRESA
            ExcelUtil.crearCeldaAjuste(row1, 4, myWorkBook, mySheet, tarjetas.getNombreEmpresa());

        }

//        HSSFWorkbook myWorkBook = new HSSFWorkbook();
//        HSSFSheet mySheet = myWorkBook.createSheet();
//        mySheet.setGridsPrinted(false);
//        String[] nombreProductosPe = {"natural","juridica","juridicaUS","comedores"};
//        String[] nombreProductosCo = {"juridica"};
//        String[] nombreProductosVe = {"natural","juridica"};
//        
//        try{
//            ExcelUtil.crearTitulo(myWorkBook, mySheet, "Reporte de Actividad Diaria, "+DateUtil.formatYYYYMMDD(fecha, "DD/MM/YYYY"),0);
//            
//        
//            this.crearEncabezado(myWorkBook, mySheet, 6);
////            this.llenarExcelPais(myWorkBook,mySheet,9,"Venezuela",this.reporteVe,nombreProductosVe,true);
////            this.llenarExcelPais(myWorkBook,mySheet,11,"Perú",this.reportePe,nombreProductosPe,true);
////            this.llenarExcelPais(myWorkBook,mySheet,15,"Colombia",this.reporteCo,nombreProductosCo,true);       
////            this.crearFilaTotalesExcel(myWorkBook, mySheet, 16, this.totales);
//            
//            
//            
//        } catch(Exception ex){
//            log.error(ex);
//            ex.printStackTrace();
//        }
        return myWorkBook;
    }

    private void crearEncabezado(HSSFWorkbook myWorkBook, HSSFSheet mySheet, int rowIndex) {
        int index = rowIndex;

        HSSFRow row1 = mySheet.createRow(index);   //Fila 1

        ExcelUtil.crearCeldaEncabezado(row1, 3, myWorkBook, mySheet, "Emisiones");
        ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row1.createCell(4));
        mySheet.addMergedRegion(new CellRangeAddress(index, index, 3, 4));

        ExcelUtil.crearCeldaEncabezado(row1, 5, myWorkBook, mySheet, "Recargas");
        mySheet.addMergedRegion(new CellRangeAddress(index, index, 5, 9));

        for (int i = 6; i <= 9; i++) {
            ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row1.createCell(i));
        }

        index = index + 1; //Apuntando a la siguiente fila       
        HSSFRow row2 = mySheet.createRow(index); //Fila 2

        ExcelUtil.crearCeldaEncabezado(row2, 1, myWorkBook, mySheet, "País");
        mySheet.addMergedRegion(new CellRangeAddress(index, index + 1, 1, 1));

        ExcelUtil.crearCeldaEncabezado(row2, 2, myWorkBook, mySheet, "Producto");
        mySheet.addMergedRegion(new CellRangeAddress(index, index + 1, 2, 2));

        ExcelUtil.crearCeldaEncabezado(row2, 3, myWorkBook, mySheet, "Día " + DateUtil.format("d/MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index, index + 1, 3, 3));

        ExcelUtil.crearCeldaEncabezado(row2, 4, myWorkBook, mySheet, "Acumulada " + DateUtil.format("MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index, index + 1, 4, 4));

        ExcelUtil.crearCeldaEncabezado(row2, 5, myWorkBook, mySheet, "Día " + DateUtil.format("dd/MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index, index, 5, 6));

        ExcelUtil.crearCeldaEncabezado(row2, 7, myWorkBook, mySheet, "Acumulada " + DateUtil.format("MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index, index, 7, 9));
        ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row2.createCell(9));

        index = index + 1;//Apuntando a la siguiente fila
        HSSFRow row3 = mySheet.createRow(index); //Fila 3

        ExcelUtil.crearCeldaEncabezado(row3, 5, myWorkBook, mySheet, "Moneda Local");
        ExcelUtil.crearCeldaEncabezado(row3, 6, myWorkBook, mySheet, "US$");
        ExcelUtil.crearCeldaEncabezado(row3, 7, myWorkBook, mySheet, "Moneda Local");
        ExcelUtil.crearCeldaEncabezado(row3, 8, myWorkBook, mySheet, "US$");
        ExcelUtil.crearCeldaEncabezado(row3, 9, myWorkBook, mySheet, "%Rep");

        for (int i = 1; i <= 4; i++) {
            ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row3.createCell(i));
        }

        for (int i = 0; i < 12; i++) {
            mySheet.setColumnWidth(i, 4500);
        }
    }

    private void llenarExcelPais(HSSFWorkbook myWorkBook, HSSFSheet mySheet, int rowIndex, String pais, Map<String, ReporteEmisionRecarga> productos, String[] nombreProductos, boolean global) {

        int index = rowIndex;

        List<ReporteEmisionRecarga> listaPais = new ArrayList();
        for (int i = 0; i < nombreProductos.length; i++) {
            listaPais.add(productos.get(nombreProductos[i]));
        }

        Iterator iteratorPeru = listaPais.iterator();

        while (iteratorPeru.hasNext()) {
            ReporteEmisionRecarga reporte = (ReporteEmisionRecarga) iteratorPeru.next();

            HSSFRow row = mySheet.createRow(index);
            ExcelUtil.crearCeldaBold(row, 1, myWorkBook, mySheet, pais);
            ExcelUtil.crearCeldaBold(row, 2, myWorkBook, mySheet, reporte.getProducto());
            ExcelUtil.crearCelda(row, 3, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesFecha()) + "");
            ExcelUtil.crearCelda(row, 4, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesAcum()) + "");
            ExcelUtil.crearCelda(row, 5, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaLocal()) + "");
            ExcelUtil.crearCelda(row, 6, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaDolares()) + "");
            ExcelUtil.crearCelda(row, 7, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumLocal()) + "");
            ExcelUtil.crearCelda(row, 8, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumDolares()) + "");
            if (global) {
                ExcelUtil.crearCelda(row, 9, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getPorcRepGlobal()) + " %");
            } else {
                ExcelUtil.crearCelda(row, 9, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getPorcRep()) + " %");
            }

            index++;
        }
        mySheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + listaPais.size() - 1, 1, 1));

    }

    public void crearFilaTotalesExcel(HSSFWorkbook myWorkBook, HSSFSheet mySheet, int rowIndex, ReporteEmisionRecarga reporte) {
        HSSFRow rowTotales = mySheet.createRow(rowIndex);
        ExcelUtil.crearCeldaBold(rowTotales, 1, myWorkBook, mySheet, "Totales");
        ExcelUtil.crearCeldaBold(rowTotales, 2, myWorkBook, mySheet, reporte.getProducto());
        ExcelUtil.crearCeldaBold(rowTotales, 3, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesFecha()) + "");
        ExcelUtil.crearCeldaBold(rowTotales, 4, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesAcum()) + "");
        ExcelUtil.crearCeldaBold(rowTotales, 5, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaLocal()) + "");
        ExcelUtil.crearCeldaBold(rowTotales, 6, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaDolares()) + "");
        ExcelUtil.crearCeldaBold(rowTotales, 7, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumLocal()) + "");
        ExcelUtil.crearCeldaBold(rowTotales, 8, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumDolares()) + "");
        ExcelUtil.crearCeldaBold(rowTotales, 9, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getPorcRep()) + " %");
        mySheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 2));
    }

    public void crearFilaCambiosMonedaExcel(HSSFWorkbook myWorkBook, HSSFSheet mySheet, int rowIndex, ReporteEmisionRecarga reporte) {
        HSSFRow rowMonedas = mySheet.createRow(rowIndex);
        ExcelUtil.crearCeldaEncabezado(rowMonedas, 1, myWorkBook, mySheet, "Cambios de Moneda");
        ExcelUtil.crearCeldaEncabezado(rowMonedas, 2, myWorkBook, mySheet, "");
        mySheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 2));

    }

}
