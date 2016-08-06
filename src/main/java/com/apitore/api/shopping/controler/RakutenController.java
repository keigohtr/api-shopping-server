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


/**
 * @author Keigo Hattori
 */
@RestController
@RequestMapping(value = "/rakuten")
public class RakutenController {
  private final Logger LOG = Logger.getLogger(RakutenController.class);


  @Bean(name="rakutenRestTemplate")
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Autowired
  @Qualifier(value="rakutenRestTemplate")
  private RestTemplate restTemplate;

  @Value("${rakuten.application.id}")
  private String RAKUTEN_APPLICATION_ID;
  @Value("${rakuten.application.secret}")
  private String RAKUTEN_APPLICATION_SECRET;
  @Value("${rakuten.affiliate.id}")
  private String RAKUTEN_AFFILIATE_ID;
  @Value("${rakuten.endpoint}")
  private String ENDPOINT;

  private final String FORMAT = "json";
  private final String FORMAT_VERSION = "2";
  private final String HITS = "10";


  @SuppressWarnings("unchecked")
  @RequestMapping(value="/IchibaItem/Search", method=RequestMethod.GET)
  public ResponseEntity<Map<String,Object>> ichibaItemSearch (
      @RequestParam("applicationId")
      String applicationId,
      @RequestParam(value="affiliateId", required=false, defaultValue="")
      String affiliateId,
      @RequestParam("keyword")
      String keyword,
      @RequestParam("genreId")
      String genreId,
      @RequestParam(value="page", required=false, defaultValue="1")
      Integer page,
      @RequestParam(value="sort", required=false, defaultValue="standard")
      String sort
      ) {

    try {
      Map<String, String> params = new HashMap<String, String>();
      params.put("format", FORMAT);
      params.put("formatVersion", FORMAT_VERSION);
      params.put("hits", HITS);
      params.put("applicationId", applicationId);
      if (!affiliateId.isEmpty())
        params.put("affiliateId", affiliateId);
      params.put("keyword", keyword);
      params.put("genreId", genreId);
      params.put("page", page.toString());
      params.put("sort", sort);
      String url = UrlFormatter.format(ENDPOINT, params);

      Map<String,Object> response = null;
      int i=0;
      while (i<5) {
        try {
          response = restTemplate.getForObject(url, Map.class, params);
          break;
        } catch (RestClientException e) {
          LOG.error("RestClientException",e);
          response = new HashMap<String,Object>();
          i++;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          LOG.error("InterruptedException",e);
        }
      }
      return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      LOG.error("IllegalArgumentException",e);
      Map<String,Object> response = new HashMap<String,Object>();
      return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      LOG.error("Exception",e);
      Map<String,Object> response = new HashMap<String,Object>();
      return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @RequestMapping(value="/private/IchibaItem/Search", method=RequestMethod.GET)
  @ApiIgnore
  public ResponseEntity<Map<String,Object>> privateIchibaItemSearch(
      @RequestParam("keyword")
      String keyword,
      @RequestParam("genreId")
      String genreId,
      @RequestParam(value="page", required=false, defaultValue="1")
      Integer page,
      @RequestParam(value="sort", required=false, defaultValue="standard")
      String sort
      ) {

    return ichibaItemSearch(
        RAKUTEN_APPLICATION_ID,
        RAKUTEN_AFFILIATE_ID,
        keyword,
        genreId,
        page,
        sort);
  }

}
