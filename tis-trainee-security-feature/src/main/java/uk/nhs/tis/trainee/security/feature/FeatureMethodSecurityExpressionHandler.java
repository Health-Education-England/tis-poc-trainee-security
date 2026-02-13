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
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

/**
 * A Feature-based implementation of MethodSecurityExpressionHandler.
 */
public class FeatureMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

  private final FeatureChecker featureChecker;

  /**
   * Create an instance of a Feature-based MethodSecurityExpressionHandler.
   *
   * @param featureChecker The {@link FeatureChecker} to be used.
   */
  public FeatureMethodSecurityExpressionHandler(FeatureChecker featureChecker) {
    this.featureChecker = featureChecker;
  }

  @Override
  public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication,
      MethodInvocation mi) {
    StandardEvaluationContext context = (StandardEvaluationContext) super.createEvaluationContext(
        authentication, mi);
    FeatureSecurityExpressionRoot root = new FeatureSecurityExpressionRoot(authentication,
        featureChecker);
    context.setRootObject(root);
    return context;
  }
}
