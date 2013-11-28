/*
 * Copyright 2013 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.real_logic.message_code_bench.sbe;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import uk.co.real_logic.sbe.examples.*;
import uk.co.real_logic.sbe.generation.java.DirectBuffer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class CarBenchmark
{
    private static final byte[] MAKE;
    private static final byte[] MODEL;
    private static final byte[] ENG_MAN_CODE;
    private static final byte[] VEHICLE_CODE;

    static
    {
        try
        {
            final Car car = new Car();

            MAKE = "MAKE".getBytes(car.makeCharacterEncoding());
            MODEL = "MODEL".getBytes(car.modelCharacterEncoding());
            ENG_MAN_CODE = "abc".getBytes(car.engine().manufacturerCodeCharacterEncoding());
            VEHICLE_CODE = "abcdef".getBytes(car.vehicleCodeCharacterEncoding());
        }
        catch (final UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @State(Scope.Benchmark)
    public static class MyState
    {
        final int bufferIndex = 0;
        final Car car = new Car();
        final MessageHeader messageHeader = new MessageHeader();
        final DirectBuffer encodeBuffer = new DirectBuffer(ByteBuffer.allocateDirect(1024));

        final byte[] tempBuffer = new byte[128];
        final DirectBuffer decodeBuffer = new DirectBuffer(ByteBuffer.allocateDirect(1024));

        {
            CarBenchmark.encode(messageHeader, car, decodeBuffer, bufferIndex);
        }
    }

    @GenerateMicroBenchmark
    public int testEncode(final MyState state)
    {
        final Car car = state.car;
        final MessageHeader messageHeader = state.messageHeader;
        final DirectBuffer buffer = state.encodeBuffer;
        final int bufferIndex = state.bufferIndex;

        encode(messageHeader, car, buffer, bufferIndex);

        return car.size();
    }

    @GenerateMicroBenchmark
    public int testDecode(final MyState state)
    {
        final Car car = state.car;
        final MessageHeader messageHeader = state.messageHeader;
        final DirectBuffer buffer = state.decodeBuffer;
        final int bufferIndex = state.bufferIndex;
        final byte[] tempBuffer = state.tempBuffer;

        decode(messageHeader, car, buffer, bufferIndex, tempBuffer);

        return car.size();
    }

    public static void encode(final MessageHeader messageHeader,
                              final Car car,
                              final DirectBuffer buffer,
                              final int bufferIndex)
    {
        messageHeader.reset(buffer, bufferIndex, 0)
             .templateId(car.templateId())
             .version((short)car.templateVersion())
             .blockLength(car.blockLength());

        car.resetForEncode(buffer, bufferIndex + messageHeader.size())
           .code(Model.A)
           .modelYear(2005)
           .serialNumber(12345)
           .available(BooleanType.TRUE)
           .putVehicleCode(VEHICLE_CODE, 0);

        car.engine().capacity(4200)
                    .numCylinders((short)8)
                    .putManufacturerCode(ENG_MAN_CODE, 0);

        car.fuelFiguresCount(3).next().speed(30).mpg(35.9f)
                               .next().speed(55).mpg(49.0f)
                               .next().speed(75).mpg(40.0f);

        final Car.PerformanceFigures perfFigures = car.performanceFiguresCount(2);
        perfFigures.next().octaneRating((short)95)
                          .accelerationCount(3).next().mph(30).seconds(4.0f)
                                               .next().mph(60).seconds(7.5f)
                                               .next().mph(100).seconds(12.2f);
        perfFigures.next().octaneRating((short)99)
                          .accelerationCount(3).next().mph(30).seconds(3.8f)
                                               .next().mph(60).seconds(7.1f)
                                               .next().mph(100).seconds(11.8f);

        car.putMake(MAKE, 0, MAKE.length);
        car.putModel(MODEL, 0, MODEL.length);
    }


    private static void decode(final MessageHeader messageHeader,
                               final Car car,
                               final DirectBuffer buffer,
                               final int bufferIndex,
                               final byte[] tempBuffer)
    {
        messageHeader.reset(buffer, bufferIndex, 0);

        final int templateId = messageHeader.templateId();
        final int actingVersion = messageHeader.version();
        final int actingBlockLength = messageHeader.blockLength();

        car.resetForDecode(buffer, bufferIndex + messageHeader.size(), actingBlockLength, actingVersion);

        car.templateId();
        car.serialNumber();
        car.modelYear();
        car.available();
        car.code();

        for (int i = 0, size = car.someNumbersLength(); i < size; i++)
        {
            car.someNumbers(i);
        }

        for (int i = 0, size = car.vehicleCodeLength(); i < size; i++)
        {
            car.vehicleCode(i);
        }

        final OptionalExtras extras = car.extras();
        extras.cruiseControl();
        extras.sportsPack();
        extras.sunRoof();

        final Engine engine = car.engine();
        engine.capacity();
        engine.numCylinders();
        engine.maxRpm();
        for (int i = 0, size = engine.manufacturerCodeLength(); i < size; i++)
        {
            engine.manufacturerCode(i);
        }

        engine.getFuel(tempBuffer, 0, tempBuffer.length);

        for (final Car.FuelFigures fuelFigures : car.fuelFigures())
        {
            fuelFigures.speed();
            fuelFigures.mpg();
        }

        for (final Car.PerformanceFigures performanceFigures : car.performanceFigures())
        {
            performanceFigures.octaneRating();

            for (final Car.PerformanceFigures.Acceleration acceleration : performanceFigures.acceleration())
            {
                acceleration.mph();
                acceleration.seconds();
            }
        }

        car.getMake(tempBuffer, 0, tempBuffer.length);
        car.getModel(tempBuffer, 0, tempBuffer.length);
    }
}
