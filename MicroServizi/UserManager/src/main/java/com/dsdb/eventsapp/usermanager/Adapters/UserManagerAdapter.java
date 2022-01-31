package com.dsdb.eventsapp.usermanager.Adapters;

import com.dsdb.eventsapp.usermanager.ModelData.User;
import com.dsdb.eventsapp.usermanager.Service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path="/usersapi")
public class UserManagerAdapter {

    @Autowired
    UserManagerService userManagerService;

    /*** INSERT USER ***/

    /** METODO POST **/
    /**
     *  URL --> http://usermanager:4444/usersapi/insertUser
     *      --> richiede un Utente
     *     Es body:
                {
                    "userName" : "Conny",
                    "email" : "mia@",
                    "password" : "Carletto"
                }
        --> restituisce una stringa di successo con codice all'utente o insuccesso
     * **/

    //http://usermanager:4444/usersapi/insertUser
    @PostMapping(path="/insertUser")
    public @ResponseBody
    String insertUser(@RequestBody User u)
    {
        try {
            /**
             *
             * Entro nello UserService e chiamo la funzione per inserire l'utente
             * Può generare un'eccezione poichè i campi sono inseriti in modo sbagliato
             * Oppure non sono rispettaati i vincoli per Username o email che devono
             * essere unici
             *
             * **/
            return userManagerService.insertUser(u);

        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }
    }

    /*** DELETE USER ***/

    /** METODO DELETE **/
    /**
     *  --> Username e codice Sono conosciuti dall'utente (il codicee viene restituito dall'insertUser)
     *      tali parametri possono viaggiare in chiaro
     *      L'inserimento del codice è dovuto ad un ulteriore Controllo al posto della Password che non
     *      deve viaggiare in chiaro
     *
     *  URL --> http://usermanager:4444/deleteUser/{userName}/{codice}
     *
     * **/

    @DeleteMapping(path = "/deleteUser/{userName}/{codice}")
    public  @ResponseBody
    String deleteUser(@PathVariable String userName, @PathVariable String codice){

        /**
         *
         * Entro nello UserService e chiamo la funzione per la cancellzione dell'utente
         * Può generare un'eccezione poichè i campi sono inseriti in modo sbagliato e quindi
         * l'utente non viene trovato
         *
         * **/


        try {
            return userManagerService.deleteUser(userName,codice);
        }catch(ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }

    }

    /*** UPDATE CODICE ***/

    //controllare
    /** METODO PUT **/
    /**
     *   Serve ad aggiornare il codice nella casualità che l'utente lo perda
     *   Per ottenerlo nuovamente devo inviare le credenzioli
     *   URL --> http://usermanager:4444/updateCodiceUser
     *
     *   Es body:
     *      {
     *          "userName" : "Conny",
     *          "password" : "Carletto"
     *       }
     *
     *
     * **/


    @PutMapping(path="/updateCodiceUser")
    public @ResponseBody
    String updateCodiceUser(@RequestBody Map<String,String> credenziali){
        try{

            /**
             *
             * Entro nello UserService e chiamo la funzione per aggiornare il codice dell'utente
             * Può generare un'eccezione poichè i campi sono inseriti in modo sbagliato e quindi
             * l'utente non viene trovato
             *
             * **/


            return userManagerService.updateCodiceUser(credenziali.get("userName"), credenziali.get("password"));
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }

    }

    /*** UPDATE USER ***/

    /** METODO PUT **/
    /**
     *   Serve ad aggiornare la password per poterla effettuare devo inviare in chiaro
     *   lo userName e il codice, nel bosy invece metterò la nuova password
     *   *   URL --> http://usermanager:4444/updatePasswordUser/{userName}/{codice}
     *
     *   Es body:
     *      nuovaPassword
     *
     ** **/

    @PutMapping(path="/updatePasswordUser/{userName}/{codice}")
    public @ResponseBody
    String updatePasswordUser(@PathVariable String userName, @PathVariable String codice, @RequestBody String newPassword ){

        try{

            /**
             *
             * Entro nello UserService e chiamo la funzione per l'aggiornamento  dell'utente
             * Può generare un'eccezione poichè i campi sono inseriti in modo sbagliato e quindi
             * l'utente non viene trovato
             *
             * **/

            return userManagerService.updatePasswordUser(userName,codice,newPassword);
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }

    }

    /*** GET ALL USER ***/

    /** METODO GET **/
    /**
     *   Serve a mostrare tutti gli utenti presenti
     *
     *   URL --> http://usermanager:4444/usersapi/getAllUsers
     *
     *  ritorna una lista di utenti se non vi sono gli utenti la lista è vuota
     ** **/

    @GetMapping(path="/getAllUsers")
    public @ResponseBody Iterable<User> getAllUsers() { return userManagerService.getAllUsers(); }


    /**METODO POST*/
    /**
     * Metodo Post per prendere gli utentu
     *
     */
    //http://usermanager:4444/usersapi/getUserId
    @PostMapping(path="/getUserId")
    public @ResponseBody
    String getUserId(@RequestBody Map<String,String> user) {
        try{
        return userManagerService.getUserId(user.get("userName"), user.get("password"));
        }catch (ResponseStatusException e){
          return e.getStatus() + " " + e.getReason();
        }
    }

    //Comunicazioni per l'utente
    //http://eventsapp.dev.loc/usersapi/getEmail/1
    @GetMapping(path="/getEmail/{idUser}")
    public @ResponseBody
    String getEmail (@PathVariable Integer idUser){
        try{
            return userManagerService.getEmail(idUser);
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }


    }

    @PostMapping(path="/addNprenotazioni")
    public @ResponseBody
    String addNprenotazioni (@RequestBody Map<String,String> user){
        try {
            return userManagerService.addNprenotazioni(user.get("userName"), user.get("password"), user.get("NPrenotazioniFatte") );
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }
    }



}
