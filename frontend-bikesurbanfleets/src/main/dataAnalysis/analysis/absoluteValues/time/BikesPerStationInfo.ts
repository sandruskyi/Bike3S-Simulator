import { Observer } from '../../../../ObserverPattern';
import { Station, Reservation } from '../../../../../systemDataTypes/Entities';
import { TimeEntry, Event } from '../../../../../systemDataTypes/SystemInternalData';
import { Info } from "../Info";

interface BikesPerTime {
  time: number;
  availableBikes: number;
}

export class StationBikesPerTimeList {
    private bikesPerTimeList: Array<BikesPerTime>;
 
  public constructor(bikes: number) {
    this.bikesPerTimeList = new Array();
    let value: BikesPerTime = {time: 0, availableBikes: bikes};
    this.bikesPerTimeList.push(value);
  }
  
  public addBike(time: number): void {
    let lastPos: number = this.bikesPerTimeList.length-1;
    let bikes: number = this.bikesPerTimeList[lastPos].availableBikes; 
    let value: BikesPerTime = {time: time, availableBikes: ++bikes};
    this.bikesPerTimeList.push(value);
  }
  
  public substractBike(time: number): void {
    let lastPos: number = this.bikesPerTimeList.length-1;
    let bikes: number = this.bikesPerTimeList[lastPos].availableBikes; 
    let value: BikesPerTime = {time: time, availableBikes: --bikes};
    this.bikesPerTimeList.push(value);
  }
}

export class BikesPerStationInfo implements Info, Observer {
  private stations: Map<number, StationBikesPerTimeList>;
  private reservations: Array<Reservation>;

    public constructor(reservations: Array<Reservation>) {
        this.reservations = reservations;
        this.stations = new Map();
    }
  
    public async init(systemStations: Array<Station>) {
        for(let station of systemStations) {
            let value: StationBikesPerTimeList = new StationBikesPerTimeList(this.obtainInitAvailableBikesOf(station));
            this.stations.set(station.id, value);
        }
    }
  
  public update(timeEntry: TimeEntry): void {
    let instant: number = timeEntry.time;
    let events: Array<Event> = timeEntry.events;
      
    let lastPos, reservationId: number;
    let reservation: Reservation;
    let station: Station;
      
    for(let event of events) {
      let eventStations: Array<Station> = event.changes.stations;
        
      switch(event.name) {
        case "EventUserAppears": {
          if (eventStations !== undefined) {
            // If there are several bike reservations, only the last can be a ctive
            station = eventStations[0];
            lastPos = station.reservations.id.length-1;
            reservationId = station.reservations.id[lastPos];
            reservation = this.getReservation(reservationId);
               
            if (reservation.state === "ACTIVE") {  // and, of course, reservationtype = BIKE
                // Decreasing available bikes at the time the UserAppears event's happened 
                this.stations.get(station.id).substractBike(instant);
            }
          }
          break;
        }
          
        case "EventUserArrivesAtStationToRentBikeWithoutReservation": {
            if (eventStations.length > 0) {
                station = eventStations[0];
        //  TODO: make a method?
            
                // If bike ids have been registered, a change has occurred (there's 1 bike less)
                if (station.bikes !== undefined) {  // rental + mayvbe, slot reservations
                    this.stations.get(station.id).substractBike(instant);
                }
                else { // (station.reservations !== undefined) -> bike reservations (NOT rental)
                    // Getting the last changed station registered, wihich can conatin an active bike reservation  
                    station = eventStations[eventStations.length-1];
                    lastPos = station.reservations.id.length-1;
                    reservationId = station.reservations.id[lastPos];
                    reservation = this.getReservation(reservationId);
                
                    if (reservation.state === "ACTIVE") {  // and, of course, reservation.type === "BIKE"  
                        // Decreasing available bikes at the time the UserAppears event''s happened  
                        this.stations.get(station.id).substractBike(instant);
                    }
                }
            }
            break;
        }
          
        case "EventBikeReservationTimeout": {
            let historyReservations: Array<Reservation> = event.changes.reservations;
            reservation = this.getReservation(historyReservations[0].id);
            // As reservation state is expired, the iinvolved station has one more available bike
            let stationId: number = reservation.station.id;
            this.stations.get(stationId).addBike(instant);
            break;
        }
          
        case "EventUserArrivesAtStationToReturnBikeWithReservation": {
            // only a station can have registered changes (the one the user's reached)
            station = eventStations[0];
            this.stations.get(station.id).addBike(instant);
            break;
        }
          
        case "EventUserArrivesAtStationToReturnBikeWithoutReservatioon": {
            if (eventStatiosn.length > 0) {
                station = eventStations[0];
                if (station.bikes !== undefined) {
                    this.stations.get(station.id).addBike(instant);
                }
            }
            break;
        }
    }
  }
    
  private obtainInitAvailableBikesOf(station: Station): number {
    let counter: number = 0;
    station.bikes.id.forEach( (bikeId) => {
        if (bikeId !== null) { 
            counter++;
        }
    });
    return counter;
  }
  
  // TODO: should it return undefined if id doesn't exist?
  private getReservation(id: number): Reservation {
    for(let reservation of this.reservations) {
      if (reservation.id === id) {
        return reservation;
      }
    }
  }

}
