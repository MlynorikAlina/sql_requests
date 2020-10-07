package com.bsu;

import java.io.*;
import java.text.ParseException;
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
        try (Scanner scanner = new Scanner(System.in)) {
            {
                LOGGER.setLevel(Level.FINE);
                File logFile = new File(LOG_FILE_NAME);
                FileHandler fh = new FileHandler(LOG_FILE_NAME, true);
                fh.setLevel(Level.FINE);
                fh.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fh);
                LOGGER.fine("Program started...\n");
            }

            System.out.println("Enter input file name: ");
            String inputFileName = scanner.nextLine();//csvFile1.csv
            System.out.println("Enter output file name: ");
            String outputFileName = scanner.nextLine();//csvFile2.csv

            try (Scanner inputFile = new Scanner(new FileReader(inputFileName));
                 FileWriter outputFile = new FileWriter(outputFileName)
            ) {
                List<Company> companyList = Company.readListOfCompanies(inputFile);
                Query.getInfoByRequest(scanner, companyList);

                    /*for (CompanyDatabase company : companyList) {
                        outputFile.write(company.toString() + "\n");
                    }*/
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
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
