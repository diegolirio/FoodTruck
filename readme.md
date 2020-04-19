# Food Truck - Log

#### First

```shell script
json-server json-server.json -w
```

#### Second

```shell script
curl --location --request POST 'localhost:8080/v1/pedidos' \
--header 'Content-Type: application/json' \
--data-raw '{
	"clienteId": 1,
	"itensId": [1,5]
}'
```


Ref.:   
https://www.baeldung.com/spring-cloud-sleuth-single-application    
https://howtodoinjava.com/spring-cloud/spring-cloud-zipkin-sleuth-tutorial/   