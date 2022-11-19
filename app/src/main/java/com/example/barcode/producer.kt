package com.example.barcode

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.ConnectionFactory
import java.nio.charset.StandardCharsets

fun sendMessageToBroker(userId: String, routingKey: String?, data: String) {
    val factory = ConnectionFactory()
    factory.host = "18.224.93.1"
    factory.port = 5672
    factory.username = "guest"
    factory.password = "guest"
    factory.virtualHost = "/"
    val connection = factory.newConnection()

    if( routingKey.isNullOrEmpty()){
        connection.use { it ->
            it.createChannel().use{channel ->
                channel.exchangeDeclare("public",BuiltinExchangeType.FANOUT)
                channel.basicPublish("public","",null, data.toByteArray(StandardCharsets.UTF_8))
            }
        }

    }else{
        connection.use { it ->
            it.createChannel().use{channel ->
                channel.exchangeDeclare("user",BuiltinExchangeType.DIRECT)
                channel.basicPublish("user",userId,null,data.toByteArray(StandardCharsets.UTF_8))
            }
        }
    }

}

fun main(){
    sendMessageToBroker("1","","mav alert")
    sendMessageToBroker("1","1","user specification notification")

}