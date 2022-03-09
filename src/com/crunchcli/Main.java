package com.crunchcli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static boolean programStart = true;
    private static File file = null;
    private static Scanner scanner;
    private static Path path; //TODO
    private static String outputName;
    private static boolean savePresent = false;
    private static String saveLocationGlobal;

    public static void main(String[] args) throws InvalidSyntaxException, IOException {

        scanner = new Scanner(System.in);

        System.out.println("Usage: crunch [minLength] [maxLength] [chars to be included] o[path\\fileName.txt]: \n");

        String input = scanner.nextLine();

            if (validParams(input) == true){
                List<List<String>> res =  processParams(input);
                if (savePresent == true) {
                    saveFile(res, savePresent);
                }
            }else{
                System.out.println("Invalid input exception, Check syntax! \n\n");
                System.out.println("Usage: crunch [minLength] [maxLength] [chars to be included] o[path\\fileName.txt]: \n");
            }
    }

    public static List<List<String>> processParams(String params) throws InvalidSyntaxException, IOException {

        String strValues;

        String minMaxPrep = params.replaceAll("^crunch ","");

        String temp = minMaxPrep;

        if (savePresent == false) {
            strValues = temp.replaceAll("^[0-9]{1,2} [0-9]{1,2} ", "").trim();
        }else{
            strValues = temp.replaceAll("^[0-9]{1,2} [0-9]{1,2}","").replaceAll("o [a-zA-Z\\\\0-9:\\s]{2,64}.txt$","").trim();
        }
        String[] arrValues = createValueArray(strValues);


        String minMax = minMaxPrep.replaceAll("o [a-zA-Z0-9!@#$%^&*()-_=+<>,.;:\";'|\\]\\[{}]{1,64}$","").trim();

        String[] values = minMax.split(" ");

        int minValue = Integer.parseInt(values[0]);
        int maxValue = Integer.parseInt(values[1]);
        String locationPrep = minMaxPrep;

        if (locationPrep.matches("[1-9]{1,2} [1-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\\\]{2,64} o [A-Z]:[a-zA-Z0-9\\\\]{1,64}.txt")) {

            String saveLocationAndFile = locationPrep.replaceAll("^[0-9] [0-9] [a-zA-Z0-9!@#$%^&*()-_=+<>,.;:\";'|\\]\\[{}]{1,64} o ", " ").trim();
            String saveLocation = saveLocationAndFile.replaceAll("[a-zA-Z0-9]{1,64}.txt$"," ").trim();
            path = Paths.get(saveLocation);
            saveLocationGlobal = saveLocation;

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            int fileNamePosition = saveLocationAndFile.lastIndexOf("\\");
            System.out.println("FileNamePosition is: " + fileNamePosition);
            String fileName = saveLocationAndFile.substring(fileNamePosition + 1);
            System.out.println("FileName is: " + fileName);
            outputName = fileName;
        }

        if (maxValue > 80){
            System.out.println("Hold on a sec there pal. You are trying to exceed the number of all atoms in the known Universe! Please slow down!");
        }
        if (minValue > maxValue){
            System.out.println("Minimum value is greater than Maximum value...Please revise");
        }

       List<List<String>> res = processQuery(minValue,maxValue,arrValues);

        return res;

    }

    public static List<List<String>> processQuery(int min, int max, String[] arr) throws IOException {

        List<List<String>> finalRes = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                List<String> res = new ArrayList<>();
                res.add(arr[i] + arr[j]);
                System.out.println(arr[i] + arr[j]);// Prints in console
                finalRes.add(res);

            }
        }

        int startIndexMain = 0;
        boolean play = true;
        int maxLength = 0;
        String firstStrWithGivenLength = "";
        boolean assigned = false;
        String test = "";
        int indexInArrWhereStrMinLengthAppearsFirst = 0;
        boolean minIndex = false;
        while (play == true){

            for (int i = startIndexMain ; i < finalRes.size(); i++){

                test = finalRes.get(i).get(0);
                if (test.length() == max && assigned == false){
                    firstStrWithGivenLength = test;
                    assigned = true;
                }

                if (firstStrWithGivenLength.equals(test) && !finalRes.contains(test)){
                    play = false;
                    break;
                }

                for (int j = 0; j < arr.length; j++){
                    List<String> res = new ArrayList<>();
                    System.out.println(test+arr[j]); // PRINTS IN CONSOLE
                    res.add(test + arr[j]);


                    if((test + arr[j]).length() == min && minIndex == false){
                        indexInArrWhereStrMinLengthAppearsFirst = finalRes.size();
                        minIndex = true;
                    }
                    String testLength = test+arr[j];
                    maxLength = testLength.length();
                    finalRes.add(res);
                }
                startIndexMain++;
            }
        }
        return finalRes;
    }

    private static void saveFile(List<List<String>> res, boolean save){
        String outputLocation = saveLocationGlobal + outputName;

        System.out.println("Saving...");
        if (save == true && res.size() > 0){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLocation))){

                for (int i = 0; i < res.size(); i++){
                    List<String> testArr = res.get(i);
                    String toSave = "";
                    for (int j = 0; j < testArr.size(); j++){
                        toSave+= testArr.get(j);
                    }
                    System.out.println(toSave);
                    if (i > 0){
                        writer.write("\n");
                    }
                    writer.write(toSave);
                    writer.flush();
                }
            }catch (IOException e){
                e.printStackTrace();
            } finally {
                System.out.println("Saving done");
                System.out.println("================EOF=================");
            }
        }else{
            return;
        }
    }

    private static String[] createValueArray(String str){
        String[] res = new String[str.length()];

        for (int i = 0; i < str.length(); i++){
            if (str.substring(i,i+1).equals(" ")){
                break;
            }
            res[i] = str.substring(i,i+1);
        }
        return res;
    }

    private static boolean validParams(String params) {
        if (params.matches("crunch [1-9]{1,2} [1-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64} o [A-Z]:[a-zA-Z0-9\\\\]{2,64}.txt")){
            savePresent = true;
        }

        if (params.matches("crunch [1-9]{1,2} [1-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64}") || params.matches("crunch [1-9]{1,2} [1-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64} o [A-Z]:[a-zA-Z0-9\\\\]{2,64}.txt")){
            return true;
        }
        return false;
    }

}
