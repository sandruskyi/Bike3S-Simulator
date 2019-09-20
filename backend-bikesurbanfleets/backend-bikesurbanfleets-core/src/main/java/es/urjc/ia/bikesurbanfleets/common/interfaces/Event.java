package es.urjc.ia.bikesurbanfleets.common.interfaces;

import java.util.List;
/**
 * This interface provides the common behaviour of events.
 * Also, it provides default implementations for some methods.
 * @author IAgroup
 *
 */
public interface Event {
    
    public enum EVENT_TYPE { USER_EVENT, MANAGER_EVENT}
    
    public enum RESULT_TYPE {
        FAILED_BIKE_RENTAL, SUCCESSFUL_BIKE_RENTAL, FAILED_BIKE_RETURN, SUCCESSFUL_BIKE_RETURN, FAILED_BIKE_RESERVATION, SUCCESSFUL_BIKE_RESERVATION,
        FAILED_SLOT_RESERVATION, SUCCESSFUL_SLOT_RESERVATION, SUCCESS, FAIL,
        EXIT_AFTER_APPEARING, EXIT_AFTER_FAILED_BIKE_RESERVATION, EXIT_AFTER_FAILED_BIKE_RENTAL, EXIT_AFTER_RESERVATION_TIMEOUT,
        EXIT_AFTER_REACHING_DESTINATION
    }
    
    public enum EXIT_REASON {
        EXIT_AFTER_APPEARING, EXIT_AFTER_FAILED_BIKE_RESERVATION, EXIT_AFTER_FAILED_BIKE_RENTAL, EXIT_AFTER_RESERVATION_TIMEOUT,
        EXIT_AFTER_REACHING_DESTINATION
    }
   
    /**
     * @return the time instant when the event will ocurr.
     */
    int getInstant();
    
    EVENT_TYPE getEventType();

    /**
     * @return a list with all the entities that are updated (but not created) in the event.
     */
    List<Entity> getInvolvedEntities();
    List<Entity> getOldEntities();
    List<Entity> getNewEntities();
    RESULT_TYPE getResult();
    void setResult(RESULT_TYPE result);
    
    /**
     * @return a string with the event information.
     */
    default String print() {
        StringBuilder sb = new StringBuilder()
                .append("Instant: ").append(getInstant()).append('\n')
                .append("Event: ").append(getClass().getSimpleName()).append('\n');
        sb.append("Result: ").append(getResult()).append('\n');

        sb.append("New entities:\n");
        List<Entity> elist=getNewEntities();
        if (elist!=null) {
            for (Entity entity : elist) {
                sb.append(entity).append('\n');
            }
        }
        sb.append("Involved entities:\n");
        elist=getInvolvedEntities();
         if (elist!=null) {
            for (Entity entity : elist) {
                sb.append(entity).append('\n');
            }
        }
        sb.append("Old entities:\n");
        elist=getOldEntities();
        if (elist!=null) {
            for (Entity entity : elist) {
                sb.append(entity).append('\n');
            }
        }

        return sb.toString();
    }
  
}