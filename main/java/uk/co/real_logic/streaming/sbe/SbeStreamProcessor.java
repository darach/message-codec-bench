package uk.co.real_logic.streaming.sbe;

import uk.co.real_logic.sbe.examples.BooleanType;
import uk.co.real_logic.sbe.examples.Car;
import uk.co.real_logic.sbe.examples.Engine;
import uk.co.real_logic.sbe.examples.Model;
import uk.co.real_logic.sbe.generation.java.DirectBuffer;
import uk.co.real_logic.streaming.ProcessStreamOfUpdates;
import uk.co.real_logic.streaming.StreamProcessor;
import uk.co.real_logic.streaming.StreamQuery;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SbeStreamProcessor implements StreamProcessor
{
    private static final byte[] VEHICLE_CODE = {'a', 'b', 'c', 'd', 'e', 'f'};
    private static final byte[] MANUFACTURER_CODE = {'1', '2', '3'};
    private static final byte[] MAKE;
    private static final byte[] MODEL;

    private static final Car CAR = new Car();

    static
    {
        try
        {
            MAKE = "Honda".getBytes("UTF-8");
            MODEL = "Civic VTi".getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public ByteBuffer setupBenchmarkData(final ByteBuffer underlyingBuffer)
    {
        final DirectBuffer directBuffer = new DirectBuffer(underlyingBuffer);
        int size = encode(CAR, directBuffer, 4);
        directBuffer.putInt(0, size, ByteOrder.nativeOrder());
        int offset = size + 4;

        while (offset < directBuffer.capacity() - (size + 4))
        {
            size = encode(CAR, directBuffer, offset + 4);
            directBuffer.putInt(offset, size, ByteOrder.nativeOrder());
            offset += size + 4;
        }

        underlyingBuffer.limit(offset);

        return underlyingBuffer;
    }

    public void process(final ByteBuffer underlyingBuffer, final StreamQuery query)
    {
        DirectBuffer directBuffer = new DirectBuffer(underlyingBuffer);
        int size = 0;
        int offset = 0;

        do
        {
            size = directBuffer.getInt(offset, ByteOrder.nativeOrder());
            processNextCar(CAR, directBuffer, offset + 4, CAR.blockLength(), CAR.templateVersion(), query);
            offset += size + 4;
        }
        while (offset < underlyingBuffer.limit());
    }

    private static int encode(final Car car, final DirectBuffer directBuffer, final int bufferOffset)
    {
        final int srcOffset = 0;

        car.resetForEncode(directBuffer, bufferOffset)
           .serialNumber(1234)
           .modelYear(2666)
           .available(BooleanType.TRUE)
           .code(Model.A)
           .putVehicleCode(VEHICLE_CODE, srcOffset);

        for (int i = 0, size = car.someNumbersLength(); i < size; i++)
        {
            car.someNumbers(i, i);
        }

        car.extras()
           .cruiseControl(true)
           .sportsPack(true)
           .sunRoof(false);

        car.engine()
           .capacity(2000)
           .numCylinders((short)4)
           .putManufacturerCode(MANUFACTURER_CODE, srcOffset);

        car.fuelFiguresCount(3)
           .next().speed(30).mpg(35.9f)
           .next().speed(55).mpg(49.0f)
           .next().speed(75).mpg(40.0f);

        final Car.PerformanceFigures performanceFigures = car.performanceFiguresCount(2);
        performanceFigures.next().octaneRating((short)95)
                                 .accelerationCount(3)
                                 .next().mph(30).seconds(4.0f)
                                 .next().mph(60).seconds(7.5f).next().mph(100).seconds(12.2f);
        performanceFigures.next().octaneRating((short)99)
                                 .accelerationCount(3)
                                 .next().mph(30).seconds(3.8f)
                                 .next().mph(60).seconds(7.1f)
                                 .next().mph(100).seconds(11.8f);

        car.putMake(MAKE, srcOffset, MAKE.length);
        car.putModel(MODEL, srcOffset, MODEL.length);

        return car.size();
    }

    private static void processNextCar(final Car car,
                                       final DirectBuffer directBuffer,
                                       final int bufferOffset,
                                       final int actingBlockLength,
                                       final int actingVersion,
                                       StreamQuery query)
    {
        car.resetForDecode(directBuffer, bufferOffset, actingBlockLength, actingVersion);
        final int modelYear = car.modelYear();
        final Engine engine = car.engine();
        final int engineCapacity = engine.capacity();
        query.car(engineCapacity, modelYear);
    }

    /**
     * For sanity testing
     */
    public static void main(String[] args)
    {
        final SbeStreamProcessor ssp = new SbeStreamProcessor();
        final ByteBuffer preparedBuffer = ssp.setupBenchmarkData(ByteBuffer.allocateDirect(32 * 1024 * 1024));
        final ProcessStreamOfUpdates.Query query = new ProcessStreamOfUpdates.Query();

        ssp.process(preparedBuffer, query);
        System.out.println(query.sum / query.count);
    }
}
