package com.bsu;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Query {
    public enum RequestNumber {
        BY_SHORT_NAME(1),
        BY_INDUSTRY(2),
        BY_ACTIVITY(3),
        BY_FOUNDATION_DATE(4),
        BY_EMPLOYEE_NUMBER(5),
        DEFAULT(0);

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
        List<Company> result = new ArrayList<>();
        companyList.forEach(company -> {
            if (company.getShortName().equalsIgnoreCase(shortName)) result.add(company);
        });
        return result;
    }

    static List<Company> findByIndustry(List<Company> companyList, String industry) {
        List<Company> result = new ArrayList<>();
        companyList.forEach(company -> {
            if (company.getIndustry().equalsIgnoreCase(industry)) result.add(company);
        });
        return result;
    }

    static List<Company> findByActivity(List<Company> companyList, String activity) {
        List<Company> result = new ArrayList<>();
        companyList.forEach(company -> {
            if (company.getActivity().equalsIgnoreCase(activity)) result.add(company);
        });
        return result;
    }

    static List<Company> findByFoundationDate(List<Company> companyList, Date date1, Date date2) {
        List<Company> result = new ArrayList<>();
        companyList.forEach(company -> {
            if (company.getDateOfFoundation().compareTo(date1) >= 0 && company.getDateOfFoundation().compareTo(date2) <= 0)
                result.add(company);
        });
        return result;
    }

    static List<Company> findByEmployeeNumber(List<Company> companyList, int num1, int num2) {
        List<Company> result = new ArrayList<>();
        companyList.forEach(company -> {
            if (company.getEmployeeNumber() >= num1 && company.getEmployeeNumber() <= num2) result.add(company);
        });
        return result;
    }

    static void getInfoByRequest(Scanner scanner, List<Company> companyList) throws ParseException {
        int key;
        List<Company> result;
        while (true) {
            Main.printMenu();
            System.out.println("Enter key: ");
            key = scanner.nextInt();
            scanner.nextLine();

            switch (RequestNumber.fromInt(key)) {
                case BY_SHORT_NAME:
                    System.out.println("Enter shortName: ");
                    String shortName = scanner.nextLine();
                    result = findByShortName(companyList, shortName);
                    break;
                case BY_INDUSTRY:
                    System.out.println("Enter industry: ");
                    String industry = scanner.nextLine();
                    result = findByIndustry(companyList, industry);
                    break;
                case BY_ACTIVITY:
                    System.out.println("Enter activity: ");
                    String activity = scanner.nextLine();
                    result = findByActivity(companyList, activity);
                    break;
                case BY_FOUNDATION_DATE:
                    System.out.println("Enter 2 dates(from dd.mm.yyyy to dd.mm.yyyy): ");
                    String[] dates = scanner.nextLine().split(" ");
                    Date date1 = Company.format.parse(dates[0]);
                    Date date2 = Company.format.parse(dates[1]);
                    result = findByFoundationDate(companyList, date1, date2);
                    break;
                case BY_EMPLOYEE_NUMBER:
                    System.out.println("Enter 2 numbers(from .. to):");
                    String[] nums = scanner.nextLine().split(" ");
                    int num1 = Integer.parseInt(nums[0]);
                    int num2 = Integer.parseInt(nums[1]);
                    result = findByEmployeeNumber(companyList, num1, num2);
                    break;
                default:
                    return;
            }

            System.out.println("Companies found: ");
            if (result.isEmpty()) System.out.println("None");
            else result.forEach(System.out::println);
        }
    }
}
