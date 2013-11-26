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
#ifndef _CODEC_BENCH_HPP
#define _CODEC_BENCH_HPP

// Interface for encoding and decoding and also benchmark harness
class CodecBench
{
public:
    virtual int encode(char *) = 0;
    virtual int decode(const char *) = 0;

    /*
     * Benchmarks
     */

    /*
     * Run 1 encoding
     */
    void runEncode(char *buffer)
    {
        encode(buffer);
    };

    /*
     * Run 1 decoding
     */
    void runDecode(const char *buffer)
    {
        decode(buffer);
    };

    /*
     * Run 1 encoding + decoding
     */
    void runEncodeAndDecode(char *buffer)
    {
        encode(buffer);
        decode(buffer);
    };

    /*
     * Run n encodings
     */
    void runEncode(char *buffer, const int n)
    {
        char *ptr = buffer;

        for (int i = 0; i < n; i++)
        {
            ptr += encode(ptr);
        }
    };

    /*
     * Run n decodings
     */
    void runDecode(const char *buffer, const int n)
    {
        const char *ptr = buffer;

        for (int i = 0; i < n; i++)
        {
            ptr += decode(ptr);
        }
    };

    /*
     * Run n encodings followed by n decodings
     */
    void runEncodeAndDecode(char *buffer, const int n)
    {
        char *ptr = buffer;

        for (int i = 0; i < n; i++)
        {
            ptr += encode(ptr);
        }
        ptr = buffer;
        for (int i = 0; i < n; i++)
        {
            ptr += decode(ptr);
        }
    };
};

#endif /* _CODEC_BENCH_HPP */
