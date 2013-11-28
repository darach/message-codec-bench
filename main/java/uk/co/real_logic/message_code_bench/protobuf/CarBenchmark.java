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

package uk.co.real_logic.message_code_bench.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import uk.co.real_logic.protobuf.examples.Examples;
import uk.co.real_logic.protobuf.examples.Examples.Car.Model;
import uk.co.real_logic.protobuf.examples.Examples.PerformanceFigures;

public class CarBenchmark
{
    private static final String VEHICLE_CODE = "abcdef";
    private static final String ENG_MAN_CODE = "abc";
    private static final String MAKE = "AUDI";
    private static final String MODEL = "R8";

    @State(Scope.Benchmark)
    public static class MyState
    {
        final Examples.Car.Builder car = Examples.Car.newBuilder();
        final byte[] decodeBuffer;

        {
            decodeBuffer = encode(car);
        }
    }

    @GenerateMicroBenchmark
    public Object testEncode(final MyState state)
    {
        final Examples.Car.Builder car = state.car;

        return encode(car);
    }

    @GenerateMicroBenchmark
    public Object testDecode(final MyState state) throws InvalidProtocolBufferException
    {
        final Examples.Car.Builder car = state.car;
        final byte[] buffer = state.decodeBuffer;

        return decode(car, buffer);
    }

    private static byte[] encode(final Examples.Car.Builder car)
    {
        car.clear()
           .setCode(Model.A)
           .setModelYear(2005)
           .setSerialNumber(12345)
           .setAvailable(true)
           .setVehicleCode(VEHICLE_CODE);

        for (int i = 0, size = 5; i < size; i++)
        {
            car.addSomeNumbers(i);
        }

        car.addOptionalExtras(Examples.Car.Extras.SPORTS_PACK)
           .addOptionalExtras(Examples.Car.Extras.SUN_ROOF);

        car.getEngineBuilder().setCapacity(4200)
                              .setNumCylinders(8)
                              .setManufacturerCode(ENG_MAN_CODE)
                              .setFuel("Petrol")
                              .setMaxRpm(9000);

        car.addFuelFiguresBuilder().setSpeed(30).setMpg(35.9F);
        car.addFuelFiguresBuilder().setSpeed(30).setMpg(49.0F);
        car.addFuelFiguresBuilder().setSpeed(30).setMpg(40.0F);

        final PerformanceFigures.Builder perf1 = car.addPerformanceBuilder().setOctaneRating(95);
        perf1.addAccelerationBuilder().setMph(30).setSeconds(4.0f);
        perf1.addAccelerationBuilder().setMph(60).setSeconds(7.5f);
        perf1.addAccelerationBuilder().setMph(100).setSeconds(12.2f);

        final PerformanceFigures.Builder perf2 = car.addPerformanceBuilder().setOctaneRating(95);
        perf2.addAccelerationBuilder().setMph(30).setSeconds(3.8f);
        perf2.addAccelerationBuilder().setMph(60).setSeconds(7.1f);
        perf2.addAccelerationBuilder().setMph(100).setSeconds(11.8f);

        car.setMake(MAKE);
        car.setModel(MODEL);

        return car.build().toByteArray();
    }

    private static Object decode(final Examples.Car.Builder car,
                                 final byte[] buffer) throws InvalidProtocolBufferException
    {
        car.clear();
        car.mergeFrom(buffer);

        car.getSerialNumber();
        car.getModelYear();
        car.hasAvailable();
        car.getCode();

        for (int i = 0, size = car.getSomeNumbersCount(); i < size; i++)
        {
            car.getSomeNumbers(i);
        }

        car.getVehicleCode();

        for (int i = 0, size = car.getOptionalExtrasCount(); i < size; i++)
        {
            car.getOptionalExtras(i);
        }

        final Examples.Engine engine = car.getEngine();
        engine.getCapacity();
        engine.getNumCylinders();
        engine.getMaxRpm();
        engine.getManufacturerCode();
        engine.getFuel();

        for (final Examples.FuelFigures fuelFigures : car.getFuelFiguresList())
        {
            fuelFigures.getSpeed();
            fuelFigures.getMpg();
        }

        for (final PerformanceFigures performanceFigures : car.getPerformanceList())
        {
            performanceFigures.getOctaneRating();

            for (final Examples.Acceleration acceleration : performanceFigures.getAccelerationList())
            {
                acceleration.getMph();
                acceleration.getSeconds();
            }
        }

        car.getMake();

        return car.getModel();
    }
}
