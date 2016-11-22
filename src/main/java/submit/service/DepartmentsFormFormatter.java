package submit.service;

// $Id: DepartmentsFormFormatter.java,v 1.6 2016-10-20 14:20:09-04 ericholp Exp $

// http://stackoverflow.com/questions/25234357/select-tag-with-object-thymeleaf-and-spring-mvc

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import org.springframework.format.Formatter;

import java.util.Locale;
import java.text.ParseException;

import javax.annotation.Resource;

import submit.entity.DepartmentsForm;
import submit.repository.DepartmentsFormRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DepartmentsFormFormatter implements Formatter<DepartmentsForm> {
    private final static Logger LOGGER = Logger.getLogger(DepartmentsFormFormatter.class.getCanonicalName());

    @Autowired
    DepartmentsFormRepository departmentsFormRepository;
    
    public String print(DepartmentsForm object, Locale locale) {
        return (object != null ? Integer.toString(object.getId()) : "");
    }

    public DepartmentsForm parse(String text, Locale locale) throws ParseException {
	//LOGGER.log(Level.INFO, "parse: text={0}", new Object[]{text});
	if(text.equals("")){
	    return null;
	}
	int id = -1;
	try {
	    id = Integer.parseInt(text);
	}catch(NumberFormatException ex){}
	if(id == -1){
	    return null;
	}
	return departmentsFormRepository.findById(id);
    }
}
