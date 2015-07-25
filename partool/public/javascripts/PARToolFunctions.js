"use strict";
var subscriberData = [], // will contain data of fetched subscribers
    tabsData = [],       // associates tab hash with the query that was entered for it
    queryId = 0,
    dataTypes,
    tabNames = {},
    tabContentType = [];

//define what will be on x and y axis
var axisX = ['12am', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12pm', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11'],
    axisY = ['Sat', 'Fri', 'Thu', 'Wed', 'Tue', 'Mon', 'Sun'];


//to move away from the edge
var leftGutter = 40,
    bottomgutter = 20,
    mainGraphWidth = 0.8,
    mainGraphHeight = 0.75;
//font
var defaultFont = {
    font: '12px Fontin-Sans, Arial',
    stroke: "none",
    fill: "#000",
    "text-anchor": 'start'
};

//tab index
var i = 0;
var tabName;
// query var for containing form data
var tabQuery;
//change property window controller
var changePropWin;

$(function () {
    tabContentType["top"] = [];
    tabContentType["bottom"] = [];
    getCdrTypes();

    function tabsConfig(formSelector) {
        tabNames[formSelector] = [];
        return {
            tabTemplate: "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
            show: function (event, ui) {
                var contentTypeTable = tabContentType[formSelector];
                var flag = true;
                for (var i = 0; i < contentTypeTable.length; i++) {
                    if (contentTypeTable[i].hash == ui.tab.hash && contentTypeTable[i].type != "chart") {
                        flag = false;
					}
                }
                if (ui.index > 0) {
                    if (flag) {
                        redrawForHash(ui.tab.hash);
                    } else {
                        addSubscriberData(ui.tab.hash);
                    }
                }
            },
            add: function (event, ui) {
                var hashTabStr = "#tab" + queryId++;
                tabQuery.id = queryId;
                tabNames[formSelector].push(tabName);
                tabsData.push({
                    hash: hashTabStr,
                    query: tabQuery
                });
                var contentTypeTable = tabContentType[formSelector];
                contentTypeTable.push({
                    hash: hashTabStr,
                    type: "chart"
                });
                getSubscriberData(tabQuery.aggregateType,
                                  tabQuery.searchType,
                                  tabQuery.searchValue,
                                  tabQuery.startDate,
                                  tabQuery.endDate,
                                  function (data) {
                                      data.queryId = queryId;
                                      subscriberData.push(data);
                                      $.unblockUI();
                                  },
                                  formSelector,
                                  ui.index);
            },
            remove: function (event, ui) {
                var queryRemovedId = 0;
                for (var p = (tabNames[formSelector].length - 1); p >= 0; p--) {
                    if (tabNames[formSelector][p] == tabName) {
                        tabNames[formSelector].splice(p, 1);
                    }
                }
                for (var i = (tabsData.length - 1); i >= 0; i--) {
                    if (tabsData[i].hash == ui.tab.hash) {
                        queryRemovedId = tabsData[i].query.id;
                        tabsData.splice(i, 1);
                    }
                }
                for (i = (subscriberData.length - 1); i >= 0; i--) {
                    if (subscriberData[i].queryId == queryRemovedId) {
                        subscriberData.splice(i, 1);
                    }
                }
            }
        };
    }

    $("#top").tabs(tabsConfig("top"));
    $("#bottom").tabs(tabsConfig("bottom"));

    //on add subscriber button click
    $('button.search').live("click", function () {
        var formArea = $(this).parent();
        if (validateInputData(formArea)) {
            var searchVal = formArea.find(".searchParam").val();
            var paneId = formArea.parent().attr("id");
            var paneTabs = tabNames[paneId];
            tabName = getTabName(paneTabs, searchVal);
            tabQuery = getFormData(paneId);
            createVisualisationTab(paneId);
        }
    });

    $.fn.changeVisualisationTab = function(paneId) {
        var escTabName;
        escTabName = tabName.replace(/([()])/g,'\\$1');
        var selTab = $("#" + paneId + " a:contains('" + escTabName + "')").parent();
        var index = $("li", $("#" + paneId).tabs()).index(selTab);
        //remove that tab
        $("#" + paneId).tabs("remove", index);
        createVisualisationTab(paneId);
        changePropWin.close();
    };

    function createVisualisationTab(paneId) {
        var tabs = $('#' + paneId).tabs('add', "#tab" + i, tabName);
        $("#tab" + i).append("<a href='#' class='chartProperties' onclick=showDialog('paneId="+paneId+"&tabName="+tabName+"&tabID=tab"+i+"')>Change properties</a>" +
                             "<div class='chart'></div>" +
                            "<div class='subscriberData'></div>");
        $("#tab" + i).append("<button class='buttonShowSubscriberData'>Show Subscriber Data</button>");

        $(("#tab" + i) + " > button.buttonShowSubscriberData").live("click", function () {
            var tabId = "#" + $(this).parent()[0].id;
            var tabContainerId = $(this).parent().parent()[0].id;
            var contentTypeTable = tabContentType[tabContainerId];
            for (var i = 0; i < contentTypeTable.length; i++) {
                if (contentTypeTable[i].hash == tabId) {
                    if (contentTypeTable[i].type == "chart") {
                        contentTypeTable[i].type = "details";
                        addSubscriberData(tabId);
                        this.textContent = "Hide Subscriber Data";
                    }
                    else {
                        contentTypeTable[i].type = "chart";
                        redrawForHash(tabId);
                        this.textContent = "Show Subscriber Data";
                    }
                }
            }
        });
        i++;
    }

    //close tab on x icon click
    $("span.ui-icon-close").live("click", function () {
        var tabPane = $(this).parent().parent().parent();
        //get the index of the clicked tab
        var index = $("li", tabPane.tabs()).index($(this).parent());
        //remove that tab
        tabPane.tabs("remove", index);
    });

    //initialize datepiceker
    $('#startDatepickerTop').datepick({ dateFormat: 'yyyy-mm-dd' });
    $('#endDatepickerTop').datepick({ dateFormat: 'yyyy-mm-dd' });
    $('#startDatepickerBottom').datepick({ dateFormat: 'yyyy-mm-dd' });
    $('#endDatepickerBottom').datepick({ dateFormat: 'yyyy-mm-dd' });

    //change searchByLabel on radio button change
    $("input.searchByType").live("click", function () {
        var name = this.value + ": ";
        $(this).parent().children(".searchLabel").html(name);
    });

    //select cdr usage types on cdr type change
    $('select.cdr').live("change", function () {
        changeUsageType($(this).parent(), dataTypes);
    });

    $('select.usage').live("change", function () {
        setNameField($(this).parent());
    });

    //add new row on button plus click
    $('button.buttonPlus').live("click", function () {
        addNewRow($(this).parent().parent(), dataTypes,true);
    });

    //remove row on button minus click
    $('button.buttonMinus').live("click", function () {
        $(this).parent().remove();
    });

	$(window).resize(function() {
		var tablinks = $(".ui-tabs-selected").children("a");
		for(var i = 0; i < tablinks.length; i++) {
			var toRedraw = $(tablinks[i]).attr("href");
			redrawForHash(toRedraw);
		}
	});
});

//this is show on click of changeProperties link
function showDialog(urlArgStr) {
    changePropWin = window.open("changeProperties.html?"+urlArgStr, "Window1", "menubar=no,width=430,height=360,toolbar=no");
}

function redrawForHash(hash) {
	console.log(hash);
    var query;
    for (var i = 0; i < tabsData.length; i++) {
        if (tabsData[i].hash == hash) {
            query = tabsData[i].query;
            break;
        }
    }
	if(query == null) {
		return; // not a visualization tab
	}
    var data;
    for (i = 0; i < subscriberData.length; i++) {
        if (query.id == subscriberData[i].queryId) {
            data = subscriberData[i];
            break;
        }
    }
	redraw(hash, query, data);
}

function redraw(id, query, data) {
    $(id + " .subscriberData").empty();
	var aggregateRes = aggregateData(query, data);
	if(aggregateRes == null) {
		var elem = $(id + " .chart");
		elem.html("<p>No call data found for given query</p>");
	} else {
		drawSubscriberGraph(id, query, aggregateRes);
	}
}

function getUsageTypes(cdrType) {
    var type = $.grep(dataTypes, function (tp) {
        return tp.typeDesc == cdrType;
    }).pop();
    return $.map(type.cdrUsages, function (tp) {
        return tp.usageType;
    });
}

function sum2DArray(ar1, ar2) {
    var res = [];
    for (var x = 0; x < ar1.length; x++) {
        res[x] = [];
        for (var y = 0; y < ar1[x].length; y++) {
            res[x][y] = ar1[x][y] + ar2[x][y];
        }
    }
    return res;
}

function create2DArray(xSize) {
    var res = [];
    for (var x = 0; x < xSize; x++) {
        res[x] = [];
    }
    return res;
}

/* Given a query and result date, make a two-dimensional array of the data */
function aggregateData(query, data) {
    var aggregate = create2DArray(7);
    var max = 0;
    var colors = {};
    for (var usageIx = 0; usageIx < query.usageTypes.length; usageIx++) {
        var usageType = query.usageTypes[usageIx];
        colors[usageType.name] = usageType.color;
        var toInclude = [];
        if (usageType.usageType == "All") {
            var allUsageTypes = getUsageTypes(usageType.cdrType);
            for (var i = 0; i < allUsageTypes.length; i++) {
                if (data.usageData[allUsageTypes[i]] !== undefined) {
                    toInclude.push(data.usageData[allUsageTypes[i]]);
                }
            }
        } else {
            if (data.usageData[usageType.usageType] !== undefined) {
                toInclude.push(data.usageData[usageType.usageType]);
            }
        }
        // sum up the relevant data
		if(toInclude.length == 0) {
			continue; // no data for this uT, don't put in aggregation
		}
        var tmpAggregate = toInclude.reduce(function (ut1, ut2) {
            var res = {};
            for (var prop in ut1) {
                res[prop] = sum2DArray(ut1[prop], ut2[prop]);
            }
            return res;
        });

        var measureData = tmpAggregate[query.measureType];
        for (var x = 0; x < measureData.length; x++) {
            for (var y = 0; y < measureData[x].length; y++) {
                if (aggregate[x][y] === undefined) {
                    aggregate[x][y] = {};
                }
                aggregate[x][y][usageType.name] = measureData[x][y];
                if (measureData[x][y] > max) {
                    max = measureData[x][y];
                }
            }
        }
    }

	if(max == 0) {
		// no data at all for this query
		return null;
	}

    return {
        aggregate: aggregate,
        maxUsage: max,
        colors: colors
    };
}

function drawLegend(r, query, width, height) {
    var color = $.map(query.usageTypes, function (tp) { return tp.color; });
    var legendData = $.map(query.usageTypes, function (tp) { return tp.name; });

    //draw legend

    for (var k = 0; k < color.length; k++) {
        r.rect(width, height + (k * 10), 10, 10).attr({ fill: "#" + color[k] });
        r.text(width + 12, height + (k * 10) + 5, legendData[k]).attr(defaultFont);
    }

}

//draws the graph with circles
function drawSubscriberGraph(fragmentId, query, aggregateRes) {
    var aggregate = aggregateRes.aggregate;
    var maxUsage = aggregateRes.maxUsage;

    var colors = $.map(query.usageTypes, function (tp) { return tp.color; });
    var usedUsageTypes = [];
    var currentMaxUsage = 0;

    var elem = $(fragmentId + " .chart").get(0);
    $(fragmentId + " .chart").empty();

    var entireGraphWidth = Math.floor($(fragmentId).innerWidth() - 20);

    var width = $(fragmentId).innerWidth() * mainGraphWidth;
    width = Math.floor(width);

    var entireGraphHeight = Math.floor(($("#top").height() - $(".ui-tabs-nav").outerHeight()));
    var height = ($("#top").height() - $(".ui-tabs-nav").outerHeight()) * mainGraphHeight;
    height = Math.floor(height);

    var r = new Raphael(elem, entireGraphWidth, entireGraphHeight);

    function hoverIn() {
        var text = "", txtDx = 35, txtDy = 35;
        var Complementarycolor = "dddddd";
        for (var tp in this.aggr) {
            text += tp + ": " + this.aggr[tp] + "\n";
        }
        if (width < (this.x + 100)) {
            txtDx = -cellWidth;
        }
        if (height < (this.y + 50)) {
            txtDy = -cellHeight + 10;
        }
        this.text = r.text(this.x + txtDx, this.y + txtDy, text).attr(defaultFont);
        var bbox = this.text.getBBox();
        Complementarycolor = findComplementaryColor(clr.color);
        this.rect = r.rect(bbox.x - 5, bbox.y - 5, bbox.width + 10, bbox.height + 10).attr("fill", "#" + clr.color);
        this.text.attr("fill", "#" + Complementarycolor).toFront();
    }
    function hoverOut() {
        this.rect.remove();
        this.text.remove();
    }

    var x = (width - leftGutter) / axisX.length,
        y = (height - bottomgutter) / axisY.length;

    //draw y axis
    for (var i = 0; i < axisY.length; i++) {
        r.text(25, (y * (i + 0.5)) + 5, axisY[i]).attr(defaultFont);
    }
    //draw x axis
    for (i = 0; i < axisX.length; i++) {
        if (i == 10 || i == 11 || (i >= 13 && i <= 21)) {
            r.text(leftGutter + x * i + 15, height - 5, axisX[i]).attr(defaultFont);
        } else if ((i >= 1) && (i <= 9)) {
            r.text(leftGutter + x * i + 20, height - 5, axisX[i]).attr(defaultFont);
        }
        else {
            r.text(leftGutter + x * i + 10, height - 5, axisX[i]).attr(defaultFont);
        }
    }

    var cellHeight = Math.floor((height - bottomgutter) / 7);
    var cellWidth = Math.floor((width - leftGutter) / 24);
    var maxRadius = Math.min(cellHeight, cellWidth) / 2;
    var correctionFactor = 0;
    //draw circles
    //TODO change hover function
    for (var day = 0; day < 7; day++) {
        for (var hour = 0; hour < 24; hour++) {
            var circles = aggregate[day][hour];
            var pom = 0;
            var toDraw = [];
            for (var usgType in circles) {
                var val = maxRadius * circles[usgType] / maxUsage;
                var clr = $.grep(query.usageTypes, function (ut) { return ut.usageType == usgType; })[0];
                if (clr === undefined) {
                    clr = { color: colors[pom] };
                }
                pom++;
                toDraw.push({
                    usgType: usgType,
                    color: clr.color,
                    radius: val
                });
            }

            toDraw.sort(function (a, b) {
                return b.radius - a.radius;
            });
            if(hour>=7 && hour<=12){
                correctionFactor += hour * 0.1;
            }
            else if(hour>12 && hour<22){
                correctionFactor += hour * 0.02;
            }

            for (i = 0; i < toDraw.length; i++) {
                var tooltipSettings = {
                    aggr: aggregate[day][hour],
                    x: leftGutter + 25 + hour * cellWidth,
                    y: cellHeight / 2 + day * cellHeight
                };
                r.circle(
                    leftGutter + 25 + hour * cellWidth + correctionFactor,
                    cellHeight / 2 + day * cellHeight + 5,
                    toDraw[i].radius
                ).attr(
                    "fill", "#" + toDraw[i].color
                ).hover(
                    hoverIn,
                    hoverOut,
                    tooltipSettings,
                    tooltipSettings
                );
            }
        }
        correctionFactor = 0;
    }

    drawSumOfDataThroughDaySubscriberGraph(r, fragmentId, query, aggregateRes, width);
    drawSumOfDataThroughHoursSubscriberGraph(r, fragmentId, query, aggregateRes, height + 5);
    drawLegend(r, query, width, height);
}

//draw graph with the sum of data through day
function drawSumOfDataThroughDaySubscriberGraph(r, fragmentId, query, aggregateRes, startWidth) {
    var maxUsage = aggregateRes.maxUsage;
    var aggregate = aggregateRes.aggregate;
    var colors = aggregateRes.colors;

    // sum the aggregate
    var dailySum = [];
    for (var x = 0; x < aggregate.length; x++) {
        dailySum[x] = {};
        for (var y = 0; y < aggregate[x].length; y++) {
            for (var prop in aggregate[x][y]) {
                if (y == 0) {
                    dailySum[x][prop] = 0;
                }
                dailySum[x][prop] += aggregate[x][y][prop];
            }
        }
    }

    var maxDailySum = 0;
    for (var i = 0; i < 7; i++) {
        var dayTot = 0;
        for (var tu in dailySum[i]) {
            dayTot += dailySum[i][tu];
        }
        if (dayTot > maxDailySum) {
            maxDailySum = dayTot;
        }
    }

    var width = $(fragmentId).innerWidth() * (1 - mainGraphWidth);
    width = Math.floor(width);
    var availWidth = width - 100;
    var height = ($("#top").height() - $(".ui-tabs-nav").outerHeight()) * mainGraphHeight;
    height = Math.floor(height);

    var barHeight = 20;
    var ySpace = (height - bottomgutter) / axisY.length;

    //draw it
    for (i = 0; i < 7; i++) {
        var dayData = dailySum[i];
        //check if its float
        var dayTotal = 0;
        var xMove = 0;
        for (tu in dayData) {
            r.rect(startWidth + xMove,
                    ySpace * i + barHeight / 2,
                    (dayData[tu] / maxDailySum) * availWidth,
                    barHeight
                   ).attr({
                       stroke: "none",
                       fill: "#" + colors[tu]
                   });
            dayTotal += dayData[tu];
            xMove += (dayData[tu] / maxDailySum) * availWidth;
        }
        dayTotal = toFixed(dayTotal, 2);
        r.text(startWidth + 15 + xMove, ySpace * i + barHeight, dayTotal).attr(defaultFont);
    }
}

//check if number is integer
function isInteger(s) {
    return (s.toString().search(/^-?[0-9]+$/) == 0);
}

//set nubmer to fixed decimal places
function toFixed(value, precision) {
    if (isInteger(value)) {
        return value;
    } else {
        var precision = precision || 0,
			neg = value < 0,
			power = Math.pow(10, precision),
			value = Math.round(value * power),
			integral = String((neg ? Math.ceil : Math.floor)(value / power)),
			fraction = String((neg ? -value : value) % power),
			padding = new Array(Math.max(precision - fraction.length, 0) + 1).join('0');

        return precision ? integral + '.' + padding + fraction : integral;
    }
}

//draw graph with the sum of data through hours
function drawSumOfDataThroughHoursSubscriberGraph(r, fragmentId, query, aggregateRes, startHeight) {
    var maxUsage = aggregateRes.maxUsage;
    var colors = aggregateRes.colors;
    var aggregate = aggregateRes.aggregate;

    // sum the aggregate
    var hourlySum = [];
    for (var y = 0; y < aggregate[0].length; y++) {
        hourlySum[y] = {};
        for (var x = 0; x < aggregate.length; x++) {
            for (var prop in aggregate[x][y]) {
                if (x == 0) {
                    hourlySum[y][prop] = 0;
                }
                hourlySum[y][prop] += aggregate[x][y][prop];
            }
        }
    }

    var maxHourlySum = 0;
    for (var i = 0; i < 24; i++) {
        var hourTot = 0;
        for (var tu in hourlySum[i]) {
            hourTot += hourlySum[i][tu];
        }
        if (hourTot > maxHourlySum) {
            maxHourlySum = hourTot;
        }
    }

    var width = $(fragmentId).innerWidth();
    var height = ($("#top").height() - $(".ui-tabs-nav").outerHeight()) * (0.95 - mainGraphHeight);
    height = Math.floor(height);
    var availHeight = height - 20;
    var barHeight = 20;
    var xSpace = (width * mainGraphWidth - leftGutter - 20) / axisX.length;
    var correctionFactor = 5;
    for (i = 0; i < 24; i++) {
        var hourData = hourlySum[i];
        var hourTotal = 0;
        var yMove = 0;

        if (i >= 1 && i <= 5) {
            correctionFactor += i * 0.6;
        } else if ((i >= 6 && i <= 10) || (i >= 17 && i <= 21)) {
            correctionFactor += i * 0.1;
        } else if (i == 12) {
            correctionFactor += i * 0.2;
        }

        for (tu in hourData) {
            r.rect(leftGutter + xSpace * i + 10 + correctionFactor,
                   startHeight + yMove,
                   barHeight,
                   (hourData[tu] / maxHourlySum) * availHeight
                   ).attr({
                       stroke: "none",
                       fill: "#" + colors[tu]
                   });
            hourTotal += hourData[tu];
            yMove += (hourData[tu] / maxHourlySum) * availHeight;
        }

        hourTotal = toFixed(hourTotal, 2);
        r.text(leftGutter + xSpace * i + 10 + correctionFactor, startHeight + yMove + 10, hourTotal).attr(defaultFont);
    }
}

function changeUsageType(row, dataTypes) {
    var cdrType = $(row).children(".cdr").val();
    var usageType = $(row).children(".usage");
    usageType.children().remove();

    //add option ALL
    var elementAll = document.createElement("option");
    elementAll.innerHTML = "All";
    usageType.append(elementAll);

    //add cdr usage types
    for (var sel = 0; sel < dataTypes.length; sel++) {
        if (dataTypes[sel].typeDesc == cdrType) {
            for (var s = 0; s < dataTypes[sel].cdrUsages.length; s++) {
                var elem = document.createElement("option");
                elem.innerHTML = dataTypes[sel].cdrUsages[s].usageDesc;
                elem.value = dataTypes[sel].cdrUsages[s].usageType;
                usageType.append(elem);
            }
        }
    }

    setNameField(row);
}

function setNameField(row) {
    // set name field
    var cdrType = $(row).children(".cdr").val();
    var usageType = $(row).children(".usage").val();
    $(row).children(".name").val(cdrType + "/" + usageType);
}

//returns new tab name
function getTabName(tabNames, name) {
    //array containing information if name is used (true) or not (value undefined)
    var numersFound = [];
    //value currently assigned to new tab
    var numFound = 0;

    for (var l = 0; l < tabNames.length; l++) {
        //check if there is already tab with this name-index
        if (tabNames[l].indexOf("(") != -1 && tabNames[l].substring(0, tabNames[l].indexOf("(")) == name) {
            numFound = parseInt(tabNames[l].substring(tabNames[l].indexOf("(") + 1, tabNames[l].indexOf(")")));
            numersFound[numFound] = true;
            //looks just for the name
        } else if (tabNames[l] == name) {
            numersFound[0] = true;
        }
    }
    numFound = 0;
    for (l = 0; l < numersFound.length; l++) {
        //free slot found, we do not need to search further
        if (numersFound[l] === undefined) {
            numFound = l;
            break;
        } else {
            numFound++;
        }
    }
    //if needed append index to name
    if (numFound !== 0) {
        name += "(" + numFound + ")";
    }
    return name;
}

function capitalize(text) {
    //capittalizes the first letter of the input text
    return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
}


function customRange(dates) {
    //if from date is lets say 2011-11-7
    //to date cant be anything before from date
    //and vice versa
    if ($(this).hasClass("start")) {
        $(this).parent().find(".end").datepick('option', 'minDate', dates[0] || null);
    } else {
        $(this).parent().find(".start").datepick('option', 'maxDateDate', dates[0] || null);
    }
}

function init() {
    addNewRow($("#topRows"), dataTypes,false);
    addNewRow($("#bottomRows"), dataTypes,false);
    //after populating cdr types and usages
    //show the page
    $.unblockUI();
}

function validateInputData(area) {
    if (tabNames[area.parent().attr("id")].length >= 3) {
        alert("Max 3 tabs");
        return false;
    }

    var dateFormatRegEx = new RegExp('^(19|20)\\d\\d[-](0|1)\\d[-][0-3]\\d$');

    if (area.find(".searchParam").val().length === 0) {
        alert("Enter search by data");
        return false;
    }
    var startDate = area.find(".start").val();
    if (startDate.length === 0) {
        alert("Enter start date");
        return false;
    }

    if (!dateFormatRegEx.test(startDate)) {
        alert("Wrong starte date format" + "\n" + "Should be YYYY-MM-DD");
        return false;
    }
    var endDate = area.find(".end").val();
    if (endDate.length === 0) {
        alert("Enter end date");
        return false;
    }
    if (!dateFormatRegEx.test(endDate)) {
        alert("Wrong end date format" + "\n" + "Should be YYYY-MM-DD");
        return false;
    }
    return true;
}

function getSubscriberData(aggregateType, searchType, searchValue, startDate, endDate, callback, formSelector, tabIndex) {
    $.blockUI({
        message: 'Please wait...',
        css: {
            border: 'none',
            padding: '25px',
            backgroundColor: '#000',
            '-webkit-border-radius': '20px',
            '-moz-border-radius': '20px',
            opacity: 0.5,
            color: '#fff'
        }
    });
    $.getJSON(
        subscriberDataUrl({
            searchValue: searchValue,
            startDate: startDate,
            endDate: endDate,
            aggregateType: aggregateType,
            searchType: searchType
        }),
        callback
    ).error(function (jqXHR, textStatus, errorThrown) {
        $.unblockUI();
        if (jqXHR.status == 404) {
            alert(jqXHR.responseText);
        }
        $('#' + formSelector).tabs("remove", tabIndex);
    });
}

function getFormData(tab) {
    var tabElem = $("#" + tab),
        chosen = tabElem.find("input.aggregationType:checked").val(),
        searchType1 = tabElem.find("input.searchByType:checked").val(),
        imsivalue = tabElem.find("input.searchParam").val(),
        startDate1 = tabElem.find("input.start").val(),
        endDate1 = tabElem.find("input.end").val(),
        cdrTypesRows = tabElem.find("select.cdr"),
        usageTypesRows = tabElem.find("select.usage"),
        namesRows = tabElem.find("input.name"),
        colorsRows = tabElem.find("input.color"),

        usageTypes = [];

    for (var i = 0; i < cdrTypesRows.length; i++) {
        usageTypes.push({
            cdrType: cdrTypesRows[i].options[cdrTypesRows[i].selectedIndex].value,
            usageType: usageTypesRows[i].options[usageTypesRows[i].selectedIndex].value,
            name: namesRows[i].value,
            color: colorsRows[i].value
        });
    }

    var measureType = tabElem.find(".measureType").val();

    return {
        searchValue: imsivalue,
        startDate: startDate1,
        endDate: endDate1,
        aggregateType: chosen,
        measureType: measureType,
        searchType: searchType1,
        usageTypes: usageTypes
    };
}

function getCdrTypes() {
    $.blockUI({
        message: 'Fetching data... \n Please wait...',
        css: {
            border: 'none',
            padding: '25px',
            backgroundColor: '#000',
            '-webkit-border-radius': '20px',
            '-moz-border-radius': '20px',
            opacity: 0.5,
            color: '#fff'
        }
    });

    //request for json
    $.getJSON(requestCdrTypesUrl(), function (result) {
        dataTypes = result;
        init();
    }).error(function (jqXHR, textStatus, errorThrown) {
        alert("Failed to fetch CDR Types from server");
        $.unblockUI();
    });
}

function addNewRow(divArea, dataTypes,allowRemove) {
    //when a + button is pressed now row appears
    //no need to study this function
    //it creates DOM elements and adds them to page

    var rowID = 0;
    var nameLabel = divArea.attr('id');

    var newRow = document.createElement("div");

    var lbl = document.createElement("label");
    lbl.setAttribute("for", "cdrType" + capitalize(nameLabel) + "ID" + rowID);
    lbl.setAttribute("class", "labelprops extramargin");
    lbl.innerHTML = "CDR Type:";

    newRow.appendChild(lbl);

    var select = document.createElement("select");
    select.setAttribute("id", "cdrType" + capitalize(nameLabel) + "ID" + rowID);
    select.setAttribute("name", nameLabel + rowID);
    select.setAttribute("class", "cdr");

    //select all CDR types from object
    for (var sel = 0; sel < dataTypes.length; sel++) {
        var option = document.createElement("option");
        if (sel === 0) {
            option.setAttribute("selected", "selected");
        }
        option.setAttribute("value", dataTypes[sel].typeDesc);
        option.innerHTML = dataTypes[sel].typeDesc;
        select.appendChild(option);
    }

    newRow.appendChild(select);

    //all usage type for every cdr type

    lbl = document.createElement("label");
    lbl.setAttribute("for", "usageType" + capitalize(nameLabel) + rowID);
    lbl.setAttribute("class", "leftmargin");
    lbl.innerHTML = "Usage Type:";

    newRow.appendChild(lbl);

    select = document.createElement("select");
    select.setAttribute("id", "usageType" + capitalize(nameLabel) + rowID);
    select.setAttribute("class", "usage");

    newRow.appendChild(select);

    lbl = document.createElement("label");
    lbl.setAttribute("for", "itemName" + capitalize(nameLabel) + rowID);
    lbl.setAttribute("class", "leftmargin");
    lbl.innerHTML = "Item Name:";

    newRow.appendChild(lbl);

    var input = document.createElement("input");
    input.setAttribute("id", "itemName" + capitalize(nameLabel) + rowID);
    input.setAttribute("type", "text");
    input.setAttribute("value", "");
    input.setAttribute("class", "name");
    newRow.appendChild(input);

    input = document.createElement("input");
    input.setAttribute("id", "row" + capitalize(nameLabel) + "ID" + rowID);
    input.setAttribute("value", Raphael.getColor());
    input.setAttribute("name", "name" + rowID);
    input.setAttribute("class", "color");

    input.setAttribute("onChange", "");
    var col = new jscolor.color(input);
    newRow.appendChild(input);

    input = document.createElement("button");
    input.setAttribute("class", "buttonPlus");
    input.setAttribute("value", divArea);
    input.innerHTML = "+";
    newRow.appendChild(input);

    input = document.createElement("button");
    input.setAttribute("class", "buttonMinus");
    input.setAttribute("value", divArea);
    if (allowRemove == false) input.setAttribute("disabled","true");
    input.innerHTML = "-";
    newRow.appendChild(input);

    var idElem = nameLabel + "Rows";
    divArea.append(newRow);
    changeUsageType(newRow, dataTypes);
}


// this function returns hsl color for rgb input. hsl conversion required for nice complementary colors
function convertRgbToHsl(orgRgbColor) {
    'use strict';
    var splitCodeArr, decR, decG, decB, maxDecVal, minDecVal, diffDecVal, hue, saturation, lightness, diffDecR, diffDecG, diffDecB;
    splitCodeArr = orgRgbColor.split("");
    decR = parseInt(splitCodeArr[0] + splitCodeArr[1], 16) / 255; // converting hex 2 dec fraction
    decG = parseInt(splitCodeArr[2] + splitCodeArr[3], 16) / 255;
    decB = parseInt(splitCodeArr[4] + splitCodeArr[5], 16) / 255;

    //find maximum, minimum and diff of max and min
    maxDecVal = Math.max(decR, decG, decB);
    minDecVal = Math.min(decR, decG, decB);
    diffDecVal = maxDecVal - minDecVal;

    lightness = (maxDecVal + minDecVal) / 2;

    if (diffDecVal === 0) {
        //gray color
        hue = 0;
        saturation = 0;
    } else {
        if (lightness < 0.5) {
            saturation = diffDecVal / (maxDecVal + minDecVal);
        } else {
            saturation = diffDecVal / (2 - maxDecVal - minDecVal);
        }

        diffDecR = (((maxDecVal - decR) / 6) + (diffDecVal / 2)) / diffDecVal;
        diffDecG = (((maxDecVal - decG) / 6) + (diffDecVal / 2)) / diffDecVal;
        diffDecB = (((maxDecVal - decB) / 6) + (diffDecVal / 2)) / diffDecVal;

        if (decR === maxDecVal) {
            hue = diffDecB - diffDecG;
        } else if (decG === maxDecVal) {
            hue = (1 / 3) + diffDecR - diffDecB;
        } else if (decB === maxDecVal) {
            hue = (2 / 3) + diffDecG - diffDecR;
        }

        if (hue < 0) {
            hue += 1;
        }
        if (hue > 1) {
            hue -= 1;
        }
    }

    // return hsl
    return {
        "hue": hue,
        "saturation": saturation,
        "lightness": lightness
    };
}



// this function return rgb color for hsl input.
function covertHslToRgb(hue, saturation, lightness) {
    'use strict';
    var decR, decG, decB, hexRGB, hexG, hexB, temp1, temp2;

    //this private function return unit hue to rgb in decimal fraction
    function unitHueToRgbDecFrac(temp1, temp2, hue) {
        if (hue < 0) {
            hue += 1;
        }
        if (hue > 1) {
            hue -= 1;
        }
        if ((6 * hue) < 1) {
            return (temp1 + (temp2 - temp1) * 6 * hue);
        }
        if ((2 * hue) < 1) {
            return (temp2);
        }
        if ((3 * hue) < 2) {
            return (temp1 + (temp2 - temp1) * ((2 / 3) - hue) * 6);
        }
        return temp1;
    }

    //HSL from 0 to 1
    if (saturation === 0) {
        //RGB results from 0 to 255
        decR = lightness * 255;
        decG = lightness * 255;
        decB = lightness * 255;
    } else {
        if (lightness < 0.5) {
            temp2 = lightness * (1 + saturation);
        } else {
            temp2 = (lightness + saturation) - (saturation * lightness);
        }

        temp1 = 2 * lightness - temp2;

        decR = 255 * unitHueToRgbDecFrac(temp1, temp2, hue + (1 / 3));
        decG = 255 * unitHueToRgbDecFrac(temp1, temp2, hue);
        decB = 255 * unitHueToRgbDecFrac(temp1, temp2, hue - (1 / 3));
    }

    hexRGB = ("0" + Math.round(decR).toString(16)).slice(-2);
    hexG = ("0" + Math.round(decG).toString(16)).slice(-2);
    hexB = ("0" + Math.round(decB).toString(16)).slice(-2);
    return hexRGB.concat(hexG, hexB);

}

function findComplementaryColor(orgRgbColor) {
    'use strict';
    var hsl, complementHue, complementLight, complementColor;
    // converting RGB color to hue, saturation and lightness for complementary color
    hsl = convertRgbToHsl(orgRgbColor);

    //change hue and lightness for nice complementary color
    complementHue = hsl.hue + 0.5;
    if (complementHue > 1) {
        complementHue -= 1;
    }
    complementLight = hsl.lightness + 0.5;
    if (complementLight > 1) {
        complementLight -= 1;
    }
    //converting complement color from hsl to rgb
    complementColor = covertHslToRgb(complementHue, hsl.saturation, complementLight);
    return complementColor;
}

function addSubscriberData(hash) {
    $(hash + " .chart").empty();
    $(hash + " .subscriberData").empty();

    var queryId;
    var subscriber;

    for (var i = (tabsData.length - 1); i >= 0; i--) {
        if (tabsData[i].hash == hash) {
            queryId = tabsData[i].query.id;
            break;
        }
    }
    for (i = (subscriberData.length - 1); i >= 0; i--) {
        if (subscriberData[i].queryId == queryId) {
            subscriber = subscriberData[i].subscriber;
        }
    }
    $(hash + " .subscriberData").append("<ul>" +
        "<li>Id: " + subscriber.subscriberId + "</li>" +
        "<li>Name:" + subscriber.subscriberName + "</li>" +
        "<li>Address: " + subscriber.officialAddress + "</li>" +
        "<li>Type: " + subscriber.subscriberType + "</li>"+
        "<li>CustomerID: " + subscriber.customerId + "</li>" +
        "<li>IMSI: " + subscriber.imsi + "</li>" +
        "<li>MSISDN: " + subscriber.msisdn + "</li>"+
		"</ul>");
}