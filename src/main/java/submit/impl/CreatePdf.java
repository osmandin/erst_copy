package submit.impl;

// $Id: CreatePdf.java,v 1.11 2016-10-04 23:33:03-04 ericholp Exp $

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

public class CreatePdf {

    private final static Logger LOGGER = Logger.getLogger(CreatePdf.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: CreatePdf.java,v 1.11 2016-10-04 23:33:03-04 ericholp Exp $";

    // ------------------------------------------------------------------------
    public String convertfiletostring(String filenamepath) {
        File file = null;
        FileInputStream fis = null;
        try {
            file = new File(filenamepath);
            fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return new String(data, "UTF-8");
        } catch (FileNotFoundException ex) {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex2) {
                LOGGER.log(Level.SEVERE, null, ex2);
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return "";
        } catch (IOException ex) {
            try {
                fis.close();
            } catch (IOException ex2) {
                LOGGER.log(Level.SEVERE, null, ex2);
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return "";
        }
    }

    // ------------------------------------------------------------------------
    public void downloadpdf(HttpServletResponse response, String htmldata) {

        // application/pdf, text/plain, text/html, image/jpg
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=submit.pdf");
        try {
            OutputStream out = response.getOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            InputStream is = new ByteArrayInputStream(htmldata.getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
            document.close();
            out.flush();
        } catch (DocumentException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    // ------------------------------------------------------------------------
    public byte[] createpdffromstringandreturnbytearray(String data) {
        try {

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();
            InputStream is = new ByteArrayInputStream(data.getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
            document.close();

            return os.toByteArray();
        } catch (DocumentException ex) {
            LOGGER.log(Level.SEVERE, "Error occurred while sending email: ", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occurred while sending email: ", ex);
        }
        return null;
    }
}
