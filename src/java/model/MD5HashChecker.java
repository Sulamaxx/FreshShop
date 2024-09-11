package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class MD5HashChecker {

    // Method to hash a string using MD5
    public static String hashPassword(String input) {
        try {
            // Create MD5 MessageDigest instance
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Convert input to byte array and pass it to the digest method
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array into a signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert the message digest into a hex value
            String hashText = no.toString(16);

            // Add leading zeros to make it 32-bit
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to check if the provided password is correct
    public static boolean checkPassword(String enteredPassword, String storedHash) {
        // Hash the entered password using MD5
        String hashedEnteredPassword = hashPassword(enteredPassword);

        // Compare the entered hashed password with the stored hash
        return hashedEnteredPassword.equals(storedHash);
    }

//    public static void main(String[] args) {
//        // Simulating registration (storing the hashed password)
//        String originalPassword = "myPassword123";
//        String storedHash = md5Hash(originalPassword);
//        System.out.println("Stored Hash: " + storedHash);
//
//        // Simulating login (user provides password)
//        String loginPassword = "myPassword123"; // This is what the user enters
//
//        // Check if the password is correct
//        if (checkPassword(loginPassword, storedHash)) {
//            System.out.println("Login successful! Password is correct.");
//        } else {
//            System.out.println("Login failed! Incorrect password.");
//        }
//    }
}
