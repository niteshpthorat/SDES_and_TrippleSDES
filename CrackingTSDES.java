package csula.cs4780.project1;

import java.util.Scanner;

public class CrackingTSDES {
    static TripleSDES tsdes = new TripleSDES();

    static CrackingTSDES crackTSDES = new CrackingTSDES();

    static CASCII CASCII = new CASCII();

    public static void main (String[] args){

        System.out.println("\n\nQuestion 3");
        String input3 = "00011111100111111110011111101100111000000011001011110010101010110001011101001101000000110011010111111110000000001010111111000001010010111001111001010101100000110111100011111101011100100100010101000011001100101000000101111011000010011010111100010001001000100001111100100000001000000001101101000000001010111010000001000010011100101111001101111011001001010001100010100000";
        byte[] testkey1 = {0,0,0,0,0,0,0,0,0,0};
        byte[] testkey2 = {0,0,0,0,0,0,0,0,0,0};
        crackTSDES.tsdes(input3, testkey1, testkey2);
        System.out.print("Key 1: ");
        for (byte b : testkey1) {
            System.out.print(b);
        }
        System.out.print("\nKey 2: ");
        for (byte b : testkey2) {
            System.out.print(b);
        }

    }

    private void tsdes(String message, byte[] testkey1, byte[] testkey2) {
        byte[] encrypted_bytes = getBytes(message);

        for (int i = 0; i < 1024; i++) {
            for (int j = 0; j < 1024; j++) {
                //BRUTE FORCE TO FIND THE PLAIN TEXT
                byte[] plaintext_encoded = decryptTriple(testkey1, testkey2, encrypted_bytes);
                String plaintext = CASCII.toString(plaintext_encoded);
                System.out.print("KEY1 :   ");
                for (byte b : testkey1) {
                    System.out.print(b);
                }
                System.out.print("           KEY2 :   ");
                for (byte b : testkey2)
                    System.out.print(b);
                System.out.println();
                System.out.println(plaintext);
                    incrementKey(testkey2, j);
                //ONlY TO PRINT the Output
                /*if (plaintext.toLowerCase().contains("guesses")) {
                    System.out.println(plaintext);
                    return;
                } else {
                    incrementKey(testkey2, j);
                }*/
            }
            incrementKey(testkey1, i);
        }
    }


    private void incrementKey(byte[] testKey, int increment) {
        String newKey = Integer.toBinaryString(increment);
        if (newKey.length() < 10) {
            int padding_size = 10 - newKey.length();
            char[] char_padding = new char[padding_size];
            for (int i = 0; i < padding_size; i++) {
                char_padding[i] = '0';
            }
            newKey = new String(char_padding).concat(newKey);
        }
        byte[] newKeyBytes = getBytes(newKey);
        System.arraycopy(newKeyBytes, 0, testKey, 0, testKey.length);
    }

    public static byte[] getBytes(String text) {
        char[] text_arr = new char[text.length()];

        for (int i = 0; i < text.length(); i++) {
            text_arr[i] = text.charAt(i);
        }

        byte[] result = new byte[text.length()];
        for (int i = 0; i < text_arr.length; i++) {
            if (text_arr[i] == '0') {
                result[i] = 0;
            } else {
                result[i] = 1;
            }
        }
        return result;
    }


    public  byte[] decryptTriple(byte[] rawkey1, byte[] rawkey2, byte[] cipherText) {
        {
            byte[] plaintext = new byte[cipherText.length];

            for (int i = 0; i < cipherText.length; i += 8) {
                byte[] cipher = new byte[8];
                System.arraycopy(cipherText, i, cipher, 0, 8);
                byte[] temp = tsdes.Decrypt(rawkey1,rawkey2, cipher);
                System.arraycopy(temp, 0, plaintext, i, 8);
            }
            return plaintext;
        }
    }
}
