package cloud.godof.lambda.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cloud.godof.lambda.tags.exception.MacroTagsException;
import cloud.godof.lambda.tags.utils.Constants;

public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private LambdaLogger logger;
	private String paramTags;
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
		final Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put(Constants.REQUESTID, event.get(Constants.REQUESTID));
        responseMap.put(Constants.STATUS, Constants.FAILURE);
        
        try {
        	logger = context.getLogger();
        	
        	ObjectMapper mapper = new ObjectMapper();
    		
        	logger.log("Event: " + mapper.writeValueAsString(event));
	    	
	        final Map<String, Object> templateParams = (Map<String, Object>) event.getOrDefault(Constants.TEMPLATE_PARAMS, new HashMap<>());
	        if (!templateParams.containsKey(Constants.TAGS)) {
	        	throw new RuntimeException("tags param in template parameters is required");
	        }
	    	
	    	paramTags = (String) templateParams.get(Constants.TAGS);
			
			final Object fragment = event.getOrDefault(Constants.FRAGMENT, new HashMap<String, Object>());
	    	final Object retFragment;
	    	if (fragment instanceof Map) {
	    		retFragment = iterateFragment((Map<String, Object>) fragment);
	    	} else {
	    		retFragment = fragment;
	    	}
			
			responseMap.put(Constants.FRAGMENT, retFragment);
			responseMap.put(Constants.STATUS, Constants.SUCCESS);
			
			logger.log("Response: " + mapper.writeValueAsString(responseMap));
		} catch (JsonProcessingException e) {
			throw new MacroTagsException("There was an error converting the tags into JSON");
		}
        
		return responseMap;
	}
	
	private Map<String, Object> iterateFragment(final Map<String, Object> fragment) {
    	final Map<String, Object> retFragment = new HashMap<String, Object>();
    	fragment.forEach((key, value) -> {
    		if (Constants.PROPERTIES.equals(key)) {
    			Map<String, Object> propertiesFragment = (Map<String, Object>) value;
    			propertiesFragment.put(Constants.PROPERTY_TAGS, processTags(paramTags));
    			retFragment.put(key, propertiesFragment);
    		} else {
    			if (value instanceof Map ) {
        			retFragment.put(key, iterateFragment((Map<String, Object>) value));
        		} else {
        			retFragment.put(key, value);
        		}
    		}
    	});
    	return retFragment;
    }

	private List<Map<String, Object>> processTags(String paramTags) {
		List<Map<String, Object>> tags = new ArrayList<>();
		
		String[] tagSegments = paramTags.split(",");
		
		if (tagSegments.length > Constants.CLOUDFORMATION_TAGS_LIMIT) {
			throw new MacroTagsException("A maximum number of 50 tags can be specified");
		}
		
		for (int i = 0; i < tagSegments.length; i++) {
			String tagSegment = tagSegments[i];
			
			if (tagSegment.contains("=")) {
				String[] tagValues = tagSegment.trim().split("=");
				
				if (tagValues.length == 2) {
					Map<String, Object> tag = new LinkedHashMap<String, Object>();
					tag.put(Constants.KEY, tagValues[0]);
					tag.put(Constants.VALUE, tagValues[1]);
					tags.add(tag);
				}
			}
		}
		
		return tags;
	}
}
