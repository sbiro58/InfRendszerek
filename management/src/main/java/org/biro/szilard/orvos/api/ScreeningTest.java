package org.biro.szilard.orvos.api;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "newtest", eager = true)
@RequestScoped
public class ScreeningTest {

    private static final long serialVersionUID = -7302643218369732161L;
    private String name;
    private int period;
    private int min;
    private int max;
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public ScreeningTest() {
        this.gender = "M";
    }

    public ScreeningTest(String name, int period, int min, int max, String gender) {
        this.name = name;
        this.period = period;
        this.min = min;
        this.max = max;
        this.gender = gender;
    }

    public String getGender() {
        if (this.gender == null || this.gender == "")
        {
            this.gender = "M";
        }
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ScreeningTest [gender=" + gender + ", max=" + max + ", min=" + min + ", name=" + name + ", period="
                + period + "]";
    }

}