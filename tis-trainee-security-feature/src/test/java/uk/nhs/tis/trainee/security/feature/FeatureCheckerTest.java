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
import static org.hamcrest.Matchers.is;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class FeatureCheckerTest {

  private static final String PARENT_FEATURE = "feature1";
  private static final String CHILD_FEATURE = "feature2";
  private static final String NESTED_FEATURE = PARENT_FEATURE + "." + CHILD_FEATURE;

  private static final String FEATURES_CLAIM = "features";
  private static final String ENABLED = "enabled";

  private FeatureChecker checker;

  @BeforeEach
  void setUp() {
    checker = new FeatureChecker();
  }

  @Test
  void shouldReturnFalseWhenNoAuth() {
    boolean hasFeature = checker.hasFeature(null, PARENT_FEATURE);

    assertThat("Unexpected result.", hasFeature, is(false));
  }

  @Test
  void shouldReturnFalseWhenAuthNotJwt() {
    Authentication authentication = new TestingAuthenticationToken(null, null);

    boolean hasFeature = checker.hasFeature(authentication, PARENT_FEATURE);

    assertThat("Unexpected result.", hasFeature, is(false));
  }

  @Test
  void shouldReturnFalseWhenAuthJwtHasNoFeatures() {
    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim("not-features", "")
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    boolean hasFeature = checker.hasFeature(authentication, PARENT_FEATURE);

    assertThat("Unexpected result.", hasFeature, is(false));
  }

  @Test
  void shouldReturnFalseWhenFeatureNotExists() {
    Map<String, Object> features = Map.of(
        "not-feature1", Map.of(ENABLED, true)
    );

    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim(FEATURES_CLAIM, features)
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    boolean hasFeature = checker.hasFeature(authentication, PARENT_FEATURE);

    assertThat("Unexpected result.", hasFeature, is(false));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldReturnFeatureEnabledValueWhenFound(boolean enabled) {
    Map<String, Object> features = Map.of(
        PARENT_FEATURE, Map.of(ENABLED, enabled)
    );

    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim(FEATURES_CLAIM, features)
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    boolean hasFeature = checker.hasFeature(authentication, PARENT_FEATURE);

    assertThat("Unexpected result.", hasFeature, is(enabled));
  }

  @Test
  void shouldReturnFalseWhenParentFeatureDisabledAndNestedFeatureEnabled() {
    Map<String, Object> features = Map.of(
        PARENT_FEATURE, Map.of(
            ENABLED, false,
            CHILD_FEATURE, Map.of(ENABLED, true))
    );

    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim(FEATURES_CLAIM, features)
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    boolean hasParentFeature = checker.hasFeature(authentication, PARENT_FEATURE);
    assertThat("Unexpected result.", hasParentFeature, is(false));

    boolean hasChildFeature = checker.hasFeature(authentication, NESTED_FEATURE);
    assertThat("Unexpected result.", hasChildFeature, is(false));
  }

  @Test
  void shouldReturnFalseWhenParentFeatureEnabledAndNestedFeatureDisabled() {
    Map<String, Object> features = Map.of(
        PARENT_FEATURE, Map.of(
            ENABLED, true,
            CHILD_FEATURE, Map.of(ENABLED, false))
    );

    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim(FEATURES_CLAIM, features)
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    boolean hasParentFeature = checker.hasFeature(authentication, PARENT_FEATURE);
    assertThat("Unexpected result.", hasParentFeature, is(true));

    boolean hasChildFeature = checker.hasFeature(authentication, NESTED_FEATURE);
    assertThat("Unexpected result.", hasChildFeature, is(false));

  }

  @Test
  void shouldReturnTrueWhenParentFeatureEnabledAndNestedFeatureEnabled() {
    Map<String, Object> features = Map.of(
        PARENT_FEATURE, Map.of(
            ENABLED, true,
            CHILD_FEATURE, Map.of(ENABLED, true))
    );

    Jwt jwt = Jwt.withTokenValue("mock-token")
        .header("alg", "none")
        .claim(FEATURES_CLAIM, features)
        .build();
    Authentication authentication = new JwtAuthenticationToken(jwt);

    boolean hasParentFeature = checker.hasFeature(authentication, PARENT_FEATURE);
    assertThat("Unexpected result.", hasParentFeature, is(true));

    boolean hasChildFeature = checker.hasFeature(authentication, NESTED_FEATURE);
    assertThat("Unexpected result.", hasChildFeature, is(true));
  }
}
