package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor
public enum Color {
    yellow("rgba(255, 183, 3, 1)"), green("rgba(53, 173, 123, 1)");

    public final String rgb;

    public static @Nonnull Color fromRgb(@Nonnull String rgb) {
        for (Color value : Color.values()) {
            if (value.rgb.equals(rgb)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Can`t find Color by given rgb: " + rgb);
    }

}