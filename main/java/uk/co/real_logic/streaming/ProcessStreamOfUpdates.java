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
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.nio.ByteBuffer;

public class ProcessStreamOfUpdates
{
    public static class Query implements StreamQuery
    {
        public int sum;
        public int count;

       public void car(final int engineCapacity, final int year)
        {
            if (engineCapacity > 1600)
            {
                sum += year;
                count++;
            }
        }
    }

    private static final StreamProcessor processor = null;

    @State(Scope.Thread)
    public static class Data
    {
        final ByteBuffer buffy = processor.setupBenchmarkData(ByteBuffer.allocateDirect(32 * 1024 * 1024));
        final Query query = new Query();
    }

    @GenerateMicroBenchmark
    public int avgYearWhereEngineIsBig(final Data data)
    {
        // reset Query
        data.query.sum = 0;
        data.query.count = 0;
        // process all the cars
        processor.process(data.buffy, data.query);

        return data.query.sum / data.query.count;
    }
}
