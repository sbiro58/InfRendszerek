package org.biro.szilard.orvos.api;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "patient", eager = true)
@RequestScoped
public class Patient {

    private long id;
    private String name;
    private Date birthDate;
    private String socialInsuranceCode;
    private String gender;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSocialInsuranceCode() {
        return socialInsuranceCode;
    }

    public void setSocialInsuranceCode(String socialInsuranceCode) {
        this.socialInsuranceCode = socialInsuranceCode;
    }

    public Patient() {
        this.gender = "M";
    }

    public Patient(long id, String name, Date birthDate, String socialInsuranceCode, String gender) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.socialInsuranceCode = socialInsuranceCode;
        this.gender = gender;
    }

    public Patient(String name, Date birthDate, String socialInsuranceCode, String gender) {
        this.name = name;
        this.birthDate = birthDate;
        this.socialInsuranceCode = socialInsuranceCode;
        this.gender = gender;
    }

    public String getGender() {
        if (this.gender == null || this.gender == "")
        {
            this.gender = "M";
        }
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Patient [birthDate=" + birthDate + ", gender=" + gender + ", id=" + id + ", name=" + name
                + ", socialInsuranceCode=" + socialInsuranceCode + "]";
    }
}