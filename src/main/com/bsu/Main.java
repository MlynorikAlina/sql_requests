package com.bsu;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Main {
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String LOG_FILE_NAME = "logs/logfile.txt";

    public static void main(String[] args) {
        if (args.length == 2) {
            try (Scanner scanner = new Scanner(System.in);
                 Scanner inputFile = new Scanner(new FileReader(args[0]));
                 FileWriter outputFile = new FileWriter(args[1])) {
                {
                    LOGGER.setLevel(Level.FINE);
                    FileHandler fh = new FileHandler(LOG_FILE_NAME, true);
                    fh.setLevel(Level.FINE);
                    fh.setFormatter(new SimpleFormatter());
                    LOGGER.addHandler(fh);
                    LOGGER.fine("Program started...\n");
                }

                String[] lineArgs;
                List<Company> companyList = new ArrayList<>();

                while (inputFile.hasNextLine()) {
                    lineArgs = inputFile.nextLine().split(";");
                    companyList.add(new Company(lineArgs));
                }

                getInfoByRequest(scanner, companyList, outputFile);


            } catch (IOException ex) {
                ex.printStackTrace();
                LOGGER.fine("Exception :: " + ex);
            } catch (Exception ex) {
                System.out.println("Exception :: " + ex);
                LOGGER.fine("Exception :: " + ex);
            }
        }
    }

    static void getInfoByRequest(Scanner scanner, List<Company> companyList, FileWriter of) throws CustomException {
        int key;
        List<Company> result;
        while (true) {
            printMenu();
            System.out.println("Enter key: ");
            key = scanner.nextInt();
            scanner.nextLine();
            String requestData;

            switch (Query.RequestNumber.fromInt(key)) {
                case BY_SHORT_NAME:
                    System.out.println("Enter shortName: ");
                    String shortName = scanner.nextLine();
                    requestData = shortName;
                    result = Query.findByShortName(companyList, shortName);
                    break;
                case BY_INDUSTRY:
                    System.out.println("Enter industry: ");
                    String industry = scanner.nextLine();
                    requestData = industry;
                    result = Query.findByIndustry(companyList, industry);
                    break;
                case BY_ACTIVITY:
                    System.out.println("Enter activity: ");
                    String activity = scanner.nextLine();
                    requestData = activity;
                    result = Query.findByActivity(companyList, activity);
                    break;
                case BY_FOUNDATION_DATE:
                    try {
                        System.out.println("Enter 2 dates(from dd.mm.yyyy to dd.mm.yyyy): ");
                        requestData = scanner.nextLine();
                        String[] dates = requestData.split(" ");
                        Date date1 = Company.format.parse(dates[0]);
                        Date date2 = Company.format.parse(dates[1]);
                        result = Query.findByFoundationDate(companyList, date1, date2);
                    } catch (Exception ex) {
                        throw new CustomException("Reading data failed :: " + ex);
                    }
                    break;
                case BY_EMPLOYEE_NUMBER:
                    try {
                        System.out.println("Enter 2 numbers(from .. to):");
                        requestData = scanner.nextLine();
                        String[] nums = requestData.split(" ");
                        int num1 = Integer.parseInt(nums[0]);
                        int num2 = Integer.parseInt(nums[1]);
                        result = Query.findByEmployeeNumber(companyList, num1, num2);
                    } catch (Exception ex) {
                        throw new CustomException("Reading data failed :: " + ex);
                    }
                    break;
                default:
                    LOGGER.fine("EXIT\n");
                    return;
            }

            System.out.println("Companies found: ");
            try {
                if (result.isEmpty()) {
                    of.write(("None\n"));
                } else {
                    for (Company company : result)
                        of.write(company.toString() + System.lineSeparator());
                }
                of.write(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }

            LOGGER.fine("Find company " + Query.RequestNumber.fromInt(key) + "::" + requestData +
                    "\n\t\tCompanies found: " + result.size());
        }
    }

    static void printMenu() {
        System.out.println(
                "1. Найти компанию по краткому наименованию.\n" +
                        "2. Выбрать компании по отрасли.\n" +
                        "3. Выбрать компании по виду деятельности.\n" +
                        "4. Выбрать компании по дате основания в определенном промежутке (с и по).\n" +
                        "5. Выбрать компании по численности сотрудников в определенном промежутке (с и по).\n" +
                        "0. End.\n" +
                        "-------------------"
        );
    }
}
