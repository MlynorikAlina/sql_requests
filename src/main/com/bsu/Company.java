package com.bsu;

import com.sun.media.sound.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Company {
    private final String name;
    private final String shortName;
    private final Date actualizationDate;
    private final String address;
    private final Date dateOfFoundation;
    private final int employeeNumber;
    private final String auditor;
    private final String telephoneNumber;
    private final String email;
    private final String industry;
    private final String activity;
    private final String site;

    public static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return employeeNumber == company.employeeNumber &&
                Objects.equals(name, company.name) &&
                Objects.equals(shortName, company.shortName) &&
                Objects.equals(actualizationDate, company.actualizationDate) &&
                Objects.equals(address, company.address) &&
                Objects.equals(dateOfFoundation, company.dateOfFoundation) &&
                Objects.equals(auditor, company.auditor) &&
                Objects.equals(telephoneNumber, company.telephoneNumber) &&
                Objects.equals(email, company.email) &&
                Objects.equals(industry, company.industry) &&
                Objects.equals(activity, company.activity) &&
                Objects.equals(site, company.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, actualizationDate, address, dateOfFoundation, employeeNumber, auditor, telephoneNumber, email, industry, activity, site);
    }

    public Company(String[] args) throws CustomException {
        try {
            if (args.length != 12) throw new InvalidFormatException("Invalid number of arguments");
            else{
                name = args[0];
                shortName = args[1];
                actualizationDate = format.parse(args[2]);
                address = args[3];
                dateOfFoundation = format.parse(args[4]);
                employeeNumber = Integer.parseInt(args[5]);
                auditor = args[6];
                telephoneNumber = args[7];
                email = args[8];
                industry = args[9];
                activity = args[10];
                site = args[11];
            }
        } catch (Exception ex) {
            throw new CustomException("Reading input file is failed :: " + ex);
        }
    }

    private String getStringLine(Character sep) {
        return (name + sep + shortName + sep + format.format(actualizationDate) + sep + address + sep + format.format(dateOfFoundation) + sep +
                employeeNumber + sep + auditor + sep + telephoneNumber + sep + email + sep + industry + sep + activity + sep + site);
    }

    @Override
    public String toString() {
        return getStringLine(';');
    }

    public String getShortName() {
        return shortName;
    }

    public String getIndustry() {
        return industry;
    }

    public String getActivity() {
        return activity;
    }

    public Date getDateOfFoundation() {
        return dateOfFoundation;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }
}
