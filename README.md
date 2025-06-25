🔐 AES + RSA File Encryption in Java

This project performs hybrid encryption of a binary file using:

    Symmetric encryption with AES-CBC (128-bit block size, 256-bit key)

    Asymmetric encryption of the password using RSA

    Custom key derivation, manual padding, and modular exponentiation

This project was developed to demonstrate practical cryptography implementation using only Java standard libraries and low-level crypto logic.

📌 How it Works
1. 🔑 Key Derivation

    The user provides a password, encoded as UTF-8.

    A 128-bit random salt (Salt.txt) is generated.

    The password and salt are concatenated as bytes (p || s) and hashed 200 times using SHA-256:

    k = H200(p || s)

    The resulting 256-bit digest k is used as the AES encryption key.

2. 📦 AES-CBC Encryption

    A 128-bit random IV (IV.txt) is generated.

    The input binary file (e.g. Encryption.class) is encrypted using AES-CBC mode with the derived key and IV.

    Custom padding is applied:

        If the final block is incomplete, append a 1 bit and pad the rest with 0 bits.

        If the final block is full, add an entire new block starting with 1 and padded with 0s.

    Output is saved as Encryption.txt in hexadecimal (no whitespace).

3. 🔐 RSA Encryption of Password

    The password (as bytes) is encrypted using RSA public-key encryption:

    c = p^e mod N

    RSA Parameters:

        Public exponent e = 65537

        Public modulus N: (2048-bit hex value provided below)

    The modular exponentiation is implemented manually (no library methods).

    The result is saved as Password.txt (in hex).

🧪 Example Output Files

After execution, the following files will be created:

    Salt.txt – 128-bit salt in hex (32 characters)

    IV.txt – 128-bit AES IV in hex (32 characters)

    Password.txt – RSA-encrypted password in hex

    Encryption.txt – AES-encrypted .class file in hex

    Encryption.java – Your source code

    Encryption.class – The original binary file that was encrypted

🔧 Tech Stack

    Language: Java

    Libraries used:

        java.math.BigInteger

        java.security.MessageDigest (SHA-256)

        javax.crypto (AES)

    All modular exponentiation is written manually using the square-and-multiply method (left-to-right or right-to-left allowed).

    AES encryption uses NoPadding mode with manual bit-level padding.

📥 Input & Output

Command-line usage:

java Encryption Encryption.class > Encryption.txt

This takes a compiled .class file as input, encrypts it, and outputs the encrypted result in hex to Encryption.txt.
🔐 RSA Modulus (N)

The public RSA modulus used for encryption (in hexadecimal, 2048 bits):

c406136c12640a665900a9df4df63a84fc855927b729a3a106fb3f379e8e4190
ebba442f67b93402e535b18a5777e6490e67dbee954bb02175e43b6481e7563d
3f9ff338f07950d1553ee6c343d3f8148f71b4d2df8da7efb39f846ac07c8652
01fbb35ea4d71dc5f858d9d41aaa856d50dc2d2732582f80e7d38c32aba87ba9

⚠️ Common Pitfalls Addressed

This implementation avoids several common mistakes:

    Incorrect UTF-8 encoding of passwords

    Misordered or incorrect byte concatenation (p || s)

    Wrong number of hash iterations

    Incorrect padding implementation

    Misuse of BigInteger.toByteArray() (which may add a leading 0-byte)

    Hex formatting issues (e.g., negative numbers, spacing)
