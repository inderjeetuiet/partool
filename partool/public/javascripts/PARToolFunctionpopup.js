"use strict";
var dataTypes;

var paneId = getParamByName("paneId");
var tabName = getParamByName("tabName");
var tabID = getParamByName("tabID");


$(function () {
    getCdrTypes();

    var i = 0;
    //on add subscriber button click
    $('#addSubscriberTop').live("click", function () {
	    window.opener.tabQuery = getPopupFormData();
        window.opener.tabName = tabName;
		window.opener.$.fn.changeVisualisationTab(paneId);
		
	});

    
    //close tab on x icon click
    $("span.ui-icon-close").live("click", function () {
        var tabPane = $(this).parent().parent().parent();
        //get the index of the clicked tab
        var index = $("li", tabPane.tabs()).index($(this).parent());
        //remove that tab
        tabPane.tabs("remove", index);
    });

    //initialize datepiceker
   

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
        addNewRowpopup($(this).parent().parent(), dataTypes);
    });

    //remove row on button minus click
    $('button.buttonMinus').live("click", function () {
        $(this).parent().remove();
    });
});


// get url parameters by name
function getParamByName(pName) {
    var re = RegExp('[?&]'+ pName +'=([^&]*)').exec(window.location.search);
    return re && decodeURIComponent(re[1].replace(/\+/g, ' '));

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
                elem.innerHTML = dataTypes[sel].cdrUsages[s].usageType;
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


function capitalize(text) {
    //capittalizes the first letter of the input text
    return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
}

function getSubscriberData1(aggregateType, searchType, searchValue, startDate, endDate, callback) {
    $.getJSON(
        subscriberDataUrl1({
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


function getPopupFormData() {
		var searchType;
		var startDate;
		var endDate;
		if((window.opener.document.getElementById('searchByImsiTop').checked) || (window.opener.document.getElementById('searchByImsiBottom').checked)) {
		searchType = "IMSI";
		}
		else{
		searchType="MSISDN";
		}
		if((window.opener.document.getElementById('startDatepickerTop').value!=null) && (window.opener.document.getElementById('endDatepickerTop').value!=null)){
		startDate= window.opener.document.getElementById('startDatepickerTop').value;
		endDate=window.opener.document.getElementById('endDatepickerTop').value;
		} 
		else{
		startDate= window.opener.document.getElementById('startDatepickerBottom').value;
		endDate=window.opener.document.getElementById('endDatepickerBottom').value;
		}
		var tabElem = $("#popuptop"),
		imsivalue = window.opener.document.getElementById('searchParameterTop').value,
		chosen = tabElem.find("input.aggregationType1:checked").val(),
        searchType1= searchType,
        //imsivalue = tabElem.find("input.searchParam1").val(),
        startDate1 = startDate,
        endDate1 = endDate,
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
     //request for json
    $.getJSON(requestCdrTypesUrl11(), function (result) {
        dataTypes = result;
        init1();
    }).error(function(jqXHR, textStatus, errorThrown) {
        alert("Failed to fetch CDR Types from server");
        $.unblockUI();
    });
}
function init1(){
	addNewRowpopup($("#popuptopRows"), dataTypes);
	}


function addNewRowpopup(divArea, dataTypes) {
    //when a + button is pressed now row appears
    //no need to study this function
    //it creates DOM elements and adds them to page
    var rowID = 0;
    var nameLabel = divArea.attr('popuptop');

    var newRow = document.createElement("div");

    var lbl = document.createElement("label");
    lbl.setAttribute("for", "cdrType"+ rowID);
    lbl.setAttribute("class", "labelprops extramargin");
    lbl.innerHTML = "CDR Type:";

    newRow.appendChild(lbl);

    var select = document.createElement("select");
    select.setAttribute("id", "cdrType" + "ID" + rowID);
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
    lbl.setAttribute("for", "usageType"  + rowID);
    lbl.setAttribute("class", "leftmargin");
    lbl.innerHTML = "Usage Type:";

    newRow.appendChild(lbl);

    select = document.createElement("select");
    select.setAttribute("id", "usageType"  + rowID);
    select.setAttribute("class", "usage");

    newRow.appendChild(select);

    lbl = document.createElement("label");
    lbl.setAttribute("for", "itemName"  + rowID);
    lbl.setAttribute("class", "leftmargin" );
    lbl.innerHTML = "Item Name:";

    newRow.appendChild(lbl);

    var input = document.createElement("input");
    input.setAttribute("id", "itemName" + rowID);
    input.setAttribute("type", "text");
    input.setAttribute("value", "");
    input.setAttribute("class", "name");
    newRow.appendChild(input);

    input = document.createElement("input");
    input.setAttribute("id", "row"  + "ID" + rowID);
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
    input.innerHTML = "-";
    newRow.appendChild(input);

    var idElem = nameLabel + "Rows";
    divArea.append(newRow);
    changeUsageType(newRow, dataTypes);
}
