/*
 * Copyright (C) 2017 Fabrizio Colonna
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

package com.colofabrix.scala.physix

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig }
import com.typesafe.scalalogging.LazyLogging

/**
  * Verlet integration, position version
  */
abstract class VerletPhysix(

  override val mass: Double,
  private var _position: Vect,
  private var _velocity: Vect,
  private var _angle: Double,
  private var _angularSpeed: Double,
  initialExternalForce: Vect

) extends RigidBody with LazyLogging {

  import com.colofabrix.scala.math.VectUtils._

  /** Position of the object at the last step. */
  final
  protected
  def lastVelocity = _lastVelocity

  private
  var _lastVelocity: Vect = Vect.zero

  override
  def step(walls: Seq[Shape], bodies: Seq[RigidBody], extForces: Vect = Vect.zero): VerletPhysix = ???

  def test(walls: Seq[(Vect, Double)], extForces: Vect): VerletPhysix = {
    this._lastVelocity = this.velocity

    val acceleration = (this.internalForce + extForces).comp(_ / this.mass)
    this._velocity += acceleration * SimConfig.timeStep
    val tmpPosition = this.position + 0.5 * (this.lastVelocity + this.velocity) * SimConfig.timeStep

    for( w <- walls ) {
      val (wNormal, wDistance) = w
      val distance = (tmpPosition ∙ wNormal) + wDistance
      val wVelocity = this.velocity ∙ wNormal

      if( (distance ~< 0.0) && (wVelocity ~< 0.0) ) {
        this._velocity -= 2.0 * wVelocity * wNormal
        logger.info(s"Collision detected with $w and Tank position $tmpPosition. New velocity: $velocity")
      }
    }

    this._position += 0.5 * (this.lastVelocity + this.velocity) * SimConfig.timeStep
    return this
  }

  final
  override
  def position: Vect = _position

  final
  override
  def velocity: Vect = _velocity

  final
  override
  def angle: Double = _angle

  final
  override
  def angularSpeed: Double = _angularSpeed
}
