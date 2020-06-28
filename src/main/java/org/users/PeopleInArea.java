package org.users;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.users.client.UsersClient;
import org.users.client.model.User;

public class PeopleInArea {

    public static final int TIMEOUT = 3;
    private final UsersClient usersClient;

    public PeopleInArea(final UsersClient usersClient) {
        this.usersClient = usersClient;
    }

    public Set<User> findPeopleInArea(String cityName, int miles) throws ExecutionException, InterruptedException {

        final Future<Set<User>> future = CompletableFuture.supplyAsync(() -> this.findPeopleInCity(cityName))
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> this.findPeopleWithinVicinityOfCity(cityName, miles)),
                        (result1, result2) -> addResults(result1, result2))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
        return future.get();
    }

    private Set<User> addResults(final List<User> result1, final List<User> result2) {
        Set<User> usrs = new HashSet<>();
        usrs.addAll(result1);
        usrs.addAll(result2);
        return usrs;
    }

    private List<User> findPeopleInCity(String cityName) {
        return usersClient.getUsersByCity(cityName);
    }

    private List<User> findPeopleWithinVicinityOfCity(String cityName, int miles) {
        final List<User> allUsers = usersClient.getAllUsers();
        //As the swagger model do not provide any additional data to determine the vicinity of User,
        // a random list of users are selected
        return allUsers.stream().limit(3).collect(Collectors.toList());
    }

}
