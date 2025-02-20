/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.dto.person;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.candidacyProcess.IndividualCandidacyPersonalDetails;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.contacts.PhysicalAddressData;
import org.fenixedu.academic.domain.contacts.WebAddress;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.joda.time.YearMonthDay;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * @author - Angela Almeida (argelina@ist.utl.pt)
 * 
 */
public class PersonBean implements Serializable {

    private Person person;

    // personal information
    private String name; // read only

    private String nickname;

    private String givenNames;

    private String familyNames;

    private String username; // read only

    private Gender gender; // read only

    private String documentIdNumber; // read only

    private IDDocumentType idDocumentType;

    private String documentIdEmissionLocation;

    private YearMonthDay documentIdEmissionDate;

    private YearMonthDay documentIdExpirationDate;

    private String socialSecurityNumber;

    private ProfessionType professionType;

    private ProfessionalSituationConditionType professionalCondition;

    private String profession;

    private MaritalStatus maritalStatus = MaritalStatus.UNKNOWN;

    // contacts
    private String phone;

    private String workPhone;

    private String mobile;

    private String email;

    private String webAddress;

    private boolean isEmailAvailable;

    private boolean isHomepageAvailable;

    // birth information
    private YearMonthDay dateOfBirth;

    private Country nationality;

    private String parishOfBirth;

    private String districtSubdivisionOfBirth;

    private String districtOfBirth;

    private String fatherName;

    private String motherName;

    private Country countryOfBirth;

    // residence
    private String address;

    private String areaCode; // zip code

    private String areaOfAreaCode; // location of zip code

    private String area; // location

    private String parishOfResidence;

    private String districtSubdivisionOfResidence;

    private String districtOfResidence;

    private DistrictSubdivision districtSubdivisionOfResidenceObject;

    private Country countryOfResidence;

    private Integer studentNumber;

    private String emailConfirmation;

    private String eidentifier;
    
    private String identificationDocumentSeriesNumber;

    public PersonBean() {
        super();
    }

    public PersonBean(String name, String identificationNumber, IDDocumentType idDocumentType, YearMonthDay dateOfBirth) {
        setName(name);
        setDocumentIdNumber(identificationNumber);
        setIdDocumentType(idDocumentType);
        setDateOfBirth(dateOfBirth);
    }

    public PersonBean(String name, String identificationNumber, IDDocumentType idDocumentType, YearMonthDay dateOfBirth,
            Integer studentNumber) {
        setName(name);
        setDocumentIdNumber(identificationNumber);
        setIdDocumentType(idDocumentType);
        setDateOfBirth(dateOfBirth);
        setStudentNumber(studentNumber);
    }

    public PersonBean(IndividualCandidacyPersonalDetails details) {
        if (details.isInternal()) {
            initPerson(details.getPerson());
        } else {
            initPersonBeanFromPersonalDetails(details);
        }
        initPersonBeanFromPersonalDetails(details);
    }

    public PersonBean(Person person) {
        initPerson(person);
    }

    private void initPersonBeanFromPersonalDetails(IndividualCandidacyPersonalDetails personalDetails) {
        setGivenNames(personalDetails.getGivenNames());
        setFamilyNames(personalDetails.getFamilyNames());
        setGender(personalDetails.getGender());
        setMaritalStatus(personalDetails.getMaritalStatus());
        setNationality(personalDetails.getCountry());
        setDateOfBirth(personalDetails.getDateOfBirthYearMonthDay());
        setDocumentIdEmissionDate(personalDetails.getEmissionDateOfDocumentIdYearMonthDay());
        setDocumentIdEmissionLocation(personalDetails.getEmissionLocationOfDocumentId());
        setDocumentIdExpirationDate(personalDetails.getExpirationDateOfDocumentIdYearMonthDay());
        setDocumentIdNumber(personalDetails.getDocumentIdNumber());
        setIdDocumentType(personalDetails.getIdDocumentType());
        setSocialSecurityNumber(personalDetails.getSocialSecurityNumber());

        setAddress(personalDetails.getAddress());
        setArea(personalDetails.getArea());
        setAreaCode(personalDetails.getAreaCode());
        setAreaOfAreaCode(personalDetails.getAreaOfAreaCode());
        setCountryOfResidence(personalDetails.getCountryOfResidence());

        setPhone(personalDetails.getTelephoneContact());

        setEmail(personalDetails.getEmail());

        /*
         * 07/04/2009 - The following assignments are made when a candidacy is
         * internal or have a Person associated
         */
        if (personalDetails.isInternal()) {
            Person person = personalDetails.getPerson();
            setUsername(person.getUsername());
            setPerson(person);

            /*
             * FIXME Anil (07/04/2009): Some fields are not in
             * IndividualCandidacyPersonalDetails. But they should.
             */
            setFatherName(person.getNameOfFather());
            setMotherName(person.getNameOfMother());
            setProfession(person.getProfession());
            setCountryOfBirth(person.getCountryOfBirth());
            setParishOfBirth(person.getParishOfBirth());
            setDistrictOfBirth(person.getDistrictOfBirth());
            setDistrictSubdivisionOfBirth(person.getDistrictSubdivisionOfBirth());

            if (person.hasDefaultPhysicalAddress()) {
                final PhysicalAddress physicalAddress = person.getDefaultPhysicalAddress();
                setParishOfResidence(physicalAddress.getParishOfResidence());
                setDistrictSubdivisionOfResidence(physicalAddress.getDistrictSubdivisionOfResidence());
                setDistrictOfResidence(physicalAddress.getDistrictOfResidence());
            }

            setMobile(person.hasDefaultMobilePhone() ? person.getDefaultMobilePhone().getNumber() : null);
            setWebAddress(person.hasDefaultWebAddress() ? person.getDefaultWebAddress().getUrl() : null);
            setEmailAvailable(person.getAvailableEmail());
            setHomepageAvailable(person.getAvailableWebSite());
            setEidentifier(person.getEidentifier());
        }
    }

    private void initPerson(Person person) {
        setNickname(person.getNickname());
        setGivenNames(person.getGivenNames());
        setFamilyNames(person.getFamilyNames());
        setUsername(person.getUsername());
        setGender(person.getGender());
        setMaritalStatus(person.getMaritalStatus());
        setFatherName(person.getNameOfFather());
        setMotherName(person.getNameOfMother());
        setProfession(person.getProfession());
        setNationality(person.getCountry());

        setCountryOfBirth(person.getCountryOfBirth());
        setDateOfBirth(person.getDateOfBirthYearMonthDay());
        setParishOfBirth(person.getParishOfBirth());
        setDistrictOfBirth(person.getDistrictOfBirth());
        setDistrictSubdivisionOfBirth(person.getDistrictSubdivisionOfBirth());

        setDocumentIdEmissionDate(person.getEmissionDateOfDocumentIdYearMonthDay());
        setDocumentIdEmissionLocation(person.getEmissionLocationOfDocumentId());
        setDocumentIdExpirationDate(person.getExpirationDateOfDocumentIdYearMonthDay());
        setDocumentIdNumber(person.getDocumentIdNumber());
        setIdDocumentType(person.getIdDocumentType());
        setSocialSecurityNumber(person.getSocialSecurityNumber());

        if (person.hasDefaultPhysicalAddress()) {
            final PhysicalAddress physicalAddress = person.getDefaultPhysicalAddress();
            setAddress(physicalAddress.getAddress());
            setArea(physicalAddress.getArea());
            setAreaCode(physicalAddress.getAreaCode());
            setAreaOfAreaCode(physicalAddress.getAreaOfAreaCode());
            setParishOfResidence(physicalAddress.getParishOfResidence());
            setDistrictSubdivisionOfResidence(physicalAddress.getDistrictSubdivisionOfResidence());
            setDistrictOfResidence(physicalAddress.getDistrictOfResidence());
            setCountryOfResidence(physicalAddress.getCountryOfResidence());
        }

        setPhone(person.hasDefaultPhone() ? person.getDefaultPhone().getNumber() : null);
        setMobile(person.hasDefaultMobilePhone() ? person.getDefaultMobilePhone().getNumber() : null);
        setWebAddress(person.hasDefaultWebAddress() ? person.getDefaultWebAddress().getUrl() : null);

        setEmail(person.getEmail());

        setEmailAvailable(person.getAvailableEmail());
        setHomepageAvailable(person.getAvailableWebSite());

        setPerson(person);
        
        setIdentificationDocumentSeriesNumber(person.getIdentificationDocumentSeriesNumberValue());
        
        if(Strings.isNullOrEmpty(getIdentificationDocumentSeriesNumber())) {
            setIdentificationDocumentSeriesNumber(person.getIdentificationDocumentExtraDigitValue());
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaOfAreaCode() {
        return areaOfAreaCode;
    }

    public void setAreaOfAreaCode(String areaOfAreaCode) {
        this.areaOfAreaCode = areaOfAreaCode;
    }

    public Country getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(Country countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public Country getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(Country countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public boolean hasCountryOfResidence() {
        return getCountryOfResidence() != null;
    }

    public YearMonthDay getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(YearMonthDay dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDistrictOfBirth() {
        return districtOfBirth;
    }

    public void setDistrictOfBirth(String districtOfBirth) {
        this.districtOfBirth = districtOfBirth;
    }

    public String getDistrictOfResidence() {
        return getDistrictSubdivisionOfResidenceObject() != null ? getDistrictSubdivisionOfResidenceObject().getDistrict()
                .getName() : districtOfResidence;
    }

    public void setDistrictOfResidence(String districtOfResidence) {
        this.districtOfResidence = districtOfResidence;
    }

    public DistrictSubdivision getDistrictSubdivisionOfResidenceObject() {
        return this.districtSubdivisionOfResidenceObject;
    }

    public void setDistrictSubdivisionOfResidenceObject(DistrictSubdivision districtSubdivision) {
        this.districtSubdivisionOfResidenceObject = districtSubdivision;
    }

    public String getDistrictSubdivisionOfBirth() {
        return districtSubdivisionOfBirth;
    }

    public void setDistrictSubdivisionOfBirth(String districtSubdivisionOfBirth) {
        this.districtSubdivisionOfBirth = districtSubdivisionOfBirth;
    }

    public String getDistrictSubdivisionOfResidence() {
        return getDistrictSubdivisionOfResidenceObject() != null ? getDistrictSubdivisionOfResidenceObject().getName() : districtSubdivisionOfResidence;
    }

    public void setDistrictSubdivisionOfResidence(String districtSubdivisionOfResidence) {
        this.districtSubdivisionOfResidence = districtSubdivisionOfResidence;
    }

    public YearMonthDay getDocumentIdEmissionDate() {
        return documentIdEmissionDate;
    }

    public void setDocumentIdEmissionDate(YearMonthDay documentIdEmissionDate) {
        this.documentIdEmissionDate = documentIdEmissionDate;
    }

    public String getDocumentIdEmissionLocation() {
        return documentIdEmissionLocation;
    }

    public void setDocumentIdEmissionLocation(String documentIdEmissionLocation) {
        this.documentIdEmissionLocation = documentIdEmissionLocation;
    }

    public YearMonthDay getDocumentIdExpirationDate() {
        return documentIdExpirationDate;
    }

    public void setDocumentIdExpirationDate(YearMonthDay documentIdExpirationDate) {
        this.documentIdExpirationDate = documentIdExpirationDate;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public IDDocumentType getIdDocumentType() {
        return idDocumentType;
    }

    public void setIdDocumentType(IDDocumentType idDocumentType) {
        this.idDocumentType = idDocumentType;
    }

    public boolean isEmailAvailable() {
        return isEmailAvailable;
    }

    public void setEmailAvailable(boolean isEmailAvailable) {
        this.isEmailAvailable = isEmailAvailable;
    }

    public boolean isHomepageAvailable() {
        return isHomepageAvailable;
    }

    public void setHomepageAvailable(boolean isHomepageAvailable) {
        this.isHomepageAvailable = isHomepageAvailable;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus == null ? MaritalStatus.UNKNOWN : maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobileNumber) {
        this.mobile = mobileNumber;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getFamilyNames() {
        return familyNames;
    }

    public void setFamilyNames(String familyNames) {
        this.familyNames = familyNames;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public String getParishOfBirth() {
        return parishOfBirth;
    }

    public void setParishOfBirth(String parishOfBirth) {
        this.parishOfBirth = parishOfBirth;
    }

    public String getParishOfResidence() {
        return parishOfResidence;
    }

    public void setParishOfResidence(String parishOfResidence) {
        this.parishOfResidence = parishOfResidence;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public ProfessionType getProfessionType() {
        return professionType;
    }

    public void setProfessionType(ProfessionType professionType) {
        this.professionType = professionType;
    }

    public ProfessionalSituationConditionType getProfessionalCondition() {
        return professionalCondition;
    }

    public void setProfessionalCondition(ProfessionalSituationConditionType professionalCondition) {
        this.professionalCondition = professionalCondition;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    /*
     * 08/05/2009 - VAT Number and Social Security Number is the same thing
     */
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getFiscalCode() {
        return socialSecurityNumber;
    }

    public void setFiscalCode(String value) {
        this.socialSecurityNumber = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<PhysicalAddress> getSortedPhysicalAdresses() {
        final List<PhysicalAddress> result = getPerson().getPendingOrValidPhysicalAddresses();
        Collections.sort(result, PhysicalAddress.COMPARATOR_BY_ADDRESS);
        return result;
    }

    public PhysicalAddressData getPhysicalAddressData() {
        return new PhysicalAddressData(getAddress(), getAreaCode(), getAreaOfAreaCode(), getArea(), getParishOfResidence(),
                getDistrictSubdivisionOfResidence(), getDistrictOfResidence(), getCountryOfResidence());
    }

    public List<Phone> getSortedPhones() {
        final List<Phone> result = getPerson().getPendingOrValidPhones();
        Collections.sort(result, Phone.COMPARATOR_BY_NUMBER);
        return result;
    }

    public List<MobilePhone> getSortedMobilePhones() {
        final List<MobilePhone> result = getPerson().getPendingOrValidMobilePhones();
        Collections.sort(result, MobilePhone.COMPARATOR_BY_NUMBER);
        return result;
    }

    public List<EmailAddress> getSortedEmailAddresses() {
        final List<EmailAddress> result = getPerson().getPendingOrValidEmailAddresses();
        Collections.sort(result, EmailAddress.COMPARATOR_BY_EMAIL);
        return result;
    }

    public List<WebAddress> getSortedWebAddresses() {
        final List<WebAddress> result = getPerson().getWebAddresses();
        Collections.sort(result, WebAddress.COMPARATOR_BY_URL);
        return result;
    }

    public boolean hasPerson() {
        return getPerson() != null;
    }

    public boolean hasStudent() {
        return hasPerson() && getPerson().getStudent() != null;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmailConfirmation() {
        return this.emailConfirmation;
    }

    public void setEmailConfirmation(final String value) {
        this.emailConfirmation = value;
    }

    public String getEidentifier() {
        return this.eidentifier;
    }

    public void setEidentifier(final String eidentifier) {
        this.eidentifier = eidentifier;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Person save() {
        return save(getPerson());
    }
    
    public String getIdentificationDocumentSeriesNumber() {
        return identificationDocumentSeriesNumber;
    }
    
    public void setIdentificationDocumentSeriesNumber(String identificationDocumentSeriesNumber) {
        this.identificationDocumentSeriesNumber = identificationDocumentSeriesNumber;
    }

    @Atomic(mode = TxMode.WRITE)
    public Person save(Person person) {
        person.getProfile().changeName(this.getGivenNames(), this.getFamilyNames(), null);

        person.setGender(this.getGender());
        person.setProfession(this.getProfession());
        person.setMaritalStatus(this.getMaritalStatus());

        // identification
        person.setIdentification(this.getDocumentIdNumber(), this.getIdDocumentType());
        person.setEmissionLocationOfDocumentId(this.getDocumentIdEmissionLocation());
        person.setEmissionDateOfDocumentIdYearMonthDay(this.getDocumentIdEmissionDate());
        person.setExpirationDateOfDocumentIdYearMonthDay(this.getDocumentIdExpirationDate());
        person.setSocialSecurityNumber(this.getSocialSecurityNumber());
        person.setEidentifier(this.getEidentifier());

        // filiation
        person.setDateOfBirthYearMonthDay(this.getDateOfBirth());
        person.setCountry(this.getNationality());
        person.setParishOfBirth(this.getParishOfBirth());
        person.setDistrictSubdivisionOfBirth(this.getDistrictSubdivisionOfBirth());
        person.setDistrictOfBirth(this.getDistrictOfBirth());
        person.setCountryOfBirth(this.getCountryOfBirth());
        person.setNameOfMother(this.getMotherName());
        person.setNameOfFather(this.getFatherName());

        person.setDefaultPhysicalAddressData(this.getPhysicalAddressData(), true);
        person.setDefaultPhoneNumber(this.getPhone());
        person.setDefaultMobilePhoneNumber(this.getMobile());
        person.setDefaultWebAddressUrl(this.getWebAddress());
        person.setDefaultEmailAddressValue(this.getEmail(), true);
        return person;
    }
}
