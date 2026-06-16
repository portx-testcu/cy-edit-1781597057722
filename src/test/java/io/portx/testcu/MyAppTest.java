package io.portx.testcu;

import io.portx.camel.test.ApiTest;
import io.portx.camel.test.util.TestResourceReader;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.util.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class MyAppTest extends ApiTest {
    @Test
    public void testSayHello() throws Exception {
        Exchange exchange = sendTestRequest("direct:sayHello", ExchangePattern.InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setHeader("tenantId", "demobank");
                in.setHeader("assetId", "hello-world-api");
                in.setBody(TestResourceReader.readFileAsString("test-data/sayHelloRequest.json"));
            }
        });

        String responseHeader = exchange.getMessage().getHeader("CamelHttpResponseCode", String.class);
        assertEquals("200", responseHeader);

        String response = exchange.getMessage().getBody(String.class);
        JSONAssert.assertEquals("{\"response\",\"Hello World\"}", response, true);
    }

    @Override
    public String[] getMockedRouteIDs() {
        return new String[] {
                "sayHelloRoute"
        };
    }

    @Override
    public String[] getMockedURIs() {
        return new String[] { "https:*" };
    }
}
