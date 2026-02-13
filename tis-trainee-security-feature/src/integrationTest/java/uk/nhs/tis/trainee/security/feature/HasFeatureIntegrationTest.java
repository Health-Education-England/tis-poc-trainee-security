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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.tis.trainee.security.feature.support.FeatureTestApplication;

@SpringBootTest(classes = FeatureTestApplication.class)
@AutoConfigureMockMvc
class HasFeatureIntegrationTest {

  private static final String FEATURES_CLAIM = "features";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturn401FromUnprotectedWhenNoToken() throws Exception {
    mockMvc.perform(get("/unprotected"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturn200FromUnprotectedWhenNoFeatures() throws Exception {
    mockMvc.perform(get("/unprotected")
            .with(jwt().jwt(jwt -> jwt.claim(FEATURES_CLAIM, Map.of()))))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/protected/feature1", "/protected/feature1/feature2"})
  void shouldReturn401FromProtectedWhenNoToken(String path) throws Exception {
    mockMvc.perform(get(path))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/protected/feature1", "/protected/feature1/feature2"})
  void shouldReturn403FromProtectedWhenNoFeatures(String path) throws Exception {
    mockMvc.perform(get(path)
            .with(jwt().jwt(jwt -> jwt.claim(FEATURES_CLAIM, Map.of()))))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/protected/feature1", "/protected/feature1/feature2"})
  void shouldReturn403FromFeatureProtectedWhenFeaturesDisabled(String path) throws Exception {
    mockMvc.perform(get(path)
            .with(jwt().jwt(jwt -> jwt.claim(FEATURES_CLAIM, createFeaturesClaim(false, false)))))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldReturn403FromFeatureProtectedWhenParentFeatureDisabled() throws Exception {
    mockMvc.perform(get("/protected/feature1/feature2")
            .with(jwt().jwt(jwt -> jwt.claim(FEATURES_CLAIM, createFeaturesClaim(false, true)))))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/protected/feature1", "/protected/feature1/feature2"})
  void shouldReturn200FromFeatureProtectedWhenFeaturesEnabled(String path) throws Exception {
    mockMvc.perform(get(path)
            .with(jwt().jwt(jwt -> jwt.claim(FEATURES_CLAIM, createFeaturesClaim(true, true)))))
        .andExpect(status().isOk());
  }

  /**
   * Create an example features claim map for use with JWT.
   *
   * @param feature1Enabled Whether feature1 should be enabled.
   * @param feature2Enabled Whether feature1.feature2 should be enabled.
   * @return The map to assign to "features" JWT claim.
   */
  private Map<String, Object> createFeaturesClaim(boolean feature1Enabled,
      boolean feature2Enabled) {
    return Map.of(
        "feature1", Map.of(
            "enabled", feature1Enabled,
            "feature2", Map.of(
                "enabled", feature2Enabled
            )
        )
    );
  }
}
