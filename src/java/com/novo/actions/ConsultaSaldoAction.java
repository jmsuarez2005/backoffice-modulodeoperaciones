package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.dao.ConsultaDAO;
import com.novo.model.Ajuste;
import com.novo.model.TAjuste;
import com.novo.model.TBloqueo;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.objects.util.Utils;
import com.novo.process.ReporteTransacciones;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ConsultaSaldoAction extends ActionSupport
  implements BasicConfig
{
  private static Logger log = Logger.getLogger(ConsultaSaldoAction.class);
  private String message = "";
  private String nroTarjeta;
  private List<Tarjeta> tarjetas;
  private List<Tarjeta> tarjetasAct;
  private List<Tarjeta> tarjetasRes;
  private String selectedAjuste = "";
  private String selectedCampoValue = "";
  private String selectedBloqueo;
  private UsuarioSesion usuarioSesion;
  private List<TAjuste> tipoAjustes;
  private List<Ajuste> ajustex;
  private List<TBloqueo> tipoBloqueo;
  String pais;
  String reportFile;
  InputStream inputStream;
  private File file;
  private String contentType;
  private String filename;

  public ConsultaSaldoAction()
  {
    this.tarjetas = new ArrayList();
    this.tarjetasAct = new ArrayList();
    this.tarjetasRes = new ArrayList();
  }

  public String execute()
  {
    return "success";
  }

  public List<Ajuste> getAjustex() {
    return this.ajustex;
  }

  public void setAjustex(List<Ajuste> ajustex) {
    this.ajustex = ajustex;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String reportes() throws FileNotFoundException, IOException, InvalidFormatException
  {
    Properties properties = Utils.getConfig("operaciones.properties");

    this.reportFile = "Reporte Saldo.xls";
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    InputStream buffer1 = new FileInputStream(properties.getProperty("consulta"));
    log.info(properties.getProperty("consulta"));

    Workbook workbook = WorkbookFactory.create(buffer1);
    workbook.write(boas);

    setInputStream(new ByteArrayInputStream(boas.toByteArray()));

    return "excel";
  }

  public String Upload() {
    ReporteTransacciones business = new ReporteTransacciones();
    Properties properties = Utils.getConfig("operaciones.properties");
    UsuarioSesion usuario = (UsuarioSesion)ActionContext.getContext().getSession().get("usuarioSesion");
    this.pais = ((String)ActionContext.getContext().getSession().get("pais"));
    usuario.getIdUsuario();
    Tarjeta tar = new Tarjeta();
    Ajuste ajusteB = new Ajuste();
    this.ajustex = new ArrayList();

    boolean procesoOk = true;
    ConsultaDAO consulta = new ConsultaDAO("operaciones", databases, this.pais);
    try {
      log.info(this.file.getAbsolutePath() + " " + this.file.getCanonicalPath());

      File file2 = new File(this.file.getPath() + this.filename);
      this.file.renameTo(file2);
      InputStream buffer = new FileInputStream(file2.getAbsolutePath());

      Workbook workbook = WorkbookFactory.create(buffer);
      HSSFWorkbook workbook1 = new HSSFWorkbook();
      workbook.write(new FileOutputStream(this.file));

      Sheet sheet = workbook.getSheetAt(0);
      Sheet sheet1 = workbook1.createSheet();
      String tarjetaString = "";
      String montoString = "";
      double tarjetaDouble = 0.0D;
      double montoDouble = 0.0D;
      int i = 0;
      do {
        Row row = sheet.getRow(i);
        Row row1 = sheet1.createRow(i);

        if (row.getCell(0).getCellType() == 3)
        {
          break;
        }

        if (row.getCell(0).getCellType() == 0) {
          tarjetaDouble = row.getCell(0).getNumericCellValue();
          tarjetaString = String.valueOf(tarjetaDouble);
        }
        else {
          tarjetaString = row.getCell(0).getStringCellValue();
        }

        ajusteB.setTarjeta(tarjetaString);
        row1.createCell(0).setCellValue(tarjetaString);

        tar.setNroTarjeta(tarjetaString);
        if (!tarjetaString.matches("\\d{16}")) {
          this.message = "Error con el formato de la tarjeta";
          procesoOk = false;
          break;
        }
        
         this.ajustex.add(ajusteB);
       
        if (business.checkTarjetas(this.ajustex).compareToIgnoreCase("error") == 0)
        {
          this.message = "Error, Tarjeta Inexistente.";
          return "success";
        }else{
        
        if(procesoOk)
        {
            this.message = "Archivo cargado con exito";
        }
        
        FileOutputStream prueba = new FileOutputStream(properties.getProperty("consulta"));
        workbook1.write(prueba);
        prueba.close();

        ActionContext.getContext().getSession().put("tarjetasAct", this.tarjetasAct);
        
        
        }
        

        consulta.RegistrarConsultaDAO(tar);
        row1.createCell(1).setCellValue(tar.getSaldoDisponible());

        this.ajustex.add(ajusteB);
        this.tarjetasRes.add(tar);

        tar = new Tarjeta();
        log.info("tarjeta [" + tarjetaString + "] ");

        i++;
        log.info("por favor" + sheet.getRow(i));
      }while ((!tarjetaString.equals("")) && (sheet.getRow(i) != null));

      if (i > 500) {
        this.message = "Numero de registros en el archivo excedido";
        return "success";
      }
//      if (procesoOk)
//      {
//        this.message = "Carga de Archivo Exitosa. ";
//        if (business.checkTarjetas(this.ajustex).compareToIgnoreCase("error") == 0)
//        {
//          this.message = "Error, Tarjeta Inexistente.";
//          return "success";
//        }
//
//        FileOutputStream prueba = new FileOutputStream(properties.getProperty("consulta"));
//        workbook1.write(prueba);
//        prueba.close();
//
//        ActionContext.getContext().getSession().put("tarjetasAct", this.tarjetasAct);
//      }
    } catch (Exception e) {
      log.error("error ", e);
      this.message = "error";
    }

    return "success";
  }

  public String getNroTarjeta() {
    return this.nroTarjeta;
  }

  public void setNroTarjeta(String nroTarjeta) {
    this.nroTarjeta = nroTarjeta;
  }

  public List<Tarjeta> getTarjetas() {
    return this.tarjetas;
  }

  public void setTarjetas(List<Tarjeta> tarjetas) {
    this.tarjetas = tarjetas;
  }

  public List<Tarjeta> getTarjetasAct() {
    return this.tarjetasAct;
  }

  public void setTarjetasAct(List<Tarjeta> tarjetasAct) {
    this.tarjetasAct = tarjetasAct;
  }

  public String getSelectedAjuste() {
    return this.selectedAjuste;
  }

  public void setSelectedAjuste(String selectedAjuste) {
    this.selectedAjuste = selectedAjuste;
  }

  public String getSelectedCampoValue() {
    return this.selectedCampoValue;
  }

  public void setSelectedCampoValue(String selectedCampoValue) {
    this.selectedCampoValue = selectedCampoValue;
  }

  public File getFile() {
    return this.file;
  }

  public String getReportFile() {
    return this.reportFile;
  }

  public void setReportFile(String reportFile) {
    this.reportFile = reportFile;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getContentType() {
    return this.contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public List<TAjuste> getTipoAjustes() {
    return this.tipoAjustes;
  }

  public void setTipoAjustes(List<TAjuste> tipoAjustes) {
    this.tipoAjustes = tipoAjustes;
  }

  public String getSelectedBloqueo() {
    return this.selectedBloqueo;
  }

  public void setSelectedBloqueo(String selectedBloqueo) {
    this.selectedBloqueo = selectedBloqueo;
  }

  public List<TBloqueo> getTipoBloqueo() {
    return this.tipoBloqueo;
  }

  public void setTipoBloqueo(List<TBloqueo> tipoBloqueo) {
    this.tipoBloqueo = tipoBloqueo;
  }

  public List<Tarjeta> getTarjetasRes() {
    return this.tarjetasRes;
  }

  public InputStream getInputStream() {
    return this.inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setTarjetasRes(List<Tarjeta> tarjetasRes) {
    this.tarjetasRes = tarjetasRes;
  }
}