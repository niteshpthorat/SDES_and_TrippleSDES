package csula.cs4780.project1;

import java.util.Scanner;

public class CrackingSDES {
    static SDES sdes = new SDES();
    static CrackingSDES crackSDES = new CrackingSDES();
    static CASCII CASCII = new CASCII();

    public static void main (String[] args){

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nQuestion 1 : ");
        byte[] rawKey3 = {0,1,1,1,0,0,1,1,0,1};
        System.out.println("Enter Plaintext for converting to CASCII bits");
        String plainText = scanner.nextLine();

        byte[] result = crackSDES.sdesEncode(plainText.toUpperCase(), rawKey3);
        System.out.println("\nCiphertext CASCII converted to bits:");
        for (byte b : result) {
            System.out.print(b + " ");
        }

        System.out.println("\n\nQuestion 2");
        String input = "1011011001111001001011101111110000111110100000000001110111010001111011111101101100010011000000101101011010101000101111100011101011010111100011101001010111101100101110000010010101110001110111011111010101010100001100011000011010101111011111010011110111001001011100101101001000011011111011000010010001011101100011011110000000110010111111010000011100011111111000010111010100001100001010011001010101010000110101101111111010010110001001000001111000000011110000011110110010010101010100001000011010000100011010101100000010111000000010101110100001000111010010010101110111010010111100011111010101111011101111000101001010001101100101100111001110111001100101100011111001100000110100001001100010000100011100000000001001010011101011100101000111011100010001111101011111100000010111110101010000000100110110111111000000111110111010100110000010110000111010001111000101011111101011101101010010100010111100011100000001010101110111111101101100101010011100111011110101011011";
        byte[] result2 = crackSDES.sdes(input);
        System.out.println();
        System.out.print("Key: ");
        for (byte b : result2) {
            System.out.print(b);
        }

    }


    private byte[] sdes(String input) {
        byte[] result = new byte[input.length()];
        byte[] encrypted_bytes = getBytes(input);
        byte[] testKey = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < 1024; i++) {
            byte[] plaintext_encoded = decryptMessage(testKey, encrypted_bytes);
            String plainText = CASCII.toString(plaintext_encoded);
            //BRUTE FORCE TO FIND THE PLAIN TEXT
            /*System.out.println();
            System.out.print("Key: ");
            for (byte b : testKey) {
                System.out.print(b);
            }
            System.out.println();
            System.out.print("Message: " + plainText);
            incrementKey(testKey, i);*/
            // To print only the output decrypted text
            if (plainText.toLowerCase().contains("whoever")) {
                System.out.println("Message: " + plainText);
                return testKey;
            } else {
                incrementKey(testKey, i);
            }

        }
        return result;
    }

    public static byte[] decryptMessage(byte[] rawkey, byte[] cipher) {
        byte[] plaintext = new byte[cipher.length];

        for (int i = 0; i < cipher.length; i += 8) {
            byte[] ciphertext = new byte[8];
            System.arraycopy(cipher, i, ciphertext, 0, 8);
            byte[] temp = sdes.decrypt(rawkey, ciphertext);
            System.arraycopy(temp, 0, plaintext, i, 8);
        }
        return plaintext;
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

    private byte[] sdesEncode(String plainText, byte[] rawKey) {
        byte[] convertedText = CASCII.Convert(plainText);
        System.out.println("CASCII conversion of Plain Text:");
        for (byte b : convertedText) {
            System.out.print(b + " ");
        }
        return encryptMessage(rawKey, convertedText);
    }

    public static byte[] encryptMessage(byte[] rawkey, byte[] message) {
        byte[] ciphertext = new byte[message.length];

        for (int i = 0; i < message.length; i += 8) {
            byte[] plaintext = new byte[8];
            System.arraycopy(message, i, plaintext, 0, 8);
            byte[] temp = sdes.encrypt(rawkey, plaintext);
            System.arraycopy(temp, 0, ciphertext, i, 8);
        }
        return ciphertext;
    }
}
