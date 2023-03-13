package de.rieckpil.courses.book.review;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static de.rieckpil.courses.book.review.RandomReviewParameterResolverExtension.RandomReview;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomReviewParameterResolverExtension.class)
class ReviewVerifierTest {

  private ReviewVerifier reviewVerifier;

  @BeforeEach
  void setup() {
    reviewVerifier = new ReviewVerifier();
  }

  @Test
  @DisplayName("Should fail when review contains swear word")
  void shouldFailWhenReviewContainsSwearWord() {

    //Given
    String review = "This book is shit";

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    assertFalse(result, "ReviewVerifier did not detect swear word");

  }

  @Test
  @DisplayName("Should fail when review contains 'lorem ipsum'")
  void shouldFailWhenReviewContainsLoremIpsum() {

    //Given
    String review = "lorem ipsum";

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    assertFalse(result, "ReviewVerifier did not detect 'lorem ipsum'");

  }

  @ParameterizedTest
  @CsvFileSource(resources = "/badReview.csv")
  @DisplayName("Should fail when review is of bad quality")
  void shouldFailWhenReviewIsOfBadQuality(String review) {

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    assertFalse(result, "ReviewVerifier passed a bad quality review");

  }

  @RepeatedTest(5)
  @DisplayName("Should fail when random review quality is bad")
  void shouldFailWhenRandomReviewQualityIsBad(@RandomReview String review) {

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    assertFalse(result, "ReviewVerifier passed a bad quality review");

  }

  @Test
  @DisplayName("Should pass when review is good")
  void shouldPassWhenReviewIsGood() {

    //Given
    String review = "I can totally recommend this book " +
      "who is interested in learning how to write Java code!";

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    assertTrue(result, "ReviewVerifier did not pass a good review");

  }

  @Test
  @DisplayName("Should pass when review is good Hamcrest")
  void shouldPassWhenReviewIsGoodHamcrest() {

    //Given
    String review = "I can totally recommend this book " +
      "who is interested in learning how to write Java code!";

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    assertThat("ReviewVerifier did not pass a good review", result, equalTo(true));

  }

  @Test
  @DisplayName("Should pass when review is good AssertJ")
  void shouldPassWhenReviewIsGoodAssertJ() {

    //Given
    String review = "I can totally recommend this book " +
      "who is interested in learning how to write Java code!";

    //When
    boolean result = reviewVerifier.doesMeetQualityStandards(review);

    //Then
    Assertions.assertThat(result)
              .withFailMessage("ReviewVerifier did not pass a good review")
              .isTrue();

  }

}
