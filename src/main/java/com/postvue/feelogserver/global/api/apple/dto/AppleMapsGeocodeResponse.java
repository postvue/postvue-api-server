package com.postvue.feelogserver.global.api.apple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AppleMapsGeocodeResponse {

    @JsonProperty("results")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        @JsonProperty("coordinate")
        private Coordinate coordinate;

        @JsonProperty("displayMapRegion")
        private DisplayMapRegion displayMapRegion;

        @JsonProperty("name")
        private String name;

        @JsonProperty("formattedAddressLines")
        private List<String> formattedAddressLines;

        @JsonProperty("structuredAddress")
        private StructuredAddress structuredAddress;

        @JsonProperty("country")
        private String country;

        @JsonProperty("countryCode")
        private String countryCode;

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public DisplayMapRegion getDisplayMapRegion() {
            return displayMapRegion;
        }

        public String getName() {
            return name;
        }

        public List<String> getFormattedAddressLines() {
            return formattedAddressLines;
        }

        public StructuredAddress getStructuredAddress() {
            return structuredAddress;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    public static class Coordinate {
        @JsonProperty("latitude")
        private double latitude;

        @JsonProperty("longitude")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public static class DisplayMapRegion {
        @JsonProperty("southLatitude")
        private double southLatitude;

        @JsonProperty("westLongitude")
        private double westLongitude;

        @JsonProperty("northLatitude")
        private double northLatitude;

        @JsonProperty("eastLongitude")
        private double eastLongitude;

        public double getSouthLatitude() {
            return southLatitude;
        }

        public double getWestLongitude() {
            return westLongitude;
        }

        public double getNorthLatitude() {
            return northLatitude;
        }

        public double getEastLongitude() {
            return eastLongitude;
        }
    }

    public static class StructuredAddress {
        @JsonProperty("administrativeArea")
        private String administrativeArea;

        @JsonProperty("administrativeAreaCode")
        private String administrativeAreaCode;

        @JsonProperty("locality")
        private String locality;

        @JsonProperty("postCode")
        private String postCode;

        @JsonProperty("thoroughfare")
        private String thoroughfare;

        @JsonProperty("fullThoroughfare")
        private String fullThoroughfare;

        @JsonProperty("areasOfInterest")
        private List<String> areasOfInterest;

        public String getAdministrativeArea() {
            return administrativeArea;
        }

        public String getAdministrativeAreaCode() {
            return administrativeAreaCode;
        }

        public String getLocality() {
            return locality;
        }

        public String getPostCode() {
            return postCode;
        }

        public String getThoroughfare() {
            return thoroughfare;
        }

        public String getFullThoroughfare() {
            return fullThoroughfare;
        }

        public List<String> getAreasOfInterest() {
            return areasOfInterest;
        }
    }
}
