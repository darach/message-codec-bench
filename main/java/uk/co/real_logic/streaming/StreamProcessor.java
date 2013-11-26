package uk.co.real_logic.streaming;

import java.nio.ByteBuffer;

/**
 * The usecase for this benchmark is the processing of a stream of messages, all of the Car type. The stream
 * of cars is being processed to compute/filter based on the data fed into the query. Because the encoding is
 * not uniform each implementation should provide the prepare method that setup up the data to be used in the
 * benchmark.
 *
 * @author nitsanw
 */
public interface StreamProcessor
{
    /**
     * Fill up the buffer with as many cars as would fit. In future we may want to get the data from a file
     * with data is JSON/CSV format.
     *
     * @param buffy with no cars
     * @return buffy full of cars and with limit set appropriately
     */
    ByteBuffer setupBenchmarkData(ByteBuffer buffy);

    /**
     * @param buffy full of cars, the same buffy will be used again to call this method so keep the
     *              limit/position where it is
     * @param query feed each car into the query
     */
    void process(ByteBuffer buffy, StreamQuery query);
}
