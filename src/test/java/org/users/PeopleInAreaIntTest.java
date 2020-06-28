package org.users;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.users.client.UsersClient;
import org.users.client.model.User;

public class PeopleInAreaIntTest {

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ROOT_CONTEXT = "http://127.0.0.1:8088";

    private PeopleInArea peopleInArea;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8088));

    private UsersClient usersClient;

    @Before
    public void setUp() {
        usersClient = new UsersClient(ROOT_CONTEXT, new RestTemplate());
        peopleInArea = new PeopleInArea(usersClient);
    }

    @Test
    public void shouldReturnResults() throws ExecutionException, InterruptedException {

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
        final Set<User> london = peopleInArea.findPeopleInArea("london", 12);

        //then
        assertEquals(6, london.size());
    }

    @Test(expected = ExecutionException.class)
    public void shouldThrowExecutionExceptionWhenTimeoutOccurs() throws ExecutionException, InterruptedException {
        stubFor(get(urlPathEqualTo("/users/"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(okJson("[" +
                        "    {" +
                        "        \"id\": 4" +
                        "    }" +
                        "]"))
        );

        stubFor(get(urlPathMatching("/city/london/users/"))
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .willReturn(okJson("[" +
                        "    {" +
                        "        \"id\": 11" +
                        "    }" +
                        "]")
                        .withFixedDelay((PeopleInArea.TIMEOUT+1)*1000))

        );

        //when
        peopleInArea.findPeopleInArea("london", 12);
    }
}
