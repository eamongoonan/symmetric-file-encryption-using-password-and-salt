import java.security.*;
import javax.crypto.spec.*;
import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Encryption
{
    // "Strong" password
    private static final String SECURE_PASSWORD = "p@55w0rd4nd54lt?";

    public static void main(String[] args) throws Exception
    {
        // File to be encrypted taken as argument
        String file = args[0];

        // Generate randomised salt and iv
        byte[] salt = generateRandomBytes();
        byte[] iv = generateRandomBytes();

        // Obtain key using password and salt
        SecretKeySpec aesKey = getKey(salt);

        // Encrypt the file
        byte[] encryptedFile = encryptFile(file, aesKey, iv);

        // RSA encrypt the password
        BigInteger encryptedPassword = rsaEncryptPassword();

        // Output encrypted data and other components to separate files
        writeFiles(salt, iv, encryptedFile, encryptedPassword);
    }

    // Convert byte array to hexadecimal
    private static String toHex(byte[] bytes)
    {
        // Ensures positive value, avoids potential issues regarding twos complement and extra leading zeros
        return new BigInteger(1, bytes).toString(16);
    }

    // Outputs various components to separate files
    private static void writeFiles(byte[] salt, byte[] iv, byte[] encrypted, BigInteger encryptedPassword) throws Exception
    {
        writeFile("Salt.txt", toHex(salt));
        writeFile("IV.txt", toHex(iv));
        writeFile("Encryption.txt", toHex(encrypted));
        writeFile("Password.txt", encryptedPassword.toString(16));
    }

    // Write string to a specified file
    private static void writeFile(String filename, String content) throws Exception
    {
        Files.writeString(Paths.get(filename), content);
    }

    private static SecretKeySpec getKey(byte[] salt) throws NoSuchAlgorithmException
    {
        MessageDigest sha256 = getSHA256Digest();

        // Concatenate password and salt for hashing derivation. Password converted into bytes using UTF-8 encoding
        byte[] hash = concatBytes(Assignment1.SECURE_PASSWORD.getBytes(StandardCharsets.UTF_8), salt);

        // Hash 200 times using SHA_256
        for (int i = 0; i < 200; i++)
        {
            hash = sha256.digest(hash);
        }

        // Return the AES key
        return new SecretKeySpec(hash, "AES");
    }

    // Fetch SHA-256 instance
    private static MessageDigest getSHA256Digest() throws NoSuchAlgorithmException
    {
        return MessageDigest.getInstance("SHA-256");
    }

    // Generate random bytes (for salt and iv)
    private static byte[] generateRandomBytes()
    {
        SecureRandom randomizer = new SecureRandom();
        byte[] bytesArray = new byte[16]; // 16 bytes or 128 bits
        randomizer.nextBytes(bytesArray);
        return bytesArray;
    }

    // Concatenate two byte arrays (password and salt)
    private static byte[] concatBytes(byte[] a, byte[] b)
    {
        byte[] combined = new byte[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }

    private static byte[] encryptFile(String filename, SecretKeySpec aesKey, byte[] iv) throws Exception
    {
        // NoPadding option selected as manual padding is required
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

        // Read file contents
        byte[] fileContents = Files.readAllBytes(Paths.get(filename));

        // Calculate required padding (see if file contents are not multiple of block size)
        int remainder = fileContents.length % 16;
        int paddingLength = (remainder == 0) ? 16 : 16 - remainder;

        // Create padding (with bit values not characters!)
        byte[] padding = new byte[paddingLength];
        padding[0] = (byte) 0x80;

        // Append padding to file contents
        byte[] paddedFile = concatBytes(fileContents, padding);

        // Encrypt and return the padded data
        return cipher.doFinal(paddedFile);
    }

    private static BigInteger rsaEncryptPassword()
    {
        BigInteger passwordInt = new BigInteger(1, Assignment1.SECURE_PASSWORD.getBytes(StandardCharsets.UTF_8));
        return modularExponentiation(passwordInt);
    }

    // RSA encryption using square and multiply, left-to-right method
    private static BigInteger modularExponentiation(BigInteger base)
    {
        // Encryption exponent
        final BigInteger EXPONENT = new BigInteger("65537");

        // Public modulus
        final BigInteger MODULUS = new BigInteger("c406136c12640a665900a9df4df63a84fc855927b729a3a106fb3f379e8e4190ebba442f67b93402e535b18a5777e6490e67dbee954bb02175e43b6481e7563d3f9ff338f07950d1553ee6c343d3f8148f71b4d2df8da7efb39f846ac07c865201fbb35ea4d71dc5f858d9d41aaa856d50dc2d2732582f80e7d38c32aba87ba9", 16);

        // Perform modular exponentiation
        BigInteger result = BigInteger.ONE;
        for (int i = EXPONENT.bitLength() - 1; i >= 0; i--)
        {
            result = result.multiply(result).mod(MODULUS);
            if (EXPONENT.testBit(i))
            {
                result = result.multiply(base).mod(MODULUS);
            }
        }
        return result;
    }
}
