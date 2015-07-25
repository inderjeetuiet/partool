package dsd.partool;

import controllers.JsonExclude;

public class VisualizationData {

    private Long[][] count;
    private Double[][] ratedAmount;
    private Double[][] discountedAmount;
    private Double[][] duration;

    @JsonExclude
    private int numberOfWeekdays = 7;
    @JsonExclude
    private int numberOfHours = 24;

    public VisualizationData() {
	// Declare arrays
	count = new Long[numberOfWeekdays][numberOfHours];
	ratedAmount = new Double[numberOfWeekdays][numberOfHours];
	discountedAmount = new Double[numberOfWeekdays][numberOfHours];
	duration = new Double[numberOfWeekdays][numberOfHours];

	// Initialize arrays
	for (int day = 0; day < numberOfWeekdays; day++) {
	    for (int hour = 0; hour < numberOfHours; hour++) {
		count[day][hour] = 0L;
		ratedAmount[day][hour] = 0d;
		discountedAmount[day][hour] = 0d;
		duration[day][hour] = 0d;
	    }
	}
    }

    // getters and setters
    public Long[][] getCount() {
	return count;
    }

    public void setCount(Long[][] count) {
	this.count = count;
    }

    public Double[][] getRatedAmount() {
	return ratedAmount;
    }

    public void setRatedAmount(Double[][] ratedAmount) {
	this.ratedAmount = ratedAmount;
    }

    public Double[][] getDiscountedAmount() {
	return discountedAmount;
    }

    public void setDiscountedAmount(Double[][] discountedAmount) {
	this.discountedAmount = discountedAmount;
    }

    public Double[][] getDuration() {
	return duration;
    }

    public void setDuration(Double[][] duration) {
	this.duration = duration;
    }

}
