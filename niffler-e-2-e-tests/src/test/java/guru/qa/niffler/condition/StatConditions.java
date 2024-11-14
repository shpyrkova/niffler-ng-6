package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color " + expectedColor.rgb) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition colorAndText(Bubble... bubbles) {
        return new WebElementsCondition() {

            final String expectedBubbles = Arrays.stream(bubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles parameters given");
                }
                if (bubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", bubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<Bubble> actualBubbles = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Bubble expectedBubble = Arrays.stream(bubbles).toList().get(i);
                    final Color colorToCheck = expectedBubble.color();
                    final String textToCheck = expectedBubble.text();
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualBubbles.add(new Bubble(Color.fromRgb(rgba), text));
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba) && textToCheck.equals(text);
                    }
                }

                if (!passed) {
                    final String actualResults = actualBubbles.toString();
                    final String message = String.format(
                            "List checks mismatch (actual bubbles: %s, expected bubbles: %s)",
                            actualResults, expectedBubbles
                    );
                    return rejected(message, actualResults);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbles;
            }
        };
    }

    public static WebElementsCondition colorAndTextAnyOrder(Bubble... bubbles) {
        return new WebElementsCondition() {

            final List<Bubble> expectedBubblesList = Arrays.asList(bubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles parameters given");
                }
                if (bubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", bubbles.length, elements.size());
                    return rejected(message, elements);
                }

                final Set<Bubble> expectedBubblesSet = new HashSet<>(expectedBubblesList);
                final Set<Bubble> actualBubblesSet = new HashSet<>();

                for (WebElement element : elements) {
                    final String rgba = element.getCssValue("background-color");
                    final String text = element.getText();
                    actualBubblesSet.add(new Bubble(Color.fromRgb(rgba), text));
                }

                if (!expectedBubblesSet.equals(actualBubblesSet)) {
                    final String actualResults = actualBubblesSet.toString();
                    final String message = String.format(
                            "List checks mismatch (actual bubbles: %s, expected bubbles: %s)",
                            actualResults, expectedBubblesList
                    );
                    return rejected(message, actualResults);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubblesList.toString();
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(Bubble... bubbles) {
        return new WebElementsCondition() {

            final List<Bubble> expectedBubblesList = Arrays.asList(bubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles parameters given");
                }

                final List<Bubble> actualBubbles = new ArrayList<>();
                for (final WebElement elementToCheck : elements) {
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualBubbles.add(new Bubble(Color.fromRgb(rgba), text));
                }

                if (actualBubbles.containsAll(expectedBubblesList)) {
                    return accepted();
                }
                else {
                    final String actualResults = actualBubbles.toString();
                    final String message = String.format(
                            "List checks mismatch (actual bubbles: %s, expected bubbles: %s)",
                            actualResults, expectedBubblesList
                    );
                    return rejected(message, actualResults);
                }
            }

            @Override
            public String toString() {
                return expectedBubblesList.toString();
            }
        };
    }

}