package net.sourceforge.fenixedu.domain.personnelSection.contracts;

import net.sourceforge.fenixedu.domain.RootDomainObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class EmployeeProfessionalRegime extends EmployeeProfessionalRegime_Base {

    public EmployeeProfessionalRegime(final EmployeeProfessionalData employeeProfessionalData, final LocalDate beginDate,
	    final LocalDate endDate, final ProfessionalRegime professionalRegime, final String professionalRegimeGiafId,
	    final DateTime creationDate, final DateTime modifiedDate) {
	super();
	setRootDomainObject(RootDomainObject.getInstance());
	setEmployeeProfessionalData(employeeProfessionalData);
	setBeginDate(beginDate);
	setEndDate(endDate);
	setProfessionalRegime(professionalRegime);
	setProfessionalRegimeGiafId(professionalRegimeGiafId);
	setCreationDate(creationDate);
	setModifiedDate(modifiedDate);
	setImportationDate(new DateTime());
    }

    public boolean isValid() {
	return getProfessionalRegime() != null && getBeginDate() != null
		&& (getEndDate() == null || !getBeginDate().isAfter(getEndDate()));
    }
}
