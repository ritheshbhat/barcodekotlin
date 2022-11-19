package com.example.barcode



import android.annotation.SuppressLint
import com.rabbitmq.client.*
import java.nio.charset.StandardCharsets


fun main() {
    val user_id= "1"
    val factory = ConnectionFactory()
    factory.host = "18.224.93.1"
    factory.port = 5672
    factory.username = "guest"
    factory.password = "guest"
    factory.virtualHost = "/"
    val connection = factory.newConnection()
    val channel = connection.createChannel()
    val consumerTag = "SimpleConsumer"
    channel.exchangeDeclare("public", BuiltinExchangeType.FANOUT)
    channel.exchangeDeclare("user", BuiltinExchangeType.DIRECT)

    val queueResult = channel.queueDeclare("", false, true, true, null)
    channel.queueBind(queueResult.queue, "public", "")
    channel.queueDeclare(user_id,false,true,true,null)
    channel.queueBind(user_id,"user",user_id)



    println("[$consumerTag] Waiting for messages...")
    val deliverCallback = DeliverCallback { x: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println("[$x] Received message: '$message'")
    }
    val cancelCallback = CancelCallback {
        println("[$consumerTag] was canceled")
    }

    channel.basicConsume(queueResult.queue, false, consumerTag, deliverCallback, cancelCallback)
    channel.basicConsume(user_id, false, "userTag", deliverCallback, cancelCallback)


}
