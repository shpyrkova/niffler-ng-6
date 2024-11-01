package guru.qa.niffler.service;

import javax.annotation.Nonnull;

public interface GithubClient {
    @Nonnull
    String issueState(String issueNumber);
}