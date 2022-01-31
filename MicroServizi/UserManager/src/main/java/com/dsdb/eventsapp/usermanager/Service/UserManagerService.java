package com.dsdb.eventsapp.usermanager.Service;


import com.dsdb.eventsapp.usermanager.ModelData.User;
import com.dsdb.eventsapp.usermanager.ModelData.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.Random;

@Service
@Transactional
public class UserManagerService {
    @Autowired
    UserRepository userRepository;

    /** Funzione interna che mi genera un codice da restituire all'utente per un secondo controllo**/

    private String generazioneCodice () {
        Random valore = new Random(LocalTime.now().toNanoOfDay());
        String codice = String.format("%03d", valore.nextInt(1000));
        while (userRepository.getUserByCodice(codice) != null) {
            codice = String.format("%03d", valore.nextInt(1000));
            return codice;
        }

        return codice;
    }

    /**
     * FUNZIONE che inserisce l'utente nel database richiede in ingresso un'utente
     *
     * **/

    public String insertUser(User u)
    {
        /**
         * Controllo che l'utente l'email o l'username non siano già presenti nel database
         * **/
        if (userRepository.getUserByUserNameOrEmail(u.getUserName(),u.getEmail()) != null) {
            //Se email o username  sono presenti nel database non posso crearlo poichè non rispetto i vincoli dell'entity
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username o Email già Utilizzati..");
        }else if (userRepository.getUserByUserNameAndEmail(u.getUserName(),u.getEmail())!=null){
            //se email e UserName sono già presenti vuol dire che l'utente ha già un' account
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Utente già registrato..");
        } else {
            //altrimenti crea l'utente e il codice da restituirgli
            String codice = generazioneCodice();

            userRepository.save(u);
            u.setNprenotazioniFatte(0);
            u.setCodice(codice);
            return "Utente Inserito Con Successo!  Conservare il seguente codice:  " + codice;
        }

    }

    /**
     * FUNZIONE che elimina l'utente nel database richiede in userName e codice personale
     *
     * **/
     public String deleteUser(String userName, String codice){
        User utenteCercato = userRepository.getUserByUserNameAndCodice(userName,codice);
        if (utenteCercato != null){
            //Ha trovato l'utente posso cancellarlo



            RestTemplate restTemplate = new RestTemplate();
            //            String urlDeleteReservAssociate = "http://reservmanager:8080/reservsapi/deleteReservByIdUtente/" + utenteCercato.getIdUser();
            String urlDeleteReservAssociate = "http://eventsapp-reservmanager-service.default.svc.cluster.local:7777/reservsapi/deleteReservByIdUtente/" + utenteCercato.getIdUser();
            String risposta = restTemplate.exchange(urlDeleteReservAssociate, HttpMethod.DELETE, null, String.class).getBody();
            //return risposta;



            userRepository.delete(utenteCercato);
            return "Utente Cancellato Correttamente. " + risposta;
        }else {
            //altrimenti genero eccezione
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non presente o dati Inseriti errati..");
        }
    }

    /**
     * FUNZIONE che aggiorna il codice dell'utente nel database richiede in userName e password
     *
     * **/
    public String updateCodiceUser(String userName, String password){


        User utenteCercato = userRepository.getUserByUserNameAndPassword(userName,password);

        if (utenteCercato != null){
            String codice = generazioneCodice();
            utenteCercato.setCodice(codice);
            return "Codice rigenerato, conservare codice: " + codice;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Errore inserimento credenziali o Utente non Presente..");
        }


    }

    /**
     * FUNZIONE che aggiorna la password dell'utente nel database richiede in userName e codice personale e la nuova password
     *
     * **/

    public String updatePasswordUser (String userName, String codice, String newPassword){
        User utenteCercato = userRepository.getUserByUserNameAndCodice(userName,codice);

        if (utenteCercato != null){
            utenteCercato.setPassword(newPassword);
            return "Password Aggiornata correttamente";
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Errore inserimento valori o Utente non Presente.");
        }

    }


    /**
     * FUNZIONE che ritorna tutti gli utenti registrati nel database
     *
     * **/
    public Iterable<User> getAllUsers() { return userRepository.findAll(); }


    /**
     * FUNZIONE che permette di verificare che esista un utente con in determinato id
     *
     * **/

    public String getUserId(String userName, String password){
        User utenteCercato = userRepository.getUserByUserNameAndPassword(userName,password);
        if (utenteCercato != null) {
            return utenteCercato.getIdUser().toString();
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non Presente.");

        }
    }

    //restituisce l'id per l'utente a cui di aggiornano le prenotazioni fatte

    public String addNprenotazioni(String userName, String password, String N) {
        User utenteCercato = userRepository.getUserByUserNameAndPassword(userName, password);
        if (utenteCercato != null) {
            utenteCercato.setNprenotazioniFatte(utenteCercato.getNprenotazioniFatte() + Integer.parseInt(N));
            return utenteCercato.getIdUser().toString();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non Presente.");

        }
    }

    public String getEmail (Integer idUser){
        User utenteCercato = userRepository.getUserByIdUser(idUser);
        if (utenteCercato != null) {
            return utenteCercato.getEmail();
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non Presente.");

        }
    }

}
