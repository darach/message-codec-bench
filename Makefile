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
BUILD_DIR=    target/main/cpp
OBJDIR=       $(BUILD_DIR)
CC=           gcc
CXX=          g++
LINK=         gcc
CFLAGS=       -Wall -O3
CXXFLAGS=     -Wall -O3
LD_LIBS=
JAVA=         java
SBE_JAR=      lib/sbe/sbe-0.1.jar
SBE_CAR_SCHEMA=   main/resources/sbe/car-c.xml
SBE_FIX_SCHEMA=   main/resources/sbe/order-and-quote-samples.xml
SBE_TARGET_DIR=   $(BUILD_DIR)
SBE_ARGS=     -Dsbe.output.dir="$(SBE_TARGET_DIR)" -Dsbe.target.language="Cpp99"
SBE_INCLUDE_DIR= main/resources

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

CAR_SRCS=         CarBench.cpp benchlet-main.cpp
CAR_OBJS=         $(addprefix $(OBJDIR)/, $(CAR_SRCS:.cpp=.o))

NOS_SRCS=         NosBench.cpp benchlet-main.cpp
NOS_OBJS=         $(addprefix $(OBJDIR)/, $(NOS_SRCS:.cpp=.o))

CXXFLAGS += -I$(SRC_DIR) -I$(BUILD_DIR) -I$(SBE_INCLUDE_DIR)
CFLAGS +=   -I..
LD_LIBS +=  -lstdc++

BENCHLET_CAR_RUNNER=    $(BUILD_DIR)/benchlet-car-runner
BENCHLET_NOS_RUNNER=    $(BUILD_DIR)/benchlet-nos-runner

all:	$(BENCHLET_CAR_RUNNER) $(BENCHLET_NOS_RUNNER)

init:       | $(BUILD_DIR)

$(BUILD_DIR):
			mkdir -p $(BUILD_DIR)

sbe-car-codec:  init $(SBE_CAR_SCHEMA)
				$(JAVA) $(SBE_ARGS) -jar $(SBE_JAR) $(SBE_CAR_SCHEMA)

$(BENCHLET_CAR_RUNNER): init sbe-car-codec $(CAR_OBJS)
						$(LINK) $(CAR_OBJS) -o $(BENCHLET_CAR_RUNNER) $(LD_LIBS)

run-sbe-car-benchmark:  $(BENCHLET_CAR_RUNNER)
						$(BENCHLET_CAR_RUNNER)

sbe-fix-codec:  init $(SBE_FIX_SCHEMA)
				$(JAVA) $(SBE_ARGS) -jar $(SBE_JAR) $(SBE_FIX_SCHEMA)

$(BENCHLET_NOS_RUNNER): init sbe-fix-codec $(NOS_OBJS)
						$(LINK) $(NOS_OBJS) -o $(BENCHLET_NOS_RUNNER) $(LD_LIBS)

run-sbe-nos-benchmark:  $(BENCHLET_NOS_RUNNER)
						$(BENCHLET_NOS_RUNNER)

$(OBJDIR)/%.o : %.cpp
				$(CC) -c $(CXXFLAGS) $< -o $@

clean:
		$(RM) -rf $(BUILD_DIR)
