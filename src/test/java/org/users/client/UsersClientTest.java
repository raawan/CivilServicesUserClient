package org.users.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.users.client.model.User;

public class UsersClientTest {

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ROOT_CONTEXT = "http://127.0.0.1:8089";
    public static final int STATUS_CODE_FOR_CLIENT_FAULT = 404;
    public static final int STATUS_CODE_FOR_SERVER_FAULT = 500;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8089));

    private UsersClient usersClient;

    @Before
    public void setUp() {
        usersClient = new UsersClient(ROOT_CONTEXT, new RestTemplate());
    }

    @Test
    public void shouldReturnUsersGivenCallToGetAllUsers() {

        //given
        stubFor(get(urlPathEqualTo("/users/"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(okJson("[" +
                        "    {" +
                        "        \"id\": 4" +
                        "    }," +
                        "    {" +
                        "        \"id\": 5" +
                        "    }," +
                        "    {" +
                        "        \"id\": 6" +
                        "    }" +
                        "]"))
        );

        //when
        final List<User> allUsers = usersClient.getAllUsers();

        //then
        assertEquals(4L, getActual(allUsers, 4L).get().getId());
        assertEquals(5L, getActual(allUsers, 5L).get().getId());
        assertEquals(Optional.empty(), getActual(allUsers, 3L));
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnClientFaultsGivenCallToGetAllUsers() {

        //given
        stubFor(get(urlPathEqualTo("/users"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(STATUS_CODE_FOR_CLIENT_FAULT)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        //when and then
        usersClient.getAllUsers();
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnServerFaultsGivenCallToGetAllUsers() {

        //given
        stubFor(get(urlPathEqualTo("/users"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(STATUS_CODE_FOR_SERVER_FAULT)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        //when and then
        usersClient.getAllUsers();
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnBadResponseGivenCallToGetAllUsers() {
        //given
        stubFor(get(urlPathEqualTo("/users"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK))
        );

        //when and then
        usersClient.getAllUsers();
    }

    @Test
    public void shouldReturnUserGivenCallToGetUser() {
        //given
        stubFor(get(urlPathMatching("/user/7"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(okJson(
                        "    {" +
                                "        \"id\": 7" +
                                "    }"
                ))
        );

        //when
        final User user = usersClient.getUser(7);

        //then
        assertEquals(7L, user.getId());
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnClientFaultsGivenCallToGetUser() {

        //given
        stubFor(get(urlPathMatching("/user/8"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(STATUS_CODE_FOR_CLIENT_FAULT)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        //when and then
        usersClient.getUser(8);
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnServerFaultsGivenCallToGetUser() {

        //given
        stubFor(get(urlPathMatching("/user/8"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(STATUS_CODE_FOR_SERVER_FAULT)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        //when and then
        usersClient.getUser(8);
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnBadResponseGivenCallToGetUser() {
        //given
        stubFor(get(urlPathMatching("/user/9/"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE))
        );

        //when and then
        usersClient.getUser(9);
    }


    @Test
    public void shouldReturnUsersGivenCallToGetUsersFromCity() {

        //given
        stubFor(get(urlPathMatching("/city/london/users/"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(okJson("[" +
                        "    {" +
                        "        \"id\": 11" +
                        "    }," +
                        "    {" +
                        "        \"id\": 12" +
                        "    }," +
                        "    {" +
                        "        \"id\": 13" +
                        "    }" +
                        "]"))
        );

        //when
        final List<User> allUsers = usersClient.getUsersByCity("london");

        //then
        assertEquals(11L, getActual(allUsers, 11L).get().getId());
        assertEquals(13L, getActual(allUsers, 13L).get().getId());
        assertEquals(Optional.empty(), getActual(allUsers, 3L));
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnClientFaultsGivenCallToGetUsersFromCity() {

        //given
        stubFor(get(urlPathMatching("/city/london/users"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(STATUS_CODE_FOR_CLIENT_FAULT)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        //when and then
        usersClient.getUsersByCity("london");
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnServerFaultsGivenCallToGetUsersFromCity() {

        //given
        stubFor(get(urlPathMatching("/city/london/users"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withStatus(STATUS_CODE_FOR_SERVER_FAULT)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        //when and then
        usersClient.getUsersByCity("london");
    }

    @Test(expected = UserClientException.class)
    public void shouldThrowExceptionOnBadResponseGivenCallToGetUsersFromCity() {

        //given
        stubFor(get(urlPathMatching("/city/london/users"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER))
        );

        //when and then
        usersClient.getUsersByCity("london");
    }

    @After
    public void resetAllWireMockRules() {
        wireMockRule.resetAll();
    }

    private Optional<User> getActual(final List<User> allUsers, final long l) {
        final Optional<User> optionalUser = allUsers.stream().filter(user -> user.getId() == l).findAny();
        if (optionalUser.isPresent()) {
            return optionalUser;
        } else {
            return Optional.empty();
        }
    }
}
