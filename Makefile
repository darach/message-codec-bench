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
CFLAGS=       -Wall
CXXFLAGS=     -Wall
LD_LIBS=
JAVA=         java
SBE_JAR=      lib/sbe/sbe-0.1-20131125.jar
SBE_SCHEMA=   main/resources/sbe/car-c.xml
SBE_ARGS=     -Dsbe.output.dir="$(BUILD_DIR)" -Dsbe.target.language="Cpp99"
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
SRCS=         CarBench.cpp benchlet-main.cpp

OBJS=         $(addprefix $(OBJDIR)/, $(SRCS:.cpp=.o))

CXXFLAGS += -I$(SRC_DIR) -I$(BUILD_DIR) -I$(SBE_INCLUDE_DIR)
CFLAGS +=   -I..
LD_LIBS +=  -lstdc++

BENCHLET_RUNNER=    $(BUILD_DIR)/benchlet-runner

all:	$(BENCHLET_RUNNER) run-sbe-benchmark

init:       | $(BUILD_DIR)

$(BUILD_DIR):
			mkdir -p $(BUILD_DIR)

sbe-codec:  init $(SBE_SCHEMA)
			$(JAVA) $(SBE_ARGS) -jar $(SBE_JAR) $(SBE_SCHEMA)

$(BENCHLET_RUNNER): init sbe-codec $(OBJS)
					$(LINK) $(OBJS) -o $(BENCHLET_RUNNER) $(LD_LIBS)

run-sbe-benchmark:  $(BENCHLET_RUNNER)
					$(BENCHLET_RUNNER)

$(OBJDIR)/%.o : %.cpp
				$(CC) -c $(CXXFLAGS) $< -o $@

clean:
		$(RM) -rf $(BUILD_DIR)
