package de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

class CircularBuffer[A](size: Int)(implicit tag: ClassTag[A]) {
    private val buffer = new Array[A](size)
    private var head = 0
    private var tail = 0

    def add(element: A): Unit = {
        buffer(head) = element
        head = (head + 1) % size
        if (head == tail) tail = (tail + 1) % size
    }

    def addNewest(element: A): Unit = add(element)

    def addOldest(element: A): Unit = {
        tail = (tail - 1 + size) % size
        buffer(tail) = element
        if (head == tail) head = (head - 1 + size) % size
    }

    def get(index: Int): Option[A] = 
        if (index >= 0 && index < size) Some(buffer(index))
        else None

    def getAll: Seq[A] = 
        if (tail < head) buffer.slice(tail, head)
        else buffer.slice(tail, buffer.length) ++ buffer.slice(0, head)

    def getLowerThird: ListBuffer[A] = {
        val third = size / 3
        val start = 0
        val end = third
        val lowerThird = if (tail < head) buffer.slice(start, end)
        else (buffer.slice(tail, buffer.length) ++ buffer.slice(0, head)).slice(start, end)
        ListBuffer(lowerThird: _*)
    }

    def getUpperThird: ListBuffer[A] = {
        val third = size / 3
        val start = 2 * third
        val end = size
        val upperThird = if (tail < head) buffer.slice(start, end)
        else (buffer.slice(tail, buffer.length) ++ buffer.slice(0, head)).slice(start, end)
        ListBuffer(upperThird: _*)
    }

    def getMiddleThird: ListBuffer[A] = {
        val third = size / 3
        val start = third
        val end = 2 * third
        val middleThird = if (tail < head) buffer.slice(start, end)
        else (buffer.slice(tail, buffer.length) ++ buffer.slice(0, head)).slice(start, end)
        ListBuffer(middleThird: _*)
    }

    def removeOldest(): Option[A] = 
        if (head != tail) {
            val oldest = buffer(tail)
            tail = (tail + 1) % size
            Some(oldest)
        } else None  

    def removeNewest(): Option[A] = 
        if (head != tail) {
            head = (head - 1 + size) % size
            val newest = buffer(head)
            Some(newest)
        } else None

    def getNewestThird: ListBuffer[A] = {
    val third = size / 3
    val start = 2 * third
    val end = size
    val newestThird = if (tail < head) buffer.slice(start, end)
    else (buffer.slice(tail, buffer.length) ++ buffer.slice(0, head)).slice(start, end)
    ListBuffer(newestThird: _*)
}

    def getOldestThird: ListBuffer[A] = {
        val third = size / 3
        val start = 0
        val end = third
        val oldestThird = if (tail < head) buffer.slice(start, end)
        else (buffer.slice(tail, buffer.length) ++ buffer.slice(0, head)).slice(start, end)
        ListBuffer(oldestThird: _*)
    }
}