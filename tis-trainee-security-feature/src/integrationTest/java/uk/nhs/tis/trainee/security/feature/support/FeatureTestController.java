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

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.tis.trainee.security.feature.FeatureChecker;

/**
 * A controller for testing of {@link FeatureChecker}, with various feature scenarios.
 */
@RestController
class FeatureTestController {

  /**
   * An endpoint for testing an "unprotected" endpoint with no required feature flags.
   *
   * @return 200 if successfully called.
   */
  @GetMapping("/unprotected")
  ResponseEntity<Void> unprotected() {
    return ResponseEntity.ok(null);
  }

  /**
   * An endpoint for testing a "protected" endpoint with a single required feature flags. The
   * authenticated user must have the "feature1" flag enabled.
   *
   * @return 200 if successfully called.
   */
  @GetMapping("/protected/feature1")
  @PreAuthorize("hasFeature('feature1')")
  ResponseEntity<Void> protectedFeature1() {
    return ResponseEntity.ok(null);
  }

  /**
   * An endpoint for testing a "protected" endpoint with nested required feature flags. The
   * authenticated user must have both the "feature1" and it's child "feature2" flags enabled.
   *
   * @return 200 if successfully called.
   */
  @GetMapping("/protected/feature1/feature2")
  @PreAuthorize("hasFeature('feature1.feature2')")
  ResponseEntity<Void> protectedNestedFeatures() {
    return ResponseEntity.ok(null);
  }
}
