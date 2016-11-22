package submit.service;

// $Id: RsasFormServiceImpl.java,v 1.2 2016-10-31 15:58:51-04 ericholp Exp $

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import submit.entity.RsasForm;
import submit.entity.UsersForm;
import submit.entity.RsaFileDataForm;
import submit.repository.RsasFormRepository;
import submit.repository.SsasFormRepository;
import submit.repository.RsaFileDataFormRepository;

import submit.entity.SsasForm;
import submit.entity.SubmitRequestErrors;
import submit.entity.SsaContactsForm;
import submit.entity.DepartmentsForm;
import submit.entity.SubmitData;
import submit.entity.SsaAccessRestrictionsForm;
import submit.entity.SsaContactsForm;
import submit.entity.SsaCopyrightsForm;
import submit.entity.SsaFormatTypesForm;
import submit.repository.SsaContactsFormRepository;
import submit.repository.SsaCopyrightsFormRepository;
import submit.repository.SsaFormatTypesFormRepository;
import submit.repository.SsaAccessRestrictionsFormRepository;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RsasFormServiceImpl implements RsasFormService {
    private final static Logger LOGGER = Logger.getLogger(RsasFormServiceImpl.class.getCanonicalName());

    @Resource
    private RsasFormRepository repo;
    
    @Resource
    private SsasFormRepository ssarepo;
    
    @Resource
    private SsasFormService ssaservice;

    @Resource
    private RsaFileDataFormRepository filedatarepo;
    
    @Transactional
    public void saveForm(RsasForm rsa){
	
	ssaservice.saveSsaFormForRsa(rsa.getSsasForm());
	
	List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
	filedatarepo.save(fds);

	repo.save(rsa);
    }
}
