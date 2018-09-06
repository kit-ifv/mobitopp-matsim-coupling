package edu.kit.ifv.mobitopp.matsim;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;

public class DummyPerson implements Person {

	private final List<? extends Plan> oldPlans;
	private final List<Plan> addedPlans;
	private final List<Plan> removedPlans;

	public DummyPerson(Plan oldPlan) {
		super();
		this.oldPlans = asList(oldPlan);
		this.addedPlans = new ArrayList<>();
		this.removedPlans = new ArrayList<>();
	}

	@Override
	public Map<String, Object> getCustomAttributes() {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<? extends Plan> getPlans() {
		return oldPlans;
	}

	@Override
	public boolean addPlan(Plan plan) {
		return addedPlans.add(plan);
	}

	@Override
	public boolean removePlan(Plan plan) {
		return removedPlans.add(plan);
	}

	@Override
	public Plan getSelectedPlan() {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void setSelectedPlan(Plan selectedPlan) {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public Plan createCopyOfSelectedPlanAndMakeSelected() {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public Id<Person> getId() {
		throw new RuntimeException("Not implemented!");
	}
	
	public List<Plan> addedPlans() {
		return addedPlans;
	}

	public List<Plan> removedPlans() {
		return removedPlans;
	}

}
