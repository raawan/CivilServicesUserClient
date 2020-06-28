package org.users;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.users.client.UserClientException;
import org.users.client.UsersClient;
import org.users.client.model.User;

public class PeopleInAreaTest {

    private PeopleInArea peopleInArea;

    @Mock
    private UsersClient usersClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        peopleInArea = new PeopleInArea(usersClient);
    }

    @Test
    public void shouldReturnResultWhenCallsToUserClientEndpointsAreSuccessful() throws ExecutionException, InterruptedException {
        //given
        List<User> userListByCity = List.of(new User(1), new User(2), new User(3));
        when(usersClient.getUsersByCity(anyString())).thenReturn(userListByCity);

        List<User> userListByVicinity = List.of(new User(14), new User(15), new User(3), new User(2));
        when(usersClient.getAllUsers()).thenReturn(userListByVicinity);

        //when
        final Set<User> london = peopleInArea.findPeopleInArea("London", 12);

        //then
        assertEquals(5, london.size());
        verify(usersClient,times(1)).getUsersByCity(anyString());
        verify(usersClient,times(1)).getAllUsers();
    }

    @Test(expected = ExecutionException.class)
    public void shouldThrowExceptionWhenCallToGetUserByCityEndpointIsUnsuccessful() throws ExecutionException, InterruptedException {

        //given
        when(usersClient.getUsersByCity(anyString())).thenThrow(UserClientException.class);
        List<User> userListByVicinity = List.of(new User(14), new User(15), new User(3), new User(2));
        when(usersClient.getAllUsers()).thenReturn(userListByVicinity);

        //when and then
        peopleInArea.findPeopleInArea("Munich", 12);
    }

    @Test(expected = ExecutionException.class)
    public void shouldThrowExceptionWhenCallToGetAllUsersEndpointIsUnsuccessful() throws ExecutionException, InterruptedException {

        //given
        List<User> userListByCity = List.of(new User(1), new User(2), new User(3));
        when(usersClient.getUsersByCity(anyString())).thenReturn(userListByCity);
        when(usersClient.getAllUsers()).thenThrow(UserClientException.class);

        //when and then
        peopleInArea.findPeopleInArea("Zurich", 12);
    }
}
