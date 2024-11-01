package guru.qa.niffler.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataFilterValues {
    TODAY("Today"), WEEK("last week"), MONTH("Last month"), ALL("All time");
    public final String text;
}