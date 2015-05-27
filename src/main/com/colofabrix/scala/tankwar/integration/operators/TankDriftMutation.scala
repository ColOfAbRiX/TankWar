package com.colofabrix.scala.tankwar.integration.operators

import java.util.Random

import com.colofabrix.scala.tankwar.TankChromosome
import org.uncommons.maths.number.NumberGenerator
import org.uncommons.maths.random.Probability

/**
 * A drift mutation means that if a value has to change the new value is
 * a modification of the old one, a drift from it, by a random value generated
 * by a proper distribution generator
 */
class TankDriftMutation(probability: Probability, generator: NumberGenerator[java.lang.Double]) extends TankFullMutation(probability) {

  /**
   * Rules that defines how to mutate
   *
   * This rule works like this: for every value there is a specific `probability` that it mutates.
   * If the value mutates, then the new value a drift from the old value and there are no random
   * possibilities. Usually the new value should have higher probability to fall near the old value
   * with a Gaussian distribution, but this is up to the developer
   *
   * @param scale Range of the random values, in [-scale, scale]
   * @param x Value that possibly mutates
   * @param rng Random number generator
   * @return The old value or a new mutated value
   */
  override def mutationRule(scale: Double)(x: Double, rng: Random) = {
    if( rng.nextDouble <= probability.doubleValue )
      x + generator.nextValue
    else x
  }

  /**
   * Mutate the sight ratio of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new sight ratio with applied the mutation rules
   */
  override def mutateSightRatio(c: TankChromosome, random: Random) = {
    Math.abs(mutationRule(0.998)(c.sightRatio, random)) + 0.001
  }

}
