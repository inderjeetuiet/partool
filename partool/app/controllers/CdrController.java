package controllers;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import java.util.GregorianCalendar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Query;

import org.joda.time.Weeks;

import play.db.jpa.JPA;
import play.mvc.With;
import play.mvc.Http.Response;
import dsd.partool.TestDB;
import dsd.partool.VisualizationData;
import dsd.partool.ClientResponse;

import models.SubscriberInformation;
import models.CdrInformation;
import models.CdrUsage;

@With(Secure.class)
public class CdrController extends JsonController {

	/**
	 * Gets visualization data from DB depending on input data and returns it to
	 * client side in JSON format.
	 * 
	 * @param searchValue
	 *            the value by which the subscriber will be searched
	 * @param startDate
	 *            start date for time interval from which data will be fetched
	 * @param endDate
	 *            end date for time interval from which data will be fetched
	 * @param aggregateType
	 *            type for data aggregation
	 * @param searchType
	 *            type of search value
	 */
	public static void getCdrs(String searchValue, String startDate,
			String endDate, String aggregateType, String searchType) {

		// Request Validation
		if (!checkSearchValue(searchValue, searchType)) {
			return;
		}

		if (aggregateType == null || aggregateType.trim().isEmpty()) {
			Response.current().status = 404;
			Response.current().print(
					"Aggregation Type not found. Should be 'SUM' or 'AVG'.");
			return;
		}

		if (startDate == null || startDate.trim().isEmpty()) {
			Response.current().status = 404;
			Response.current().print("Start Date not found!");
			return;
		}
		if (endDate == null || endDate.trim().isEmpty()) {
			Response.current().status = 404;
			Response.current().print("End Date not found!");
			return;
		}

		final String TIME = "000000";
		final String DATE_FORMAT = "yyyy-MM-dd";		

		Query query;
		List<Object[]> results;
		SubscriberInformation subscriber;
		String whereCondition;

		DateFormat format = new SimpleDateFormat(DATE_FORMAT);

		format.setLenient(false);

		// validating date format
		try {
			format.parse(startDate);
			format.parse(endDate);
		} catch (ParseException e) {
			Response.current().status = 404;
			Response.current().print(
					"Date is not valid according to "
							+ ((SimpleDateFormat) format).toPattern()
									.toUpperCase() + " pattern.");
			return;
		} catch (IllegalArgumentException e) {
			Response.current().status = 404;
			Response.current().print(e.getMessage());
			return;
		}

		if (startDate.compareTo(endDate) >= 0) {
			Response.current().status = 404;
			Response.current().print(
					"Invalid request, start date must be before end date!");
			return;
		}

		// formatting input dates to yyyymmddhhmiss
		startDate = startDate.replaceAll("-", "") + TIME;
		endDate = endDate.replaceAll("-", "") + TIME;

		try
		{
		// checking search type and setting condition for query
		if (searchType.toLowerCase().equals("imsi")) {
			subscriber = SubscriberInformation.find("imsi", searchValue)
					.first();
			whereCondition = "r.imsi";

		} else {
			subscriber = SubscriberInformation.find("msisdn", searchValue)
					.first();
			whereCondition = "r.msisdn";
		}		
	
		// fetching requested data from DB
		if (aggregateType.toLowerCase().equals("sum")) {
			query = JPA
					.em()
					.createQuery(
							"select "
									+ "t.usageType as description, "
									+ "substring(r.callDateTime,0,9) as date, "
									+ "substring(r.callDateTime,9,2) as hour, "
									+ "count(*) as count, "
									+ "sum(r.callDuration) as duration, "
									+ "sum(r.ratedAmount) as rated, "
									+ "sum(r.discountedAmount) as discounted "
									+ "from CdrInformation r "
									+ "join r.cdrUsage t "
									+ "where "
									+ whereCondition + " = :searchValue "
									+ " and r.callDateTime between :startDate and :endDate "
									+ "group by substring(r.callDateTime,0,9), substring(r.callDateTime,9,2), t.usageType "
									+ "order by t.usageType, substring(r.callDateTime,0,9), substring(r.callDateTime,9,2)");
		} else if (aggregateType.toLowerCase().equals("avg")) {
			query = JPA
					.em()
					.createQuery(
							"select "
									+ "t.usageType as description, "
									+ "substring(r.callDateTime,0,9) as date, "
									+ "substring(r.callDateTime,9,2) as hour, "
									+ "count(*) as count, "
									+ "avg(r.callDuration) as duration, "
									+ "avg(r.ratedAmount) as rated, "
									+ "avg(r.discountedAmount) as discounted "
									+ "from CdrInformation r "
									+ "join r.cdrUsage t "
									+ "where "
									+ whereCondition + " = :searchValue "
									+ " and r.callDateTime between :startDate and :endDate "
									+ "group by substring(r.callDateTime,0,9), substring(r.callDateTime,9,2), t.usageType "
									+ "order by t.usageType, substring(r.callDateTime,0,9), substring(r.callDateTime,9,2)");
		} else {
			// Invalid aggregation function
			Response.current().status = 404;
			Response.current().print(
					"Invalid aggregation type. Should be 'SUM' or 'AVG'.");
			return;
		}

		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);		
		query.setParameter("searchValue",searchValue);

		results = query.getResultList();

		ClientResponse response = new ClientResponse();
		response.setEndDate(endDate);
		response.setStartDate(startDate);
		response.setSubscriber(subscriber);
		response.setUsageData(mapResults(results, aggregateType, startDate,
				endDate));

		renderJSON(response);
		}
		catch (Exception e) {
			Response.current().status = 404;
			Response.current().print(
					"Internal Server Error");
			return;
		}

	}

	/**
	 * Formats the fetched data for sending back to client side.
	 * 
	 * @param results
	 *            CDR information fetched from DB
	 * @param aggregateType
	 *            data aggregation type, should be SUM or AVG
	 * @return formatted visualization data
	 */
	private static Hashtable<String, VisualizationData> mapResults(
			List<Object[]> results, String aggregateType, String startDate,
			String endDate) {

		Hashtable<String, VisualizationData> resultSet = new Hashtable<String, VisualizationData>();
		int[] mappedResultsCount = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		
		Integer year = Integer.parseInt(startDate.substring(0, 4));
		Integer month = Integer.parseInt(startDate.substring(4, 6));
		Integer day = Integer.parseInt(startDate.substring(6, 8));
		// get weekday from date
		GregorianCalendar calendarStartDate = new GregorianCalendar(year,
				month - 1, day);
		calendarStartDate.setFirstDayOfWeek(MONDAY);

		year = Integer.parseInt(endDate.substring(0, 4));
		month = Integer.parseInt(endDate.substring(4, 6));
		day = Integer.parseInt(endDate.substring(6, 8));
		// get weekday from date
		GregorianCalendar calendarEndDate = new GregorianCalendar(year,
				month - 1, day);
		calendarEndDate.setFirstDayOfWeek(MONDAY);

		int weekday;

		while (calendarStartDate.before(calendarEndDate)) {
			weekday = calendarStartDate.get(DAY_OF_WEEK);
			weekday = (7 - weekday) % 7; // Friday is 1, Thursday us 2..
			mappedResultsCount[weekday]=mappedResultsCount[weekday]+1;
			calendarStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		// aggregate results to weekdays
		for (Object[] objects : results) {

			

			VisualizationData visualizationData;

			String cdrUsage = (String) objects[0];

			if (resultSet.containsKey(cdrUsage)) {
				visualizationData = resultSet.get(cdrUsage);
			} else {
				visualizationData = new VisualizationData();
				resultSet.put(cdrUsage, visualizationData);

			}

			Long[][] count = visualizationData.getCount();
			Double[][] ratedAmount = visualizationData.getRatedAmount();
			Double[][] discountedAmount = visualizationData
					.getDiscountedAmount();
			Double[][] duration = visualizationData.getDuration();

			// read date from result
			String cdrDate = (String) objects[1]; // format is yyyymmdd
			year = Integer.parseInt(cdrDate.substring(0, 4));
			month = Integer.parseInt(cdrDate.substring(4, 6));
			day = Integer.parseInt(cdrDate.substring(6, 8));
			// get weekday from date
			GregorianCalendar calendarDate = new GregorianCalendar(year,
					month - 1, day);
			calendarDate.setFirstDayOfWeek(MONDAY);
			weekday = calendarDate.get(DAY_OF_WEEK);// at this point
			// Saturday is day 0
			weekday = (7 - weekday) % 7; // Friday is 1, Thursday us 2..
			// read hour from result
			Integer hour = Integer.parseInt((String) objects[2]);

			// sum measures to weekdays
			
				count[weekday][hour] += (Long) objects[3];
			
				ratedAmount[weekday][hour] += (Double) objects[5];
			
			if (objects[6]!=null){
				discountedAmount[weekday][hour] += (Double) objects[6];
			}
				duration[weekday][hour] += (Double) objects[4];
			

		}

		if (aggregateType.toLowerCase().equals("avg")) {

			// iterate over each cdrUsageType
			for (String cdrUsage : resultSet.keySet()) {

				VisualizationData visualizationData = resultSet.get(cdrUsage);

				Long[][] count = visualizationData.getCount();
				Double[][] ratedAmount = visualizationData.getRatedAmount();
				Double[][] discountedAmount = visualizationData
						.getDiscountedAmount();
				Double[][] duration = visualizationData.getDuration();

				for (day = 0; day < 7; day++) {
					for (int hour = 0; hour < 24; hour++) {
						if (mappedResultsCount[day] != 0) {
							count[day][hour] = count[day][hour]
									/ (long) mappedResultsCount[day];
							ratedAmount[day][hour] = ratedAmount[day][hour]
									/ (double) mappedResultsCount[day];
							discountedAmount[day][hour] = discountedAmount[day][hour]
									/ (double) mappedResultsCount[day];
							duration[day][hour] = duration[day][hour]
									/ (double) mappedResultsCount[day];
						}
					}
				}
			}
		}

		return resultSet;

	}

	/**
	 * Validates IMSI and MSISDN format.
	 * 
	 * @param searchValue
	 *            the value by which the subscriber will be searched
	 * @param searchType
	 *            type of search value
	 */
	private static boolean checkSearchValue(String searchValue,
			String searchType) {

		if (searchType == null || searchType.length() == 0) {
			Response.current().status = 404;
			Response.current().print("Search Type not found!");
			return false;
		}

		if (!searchType.toLowerCase().equals("imsi")
				&& !searchType.toLowerCase().equals("msisdn")) {
			// Invalid search type
			Response.current().status = 404;
			Response.current().print(
					"Invalid Search Type. Should be 'IMSI' or 'MSISDN'.");
			return false;
		}

		// check search value length
		if (searchValue == null || searchValue.length() == 0) {
			Response.current().status = 404;
			Response.current().print(searchType.toUpperCase() + " not found!");
			return false;

		} else if ((searchType.toLowerCase().equals("imsi") && searchValue
				.length() != 15)
				|| (searchType.toLowerCase().equals("msisdn") && searchValue
						.length() != 9)) {
			Response.current().status = 404;
			Response.current().print(searchType.toUpperCase() + " is invalid!");
			return false;
		} else {
			// check if search value contains just digits
			for (int i = 0; i < searchValue.length(); i++) {
				if (!Character.isDigit(searchValue.charAt(i))) {
					Response.current().status = 404;
					Response.current().print(
							searchType.toUpperCase() + " should be a number!");
					return false;
				}
			}
		}

		return true;
	}
}