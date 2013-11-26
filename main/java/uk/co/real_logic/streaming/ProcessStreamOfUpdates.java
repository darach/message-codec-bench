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

package uk.co.real_logic.streaming;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import uk.co.real_logic.sbe.examples.BooleanType;
import uk.co.real_logic.sbe.examples.Car;
import uk.co.real_logic.sbe.examples.Engine;
import uk.co.real_logic.sbe.examples.Model;
import uk.co.real_logic.sbe.generation.java.DirectBuffer;

import java.nio.ByteBuffer;

public class ProcessStreamOfUpdates
{
    public final static class Q implements StreamQuery {
        public int sum;
        public int count;
        @Override
        public
        void car(int engineCapacity, int year) {
            if (engineCapacity >  1600) {
                sum +=year;
                count++;
            }
        }
    }
    private static final StreamProcessor processor = null;
    @State(Scope.Thread)
    public static class Data {
        ByteBuffer buffy = processor.setupBenchmarkData(ByteBuffer.allocateDirect(32 * 1024 * 1024));
        Q query = new Q();
    }
    @GenerateMicroBenchmark
    public int avgYearWhereEngineIsBig(Data data)
    {
        // reset Q
        data.query.sum = 0;
        data.query.count = 0;
        // process all the cars
        processor.process(data.buffy, data.query);
        
        return data.query.sum/data.query.count;
    }
}
