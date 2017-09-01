/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 *
 * @author jorojas
 */
public class ExcelUtil {
    
    public static void crearCelda(HSSFRow row,int index,HSSFWorkbook myWorkBook,HSSFSheet mySheet,String content){
        HSSFCell cell = row.createCell(index);
        cell.setCellValue(new HSSFRichTextString(content));
        setEstiloCelda(myWorkBook, mySheet, cell);
    }
    
    public static void crearCeldaAjuste(HSSFRow row,int index,HSSFWorkbook myWorkBook,HSSFSheet mySheet,String content){
        HSSFCell cell = row.createCell(index);
        cell.setCellValue(new HSSFRichTextString(content));
        setEstiloCeldaAjuste(myWorkBook, mySheet, cell);
    }
    
    public static void crearCeldaBold(HSSFRow row,int index,HSSFWorkbook myWorkBook,HSSFSheet mySheet,String content){
        HSSFCell cell = row.createCell(index);
        cell.setCellValue(new HSSFRichTextString(content));
        setEstiloCelda(myWorkBook, mySheet, cell);
        cell.getCellStyle().getFont(myWorkBook).setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    }
    
    public static void crearCeldaEncabezado(HSSFRow row,int index,HSSFWorkbook myWorkBook,HSSFSheet mySheet,String content){
        HSSFCell cell = row.createCell(index);
        cell.setCellValue(new HSSFRichTextString(content));
        setEstiloEncabezado(myWorkBook, mySheet, cell);
    }
    
    public static void crearTitulo( HSSFWorkbook myWorkBook,HSSFSheet mySheet,String title,int row){
        
        // Write a String in Cell 1B
        HSSFRow row1 = mySheet.createRow(row);
        HSSFCell cell1B = row1.createCell(1);
        cell1B.setCellValue(new HSSFRichTextString(title));

        // Style Font in Cell 1B
        HSSFCellStyle cellStyle = myWorkBook.createCellStyle();
        cellStyle = myWorkBook.createCellStyle();
        HSSFFont hSSFFont = myWorkBook.createFont();
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setFontHeightInPoints((short) 16);
        hSSFFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        hSSFFont.setColor(HSSFColor.DARK_BLUE.index);
        cellStyle.setFont(hSSFFont);
        cell1B.setCellStyle(cellStyle);
        
    }
    
    
    public static void setEstiloEncabezado(HSSFWorkbook myWorkBook,HSSFSheet mySheet,HSSFCell myCell){
        HSSFCellStyle cellStyle = myWorkBook.createCellStyle();
        HSSFFont hSSFFont = myWorkBook.createFont();
        
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
//        hSSFFont.setFontHeightInPoints((short) 16);
        hSSFFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        hSSFFont.setColor(HSSFColor.WHITE.index);

        cellStyle.setFont(hSSFFont);
        
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        cellStyle.setWrapText(true);
        
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        
        myCell.setCellStyle(cellStyle);
    }
    
     public static void setEstiloCelda(HSSFWorkbook myWorkBook,HSSFSheet mySheet,HSSFCell myCell){
        HSSFCellStyle cellStyle = myWorkBook.createCellStyle();
        HSSFFont hSSFFont = myWorkBook.createFont();
        
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setColor(HSSFColor.BLACK.index);

        cellStyle.setFont(hSSFFont);
        
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        cellStyle.setWrapText(true);
        
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        
        myCell.setCellStyle(cellStyle);
    }
     
     public static void setEstiloCeldaAjuste(HSSFWorkbook myWorkBook,HSSFSheet mySheet,HSSFCell myCell){
        HSSFCellStyle cellStyle = myWorkBook.createCellStyle();
        HSSFFont hSSFFont = myWorkBook.createFont();
        
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setColor(HSSFColor.BLACK.index);

        cellStyle.setFont(hSSFFont);
        
        //cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        cellStyle.setWrapText(true);
        
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        
        myCell.setCellStyle(cellStyle);
    }
    
}
