package com.bsu;

import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
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
