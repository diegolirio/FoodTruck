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


### Cassandra 

```sh
sudo docker-compose -f docker-compose.separed.yml up 
```

```sh
sudo docker exec -it d2d786706ef8_cassandra bash
cqlsh
cqlsh> describe tables;
```

```
Keyspace jaeger_v1_dc1
----------------------
service_name_index  service_names  service_operation_index  traces            
dependencies_v2     tag_index      duration_index           operation_names_v2
```

```sh
cqlsh> select * from jaeger_v1_dc1.service_name_index ;    

 service_name | bucket | start_time       | trace_id
--------------+--------+------------------+------------------------------------
      produto |      1 | 1609780387080000 | 0x000000000000000072bed4789a519110
      produto |      1 | 1609780375077000 | 0x00000000000000003edca91cc5ecfda7
```
   
TTL Default 48 horas   
   
```sh
cqlsh> select operation_name, ttl(trace_id) from jaeger_v1_dc1.service_operation_index;

 operation_name | ttl(trace_id)
----------------+---------------
        getById |        172795
        getById |        170732
```		

```sh
cqlsh> insert into jaeger_v1_dc1.service_operation_index (service_name, operation_name, start_time, trace_id) values ('produto', 'getById', 1609780387080000, 0x000000000000000072bed4789a519110) using ttl 15;
```

```sh
cqlsh> select operation_name, ttl(trace_id) from jaeger_v1_dc1.service_operation_index;

 operation_name | ttl(trace_id)
----------------+---------------
        getById |        172578
        getById |            10
        getById |        170515
```		