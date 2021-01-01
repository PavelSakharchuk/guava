/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.NullPointerTester;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import junit.framework.TestCase;

/** @author Julien Silland */
@GwtCompatible(emulated = true)
public class SplitterTest extends TestCase {

  private static final Splitter COMMA_SPLITTER = Splitter.on(',');

  public void testSplitNullString() {
    try {
      COMMA_SPLITTER.split(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testCharacterSimpleSplit() {
    String simple = "a,b,c";
    Iterable<String> letters = COMMA_SPLITTER.split(simple);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  /**
   * All of the infrastructure of split and splitToString is identical, so we do one test of
   * splitToString. All other cases should be covered by testing of split.
   *
   * <p>TODO(user): It would be good to make all the relevant tests run on both split and
   * splitToString automatically.
   */
  public void testCharacterSimpleSplitToList() {
    String simple = "a,b,c";
    List<String> letters = COMMA_SPLITTER.splitToList(simple);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  public void testCharacterSimpleSplitToStream() {
    String simple = "a,b,c";
    List<String> letters = COMMA_SPLITTER.splitToStream(simple).collect(toImmutableList());
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  public void testToString() {
    assertEquals("[]", COMMA_SPLITTER.split("").toString());
    assertEquals("[a, b, c]", COMMA_SPLITTER.split("a,b,c").toString());
    assertEquals("[yam, bam, jam, ham]", Splitter.on(", ").split("yam, bam, jam, ham").toString());
  }

  public void testCharacterSimpleSplitWithNoDelimiter() {
    String simple = "a,b,c";
    Iterable<String> letters = Splitter.on('.').split(simple);
    assertThat(letters).containsExactly("a,b,c").inOrder();
  }

  public void testCharacterSplitWithDoubleDelimiter() {
    String doubled = "a,,b,c";
    Iterable<String> letters = COMMA_SPLITTER.split(doubled);
    assertThat(letters).containsExactly("a", "", "b", "c").inOrder();
  }

  public void testCharacterSplitWithDoubleDelimiterAndSpace() {
    String doubled = "a,, b,c";
    Iterable<String> letters = COMMA_SPLITTER.split(doubled);
    assertThat(letters).containsExactly("a", "", " b", "c").inOrder();
  }

  public void testCharacterSplitWithTrailingDelimiter() {
    String trailing = "a,b,c,";
    Iterable<String> letters = COMMA_SPLITTER.split(trailing);
    assertThat(letters).containsExactly("a", "b", "c", "").inOrder();
  }

  public void testCharacterSplitWithLeadingDelimiter() {
    String leading = ",a,b,c";
    Iterable<String> letters = COMMA_SPLITTER.split(leading);
    assertThat(letters).containsExactly("", "a", "b", "c").inOrder();
  }

  public void testCharacterSplitWithMultipleLetters() {
    Iterable<String> testCharacteringMotto =
        Splitter.on('-').split("Testing-rocks-Debugging-sucks");
    assertThat(testCharacteringMotto)
        .containsExactly("Testing", "rocks", "Debugging", "sucks")
        .inOrder();
  }

  public void testCharacterSplitWithMatcherDelimiter() {
    Iterable<String> testCharacteringMotto =
        Splitter.on(CharMatcher.whitespace()).split("Testing\nrocks\tDebugging sucks");
    assertThat(testCharacteringMotto)
        .containsExactly("Testing", "rocks", "Debugging", "sucks")
        .inOrder();
  }

  public void testCharacterSplitWithDoubleDelimiterOmitEmptyStrings() {
    String doubled = "a..b.c";
    Iterable<String> letters = Splitter.on('.').omitEmptyStrings().split(doubled);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  public void testCharacterSplitEmptyToken() {
    String emptyToken = "a. .c";
    Iterable<String> letters = Splitter.on('.').trimResults().split(emptyToken);
    assertThat(letters).containsExactly("a", "", "c").inOrder();
  }

  public void testCharacterSplitEmptyTokenOmitEmptyStrings() {
    String emptyToken = "a. .c";
    Iterable<String> letters = Splitter.on('.').omitEmptyStrings().trimResults().split(emptyToken);
    assertThat(letters).containsExactly("a", "c").inOrder();
  }

  public void testCharacterSplitOnEmptyString() {
    Iterable<String> nothing = Splitter.on('.').split("");
    assertThat(nothing).containsExactly("").inOrder();
  }

  public void testCharacterSplitOnEmptyStringOmitEmptyStrings() {
    assertThat(Splitter.on('.').omitEmptyStrings().split("")).isEmpty();
  }

  public void testCharacterSplitOnOnlyDelimiter() {
    Iterable<String> blankblank = Splitter.on('.').split(".");
    assertThat(blankblank).containsExactly("", "").inOrder();
  }

  public void testCharacterSplitOnOnlyDelimitersOmitEmptyStrings() {
    Iterable<String> empty = Splitter.on('.').omitEmptyStrings().split("...");
    assertThat(empty).isEmpty();
  }

  public void testCharacterSplitWithTrim() {
    String jacksons =
        "arfo(Marlon)aorf, (Michael)orfa, afro(Jackie)orfa, " + "ofar(Jemaine), aff(Tito)";
    Iterable<String> family =
        COMMA_SPLITTER
            .trimResults(CharMatcher.anyOf("afro").or(CharMatcher.whitespace()))
            .split(jacksons);
    assertThat(family)
        .containsExactly("(Marlon)", "(Michael)", "(Jackie)", "(Jemaine)", "(Tito)")
        .inOrder();
  }

  public void testStringSimpleSplit() {
    String simple = "a,b,c";
    Iterable<String> letters = Splitter.on(",").split(simple);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  public void testStringSimpleSplitWithNoDelimiter() {
    String simple = "a,b,c";
    Iterable<String> letters = Splitter.on(".").split(simple);
    assertThat(letters).containsExactly("a,b,c").inOrder();
  }

  public void testStringSplitWithDoubleDelimiter() {
    String doubled = "a,,b,c";
    Iterable<String> letters = Splitter.on(",").split(doubled);
    assertThat(letters).containsExactly("a", "", "b", "c").inOrder();
  }

  public void testStringSplitWithDoubleDelimiterAndSpace() {
    String doubled = "a,, b,c";
    Iterable<String> letters = Splitter.on(",").split(doubled);
    assertThat(letters).containsExactly("a", "", " b", "c").inOrder();
  }

  public void testStringSplitWithTrailingDelimiter() {
    String trailing = "a,b,c,";
    Iterable<String> letters = Splitter.on(",").split(trailing);
    assertThat(letters).containsExactly("a", "b", "c", "").inOrder();
  }

  public void testStringSplitWithLeadingDelimiter() {
    String leading = ",a,b,c";
    Iterable<String> letters = Splitter.on(",").split(leading);
    assertThat(letters).containsExactly("", "a", "b", "c").inOrder();
  }

  public void testStringSplitWithMultipleLetters() {
    Iterable<String> testStringingMotto = Splitter.on("-").split("Testing-rocks-Debugging-sucks");
    assertThat(testStringingMotto)
        .containsExactly("Testing", "rocks", "Debugging", "sucks")
        .inOrder();
  }

  public void testStringSplitWithDoubleDelimiterOmitEmptyStrings() {
    String doubled = "a..b.c";
    Iterable<String> letters = Splitter.on(".").omitEmptyStrings().split(doubled);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  public void testStringSplitEmptyToken() {
    String emptyToken = "a. .c";
    Iterable<String> letters = Splitter.on(".").trimResults().split(emptyToken);
    assertThat(letters).containsExactly("a", "", "c").inOrder();
  }

  public void testStringSplitEmptyTokenOmitEmptyStrings() {
    String emptyToken = "a. .c";
    Iterable<String> letters = Splitter.on(".").omitEmptyStrings().trimResults().split(emptyToken);
    assertThat(letters).containsExactly("a", "c").inOrder();
  }

  public void testStringSplitWithLongDelimiter() {
    String longDelimiter = "a, b, c";
    Iterable<String> letters = Splitter.on(", ").split(longDelimiter);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  public void testStringSplitWithLongLeadingDelimiter() {
    String longDelimiter = ", a, b, c";
    Iterable<String> letters = Splitter.on(", ").split(longDelimiter);
    assertThat(letters).containsExactly("", "a", "b", "c").inOrder();
  }

  public void testStringSplitWithLongTrailingDelimiter() {
    String longDelimiter = "a, b, c, ";
    Iterable<String> letters = Splitter.on(", ").split(longDelimiter);
    assertThat(letters).containsExactly("a", "b", "c", "").inOrder();
  }

  public void testStringSplitWithDelimiterSubstringInValue() {
    String fourCommasAndFourSpaces = ",,,,    ";
    Iterable<String> threeCommasThenThreeSpaces = Splitter.on(", ").split(fourCommasAndFourSpaces);
    assertThat(threeCommasThenThreeSpaces).containsExactly(",,,", "   ").inOrder();
  }

  public void testStringSplitWithEmptyString() {
    try {
      Splitter.on("");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testStringSplitOnEmptyString() {
    Iterable<String> notMuch = Splitter.on(".").split("");
    assertThat(notMuch).containsExactly("").inOrder();
  }

  public void testStringSplitOnEmptyStringOmitEmptyString() {
    assertThat(Splitter.on(".").omitEmptyStrings().split("")).isEmpty();
  }

  public void testStringSplitOnOnlyDelimiter() {
    Iterable<String> blankblank = Splitter.on(".").split(".");
    assertThat(blankblank).containsExactly("", "").inOrder();
  }

  public void testStringSplitOnOnlyDelimitersOmitEmptyStrings() {
    Iterable<String> empty = Splitter.on(".").omitEmptyStrings().split("...");
    assertThat(empty).isEmpty();
  }

  public void testStringSplitWithTrim() {
    String jacksons =
        "arfo(Marlon)aorf, (Michael)orfa, afro(Jackie)orfa, " + "ofar(Jemaine), aff(Tito)";
    Iterable<String> family =
        Splitter.on(",")
            .trimResults(CharMatcher.anyOf("afro").or(CharMatcher.whitespace()))
            .split(jacksons);
    assertThat(family)
        .containsExactly("(Marlon)", "(Michael)", "(Jackie)", "(Jemaine)", "(Tito)")
        .inOrder();
  }

  @GwtIncompatible // Splitter.onPattern
  public void testPatternSimpleSplit() {
    String simple = "a,b,c";
    Iterable<String> letters = Splitter.onPattern(",").split(simple);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  @GwtIncompatible // Splitter.onPattern
  public void testPatternSimpleSplitWithNoDelimiter() {
    String simple = "a,b,c";
    Iterable<String> letters = Splitter.onPattern("foo").split(simple);
    assertThat(letters).containsExactly("a,b,c").inOrder();
  }

  @GwtIncompatible // Splitter.onPattern
  public void testPatternSplitWithDoubleDelimiter() {
    String doubled = "a,,b,c";
    Iterable<String> letters = Splitter.onPattern(",").split(doubled);
    assertThat(letters).containsExactly("a", "", "b", "c").inOrder();
  }

  @GwtIncompatible // Splitter.onPattern
  public void testPatternSplitWithDoubleDelimiterAndSpace() {
    String doubled = "a,, b,c";
    Iterable<String> letters = Splitter.onPattern(",").split(doubled);
    assertThat(letters).containsExactly("a", "", " b", "c").inOrder();
  }

  @GwtIncompatible // Splitter.onPattern
  public void testPatternSplitWithTrailingDelimiter() {
    String trailing = "a,b,c,";
    Iterable<String> letters = Splitter.onPattern(",").split(trailing);
    assertThat(letters).containsExactly("a", "b", "c", "").inOrder();
  }

  @GwtIncompatible // Splitter.onPattern
  public void testPatternSplitWithLeadingDelimiter() {
    String leading = ",a,b,c";
    Iterable<String> letters = Splitter.onPattern(",").split(leading);
    assertThat(letters).containsExactly("", "a", "b", "c").inOrder();
  }

  // TODO(kevinb): the name of this method suggests it might not actually be testing what it
  // intends to be testing?
  @GwtIncompatible // Splitter.onPattern
  public void testPatternSplitWithMultipleLetters() {
    Iterable<String> testPatterningMotto =
        Splitter.onPattern("-").split("Testing-rocks-Debugging-sucks");
    assertThat(testPatterningMotto)
        .containsExactly("Testing", "rocks", "Debugging", "sucks")
        .inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  private static Pattern literalDotPattern() {
    return Pattern.compile("\\.");
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitWithDoubleDelimiterOmitEmptyStrings() {
    String doubled = "a..b.c";
    Iterable<String> letters = Splitter.on(literalDotPattern()).omitEmptyStrings().split(doubled);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  @AndroidIncompatible // Bug in older versions of Android we test against, since fixed.
  public void testPatternSplitLookBehind() {
    if (!CommonPattern.isPcreLike()) {
      return;
    }
    String toSplit = ":foo::barbaz:";
    String regexPattern = "(?<=:)";
    Iterable<String> split = Splitter.onPattern(regexPattern).split(toSplit);
    assertThat(split).containsExactly(":", "foo:", ":", "barbaz:").inOrder();
    // splits into chunks ending in :
  }

  @GwtIncompatible // java.util.regex.Pattern
  @AndroidIncompatible // Bug in older versions of Android we test against, since fixed.
  public void testPatternSplitWordBoundary() {
    String string = "foo<bar>bletch";
    Iterable<String> words = Splitter.on(Pattern.compile("\\b")).split(string);
    assertThat(words).containsExactly("foo", "<", "bar", ">", "bletch").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitWordBoundary_singleCharInput() {
    String string = "f";
    Iterable<String> words = Splitter.on(Pattern.compile("\\b")).split(string);
    assertThat(words).containsExactly("f").inOrder();
  }

  @AndroidIncompatible // Apparently Gingerbread's regex API is buggy.
  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitWordBoundary_singleWordInput() {
    String string = "foo";
    Iterable<String> words = Splitter.on(Pattern.compile("\\b")).split(string);
    assertThat(words).containsExactly("foo").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitEmptyToken() {
    String emptyToken = "a. .c";
    Iterable<String> letters = Splitter.on(literalDotPattern()).trimResults().split(emptyToken);
    assertThat(letters).containsExactly("a", "", "c").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitEmptyTokenOmitEmptyStrings() {
    String emptyToken = "a. .c";
    Iterable<String> letters =
        Splitter.on(literalDotPattern()).omitEmptyStrings().trimResults().split(emptyToken);
    assertThat(letters).containsExactly("a", "c").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitOnOnlyDelimiter() {
    Iterable<String> blankblank = Splitter.on(literalDotPattern()).split(".");

    assertThat(blankblank).containsExactly("", "").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitOnOnlyDelimitersOmitEmptyStrings() {
    Iterable<String> empty = Splitter.on(literalDotPattern()).omitEmptyStrings().split("...");
    assertThat(empty).isEmpty();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitMatchingIsGreedy() {
    String longDelimiter = "a, b,   c";
    Iterable<String> letters = Splitter.on(Pattern.compile(",\\s*")).split(longDelimiter);
    assertThat(letters).containsExactly("a", "b", "c").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitWithLongLeadingDelimiter() {
    String longDelimiter = ", a, b, c";
    Iterable<String> letters = Splitter.on(Pattern.compile(", ")).split(longDelimiter);
    assertThat(letters).containsExactly("", "a", "b", "c").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitWithLongTrailingDelimiter() {
    String longDelimiter = "a, b, c/ ";
    Iterable<String> letters = Splitter.on(Pattern.compile("[,/]\\s")).split(longDelimiter);
    assertThat(letters).containsExactly("a", "b", "c", "").inOrder();
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitInvalidPattern() {
    try {
      Splitter.on(Pattern.compile("a*"));
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testPatternSplitWithTrim() {
    String jacksons =
        "arfo(Marlon)aorf, (Michael)orfa, afro(Jackie)orfa, " + "ofar(Jemaine), aff(Tito)";
    Iterable<String> family =
        Splitter.on(Pattern.compile(","))
            .trimResults(CharMatcher.anyOf("afro").or(CharMatcher.whitespace()))
            .split(jacksons);
    assertThat(family)
        .containsExactly("(Marlon)", "(Michael)", "(Jackie)", "(Jemaine)", "(Tito)")
        .inOrder();
  }

  public void testSplitterIterableIsUnmodifiable_char() {
    assertIteratorIsUnmodifiable(COMMA_SPLITTER.split("a,b").iterator());
  }

  public void testSplitterIterableIsUnmodifiable_string() {
    assertIteratorIsUnmodifiable(Splitter.on(",").split("a,b").iterator());
  }

  @GwtIncompatible // java.util.regex.Pattern
  public void testSplitterIterableIsUnmodifiable_pattern() {
    assertIteratorIsUnmodifiable(Splitter.on(Pattern.compile(",")).split("a,b").iterator());
  }

  private void assertIteratorIsUnmodifiable(Iterator<?> iterator) {
    iterator.next();
    try {
      iterator.remove();
      fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  public void testSplitterIterableIsLazy_char() {
    assertSplitterIterableIsLazy(COMMA_SPLITTER);
  }

  public void testSplitterIterableIsLazy_string() {
    assertSplitterIterableIsLazy(Splitter.on(","));
  }

  @GwtIncompatible // java.util.regex.Pattern
  @AndroidIncompatible // not clear that j.u.r.Matcher promises to handle mutations during use
  public void testSplitterIterableIsLazy_pattern() {
    if (!CommonPattern.isPcreLike()) {
      return;
    }
    assertSplitterIterableIsLazy(Splitter.onPattern(","));
  }

  /**
   * This test really pushes the boundaries of what we support. In general the splitter's behaviour
   * is not well defined if the char sequence it's splitting is mutated during iteration.
   */
  private void assertSplitterIterableIsLazy(Splitter splitter) {
    StringBuilder builder = new StringBuilder();
    Iterator<String> iterator = splitter.split(builder).iterator();

    builder.append("A,");
    assertEquals("A", iterator.next());
    builder.append("B,");
    assertEquals("B", iterator.next());
    builder.append("C");
    assertEquals("C", iterator.next());
    assertFalse(iterator.hasNext());
  }

  public void testFixedLengthSimpleSplit() {
    String simple = "abcde";
    Iterable<String> letters = Splitter.fixedLength(2).split(simple);
    assertThat(letters).containsExactly("ab", "cd", "e").inOrder();
  }

  public void testFixedLengthSplitEqualChunkLength() {
    String simple = "abcdef";
    Iterable<String> letters = Splitter.fixedLength(2).split(simple);
    assertThat(letters).containsExactly("ab", "cd", "ef").inOrder();
  }

  public void testFixedLengthSplitOnlyOneChunk() {
    String simple = "abc";
    Iterable<String> letters = Splitter.fixedLength(3).split(simple);
    assertThat(letters).containsExactly("abc").inOrder();
  }

  public void testFixedLengthSplitSmallerString() {
    String simple = "ab";
    Iterable<String> letters = Splitter.fixedLength(3).split(simple);
    assertThat(letters).containsExactly("ab").inOrder();
  }

  public void testFixedLengthSplitEmptyString() {
    String simple = "";
    Iterable<String> letters = Splitter.fixedLength(3).split(simple);
    assertThat(letters).containsExactly("").inOrder();
  }

  public void testFixedLengthSplitEmptyStringWithOmitEmptyStrings() {
    assertThat(Splitter.fixedLength(3).omitEmptyStrings().split("")).isEmpty();
  }

  public void testFixedLengthSplitIntoChars() {
    String simple = "abcd";
    Iterable<String> letters = Splitter.fixedLength(1).split(simple);
    assertThat(letters).containsExactly("a", "b", "c", "d").inOrder();
  }

  public void testFixedLengthSplitZeroChunkLen() {
    try {
      Splitter.fixedLength(0);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testFixedLengthSplitNegativeChunkLen() {
    try {
      Splitter.fixedLength(-1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testLimitLarge() {
    String simple = "abcd";
    Iterable<String> letters = Splitter.fixedLength(1).limit(100).split(simple);
    assertThat(letters).containsExactly("a", "b", "c", "d").inOrder();
  }

  public void testLimitOne() {
    String simple = "abcd";
    Iterable<String> letters = Splitter.fixedLength(1).limit(1).split(simple);
    assertThat(letters).containsExactly("abcd").inOrder();
  }

  public void testLimitFixedLength() {
    String simple = "abcd";
    Iterable<String> letters = Splitter.fixedLength(1).limit(2).split(simple);
    assertThat(letters).containsExactly("a", "bcd").inOrder();
  }

  public void testLimit1Separator() {
    String simple = "a,b,c,d";
    Iterable<String> items = COMMA_SPLITTER.limit(1).split(simple);
    assertThat(items).containsExactly("a,b,c,d").inOrder();
  }

  public void testLimitSeparator() {
    String simple = "a,b,c,d";
    Iterable<String> items = COMMA_SPLITTER.limit(2).split(simple);
    assertThat(items).containsExactly("a", "b,c,d").inOrder();
  }

  public void testLimitExtraSeparators() {
    String text = "a,,,b,,c,d";
    Iterable<String> items = COMMA_SPLITTER.limit(2).split(text);
    assertThat(items).containsExactly("a", ",,b,,c,d").inOrder();
  }

  public void testLimitExtraSeparatorsOmitEmpty() {
    String text = "a,,,b,,c,d";
    Iterable<String> items = COMMA_SPLITTER.limit(2).omitEmptyStrings().split(text);
    assertThat(items).containsExactly("a", "b,,c,d").inOrder();
  }

  public void testLimitExtraSeparatorsOmitEmpty3() {
    String text = "a,,,b,,c,d";
    Iterable<String> items = COMMA_SPLITTER.limit(3).omitEmptyStrings().split(text);
    assertThat(items).containsExactly("a", "b", "c,d").inOrder();
  }

  public void testLimitExtraSeparatorsTrim() {
    String text = ",,a,,  , b ,, c,d ";
    Iterable<String> items = COMMA_SPLITTER.limit(2).omitEmptyStrings().trimResults().split(text);
    assertThat(items).containsExactly("a", "b ,, c,d").inOrder();
  }

  public void testLimitExtraSeparatorsTrim3() {
    String text = ",,a,,  , b ,, c,d ";
    Iterable<String> items = COMMA_SPLITTER.limit(3).omitEmptyStrings().trimResults().split(text);
    assertThat(items).containsExactly("a", "b", "c,d").inOrder();
  }

  public void testLimitExtraSeparatorsTrim1() {
    String text = ",,a,,  , b ,, c,d ";
    Iterable<String> items = COMMA_SPLITTER.limit(1).omitEmptyStrings().trimResults().split(text);
    assertThat(items).containsExactly("a,,  , b ,, c,d").inOrder();
  }

  public void testLimitExtraSeparatorsTrim1NoOmit() {
    String text = ",,a,,  , b ,, c,d ";
    Iterable<String> items = COMMA_SPLITTER.limit(1).trimResults().split(text);
    assertThat(items).containsExactly(",,a,,  , b ,, c,d").inOrder();
  }

  public void testLimitExtraSeparatorsTrim1Empty() {
    String text = "";
    Iterable<String> items = COMMA_SPLITTER.limit(1).split(text);
    assertThat(items).containsExactly("").inOrder();
  }

  public void testLimitExtraSeparatorsTrim1EmptyOmit() {
    String text = "";
    Iterable<String> items = COMMA_SPLITTER.omitEmptyStrings().limit(1).split(text);
    assertThat(items).isEmpty();
  }

  public void testInvalidZeroLimit() {
    try {
      COMMA_SPLITTER.limit(0);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @GwtIncompatible // NullPointerTester
  public void testNullPointers() {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Splitter.class);
    tester.testAllPublicInstanceMethods(COMMA_SPLITTER);
    tester.testAllPublicInstanceMethods(COMMA_SPLITTER.trimResults());
  }

  public void testMapSplitter_trimmedBoth() {
    Map<String, String> m =
        COMMA_SPLITTER
            .trimResults()
            .withKeyValueSeparator(Splitter.on(':').trimResults())
            .split("boy  : tom , girl: tina , cat  : kitty , dog: tommy ");
    ImmutableMap<String, String> expected =
        ImmutableMap.of("boy", "tom", "girl", "tina", "cat", "kitty", "dog", "tommy");
    assertThat(m).isEqualTo(expected);
    assertThat(m.entrySet()).containsExactlyElementsIn(expected.entrySet()).inOrder();
  }

  public void testMapSplitter_trimmedEntries() {
    Map<String, String> m =
        COMMA_SPLITTER
            .trimResults()
            .withKeyValueSeparator(":")
            .split("boy  : tom , girl: tina , cat  : kitty , dog: tommy ");
    ImmutableMap<String, String> expected =
        ImmutableMap.of("boy  ", " tom", "girl", " tina", "cat  ", " kitty", "dog", " tommy");

    assertThat(m).isEqualTo(expected);
    assertThat(m.entrySet()).containsExactlyElementsIn(expected.entrySet()).inOrder();
  }

  public void testMapSplitter_trimmedKeyValue() {
    Map<String, String> m =
        COMMA_SPLITTER
            .withKeyValueSeparator(Splitter.on(':').trimResults())
            .split("boy  : tom , girl: tina , cat  : kitty , dog: tommy ");
    ImmutableMap<String, String> expected =
        ImmutableMap.of("boy", "tom", "girl", "tina", "cat", "kitty", "dog", "tommy");
    assertThat(m).isEqualTo(expected);
    assertThat(m.entrySet()).containsExactlyElementsIn(expected.entrySet()).inOrder();
  }

  public void testMapSplitter_notTrimmed() {
    Map<String, String> m =
        COMMA_SPLITTER
            .withKeyValueSeparator(":")
            .split(" boy:tom , girl: tina , cat :kitty , dog:  tommy ");
    ImmutableMap<String, String> expected =
        ImmutableMap.of(" boy", "tom ", " girl", " tina ", " cat ", "kitty ", " dog", "  tommy ");
    assertThat(m).isEqualTo(expected);
    assertThat(m.entrySet()).containsExactlyElementsIn(expected.entrySet()).inOrder();
  }

  public void testMapSplitter_CharacterSeparator() {
    // try different delimiters.
    Map<String, String> m =
        Splitter.on(",").withKeyValueSeparator(':').split("boy:tom,girl:tina,cat:kitty,dog:tommy");
    ImmutableMap<String, String> expected =
        ImmutableMap.of("boy", "tom", "girl", "tina", "cat", "kitty", "dog", "tommy");

    assertThat(m).isEqualTo(expected);
    assertThat(m.entrySet()).containsExactlyElementsIn(expected.entrySet()).inOrder();
  }

  public void testMapSplitter_multiCharacterSeparator() {
    // try different delimiters.
    Map<String, String> m =
        Splitter.on(",")
            .withKeyValueSeparator(":^&")
            .split("boy:^&tom,girl:^&tina,cat:^&kitty,dog:^&tommy");
    ImmutableMap<String, String> expected =
        ImmutableMap.of("boy", "tom", "girl", "tina", "cat", "kitty", "dog", "tommy");

    assertThat(m).isEqualTo(expected);
    assertThat(m.entrySet()).containsExactlyElementsIn(expected.entrySet()).inOrder();
  }

  public void testMapSplitter_emptySeparator() {
    try {
      COMMA_SPLITTER.withKeyValueSeparator("");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testMapSplitter_malformedEntry() {
    try {
      COMMA_SPLITTER.withKeyValueSeparator("=").split("a=1,b,c=2");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  /**
   * Testing the behavior in https://github.com/google/guava/issues/1900 - this behavior may want to
   * be changed?
   */
  public void testMapSplitter_extraValueDelimiter() {
    try {
      COMMA_SPLITTER.withKeyValueSeparator("=").split("a=1,c=2=");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testMapSplitter_orderedResults() {
    Map<String, String> m =
        COMMA_SPLITTER.withKeyValueSeparator(":").split("boy:tom,girl:tina,cat:kitty,dog:tommy");

    assertThat(m.keySet()).containsExactly("boy", "girl", "cat", "dog").inOrder();
    assertThat(m)
        .isEqualTo(ImmutableMap.of("boy", "tom", "girl", "tina", "cat", "kitty", "dog", "tommy"));

    // try in a different order
    m = COMMA_SPLITTER.withKeyValueSeparator(":").split("girl:tina,boy:tom,dog:tommy,cat:kitty");

    assertThat(m.keySet()).containsExactly("girl", "boy", "dog", "cat").inOrder();
    assertThat(m)
        .isEqualTo(ImmutableMap.of("boy", "tom", "girl", "tina", "cat", "kitty", "dog", "tommy"));
  }

  public void testMapSplitter_duplicateKeys() {
    try {
      COMMA_SPLITTER.withKeyValueSeparator(":").split("a:1,b:2,a:3");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testMapSplitter_varyingTrimLevels() {
    MapSplitter splitter = COMMA_SPLITTER.trimResults().withKeyValueSeparator(Splitter.on("->"));
    Map<String, String> split = splitter.split(" x -> y, z-> a ");
    assertThat(split).containsEntry("x ", " y");
    assertThat(split).containsEntry("z", " a");
  }

  public void testSplitterSkipJson() {
    String scriptData = "window._sharedData={\"config\":{\"csrf_token\":\"xtLgg2v5dprbhtD3M73RycGeL4m2EFun\",\"viewer\":null,\"viewerId\":null},\"country_code\":\"BY\",\"language_code\":\"ja\",\"locale\":\"ja_JP\",\"entry_data\":{\"LandingPage\":[{\"captcha\":{\"enabled\":false,\"key\":\"\"},\"hsite_redirect_url\":\"\",\"prefill_phone_number\":\"\",\"gdpr_required\":false,\"tos_version\":\"row\",\"sideload_url\":null,\"seo_category_infos\":[[\"\\u7f8e\\u5bb9\",\"beauty\"],[\"\\u30c0\\u30f3\\u30b9\\u30fb\\u30d1\\u30d5\\u30a9\\u30fc\\u30de\\u30f3\\u30b9\",\"dance_and_performance\"],[\"\\u30d5\\u30a3\\u30c3\\u30c8\\u30cd\\u30b9\",\"fitness\"],[\"\\u30d5\\u30fc\\u30c9\\u30fb\\u30c9\\u30ea\\u30f3\\u30af\",\"food_and_drink\"],[\"\\u30db\\u30fc\\u30e0\\u30fb\\u30ac\\u30fc\\u30c7\\u30cb\\u30f3\\u30b0\",\"home_and_garden\"],[\"\\u97f3\\u697d\",\"music\"],[\"\\u8996\\u899a\\u82b8\\u8853\",\"visual_arts\"]]}]},\"hostname\":\"www.instagram.com\",\"is_whitelisted_crawl_bot\":false,\"connection_quality_rating\":\"EXCELLENT\",\"deployment_stage\":\"c2\",\"platform\":\"windows_nt_10\",\"nonce\":\"9e8zpJLyVt8SUkw4hR0rYw==\",\"mid_pct\":91.86913,\"zero_data\":{},\"cache_schema_version\":3,\"server_checks\":{},\"knobx\":{\"17\":false,\"20\":true,\"22\":true,\"23\":true,\"24\":true,\"25\":true,\"26\":true,\"27\":true,\"29\":true,\"32\":true,\"34\":true,\"35\":false,\"38\":25000,\"39\":true,\"4\":false,\"40\":false},\"to_cache\":{\"gatekeepers\":{\"10\":false,\"100\":false,\"101\":true,\"102\":true,\"103\":true,\"104\":true,\"105\":true,\"106\":true,\"107\":false,\"108\":true,\"11\":false,\"112\":true,\"113\":true,\"114\":true,\"116\":true,\"119\":true,\"12\":false,\"120\":true,\"123\":false,\"128\":false,\"13\":true,\"131\":false,\"132\":false,\"137\":true,\"14\":true,\"140\":false,\"142\":false,\"146\":true,\"147\":false,\"149\":false,\"15\":true,\"150\":false,\"151\":false,\"152\":true,\"153\":false,\"154\":true,\"156\":false,\"157\":false,\"158\":false,\"159\":false,\"16\":true,\"160\":false,\"162\":true,\"166\":false,\"167\":false,\"168\":true,\"169\":false,\"170\":false,\"171\":true,\"173\":true,\"174\":true,\"175\":true,\"178\":true,\"179\":true,\"180\":false,\"181\":false,\"185\":true,\"186\":true,\"187\":false,\"188\":false,\"189\":false,\"190\":true,\"191\":true,\"192\":true,\"193\":true,\"195\":true,\"196\":false,\"197\":true,\"198\":true,\"199\":true,\"200\":true,\"201\":true,\"202\":false,\"203\":true,\"204\":true,\"205\":false,\"208\":false,\"209\":true,\"211\":true,\"212\":true,\"213\":true,\"217\":false,\"218\":false,\"219\":false,\"23\":false,\"24\":false,\"26\":true,\"27\":false,\"28\":false,\"29\":true,\"31\":false,\"32\":true,\"34\":false,\"35\":false,\"38\":true,\"4\":true,\"40\":true,\"41\":false,\"43\":true,\"5\":false,\"59\":true,\"6\":false,\"61\":false,\"62\":false,\"63\":false,\"64\":false,\"65\":false,\"67\":true,\"68\":false,\"69\":true,\"7\":false,\"71\":false,\"73\":false,\"74\":false,\"75\":true,\"78\":true,\"79\":false,\"8\":false,\"81\":false,\"82\":true,\"84\":false,\"86\":false,\"9\":false,\"91\":false,\"95\":true,\"97\":false},\"qe\":{\"app_upsell\":{\"g\":\"\",\"p\":{}},\"igl_app_upsell\":{\"g\":\"\",\"p\":{}},\"notif\":{\"g\":\"\",\"p\":{}},\"onetaplogin\":{\"g\":\"\",\"p\":{}},\"felix_clear_fb_cookie\":{\"g\":\"\",\"p\":{}},\"felix_creation_duration_limits\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting_v2\":{\"g\":\"\",\"p\":{}},\"felix_creation_validation\":{\"g\":\"\",\"p\":{}},\"post_options\":{\"g\":\"\",\"p\":{}},\"sticker_tray\":{\"g\":\"\",\"p\":{}},\"web_sentry\":{\"g\":\"\",\"p\":{}},\"0\":{\"p\":{\"9\":false},\"l\":{},\"qex\":true},\"100\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"101\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"108\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"109\":{\"p\":{},\"l\":{},\"qex\":true},\"111\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"113\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"4\":false,\"5\":false,\"7\":false,\"8\":false},\"l\":{},\"qex\":true},\"117\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"118\":{\"p\":{\"0\":false,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"119\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"12\":{\"p\":{\"0\":5},\"l\":{},\"qex\":true},\"121\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"122\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"123\":{\"p\":{\"0\":true,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"124\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":true,\"4\":false,\"5\":\"switch_text\",\"6\":\"chevron\"},\"l\":{},\"qex\":true},\"125\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"127\":{\"p\":{\"0\":true,\"1\":true,\"2\":true},\"l\":{},\"qex\":true},\"128\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"129\":{\"p\":{\"1\":false,\"2\":true},\"l\":{},\"qex\":true},\"13\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"131\":{\"p\":{\"2\":true,\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"132\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"135\":{\"p\":{\"0\":false,\"1\":false,\"2\":false,\"3\":false},\"l\":{},\"qex\":true},\"137\":{\"p\":{},\"l\":{},\"qex\":true},\"141\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"3\":false},\"l\":{},\"qex\":true},\"142\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"143\":{\"p\":{\"1\":false,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"146\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"148\":{\"p\":{\"0\":true,\"1\":true},\"l\":{},\"qex\":true},\"149\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"150\":{\"p\":{\"0\":false,\"1\":15},\"l\":{},\"qex\":true},\"151\":{\"p\":{\"0\":false,\"3\":false},\"l\":{},\"qex\":true},\"152\":{\"p\":{},\"l\":{},\"qex\":true},\"154\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"155\":{\"p\":{},\"l\":{},\"qex\":true},\"156\":{\"p\":{\"0\":false},\"l\":{\"0\":true},\"qex\":true},\"158\":{\"p\":{\"2\":false},\"l\":{\"2\":true},\"qex\":true},\"159\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"16\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"160\":{\"p\":{},\"l\":{},\"qex\":true},\"161\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"21\":{\"p\":{\"2\":false},\"l\":{},\"qex\":true},\"22\":{\"p\":{\"10\":0.0,\"11\":15,\"12\":3,\"13\":false,\"2\":8.0,\"3\":0.85,\"4\":0.95},\"l\":{},\"qex\":true},\"23\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"25\":{\"p\":{},\"l\":{},\"qex\":true},\"26\":{\"p\":{\"0\":\"\"},\"l\":{},\"qex\":true},\"28\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"29\":{\"p\":{},\"l\":{},\"qex\":true},\"31\":{\"p\":{},\"l\":{},\"qex\":true},\"33\":{\"p\":{},\"l\":{},\"qex\":true},\"34\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"36\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"37\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"39\":{\"p\":{\"0\":false,\"14\":false,\"8\":false},\"l\":{},\"qex\":true},\"41\":{\"p\":{\"3\":true},\"l\":{},\"qex\":true},\"42\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"43\":{\"p\":{\"0\":false,\"1\":false,\"2\":false},\"l\":{},\"qex\":true},\"44\":{\"p\":{\"1\":\"inside_media\",\"2\":0.2},\"l\":{},\"qex\":true},\"45\":{\"p\":{\"13\":false,\"17\":0,\"32\":false,\"35\":false,\"36\":\"control\",\"37\":false,\"38\":false,\"40\":\"control\",\"46\":false,\"47\":false,\"52\":false,\"53\":false,\"55\":false,\"56\":\"halfsheet\",\"57\":false,\"58\":false,\"59\":false,\"60\":\"v2\",\"61\":\"none\",\"62\":\"v1\",\"64\":false,\"65\":false,\"66\":3,\"67\":false,\"68\":false,\"69\":\"control\",\"70\":true,\"71\":true,\"72\":false},\"l\":{\"60\":true},\"qex\":true},\"46\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"47\":{\"p\":{\"0\":true,\"1\":true,\"10\":false,\"11\":false,\"2\":false,\"3\":false,\"9\":false},\"l\":{},\"qex\":true},\"49\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"50\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"54\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"58\":{\"p\":{\"0\":0.25,\"1\":true},\"l\":{},\"qex\":true},\"59\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"62\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"67\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":false,\"5\":true,\"7\":false},\"l\":{},\"qex\":true},\"69\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"71\":{\"p\":{\"1\":\"^/explore/.*|^/accounts/activity/$\"},\"l\":{},\"qex\":true},\"72\":{\"p\":{\"1\":false,\"2\":false},\"l\":{\"1\":true,\"2\":true},\"qex\":true},\"73\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"74\":{\"p\":{\"1\":true,\"13\":false,\"15\":false,\"2\":false,\"3\":true,\"4\":false,\"7\":false,\"9\":true},\"l\":{},\"qex\":true},\"75\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"77\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"80\":{\"p\":{\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"84\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"5\":true,\"6\":false,\"8\":false},\"l\":{},\"qex\":true},\"85\":{\"p\":{\"0\":false,\"1\":\"Pictures and Videos\"},\"l\":{},\"qex\":true},\"87\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"93\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"95\":{\"p\":{},\"l\":{},\"qex\":true},\"98\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true}},\"probably_has_app\":false,\"cb\":false},\"device_id\":\"042371BF-1F20-49B4-8180-6C30F3C535B2\",\"browser_push_pub_key\":\"BIBn3E_rWTci8Xn6P9Xj3btShT85Wdtne0LtwNUyRQ5XjFNkuTq9j4MPAVLvAFhXrUU1A9UxyxBA7YIOjqDIDHI\",\"encryption\":{\"key_id\":\"88\",\"public_key\":\"13069813a31a13dda6ba919fe0e9b45035c44b7de391fc460e3f758b7fccf307\",\"version\":\"10\"},\"is_dev\":false,\"signal_collection_config\":null,\"rollout_hash\":\"b10813bd9030\",\"bundle_variant\":\"metro\",\"frontend_env\":\"prod\"}" +
            ",df=dfsdfdsf" +
            ",sdfsf =ww" +
            ",d=(sds(=) {})";

    Map<String, String> scriptDataMap = COMMA_SPLITTER
            .withKeyValueSeparator("=")
            .split(scriptData);

    assertThat(scriptDataMap.get("window._sharedData")).isEqualTo("{\"config\":{\"csrf_token\":\"xtLgg2v5dprbhtD3M73RycGeL4m2EFun\",\"viewer\":null,\"viewerId\":null},\"country_code\":\"BY\",\"language_code\":\"ja\",\"locale\":\"ja_JP\",\"entry_data\":{\"LandingPage\":[{\"captcha\":{\"enabled\":false,\"key\":\"\"},\"hsite_redirect_url\":\"\",\"prefill_phone_number\":\"\",\"gdpr_required\":false,\"tos_version\":\"row\",\"sideload_url\":null,\"seo_category_infos\":[[\"\\u7f8e\\u5bb9\",\"beauty\"],[\"\\u30c0\\u30f3\\u30b9\\u30fb\\u30d1\\u30d5\\u30a9\\u30fc\\u30de\\u30f3\\u30b9\",\"dance_and_performance\"],[\"\\u30d5\\u30a3\\u30c3\\u30c8\\u30cd\\u30b9\",\"fitness\"],[\"\\u30d5\\u30fc\\u30c9\\u30fb\\u30c9\\u30ea\\u30f3\\u30af\",\"food_and_drink\"],[\"\\u30db\\u30fc\\u30e0\\u30fb\\u30ac\\u30fc\\u30c7\\u30cb\\u30f3\\u30b0\",\"home_and_garden\"],[\"\\u97f3\\u697d\",\"music\"],[\"\\u8996\\u899a\\u82b8\\u8853\",\"visual_arts\"]]}]},\"hostname\":\"www.instagram.com\",\"is_whitelisted_crawl_bot\":false,\"connection_quality_rating\":\"EXCELLENT\",\"deployment_stage\":\"c2\",\"platform\":\"windows_nt_10\",\"nonce\":\"9e8zpJLyVt8SUkw4hR0rYw==\",\"mid_pct\":91.86913,\"zero_data\":{},\"cache_schema_version\":3,\"server_checks\":{},\"knobx\":{\"17\":false,\"20\":true,\"22\":true,\"23\":true,\"24\":true,\"25\":true,\"26\":true,\"27\":true,\"29\":true,\"32\":true,\"34\":true,\"35\":false,\"38\":25000,\"39\":true,\"4\":false,\"40\":false},\"to_cache\":{\"gatekeepers\":{\"10\":false,\"100\":false,\"101\":true,\"102\":true,\"103\":true,\"104\":true,\"105\":true,\"106\":true,\"107\":false,\"108\":true,\"11\":false,\"112\":true,\"113\":true,\"114\":true,\"116\":true,\"119\":true,\"12\":false,\"120\":true,\"123\":false,\"128\":false,\"13\":true,\"131\":false,\"132\":false,\"137\":true,\"14\":true,\"140\":false,\"142\":false,\"146\":true,\"147\":false,\"149\":false,\"15\":true,\"150\":false,\"151\":false,\"152\":true,\"153\":false,\"154\":true,\"156\":false,\"157\":false,\"158\":false,\"159\":false,\"16\":true,\"160\":false,\"162\":true,\"166\":false,\"167\":false,\"168\":true,\"169\":false,\"170\":false,\"171\":true,\"173\":true,\"174\":true,\"175\":true,\"178\":true,\"179\":true,\"180\":false,\"181\":false,\"185\":true,\"186\":true,\"187\":false,\"188\":false,\"189\":false,\"190\":true,\"191\":true,\"192\":true,\"193\":true,\"195\":true,\"196\":false,\"197\":true,\"198\":true,\"199\":true,\"200\":true,\"201\":true,\"202\":false,\"203\":true,\"204\":true,\"205\":false,\"208\":false,\"209\":true,\"211\":true,\"212\":true,\"213\":true,\"217\":false,\"218\":false,\"219\":false,\"23\":false,\"24\":false,\"26\":true,\"27\":false,\"28\":false,\"29\":true,\"31\":false,\"32\":true,\"34\":false,\"35\":false,\"38\":true,\"4\":true,\"40\":true,\"41\":false,\"43\":true,\"5\":false,\"59\":true,\"6\":false,\"61\":false,\"62\":false,\"63\":false,\"64\":false,\"65\":false,\"67\":true,\"68\":false,\"69\":true,\"7\":false,\"71\":false,\"73\":false,\"74\":false,\"75\":true,\"78\":true,\"79\":false,\"8\":false,\"81\":false,\"82\":true,\"84\":false,\"86\":false,\"9\":false,\"91\":false,\"95\":true,\"97\":false},\"qe\":{\"app_upsell\":{\"g\":\"\",\"p\":{}},\"igl_app_upsell\":{\"g\":\"\",\"p\":{}},\"notif\":{\"g\":\"\",\"p\":{}},\"onetaplogin\":{\"g\":\"\",\"p\":{}},\"felix_clear_fb_cookie\":{\"g\":\"\",\"p\":{}},\"felix_creation_duration_limits\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting_v2\":{\"g\":\"\",\"p\":{}},\"felix_creation_validation\":{\"g\":\"\",\"p\":{}},\"post_options\":{\"g\":\"\",\"p\":{}},\"sticker_tray\":{\"g\":\"\",\"p\":{}},\"web_sentry\":{\"g\":\"\",\"p\":{}},\"0\":{\"p\":{\"9\":false},\"l\":{},\"qex\":true},\"100\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"101\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"108\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"109\":{\"p\":{},\"l\":{},\"qex\":true},\"111\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"113\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"4\":false,\"5\":false,\"7\":false,\"8\":false},\"l\":{},\"qex\":true},\"117\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"118\":{\"p\":{\"0\":false,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"119\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"12\":{\"p\":{\"0\":5},\"l\":{},\"qex\":true},\"121\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"122\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"123\":{\"p\":{\"0\":true,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"124\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":true,\"4\":false,\"5\":\"switch_text\",\"6\":\"chevron\"},\"l\":{},\"qex\":true},\"125\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"127\":{\"p\":{\"0\":true,\"1\":true,\"2\":true},\"l\":{},\"qex\":true},\"128\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"129\":{\"p\":{\"1\":false,\"2\":true},\"l\":{},\"qex\":true},\"13\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"131\":{\"p\":{\"2\":true,\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"132\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"135\":{\"p\":{\"0\":false,\"1\":false,\"2\":false,\"3\":false},\"l\":{},\"qex\":true},\"137\":{\"p\":{},\"l\":{},\"qex\":true},\"141\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"3\":false},\"l\":{},\"qex\":true},\"142\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"143\":{\"p\":{\"1\":false,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"146\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"148\":{\"p\":{\"0\":true,\"1\":true},\"l\":{},\"qex\":true},\"149\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"150\":{\"p\":{\"0\":false,\"1\":15},\"l\":{},\"qex\":true},\"151\":{\"p\":{\"0\":false,\"3\":false},\"l\":{},\"qex\":true},\"152\":{\"p\":{},\"l\":{},\"qex\":true},\"154\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"155\":{\"p\":{},\"l\":{},\"qex\":true},\"156\":{\"p\":{\"0\":false},\"l\":{\"0\":true},\"qex\":true},\"158\":{\"p\":{\"2\":false},\"l\":{\"2\":true},\"qex\":true},\"159\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"16\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"160\":{\"p\":{},\"l\":{},\"qex\":true},\"161\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"21\":{\"p\":{\"2\":false},\"l\":{},\"qex\":true},\"22\":{\"p\":{\"10\":0.0,\"11\":15,\"12\":3,\"13\":false,\"2\":8.0,\"3\":0.85,\"4\":0.95},\"l\":{},\"qex\":true},\"23\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"25\":{\"p\":{},\"l\":{},\"qex\":true},\"26\":{\"p\":{\"0\":\"\"},\"l\":{},\"qex\":true},\"28\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"29\":{\"p\":{},\"l\":{},\"qex\":true},\"31\":{\"p\":{},\"l\":{},\"qex\":true},\"33\":{\"p\":{},\"l\":{},\"qex\":true},\"34\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"36\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"37\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"39\":{\"p\":{\"0\":false,\"14\":false,\"8\":false},\"l\":{},\"qex\":true},\"41\":{\"p\":{\"3\":true},\"l\":{},\"qex\":true},\"42\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"43\":{\"p\":{\"0\":false,\"1\":false,\"2\":false},\"l\":{},\"qex\":true},\"44\":{\"p\":{\"1\":\"inside_media\",\"2\":0.2},\"l\":{},\"qex\":true},\"45\":{\"p\":{\"13\":false,\"17\":0,\"32\":false,\"35\":false,\"36\":\"control\",\"37\":false,\"38\":false,\"40\":\"control\",\"46\":false,\"47\":false,\"52\":false,\"53\":false,\"55\":false,\"56\":\"halfsheet\",\"57\":false,\"58\":false,\"59\":false,\"60\":\"v2\",\"61\":\"none\",\"62\":\"v1\",\"64\":false,\"65\":false,\"66\":3,\"67\":false,\"68\":false,\"69\":\"control\",\"70\":true,\"71\":true,\"72\":false},\"l\":{\"60\":true},\"qex\":true},\"46\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"47\":{\"p\":{\"0\":true,\"1\":true,\"10\":false,\"11\":false,\"2\":false,\"3\":false,\"9\":false},\"l\":{},\"qex\":true},\"49\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"50\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"54\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"58\":{\"p\":{\"0\":0.25,\"1\":true},\"l\":{},\"qex\":true},\"59\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"62\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"67\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":false,\"5\":true,\"7\":false},\"l\":{},\"qex\":true},\"69\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"71\":{\"p\":{\"1\":\"^/explore/.*|^/accounts/activity/$\"},\"l\":{},\"qex\":true},\"72\":{\"p\":{\"1\":false,\"2\":false},\"l\":{\"1\":true,\"2\":true},\"qex\":true},\"73\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"74\":{\"p\":{\"1\":true,\"13\":false,\"15\":false,\"2\":false,\"3\":true,\"4\":false,\"7\":false,\"9\":true},\"l\":{},\"qex\":true},\"75\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"77\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"80\":{\"p\":{\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"84\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"5\":true,\"6\":false,\"8\":false},\"l\":{},\"qex\":true},\"85\":{\"p\":{\"0\":false,\"1\":\"Pictures and Videos\"},\"l\":{},\"qex\":true},\"87\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"93\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"95\":{\"p\":{},\"l\":{},\"qex\":true},\"98\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true}},\"probably_has_app\":false,\"cb\":false},\"device_id\":\"042371BF-1F20-49B4-8180-6C30F3C535B2\",\"browser_push_pub_key\":\"BIBn3E_rWTci8Xn6P9Xj3btShT85Wdtne0LtwNUyRQ5XjFNkuTq9j4MPAVLvAFhXrUU1A9UxyxBA7YIOjqDIDHI\",\"encryption\":{\"key_id\":\"88\",\"public_key\":\"13069813a31a13dda6ba919fe0e9b45035c44b7de391fc460e3f758b7fccf307\",\"version\":\"10\"},\"is_dev\":false,\"signal_collection_config\":null,\"rollout_hash\":\"b10813bd9030\",\"bundle_variant\":\"metro\",\"frontend_env\":\"prod\"}");
  }

  public void testSplitterTrimKeyValuesJson() {
    String scriptData = "   window._sharedData =     {\"config\":{\"csrf_token\":\"xtLgg2v5dprbhtD3M73RycGeL4m2EFun\",\"viewer\":null,\"viewerId\":null},\"country_code\":\"BY\",\"language_code\":\"ja\",\"locale\":\"ja_JP\",\"entry_data\":{\"LandingPage\":[{\"captcha\":{\"enabled\":false,\"key\":\"\"},\"hsite_redirect_url\":\"\",\"prefill_phone_number\":\"\",\"gdpr_required\":false,\"tos_version\":\"row\",\"sideload_url\":null,\"seo_category_infos\":[[\"\\u7f8e\\u5bb9\",\"beauty\"],[\"\\u30c0\\u30f3\\u30b9\\u30fb\\u30d1\\u30d5\\u30a9\\u30fc\\u30de\\u30f3\\u30b9\",\"dance_and_performance\"],[\"\\u30d5\\u30a3\\u30c3\\u30c8\\u30cd\\u30b9\",\"fitness\"],[\"\\u30d5\\u30fc\\u30c9\\u30fb\\u30c9\\u30ea\\u30f3\\u30af\",\"food_and_drink\"],[\"\\u30db\\u30fc\\u30e0\\u30fb\\u30ac\\u30fc\\u30c7\\u30cb\\u30f3\\u30b0\",\"home_and_garden\"],[\"\\u97f3\\u697d\",\"music\"],[\"\\u8996\\u899a\\u82b8\\u8853\",\"visual_arts\"]]}]},\"hostname\":\"www.instagram.com\",\"is_whitelisted_crawl_bot\":false,\"connection_quality_rating\":\"EXCELLENT\",\"deployment_stage\":\"c2\",\"platform\":\"windows_nt_10\",\"nonce\":\"9e8zpJLyVt8SUkw4hR0rYw==\",\"mid_pct\":91.86913,\"zero_data\":{},\"cache_schema_version\":3,\"server_checks\":{},\"knobx\":{\"17\":false,\"20\":true,\"22\":true,\"23\":true,\"24\":true,\"25\":true,\"26\":true,\"27\":true,\"29\":true,\"32\":true,\"34\":true,\"35\":false,\"38\":25000,\"39\":true,\"4\":false,\"40\":false},\"to_cache\":{\"gatekeepers\":{\"10\":false,\"100\":false,\"101\":true,\"102\":true,\"103\":true,\"104\":true,\"105\":true,\"106\":true,\"107\":false,\"108\":true,\"11\":false,\"112\":true,\"113\":true,\"114\":true,\"116\":true,\"119\":true,\"12\":false,\"120\":true,\"123\":false,\"128\":false,\"13\":true,\"131\":false,\"132\":false,\"137\":true,\"14\":true,\"140\":false,\"142\":false,\"146\":true,\"147\":false,\"149\":false,\"15\":true,\"150\":false,\"151\":false,\"152\":true,\"153\":false,\"154\":true,\"156\":false,\"157\":false,\"158\":false,\"159\":false,\"16\":true,\"160\":false,\"162\":true,\"166\":false,\"167\":false,\"168\":true,\"169\":false,\"170\":false,\"171\":true,\"173\":true,\"174\":true,\"175\":true,\"178\":true,\"179\":true,\"180\":false,\"181\":false,\"185\":true,\"186\":true,\"187\":false,\"188\":false,\"189\":false,\"190\":true,\"191\":true,\"192\":true,\"193\":true,\"195\":true,\"196\":false,\"197\":true,\"198\":true,\"199\":true,\"200\":true,\"201\":true,\"202\":false,\"203\":true,\"204\":true,\"205\":false,\"208\":false,\"209\":true,\"211\":true,\"212\":true,\"213\":true,\"217\":false,\"218\":false,\"219\":false,\"23\":false,\"24\":false,\"26\":true,\"27\":false,\"28\":false,\"29\":true,\"31\":false,\"32\":true,\"34\":false,\"35\":false,\"38\":true,\"4\":true,\"40\":true,\"41\":false,\"43\":true,\"5\":false,\"59\":true,\"6\":false,\"61\":false,\"62\":false,\"63\":false,\"64\":false,\"65\":false,\"67\":true,\"68\":false,\"69\":true,\"7\":false,\"71\":false,\"73\":false,\"74\":false,\"75\":true,\"78\":true,\"79\":false,\"8\":false,\"81\":false,\"82\":true,\"84\":false,\"86\":false,\"9\":false,\"91\":false,\"95\":true,\"97\":false},\"qe\":{\"app_upsell\":{\"g\":\"\",\"p\":{}},\"igl_app_upsell\":{\"g\":\"\",\"p\":{}},\"notif\":{\"g\":\"\",\"p\":{}},\"onetaplogin\":{\"g\":\"\",\"p\":{}},\"felix_clear_fb_cookie\":{\"g\":\"\",\"p\":{}},\"felix_creation_duration_limits\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting_v2\":{\"g\":\"\",\"p\":{}},\"felix_creation_validation\":{\"g\":\"\",\"p\":{}},\"post_options\":{\"g\":\"\",\"p\":{}},\"sticker_tray\":{\"g\":\"\",\"p\":{}},\"web_sentry\":{\"g\":\"\",\"p\":{}},\"0\":{\"p\":{\"9\":false},\"l\":{},\"qex\":true},\"100\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"101\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"108\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"109\":{\"p\":{},\"l\":{},\"qex\":true},\"111\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"113\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"4\":false,\"5\":false,\"7\":false,\"8\":false},\"l\":{},\"qex\":true},\"117\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"118\":{\"p\":{\"0\":false,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"119\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"12\":{\"p\":{\"0\":5},\"l\":{},\"qex\":true},\"121\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"122\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"123\":{\"p\":{\"0\":true,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"124\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":true,\"4\":false,\"5\":\"switch_text\",\"6\":\"chevron\"},\"l\":{},\"qex\":true},\"125\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"127\":{\"p\":{\"0\":true,\"1\":true,\"2\":true},\"l\":{},\"qex\":true},\"128\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"129\":{\"p\":{\"1\":false,\"2\":true},\"l\":{},\"qex\":true},\"13\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"131\":{\"p\":{\"2\":true,\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"132\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"135\":{\"p\":{\"0\":false,\"1\":false,\"2\":false,\"3\":false},\"l\":{},\"qex\":true},\"137\":{\"p\":{},\"l\":{},\"qex\":true},\"141\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"3\":false},\"l\":{},\"qex\":true},\"142\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"143\":{\"p\":{\"1\":false,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"146\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"148\":{\"p\":{\"0\":true,\"1\":true},\"l\":{},\"qex\":true},\"149\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"150\":{\"p\":{\"0\":false,\"1\":15},\"l\":{},\"qex\":true},\"151\":{\"p\":{\"0\":false,\"3\":false},\"l\":{},\"qex\":true},\"152\":{\"p\":{},\"l\":{},\"qex\":true},\"154\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"155\":{\"p\":{},\"l\":{},\"qex\":true},\"156\":{\"p\":{\"0\":false},\"l\":{\"0\":true},\"qex\":true},\"158\":{\"p\":{\"2\":false},\"l\":{\"2\":true},\"qex\":true},\"159\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"16\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"160\":{\"p\":{},\"l\":{},\"qex\":true},\"161\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"21\":{\"p\":{\"2\":false},\"l\":{},\"qex\":true},\"22\":{\"p\":{\"10\":0.0,\"11\":15,\"12\":3,\"13\":false,\"2\":8.0,\"3\":0.85,\"4\":0.95},\"l\":{},\"qex\":true},\"23\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"25\":{\"p\":{},\"l\":{},\"qex\":true},\"26\":{\"p\":{\"0\":\"\"},\"l\":{},\"qex\":true},\"28\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"29\":{\"p\":{},\"l\":{},\"qex\":true},\"31\":{\"p\":{},\"l\":{},\"qex\":true},\"33\":{\"p\":{},\"l\":{},\"qex\":true},\"34\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"36\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"37\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"39\":{\"p\":{\"0\":false,\"14\":false,\"8\":false},\"l\":{},\"qex\":true},\"41\":{\"p\":{\"3\":true},\"l\":{},\"qex\":true},\"42\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"43\":{\"p\":{\"0\":false,\"1\":false,\"2\":false},\"l\":{},\"qex\":true},\"44\":{\"p\":{\"1\":\"inside_media\",\"2\":0.2},\"l\":{},\"qex\":true},\"45\":{\"p\":{\"13\":false,\"17\":0,\"32\":false,\"35\":false,\"36\":\"control\",\"37\":false,\"38\":false,\"40\":\"control\",\"46\":false,\"47\":false,\"52\":false,\"53\":false,\"55\":false,\"56\":\"halfsheet\",\"57\":false,\"58\":false,\"59\":false,\"60\":\"v2\",\"61\":\"none\",\"62\":\"v1\",\"64\":false,\"65\":false,\"66\":3,\"67\":false,\"68\":false,\"69\":\"control\",\"70\":true,\"71\":true,\"72\":false},\"l\":{\"60\":true},\"qex\":true},\"46\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"47\":{\"p\":{\"0\":true,\"1\":true,\"10\":false,\"11\":false,\"2\":false,\"3\":false,\"9\":false},\"l\":{},\"qex\":true},\"49\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"50\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"54\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"58\":{\"p\":{\"0\":0.25,\"1\":true},\"l\":{},\"qex\":true},\"59\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"62\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"67\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":false,\"5\":true,\"7\":false},\"l\":{},\"qex\":true},\"69\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"71\":{\"p\":{\"1\":\"^/explore/.*|^/accounts/activity/$\"},\"l\":{},\"qex\":true},\"72\":{\"p\":{\"1\":false,\"2\":false},\"l\":{\"1\":true,\"2\":true},\"qex\":true},\"73\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"74\":{\"p\":{\"1\":true,\"13\":false,\"15\":false,\"2\":false,\"3\":true,\"4\":false,\"7\":false,\"9\":true},\"l\":{},\"qex\":true},\"75\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"77\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"80\":{\"p\":{\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"84\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"5\":true,\"6\":false,\"8\":false},\"l\":{},\"qex\":true},\"85\":{\"p\":{\"0\":false,\"1\":\"Pictures and Videos\"},\"l\":{},\"qex\":true},\"87\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"93\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"95\":{\"p\":{},\"l\":{},\"qex\":true},\"98\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true}},\"probably_has_app\":false,\"cb\":false},\"device_id\":\"042371BF-1F20-49B4-8180-6C30F3C535B2\",\"browser_push_pub_key\":\"BIBn3E_rWTci8Xn6P9Xj3btShT85Wdtne0LtwNUyRQ5XjFNkuTq9j4MPAVLvAFhXrUU1A9UxyxBA7YIOjqDIDHI\",\"encryption\":{\"key_id\":\"88\",\"public_key\":\"13069813a31a13dda6ba919fe0e9b45035c44b7de391fc460e3f758b7fccf307\",\"version\":\"10\"},\"is_dev\":false,\"signal_collection_config\":null,\"rollout_hash\":\"b10813bd9030\",\"bundle_variant\":\"metro\",\"frontend_env\":\"prod\"}" +
            ", df=dfsdfdsf" +
            ", sdfsf =ww" +
            ",d=  (sds(=) {}) " +
            ",dt= " +
            ", , ,";

    Map<String, String> scriptDataMap = COMMA_SPLITTER
            .trimResults()
            .omitEmptyStrings()
            .withKeyValueSeparator("=")
            .split(scriptData);

    assertThat(scriptDataMap.get("window._sharedData")).isEqualTo("{\"config\":{\"csrf_token\":\"xtLgg2v5dprbhtD3M73RycGeL4m2EFun\",\"viewer\":null,\"viewerId\":null},\"country_code\":\"BY\",\"language_code\":\"ja\",\"locale\":\"ja_JP\",\"entry_data\":{\"LandingPage\":[{\"captcha\":{\"enabled\":false,\"key\":\"\"},\"hsite_redirect_url\":\"\",\"prefill_phone_number\":\"\",\"gdpr_required\":false,\"tos_version\":\"row\",\"sideload_url\":null,\"seo_category_infos\":[[\"\\u7f8e\\u5bb9\",\"beauty\"],[\"\\u30c0\\u30f3\\u30b9\\u30fb\\u30d1\\u30d5\\u30a9\\u30fc\\u30de\\u30f3\\u30b9\",\"dance_and_performance\"],[\"\\u30d5\\u30a3\\u30c3\\u30c8\\u30cd\\u30b9\",\"fitness\"],[\"\\u30d5\\u30fc\\u30c9\\u30fb\\u30c9\\u30ea\\u30f3\\u30af\",\"food_and_drink\"],[\"\\u30db\\u30fc\\u30e0\\u30fb\\u30ac\\u30fc\\u30c7\\u30cb\\u30f3\\u30b0\",\"home_and_garden\"],[\"\\u97f3\\u697d\",\"music\"],[\"\\u8996\\u899a\\u82b8\\u8853\",\"visual_arts\"]]}]},\"hostname\":\"www.instagram.com\",\"is_whitelisted_crawl_bot\":false,\"connection_quality_rating\":\"EXCELLENT\",\"deployment_stage\":\"c2\",\"platform\":\"windows_nt_10\",\"nonce\":\"9e8zpJLyVt8SUkw4hR0rYw==\",\"mid_pct\":91.86913,\"zero_data\":{},\"cache_schema_version\":3,\"server_checks\":{},\"knobx\":{\"17\":false,\"20\":true,\"22\":true,\"23\":true,\"24\":true,\"25\":true,\"26\":true,\"27\":true,\"29\":true,\"32\":true,\"34\":true,\"35\":false,\"38\":25000,\"39\":true,\"4\":false,\"40\":false},\"to_cache\":{\"gatekeepers\":{\"10\":false,\"100\":false,\"101\":true,\"102\":true,\"103\":true,\"104\":true,\"105\":true,\"106\":true,\"107\":false,\"108\":true,\"11\":false,\"112\":true,\"113\":true,\"114\":true,\"116\":true,\"119\":true,\"12\":false,\"120\":true,\"123\":false,\"128\":false,\"13\":true,\"131\":false,\"132\":false,\"137\":true,\"14\":true,\"140\":false,\"142\":false,\"146\":true,\"147\":false,\"149\":false,\"15\":true,\"150\":false,\"151\":false,\"152\":true,\"153\":false,\"154\":true,\"156\":false,\"157\":false,\"158\":false,\"159\":false,\"16\":true,\"160\":false,\"162\":true,\"166\":false,\"167\":false,\"168\":true,\"169\":false,\"170\":false,\"171\":true,\"173\":true,\"174\":true,\"175\":true,\"178\":true,\"179\":true,\"180\":false,\"181\":false,\"185\":true,\"186\":true,\"187\":false,\"188\":false,\"189\":false,\"190\":true,\"191\":true,\"192\":true,\"193\":true,\"195\":true,\"196\":false,\"197\":true,\"198\":true,\"199\":true,\"200\":true,\"201\":true,\"202\":false,\"203\":true,\"204\":true,\"205\":false,\"208\":false,\"209\":true,\"211\":true,\"212\":true,\"213\":true,\"217\":false,\"218\":false,\"219\":false,\"23\":false,\"24\":false,\"26\":true,\"27\":false,\"28\":false,\"29\":true,\"31\":false,\"32\":true,\"34\":false,\"35\":false,\"38\":true,\"4\":true,\"40\":true,\"41\":false,\"43\":true,\"5\":false,\"59\":true,\"6\":false,\"61\":false,\"62\":false,\"63\":false,\"64\":false,\"65\":false,\"67\":true,\"68\":false,\"69\":true,\"7\":false,\"71\":false,\"73\":false,\"74\":false,\"75\":true,\"78\":true,\"79\":false,\"8\":false,\"81\":false,\"82\":true,\"84\":false,\"86\":false,\"9\":false,\"91\":false,\"95\":true,\"97\":false},\"qe\":{\"app_upsell\":{\"g\":\"\",\"p\":{}},\"igl_app_upsell\":{\"g\":\"\",\"p\":{}},\"notif\":{\"g\":\"\",\"p\":{}},\"onetaplogin\":{\"g\":\"\",\"p\":{}},\"felix_clear_fb_cookie\":{\"g\":\"\",\"p\":{}},\"felix_creation_duration_limits\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting\":{\"g\":\"\",\"p\":{}},\"felix_creation_fb_crossposting_v2\":{\"g\":\"\",\"p\":{}},\"felix_creation_validation\":{\"g\":\"\",\"p\":{}},\"post_options\":{\"g\":\"\",\"p\":{}},\"sticker_tray\":{\"g\":\"\",\"p\":{}},\"web_sentry\":{\"g\":\"\",\"p\":{}},\"0\":{\"p\":{\"9\":false},\"l\":{},\"qex\":true},\"100\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"101\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"108\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"109\":{\"p\":{},\"l\":{},\"qex\":true},\"111\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"113\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"4\":false,\"5\":false,\"7\":false,\"8\":false},\"l\":{},\"qex\":true},\"117\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"118\":{\"p\":{\"0\":false,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"119\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"12\":{\"p\":{\"0\":5},\"l\":{},\"qex\":true},\"121\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"122\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"123\":{\"p\":{\"0\":true,\"1\":true,\"2\":false},\"l\":{},\"qex\":true},\"124\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":true,\"4\":false,\"5\":\"switch_text\",\"6\":\"chevron\"},\"l\":{},\"qex\":true},\"125\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"127\":{\"p\":{\"0\":true,\"1\":true,\"2\":true},\"l\":{},\"qex\":true},\"128\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"129\":{\"p\":{\"1\":false,\"2\":true},\"l\":{},\"qex\":true},\"13\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"131\":{\"p\":{\"2\":true,\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"132\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"135\":{\"p\":{\"0\":false,\"1\":false,\"2\":false,\"3\":false},\"l\":{},\"qex\":true},\"137\":{\"p\":{},\"l\":{},\"qex\":true},\"141\":{\"p\":{\"0\":true,\"1\":false,\"2\":true,\"3\":false},\"l\":{},\"qex\":true},\"142\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"143\":{\"p\":{\"1\":false,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"146\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"148\":{\"p\":{\"0\":true,\"1\":true},\"l\":{},\"qex\":true},\"149\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"150\":{\"p\":{\"0\":false,\"1\":15},\"l\":{},\"qex\":true},\"151\":{\"p\":{\"0\":false,\"3\":false},\"l\":{},\"qex\":true},\"152\":{\"p\":{},\"l\":{},\"qex\":true},\"154\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"155\":{\"p\":{},\"l\":{},\"qex\":true},\"156\":{\"p\":{\"0\":false},\"l\":{\"0\":true},\"qex\":true},\"158\":{\"p\":{\"2\":false},\"l\":{\"2\":true},\"qex\":true},\"159\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"16\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"160\":{\"p\":{},\"l\":{},\"qex\":true},\"161\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"21\":{\"p\":{\"2\":false},\"l\":{},\"qex\":true},\"22\":{\"p\":{\"10\":0.0,\"11\":15,\"12\":3,\"13\":false,\"2\":8.0,\"3\":0.85,\"4\":0.95},\"l\":{},\"qex\":true},\"23\":{\"p\":{\"0\":false,\"1\":false},\"l\":{},\"qex\":true},\"25\":{\"p\":{},\"l\":{},\"qex\":true},\"26\":{\"p\":{\"0\":\"\"},\"l\":{},\"qex\":true},\"28\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"29\":{\"p\":{},\"l\":{},\"qex\":true},\"31\":{\"p\":{},\"l\":{},\"qex\":true},\"33\":{\"p\":{},\"l\":{},\"qex\":true},\"34\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"36\":{\"p\":{\"0\":true,\"1\":true,\"2\":false,\"3\":false,\"4\":false},\"l\":{},\"qex\":true},\"37\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"39\":{\"p\":{\"0\":false,\"14\":false,\"8\":false},\"l\":{},\"qex\":true},\"41\":{\"p\":{\"3\":true},\"l\":{},\"qex\":true},\"42\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"43\":{\"p\":{\"0\":false,\"1\":false,\"2\":false},\"l\":{},\"qex\":true},\"44\":{\"p\":{\"1\":\"inside_media\",\"2\":0.2},\"l\":{},\"qex\":true},\"45\":{\"p\":{\"13\":false,\"17\":0,\"32\":false,\"35\":false,\"36\":\"control\",\"37\":false,\"38\":false,\"40\":\"control\",\"46\":false,\"47\":false,\"52\":false,\"53\":false,\"55\":false,\"56\":\"halfsheet\",\"57\":false,\"58\":false,\"59\":false,\"60\":\"v2\",\"61\":\"none\",\"62\":\"v1\",\"64\":false,\"65\":false,\"66\":3,\"67\":false,\"68\":false,\"69\":\"control\",\"70\":true,\"71\":true,\"72\":false},\"l\":{\"60\":true},\"qex\":true},\"46\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"47\":{\"p\":{\"0\":true,\"1\":true,\"10\":false,\"11\":false,\"2\":false,\"3\":false,\"9\":false},\"l\":{},\"qex\":true},\"49\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"50\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"54\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"58\":{\"p\":{\"0\":0.25,\"1\":true},\"l\":{},\"qex\":true},\"59\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"62\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"67\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":false,\"5\":true,\"7\":false},\"l\":{},\"qex\":true},\"69\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"71\":{\"p\":{\"1\":\"^/explore/.*|^/accounts/activity/$\"},\"l\":{},\"qex\":true},\"72\":{\"p\":{\"1\":false,\"2\":false},\"l\":{\"1\":true,\"2\":true},\"qex\":true},\"73\":{\"p\":{\"0\":false},\"l\":{},\"qex\":true},\"74\":{\"p\":{\"1\":true,\"13\":false,\"15\":false,\"2\":false,\"3\":true,\"4\":false,\"7\":false,\"9\":true},\"l\":{},\"qex\":true},\"75\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"77\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true},\"80\":{\"p\":{\"3\":true,\"4\":false},\"l\":{},\"qex\":true},\"84\":{\"p\":{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"5\":true,\"6\":false,\"8\":false},\"l\":{},\"qex\":true},\"85\":{\"p\":{\"0\":false,\"1\":\"Pictures and Videos\"},\"l\":{},\"qex\":true},\"87\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"93\":{\"p\":{\"0\":true},\"l\":{},\"qex\":true},\"95\":{\"p\":{},\"l\":{},\"qex\":true},\"98\":{\"p\":{\"1\":false},\"l\":{},\"qex\":true}},\"probably_has_app\":false,\"cb\":false},\"device_id\":\"042371BF-1F20-49B4-8180-6C30F3C535B2\",\"browser_push_pub_key\":\"BIBn3E_rWTci8Xn6P9Xj3btShT85Wdtne0LtwNUyRQ5XjFNkuTq9j4MPAVLvAFhXrUU1A9UxyxBA7YIOjqDIDHI\",\"encryption\":{\"key_id\":\"88\",\"public_key\":\"13069813a31a13dda6ba919fe0e9b45035c44b7de391fc460e3f758b7fccf307\",\"version\":\"10\"},\"is_dev\":false,\"signal_collection_config\":null,\"rollout_hash\":\"b10813bd9030\",\"bundle_variant\":\"metro\",\"frontend_env\":\"prod\"}");
  }
}
