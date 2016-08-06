package com.apitore.api.shopping.controler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.amazon.advertising.api.helper.SignedRequestsHelper;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchResponse;

import springfox.documentation.annotations.ApiIgnore;


/**
 * @author Keigo Hattori
 */
@RestController
@RequestMapping(value = "/amazon")
public class AmazonController {
  private final Logger LOG = Logger.getLogger(AmazonController.class);


  @Bean(name="amazonRestTemplate")
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Autowired
  @Qualifier(value="amazonRestTemplate")
  private RestTemplate restTemplate;

  @Value("${aws.accesskey.id}")
  private String AWS_ACCESS_KEY_ID;
  @Value("${aws.secretkey}")
  private String AWS_SECRET_KEY;
  @Value("${aws.associate.tag}")
  private String ASSOCIATE_TAG;
  @Value("${aws.endpoint}")
  private String ENDPOINT;


  @RequestMapping(value="/itemsearch", method=RequestMethod.GET)
  public ResponseEntity<ItemSearchResponse> itemsearch (
      @RequestParam("accesskey_id")
      String accesskey_id,
      @RequestParam("secretkey")
      String secretkey,
      @RequestParam("associate_tag")
      String associate_tag,
      @RequestParam("endpoint")
      String endpoint,
      @RequestParam("search_index")
      String search_index,
      @RequestParam("keywords")
      String keywords,
      @RequestParam(value="response_group", required=false, defaultValue="Images,ItemAttributes,Offers")
      String response_group,
      @RequestParam(value="page", required=false, defaultValue="1")
      Integer page,
      @RequestParam(value="sort", required=false, defaultValue="")
      String sort
      ) {

    try {
      SignedRequestsHelper helper = SignedRequestsHelper.getInstance(endpoint, accesskey_id, secretkey);
      Map<String, String> params = new HashMap<String, String>();
      params.put("Service", "AWSECommerceService");
      params.put("Operation", "ItemSearch");
      params.put("AssociateTag", associate_tag);
      params.put("SearchIndex", search_index);
      params.put("Keywords", keywords);
      params.put("ResponseGroup", response_group);
      params.put("ItemPage", page.toString());
      if (!sort.isEmpty() && !search_index.equals("All"))
        params.put("Sort", sort);
      String requestUrl = helper.sign(params);
      URL url = new URL(requestUrl);
      URI uri = url.toURI();

      ItemSearchResponse response = null;
      int i=0;
      while (i<5) {
        try {
          response = restTemplate.getForObject(uri, ItemSearchResponse.class);
          break;
        } catch (RestClientException e) {
          LOG.error("RestClientException",e);
          response = new ItemSearchResponse();
          i++;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          LOG.error("InterruptedException",e);
        }
      }
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.OK);
    } catch (InvalidKeyException e) {
      LOG.error("InvalidKeyException",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IllegalArgumentException e) {
      LOG.error("IllegalArgumentException",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (UnsupportedEncodingException e) {
      LOG.error("UnsupportedEncodingException",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (NoSuchAlgorithmException e) {
      LOG.error("NoSuchAlgorithmException",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (IOException e) {
      LOG.error("IOException",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (URISyntaxException e) {
      LOG.error("URISyntaxException",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      LOG.error("Exception",e);
      ItemSearchResponse response = new ItemSearchResponse();
      return new ResponseEntity<ItemSearchResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @RequestMapping(value="/private/itemsearch", method=RequestMethod.GET)
  @ApiIgnore
  public ResponseEntity<ItemSearchResponse> privateItemsearch(
      @RequestParam("search_index")
      String search_index,
      @RequestParam("keywords")
      String keywords,
      @RequestParam(value="response_group", required=false, defaultValue="Images,ItemAttributes,Offers")
      String response_group,
      @RequestParam(value="page", required=false, defaultValue="1")
      Integer page,
      @RequestParam(value="sort", required=false, defaultValue="")
      String sort
      ) {

    return itemsearch(
        AWS_ACCESS_KEY_ID,
        AWS_SECRET_KEY,
        ASSOCIATE_TAG,
        ENDPOINT,
        search_index,
        keywords,
        response_group,
        page,
        sort);
  }

}
