package org.example;


import java.util.Arrays;
import java.io.*;

public class Main {
    private static final String symmetric_Key = "qwerasdf";


    /**
     * @param key symmetric 8byte 문자열 key를 입력받음<br>
     * @return 64bit 문자열을 반환
     * */
    public static int[] initialKey(String key) {
        byte[] initialKey = key.getBytes();
        int[] bitArray = new int[initialKey.length * 8];

        int bitIndex = 0;
        for (byte b : initialKey)
            for (int i = 7; i >= 0; i--) {
                bitArray[bitIndex++] = (b >> i) & 1;
            }
        return bitArray;
    }

    /**
     * DES의 PC1 연산을 수행하여 64비트 키를 56비트로 압축
     * 그 결과를 28비트인 ci와 di 배열로 반환
     *
     * @param key DES 암호키 (64비트)
     * @return ci와 di 배열을 포함한 int[][] 형식의 결과 (총 56비트)
     */
    public static int[][] performPC1(int[] key) {
        // PC1 테이블 정의
        int[] pc1Table = {
                57, 49, 41, 33, 25, 17, 9,
                1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27,
                19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15,
                7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29,
                21, 13, 5, 28, 20, 12, 4
        };

        // 56비트 압축 키 배열 초기화
        int[] compressedKey = new int[56];

        // PC1 테이블을 사용하여 64비트 키를 56비트로 압축
        for (int i = 0; i < 56; i++) {
            int pc1Index = pc1Table[i] - 1;
            compressedKey[i] = key[pc1Index];
        }

        // 56비트 키를 28비트 ci와 di 배열로 분리
        int[] ci = new int[28];
        int[] di = new int[28];

        System.arraycopy(compressedKey, 0, ci, 0, 28);
        System.arraycopy(compressedKey, 28, di, 0, 28);

        return new int[][]{ci, di};
    }




    /**
     * @param compressedKey PC1을 거친 총 56비트의 ci + di를 받음
     * @return 각 28bit의 ci,di 를 Left Shift Rotate과정 후 int[][] 형식으로 반환(총 58bit)
     * */
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

        // Ci와 Di를 좌측으로 시프트 (left rotate shift)
        for (int i = 0; i < ciLength; i++) {
            shiftedCi[i] = ci[(i + shiftAmount) % ciLength];
            shiftedDi[i] = di[(i + shiftAmount) % ciLength];
        }

        // 가장 좌측 비트를 유지
        shiftedCi[0] = ci[(ciLength - shiftAmount) % ciLength];
        shiftedDi[0] = di[(ciLength - shiftAmount) % ciLength];

        return new int[][]{ shiftedCi, shiftedDi };
    }


    /**
     * DES의 PC2 연산을 수행하여 56비트 키를 48비트로 압축
     * 그 결과를 48비트 int 배열로 반환
     *
     * @param shiftedKey Left Shift Rotate를 거친 ci와 di 배열 (각각 28비트)
     * @return 48비트 int 배열
     */
    public static int[] PC2(int[][] shiftedKey) {

        int[] pc2Table = {
                14, 17, 11, 24, 1, 5,
                3, 28, 15, 6, 21, 10,
                23, 19, 12, 4, 26, 8,
                16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55,
                30, 40, 51, 45, 33, 48,
                44, 49, 39, 56, 34, 53,
                46, 42, 50, 36, 29, 32
        };

        // shiftedCi와 shiftedDi를 결합하여 56비트 키 생성
        int[] shiftedCi = shiftedKey[0];
        int[] shiftedDi = shiftedKey[1];
        int combinedLength = shiftedCi.length + shiftedDi.length;
        int[] combinedKey = new int[combinedLength];

        for (int i = 0; i < shiftedCi.length; i++) {
            combinedKey[i] = shiftedCi[i];
            combinedKey[i + shiftedCi.length] = shiftedDi[i];
        }

        // PC2 테이블을 사용하여 56비트 키를 48비트로 압축
        int[] pc2Result = new int[48];
        for (int i = 0; i < 48; i++) {
            int pc2Index = pc2Table[i] - 1; // PC2 테이블에서 해당 비트의 위치
            pc2Result[i] = combinedKey[pc2Index]; // 해당 위치의 비트 가져오기
        }

        return pc2Result;
    }


    public static int[] performInitialPermutation(int[] inputBlock) {
        int[] initialPermutationTable = {
                58, 50, 42, 34, 26, 18, 10, 2,
                60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6,
                64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9, 1,
                59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5,
                63, 55, 47, 39, 31, 23, 15, 7
        };

        int[] permutedBlock = new int[64];

        for (int i = 0; i < 64; i++) {
            int bitPosition = initialPermutationTable[i] - 1;
            permutedBlock[i] = inputBlock[bitPosition];
        }

        return permutedBlock;
    }




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

    private static final int[] permutationTable = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    private static final int[] expansionTable = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };


    /**
     * 주어진 오른쪽 반을 확장
     *
     * @param rightHalf 확장할 비트 배열
     * @return 확장된 비트 배열
     */
    public static int[] expansion(int[] rightHalf) {
        int[] expandedRightHalf = new int[48];
        // 확장 표(expansionTable)를 사용하여 확장
        for (int i = 0; i < 48; i++) {
            int bitPosition = expansionTable[i] - 1;
            expandedRightHalf[i] = rightHalf[bitPosition];
        }
        return expandedRightHalf;
    }



    /**
     * XOR 연산
     * @param expandedRightHalf XOR 연산에 사용될 첫 번째 비트 배열.
     * @param roundKey          XOR 연산에 사용될 두 번째 비트 배열 (라운드 키).
     * @return XOR 연산 결과 비트 배열
     */
    public static int[] xor(int[] expandedRightHalf, int[] roundKey) {

        if(expandedRightHalf.length == 48) {
            int[] xorResult = new int[48];
            for (int i = 0; i < 48; i++) {
                xorResult[i] = expandedRightHalf[i] ^ roundKey[i];
            }
            return xorResult;
        }
        else{
            int[] xorResult = new int[32];
            for (int i = 0; i < 32; i++) {
                xorResult[i] = expandedRightHalf[i] ^ roundKey[i];
            }
            return xorResult;
        }
    }



    /**
     * S-Box를 사용하여 48비트 입력을 32비트로 변환
     *
     * @param xorResult S-Box 적용에 사용될 48비트 입력 비트 배열.
     * @return 32비트 출력 비트 배열.
     */
    public static int[] substitution(int[] xorResult) {
        // 48비트 입력을 32비트 출력으로 변환
        int[] substitutedBlock = new int[32];
        // 6비트씩 처리하며 S-Box를 적용
        for (int i = 0; i < 48; i += 6) {
            int row = xorResult[i] * 2 + xorResult[i + 5];
            int col = xorResult[i + 1] * 8 + xorResult[i + 2] * 4 + xorResult[i + 3] * 2 + xorResult[i + 4];
            int value = sBox[i / 6][row * 16 + col];
            // 4비트 이진수로 변환 후 substitutedBlock에 저장
            for (int j = 0; j < 4; j++) {
                substitutedBlock[i / 6 * 4 + j] = (value >> (3 - j)) & 1;
            }
        }
        return substitutedBlock;
    }

    /**
     * @param block 변환할 비트 배열
     * @return 순열로 변환된 비트 배열
     */
    public static int[] permutation(int[] block) {
        int[] permutedBlock = new int[block.length];
        for (int i = 0; i < block.length; i++) {
            int bitPosition = permutationTable[i] - 1;
            permutedBlock[i] = block[bitPosition];
        }
        return permutedBlock;
    }


    /**
     * 최종 순열
     * @param inputBlock 변환할 입력 비트 배열.
     * @return 최종 순열로 변환된 비트 배열.
     */
    public static int[] performFinalPermutation(int[] inputBlock) {
        int[] finalPermutationTable = {
                40, 8, 48, 16, 56, 24, 64, 32,
                39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 46, 14, 54, 22, 62, 30,
                37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 44, 12, 52, 20, 60, 28,
                35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 42, 10, 50, 18, 58, 26,
                33, 1, 41, 9, 49, 17, 57, 25
        };

        int[] permutedBlock = new int[64];

        for (int i = 0; i < 64; i++) {
            int bitPosition = finalPermutationTable[i] - 1;
            permutedBlock[i] = inputBlock[bitPosition];
        }

        return permutedBlock;
    }


    /**
     * 초기 키를 사용하여 16개의 라운드 키를 생성
     *
     * @param initialKey 초기 키 비트 배열
     * @return 16개의 라운드 키를 포함하는 2차원 배열
     */
    private static int[][] generateRoundKeys(int[] initialKey) {
        int[][] roundKeys = new int[16][48];

        // PC1 연산 수행
        int[][] pc1Result = performPC1(initialKey);

        // 16 라운드의 라운드 키 생성
        for (int round = 0; round < 16; round++) {
            // 좌측과 우측으로 나뉜 Ci와 Di를 생성
            int[][] shiftedKey = leftShift(pc1Result, round + 1);
            int[] combinedCiDi = concatenateArrays(shiftedKey[0], shiftedKey[1]);

            // PC2 연산을 수행하여 라운드 키 생성
            int[] roundKey = PC2(shiftedKey);
            roundKeys[round] = roundKey;
        }

        return roundKeys;
    }


    /**
     * @param plaintext 평문 문자열.
     * @param key       사용할 키 문자열.
     * @return 암호문을 나타내는 비트 배열.
     */
    public static int[] encrypt(String plaintext, String key) {
        int[] plaintextBits = stringToBinaryArray(plaintext);
        int[] keyBits = initialKey(key);
        int[][] roundKeys = generateRoundKeys(keyBits);
        int[] initialPermutationResult = performInitialPermutation(plaintextBits);

        int[] leftHalf = Arrays.copyOfRange(initialPermutationResult, 0, 32);
        int[] rightHalf = Arrays.copyOfRange(initialPermutationResult, 32, 64);
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
        int[] ciphertext = performFinalPermutation(finalPermutationInput);

        return ciphertext;
    }

    /**
     * @param ciphertext 암호문을 나타내는 비트 배열
     * @param key        사용할 키 문자열
     * @return 복호화된 평문을 나타내는 비트 배열
     */
    public static int[] decrypt(int[] ciphertext, String key) {

        int[][] roundKeys = generateRoundKeys(initialKey(key));
        // 초기 순열 (Initial Permutation)
        int[] initialPermutationResult = performInitialPermutation(ciphertext);

        // 초기 순열과는 반대로 왼쪽 반과 오른쪽 반을 교환
        int[] leftHalf = Arrays.copyOfRange(initialPermutationResult, 0, 32);
        int[] rightHalf = Arrays.copyOfRange(initialPermutationResult, 32, 64);

        // 16 라운드의 키를 역순으로 사용하여 복호화
        for (int round = 15; round >= 0; round--) {
            // 오른쪽 반만을 처리
            int[] expandedRightHalf = expansion(rightHalf);
            int[] xorResult = xor(expandedRightHalf, roundKeys[round]);
            int[] substitutedBlock = substitution(xorResult);
            int[] permutedBlock = permutation(substitutedBlock);

            // 왼쪽 반과 XOR 연산
            int[] temp = leftHalf.clone();
            leftHalf = rightHalf.clone();
            rightHalf = xor(temp, permutedBlock);
        }

        // 최종 라운드 후, 왼쪽 반과 오른쪽 반을 교환 (Final Permutation 직전)
        int[] finalPermutationInput = concatenateArrays(rightHalf, leftHalf);

        // 최종 순열 (Final Permutation)
        int[] decryptedBits = performFinalPermutation(finalPermutationInput);

        return decryptedBits;
    }

    public static String binaryArrayToString(int[] binaryArray) {
        int numBytes = binaryArray.length / 8;
        byte[] bytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            int byteValue = 0;
            for (int j = 0; j < 8; j++) {
                byteValue = (byteValue << 1) | binaryArray[i * 8 + j];
            }
            bytes[i] = (byte) byteValue;
        }
        return new String(bytes);
    }


    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
        }
        System.out.println();
    }

    private static void printIntArray(int[] intArray) {
        for (int i = 0; i < intArray.length; i++) {
            System.out.print(intArray[i]);
            if ((i + 1) % 8 == 0) {
                System.out.print(" "); // 8비트마다 공백을 추가하여 출력
            }
        }
        System.out.println(); // 줄 바꿈
    }


    private static int[] concatenateArrays(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

    private static int[] stringToBinaryArray(String input) {
        byte[] bytes = input.getBytes();
        int[] binaryArray = new int[bytes.length * 8];
        int index = 0;
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                binaryArray[index++] = (b >> i) & 1;
            }
        }
        return binaryArray;
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


    public static void main(String[] args)
    {

        //================================================================================================
        //==================================      Plain text, KEY     ====================================
        //================================================================================================

        String plaintext = readFile("/Users/devmingyu/Desktop/plaintext.txt"); // 원문
        String key = symmetric_Key; // 8바이트 키

        System.out.println("Plaintext: " + plaintext);
        System.out.println("key: " + key);


        //================================================================================================
        //===============================      Cipher text, encrypt    ====================================
        //================================================================================================


        int[] ciphertext = encrypt(plaintext, key);

        System.out.println("Ciphertext: "+ binaryArrayToString(ciphertext));


        //================================================================================================
        //===============================      Cipher text, decrypt    ====================================
        //================================================================================================

        int[] decryptedBits = decrypt(ciphertext,key);
        String decryptedText = binaryArrayToString(decryptedBits);
        writeFile("/Users/devmingyu/Desktop/decrypt_result.txt",decryptedText);

        // 복호화된 평문 출력
        System.out.println("Decrypted Text: " + decryptedText);
    }
}