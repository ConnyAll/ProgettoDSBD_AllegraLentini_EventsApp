package com.dsdb.eventsapp.reservmanager.Adapters;


import com.dsdb.eventsapp.reservmanager.ModelData.Reserv;
import com.dsdb.eventsapp.reservmanager.Service.ReservManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(path="/reservsapi")
public class ReservManagerAdapter {

    /**FUNZIONI PER CALCOLARE LE METRICHE **/

    /**
     * tale funzione Duration mi permette di andare a calcolare il tempo che intercorre tra la ricezione della
     * richiesta e il tempo di arrivo della risposta**/
    public void DurationMetrica(String filePath, long metrica)
            throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("DD/MM/YYYY HH:mm:ss");
        String timeStamp = date.format(new Date());

        FileWriter fw = new FileWriter(filePath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(timeStamp + "," + metrica);
        bw.newLine();
        bw.close();
    }

    /**
     * Tale funzione mi calcolala la frequenza delle richieste errate **/
    static Integer countErrorRequest=0;
    static long startTimeE = 0;
    static long endTimeE = 0;
    public void RateofErrorRequestMetrica (String filePath)
            throws IOException {
        countErrorRequest++;
        double tempoTrascorso = 0;

        if (countErrorRequest == 1) {
            startTimeE = System.currentTimeMillis();
        }
        if (countErrorRequest == 5) {
            endTimeE = System.currentTimeMillis();
            tempoTrascorso = Double.parseDouble(endTimeE - startTimeE + "") / 1000;
            SimpleDateFormat date = new SimpleDateFormat("DD/MM/YYYY HH:mm:ss");
            String timeStamp = date.format(new Date());

            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(timeStamp + "," + countErrorRequest / tempoTrascorso);
            bw.newLine();
            bw.close();
            countErrorRequest = 0;
        }
    }


    /** Mi calcola la frequenza delle richieste corrette**/
    static Integer countRequestTot=0;
    static long startTimeR = 0;
    static long endTimeR = 0;
    public void RateofRequestMetrics(String filePath)
            throws IOException {
        countRequestTot++;
        double tempoTrascorso = 0.0;

        if (countRequestTot == 1) {
            startTimeR = System.currentTimeMillis();
        }
        if (countRequestTot == 5) {
            endTimeR = System.currentTimeMillis();
            tempoTrascorso = Double.parseDouble(endTimeR - startTimeR + "") / 1000;
            SimpleDateFormat date = new SimpleDateFormat("DD/MM/YYYY HH:mm:ss");
            String timeStamp = date.format(new Date());

            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(timeStamp + "," + countRequestTot / tempoTrascorso);
            bw.newLine();
            bw.close();
            countRequestTot = 0;
        }


    }



    @Autowired
    ReservManagerService reservManagerService;

    /** GET RESERV**/

    /** METODO GET **/
    /**
     *      mostra tutte le prenotazioni avvenute
     * **/

    //http://reservmanager:7777/reservsapi/getReserv
    @GetMapping(path="/getReserv")
    public @ResponseBody  Iterable <Reserv> getReserv(){   return reservManagerService.getReserv();}


    /** GET RESERV BY ID UTENTE**/

    /** METODO GET **/
    /**
     *      mostra tutte le prenotazioni avvenute da parte di un determinato utente
     * **/

    //http://reservmanager:7777/reservsapi/getReservByIdUtente/1
    @GetMapping(path="/getReservByIdUtente/{IdUtente}")
    public @ResponseBody Iterable<Reserv> getReservByIdUtente(@PathVariable Integer IdUtente){   return reservManagerService.getReservByIdUtente(IdUtente);}

    /** CREATE RESERV**/

    /** METODO POST **/
    /**
     *      crea una prenotazione per un determinato utente i parametri  idUser idEvent e Nbiglietti vengono passati in chiaro
     *      restituisce una stringa di successo  o insuccesso dell'inserimento
     * **/


    @PostMapping(path="/createReserv/{Nbiglietti}/{idEvent}")
    public @ResponseBody
    String createReserv(@RequestBody Map<String, String> user, @PathVariable Integer Nbiglietti, @PathVariable Integer idEvent)
    {
        try {
            long start = System.nanoTime(); //istante ti arrivo della richiesta
            String Response = reservManagerService.createReserv(user.get("userName"), user.get("password"),Nbiglietti,idEvent);
            long end = System.nanoTime(); //istante di arrivo della risposta

            long tempoTrascorso = end - start;


            try {
                //salvo le metriche in un file interno al mio micro servizio che poi prendo tramite un comando da shell
                DurationMetrica("/app/DurationMetrica.txt", tempoTrascorso);
                RateofRequestMetrics("/app/RateofRequestMetrics.txt");
            } catch (IOException e1) {
                e1.getMessage();
            }

            return Response;
            //se va male catturo l'eccezione
        }catch (ResponseStatusException e){

            try {
                RateofErrorRequestMetrica("/app/RateofErrorRequestMetrica.txt");
            } catch (IOException ex2) {
                ex2.getMessage();
            }

            return e.getStatus() + " " + e.getReason();
        }
    }


    /** DELETE RESERV**/

    /** METODO DELETE **/
    /**
     *      cancella una determinata prenotazione
     *    restituisce una stringa di successo  o insuccesso dell'inserimento
     * **/

    @DeleteMapping(path="/deleteReserv/{idReserv}")
    public @ResponseBody
    String deleteReserv( @PathVariable Integer idReserv)
    {
            try {
                return reservManagerService.deleteReserv(idReserv);
                //se va male catturo l'eccezione
            }catch (ResponseStatusException e){
                return e.getStatus() + " " + e.getReason();
            }
    }


    /** DELETE RESERV**/

    /** METODO DELETE **/
    /**
     *      cancella una prenotazione per un determinato evento
     *      funzione che viene chiamata dalla delete in EventManager
     * **/

    //http://reservmanager:7777/reservsapi/deleteReservByIdEvent/1
    @DeleteMapping(path="/deleteReservByIdEvent/{idEvent}")
    public @ResponseBody
    String deleteReservByIdEvent(@PathVariable Integer idEvent)
    {
        try {
            return reservManagerService.deleteReservByIdEvent(idEvent);
            //se va male catturo l'eccezione
        }catch (ResponseStatusException e){
        return e.getStatus() + " " + e.getReason();
        } catch (IOException ex) {
            //return ex.printStackTrace();
            //return ex.printStackTrace(System.out.format());
           return ex.getMessage();
        }


    }

    /** DELETE RESERV**/

    /** METODO DELETE **/
    /**
     *      cancella una prenotazione per un determinato utente
     *      funzione che viene chiamata dalla delete in UserManager
     * **/

    @DeleteMapping(path="/deleteReservByIdUtente/{idUtente}")
    public @ResponseBody
    String deleteReservByIdUtente(@PathVariable Integer idUtente)
    {
        try {
            return reservManagerService.deleteReservByIdUtente(idUtente);
            //se va male catturo l'eccezione
        }catch (ResponseStatusException e){
            return e.getStatus() + " " + e.getReason();
        }
    }





}
