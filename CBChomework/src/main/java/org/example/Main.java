package org.example;


import java.io.*;
import java.util.Arrays;

public class Main {


    public static String sessionkey = "alsodkql";
    public static String IV = "anskqwfe";
//    public static String plaintext = "ddddeash";


    // PC1 테이블 정의
    public static int[] pc1Table = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    public static int[] pc2Table = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };


    public static int[] initialPermutationTable = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };



    private static final int[][] sBox = {
            {// S-Box 0
                    14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
            },
            {// S-Box 1
                    15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
            },
            {// S-Box 2
                    15, 2, 8, 7, 1, 14, 10, 11, 5, 3, 4, 12, 6, 0, 9, 13,
                    10, 13, 1, 11, 6, 8, 12, 7, 3, 15, 0, 14, 9, 2, 5, 4,
                    3, 12, 1, 10, 4, 5, 9, 0, 14, 6, 7, 11, 2, 8, 13, 15,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15
            },
            {// S-Box 3
                    10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
            },
            {// S-Box 4
                    7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
            },
            {// S-Box 5
                    2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
            },
            {// S-Box 6
                    12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
            },
            {// S-Box 7
                    4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
            }
    };


    public static int[] permutationTable = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    public static int[] expansionTable = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };


    public static int[] finalPermutationTable = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    /**
     * 최종 순열
     * @param inputBlock 변환할 입력 비트 배열.
     * @return 최종 순열로 변환된 비트 배열.
     */
    public static int[] finalPermutation(int[] inputBlock) {

        int[] permutedBlock = new int[64];

        for (int i = 0; i < 64; i++) {
            int bitPosition = finalPermutationTable[i] - 1;
            permutedBlock[i] = inputBlock[bitPosition];
        }

        return permutedBlock;
    }


    public static int[] convertToBinary(String input) {
        // 입력의 이진 표현을 저장할 배열을 생성합니다.
        // 각 문자 또는 숫자는 8비트(1바이트)로 변환되므로 배열 크기를 조정합니다.
        int[] binaryArray = new int[input.length() * 8];

        int index = 0;
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                // 입력 문자가 10진수 숫자인 경우, 이를 이진으로 변환합니다.
                int decimalValue = Character.getNumericValue(c);
                String binaryValue = String.format("%8s", Integer.toBinaryString(decimalValue)).replace(' ', '0');
                for (char bit : binaryValue.toCharArray()) {
                    binaryArray[index++] = Character.getNumericValue(bit);
                }
            } else {
                // 입력 문자인 경우 (ASCII 코드로 변환)
                int asciiValue = (int) c;
                String binaryValue = String.format("%8s", Integer.toBinaryString(asciiValue)).replace(' ', '0');
                for (char bit : binaryValue.toCharArray()) {
                    binaryArray[index++] = Character.getNumericValue(bit);
                }
            }
        }

        return binaryArray;
    }

    public static int[] xor(int[] arr1, int[] arr2) {
        // 결과 배열을 초기화합니다.
        int[] result = new int[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i] ^ arr2[i];
        }

        return result;
    }

    public static int[] expansion(int[] arr) {
        // 확장된 배열을 생성합니다.
        int[] expanded = new int[48];
        // 확장 표(expansionTable)를 사용하여 확장합니다.
        for (int i = 0; i < 48; i++) {
            int bitPosition = expansionTable[i] - 1;
            expanded[i] = arr[bitPosition];
        }
        return expanded;
    }

    public static int[] initialPermutation(int[] inputBlock) {
        // 초기 순열을 적용한 배열을 생성합니다.
        int[] permutedBlock = new int[64];

        for (int i = 0; i < 64; i++) {
            int bitPosition = initialPermutationTable[i] - 1;
            permutedBlock[i] = inputBlock[bitPosition];
        }

        return permutedBlock;
    }

    public static int[] permutation(int[] block) {
        // 순열을 적용한 배열을 생성합니다.
        int[] permutedBlock = new int[block.length];
        for (int i = 0; i < block.length; i++) {
            int bitPosition = permutationTable[i] - 1;
            permutedBlock[i] = block[bitPosition];
        }
        return permutedBlock;
    }

    public static int[][] PC1(int[] key) {
        // 56비트로 압축된 키 배열을 초기화합니다.
        int[] compressedKey = new int[56];

        // PC1 테이블을 사용하여 64비트 키를 56비트로 압축합니다.
        for (int i = 0; i < 56; i++) {
            int pc1Index = pc1Table[i] - 1;
            compressedKey[i] = key[pc1Index];
        }

        // 56비트 키를 28비트 ci와 di 배열로 분리합니다.
        int[] ci = new int[28];
        int[] di = new int[28];

        System.arraycopy(compressedKey, 0, ci, 0, 28);
        System.arraycopy(compressedKey, 28, di, 0, 28);

        return new int[][]{ci, di};
    }

    public static int[] PC2(int[][] shiftedKey) {
        // shiftedCi와 shiftedDi를 결합하여 56비트 키를 생성합니다.
        int[] shiftedCi = shiftedKey[0];
        int[] shiftedDi = shiftedKey[1];
        int combinedLength = shiftedCi.length + shiftedDi.length;
        int[] combinedKey = new int[combinedLength];

        for (int i = 0; i < shiftedCi.length; i++) {
            combinedKey[i] = shiftedCi[i];
            combinedKey[i + shiftedCi.length] = shiftedDi[i];
        }

        // PC2 테이블을 사용하여 56비트 키를 48비트로 압축합니다.
        int[] pc2Result = new int[48];
        for (int i = 0; i < 48; i++) {
            int pc2Index = pc2Table[i] - 1; // PC2 테이블에서 해당 비트의 위치
            pc2Result[i] = combinedKey[pc2Index]; // 해당 위치의 비트 가져오기
        }

        return pc2Result;
    }

    /**
     * @param compressedKey PC1을 거친 총 56비트의 ci + di를 받음
     * @return 각 28비트의 ci, di를 Left Shift Rotate과정 후 int[][] 형식으로 반환(총 58비트)
     */
    public static int[][] leftShift(int[][] compressedKey, int round) {
        int[] ci = compressedKey[0];
        int[] di = compressedKey[1];

        int shiftAmount;
        if (round == 1 || round == 2 || round == 9 || round == 16) {
            shiftAmount = 2; // 1, 2, 9, 16 라운드에서는 2비트 시프트
        } else {
            shiftAmount = 1; // 나머지 라운드에서는 1비트 시프트
        }

        int ciLength = 28;
        int[] shiftedCi = new int[ciLength];
        int[] shiftedDi = new int[ciLength];

        // Ci와 Di를 좌측으로 시프트합니다 (left rotate shift).
        for (int i = 0; i < ciLength; i++) {
            shiftedCi[i] = ci[(i + shiftAmount) % ciLength];
            shiftedDi[i] = di[(i + shiftAmount) % ciLength];
        }

        // 가장 좌측 비트를 유지합니다.
        shiftedCi[0] = ci[(ciLength - shiftAmount) % ciLength];
        shiftedDi[0] = di[(ciLength - shiftAmount) % ciLength];

        return new int[][]{shiftedCi, shiftedDi};
    }

    public static int[] substitution(int[] xorResult) {
        int[] substitutedBlock = new int[32]; // S-Box 대체 결과를 저장할 배열 (32비트)

        // 6비트씩 처리하며 S-Box를 적용합니다.
        for (int i = 0; i < 48; i += 6) {
            int row = xorResult[i] * 2 + xorResult[i + 5]; // S-Box에서 행 결정
            int col = xorResult[i + 1] * 8 + xorResult[i + 2] * 4 + xorResult[i + 3] * 2 + xorResult[i + 4]; // S-Box에서 열 결정
            int sBoxValue = sBox[i / 6][row * 16 + col]; // S-Box에서의 대체 값

            // 4비트로 변환 후 substitutedBlock에 저장합니다.
            for (int j = 0; j < 4; j++) {
                substitutedBlock[i / 6 * 4 + j] = (sBoxValue >> (3 - j)) & 1;
            }
        }

        return substitutedBlock;
    }



        private static int[][] generateRoundKeys() {
        int[][] roundKeys = new int[16][48]; // 16개의 라운드 키 (각각 48비트)

        int[] originKey = convertToBinary(sessionkey);

        // 64비트 키를 56비트로 압축 (PC1 적용)
        int[][] compressedKey = PC1(originKey);

        int[] ci = compressedKey[0];
        int[] di = compressedKey[1];

        for (int round = 0; round < 16; round++) {
            // 좌측으로 시프트
            int[][] shiftedKey = leftShift(new int[][]{ci, di}, round + 1);

            // 56비트 키를 48비트로 압축 (PC2 적용)
            roundKeys[round] = PC2(shiftedKey);

            ci = shiftedKey[0];
            di = shiftedKey[1];
        }

        return roundKeys;
    }

    public static int[] encrypt(String plaintext) {
        int[] plain = convertToBinary(plaintext);
        int[] initialVector = convertToBinary(IV);

        int[][] roundKeys = generateRoundKeys();

        int[] permuted = xor(plain, initialVector);

        int[] leftHalf = Arrays.copyOfRange(permuted, 0, 32);
        int[] rightHalf = Arrays.copyOfRange(permuted, 32, 64);

        for (int round = 0; round < 16; round++) {
            int[] expandedRightHalf = expansion(rightHalf);
            int[] xorResult = xor(expandedRightHalf, roundKeys[round]);
            int[] substitutedBlock = substitution(xorResult);
            int[] permutedBlock = permutation(substitutedBlock);

            int[] temp = leftHalf.clone();

            leftHalf = rightHalf.clone();
            rightHalf = xor(temp, permutedBlock);
        }

        int[] finalPermutationInput = concatenateArrays(rightHalf, leftHalf);

        int[] ciphertext = finalPermutation(finalPermutationInput);

        return ciphertext;
    }

    public static int[] decrypt(int[] ciphertext) {
        int[] initialVector = convertToBinary(IV);
        int[][] roundKeys = generateRoundKeys();
        int[] initialPermutationInput = initialPermutation(ciphertext);

        int[] leftHalf = Arrays.copyOfRange(initialPermutationInput, 0, 32);
        int[] rightHalf = Arrays.copyOfRange(initialPermutationInput, 32, 64);

        for (int round = 15; round >= 0; round--) {
            int[] expandedRightHalf = expansion(rightHalf);
            int[] xorResult = xor(expandedRightHalf, roundKeys[round]);
            int[] substitutedBlock = substitution(xorResult);
            int[] permutedBlock = permutation(substitutedBlock);

            int[] temp = leftHalf.clone();

            leftHalf = rightHalf.clone();
            rightHalf = xor(temp, permutedBlock);
        }

        int[] finalPermutationInput = concatenateArrays(rightHalf, leftHalf);

        int[] plaintext = xor(finalPermutationInput, initialVector);

        return plaintext;
    }


    private static int[] concatenateArrays(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }


    public static String binaryIntArrayToString(int[] binaryArray) {
        StringBuilder result = new StringBuilder();

        // 이진 배열을 8비트 단위로 묶어서 처리
        for (int i = 0; i < binaryArray.length; i += 8) {
            StringBuilder binaryStr = new StringBuilder();

            // 8비트로 구성된 문자열 생성
            for (int j = 0; j < 8; j++) {
                binaryStr.append(binaryArray[i + j]);
            }

            // 2진수 문자열을 10진수 정수로 변환하고 ASCII 문자로 변환
            int decimalValue = Integer.parseInt(binaryStr.toString(), 2);
            char character = (char) decimalValue;

            // 결과 문자열에 추가
            result.append(character);
        }

//        System.out.println(result.toString());

        return result.toString();
    }


    private static String readFile(String path) {
        try {
            // 파일 읽기
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 오류 시 null 반환
    }

    public static void writeFile(String path, String decrypt) {
        try {
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(decrypt);

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }




    public static void main(String[] args) {

        //================================================================================================
        //==================================      Plain text, KEY     ====================================
        //================================================================================================

        String plaintext = readFile("/Users/devmingyu/Desktop/plaintext.txt"); // 원문
        String key = sessionkey; // 8바이트 키

        System.out.println("initial Vector : " + IV);
        System.out.println("session Key "+ sessionkey);
        System.out.println("Plaintext: " + plaintext);



        //================================================================================================
        //===============================      Cipher text, encrypt    ====================================
        //================================================================================================

        System.out.println("  ");
        int[] ciphertext = encrypt(plaintext);
        System.out.println("Ciphertext: "+ binaryIntArrayToString(ciphertext));


        //================================================================================================
        //===============================      Cipher text, decrypt    ====================================
        //================================================================================================

        int[] decryptedBits = decrypt(ciphertext);
        String decryptedText = binaryIntArrayToString(decryptedBits);
        writeFile("/Users/devmingyu/Desktop/decrypt_result.txt",decryptedText);

        // 복호화된 평문 출력
        System.out.println("Decrypted Text: " + decryptedText);
    }
}


