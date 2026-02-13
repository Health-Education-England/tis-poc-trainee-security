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

package uk.nhs.tis.trainee.security.feature.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import uk.nhs.tis.trainee.security.feature.FeatureChecker;
import uk.nhs.tis.trainee.security.feature.FeatureMethodSecurityExpressionHandler;

/**
 * Configuration for integration testing, reflects how a service would config the library.
 */
@Configuration
@EnableMethodSecurity
public class TestSecurityConfiguration {

  /**
   * Create a bean of type {@link FeatureChecker}.
   *
   * @return The created {@code FeatureChecker}.
   */
  @Bean
  public FeatureChecker featureChecker() {
    return new FeatureChecker();
  }

  /**
   * Create a bean of type {@link MethodSecurityExpressionHandler}.
   *
   * @return The created {@link FeatureMethodSecurityExpressionHandler}.
   */
  @Bean
  public MethodSecurityExpressionHandler featureMethodSecurityExpressionHandler(
      FeatureChecker featureChecker) {
    return new FeatureMethodSecurityExpressionHandler(featureChecker);
  }
}
