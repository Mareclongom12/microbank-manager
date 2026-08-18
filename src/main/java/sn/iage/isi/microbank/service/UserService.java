package sn.iage.isi.microbank.service;

import sn.iage.isi.microbank.dao.UserDAO;
import sn.iage.isi.microbank.model.User;
import sn.iage.isi.microbank.model.UserStatus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // Retourne l'utilisateur si login/mot de passe corrects et compte actif, sinon null
    public User authenticate(String login, String motDePasse) {
        User user = userDAO.findByLogin(login);
        if (user == null) {
            return null;
        }
        if (user.getStatut() != UserStatus.ACTIF) {
            return null;
        }
        String hashed = hashPassword(motDePasse);
        if (!hashed.equals(user.getMotDePasse())) {
            return null;
        }
        return user;
    }

    public void createUser(User user) {
        user.setMotDePasse(hashPassword(user.getMotDePasse()));
        userDAO.save(user);
    }

    public void toggleStatus(Long userId) {
        User user = userDAO.findById(userId);
        if (user != null) {
            user.setStatut(user.getStatut() == UserStatus.ACTIF ? UserStatus.INACTIF : UserStatus.ACTIF);
            userDAO.update(user);
        }
    }

    public List<User> getAll() {
        return userDAO.findAll();
    }

    // Hash simple SHA-256 (suffisant pour un projet académique)
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}