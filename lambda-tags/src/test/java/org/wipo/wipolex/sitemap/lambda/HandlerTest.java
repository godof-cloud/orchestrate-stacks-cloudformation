package org.wipo.wipolex.sitemap.lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

import cloud.godof.lambda.tags.Handler;
import cloud.godof.lambda.tags.utils.Constants;

@RunWith(MockitoJUnitRunner.class)
public class HandlerTest {
	
	private final String TAGS = "application=Orchestrate Stacks,author=GodOfCloud";
	
	@Mock
	private Context context;
	
	@InjectMocks
	private Handler testHandler;
	
	@Before
	public void beforeEach() {
		Mockito.when(context.getLogger()).thenReturn(new LambdaLogger() {
			@Override
			public void log(String string) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Test
	public void convertTagsSuccessfully() throws Exception {
		Map<String, Object> event = buildEvent();
		
		Map<String, Object> response = testHandler.handleRequest(event, context);
		
		Map<String, Object> expectedResult = buildExpectedResult(event);
		
		Assert.assertEquals(expectedResult.get(Constants.REQUESTID), response.get(Constants.REQUESTID));		
		Assert.assertEquals(expectedResult.get(Constants.STATUS), response.get(Constants.STATUS));
		
		ObjectMapper mapper = new ObjectMapper();
		
		Assert.assertTrue(mapper.writeValueAsString(expectedResult.get(Constants.FRAGMENT)).equals(mapper.writeValueAsString(response.get(Constants.FRAGMENT))));
	}
	
	private Map<String, Object> buildExpectedResult(Map<String, Object> event) {
		Map<String, Object> expectedResult = new HashMap<>();
		expectedResult.put(Constants.REQUESTID, event.get(Constants.REQUESTID));
		
		
		Map<String, Object> expectedFragment = buildFragment();
		Map<String, Object> resources = (Map<String, Object>) expectedFragment.get("Resources");
		Map<String, Object> stackResource = (Map<String, Object>) resources.get("StackResource");
		Map<String, Object> properties = (Map<String, Object>) stackResource.get("Properties");
		
		Map<String, Object> applicationTag = new LinkedHashMap<String, Object>();
		applicationTag.put(Constants.KEY, "application");
		applicationTag.put(Constants.VALUE, "Orchestrate Stacks");
		Map<String, Object> authorTag = new LinkedHashMap<String, Object>();
		authorTag.put(Constants.KEY, "author");
		authorTag.put(Constants.VALUE, "GodOfCloud");
		
		List<Map<String, Object>> tags = new ArrayList<>();
		tags.add(applicationTag);
		tags.add(authorTag);
		
		properties.put(Constants.PROPERTY_TAGS, tags);
		
		expectedResult.put(Constants.FRAGMENT, expectedFragment);
		expectedResult.put(Constants.STATUS, Constants.SUCCESS);
	
		return expectedResult;
	}
	
	private Map<String, Object> buildEvent() {
		Map<String, Object> event = new HashMap<>();
		event.put(Constants.ACCOUNTID, "000000000000");
		event.put(Constants.FRAGMENT, buildFragment());
		event.put(Constants.TRANSFORM_ID, "000000000000:append-tags");
		event.put(Constants.REQUESTID, UUID.randomUUID().toString());
		event.put(Constants.REGION, "us-east-1");
		
		Map<String, Object> templateParameters = new HashMap<>();
		templateParameters.put(Constants.TAGS, TAGS);
		event.put(Constants.TEMPLATE_PARAMS, templateParameters);
		
		return event;
	}
	
	private Map<String, Object> buildFragment() {
		Map<String, Object> fragment = new HashMap<>();
		fragment.put("AWSTemplateFormatVersion", "2010-09-09");
		fragment.put("Description", "Template description");
		
		Map<String, Object> parameters = new HashMap<>();
		Map<String, Object> tagsParam = new HashMap<>();
		tagsParam.put("Type", "String");
		parameters.put(Constants.TAGS, tagsParam);
		fragment.put("Parameters", parameters);
		
		Map<String, Object> stackResource = new HashMap<>();
		stackResource.put("Type", "AWS::CloudFormation::Stack");
		
		Map<String, Object> resourceProperties = new HashMap<>();
		resourceProperties.put("TemplateURL", "https://mybucket.s3.eu-central-1.amazonaws.com/templates/stack.yml");
		stackResource.put("Properties", resourceProperties);
		
		Map<String, Object> resources = new HashMap<>();
		resources.put("StackResource", stackResource);
		fragment.put("Resources", resources);
		
		return fragment;
	}
	
}