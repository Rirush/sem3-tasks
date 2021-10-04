#!/bin/bash

export TICKERS="BTC DOGE ETH BNB CAKE USDT"
export CURRENCIES="USD EUR CHF CNY RUB UAH JPY HUF PHP BEL"

mkdir -p result/

for ticker in $(echo $TICKERS | tr " " " ")
do
    echo "https://min-api.cryptocompare.com/data/price?fsym=${ticker}&tsyms=USD,EUR,CHF,CNY,RUB,UAH,JPY,HUF,PHP,BEL"
    wget "-O${ticker}.json" -q "https://min-api.cryptocompare.com/data/price?fsym=${ticker}&tsyms=USD,EUR,CHF,CNY,RUB,UAH,JPY,HUF,PHP,BEL" &
done

for job in `jobs -p`
do
    wait $job
done

for ticker in $(echo $TICKERS | tr " " " ")
do
    for currency in $(echo $CURRENCIES | tr " " " ")
    do
        jq ".$currency" "${ticker}.json" > "result/$ticker-$currency.txt"
    done
    rm "${ticker}.json"
done