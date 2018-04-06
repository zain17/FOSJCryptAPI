package tn.esprit;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        /**
         * Exemple de cryptage: utiliser lors de l'inscription d'un utilisateur
         */
        Sha512 sha512 =FOSJCrypt.crypt("0000");
        System.out.println(sha512);
        /**
         * Exemple de cryptage: utiliser lors de login d'un utilisateur
         */
        String strSaltDB="pdFIRT2iq5SwAJSgFD0V6WY03ww3tiUXX4CrGWUxbeg";
        String strHashDb="edbTsi9vP9TiazpZKTQzJ7Rgl9tehkfO7uDNIQdhBJWl+nkarNSOEwSy8WSWZFXJ9evJxffeVNqCQFNIJrpqhg==";
        boolean cs=FOSJCrypt.checkPassword(strHashDb,"0000",strSaltDB);
        System.out.println(cs);
    }
}
