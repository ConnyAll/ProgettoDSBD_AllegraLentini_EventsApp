package com.dsdb.eventmanager.eventmanager.Service;

import com.dsdb.eventmanager.eventmanager.ModelData.Event;
import com.dsdb.eventmanager.eventmanager.ModelData.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@Service
@Transactional
public class EventManagerService {

    @Autowired
    EventRepository eventRepository;

    /**
     * FUNZIONE che permette di inserire un evento
     *
     * **/

    public String insertEvent(Event ev)
    {
        //controllo che l'evento sia già presente
        Event eventoCercato = eventRepository.getEventByCodice(ev.getCodice());
        if (eventoCercato == null) {
            //se l'evento non è presento controllo che i dati per i posti inseriti siano corretti
            if( ev.getPostiDisp()<= ev.getPostiMax() && ev.getPostiDisp()>0) {
                eventRepository.save(ev);
                return "Evento Inserito Con Successo! ";
            }else return "Errore di inserimento nei seguenti parametri PostiMax o PostiDis.";
        }else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codice dell'evento già presente.");
        }
    }

    /**
     * FUNZIONE che di cancellare un evento
     *
     * **/

    public String deleteEvent(String codice){
        //controllo che l'evento esista o meno
        Event eventoCercato = eventRepository.getEventByCodice(codice);
        if (eventoCercato != null){


            RestTemplate restTemplate = new RestTemplate();
            String urlDeleteReservAssociate = "http://eventsapp-reservmanager-service.default.svc.cluster.local:7777/reservsapi/deleteReservByIdEvent/" + eventoCercato.getIdEvent();

            //String urlDeleteReservAssociate = "http://reservmanager:8080/reservsapi/deleteReservByIdEvent/" + eventoCercato.getIdEvent();
            String risposta = restTemplate.exchange(urlDeleteReservAssociate, HttpMethod.DELETE, null, String.class).getBody();
            //return risposta;

            eventRepository.delete(eventoCercato);
            return "Evento Cancellato Correttamente. "+ risposta;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento non presente o codice Errato.");
        }
    }


    /**
     * FUNZIONE che permette di aggiornare il numero massimo di posti che può ospitare l'evento
     *
     * **/

    public String updatePostiMax(Integer Nposti,  String codice){
        Event eventoCercato = eventRepository.getEventByCodice(codice);
        if (eventoCercato != null){
            if(eventoCercato.getPostiDisp()<=Nposti) {
                eventoCercato.setPostiMax(Nposti);
                return "Evento Aggiornato Correttamente..";
            }else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il numero di posti Massimo per un evento non può essere minore dei Posti disponibili");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento non presente o codice Errato.");
        }

    }


    /**
     * FUNZIONE che permette di aggiornare il numero di posti disponibili, questi cambieranno ad ogni prenotazione effettuata
     *
     * **/
     public String updatePostiDisp(Integer idEvent,  Integer Nposti){
        Event eventoCercato = eventRepository.getEventByIdEvent(idEvent);
        if (eventoCercato != null){
            Integer postiPresenti = Integer.parseInt(getNpostiDisp(idEvent));
            if (postiPresenti>=Nposti) {
                Integer postiAttuali = postiPresenti - Nposti;
                eventoCercato.setPostiDisp(postiAttuali);
                return "Posti Aggiornati Correttamente.";
            }else  {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il numero di posti richiesto è maggiore dei posti presenti impossibile completare la Prentotazione.");

            }
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento non presente.");
        }

    }



    /**
     * FUNZIONE che permette di mostrare tutti gli eventi
     *
     * **/

    public Iterable<Event> getAllEvents() { return eventRepository.findAll(); }


    /**
     * FUNZIONE che permette di prendere il numero di posti disponibili per un evento
     *
     * **/
    public String getNpostiDisp (Integer idEvent){
        Event eventoCercato = eventRepository.getEventByIdEvent(idEvent);
       if (eventoCercato !=null) {
           return eventoCercato.getPostiDisp().toString();
       }else {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento non presente.");
       }

    }



}
