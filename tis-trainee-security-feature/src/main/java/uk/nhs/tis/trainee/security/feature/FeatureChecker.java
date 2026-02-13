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

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Component that evaluates JWT claims to determine whether a given feature is enabled for the
 * current user.
 *
 * <p>Example usage:</p>
 * <pre>
 * boolean allowed = featureChecker.hasFeature(authentication, "parentFeature.childFeature");
 * </pre>
 *
 * <p>This class is typically used in conjunction with
 * {@link org.springframework.security.access.prepost.PreAuthorize} to provide method-level security
 * based on JWT features.</p>
 *
 * <p>Example usage:</p>
 * <pre>@PreAuthorize("hasFeature('parentFeature.childFeature')")</pre>
 */
public class FeatureChecker {

  /**
   * Determines whether the given feature is enabled for the supplied authentication.
   *
   * @param authentication The authentication to use, must be JWT.
   * @param featurePath    Dot-delimited path to the feature in the JWT claims e.g.
   *                       "parentFeature.childFeature".
   * @return true if the feature is enabled, or false if not found or disabled.
   */
  public boolean hasFeature(Authentication authentication, String featurePath) {
    if (!(authentication instanceof JwtAuthenticationToken jwt)) {
      return false;
    }

    Map<String, Object> features = jwt.getToken().getClaim("features");
    if (features == null) {
      return false;
    }

    return checkFeature(features, featurePath);
  }

  /**
   * Check whether the given feature is enabled in the supplied features map.
   *
   * @param features    The map of features from the JWT claims.
   * @param featurePath The dot-delimited path to the feature in the JWT claims.
   * @return true if the feature is enabled, or false if not found or disabled.
   */
  private boolean checkFeature(Map<String, Object> features, String featurePath) {
    String[] pathSegments = featurePath.split("\\.");
    Map<String, Object> current = features;

    for (String pathSegment : pathSegments) {
      Object value = current.get(pathSegment);

      if (!(value instanceof Map)) {
        return false;
      }

      Map<String, Object> featureMap = (Map<String, Object>) value;

      if (featureMap.get("enabled") instanceof Boolean enabled && !enabled) {
        return false;
      }

      current = featureMap;
    }

    return true;
  }
}
