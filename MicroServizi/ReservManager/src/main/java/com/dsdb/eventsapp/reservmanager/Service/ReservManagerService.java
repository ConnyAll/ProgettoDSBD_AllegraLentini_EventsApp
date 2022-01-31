package com.dsdb.eventsapp.reservmanager.Service;

import com.dsdb.eventsapp.reservmanager.ModelData.Reserv;
import com.dsdb.eventsapp.reservmanager.ModelData.ReservRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;


@Service
public class ReservManagerService {

    @Autowired
    ReservRepository reservRepository;

    /**
     * FUNZIONE che permette di vedere tutte le prenotazioni
     *
     * **/

    public Iterable<Reserv> getReserv() { return reservRepository.findAll(); }



    /**
     * FUNZIONE che permette di aggiornare i postidisponibili in caso di prenotazione o disdetta della prenotazione
     *
     * **/
    public String aggiornaPosti (Integer idEvent, Integer Nbiglietti) {
        RestTemplate restTemplate = new RestTemplate();
        //String urlControlloEventi = "http://eventsapp-eventmanager-service.default.svc.cluster.local:5555/eventsapi/updatePostiDisp/" + idEvent+ "/" + Nbiglietti;
        String urlControlloEventi = "http://eventsapp.dev.loc/eventsapi/updatePostiDisp/" + idEvent+ "/" + Nbiglietti;

        String risposta = restTemplate.exchange(urlControlloEventi, HttpMethod.PUT, null, String.class).getBody();
        return risposta;
    }

    /**
     * FUNZIONE che permette di aggiornare aggiornare le prenotazioni dell'utente in modo asincrono dalla riuscita della Prenotazione
     * inoltre controlla se esiste l'utente poichè mi restituisce l'id ìUser
     * **/

    public String AggiuntaNprenotazione(String userName, String password, Integer N){
        RestTemplate restTemplate = new RestTemplate();

        //String urlAggiuntaNprenotazione = "http://usermanager:8080/usersapi/addNprenotazioni";
        String urlAggiuntaNprenotazione = "http://eventsapp-usermanager-service.default.svc.cluster.local:4444/usersapi/addNprenotazioni";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> args = new HashMap<>();
        args.put("userName", userName);
        args.put("password", password);
        args.put("NPrenotazioniFatte", N.toString());
        HttpEntity<?> httpEntity = new HttpEntity<Object>(args, headers);
        return restTemplate.exchange(urlAggiuntaNprenotazione, HttpMethod.POST, httpEntity, String.class).getBody();


    }

    /**
     * FUNZIONE che nel caso in cui si verifichi un errore con la creazione della prenotazione ossia l'evento non esiste più o i posti disponibili sono minori rispetto a
     * quelli richiesti dall'utente mi effetta un'azione di compesazione sul contatore NprenotazioniFatte presente in user che all'inizio della creazione della prenotazione
     * è stato incrementato
     *
     * **/

    public void CompensazioneSaga (String userName,String password){
        Integer n=-1;
         Integer rispostaUserPrenotazioni = Integer.parseInt(AggiuntaNprenotazione (userName,password,n));
    }






    /**
     * FUNZIONE che permette di vedere se l'idEvent inserito sia valido
     *
     * **/
    public String ControlloEvento(Integer idEvent){
        RestTemplate restTemplate = new RestTemplate();
       // String urlControlloEventi = "http://eventmanager:8080/eventsapi/getNpostiDisp/" + idEvent;
        String urlControlloEventi = "http://eventsapp-eventmanager-service.default.svc.cluster.local:5555/eventsapi/getNpostiDisp/" + idEvent;
        String NpostiDisp = restTemplate.exchange(urlControlloEventi, HttpMethod.GET, null, String.class).getBody();
        return NpostiDisp;
    }




     /**
     * FUNZIONE che permette di Creare una Prenotazione invocata dallo UserManager
     *
     * **/


    public String createReserv( String userName, String password,  Integer Nbiglietti,  Integer idEvent) {



       Integer rispostaUserPrenotazioni ;
        try {
            Integer n=1;
            rispostaUserPrenotazioni = Integer.parseInt(AggiuntaNprenotazione (userName,password,n));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato, non si può effettuare la prenotazione.");
        }




        Integer NpostiDisp;
        try {
            NpostiDisp = Integer.parseInt(ControlloEvento(idEvent));
        } catch (Exception e) {
            CompensazioneSaga (userName,password);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento non trovato, non si può effettuare la prenotazione.");
        }

        if (NpostiDisp < Nbiglietti) {
            CompensazioneSaga (userName,password);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "I posti disponibili sono minori di quelli richiesti, non si può effettuare la prenotazione.");
        } else if (reservRepository.getReservByIdEventAndIdUser(idEvent, rispostaUserPrenotazioni) != null) {
            CompensazioneSaga (userName,password);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Utente hai già una prenotazione per questo evento.");
        } else {
            Reserv newReservation = new Reserv();
            newReservation.setIdUser(rispostaUserPrenotazioni);
            newReservation.setIdEvent(idEvent);
            newReservation.setNbiglietti(Nbiglietti);
            reservRepository.save(newReservation);
            //Devo aggiornare i posti nell'evento
            return "Prenotazione Inserita e " + aggiornaPosti(idEvent, Nbiglietti);

        }
    }

    /**
     * FUNZIONE che permette di cancellare una determinata Prenotazione di un utente per un determinato evento
     *
     * **/
    @Transactional
    public String deleteReserv(Integer idReserv) {

        Optional<Reserv> reservCercata;
        try {
            reservCercata = reservRepository.findById(idReserv);
            Integer PostiLiberati = (-(reservCercata.get().getNbiglietti()));
            reservRepository.deleteById(reservCercata.get().getIdPrenotazione());
            return "Prenotazione Cancellata e " + aggiornaPosti(reservCercata.get().getIdEvent(), PostiLiberati);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata.");
        }

    }


    /**
     * FUNZIONE che permette di prendere le prenotazioni per un determinato utente
     *
     * **/

    public Iterable<Reserv> getReservByIdUtente(Integer IdUtente) {
        return reservRepository.getReservsByIdUser(IdUtente);
    }

    /**
     * FUNZIONE che permette di cancellare le prenotazioni legate ad un evento
     *
     * **/

    @Transactional
    public String deleteReservByIdEvent (Integer idEvent) throws IOException {
       List<String> listaEmail =  new ArrayList<>();
        Iterable<Reserv>  reservs = reservRepository.getReservsByIdEvent(idEvent);
        if (reservs == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione Non Trovata.");

        } else {

              String memo = "\n**** Promemoria **** \n Avvertire i seguenti utenti della cancellazione dell'evento "+ idEvent +" per cui si erano prenotati \n";


            for (Reserv r : reservs) {
                RestTemplate restTemplate = new RestTemplate();
                //String urlControlloEventi = "http://usermanager:8080/usersapi/getEmail/" + r.getIdUser();
                String urlControlloEventi = "http://eventsapp-usermanager-service.default.svc.cluster.local:4444/usersapi/getEmail/" + r.getIdUser();

                String EmailUser = restTemplate.exchange(urlControlloEventi, HttpMethod.GET, null, String.class).getBody();

                listaEmail.add(EmailUser);

                }


                reservRepository.deleteAll(reservs);
                // return  LISTAid.toString();
                if (listaEmail.isEmpty()==true){
                    return "Evento Cancellato nessun utente si è prenotato";
                }
                return "Evento Cancellato quindi sono state eliminate anche le prenotazioni associate.\n " + memo + listaEmail.toString() ;

        }

    }

    /**
     * FUNZIONE che permette di cancellare le prenotazioni legate ad un utente
     *
     * **/


    @Transactional
    public String deleteReservByIdUtente(Integer idUtente){

        Iterable<Reserv>  reservs = reservRepository.getReservsByIdUser(idUtente);
        if (reservs == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata.");

        } else {
            reservRepository.deleteAll(reservs);
            return "Utente Cancellato quindi sono state eliminate anche le prenotazioni associate" ;

        }


    }


}
