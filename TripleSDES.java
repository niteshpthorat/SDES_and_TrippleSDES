package csula.cs4780.project1;

import java.util.Scanner;

public class TripleSDES {
    private static byte[] key = new byte[10];
    private static byte[] key1 = new byte[8];
    private static byte[] key2 = new byte[8];

    static TripleSDES tdes = new TripleSDES();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String plainText, rawKey1, rawKey2, option = null;

        try {
            do {

                System.out.println("\n Press key 1 => Encrypt, 2 => Decrypt, 0 => Terminate\n");
                option = scanner.next();

                if (option.equals("1")) {

                    System.out.print("Enter first 10-bit Key : ");
                    rawKey1 = scanner.next();

                    System.out.print("Enter second 10-bit Key : ");
                    rawKey2 = scanner.next();

                    System.out.print("Enter 8-bit Plaintext : ");
                    plainText = scanner.next();

                    byte[] encryptedText = Encrypt(tdes.convertToByte(rawKey1), tdes.convertToByte(rawKey2),
                            tdes.convertToByte(plainText));
                    System.out.print("\nCiphertext is: ");

                    for (byte text : encryptedText) {
                        System.out.print(text);
                    }

                } else if (option.equals("2")) {

                    System.out.println("\n-------Decrypt---------------\n");

                    System.out.print("Enter first 10-bit Key : ");
                    rawKey1 = scanner.next();

                    System.out.print("Enter second 10-bit Key : ");
                    rawKey2 = scanner.next();

                    System.out.print("Enter 8-bit Ciphertext : ");
                    plainText = scanner.next();

                    byte[] decryptedText = Decrypt(tdes.convertToByte(rawKey1), tdes.convertToByte(rawKey2),
                            tdes.convertToByte(plainText));
                    System.out.print("\nPlaintext is: ");

                    for (byte text : decryptedText) {
                        System.out.print(text);
                    }

                }

            } while (!option.equals("0"));

        } catch (Exception e) {
            System.out.println("Caught an error : " + e);
        } finally {
            scanner.close();
        }
    }

    public static byte[] Encrypt(byte[] rawkey1, byte[] rawkey2, byte[] plaintext) {
        return tdes.encryption(rawkey1, tdes.decryption(rawkey2, tdes.encryption(rawkey1, plaintext)));
    }

    public static byte[] Decrypt(byte[] rawkey1, byte[] rawkey2, byte[] ciphertext) {
        return tdes.decryption(rawkey1, tdes.encryption(rawkey2, tdes.decryption(rawkey1, ciphertext)));
    }


    public static byte[] decryption(byte[] rawKey, byte[] cipherText) {
        key = rawKey;
        generateKey();

        cipherText = initialPermutation(cipherText);

        byte[] leftPermutation = new byte[4];
        byte[] rightPermutation = new byte[4];

        //Left 4 bits of ciphertext
        for (int i = 0; i < 4; i++) {
            leftPermutation[i] = cipherText[i];
        }

        //Right 4 bits of ciphertext
        for (int i = 0; i < 4; i++) {
            rightPermutation[i] = cipherText[i + 4];
        }
        int[] exp = new int[8];
        exp = functionFofK(leftPermutation, rightPermutation, key2);

        byte[] temp = new byte[8];
        temp = swap(exp);

        //
        for (int i = 0; i < 4; i++) {
            leftPermutation[i] = temp[i];
        }
        for (int i = 0; i < 4; i++) {
            rightPermutation[i] = temp[i + 4];
        }
        int[] right1 = new int[8];
        right1 = functionFofK(leftPermutation, rightPermutation, key1);

        byte[] key = new byte[right1.length];
        for (int i = 0; i < right1.length; i++) {
            key[i] = Byte.parseByte(Integer.toString(right1[i]));
        }

        cipherText = key;
        cipherText = inverseIP(cipherText);

        return cipherText;
    }

    public static byte[] encryption(byte[] rawKey, byte[] plainText) {
        key = rawKey;
        generateKey();

        plainText = initialPermutation(plainText);

        byte[] leftPermutation = new byte[4];
        byte[] rightPermutation = new byte[4];

        for (int i = 0; i < 4; i++) {
            leftPermutation[i] = plainText[i];
        }

        for (int i = 0; i < 4; i++) {
            rightPermutation[i] = plainText[i + 4];
        }

        int[] exp = new int[8];
        exp = functionFofK(leftPermutation, rightPermutation, key1);

        byte[] temp = new byte[8];
        temp = swap(exp);


        for (int i = 0; i < 4; i++) {
            leftPermutation[i] = temp[i];
        }

        for (int i = 0; i < 4; i++) {
            rightPermutation[i] = temp[i + 4];
        }

        int[] r2 = new int[8];
        r2 = functionFofK(leftPermutation, rightPermutation, key2);

        byte[] key = new byte[r2.length];
        for (int i = 0; i < r2.length; i++) {
            key[i] = Byte.parseByte(Integer.toString(r2[i]));
        }

        plainText = key;
        plainText = inverseIP(plainText);

        return plainText;
    }

    public static byte[] initialPermutation(byte[] plainText) {
        byte[] temp = new byte[8];
        int[] order = {1, 5, 2, 0, 3, 7, 4, 6};

        for (int i = 0; i < 8; i++) {
            temp[i] = plainText[order[i]];
        }
        return temp;
    }

    public static byte[] inverseIP(byte[] plainText) {

        byte[] temp = new byte[8];
        int[] order = {3, 0, 2, 4, 6, 1, 7, 5};
        for (int i = 0; i < 8; i++) {
            temp[i] = plainText[order[i]];
        }

        return temp;

    }

    public static byte[] swap(int[] input) {

        int[] temp = new int[8];
        int[] order = {4, 5, 6, 7, 0, 1, 2, 3};
        for (int i = 0; i < 8; i++) {
            temp[i] = input[order[i]];
        }

        byte[] output = new byte[temp.length];
        for (int i = 0; i < temp.length; i++) {
            output[i] = Byte.parseByte(Integer.toString(temp[i]));
        }

        return output;
    }

    public static int[] functionFofK(byte[] left, byte[] right, byte[] key) {

        int[] temp = new int[4];
        int[] out = new int[8];

        temp = mappingF(right, key);

        int[] keyArr = new int[left.length];
        for (int i = 0; i < left.length; i++) {
            keyArr[i] = Integer.parseInt(Byte.toString(left[i]));
        }

        int[] rightEnd = new int[right.length];
        for (int i = 0; i < right.length; i++) {
            rightEnd[i] = Integer.parseInt(Byte.toString(right[i]));
        }

        for (int i = 0; i < 4; i++) {
            out[i] = keyArr[i] ^ temp[i];
            out[i + 4] = rightEnd[i];
        }
        return out;

    }

    public static int[] mappingF(byte[] right, byte[] key) {
        int[] temp = new int[8];
        int[] order = {3, 0, 1, 2, 1, 2, 3, 0};

        int[] keyArr = new int[key.length];
        for (int i = 0; i < key.length; i++) {
            keyArr[i] = Integer.parseInt(Byte.toString(key[i]));
        }

        int[] mapping = new int[right.length];
        for (int i = 0; i < right.length; i++) {
            mapping[i] = Integer.parseInt(Byte.toString(right[i]));
        }

        //EP
        for (int i = 0; i < temp.length; i++) {
            temp[i] = mapping[order[i]];
        }

        //XOR
        for (int i = 0; i < 8; i++) {
            temp[i] = temp[i] ^ keyArr[i];
        }

        final int[][] S0 = {{1, 0, 3, 2}, {3, 2, 1, 0}, {0, 2, 1, 3}, {3, 1, 3, 2}};
        final int[][] S1 = {{0, 1, 2, 3}, {2, 0, 1, 3}, {3, 0, 1, 0}, {2, 1, 0, 3}};

        int d11 = temp[0];
        int d14 = temp[3];

        int row1 = tdes.BinToDec(d11, d14);

        int d12 = temp[1];
        int d13 = temp[2];
        int col1 = tdes.BinToDec(d12, d13);

        int o1 = S0[row1][col1];

        int[] out1 = tdes.DecToBinArr(o1);

        int d21 = temp[4];
        int d24 = temp[7];
        int row2 = tdes.BinToDec(d21, d24);

        int d22 = temp[5];
        int d23 = temp[6];
        int col2 = tdes.BinToDec(d22, d23);

        int o2 = S1[row2][col2];

        int[] out2 = tdes.DecToBinArr(o2);

        int[] out = new int[4];
        out[0] = out1[0];
        out[1] = out1[1];
        out[2] = out2[0];
        out[3] = out2[1];


        int[] outPermutation = new int[4];
        outPermutation[0] = out[1];
        outPermutation[1] = out[3];
        outPermutation[2] = out[2];
        outPermutation[3] = out[0];

        return outPermutation;
    }

    public static byte[] convertToByte(String givenKey) {

        char c1;
        String ts;
        byte[] bytes = new byte[givenKey.length()];

        try {
            for (int i = 0; i < givenKey.length(); i++) {
                c1 = givenKey.charAt(i);
                ts = Character.toString(c1);
                bytes[i] = Byte.parseByte(ts);

                if (bytes[i] != 0 && bytes[i] != 1) {
                    throw new RuntimeException("\n Not a valid key ");
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return bytes;
    }

    public static void generateKey() {

        permutationP10();
        leftShiftLS1();
        key1 = permutationP8();
        leftShiftLS2();
        key2 = permutationP8();

    }

    public static void permutationP10() {
        byte[] temp = new byte[10];
        int[] order = {2, 4, 1, 6, 3, 9, 0, 8, 7, 5};

        for (int i = 0; i < 10; i++) {
            temp[i] = key[order[i]];
        }
        key = temp;
    }

    public static void leftShiftLS1() {
        byte[] temp = new byte[10];
        int[] order = {1, 2, 3, 4, 0, 6, 7, 8, 9, 5};
        for (int i = 0; i < 10; i++) {
            temp[i] = key[order[i]];
        }
        key = temp;
    }

    public static byte[] permutationP8() {
        byte[] temp = new byte[8];
        int[] order = {5, 2, 6, 3, 7, 4, 9, 8};
        for (int i = 0; i < 8; i++) {
            temp[i] = key[order[i]];
        }
        return temp;

    }

    public static void leftShiftLS2() {
        byte[] temp = new byte[10];

        int[] order = {2, 3, 4, 0, 1, 7, 8, 9, 5, 6};

        for (int i = 0; i < 10; i++) {
            temp[i] = key[order[i]];
        }
        key = temp;

    }

    public static int BinToDec(int... bits) {

        int initial = 0;
        int base = 1;
        for (int i = bits.length - 1; i >= 0; i--) {
            initial = initial + (bits[i] * base);
            base = base * 2;
        }

        return initial;
    }

    public static int[] DecToBinArr(int no) {

        if (no == 0) {
            int[] zero = new int[2];
            zero[0] = 0;
            zero[1] = 0;
            return zero;
        }
        int[] initial = new int[10];

        int count = 0;
        for (int i = 0; no != 0; i++) {
            initial[i] = no % 2;
            no = no / 2;
            count++;
        }

        int[] temp = new int[count];

        for (int i = count - 1, j = 0; i >= 0 && j < count; i--, j++) {
            temp[j] = initial[i];
        }

        if (count < 2) {
            initial = new int[2];
            initial[0] = 0;
            initial[1] = temp[0];
            return initial;
        }

        return temp;
    }
}