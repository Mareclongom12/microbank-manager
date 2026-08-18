package sn.iage.isi.microbank.service;

import sn.iage.isi.microbank.dao.ClientDAO;
import sn.iage.isi.microbank.model.Client;
import sn.iage.isi.microbank.model.ClientStatus;

import java.util.List;
import java.util.regex.Pattern;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public void createClient(Client client) {
        validate(client);
        if (clientDAO.existsByNumeroPiece(client.getNumeroPiece())) {
            throw new IllegalArgumentException("Un client avec ce numéro de pièce existe déjà");
        }
        clientDAO.save(client);
    }

    public void updateClient(Client client) {
        validate(client);
        clientDAO.update(client);
    }

    public void deleteClient(Long id) {
        clientDAO.delete(id);
    }

    public Client getById(Long id) {
        return clientDAO.findById(id);
    }

    public List<Client> getAll() {
        return clientDAO.findAll();
    }

    public List<Client> search(String keyword, int page, int size) {
        return clientDAO.search(keyword, page, size);
    }

    public long countSearch(String keyword) {
        return clientDAO.countSearch(keyword);
    }

    public List<Client> getPaginated(int page, int size) {
        return clientDAO.findPaginated(page, size);
    }

    public long count() {
        return clientDAO.count();
    }

    private void validate(Client client) {
        if (client.getNom() == null || client.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (client.getPrenom() == null || client.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (client.getTelephone() == null || client.getTelephone().isBlank()) {
            throw new IllegalArgumentException("Le téléphone est obligatoire");
        }
        if (client.getEmail() != null && !client.getEmail().isBlank()
                && !EMAIL_PATTERN.matcher(client.getEmail()).matches()) {
            throw new IllegalArgumentException("L'email n'est pas valide");
        }
        if (client.getStatut() == null) {
            client.setStatut(ClientStatus.ACTIF);
        }
    }
}