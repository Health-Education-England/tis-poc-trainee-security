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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.util.SimpleMethodInvocation;

class FeatureMethodSecurityExpressionHandlerTest {

  private FeatureMethodSecurityExpressionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new FeatureMethodSecurityExpressionHandler(new FeatureChecker());
  }

  @Test
  void shouldSetFeatureExpressionAsRoot() throws NoSuchMethodException {
    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("dummy", "claim")
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    MethodInvocation mi = new SimpleMethodInvocation(new Object(),
        Object.class.getMethod("toString"));

    EvaluationContext context = handler.createEvaluationContext(() -> authentication, mi);

    TypedValue root = context.getRootObject();
    assertThat("Unexpected root type.", root.getValue(),
        instanceOf(FeatureSecurityExpressionRoot.class));
  }
}
