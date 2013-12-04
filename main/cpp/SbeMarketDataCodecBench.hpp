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
#ifndef _SBE_MARKET_DATA_CODEC_BENCH_HPP
#define _SBE_MARKET_DATA_CODEC_BENCH_HPP

#include "CodecBench.hpp"
#include "uk_co_real_logic_sbe_samples_fix/MessageHeader.hpp"
#include "uk_co_real_logic_sbe_samples_fix/MarketDataIncrementalRefreshTrades.hpp"

using namespace uk_co_real_logic_sbe_samples_fix;

class SbeMarketDataCodecBench : public CodecBench
{
public:
    virtual int encode(char *buffer)
    {
        messageHeader_.wrap(buffer, 0, 0)
                      .templateId(MarketDataIncrementalRefreshTrades::templateId())
                      .version(MarketDataIncrementalRefreshTrades::templateVersion())
                      .blockLength(MarketDataIncrementalRefreshTrades::blockLength());

        marketData_.wrapForEncode(buffer + MessageHeader::size(), 0)
                   .TransactTime(1234L)
                   .EventTimeDelta(987)
                   .MatchEventIndicator(MatchEventIndicator::END_EVENT);

        MarketDataIncrementalRefreshTrades::MdIncGrp &mdIncGrp = marketData_.mdIncGrpCount(2);

        mdIncGrp.next();
        mdIncGrp.TradeId(1234L);
        mdIncGrp.SecurityId(56789L);
        mdIncGrp.MdEntryPx().mantissa(50);
        mdIncGrp.MdEntrySize().mantissa(10);
        mdIncGrp.NumberOfOrders(1);
        mdIncGrp.MdUpdateAction(MDUpdateAction::NEW);
        mdIncGrp.RptSeq((short)1);
        mdIncGrp.AggressorSide(Side::BUY);
        mdIncGrp.MdEntryType(MDEntryType::BID);

        mdIncGrp.next();
        mdIncGrp.TradeId(1234L);
        mdIncGrp.SecurityId(56789L);
        mdIncGrp.MdEntryPx().mantissa(50);
        mdIncGrp.MdEntrySize().mantissa(10);
        mdIncGrp.NumberOfOrders(1);
        mdIncGrp.MdUpdateAction(MDUpdateAction::NEW);
        mdIncGrp.RptSeq((short)1);
        mdIncGrp.AggressorSide(Side::SELL);
        mdIncGrp.MdEntryType(MDEntryType::OFFER);

        return MessageHeader::size() + marketData_.size();
    };

    virtual int decode(const char *buffer)
    {
        int64_t actingVersion;
        int64_t actingBlockLength;

        messageHeader_.wrap((char *)buffer, 0, 0);

        actingVersion = messageHeader_.version();
        actingBlockLength = messageHeader_.blockLength();

        marketData_.wrapForDecode((char *)buffer, MessageHeader::size(), actingBlockLength, actingVersion);

        marketData_.TransactTime();
        marketData_.EventTimeDelta();
        marketData_.MatchEventIndicator();

        MarketDataIncrementalRefreshTrades::MdIncGrp &mdIncGrp = marketData_.mdIncGrp();
        while (mdIncGrp.hasNext())
        {
            mdIncGrp.next();
            mdIncGrp.TradeId();
            mdIncGrp.SecurityId();
            mdIncGrp.MdEntryPx().mantissa();
            mdIncGrp.MdEntrySize().mantissa();
            mdIncGrp.NumberOfOrders();
            mdIncGrp.MdUpdateAction();
            mdIncGrp.RptSeq();
            mdIncGrp.AggressorSide();
            mdIncGrp.MdEntryType();
        }

        return MessageHeader::size() + marketData_.size();
    };

private:
    MessageHeader messageHeader_;
    MarketDataIncrementalRefreshTrades marketData_;
};

#endif /* _SBE_MARKET_DATA_CODEC_BENCH_HPP */
