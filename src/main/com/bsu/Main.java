package com.bsu;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String LOG_FILE_NAME = "logs/logfile.txt";
    public static String request_output_file = "request.txt";

    public static void main(String[] args) {
        if (args.length == 3) {
            try (Scanner scanner = new Scanner(System.in);
                 Scanner inputFile = new Scanner(new FileReader(args[0]));
                 Scanner inputRequestFile = new Scanner(new FileReader(args[2]));
                 FileWriter outputFile = new FileWriter(args[1]);
                 FileWriter outputRequestFile = new FileWriter(request_output_file)) {
                {
                    LOGGER.setLevel(Level.FINE);
                    FileHandler fh = new FileHandler(LOG_FILE_NAME, true);
                    fh.setLevel(Level.FINE);
                    fh.setFormatter(new SimpleFormatter());
                    LOGGER.addHandler(fh);
                    LOGGER.fine("Program started...\n");
                }

                String[] lineArgs;
                String request;
                List<Company> companyList = new ArrayList<>();

                while (inputFile.hasNextLine()) {
                    lineArgs = inputFile.nextLine().split(";");
                    companyList.add(new Company(lineArgs));

                }
                while (inputRequestFile.hasNextLine()){
                    request = inputRequestFile.nextLine();
                    processing(request, companyList);
                }

                //getInfoByRequest(scanner, companyList, outputFile);
                //String sql = "select * from company_table where short_name=\"short name\" employee_num between 90 and 181";


            } catch (IOException ex) {
                ex.printStackTrace();
                LOGGER.fine("Exception :: " + ex);
            } catch (Exception ex) {
                System.out.println("Exception :: " + ex);
                LOGGER.fine("Exception :: " + ex);
            }
        }
    }

    static void processing(String str, List<Company> companyList) throws CustomException, ParseException {
        String[] data = str.toLowerCase().split("(=\".*\" )|(=\".*\"$)|[ ,]+");
        Pattern r = Pattern.compile("\".*?\"");
        Matcher m = r.matcher(str);
        if (data[0].equals("select") && data[1].equals("*") && data[2].equals("from")) {
            List<Company> result = companyList;
            if (data[3].equals("company_table")) {
                int i = 4;
                if (data[i].equals("where")) {
                    for (i = 5; i < data.length && !data[i].equals("into"); ++i)
                        switch (data[i]) {
                            case "short_name":
                                if(!m.find())throw new CustomException("Invalid command format");
                                result = Query.findByShortName(result, str.substring(m.start() + 1, m.end() - 1));
                                break;
                            case "industry":
                                if(!m.find())throw new CustomException("Invalid command format");
                                result = Query.findByIndustry(result, str.substring(m.start() + 1, m.end() - 1));
                                break;
                            case "activity":
                                if(!m.find())throw new CustomException("Invalid command format");
                                result = Query.findByActivity(result, str.substring(m.start() + 1, m.end() - 1));
                                break;
                            case "foundation_date":
                                if (i + 4 < data.length && data[++i].equals("between") && data[i + 2].equals("and")) {
                                    Date date1 = Company.format.parse(data[++i]);
                                    Date date2 = Company.format.parse(data[i + 2]);
                                    i = i + 2;
                                    result = Query.findByFoundationDate(result, date1, date2);
                                } else throw new CustomException("Invalid command format");
                                break;
                            case "employee_num":
                                if (i + 4 < data.length && data[++i].equals("between") && data[i + 2].equals("and")) {
                                    int num1 = Integer.parseInt(data[++i]);
                                    int num2 = Integer.parseInt(data[i + 2]);
                                    i = i + 2;
                                    result = Query.findByEmployeeNumber(result, num1, num2);
                                } else throw new CustomException("Invalid command format");
                                break;
                            case "and":
                                break;
                            default:
                                throw new CustomException("Invalid command format");
                        }

                }
                if (i < data.length && data[i].equals("into") && data[++i].equals("outfile")) {
                    if (i + 1 >= data.length) throw new CustomException("Invalid command format");
                    Main.request_output_file = data[++i];
                }
                try (FileWriter of = new FileWriter(Main.request_output_file,true)) {
                    if (result.isEmpty()) {
                        of.write(("None\n"));
                    } else {
                        for (Company company : result)
                            of.write(company.toString() + System.lineSeparator());
                    }
                    of.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.fine("Exception :: " + e);
                }
                LOGGER.fine("SQL request: " + str +
                        "\n\t\tCompanies found: " + result.size());
            } else throw new CustomException("Invalid table name");
        } else {
            throw new CustomException("Invalid command format");
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
