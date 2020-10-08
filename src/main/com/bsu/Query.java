package com.bsu;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Query {
    public enum RequestNumber {
        BY_SHORT_NAME(1),
        BY_INDUSTRY(2),
        BY_ACTIVITY(3),
        BY_FOUNDATION_DATE(4),
        BY_EMPLOYEE_NUMBER(5),
        DEFAULT(6),
        EXIT(0);

        private final int value;

        RequestNumber(int val) {
            value = val;
        }

        public static RequestNumber fromInt(int value) {
            for (RequestNumber type : values()) {
                if (type.value == value) return type;
            }
            return RequestNumber.DEFAULT;
        }
    }

    static List<Company> findByShortName(List<Company> companyList, String shortName) {
        return companyList.stream().filter(company -> company.getShortName().equalsIgnoreCase(shortName)).collect(Collectors.toList());
    }

    static List<Company> findByIndustry(List<Company> companyList, String industry) {
        return companyList.stream().filter(company -> company.getIndustry().equalsIgnoreCase(industry)).collect(Collectors.toList());
    }

    static List<Company> findByActivity(List<Company> companyList, String activity) {
        return companyList.stream().filter(company -> company.getActivity().equalsIgnoreCase(activity)).collect(Collectors.toList());
    }

    static List<Company> findByFoundationDate(List<Company> companyList, Date startDate, Date endDate) {
        return companyList.stream().filter(company -> company.getDateOfFoundation().compareTo(startDate) >= 0 && company.getDateOfFoundation().compareTo(endDate) <= 0).collect(Collectors.toList());
    }

    static List<Company> findByEmployeeNumber(List<Company> companyList, int startNum, int endNum) {
        return companyList.stream().filter(company -> company.getEmployeeNumber() >= startNum && company.getEmployeeNumber() <= endNum).collect(Collectors.toList());
    }

}
