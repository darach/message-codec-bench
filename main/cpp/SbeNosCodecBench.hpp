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
#ifndef _SBE_NOS_CODEC_BENCH_HPP
#define _SBE_NOS_CODEC_BENCH_HPP

#include "CodecBench.hpp"
#include "uk_co_real_logic_sbe_samples_fix/NewOrder.hpp"

using namespace uk_co_real_logic_sbe_samples_fix;

// 12
char ACCOUNT[] = "NewJackHawt1";
// 20
char CLORDID[] = "NewJackHawtClOrdId01";
// 6
char SYMBOL[] = "SBEOPT";
// 10
char ALLOCACCOUNT[] = "NewJackets";
// 20
char SECURITYDESC[] = "DataLayoutHasOptions";
// 3
char SECURITYTYPE[] = "UHH";
// 12
char SELFMATCHPREVENTIONID[] = "DONTMATCHID1";
// 3
char GIVEUPFIRM[] = "OHH";
// 2
char CMTAGIVEUPCD[] = "OH";
// 20
char CORRELATIONCLORDID[] = "NewJackHawtClOrdId01";

class SbeNewOrderSingleCodecBench : public CodecBench
{
public:
    virtual int encode(char *buffer)
    {
        nos_.resetForEncode(buffer, 0)
            .putAccount(ACCOUNT)
            .putClOrdID(CLORDID)
            .HandInst(HandInst::AUTOMATED_EXECUTION)
            .CustOrderHandlingInst(CustOrderHandlingInst::ALGO_ENGINE);

        nos_.OrderQty().mantissa(10);

        nos_.OrdType(OrdType::MARKET_ORDER);

        nos_.Price()
            .mantissa(3509)
            .exponent(-2);

        nos_.Side(Side::BUY)
            .putSymbol(SYMBOL)
            .TimeInForce(TimeInForce::GOOD_TILL_CANCEL)
            .TransactTime(0xFFFFFFFFFEFE)
            .ManualOrderIndicator(BooleanType::FIX_FALSE)
            .putAllocAccount(ALLOCACCOUNT);

        nos_.StopPx()
            .mantissa(3510)
            .exponent(-2);

        nos_.putSecurityDesc(SECURITYDESC);

        nos_.MinQty().mantissa(9);

        nos_.putSecurityType(SECURITYTYPE)
            .CustomerOrFirm(CustomerOrFirm::CUSTOMER);

        nos_.MaxShow().mantissa(5);

        nos_.ExpireDate(1210)
            .putSelfMatchPreventionID(SELFMATCHPREVENTIONID)
            .CtiCode(CtiCode::OWN)
            .putGiveUpFirm(GIVEUPFIRM)
            .putCmtaGiveupCD(CMTAGIVEUPCD)
            .putCorrelationClOrdID(CORRELATIONCLORDID);

        return nos_.size();
    };

    virtual int decode(const char *buffer)
    {
        nos_.resetForDecode((char *)buffer, 0, nos_.blockLength(), nos_.templateVersion());

        int64_t tmpInt;
        const char *tmpChar;

        tmpChar = nos_.Account();
        tmpChar = nos_.ClOrdID();
        tmpInt = nos_.HandInst();
        tmpInt = nos_.CustOrderHandlingInst();
        tmpInt = nos_.OrderQty().mantissa();
        tmpInt = nos_.OrdType();
        tmpInt = nos_.Price().mantissa();
        tmpInt = nos_.Price().exponent();
        tmpInt = nos_.Side();
        tmpChar = nos_.Symbol();
        tmpInt = nos_.TimeInForce();
        tmpInt = nos_.TransactTime();
        tmpInt = nos_.ManualOrderIndicator();
        tmpChar = nos_.AllocAccount();
        tmpInt = nos_.StopPx().mantissa();
        tmpInt = nos_.StopPx().exponent();
        tmpChar = nos_.SecurityDesc();
        tmpInt = nos_.MinQty().mantissa();
        tmpChar = nos_.SecurityType();
        tmpInt = nos_.CustomerOrFirm();
        tmpInt = nos_.MaxShow().mantissa();
        tmpInt = nos_.ExpireDate();
        tmpChar = nos_.SelfMatchPreventionID();
        tmpInt = nos_.CtiCode();
        tmpChar = nos_.GiveUpFirm();
        tmpChar = nos_.CmtaGiveupCD();
        tmpChar = nos_.CorrelationClOrdID();

        return nos_.size();
    };

private:
    NewOrder nos_;
};

#endif /* _SBE_NOS_CODEC_BENCH_HPP */
