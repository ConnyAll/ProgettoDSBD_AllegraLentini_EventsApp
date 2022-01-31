package com.dsdb.eventmanager.eventmanager.Adapters;


import com.dsdb.eventmanager.eventmanager.ModelData.Event;
import com.dsdb.eventmanager.eventmanager.Service.EventManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequestMapping(path="/eventsapi")
public class EventManagerAdapter {

    @Autowired
    EventManagerService eventsManagerService;

    /** INSERT EVENT **/

    /** METODO POST **/
    /**
     *  URL --> http://eventmanager:5555/eventsapi/insertEvent
     *      --> richiede un Evento
     *     Es body:
     *     {
     *       "codice" : "AAA",
     *       "nomeEvento" : "Concerto2022",
     *       "descrizione" : "MessinaCampoSportivo",
     *       "postiMax" : "100",
     *       "postiDisp" : "100",
     *       "prezzoBiglietto" : "20"
     *      }

     --> restituisce una stringa di successo  o insuccesso dell'inserimento
     * **/


    @PostMapping(path="/insertEvent")
    public @ResponseBody
    String insertEvent (@RequestBody Event ev)
    {
        try {
            return eventsManagerService.insertEvent(ev);
            //se va male catturo l'eccezione
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }
    }

    /*** DELETE EVENT ***/

    /** METODO DELETE **/
    /**
     *  --> Codice Sono conosciuti da l'amministratore che vuole inserire l'evento
     *       URL --> http://eventmanager:5555/eventsapi/deleteEvent/{codice}
     *
     * **/
    @DeleteMapping(path = "/deleteEvent/{codice}")
    public  @ResponseBody
    String deleteEvent(@PathVariable String codice){

        try {
            return eventsManagerService.deleteEvent(codice);

        }catch(ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }

    }

    /** METODO GET **/
    /**
     *   Serve a mostrare tutti gli eventi presenti
     *   URL --> http://eventmanager:5555/eventsapi/getAllEvents
     *  ritorna una lista di eventi se non vi sono la lista è vuota
     ** **/

    //http://eventmanager:5555/eventsapi/getAllEvents
    @GetMapping(path="/getAllEvents")
    public @ResponseBody Iterable<Event> getAllEvents() { return eventsManagerService.getAllEvents(); }


    /** UPDATE EVENT **/
    /** METODO PUT **/
    /**
     *   Serve ad aggiornare il numero di posti massimo dell'evento
     *   URL --> http://eventmanager:5555/eventsapi/updatePostiDisp/{idEvent}/{Nposti}
     *
     * **/


    @PutMapping(path="/updatePostiDisp/{idEvent}/{Nposti}")
    public @ResponseBody
    String updatePostiMax(@PathVariable Integer idEvent, @PathVariable Integer Nposti ){
        try{
           return eventsManagerService.updatePostiDisp(idEvent, Nposti);
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }

    }



    /** UPDATE EVENT **/
    /** METODO PUT **/
    /**
     *   Serve ad aggiornare il codice nella casualità che l'utente lo perda
     *   Per ottenerlo nuovamente devo inviare le credenzioli
     *   URL --> http://eventmanager:5555/eventsapi/updatePostiMax/{Nposti}
     *           http://eventmanager:5555/eventsapi/updatePostiMax/300
     *   Es body:
     *      AAA
     *
     * **/


    @PutMapping(path="/updatePostiMax/{Nposti}")
    public @ResponseBody
    String updatePostiMax(@PathVariable Integer Nposti, @RequestBody String codice ){
        try{
            return eventsManagerService.updatePostiMax(Nposti, codice);
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }

    }


    /**GET POSTI DISPONIBILI**/
    /**
     *  Mi permette di conoscere il numero di posti disponibili per un determinato evento
     *  URL -->  http://eventmanager:5555/eventsapi/getNpostiDisp/{idEvent}
     *
     * */

    //UTILE PER I CONTROLLI DEI POSTI DISPONIBILI IN FASE DI creazione di una prenotazione
    //http://eventmanager:5555/eventsapi/getNpostiDisp/{idEvent}
    @GetMapping(path="/getNpostiDisp/{idEvent}")
    public @ResponseBody String getNpostiDisp(@PathVariable Integer idEvent) {

        try {
            return eventsManagerService.getNpostiDisp(idEvent);
        }catch(ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }


    }


}
