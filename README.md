# crypto-price

>The application gets crypto price information using https://www.cryptocompare.com/api at a scheduled rate.
>It saves the price information to a database as well as pushing the information to a websocket.
>The database persistence and the web socket message pushing are on separate threads 
>and can be scheduled at a different rate using the config file.
