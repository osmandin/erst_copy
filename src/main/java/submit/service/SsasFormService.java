package submit.service;

import submit.entity.DepartmentsForm;
import submit.entity.SubmitRequestErrors;
import submit.entity.SubmitData;
import submit.entity.SsasForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

public interface SsasFormService {
    public SubmitRequestErrors checkForDups(SubmitData submitData);

    public void saveForm(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm);

    public void saveSsaFormForRsa(SsasForm ssasForm);

    public void create(SsasForm ssasForm, DepartmentsForm selectedDepartmentsForm, HttpSession session, HttpServletRequest request);
}
