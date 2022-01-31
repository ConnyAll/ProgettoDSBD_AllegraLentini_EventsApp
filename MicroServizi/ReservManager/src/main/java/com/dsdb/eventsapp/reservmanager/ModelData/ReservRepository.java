package com.dsdb.eventsapp.reservmanager.ModelData;

import org.springframework.data.repository.CrudRepository;

public interface ReservRepository extends CrudRepository<Reserv, Integer> {
    public Reserv getReservByIdEventAndIdUser(Integer idEvent, Integer idUser);
    public Iterable<Reserv> getReservsByIdUser (Integer idUtente);
    public Iterable<Reserv> getReservsByIdEvent(Integer idEvent);

}
