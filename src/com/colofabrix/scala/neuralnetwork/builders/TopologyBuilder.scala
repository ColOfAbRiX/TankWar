package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork._
import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction
import com.colofabrix.scala.neuralnetwork.layers._

import scala.util.Random

abstract class TopologyBuilder {
  def build: FeedforwardNeuralNetwork
}

class Random3LNetwork (
  n_inputs: Int,
  n_hidden: Int,
  n_output: Int,
  scaling: Double = 1.0,
  activation: String = "tanh"
)
extends TopologyBuilder {

  private def getRandom = Random.nextDouble * 2 * scaling - scaling

  override def build: FeedforwardNeuralNetwork = {
    val inputLayer = new InputLayer(n_inputs)

    val hiddenLayer = new HiddenLayer(
      ActivationFunction(activation),
      n_inputs,
      n_hidden,
      Seq.fill(n_hidden)(getRandom),
      Seq.fill(n_hidden, n_inputs)(getRandom)
    )

    val outputLayer = new OutputLayer(
      ActivationFunction(activation),
      n_hidden,
      n_output,
      Seq.fill(n_output)(getRandom),
      Seq.fill(n_output, n_hidden)(getRandom)
    )

    new FeedforwardNeuralNetwork(inputLayer, Seq(hiddenLayer), outputLayer)
  }
}

abstract class CollectionReader {
  def getNextLayer: CollectionReader
  def getNextNeuron: CollectionReader
  def getNextInputWeight: Double
  def getNextNeuronBias: Double
  def getNextActivationFunction: ActivationFunction
}