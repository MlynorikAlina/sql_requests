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

    public static void main(String[] args) {
        if (args.length >= 3) {
            try (Scanner scanner = new Scanner(System.in);
                 Scanner inputFile = new Scanner(new FileReader(args[0]));
                 Scanner inputRequestFile = new Scanner(new FileReader(args[2]));
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
        String[] data = str.toLowerCase().split("(=\".*\" )|(=\".*\"$)|[ ]+");
        Pattern r = Pattern.compile("\".*?\"");
        Matcher m = r.matcher(str);
        if (data.length>=4&&data[0].equals(SQLRequests.COMMAND_SELECT) && data[1].equals("*") && data[2].equals(SQLRequests.COMMAND_FROM)) {
            List<Company> result = companyList;
            if (data[3].equals(SQLRequests.TABLE_NAME)) {
                int i = 4;
                if (data[i].equals(SQLRequests.COMMAND_WHERE)) {
                    for (i = 5; i < data.length && !data[i].equals(SQLRequests.COMMAND_INTO); ++i)
                        switch (data[i]) {
                            case SQLRequests.COLUMN_SHORT_NAME:
                                if(!m.find())throw new CustomException("Invalid command format");
                                result = Query.findByShortName(result, str.substring(m.start() + 1, m.end() - 1));
                                break;
                            case SQLRequests.COLUMN_INDUSTRY:
                                if(!m.find())throw new CustomException("Invalid command format");
                                result = Query.findByIndustry(result, str.substring(m.start() + 1, m.end() - 1));
                                break;
                            case SQLRequests.COLUMN_ACTIVITY:
                                if(!m.find())throw new CustomException("Invalid command format");
                                result = Query.findByActivity(result, str.substring(m.start() + 1, m.end() - 1));
                                break;
                            case SQLRequests.COLUMN_FOUNDATION_DATE:
                                if (i + 4 < data.length && data[++i].equals(SQLRequests.COMMAND_BETWEEN) && data[i + 2].equals(SQLRequests.COMMAND_AND)) {
                                    Date date1 = Company.format.parse(data[++i]);
                                    Date date2 = Company.format.parse(data[i + 2]);
                                    i = i + 2;
                                    result = Query.findByFoundationDate(result, date1, date2);
                                } else throw new CustomException("Invalid command format");
                                break;
                            case SQLRequests.COLUMN_EMPLOYEE_NUM:
                                if (i + 4 < data.length && data[++i].equals(SQLRequests.COMMAND_BETWEEN) && data[i + 2].equals(SQLRequests.COMMAND_AND)) {
                                    int num1 = Integer.parseInt(data[++i]);
                                    int num2 = Integer.parseInt(data[i + 2]);
                                    i = i + 2;
                                    result = Query.findByEmployeeNumber(result, num1, num2);
                                } else throw new CustomException("Invalid command format");
                                break;
                            case SQLRequests.COMMAND_AND:
                                break;
                            default:
                                throw new CustomException("Invalid command format");
                        }

                }
                String outputFileName = "request.txt";
                if (i < data.length && data[i].equals(SQLRequests.COMMAND_INTO) && data[++i].equals(SQLRequests.COMMAND_OUTFILE)) {
                    if (i + 1 >= data.length) throw new CustomException("Invalid command format");
                    outputFileName = data[++i];
                }
                try (FileWriter of = new FileWriter(outputFileName,true)) {
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

}
