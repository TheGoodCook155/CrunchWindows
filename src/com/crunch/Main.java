package com.crunch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static File file = null;
    private static Scanner scanner;
    private static Path path;
    private static String outputName;
    private static boolean savePresent = false;
    private static String saveLocationGlobal;
    private static Runtime runtime;


    public static void main(String[] args) throws IOException {



        scanner = new Scanner(System.in);

        System.out.println("Usage: crunch [minLength] [maxLength] [chars to be included] o[path\\fileName.txt]: \n");

        String input = scanner.nextLine();

            if (validParams(input) == true){
                processParams(input);
            }else{
                System.out.println("Invalid input exception, Check syntax! \n\n");
                System.out.println("Usage: crunch [minLength] [maxLength] [chars to be included] o[path\\fileName.txt]: \n");
            }
    }

    public static void processParams(String params) throws IOException {

        String strValues;

        String minMaxPrep = params.replaceAll("^crunch ","");

        String temp = minMaxPrep;
        if (savePresent == false) {
            strValues = temp.replaceAll("^[1-9]{1}(\\d{0,1})? [0-9]{1,2} ", "").trim();
        }else{
            strValues = temp.replaceAll("^[1-9]{1}(\\d{0,1})? [0-9]{1,2}","").replaceAll("o [a-zA-Z\\\\0-9:\\s]{2,64}.txt$","").trim();
        }
        String[] arrValues = createValueArray(strValues);


        String minMax = minMaxPrep.replaceAll("o [a-zA-Z0-9!@#$%^&*()-_=+<>,.;:\";'|\\]\\[{}]{1,64}$","").trim();

        String[] values = minMax.split(" ");

        int minValue = Integer.parseInt(values[0]);
        int maxValue = Integer.parseInt(values[1]);
        String locationPrep = minMaxPrep;

        if (locationPrep.matches("[1-9]{1}(\\d{0,1})? [0-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64} o [A-Z]:[a-zA-Z0-9\\\\]{2,64}.txt")) {
            String saveLocationAndFile = locationPrep.replaceAll("[1-9]{1}(\\d{0,1})? [0-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64} o ", " ").trim();
            String saveLocation = saveLocationAndFile.replaceAll("[a-zA-Z0-9]{1,64}.txt$"," ").trim();
            path = Paths.get(saveLocation);
            saveLocationGlobal = saveLocation;

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            int fileNamePosition = saveLocationAndFile.lastIndexOf("\\");
            String fileName = saveLocationAndFile.substring(fileNamePosition + 1);
            outputName = fileName;
        }

        if (maxValue > 80){
            System.out.println("Hold on a sec there pal. You are trying to exceed the number of all atoms in the known Universe! Please slow down!");
        }
        if (minValue > maxValue){
            System.out.println("Minimum value is greater than Maximum value...Please revise");
        }

       processQuery(minValue,maxValue,arrValues);

    }

    public static void processQuery(int min, int max, String[] arr) throws IOException {
        Queue<String> finalRes = new LinkedList<>();
        runtime = Runtime.getRuntime();
        BufferedWriter writer = null;
        String outputLocation = "";
        if (savePresent == true){
            outputLocation = saveLocationGlobal + outputName;
            writer = new BufferedWriter(new FileWriter(outputLocation, StandardCharsets.UTF_8));
        }

        if (savePresent == true){
            System.out.println("Saving...");
        }
        if (savePresent == false){
            System.out.println("Calculating...");
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                String toWrite = arr[i] + arr[j];

                if (savePresent == false && toWrite.length() >= min){
                    System.out.println(toWrite);
                }
                if (savePresent == true && toWrite.length() >= min) {
                    writer.write(toWrite);
                    writer.write("\n");
                    writer.flush();
                }
                finalRes.add(toWrite);
            }
        }
        BufferedWriter log = new BufferedWriter(new FileWriter("leak.txt"));
        int finalResSize = finalRes.size();
        boolean maxStackAssigned = false;
        int maxStack = 0;
        if (maxStackAssigned == false) {
            maxStack = finalResSize;
            maxStackAssigned = true;
        }
        int startIndexMain = 0;
        boolean play = true;
//        int maxLength = 0;
        String firstStrWithGivenLength = "";
        boolean assigned = false;
        String test = "";
//        int indexInArrWhereStrMinLengthAppearsFirst = 0;
        boolean minIndex = false;

        while (play == true){
            for (int i = startIndexMain ; i < finalRes.size(); i++){
                test = finalRes.poll();

                if (test.length() == max && assigned == false){
                    firstStrWithGivenLength = test;
                    assigned = true;
                }

                if (firstStrWithGivenLength.equals(test) && !finalRes.contains(test)){
                    play = false;
                    break;
                }
                for (int j = 0; j < arr.length; j++){

                    String toWrite = test+arr[j];
                    if (savePresent == false && toWrite.length() >= min){
                        System.out.println(toWrite);
                    }
                    if (savePresent == true && toWrite.length() >= min){
                        writer.write(toWrite);
                        writer.write("\n");
                        writer.flush();
                    }

                    if((test + arr[j]).length() == min && minIndex == false){
//                        indexInArrWhereStrMinLengthAppearsFirst = finalRes.size();
                        minIndex = true;
                    }
//                    String testLength = test+arr[j];
//                    maxLength = testLength.length();
                    finalRes.add(toWrite);

                        log.write(String.valueOf(runtime.freeMemory()));
                        log.write("\n");
                        log.flush();

//                    System.out.println("Free memory: " + runtime.freeMemory());
                    runtime.gc();

                }
                if (startIndexMain < maxStack){
                    startIndexMain++;
                }

            }
            startIndexMain = 0;

        }
        if (savePresent == true) {
            System.out.println("Saving done");
            System.out.println("================EOF=================");
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
        if (params.matches("crunch [1-9]{1}(\\d{0,1})? [0-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64} o [A-Z]:[a-zA-Z0-9\\\\]{2,64}.txt")){
            savePresent = true;
        }

        if (params.matches("crunch [1-9]{1}(\\d{0,1})? [0-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64}") || params.matches("crunch [1-9]{1}(\\d{0,1})? [0-9]{1,2} [a-zA-Z0-9!@#$%^&*()_+/={},.<>/?';\\]\\[-]{1,64} o [A-Z]:[a-zA-Z0-9\\\\]{2,64}.txt")){
            return true;
        }
        return false;
    }

}
