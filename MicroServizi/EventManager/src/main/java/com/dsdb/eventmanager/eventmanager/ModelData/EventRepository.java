package com.dsdb.eventmanager.eventmanager.ModelData;

import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Integer> {
    public Event getEventByCodice (String codice);
    public Event getEventByIdEvent(Integer idEvent);
}
