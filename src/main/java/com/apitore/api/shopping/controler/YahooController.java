package com.apitore.api.shopping.controler;

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

import com.apitore.api.shopping.utils.UrlFormatter;

import springfox.documentation.annotations.ApiIgnore;
import yahoo.jp.itemsearch.ResultSet;


/**
 * @author Keigo Hattori
 */
@RestController
@RequestMapping(value = "/yahoo")
public class YahooController {
  private final Logger LOG = Logger.getLogger(YahooController.class);


  @Bean(name="yahooRestTemplate")
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Autowired
  @Qualifier(value="yahooRestTemplate")
  private RestTemplate restTemplate;

  @Value("${yahoo.application.id}")
  private String YAHOO_APPLICATION_ID;
  @Value("${yahoo.secret}")
  private String YAHOO_SECRET;
  @Value("${yahoo.endpoint}")
  private String ENDPOINT;

  private final String IMAGE_SIZE = "132";
  private final Integer HITS = 10;


  @RequestMapping(value="/itemSearch", method=RequestMethod.GET)
  public ResponseEntity<ResultSet> itemSearch (
      @RequestParam("appid")
      String appid,
      @RequestParam("query")
      String query,
      @RequestParam("category_id")
      String category_id,
      @RequestParam(value="page", required=false, defaultValue="1")
      Integer page,
      @RequestParam(value="sort", required=false, defaultValue="-score")
      String sort
      ) {

    try {
      Map<String, String> params = new HashMap<String, String>();
      params.put("appid", appid);
      params.put("query", query);
      params.put("category_id", category_id);
      params.put("hits", HITS.toString());
      params.put("offset", String.valueOf(HITS*(page-1)));
      params.put("image_size", IMAGE_SIZE);
      params.put("sort", sort);
      String url = UrlFormatter.format(ENDPOINT, params);

      ResultSet response = null;
      int i=0;
      while (i<5) {
        try {
          response = restTemplate.getForObject(url, ResultSet.class, params);
          break;
        } catch (RestClientException e) {
          LOG.error("RestClientException",e);
          response = new ResultSet();
          i++;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          LOG.error("InterruptedException",e);
        }
      }
      return new ResponseEntity<ResultSet>(response,HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.error("IllegalArgumentException",e);
      ResultSet response = new ResultSet();
      return new ResponseEntity<ResultSet>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      LOG.error("Exception",e);
      ResultSet response = new ResultSet();
      return new ResponseEntity<ResultSet>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @RequestMapping(value="/private/itemSearch", method=RequestMethod.GET)
  @ApiIgnore
  public ResponseEntity<ResultSet> privateItemSearch(
      @RequestParam("query")
      String query,
      @RequestParam("category_id")
      String category_id,
      @RequestParam(value="page", required=false, defaultValue="1")
      Integer page,
      @RequestParam(value="sort", required=false, defaultValue="-score")
      String sort
      ) {

    return itemSearch(
        YAHOO_APPLICATION_ID,
        query,
        category_id,
        page,
        sort);
  }

}
