package com.sistemas.herrerasystemmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Amount and unit fields accept the common names used by the reports projections. */
data class SalesByLineFlavorDto(
    @SerializedName("lineName") val lineName: String = "",
    @SerializedName("flavorName") val flavorName: String = "",
    @SerializedName(value = "unitsSold", alternate = ["quantitySold", "totalUnits"]) val unitsSold: Double = 0.0,
    @SerializedName(value = "amount", alternate = ["totalSales", "salesAmount"]) val amount: Double = 0.0
)

data class MonthlySalesTrendDto(
    @SerializedName("year") val year: Int = 0,
    @SerializedName("month") val month: Int = 0,
    @SerializedName("lineName") val lineName: String = "",
    @SerializedName(value = "unitsSold", alternate = ["quantitySold", "totalUnits"]) val unitsSold: Double = 0.0
)

data class SalesByGeographyDto(
    @SerializedName("departmentName") val departmentName: String = "",
    @SerializedName("municipalityName") val municipalityName: String = "",
    @SerializedName(value = "unitsSold", alternate = ["quantitySold", "totalUnits"]) val unitsSold: Double = 0.0,
    @SerializedName(value = "amount", alternate = ["totalSales", "salesAmount"]) val amount: Double = 0.0
)

data class TopCustomerDto(
    @SerializedName(value = "customerName", alternate = ["clientName", "fullName"]) val customerName: String = "",
    @SerializedName(value = "unitsBought", alternate = ["unitsSold", "quantityBought", "totalUnits"]) val unitsBought: Double = 0.0,
    @SerializedName(value = "amount", alternate = ["totalSales", "totalSpent"]) val amount: Double = 0.0
)

data class SalesByPresentationDto(
    @SerializedName("presentationName") val presentationName: String = "",
    @SerializedName("lineName") val lineName: String = "",
    @SerializedName(value = "unitsSold", alternate = ["quantitySold", "totalUnits"]) val unitsSold: Double = 0.0,
    @SerializedName(value = "participationPercentage", alternate = ["percentage", "sharePercentage"]) val participationPercentage: Double = 0.0
)
