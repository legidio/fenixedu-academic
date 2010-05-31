package net.sourceforge.fenixedu.domain.personnelSection.contracts;

import net.sourceforge.fenixedu.domain.Employee;
import net.sourceforge.fenixedu.domain.RootDomainObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class EmployeeProfessionalData extends EmployeeProfessionalData_Base {

    public EmployeeProfessionalData(final Employee employee, final ContractSituation contractSituation,
	    final String contractSituationGiafId, final LocalDate contractSituationDate,
	    final ProfessionalRelation professionalRelation, final String professionalRelationGiafId,
	    final LocalDate professionalRelationDate, final ProfessionalContractType professionalContractType,
	    final String professionalContractTypeGiafId, final ProfessionalCategory professionalCategory,
	    final String professionalCategoryGiafId, final LocalDate professionalCategoryDate,
	    final ProfessionalRegime professionalRegime, final String professionalRegimeGiafId,
	    final LocalDate professionalRegimeDate, final DateTime creationDate, final DateTime modifiedDate) {
	super();
	setRootDomainObject(RootDomainObject.getInstance());
	setEmployee(employee);
	setContractSituation(contractSituation);
	setContractSituationGiafId(contractSituationGiafId);
	setContractSituationDate(contractSituationDate);
	setProfessionalRelation(professionalRelation);
	setProfessionalRelationGiafId(professionalRelationGiafId);
	setProfessionalRelationDate(professionalRelationDate);
	setProfessionalContractType(professionalContractType);
	setProfessionalContractTypeGiafId(professionalContractTypeGiafId);
	setProfessionalCategory(professionalCategory);
	setProfessionalCategoryGiafId(professionalCategoryGiafId);
	setProfessionalCategoryDate(professionalCategoryDate);
	setProfessionalRegime(professionalRegime);
	setProfessionalRegimeGiafId(professionalRegimeGiafId);
	setProfessionalRegimeDate(professionalRegimeDate);
	setCreationDate(creationDate);
	setModifiedDate(modifiedDate);
	setImportationDate(new DateTime());
    }

}
