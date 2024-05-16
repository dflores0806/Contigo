$(document).ready(function() {

	// Loading
	$('#loading').hide();

	// Sidebar
	$("#sidebar").mCustomScrollbar({
		theme: "minimal"
	});

	$('#dismiss, .overlay').on('click', function() {
		$('#sidebar').removeClass('active');
		$('.overlay').removeClass('active');
	});

	$("input[name='opt-vis']").click(function() {
		checkVis();
	});

	$("input[name='heatmap']").click(function() {
		checkHeatmap();
	});

	$('#mos-res-cl').change(function() {
		checkClustering();
	});

	$('#mos-res-cir').change(function() {
		checkCircle();
	});

	curLang = $("#selLang").val();

	initMap();

});

var $table = $('#medical-data-tab')
var $remove = $('#remove')
var selections = []

// Vars
var curMarkers = []; // Todos los marcadores
var curMarkersOk = []; // Respuestas Ok
var curMarkersAl = []; // Respuestas Alerta
var curMarkersAlObj = []; // Respuestas Alerta objetos

var markerCluster; // Clustering
var markerClusterOk; // Clustering
var markerClusterAl; // Clustering

var curLatlng = [];
var curLatlngOk = [];
var curLatlngAl = [];

var curHeatmap;
var curHeatmapOk;
var curHeatmapAl;

let medicalDataChart;

var gradient = ['rgba(255, 255, 0, 0)', 'rgba(255, 255, 0, 1)',
	'rgba(191, 255, 0, 1)', 'rgba(127, 255, 0, 1)', 'rgba(63, 255, 0, 1)',
	'rgba(0, 255, 0, 1)', 'rgba(0, 0, 223, 1)', 'rgba(0, 0, 191, 1)',
	'rgba(0, 0, 159, 1)', 'rgba(0, 0, 127, 1)', 'rgba(0, 63, 91, 1)',
	'rgba(0, 127, 63, 1)', 'rgba(0, 191, 31, 1)', 'rgba(0, 255, 0, 1)']

var curMarkersOkUUID = []; // UUID Respuestas Ok
var curMarkersAlUUID = []; // UUID Respuestas Alerta

var allData = [];

var infoWindows = [];
var lastMarker;
var posMarker;
var pos;
var map;
var cityCircle = null;
var visMode;

var curPosition = "";
var markers = [];

var position = ""
var json = ""
var lat = ""
var lng = ""
var radius = ""
var minActivityTime = ""
var duringActivityTime = ""

var prev_infowindow = false;

// Chart vars
var labels = [];
var chtrestbps = [];
var chfbs = [];
var chthalach = [];
var chthal = [];
var chtarget = [];
var chprediction = [];
var chname = "";

// Validate data
var valUuid = ""
var valName = ""
var valHealthData = ""
var valResource = "Retrain"

var curLang = ""

function getIdSelections() {
	return $.map($table.bootstrapTable('getSelections'), function(row) {
		return row.id
	})
}

function responseHandler(res) {
	$.each(res.rows, function(i, row) {
		row.state = $.inArray(row.id, selections) !== -1
	})
	return res
}

function detailFormatter(index, row) {
	var html = []
	$.each(row, function(key, value) {
		html.push('<p><b>' + key + ':</b> ' + value + '</p>')
	})
	return html.join('')
}

function operateFormatter(value, row, index) {
	return [
		'<a class="remove" href="javascript:void(0)" title="Remove">',
		'<i class="fa fa-trash"></i>',
		'</a>'
	].join('')
}

window.operateEvents = {
	'click .like': function(e, value, row, index) {
		alert('You click like action, row: ' + JSON.stringify(row))
	},
	'click .remove': function(e, value, row, index) {

		var msg = "Do you want to delete the record? You will not be able to recover it"
		if (curLang == "es_ES")
			msg = "¿Deseas borrar el registro? No se podrá recuperar"

		if (window.confirm(msg)) {
			$table.bootstrapTable('remove', {
				field: 'id',
				values: [row.id]
			})

			// Find value in JSON and recharge the chart
			chtrestbps.splice(index, 1);
			chfbs.splice(index, 1);
			chthalach.splice(index, 1);
			chthal.splice(index, 1);
			chtarget.splice(index, 1);
			chprediction.splice(index, 1);

			configureChart();
		}
	}
}


function initTable() {
	$table.bootstrapTable('destroy').bootstrapTable({
		height: 550,
		locale: "en-US",
		customToolbarButtons: [
			{
				name: "btn-validate",
				title: " " + fmtMedicalValidation,
				icon: "bi bi-check-lg",
				callback: validateMedicalData
			}
		],
		columns: [
			[{
				title: 'ID',
				field: 'id',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Timestamp',
				field: 'Timestamp',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Trestbps',
				field: 'Trestbps',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'FBS',
				field: 'FBS',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Thalach',
				field: 'Thalach',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Thal',
				field: 'Thal',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Target',
				field: 'Target',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Prediction (%)',
				field: 'Prediction (%)',
				align: 'center',
				valign: 'middle',
				sortable: true
			}, {
				title: 'Reject',
				field: 'Reject',
				align: 'center',
				clickToSelect: false,
				events: window.operateEvents,
				formatter: operateFormatter
			}]
		]
	})
	$table.on('check.bs.table uncheck.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table',
		function() {
			$remove.prop('disabled', !$table.bootstrapTable('getSelections').length)
			// save your data, here just save the current page
			selections = getIdSelections()
			// push or splice the selections if you want to save all data selections
		})
	$table.on('all.bs.table', function(e, name, args) {
		//console.log(name, args)
	})
	$remove.click(function() {
		var ids = getIdSelections()
		$table.bootstrapTable('remove', {
			field: 'id',
			values: ids
		})
		$remove.prop('disabled', true)
	})
}

var validateMedicalData = function() {
	var validateDataJson = {
		"uuid": valUuid,
		"resource": valResource,
		"healthData": valHealthData
	}

	var csrf_token = $("#csrf-token").val()

	var msg = "You will validate the data of '" + valName
		+ "'. These data will be used in the retraining of the prediction model. Would you like to continue?"
	if (curLang == "es_ES")
		msg = "Va a validar los datos de '" + valName
			+ "'. Estos datos serán utilizados en el reentrenamiento del modelo de predicción. \u00BFDesea continuar?"

	var r = confirm(msg);
	if (r == true) {
		// Send ajax request
		$.ajax({
			url: "validateData",
			type: "POST",
			data: { _csrf: csrf_token, data: JSON.stringify(validateDataJson) },
			success: function(data) {

				var msg = "Data validated successfully!"
				if (curLang == "es_ES")
					msg = "\u00A1Datos validados correctamente!"

				alert(msg);
			},
			error: function(response) {
				var msg = "An error has occurred.\n\n"
				if (curLang == "es_ES")
					msg = "Se ha producido un error.\n\n"
				alert(msg + "Code: "
					+ response.status);
			}
		});
	}
}

$(function() {
	initTable()
	$('#locale').change(initTable)
})

function printValueActivity(sliderID, textbox) {
	var x = document.getElementById(textbox);
	var y = document.getElementById(sliderID);
	x.value = y.value;

	updateSites();

}

function updateLanguage() {
	curLang = $("#selLang").val();
	localStorage.setItem("lang", curLang);
	$("#selLangForm").submit();
}

function initMap() {

	var mapZoom = 5;
	var mapCenter = {
		lat: 39.475088,
		lng: -6.371472
	};

	map = new google.maps.Map(document.getElementById('map'), {
		center: {
			lat: 40.4378698,
			lng: -3.8196207
		},
		zoom: 5
	});

	// Try HTML5 geolocation.
	if (navigator.geolocation) {
		user_location = true;
		navigator.geolocation
			.getCurrentPosition(
				function(position) {
					user_location = true;
					var pos = {
						lat: position.coords.latitude,
						lng: position.coords.longitude
					};

					posMarker = new google.maps.Marker(
						{
							position: pos,
							title: "Estoy aqui",
							icon: "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_black.png"
						});
					map.setZoom(15);
					posMarker.setMap(map);

					lastMarker = new google.maps.Marker(
						{
							position: pos,
							title: "Estoy aqui",
							icon: "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_blue%23.png"
						});
					map.setZoom(15);
					// lastMarker.setMap(map);

					// infoWindow.setPosition(pos);
					// infoWindow.setContent('Estás aqui');
					map.setCenter(pos);
				},
				function() {
					var pos = {
						lat: 40.416775,
						lng: -3.703790
					};

					posMarker = new google.maps.Marker(
						{
							position: pos,
							icon: "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_black.png"
						});
					map.setZoom(7);
					posMarker.setMap(map);

					lastMarker = new google.maps.Marker(
						{
							position: pos,
							icon: "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_blue%23.png"
						});
					map.setZoom(7);
					// lastMarker.setMap(map);
					map.setCenter(pos);

				});
	} else {
		// Browser doesn't support Geolocation
		position = "";
		// handleLocationError(false, infoWindow, map.getCenter());
	}

	google.maps.event.addListener(map, 'click', function(event) {
		$("#sidebarCollapse").removeClass("disabled");
		$('#sidebarCollapse').on('click', function() {
			$('#sidebar').addClass('active');
			$('.overlay').addClass('active');
			$('.collapse.in').toggleClass('in');
			$('a[aria-expanded=true]').attr('aria-expanded', 'false');
		});

		$("#info-msg").slideUp();

		curPosition = event.latLng;
		placeMarker(event.latLng);

		$("#mos-res-cir").prop("checked", true);

		// Generate random markers
		var bounds = cityCircle.getBounds();
		// map.fitBounds(bounds);

	});

	lastMarker = new google.maps.Marker({
		position: pos,
		title: "Estoy aqui"
	});
	lastMarker.setMap(map);

}

function checkHeatmap() {
	var radioValue = $("input[name='heatmap']:checked").val();
	if (radioValue == 0) {
		// curHeatMap.setMap(null);
		curHeatMapOk.setMap(null);
		curHeatMapAl.setMap(null);
	} else if (radioValue == 1) {
		// curHeatMap.setMap(map);
		if (curHeatMapOk.getMap() == null)
			curHeatMapOk.setMap(map);
		if (curHeatMapAl.getMap() == null)
			curHeatMapAl.setMap(map);
	} else if (radioValue == 2) {
		// curHeatMap.setMap(null);
		if (curHeatMapOk.getMap() == null)
			curHeatMapOk.setMap(map);
		curHeatMapAl.setMap(null);
	} else if (radioValue == 3) {
		// curHeatMap.setMap(null);
		curHeatMapOk.setMap(null);
		if (curHeatMapAl.getMap() == null)
			curHeatMapAl.setMap(map);
	}
}

function checkVis() {
	// 0. Todo, 1. Ok, 2. ¡Alerta!, 3. Ocultar
	var radioValue = $("input[name='opt-vis']:checked").val();
	var cl = $("#mos-res-cl").prop("checked");
	if (radioValue == 0) {

		clearMarkers(curMarkersAl);
		if (markerClusterAl != null)
			markerClusterAl.clearMarkers();

		clearMarkers(curMarkersOk);
		if (markerClusterOk != null)
			markerClusterOk.clearMarkers();

		clearMarkers(curMarkers);
		if (markerCluster != null & cl == true) {
			markerCluster.clearMarkers();
			markerCluster = new MarkerClusterer(
				map,
				curMarkers,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
		}

		showMarkers(curMarkers);

	} else if (radioValue == 1) {

		clearMarkers(curMarkers);
		if (markerCluster != null)
			markerCluster.clearMarkers();

		clearMarkers(curMarkersOk);
		if (markerClusterOk != null & cl == true) {
			markerClusterOk.clearMarkers();
			markerClusterOk = new MarkerClusterer(
				map,
				curMarkersOk,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
		}

		clearMarkers(curMarkersAl);
		if (markerClusterAl != null)
			markerClusterAl.clearMarkers();

		showMarkers(curMarkersOk);

	} else if (radioValue == 2) {

		clearMarkers(curMarkers);
		if (markerCluster != null)
			markerCluster.clearMarkers();

		clearMarkers(curMarkersOk);
		if (markerClusterOk != null)
			markerClusterOk.clearMarkers();

		clearMarkers(curMarkersAl);
		if (markerClusterAl != null & cl == true) {
			markerClusterAl.clearMarkers();
			markerClusterAl = new MarkerClusterer(
				map,
				curMarkersAl,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
		}

		showMarkers(curMarkersAl);

	} else if (radioValue == 3) {

		clearMarkers(curMarkers);
		if (markerCluster != null)
			markerCluster.clearMarkers();

		clearMarkers(curMarkersAl);
		if (markerClusterAl != null)
			markerClusterAl.clearMarkers();

		clearMarkers(curMarkersOk);
		if (markerClusterOk != null)
			markerClusterOk.clearMarkers();

	}

}

function checkCircle() {
	if ($("#mos-res-cir").prop("checked") == false) {
		cityCircle.setOptions({
			fillOpacity: 0,
			strokeOpacity: 0
		});
	} else {
		cityCircle.setOptions({
			strokeColor: '#FF0000',
			strokeOpacity: 0.8,
			strokeWeight: 2,
			fillColor: '#FF0000',
			fillOpacity: 0.35,
		});
	}
}

function checkClustering() {

	if ($("#mos-res-cl").prop("checked") == true) {

		clearMarkers(curMarkers);
		markerCluster.clearMarkers();

		clearMarkers(curMarkersOk);
		markerClusterOk.clearMarkers();

		clearMarkers(curMarkersAl);
		markerClusterAl.clearMarkers();

		var radioValue = $("input[name='opt-vis']:checked").val();
		if (radioValue == 0) {
			markerCluster = new MarkerClusterer(
				map,
				curMarkers,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
			showMarkers(curMarkers);
		} else if (radioValue == 1) {
			markerClusterOk = new MarkerClusterer(
				map,
				curMarkersOk,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
			showMarkers(curMarkersOk);
		} else if (radioValue == 2) {
			showMarkers(curMarkersAl);
			markerClusterAl = new MarkerClusterer(
				map,
				curMarkersAl,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
		}

	} else {

		var radioValue = $("input[name='opt-vis']:checked").val();

		clearMarkers(curMarkers);
		markerCluster.clearMarkers();

		clearMarkers(curMarkersOk);
		markerClusterOk.clearMarkers();

		clearMarkers(curMarkersAl);
		markerClusterAl.clearMarkers();

		if (radioValue == 0) {
			showMarkers(curMarkers);
		} else if (radioValue == 1) {
			showMarkers(curMarkersOk);
		} else if (radioValue == 2) {
			showMarkers(curMarkersAl);
		}

	}

}

function clearMarkers(map) {
	for (var i = 0; i < curMarkers.length; i++) {
		curMarkers[i].setMap(map);
	}
}

function placeMarker(location) {
	lastMarker.setMap(null);
	lastMarker = new google.maps.Marker(
		{
			position: location,
			title: "Estoy aqui",
			icon: "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_blue%23.png"
		});

	// Circulo
	if (cityCircle != null)
		cityCircle.setMap(null);

	cityCircle = new google.maps.Circle({
		strokeColor: '#FF0000',
		strokeOpacity: 0.8,
		strokeWeight: 2,
		fillColor: '#FF0000',
		fillOpacity: 0.35,
		map: map,
		center: location,
		radius: 100
	});

	var y = document.getElementById("sliderValue");

	cityCircle.setRadius(parseInt(y.value));
	// Fin circulo

	lastMarker.setMap(map);
	// map.setCenter(lastMarker.position);
	updateSites();

}

function closeAllInfoWindows() {
	for (var i = 0; i < infoWindows.length; i++) {
		infoWindows[i].close();
	}
}

function fitBounds() {

	// Set map zoom and limits
	var bounds = new google.maps.LatLngBounds();
	var cantidad = 0;

	map.data.forEach(function(feature) {
		processPoints(feature.getGeometry(), bounds.extend, bounds);
		cantidad++;
	});

	for (var j = 0; j < markers.length; j++) {
		bounds.extend(markers[j].getPosition());
		cantidad++;
	}

	if (cantidad > 0) {
		map.fitBounds(bounds);
	}
}

function printValue(sliderID, textbox) {
	var x = document.getElementById(textbox);
	var y = document.getElementById(sliderID);
	x.value = y.value;

	if (cityCircle != null)
		cityCircle.setRadius(parseInt(y.value));

	updateSites();

}

function printValueActivity(sliderID, textbox) {
	var x = document.getElementById(textbox);
	var y = document.getElementById(sliderID);
	x.value = y.value;

	updateSites();

}

function changeStyle() {
	map.data.setStyle({
		clickable: false,

		strokeColor: 'MidnightBlue',
		fillColor: 'DodgerBlue',
		strokeOpacity: '1.0',
		strokeWeight: 2
	});
}

function processPoints(geometry, callback, thisArg) {
	if (geometry instanceof google.maps.LatLng) {
		callback.call(thisArg, geometry);
	} else if (geometry instanceof google.maps.Data.Point) {
		callback.call(thisArg, geometry.get());
	} else {
		geometry.getArray().forEach(function(g) {
			processPoints(g, callback, thisArg);
		});
	}
}

function generarListado(f) {
	// f = formato: pdf o excel

	var msg = "Generate report in " + f + "?"
	if (curLang == "es_ES")
		msg = "¿Generar listado en " + f + "?"

	var r = confirm(msg);
	if (r == true) {

		var params = {
			"latitude": lat,
			"longitude": lng,
			"radius": radius,
			"minActivityTime": minActivityTime,
			"range": duringActivityTime,
		};

		$.ajax({
			url: "report",
			type: "GET",
			dataType: "json",
			contentType: "application/json",
			cache: false,
			data: {
				"params": JSON.stringify(params),
				"curMarkersAl": JSON.stringify(curMarkersAlObj),
				"formato": f,
				"lang": curLang
			},
			success: function(response) {
				// alert("\u00A1Listado generado correctamente!");
				//console.log("success")

			},
			error: function(response) {
				// alert("!!Se ha producido un error.\n\n" + "Code: "
				// + response.status);
				//console.log("error")

			}
		});
	}

}

function buscar() {

	$('#loading').show();

	deleteMarkers();

	// Reset medical data options
	$('#btn-chart-download').prop('disabled', true);
	$('#btn-validate-data').prop('disabled', true);
	document.querySelector('#myTab > li:first-child button').click()
	document.querySelector('#graph-user-sel').value = 0
	$('#medical-data-tab').bootstrapTable('removeAll')
	if (medicalDataChart) {
		medicalDataChart.destroy();
	}

	position = JSON.stringify(curPosition);
	json = JSON.parse(position);
	lat = json["lat"];
	lng = json["lng"];
	radius = $("#sliderValue").val();
	minActivityTime = $("#slider-actValue").val();
	duringActivityTime = $('input:radio[name="horasActividad"]:checked').val();

	// Borrar cuando MQTT este hecho
	var bounds = cityCircle.getBounds();
	// map.fitBounds(bounds);
	var sw = bounds.getSouthWest();
	var ne = bounds.getNorthEast();

	var swlat = sw.lat()
	var swlng = sw.lng()
	var nelat = ne.lat()
	var nelng = ne.lng()
	// Fin borrar

	$
		.ajax({
			url: "find",
			type: "GET",
			dataType: "json",
			data: {
				"swlat": swlat, // Eliminar cuando MQTT este listo
				"swlng": swlng, // Eliminar cuando MQTT este listo
				"nelat": nelat, // Eliminar cuando MQTT este listo
				"nelng": nelng, // Eliminar cuando MQTT este listo
				"lat": lat,
				"lng": lng,
				"radius": radius,
				"duringActivityTime": duringActivityTime,
				"minActivityTime": minActivityTime
			},
			success: function(data) {

				$('#loading').hide();

				data.sort(function(a, b) {
					return compareStrings(a.name, b.name);
				})



				$('#sidebar').removeClass('active');
				$('.overlay').removeClass('active');

				showActivities(data);

				var repParams = {
					"latitude": lat,
					"longitude": lng,
					"radius": radius,
					"minActivityTime": minActivityTime,
					"range": duringActivityTime,
				};

				$("#res-lis-pdf-params").val(JSON.stringify(repParams));
				$("#res-lis-pdf-markers").val(
					JSON.stringify(curMarkersAlObj));

				$("#res-lis-xls-params").val(JSON.stringify(repParams));
				$("#res-lis-xls-markers").val(
					JSON.stringify(curMarkersAlObj));


				var msd = document.getElementById("medical-select-default");
				$("#graph-user-sel").empty();
				document.getElementById("graph-user-sel").appendChild(msd)

				for (var i = 0; i < allData.length; i++) {

					var genre = fmtMedicalGenreMale
					var userGenre = allData[i]["genre"]
					if (userGenre == 1)
						genre = fmtMedicalGenreFemale


					var el = new Option(allData[i]["name"] + " - (" + genre + ", " + allData[i]["age"] + ")", allData[i]["uuid"]);
					document.getElementById("graph-user-sel").appendChild(el)
				}

				$("#btn-resumen").fadeIn();
				$("#btn-listado").fadeIn();
				$("#btn-graficas").fadeIn();

				$("#btn-lst-alert").text(fmtNavAlerts + " (" + curMarkersAlObj.length + ")")
				$("#btn-res-detail").text(fmtNavSummary + " (" + curMarkers.length + ")")
				$("#btn-graphic").text(fmtNavMedical + " (" + curMarkers.length + ")")

			},
			error: function(response) {
				$('#loading').hide();
				var msg = "An error ocurred.\n\n"
				if (curLang == "es_ES")
					msg = "Se ha producido un error.\n\n"
				alert(msg + "Code: "
					+ response.status);
			}
		});

}

function compareStrings(a, b) {
	// Assuming you want case-insensitive comparison
	a = a.toLowerCase();
	b = b.toLowerCase();

	return (a < b) ? -1 : (a > b) ? 1 : 0;
}

function showActivities(data) {

	var red = "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_red.png";
	var green = "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_green.png";

	allData = []
	for (var i = 0; i < data.length; i++) {
		var a = data[i];

		var lat = a["location"]["lat"];
		var lng = a["location"]["lng"];

		var icon = red;

		var isTrueSet = (a["state"] === true);

		var status = "";
		if (isTrueSet) {
			icon = green;
		} else {
			icon = red;
		}

		var point = new google.maps.LatLng(lat, lng);
		curLatlng.push(point)

		// Points inside the circle
		if (google.maps.geometry.spherical.computeDistanceBetween(point,
			cityCircle.getCenter()) < cityCircle.getRadius()) {
			createMarker(map, point, a, icon);
			allData.push(a)
		}
	}

	// Summary
	var radius = $("#sliderValue").val();
	var minActivityTime = $("#slider-actValue").val();
	var duringActivityTime = $('input:radio[name="horasActividad"]:checked')
		.val();

	$("#res-par-rad").text(radius + " metros");
	$("#res-par-act").text(minActivityTime + " horas");
	$("#res-par-dur").text(duringActivityTime + " horas");

	$("#res-res-num").text(curMarkers.length);
	$("#res-lis-num").text(curMarkersAl.length);
	$("#res-res-ok").text(curMarkersOk.length);
	$("#res-res-ale").text(curMarkersAl.length);
	$("#res-res-env").text(curMarkersAl.length);

	markerCluster = clusterMap(map, curMarkers, markerCluster, 1);
	markerClusterOk = clusterMap(map, curMarkersOk, markerClusterOk, 1);
	markerClusterAl = clusterMap(map, curMarkersAl, markerClusterAl, 1);

	curHeatMapOk = new google.maps.visualization.HeatmapLayer({
		data: curLatlngOk,
		map: map,
		gradient: gradient
	});

	curHeatMapAl = new google.maps.visualization.HeatmapLayer({
		data: curLatlngAl,
		map: map
	});

	checkHeatmap();
	checkClustering();
	checkVis();

}

function setUserData(user) {

	$('#btn-chart-download').prop('disabled', false);
	$('#btn-validate-data').prop('disabled', false);

	for (var i = 0; i < allData.length; i++) {
		var u = allData[i];
		if (u["uuid"] == user[user.selectedIndex].value) {
			var md = new Array(u["healthParams"]);

			// Chart vars
			labels = [];
			chtrestbps = [];
			chfbs = [];
			chthalach = [];
			chthal = [];
			chtarget = [];
			chprediction = [];
			chname = u["name"];


			// Data vars
			var medicalData = []

			for (var j = 0; j < md[0].length; j++) {

				var timestamp = md[0][j]["timestamp"];
				var thresbps = md[0][j]["trestbps"];
				var fbs = md[0][j]["fbs"];
				var thalach = md[0][j]["thalach"];
				var thal = md[0][j]["thal"];
				var target = md[0][j]["target"];
				var precision = md[0][j]["precision"];
				var valid = "";

				valid = "<div class='form-check'><input class='form-check-input' type = 'checkbox' value = ''id = 'flexCheckDefault' > <label class='form-check-label' for='flexCheckDefault'></label></div>";

				var data = {
					"id": j + 1,
					"Timestamp": timestamp,
					"Trestbps": thresbps,
					"FBS": fbs,
					"Thalach": thalach,
					"Thal": thal,
					"Target": target,
					"Prediction (%)": parseFloat(precision).toFixed(2),
					"Reject": valid
				}

				labels.push(timestamp);
				chtrestbps.push(thresbps);
				chfbs.push(fbs);
				chthalach.push(thalach);
				chthal.push(thal);
				chtarget.push(target);
				chprediction.push(precision);

				medicalData.push(data)

			}

			$('#medical-data-tab').bootstrapTable('removeAll')
			$('#medical-data-tab').bootstrapTable('load', medicalData)

			// Configure chart
			configureChart();

			// Set validate data vars
			valUuid = user[user.selectedIndex].value
			valName = u["name"]
			valHealthData = md;
			valResource = "Retrain"

		}
	}
}

function configureChart() {
	// Configure chart
	const ctx = document.getElementById('medical-data-chart');

	if (medicalDataChart) {
		medicalDataChart.destroy();
	}

	medicalDataChart = new Chart(ctx, {
		type: 'line',
		data: {
			labels: labels,
			datasets: [{
				label: 'Trestbps',
				data: chtrestbps,
				borderWidth: 1
			}, {
				label: 'FBS',
				data: chfbs,
				borderWidth: 1
			}, {
				label: 'Thalach',
				data: chthalach,
				borderWidth: 1
			}, {
				label: 'Thal',
				data: chthal,
				borderWidth: 1
			}, {
				label: 'Target',
				data: chtarget,
				borderWidth: 1
			}, {
				label: 'Prediction',
				data: chprediction,
				borderWidth: 1
			}]
		},
		options: {
			scales: {
				y: {
					beginAtZero: true,
					padding: 20,
					height: 400,
					callback: function(value, index, values) {
						return value / 1000 + "k";
					}
				}, x: {
					ticks: {
						maxRotation: 90,
						minRotation: 90
					}
				}
			}, plugins: {
				title: {
					display: true,
					text: chname
				}
			}, options: {
				maintainAspectRatio: false
			}
		}
	});

}

$('#btn-chart-download').on('click', function(event) {
	var a = document.createElement('a');
	a.href = medicalDataChart.toBase64Image();
	a.download = 'Medical-data_' + chname + '.png';

	a.click();
});


function castToHoursAndMinutes(activityTime) {
	hours = 0;
	minutes = 0;

	if (activityTime >= 60) {
		hours = parseInt(activityTime / 60);
		if (activityTime % 60 != 0) {
			minutes = activityTime % 60;
			if (hours > 1)
				return hours + " " + fmtInfoActitityUnitHours + ", " + minutes + " " + fmtInfoActitityUnitMinutes;
			else
				return hours + " " + fmtInfoActitityUnitHour + ", " + minutes + " " + fmtInfoActitityUnitMinutes;
		} else {
			if (hours > 1)
				return hours + " " + fmtInfoActitityUnitHours;
			else
				return hours + " " + fmtInfoActitityUnitHour;
		}

	} else
		return activityTime + " " + fmtInfoActitityUnitMinutes;

}

function createMarker(map, point, a, icono) {

	var marker = new google.maps.Marker({
		position: point,
		map: map,
		icon: icono
	});

	infowindow = new google.maps.InfoWindow();

	var isTrueSet = (a["state"] === true);

	var status = "";
	if (isTrueSet) {

		curMarkers.push(marker);
		curLatlng.push(point);
		curMarkersOk.push(marker);
		curLatlngOk.push(point);
		curMarkersOkUUID.push(a["uuid"]);

		status = fmtInfoStatusOk;

		var act = castToHoursAndMinutes(a["activityTime"]);

		// var act = a["activityTime"] / 60;
		// act = act.toFixed(2);

		google.maps.event.addListener(marker, "click", function(evt) {
			infowindow.setContent("<h5>" + fmtInfoStatus + ": " + status + "</h5>"
				+ "</hr></br>" + "<b>" + fmtInfoActivity + ":</b> " + act + "<br/>"
				+ "<b>" + fmtInfoName + ":</b> " + a["name"] + "<br/>"
				+ "<b>" + fmtInfoAddress + ":</b> " + a["address"] + "<br/>"
				+ "<b>" + fmtInfoPostalCode + ":</b> " + a["postalAddress"] + "<br/>"
				+ "<b>" + fmtInfoPhone + ":</b> " + a["phone"] + "<br/>"
				+ "<b>" + fmtInfoCoords + ":</b></br> " + " - Lat: "
				+ a["location"]["lat"] + "<br/>" + " - Lng: "
				+ a["location"]["lng"]);
			infowindow.open(map, marker);
		});

	} else {

		curMarkers.push(marker);
		curMarkersAl.push(marker)
		curLatlngAl.push(point);
		curMarkersAlUUID.push(a["uuid"]);

		status = fmtInfoStatusAlert;

		var act = castToHoursAndMinutes(a["activityTime"]);

		// Create object
		var o = {
			"name": a["name"],
			"address": a["address"],
			"postalAddress": a["postalAddress"],
			"phone": a["phone"],
			"activityTime": act
		}
		curMarkersAlObj.push(o);

		google.maps.event
			.addListener(
				marker,
				"click",
				function(evt) {

					var eName = a["name"].toString();
					var eUuid = a["uuid"].toString();


					infowindow
						.setContent("<h5>" + fmtInfoStatus + ": "
							+ status
							+ "</h5>"
							+ "<button type='button' class='btn btn-warning btn-sm' id='"
							+ a["uuid"]
							+ "' onclick=" + "\"sendNotification('"
							+ eUuid + "','" + eName
							+ "')\">" + fmtInfoSendNotification + "</button><br/>"
							+ "</hr></br>"
							+ "<b>" + fmtInfoActivity + ":</b> " + act
							+ "<br/>" + "<br/>"
							+ "<b>" + fmtInfoName + ":</b> " + a["name"]
							+ "<br/>"
							+ "<b>" + fmtInfoAddress + ":</b> "
							+ a["address"] + "<br/>"
							+ "<b>" + fmtInfoPostalCode + ":</b> "
							+ a["postalAddress"] + "<br/>"
							+ "<b>" + fmtInfoPhone + ":</b> "
							+ a["phone"] + "<br/>"
							+ "<b>" + fmtInfoCoords + ":</b></br> "
							+ " - Lat: " + a["location"]["lat"]
							+ "<br/>" + " - Lng: "
							+ a["location"]["lng"]);
					infowindow.open(map, marker);
				});
	}
}

function sendNotification(uuid, name) {

	var msg = "You are going to send a notification to '" + name
		+ "'. Would you like to continue?"
	if (curLang == "es_ES")
		msg = "Va a enviar una notificación a '" + name
			+ "'. \u00BFDesea continuar?"

	var r = confirm(msg);
	if (r == true) {
		// Send ajax request
		$.ajax({
			url: "sendNotification",
			type: "GET",
			data: {
				"uuid": uuid,
				"curLang": curLang
			},
			success: function(data) {
				var msg = "Notification sent successfully!"
				if (curLang == "es_ES")
					msg = "\u00A1Notificaci\u00f3n enviada correctamente!"

				alert(msg);
			},
			error: function(response) {
				var msg = "An error has occurred.\n\n"
				if (curLang == "es_ES")
					msg = "Se ha producido un error.\n\n"
				alert(msg + "Code: "
					+ response.status);
			}
		});
	}
}

function sendNotifications() {

	var msg = "You are going to send " + curMarkersAlUUID.length
		+ " notifications. Would you like to continue?"
	if (curLang == "es_ES")
		msg = "Va a enviar " + curMarkersAlUUID.length
			+ " notificaciones. \u00BFDesea continuar?"

	var r = confirm(msg);
	if (r == true) {
		// Send ajax request
		var curMarkersAlStr = curMarkersAlUUID.toString();
		$.ajax({
			url: "sendNotifications",
			type: "GET",
			data: {
				"curMarkersAlUUID": curMarkersAlStr,
				"curLang": curLang
			},
			success: function(data) {
				var msg = "Notifications sent successfully"
				if (curLang == "es_ES")
					msg = "\u00A1Notificaciones enviadas correctamente!"
				alert(msg);
			},
			error: function(response) {
				var msg = "An error ocurred.\n\n"
				if (curLang == "es_ES")
					msg = "Se ha producido un error.\n\n"
				alert(msg + "Code: "
					+ response.status);
			}
		});
	}
}

// MARKERS FUNCTIONS //

// Enable/disable the clustering in the map. 0. Disable, 1. Enable
function clusterMap(map, markers, markerCluster, mode) {
	if (mode == 0) {
		clearMarkers(markers);
		markerCluster.clearMarkers();
		showMarkers(markers);

	} else {
		try {
			var markerCluster = new MarkerClusterer(
				map,
				markers,
				{
					imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
				});
		} catch (ex) {

		}

	}
	return markerCluster;
}

// Sets the map on all markers in the array.
function setMapOn(map, markers) {
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(map);
	}
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers(markers) {
	if (markers != null)
		setMapOn(null, markers);
}

// Shows any markers currently in the array.
function showMarkers(markers) {
	if (markers != null)
		setMapOn(map, markers);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {

	clearMarkers(curMarkers);
	clearMarkers(curMarkersOk);
	clearMarkers(curMarkersAl);

	try {
		markerCluster.clearMarkers();
	} catch (ex) {

	}

	try {
		markerClusterOk.clearMarkers();
	} catch (ex) {

	}

	try {
		markerClusterAl.clearMarkers();
	} catch (ex) {

	}

	try {
		curHeatMap.setMap(null);
	} catch (ex) {

	}
	try {
		curHeatMapOk.setMap(null);
	} catch (ex) {

	}
	try {
		curHeatMapAl.setMap(null);
	} catch (ex) {

	}

	curMarkers = [];
	curMarkersOk = [];
	curMarkersAl = [];
	curMarkersAlObj = [];

	curLatlng = [];
	curLatlngOk = [];
	curLatlngAl = [];

	curMarkersOkUUID = [];
	curMarkersAlUUID = [];
}