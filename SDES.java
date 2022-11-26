package csula.cs4780.project1;

import java.util.Arrays;
import java.util.Scanner;

public class SDES {

    static byte[] key1 = new byte[8];
    static byte[] key2 = new byte[8];
    byte[] P10 = {3, 5, 2, 7, 4, 10, 1, 9 ,8, 6};
    byte[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    static byte[][] S0 = {
            {1,0,3,2},
            {3,2,1,0},
            {0,2,1,3},
            {3,1,3,2},
    };

    static byte [][] S1=  {
            {0,1,2,3},
            {2,0,1,3},
            {3,0,1,0},
            {2,1,0,3}
    };

    public static void main(String[] args) {
        SDES sdes = new SDES();
        Scanner scanner = new Scanner(System.in);
        String option;
        do{
            System.out.println("""
                    \r
                    Select the operations needed to be performed on SDES: \r
                     1. Encryption \r
                     2. Decryption \r
                     0.Exit""");
            option = scanner.nextLine();

            switch (option) {
                case "1" -> {
                    System.out.println("Enter 10-bit Raw Key in binary");
                    String rawKeyStr = scanner.nextLine();
                    if (rawKeyStr.length() == 10) {
                        System.out.println("Enter 8-bit Plaintext in binary");
                        String plainTextStr = scanner.nextLine();
                        if (plainTextStr.length() == 8) {
                            byte[] rawKey = convertStringToByte(rawKeyStr);
                            byte[] plainText = convertStringToByte(plainTextStr);

                            byte[] output = sdes.encrypt(rawKey, plainText);
                            System.out.print("The Cipher Text is  :  ");
                            for (byte b : output)
                                System.out.print(b);

                        } else {
                            System.out.println("Enter Valid input for plain text");
                        }
                    } else {
                        System.out.println("Enter Valid input for raw key");
                    }
                }
                case "2" -> {
                    System.out.println("Enter 10-bit Raw Key in binary");
                    String rawKeyStrDec = scanner.nextLine();
                    if (rawKeyStrDec.length() == 10) {
                        System.out.println("Enter 8-bit Cipher Text in binary");
                        String cipherTextStr = scanner.nextLine();
                        if (cipherTextStr.length() == 8) {
                            byte[] rawKeyDec = convertStringToByte(rawKeyStrDec);
                            byte[] cipherTextDec = convertStringToByte(cipherTextStr);

                            byte[] output = sdes.decrypt(rawKeyDec, cipherTextDec);
                            System.out.print("The Plain Text is  :  ");
                            for (byte b : output)
                                System.out.print(b);

                        } else {
                            System.out.println("Enter Valid input for plain text");
                        }
                    } else {
                        System.out.println("Enter Valid input for raw key");
                    }
                }
                default -> System.out.println("Enter valid input");
            }
        }while(!option.equals("0"));
    }

    static byte[] initialPermutation(byte[] text){
        byte[] outputPosition = {2,  6,  3,  1,  4,  8,  5,  7};
        byte[] output = new byte[8];

        for (int i = 0; i < text.length; i++) {
            output[i] = text[outputPosition[i] -1];
        }
        return output;
    }

    static byte[] initialPermutationInverse(byte[] text){

        byte[] inverseOutputPosition = {4, 1, 3, 5, 7, 2, 8, 6};
        byte[] output = new byte[8];

        for (int i = 0; i < text.length; i++) {
            output[i] = text[inverseOutputPosition[i] -1];
        }

        return output;
    }

    void generateKey(byte[] key){
        byte [] keyGenerate = new byte[10];

        for (int i = 0; i < 10; i++){
            keyGenerate[i] = key[P10[i] - 1];
        }

        byte[] leftHalf = Arrays.copyOfRange(keyGenerate, 0, 5) ;
        byte[] rightHalf = Arrays.copyOfRange(keyGenerate, 5, 10);

        byte[] leftShift = shift(leftHalf, 1);
        byte[] rightShift = shift(rightHalf, 1);

        for (int i = 0; i < 5; i++){
            keyGenerate[i] = leftShift[i];
            keyGenerate[i + 5] = rightShift[i];
        }

        for (int i = 0; i < 8; i++){
            key1[i] = keyGenerate[P8[i] - 1];
        }


        byte[] leftSecondShift = shift(leftHalf, 2);
        byte[] rightSecondShift = shift(rightHalf, 2);

        for (int i = 0; i < 5; i++){
            keyGenerate[i] = leftSecondShift[i];
            keyGenerate[i + 5] = rightSecondShift[i];
        }

        for (int i = 0; i < 8; i++){
            key2[i] = keyGenerate[P8[i] - 1];
        }
    }

    static byte[] shift(byte[] input, int numberOfShift){
        while (numberOfShift > 0) {
            int temp = input[0];
            for (int i = 0; i < input.length - 1; i++) {
                input[i] = input[i + 1];
            }
            input[input.length - 1] = (byte) temp;
            numberOfShift--;
        }
        return input;

    }

    static byte[] ExpandArray(byte[] input){
        byte [] expandedOutput = {4, 1, 2, 3, 2, 3, 4, 1};
        byte [] expArr = new byte[8];

        for (int i = 0; i < expArr.length; i++) {
            expArr[i] = input[expandedOutput[i] - 1];
        }
        return expArr;
    }

    private static byte[] reverseCombinedP4(byte[] input) {
        byte [] reverseCombinedPositions = {5, 6, 7, 8, 1, 2, 3, 4};
        byte[] reverseCombinedP4 = new byte[8];

        for (int i = 0; i < reverseCombinedP4.length; i++) {
            reverseCombinedP4[i] = input[reverseCombinedPositions[i] - 1];
        }
        return reverseCombinedP4;
    }

    private static byte[] sBoxOutputs(byte[] inputArray, byte[] key) {

        byte[] leftBlockIP = Arrays.copyOfRange(inputArray, 0, 4);
        byte[] rightBlockIP = Arrays.copyOfRange(inputArray, 4, 8);

        byte[] expandedArray = ExpandArray(rightBlockIP);

        byte[] xorExpandedAndKey = xor(key, expandedArray );

        byte[] leftXOR = Arrays.copyOfRange(xorExpandedAndKey, 0, 4);
        byte[] rightXOR = Arrays.copyOfRange(xorExpandedAndKey, 4, 8);

        String SBoxOutput = sBoxExecution(leftXOR, rightXOR);

        byte[] SBoxOutputByte = convertStringToByte(SBoxOutput);

        byte[] outputP4 = calculateP4(SBoxOutputByte);

        byte[] p4XORLeftIP = xor(leftBlockIP,outputP4) ;

        byte[] combinedP4 = new byte[8];

        for (int i = 0; i < 4; i++){
            combinedP4[i] = p4XORLeftIP[i];
            combinedP4[i + 4] = rightBlockIP[i];
        }
        return combinedP4;
    }

    private static byte [] calculateP4(byte[] input) {
        byte [] outputP4Position = {2, 4, 3, 1};
        byte[] outputP4 = new byte[input.length];

        for (int i = 0; i < outputP4.length; i++) {
            outputP4[i] = input[outputP4Position[i] - 1];
        }

        return outputP4;
    }

    private static String sBoxExecution(byte[] leftXOR, byte[] rightXOR) {
        String leftXORRow = leftXOR[0] + "" + leftXOR[3];
        String leftXORCol = leftXOR[1] + "" + leftXOR[2];
        String rightXORRow = rightXOR[0] + "" + rightXOR[3];
        String rightXORCol = rightXOR[1] + "" + rightXOR[2];

        int leftRowInt = Integer.parseInt(leftXORRow,2);
        int leftColInt = Integer.parseInt(leftXORCol,2);
        int rightRowInt = Integer.parseInt(rightXORRow,2);
        int rightColInt = Integer.parseInt(rightXORCol,2);

        int leftSBox = S0[leftRowInt][leftColInt];
        int rightSBox = S1[rightRowInt][rightColInt];

        String leftBox = String.format("%02d", Integer.parseInt(Integer.toBinaryString(leftSBox)));
        String rightBox = String.format("%02d", Integer.parseInt(Integer.toBinaryString(rightSBox)));

        return leftBox.concat(rightBox);
    }

    private static byte [] xor(byte[] key, byte[] inputExpand) {
        byte[] xorOutput = new byte[inputExpand.length];
        for (int i = 0; i < inputExpand.length; i++){
            xorOutput[i] = (byte) ( key[i] ^ inputExpand[i]);
        }
        return xorOutput;
    }

    private static byte[] convertStringToByte(String inputString) {

        byte[] convertedToByte =  new byte[inputString.length()];
        for (int i = 0; i < inputString.length(); i++){
            convertedToByte[i] = (byte)( (inputString.charAt(i)) -48);

            if (convertedToByte[i] != 0 && convertedToByte[i] != 1) {
                throw new RuntimeException("\n The Key is not valid");
            }
        }
        return convertedToByte;
    }


    public byte[] encrypt(byte[] rawKey, byte[] plainText){
        //Key Generation
        generateKey(rawKey);
        byte[] combinedP4Part1 = sBoxOutputs(initialPermutation(plainText), key1);
        byte[] reverseCombinedP4 = reverseCombinedP4(combinedP4Part1);
        byte[] combinedP4Part2 = sBoxOutputs(reverseCombinedP4, key2);

        return initialPermutationInverse(combinedP4Part2);
    }

    public byte[] decrypt(byte[] rawKey, byte[] cipherText){
        //Key Generation
        generateKey(rawKey);
        byte[] combinedP4Part1 = sBoxOutputs(initialPermutation(cipherText), key2);
        byte[] reverseCombinedP4 = reverseCombinedP4(combinedP4Part1);
        byte[] combinedP4Part2 = sBoxOutputs(reverseCombinedP4, key1);

        return initialPermutationInverse(combinedP4Part2);
    }

    public  byte[] encrpytTriple(byte[] rawkey1, byte[] rawkey2, byte[] plaintext) {
        return encrypt(rawkey1, decrypt(rawkey2, encrypt(rawkey1, plaintext)));
    }

    public  byte[] decryptTriple(byte[] rawkey1, byte[] rawkey2, byte[] ciphertext) {
        return decrypt(rawkey1, encrypt(rawkey2, decrypt(rawkey1, ciphertext)));
    }


}
