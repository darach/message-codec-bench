package uk.co.real_logic.streaming;

/**
 * The {@link StreamProcessor} should call this interface with the details of each car.
 * 
 * @author nitsanw
 *
 */
public interface StreamQuery {
    void car(int engineCapacity, int year);
}
