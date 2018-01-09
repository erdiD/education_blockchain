package de.deutschebahn.ilv.app;

import de.deutschebahn.ilv.app.user.UserDataConverter;
import de.deutschebahn.ilv.domain.HistoryEntry;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.inject.Inject;
import javax.json.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 05.07.2017.
 */
public class ObjectHistoryConverter {

    @Inject
    Logger logger;
    
	@Inject
	UserDataConverter userDataConverter;

	/**
	 * This returns an Array like: [ {action: "create_demand", entries: [{...}, {...}] } ]
	 * entries has usually just one entry except for 2 actions.
	 * 
	 * @param historyEntries
	 * @return
	 */
	public JsonArray serialize(Collection<HistoryEntry> historyEntries) {

		Objects.requireNonNull(historyEntries);
		
		ArrayList<Map<ObjectStateTransitionAction, List<HistoryEntry>>> fullHeList = new ArrayList<Map<ObjectStateTransitionAction, List<HistoryEntry>>>();
		historyEntries.stream()
			.sorted((he1, he2) -> (int) (he1.getCreationTime().getTime() - he2.getCreationTime().getTime()))
			.forEach(he -> {
				// this is creating a single map entry and adding it to the fullHistoryEntryList.
				Map<ObjectStateTransitionAction, List<HistoryEntry>> historySectionMap = new HashMap<>();
				
				if ( !fullHeList.isEmpty() && isThisActionSameAsPriviousAction(fullHeList, he) ) {
					// same action than the previous one => add to same list
					Map<ObjectStateTransitionAction, List<HistoryEntry>> previousHistorySection = fullHeList.get(fullHeList.size()-1);
					List<HistoryEntry> previousHistoryEntryList = previousHistorySection.get(he.getAction());
					previousHistoryEntryList.add(he);
				} else {
					// create new entry
					ArrayList<HistoryEntry> newHistoryEntryList = new ArrayList<>();
					newHistoryEntryList.add(he);
					historySectionMap.put(he.getAction(), newHistoryEntryList);
					fullHeList.add(historySectionMap);
				}
					
		});
		
		if (!historyEntries.isEmpty()){
			JsonArray serializeMap = this.serializeList(fullHeList);
			return serializeMap;
		} else {
			// returns an empty list if no entry is made
			return Json.createArrayBuilder().build();
		}
	}
	
	
	private boolean isThisActionSameAsPriviousAction ( List<Map<ObjectStateTransitionAction, List<HistoryEntry>>> fullHeList, HistoryEntry he){
		if (fullHeList.isEmpty()){
			return false;
		}
		Map<ObjectStateTransitionAction, List<HistoryEntry>> lastHistoryEntry = fullHeList.get(fullHeList.size()-1);
		ObjectStateTransitionAction lastAction = lastHistoryEntry.keySet().iterator().next();
		ObjectStateTransitionAction currentAction = he.getAction();
		return currentAction == lastAction;
	}
	
	private JsonArray serializeList(ArrayList<Map<ObjectStateTransitionAction, List<HistoryEntry>>> fullHeList) {
		JsonArrayBuilder heEntryArrayBuilder = Json.createArrayBuilder();
		
		fullHeList.forEach( m -> {
			Enum<ObjectStateTransitionAction> action = m.keySet().iterator().next();
			List<HistoryEntry> historyEntriesList = m.get(action);
			
			JsonArrayBuilder heEntryArray = historyEntriesList.stream()
				.map(this::serialize)
				.collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add);
			
			JsonObject finalSingleObject = Json.createObjectBuilder()
			.add("action", action.name())
			.add("entries", heEntryArray)
			.build();
			
			heEntryArrayBuilder.add(finalSingleObject);
		});
		
		return heEntryArrayBuilder.build();
	}

	private JsonObject serialize(HistoryEntry historyEntry) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("date", SerializationHelper.convertFromDate(historyEntry.getCreationTime()))
				.add("author", userDataConverter.serialize(historyEntry.getUser()))
//				.add("blockId", historyEntry.getBlockId()).add("transactionId", historyEntry.getTransactionId())
				.add("customCommentText", "");
		return builder.build();
	}
	
}
