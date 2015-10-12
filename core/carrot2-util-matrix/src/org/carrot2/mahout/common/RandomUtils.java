/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.carrot2.mahout.common;



/**
 * <p>
 * The source of random stuff for the whole project. This lets us make all randomness in the project
 * predictable, if desired, for when we run unit tests, which should be repeatable.
 * </p>
 * 
 * <p>
 * This class is increasingly incorrectly named as it also includes other mathematical utility methods.
 * </p>
 */
public final class RandomUtils {

  private RandomUtils() { }
  
  /** @return what {@link Double#hashCode()} would return for the same value */
  public static int hashDouble(double value) {
    long v = Double.doubleToLongBits(value);
    return (int) (v ^ (v >>> 32));
  }

  /** @return what {@link Float#hashCode()} would return for the same value */
  public static int hashFloat(float value) {
    return Float.floatToIntBits(value);
  }
  
}
