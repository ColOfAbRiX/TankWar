/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.scala

import java.awt.GraphicsEnvironment
import java.io.File

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.integration._
import com.colofabrix.scala.simulation.integration.operators.{ TankCrossover, TankDriftMutation, TankFullMutation }
import com.colofabrix.scala.simulation.{ Tank, World }
import org.uncommons.maths.random.{ GaussianGenerator, MersenneTwisterRNG, Probability }
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection._
import org.uncommons.watchmaker.framework.termination._

import scala.collection.JavaConverters._

/**
 * Simulation entry point
 */
object TankWarMain {

  def main( args: Array[String] ): Unit = {

    // Clean analysis files
    for {
      files ← Option( new File( "." ).listFiles )
      file ← files if file.getName.endsWith( ".csv" )
    } file.delete()

    // Detect screen size
    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDisplayMode;

    // Create a new world where to run the Tanks
    val world = new World(
      max_rounds = 3000,
      arena = Box( Vector2D.new_xy( 0, 0 ), Vector2D.new_xy( gd.getWidth - 60.0, gd.getHeight - 60.0 ) ),
      dead_time = 1.0 / 5.0,
      //dead_time = 1.0 / 3.0,
      max_bullet_speed = 5,
      bullet_life = 20
    )

    // Mutation pipeline
    val pipeline = new EvolutionPipeline[Tank](
      List(
      // A very small mutation from the current values is applied frequently
      new TankDriftMutation(
        new Probability( 0.05 ), new GaussianGenerator( 0, Tank.defaultRange / ( 2.96 * 50.0 ), new MersenneTwisterRNG() )
      ),
      // A less small drift is applied less frequently
      new TankDriftMutation(
        new Probability( 0.005 ), new GaussianGenerator( 0, Tank.defaultRange / ( 2.96 * 5.0 ), new MersenneTwisterRNG() )
      ),
      // Every so and then a value is changed completely
      new TankFullMutation( new Probability( 0.001 ) ),
      // Crossover between tanks
      new TankCrossover( 1, new Probability( 0.005 ) )
    ).asJava
    )

    // Evolutionary engine
    val engine = new TankEvolutionEngine(
      new TankFactory( world ),
      pipeline,
      new TankEvaluator(),
      new RankSelection(),
      new MersenneTwisterRNG()
    )

    engine.addEvolutionObserver( new EvolutionLogger )

    //engine.evolve( 40, 8, new GenerationCount(1000) )
    engine.evolve( 40, 2, new GenerationCount( 1000 ) )

    return
  }

}