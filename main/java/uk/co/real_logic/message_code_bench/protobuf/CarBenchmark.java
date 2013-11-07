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

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import uk.co.real_logic.protobuf.examples.Examples;
import uk.co.real_logic.protobuf.examples.Examples.Car.Model;
import uk.co.real_logic.protobuf.examples.Examples.Engine;
import uk.co.real_logic.protobuf.examples.Examples.PerformanceFigures;

public class CarBenchmark
{
    @State(Scope.Thread)
    public static class MyState
    {
        Examples.Car.Builder car = Examples.Car.newBuilder();
    }

    @GenerateMicroBenchmark
    public byte[] testMethod(final MyState state)
    {
        state.car.clear();

        state.car.setCode(Model.A)
                 .setModelYear(2005)
                 .setSerialNumber(12345)
                 .setAvailable(true)
                 .setVehicleCode("abcdef");

        state.car.addFuelFiguresBuilder().
            setSpeed(30).setMpg(35.9F);
        state.car.addFuelFiguresBuilder().
            setSpeed(30).setMpg(49.0F);
        state.car.addFuelFiguresBuilder().
            setSpeed(30).setMpg(40.0F);
        

        PerformanceFigures.Builder perf1 = state.car.addPerformanceBuilder().setOctaneRating(95);
        perf1.addAccelerationBuilder().setMph(30).setSeconds(4.0f);
        perf1.addAccelerationBuilder().setMph(60).setSeconds(7.5f);
        perf1.addAccelerationBuilder().setMph(100).setSeconds(12.2f);

        PerformanceFigures.Builder perf2 = state.car.addPerformanceBuilder().setOctaneRating(95);
        perf2.addAccelerationBuilder().setMph(30).setSeconds(3.8f);
        perf2.addAccelerationBuilder().setMph(60).setSeconds(7.1f);
        perf2.addAccelerationBuilder().setMph(100).setSeconds(11.8f);

        state.car.setMake("AUDI");
        state.car.setModel("R8");

        Engine.Builder engine = state.car.getEngineBuilder();
        engine.setCapacity(8000);
        engine.setManufacturerCode("V8");
        engine.setNumCylinders(8);
        
        return state.car.build().toByteArray();
    }
}
