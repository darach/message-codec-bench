#
# Copyright 2013 Real Logic Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# GNU make
#

.SUFFIXES:    .cpp .o

SRC_DIR=      main/cpp
BUILD_DIR=    target/gen/cpp
OBJDIR=       $(BUILD_DIR)
CC=           gcc
CXX=          g++
LINK=         gcc
CFLAGS=       -Wall -Ofast
CXXFLAGS=     -Wall -Ofast
LD_LIBS=
JAVA=         java
SBE_JAR=      lib/sbe/sbe-0.1.jar
SBE_CAR_SCHEMA=   main/resources/sbe/car-c.xml
SBE_FIX_SCHEMA=   main/resources/sbe/fix-message-samples.xml
SBE_TARGET_DIR=   $(BUILD_DIR)
SBE_ARGS=     -Dsbe.output.dir="$(SBE_TARGET_DIR)" -Dsbe.target.language="Cpp99"
SBE_INCLUDE_DIR= main/resources

#ifeq ($(origin PROTOBUF_HOME), undefined)
#    PROTOBUF_HOME_NOT_DEFINED=  1
#endif

PROTOBUF_RESOURCES=     main/resources/protobuf
PROTOBUF_HOME?=         $(HOME)/dev/protobuf-2.5.0
PROTOBUF_CAR_SCHEMA=    main/resources/protobuf/car.proto
PROTOBUF_FIX_SCHEMA=    main/resources/protobuf/fix-messages.proto
PROTOBUF_TARGET_DIR=    $(BUILD_DIR)
PROTOBUF_CMD=           $(PROTOBUF_HOME)/bin/protoc
PROTOBUF_LIBS=          -L$(PROTOBUF_HOME)/lib -lprotobuf

PLAT=         $(shell uname -s)
ifeq ($(PLAT),Darwin)
	CFLAGS +=   -DDarwin
	CXXFLAGS += -DDarwin
else ifeq ($(PLAT),Linux)
	CFLAGS +=   -D_GNU_SOURCE
	CXXFLAGS += -D_GNU_SOURCE
endif

vpath %.cpp   $(SRC_DIR)
vpath %.hpp   $(SRC_DIR)

SBE_CAR_SRCS=         CarBench.cpp benchlet-main.cpp
SBE_CAR_OBJS=         $(addprefix $(OBJDIR)/, $(SBE_CAR_SRCS:.cpp=.o))

SBE_NOS_SRCS=         NosBench.cpp benchlet-main.cpp
SBE_NOS_OBJS=         $(addprefix $(OBJDIR)/, $(SBE_NOS_SRCS:.cpp=.o))

SBE_MD_SRCS=          MarketDataBench.cpp benchlet-main.cpp
SBE_MD_OBJS=          $(addprefix $(OBJDIR)/, $(SBE_MD_SRCS:.cpp=.o))

PB_MD_SRCS=           PbMarketDataBench.cpp benchlet-main.cpp
PB_MD_OBJS=           $(addprefix $(OBJDIR)/, $(PB_MD_SRCS:.cpp=.o))
PB_MD_OBJS+=          $(addprefix $(OBJDIR)/, fix-messages.pb.o)

CXXFLAGS += -I$(SRC_DIR) -I$(BUILD_DIR) -I$(SBE_INCLUDE_DIR) -I$(PROTOBUF_HOME)/include
CFLAGS +=   -I..
LD_LIBS +=  -lstdc++

BENCHLET_SBE_CAR_RUNNER=    $(BUILD_DIR)/benchlet-sbe-car-runner
BENCHLET_SBE_NOS_RUNNER=    $(BUILD_DIR)/benchlet-sbe-nos-runner
BENCHLET_SBE_MD_RUNNER=     $(BUILD_DIR)/benchlet-sbe-md-runner

BENCHLET_PB_MD_RUNNER=      $(BUILD_DIR)/benchlet-pb-md-runner

.PHONY: clean

all:	$(BENCHLET_SBE_CAR_RUNNER) $(BENCHLET_SBE_NOS_RUNNER) $(BENCHLET_SBE_MD_RUNNER)

init:       | $(BUILD_DIR)

$(BUILD_DIR):
			mkdir -p $(BUILD_DIR)

sbe-car-codec:  init $(SBE_CAR_SCHEMA)
				$(JAVA) $(SBE_ARGS) -jar $(SBE_JAR) $(SBE_CAR_SCHEMA)

sbe-fix-codec:  init $(SBE_FIX_SCHEMA)
				$(JAVA) $(SBE_ARGS) -jar $(SBE_JAR) $(SBE_FIX_SCHEMA)

protobuf-car-codec: init $(PROTOBUF_CAR_SCHEMA)
					$(PROTOBUF_CMD) -I$(PROTOBUF_RESOURCES) --cpp_out $(PROTOBUF_TARGET_DIR) $(PROTOBUF_CAR_SCHEMA)

protobuf-fix-codec: init $(PROTOBUF_FIX_SCHEMA)
					$(PROTOBUF_CMD) -I$(PROTOBUF_RESOURCES) --cpp_out "$(PROTOBUF_TARGET_DIR)" $(PROTOBUF_FIX_SCHEMA)

$(BENCHLET_SBE_CAR_RUNNER): init sbe-car-codec $(SBE_CAR_OBJS)
			    			$(LINK) $(SBE_CAR_OBJS) -o $(BENCHLET_SBE_CAR_RUNNER) $(LD_LIBS)

$(BENCHLET_SBE_NOS_RUNNER): init sbe-fix-codec $(SBE_NOS_OBJS)
			    			$(LINK) $(SBE_NOS_OBJS) -o $(BENCHLET_SBE_NOS_RUNNER) $(LD_LIBS)

$(BENCHLET_SBE_MD_RUNNER): init sbe-fix-codec $(SBE_MD_OBJS)
						$(LINK) $(SBE_MD_OBJS) -o $(BENCHLET_SBE_MD_RUNNER) $(LD_LIBS)

$(BENCHLET_PB_MD_RUNNER): init protobuf-fix-codec $(PB_MD_OBJS)
						$(LINK) $(PB_MD_OBJS) -o $(BENCHLET_PB_MD_RUNNER) $(LD_LIBS) $(PROTOBUF_LIBS)

run-sbe-car-benchmark:  $(BENCHLET_SBE_CAR_RUNNER)
						$(BENCHLET_SBE_CAR_RUNNER)

run-sbe-nos-benchmark:  $(BENCHLET_SBE_NOS_RUNNER)
						$(BENCHLET_SBE_NOS_RUNNER)

run-sbe-md-benchmark:  $(BENCHLET_SBE_MD_RUNNER)
					   $(BENCHLET_SBE_MD_RUNNER)

run-pb-md-benchmark:  $(BENCHLET_PB_MD_RUNNER)
					  $(BENCHLET_PB_MD_RUNNER)

run-md-benchmarks: run-sbe-md-benchmark run-pb-md-benchmark

run-car-benchmarks: run-sbe-car-benchmark

run-benchmarks:     run-md-benchmarks run-car-benchmarks

$(OBJDIR)/%.o : %.cpp
				$(CXX) -c $(CXXFLAGS) $< -o $@

$(OBJDIR)/%.o : %.cc
				$(CXX) -c $(CXXFLAGS) $< -o $@

clean:
		$(RM) -rf $(BUILD_DIR)
