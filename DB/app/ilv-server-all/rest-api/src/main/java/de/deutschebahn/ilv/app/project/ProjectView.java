package de.deutschebahn.ilv.app.project;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.Offer;

import java.util.List;

/**
 * Created by AlbertLacambraBasil on 14.08.2017.
 */
public class ProjectView {
    private Demand demand;
    private List<Offer> offers;
    private Long overallLastModifiedTimeStamp;
    private ProjectTask projectTask; 

    public ProjectView(Demand demand, List<Offer> offers, Long overallLastModifiedTimeStamp) {
        this.demand = demand;
        this.offers = offers;
        this.overallLastModifiedTimeStamp = overallLastModifiedTimeStamp;
        this.projectTask = ProjectTask.DEMAND_IN_PROGRESS;
    }
    
    public Demand getDemand() {
        return demand;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public Long getOverallLastModifiedTimeStamp() {
        return overallLastModifiedTimeStamp;
    }

	public ProjectTask getProjectTask() {
		return projectTask;
	}

	public void setProjectTask(ProjectTask projectTask) {
		this.projectTask = projectTask;
	}
}
