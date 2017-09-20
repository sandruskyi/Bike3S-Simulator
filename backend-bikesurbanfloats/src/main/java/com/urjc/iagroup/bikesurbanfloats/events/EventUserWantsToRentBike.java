package com.urjc.iagroup.bikesurbanfloats.events;

import java.util.List;
import java.util.ArrayList;
import com.urjc.iagroup.bikesurbanfloats.entities.*;

public class EventUserWantsToRentBike extends EventUser {
	
	public EventUserWantsToRentBike(int instant, Person user, Station station) {
		super(instant, user, station);
	}


	public List<Event> execute() {
		List<Event> events = new ArrayList<Event>();
		events.add(new EventUserArrivesAtStationToRentBike(getInstant()+getUser().timeTo(getStation().getPosition()), getUser(), getStation()));
		return events;
	}
}