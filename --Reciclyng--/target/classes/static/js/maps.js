	  var marker;

	  function initMap() {
		  
		  var styledMapType = new google.maps.StyledMapType(
				  [
					  {
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#ebe3cd"
					      }
					    ]
					  },
					  {
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#523735"
					      }
					    ]
					  },
					  {
					    "elementType": "labels.text.stroke",
					    "stylers": [
					      {
					        "color": "#f5f1e6"
					      }
					    ]
					  },
					  {
					    "featureType": "administrative",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "visibility": "off"
					      }
					    ]
					  },
					  {
					    "featureType": "administrative",
					    "elementType": "geometry.stroke",
					    "stylers": [
					      {
					        "color": "#c9b2a6"
					      }
					    ]
					  },
					  {
					    "featureType": "administrative.land_parcel",
					    "elementType": "geometry.stroke",
					    "stylers": [
					      {
					        "color": "#dcd2be"
					      }
					    ]
					  },
					  {
					    "featureType": "administrative.land_parcel",
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#ae9e90"
					      }
					    ]
					  },
					  {
					    "featureType": "landscape.natural",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#dfd2ae"
					      }
					    ]
					  },
					  {
					    "featureType": "poi",
					    "stylers": [
					      {
					        "visibility": "off"
					      }
					    ]
					  },
					  {
					    "featureType": "poi",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#dfd2ae"
					      }
					    ]
					  },
					  {
					    "featureType": "poi",
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#93817c"
					      }
					    ]
					  },
					  {
					    "featureType": "poi.park",
					    "elementType": "geometry.fill",
					    "stylers": [
					      {
					        "color": "#a5b076"
					      }
					    ]
					  },
					  {
					    "featureType": "poi.park",
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#447530"
					      }
					    ]
					  },
					  {
					    "featureType": "road",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#f5f1e6"
					      }
					    ]
					  },
					  {
					    "featureType": "road",
					    "elementType": "labels.icon",
					    "stylers": [
					      {
					        "visibility": "off"
					      }
					    ]
					  },
					  {
					    "featureType": "road.arterial",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#fdfcf8"
					      }
					    ]
					  },
					  {
					    "featureType": "road.highway",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#f8c967"
					      }
					    ]
					  },
					  {
					    "featureType": "road.highway",
					    "elementType": "geometry.stroke",
					    "stylers": [
					      {
					        "color": "#e9bc62"
					      }
					    ]
					  },
					  {
					    "featureType": "road.highway.controlled_access",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#e98d58"
					      }
					    ]
					  },
					  {
					    "featureType": "road.highway.controlled_access",
					    "elementType": "geometry.stroke",
					    "stylers": [
					      {
					        "color": "#db8555"
					      }
					    ]
					  },
					  {
					    "featureType": "road.local",
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#806b63"
					      }
					    ]
					  },
					  {
					    "featureType": "transit",
					    "stylers": [
					      {
					        "visibility": "off"
					      }
					    ]
					  },
					  {
					    "featureType": "transit.line",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#dfd2ae"
					      }
					    ]
					  },
					  {
					    "featureType": "transit.line",
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#8f7d77"
					      }
					    ]
					  },
					  {
					    "featureType": "transit.line",
					    "elementType": "labels.text.stroke",
					    "stylers": [
					      {
					        "color": "#ebe3cd"
					      }
					    ]
					  },
					  {
					    "featureType": "transit.station",
					    "elementType": "geometry",
					    "stylers": [
					      {
					        "color": "#dfd2ae"
					      }
					    ]
					  },
					  {
					    "featureType": "water",
					    "elementType": "geometry.fill",
					    "stylers": [
					      {
					        "color": "#b9d3c2"
					      }
					    ]
					  },
					  {
					    "featureType": "water",
					    "elementType": "labels.text.fill",
					    "stylers": [
					      {
					        "color": "#92998d"
					      }
					    ]
					  }
					],
				    { name: "Iris" }
				  );

		  
        var mapa = new google.maps.Map(document.getElementById('map'), {
          zoom: 17,
          center: {lat: -32.8813368, lng: -68.8478019},
          mapTypeControlOptions: {
              mapTypeIds: ["roadmap", "styled_map"]
            }

        });
        
        
        var styles = {
	        default: null,
	        hide: [
	          {
	            featureType: "poi.business",
	            stylers: [{ visibility: "off" }]
	          },
	          {
	            featureType: "transit",
	            elementType: "labels.icon",
	            stylers: [{ visibility: "off" }]
	          }
	        ]
        };
        
        mapa.mapTypes.set("styled_map", styledMapType);
        mapa.setMapTypeId("styled_map");

        marker = new google.maps.Marker({
            map: mapa
        });
        
        
        mapa.addListener('click', function(mapsMouseEvent) {
        	$('#latitud').val(mapsMouseEvent.latLng.lat());
            $('#longitud').val(mapsMouseEvent.latLng.lng());
        	marker.setPosition(mapsMouseEvent.latLng);
        });

        var geocoder = new google.maps.Geocoder();

        document.getElementById('geocode').addEventListener('click', function() {
          geocodeAddress(geocoder, mapa);
        });
        
        geocodeAddress(geocoder, mapa);
        
        
        
      }

      function geocodeAddress(geocoder, resultsMap) {
    	  
    	var departamento = document.getElementById('departamento');
    	var departamentoId = departamento.options[departamento.selectedIndex].text;
    	
    	var provincia = document.getElementById('provincia');
    	var provinciaId = provincia.options[provincia.selectedIndex].text;
    	
    	var pais = document.getElementById('pais');
    	var paisId = pais.options[pais.selectedIndex].text;
    	
        var address = document.getElementById('calle').value + ' ' + document.getElementById('numeracion').value + ',' + departamentoId + ','+ provinciaId + ',' + paisId;
        geocoder.geocode({'address': address}, function(results, status) {
          if (status === 'OK') {
            resultsMap.setCenter(results[0].geometry.location);
            
            $('#latitud').val(results[0].geometry.location.lat());
            $('#longitud').val(results[0].geometry.location.lng());
            
            marker.setPosition(results[0].geometry.location);
          } else {
        	  console.log('El dominicilio no fue encontrado. ' + status);
          }
        });
      }

      
      