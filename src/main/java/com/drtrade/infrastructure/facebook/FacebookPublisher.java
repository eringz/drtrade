package com.drtrade.infrastructure.facebook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Component
public class FacebookPublisher {

    private final RestClient restClient;

//    @Value("${facebook.page.id}")
//    private String pageId;
//
//    @Value("${facebook.page.access.token}")
//    private String pageAccessToken;
    @Value("${facebook.page.id:dummy_page_id}")
    private String pageId;

    @Value("${facebook.page.access.token:dummy_access_token}")
    private String pageAccessToken;

    public FacebookPublisher(RestClient restClient) {
        this.restClient = restClient;
    }

    public String publishToPage(String message) {
//        String url = String.format("https://graph.facebook.com/v19.0/me/feed");
        String url = "https://graph.facebook.com/v19.0/me/feed";

        System.out.println("URL :" + url);
        System.out.println("page id: " + pageId);
        System.out.println("page access token: " + pageAccessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("message", message);
        body.add("access_token", pageAccessToken);

        try {
            FacebookPostResponse response = restClient.post()
                    .uri(url)
                    .body(body)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .body(FacebookPostResponse.class);

            if (response != null && response.id() != null) {
                System.out.println("✅ Successfully published to Facebook Page! Post ID: " + response.id());
                return response.id();
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to post on Facebook: " + e.getMessage());
        }

        return "FAILED_POST";
    }

    private record FacebookPostResponse(String id) {}
}