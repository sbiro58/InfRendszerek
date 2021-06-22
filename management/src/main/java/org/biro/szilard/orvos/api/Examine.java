package org.biro.szilard.orvos.api;

import java.util.Date;

public class Examine {
    
    private long id;
    private String socialinsurancecode;
    private String diagnose;
    private String medicines;
    private String treatments;
    private Date examineDate;

    public Examine() {
    }

    public Examine(long id, String socialinsurancecode, String diagnose, String medicines, String treatments,
            Date examineDate) {
        this.id = id;
        this.socialinsurancecode = socialinsurancecode;
        this.diagnose = diagnose;
        this.medicines = medicines;
        this.treatments = treatments;
        this.examineDate = examineDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSocialinsurancecode() {
        return socialinsurancecode;
    }

    public void setSocialinsurancecode(String socialinsurancecode) {
        this.socialinsurancecode = socialinsurancecode;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getTreatments() {
        return treatments;
    }

    public void setTreatments(String treatments) {
        this.treatments = treatments;
    }

    public Date getExamineDate() {
        return examineDate;
    }

    public void setExamineDate(Date examineDate) {
        this.examineDate = examineDate;
    }

    @Override
    public String toString() {
        return "Examine [diagnose=" + diagnose + ", examineDate=" + examineDate + ", id=" + id + ", medicines="
                + medicines + ", socialinsurancecode=" + socialinsurancecode + ", treatments=" + treatments + "]";
    }

    
}