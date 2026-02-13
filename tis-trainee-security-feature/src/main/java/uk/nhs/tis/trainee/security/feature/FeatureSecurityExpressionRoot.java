/*
 * The MIT License (MIT)
 *
 * Copyright 2026 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package uk.nhs.tis.trainee.security.feature;

import java.util.function.Supplier;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

/**
 * Feature root object for use in Spring Security expression evaluations.
 */
public class FeatureSecurityExpressionRoot extends SecurityExpressionRoot {

  private final FeatureChecker checker;

  /**
   * Construct a root object for use in Spring Security expression evaluations.
   *
   * @param authentication A supplier for the authenticated user.
   * @param checker        The {@link FeatureChecker} to be used.
   */
  public FeatureSecurityExpressionRoot(Supplier<Authentication> authentication,
      FeatureChecker checker) {
    super(authentication);
    this.checker = checker;
  }

  /**
   * Check whether the authenticated user has the correct feature enabled.
   *
   * @param featurePath Dot-delimited path to the feature in the JWT claims e.g.
   *                    "parentFeature.childFeature".
   * @return true if the feature is enabled, or false if not found or disabled.
   */
  public boolean hasFeature(String featurePath) {
    return checker.hasFeature(getAuthentication(), featurePath);
  }
}
