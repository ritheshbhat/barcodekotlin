package com.example.barcode



import android.annotation.SuppressLint
import com.rabbitmq.client.*
import java.nio.charset.StandardCharsets


fun main() {
    val factory = ConnectionFactory()
    factory.host = "localhost"
    factory.port = 5672
    factory.username = "guest"
    factory.password = "guest"
    factory.virtualHost = "/"
    val connection = factory.newConnection()

//    val connection = factory.newConnection("guest:guest@localhost:5672/")
    val channel = connection.createChannel()
    val consumerTag = "SimpleConsumer"
    channel.exchangeDeclare("logs", BuiltinExchangeType.FANOUT)
    val queueResult = channel.queueDeclare("", false, true, true, null)
    channel.queueBind(queueResult.queue, "logs", "")


    println("queue is ${queueResult.queue}")


    println("[$consumerTag] Waiting for messages...")
    val deliverCallback = DeliverCallback { x: String?, delivery: Delivery ->
        val message = String(delivery.body, StandardCharsets.UTF_8)
        println("[$x] Received message: '$message'")
    }
    val cancelCallback = CancelCallback {
        println("[$consumerTag] was canceled")
    }

    channel.basicConsume(queueResult.queue, false, consumerTag, deliverCallback, cancelCallback)

}
