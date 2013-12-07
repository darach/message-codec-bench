Message Codec Bench
===================

Performance testing benchmarks for message encoding and decoding.

Simple Binary Encoding (SBE) can be found [here](https://github.com/real-logic/simple-binary-encoding).

License (See LICENSE file for full license)
-------------------------------------------
Copyright 2013 Real Logic Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Directory Layout
----------------

Main source code

    main

Build
-----

Full clean build:

    $ ant

Run the Java benchmarks

    $ ant perf:test

C++ Build
---------

NOTE: Linux and Mac OS only for the moment.

Dependent build:

    $ make

Run the C++ benchmarks

    $ make run-benchmarks

Use of Google Protocol Buffers
------------------------------

The benchmarks use protobuf 2.5.0 as comparison. For `ant`, define `protobuf.home` in a `build-local.properties` file.
An example file is included. For `make`, define `PROTOBUF_HOME` on the command line or edit the `Makefile`.
