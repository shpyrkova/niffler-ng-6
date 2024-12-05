package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.UserJson;

public interface UsersClient {

    UserJson createUser(String username, String password);

    void addIncomeInvitation(UserJson targetUser, int count);

    void addOutcomeInvitation(UserJson targetUser, int count);

    void addFriend(UserJson targetUser, int count);

}