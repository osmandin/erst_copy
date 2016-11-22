package submit.impl;

// $Id: MyFileUtils.java,v 1.32 2016-11-11 16:15:01-04 ericholp Exp $

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.lang.OutOfMemoryError;

import org.springframework.ui.ModelMap;

import org.apache.commons.io.IOUtils;
import java.io.BufferedOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import submit.entity.FileData;
import javax.servlet.ServletContext;
import java.io.OutputStream;

public class MyFileUtils {
    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: MyFileUtils.java,v 1.32 2016-11-11 16:15:01-04 ericholp Exp $";
    
    private final static Logger LOGGER = Logger.getLogger(MyFileUtils.class.getCanonicalName());

    // ------------------------------------------------------------------------
    public List<FileData> uploadFiles(
				      MultipartFile[] files,
				      String dropoffdirfull,
				      List<FileData> fileinfodata
				      ){
	
	List<FileData> fileinfo  = new ArrayList<FileData>();
	
	for(MultipartFile file : files){
	    FileData filedata = new FileData();
	    
	    String filename = file.getOriginalFilename();
	    String filesize = String.valueOf(file.getSize());
	    filedata.setName(filename);
	    filedata.setSize(Long.parseLong(filesize));
	    filedata.setStatus("");
	    filedata.setLastmoddatetime("");
	    filedata.setSetmoddatetimestatus("");
	    
	    try {
		file.transferTo(new File(dropoffdirfull + "/" + filename));
	    }catch(IOException ex){
		filedata.setStatus("Error: IOException: " + ex.toString());
		fileinfo.add(filedata);
		continue;
	    }
	    
	    if(file.getSize() == new File(dropoffdirfull + "/" + filename).length()){
		filedata.setStatus("success");
		
		String lastmoddatetime = "";
		for(FileData filedetails : fileinfodata){
		    if(filedetails.getName().equals(filename)){
			lastmoddatetime=filedetails.getLastmoddatetime();
			break;
		    }
		}
		
		if(lastmoddatetime.equals("")){
		    filedata.setStatus("Error: no last mod date/time found");
		    fileinfo.add(filedata);
		    continue;
		}

		Format format = new Format();
		
		boolean success = format.changeFileLastModDateTime(dropoffdirfull + "/" + filename, lastmoddatetime);
		if(success){
		    String setmoddatetime = format.returnLastModifiedDateTime(dropoffdirfull + "/" + filename);
		    filedata.setLastmoddatetime(setmoddatetime);
		    if(!lastmoddatetime.equals(setmoddatetime)){
			filedata.setSetmoddatetimestatus("Error: set of modtime=" + lastmoddatetime +" is now " + setmoddatetime + " -> failed");
		    }else{
			filedata.setSetmoddatetimestatus("success");
		    }
		    fileinfo.add(filedata);
		    continue;
		}
		
		filedata.setSetmoddatetimestatus("Error: set of modtime=" + lastmoddatetime +" failed");
		fileinfo.add(filedata);
		continue;
	    }
	    
	    filedata.setStatus("Error: updating fileinfo status: incorrect number of matches");
	    fileinfo.add(filedata);
	    LOGGER.log(Level.SEVERE, "uploadFiles: Error: updating fileinfo status: incorrect number of matches filename={0}", new Object[]{filedata.getName()});
	}
	return fileinfo;
    }

    // ------------------------------------------------------------------------
    public void downloadzipfile(
				ModelMap model,
				String dropoffdirfull,
				HttpServletResponse response,
				ServletContext context,
				String redirect,
				String rsaid
				){


	Date start = new Date();
	    
	try {
	    File[] files = {};
	    
	    if((new File(String.format(dropoffdirfull))).exists()){
		File folder = new File(String.format(dropoffdirfull));
		files = folder.listFiles();
	    }
	    
	    boolean arefiles = false;
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    
	    ZipOutputStream zip = new ZipOutputStream(baos);
	    
	    byte[] buffer = new byte[1024];
	    
	    if(files != null){
		for(File file : files){
		    if(file.isFile()){
			FileInputStream in = null;
			try {
			    arefiles = true;
			    ZipEntry ze= new ZipEntry(file.getName());
			    ze.setTime(file.lastModified());
			    zip.putNextEntry(ze);
			
			    LOGGER.log(Level.INFO, "file: {0}", new Object[]{file.getName()});
			    
			    in = new FileInputStream(String.format(dropoffdirfull + "/" + file.getName()));
			    
			    int len;
			    while ((len = in.read(buffer)) > 0) {
				zip.write(buffer, 0, len);
			    }
			    in.close();
			}catch(IOException ex){
			    if(in != null){
				try {
				    in.close();
				}catch(IOException ex2){
				    LOGGER.log(Level.SEVERE, null, ex2);
				}
			    }
			    LOGGER.log(Level.SEVERE, null, ex);
			}
		    }
		}
	    }
	    try{
		zip.closeEntry();
		zip.close();
	    }catch(IOException ex){
		LOGGER.log(Level.SEVERE, null, ex);
	    }
	    
	    response.setHeader("Content-Disposition","attachment; filename=\"inventory.zip\"");
	    response.setContentType("application/zip");
	    
	    try{
		if(arefiles){
		    LOGGER.log(Level.INFO, "start download");
		    response.getOutputStream().write(baos.toByteArray());
		    response.getOutputStream().flush();
		    response.getOutputStream().close();
		    LOGGER.log(Level.INFO, "download finished");
		}else{
		    response.getOutputStream().write("empty".getBytes());
		    response.getOutputStream().flush();
		    response.getOutputStream().close();
		}
	    }catch (IOException ex){
		LOGGER.log(Level.SEVERE, null, ex);
	    }finally{
		try {
		    baos.close();
		}catch(IOException ex2){
		    LOGGER.log(Level.SEVERE, null, ex2);
		}
	    }
	}catch(OutOfMemoryError ex){
	    Date now = new Date();
	    double secs = (double)(now.getTime()-start.getTime())/1000;
	    LOGGER.log(Level.SEVERE, "secs={0} exception={1}", new Object[]{secs, ex});

	    model.addAttribute("downloadfailed", "1");

	    Runtime rt = Runtime.getRuntime();
	    long totalMem = rt.totalMemory();
	    long maxMem = rt.maxMemory();
	    long freeMem = rt.freeMemory();
	    double megs = 1048576.0;
	    
	    LOGGER.log(Level.SEVERE, "Total Memory: {0} ({1} MiB)", new Object[]{totalMem, totalMem/megs});
	    LOGGER.log(Level.SEVERE, "Max Memory: {0} ({1} MiB)", new Object[]{maxMem, maxMem/megs});
	    LOGGER.log(Level.SEVERE, "Free Memory: {0} ({1} MiB)", new Object[]{freeMem, freeMem/megs});
	    
	    String root = context.getContextPath().toString();
	    try {
		response.sendRedirect(root + "/" + redirect + "?rsaid=" + rsaid + "&downloadfailed=1");
	    }catch(IOException ex2){
		LOGGER.log(Level.SEVERE, null, ex2);
	    }
	}
    }

    // ------------------------------------------------------------------------
    public void downloadfile(
			     String dropoffdirfull,
			     String filename,
			     HttpServletResponse response,
			     ServletContext context
			     ){
	int BUFFER_SIZE = 4096;
	
	try {
	    
	    File[] files = {};
	    
	    if((new File(String.format(dropoffdirfull))).exists()){
		File folder = new File(String.format(dropoffdirfull));
		files = folder.listFiles();
	    }
	    
	    boolean arefiles = false;
	    
	    if(files != null){
		for(File file : files){
		    if(file.isFile()){
			LOGGER.log(Level.INFO, "input file {0}", new Object[]{file.getName()});
		    }
		}
		for(File file : files){
		    if(file.isFile()){

			if(file.getName().equals(filename)){
			
			    LOGGER.log(Level.INFO, "handling file {0}", new Object[]{file.getName()});
			    
			    
			    FileInputStream inputStream = new FileInputStream(file);
			    
			    // get MIME type of the file
			    String mimeType = context.getMimeType(dropoffdirfull + "/" + file.getName());
			    if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			    }
			    LOGGER.log(Level.INFO, "MIME type: {0}", new Object[]{mimeType});
			    
			    response.setContentType(mimeType);
			    response.setContentLength((int) file.length());
			    
			    String headerKey = "Content-Disposition";
			    String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
			    response.setHeader(headerKey, headerValue);
			    
			    OutputStream outStream = response.getOutputStream();
			    
			    byte[] buffer = new byte[BUFFER_SIZE];
			    int bytesRead = -1;
			    
			    while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			    }
			
			    inputStream.close();
			    outStream.close();
			}
		    }
		}
	    }
	    
	}catch(IOException ex){
	    LOGGER.log(Level.SEVERE, null, ex);
	}
    }
}
