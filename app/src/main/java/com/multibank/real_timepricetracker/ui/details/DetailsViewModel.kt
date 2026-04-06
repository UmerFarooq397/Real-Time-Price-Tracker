package com.multibank.real_timepricetracker.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.real_timepricetracker.data.repository.PriceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PriceRepository
) : ViewModel() {

    val symbol: String = checkNotNull(savedStateHandle["symbol"])

    val uiState: StateFlow<DetailsUiState> = repository.stockItems
        .map { stocks ->
            DetailsUiState(
                stock = stocks.find { it.symbol == symbol },
                description = stockDescriptions[symbol] ?: "No description available."
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailsUiState(description = stockDescriptions[symbol] ?: "")
        )

    companion object {
        val stockDescriptions = mapOf(
            "AAPL" to "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide. The company is known for its iPhone, Mac, iPad, Apple Watch, and Apple TV product lines.",
            "GOOG" to "Alphabet Inc. is the parent company of Google, providing online advertising services, cloud computing, software, and hardware. Google Search handles over 8.5 billion queries per day across the globe.",
            "TSLA" to "Tesla, Inc. designs, develops, manufactures, leases, and sells electric vehicles, energy generation and storage systems. It operates Gigafactories across the US, China, and Europe.",
            "AMZN" to "Amazon.com, Inc. engages in the retail sale of consumer products and subscriptions through its online stores and physical stores. AWS is the world's leading cloud computing platform.",
            "MSFT" to "Microsoft Corporation develops and supports software, services, devices, and solutions worldwide. Products include Windows, Office 365, Azure cloud, Xbox, and LinkedIn.",
            "NVDA" to "NVIDIA Corporation provides graphics, compute and networking solutions. Its GPUs are the backbone of AI model training and inference, making it central to the modern AI revolution.",
            "META" to "Meta Platforms, Inc. develops products that enable people to connect and share through mobile devices and personal computers. Operates Facebook, Instagram, WhatsApp, and Threads.",
            "NFLX" to "Netflix, Inc. provides entertainment services. It offers TV series, documentaries, feature films, and games across various genres and languages. Operates in over 190 countries.",
            "AMD" to "Advanced Micro Devices, Inc. operates as a semiconductor company. Its products include microprocessors, graphics processing units, and data center solutions competing directly with Intel and NVIDIA.",
            "INTC" to "Intel Corporation designs, manufactures, and sells computer components and related products. It is one of the world's largest and highest-valued semiconductor chip manufacturers.",
            "PYPL" to "PayPal Holdings, Inc. operates a technology platform that enables digital payments on behalf of merchants and consumers worldwide. It processes billions of payment transactions annually.",
            "ADBE" to "Adobe Inc. operates as a diversified software company. It offers creative, marketing, and document management solutions through products like Photoshop, Illustrator, and Acrobat.",
            "CRM"  to "Salesforce, Inc. provides customer relationship management (CRM) software and applications. It is the world's number one CRM platform, helping businesses connect with customers.",
            "ORCL" to "Oracle Corporation offers products and services that address enterprise information technology environments worldwide, including database software, cloud infrastructure, and enterprise applications.",
            "IBM"  to "International Business Machines Corporation provides integrated solutions and services using hybrid cloud, AI, quantum computing, and security capabilities for enterprise clients.",
            "QCOM" to "Qualcomm Incorporated develops and commercializes foundational technologies for the wireless industry. Its Snapdragon chips power a large share of the world's Android smartphones.",
            "TXN"  to "Texas Instruments Incorporated designs, manufactures, and sells semiconductors to electronics designers and manufacturers globally. Known for analog and embedded processing chips.",
            "AVGO" to "Broadcom Inc. designs, develops, and supplies a broad range of semiconductor, enterprise software, and security solutions. A key supplier for data center networking and storage.",
            "MU"   to "Micron Technology, Inc. produces and markets memory and storage products. It is one of the largest manufacturers of DRAM and NAND flash memory chips in the world.",
            "NOW"  to "ServiceNow, Inc. provides cloud-based platforms and solutions. It digitizes workflows to help businesses move faster, scale, and work more efficiently across the enterprise.",
            "SNOW" to "Snowflake Inc. provides a cloud-based data platform. It enables organizations to mobilize their data with the Snowflake Data Cloud, which includes data warehouse and data lake capabilities.",
            "UBER" to "Uber Technologies, Inc. develops and operates proprietary technology applications in the United States and internationally. Operates ride-sharing, food delivery (Uber Eats), and freight businesses.",
            "LYFT" to "Lyft, Inc. operates a peer-to-peer marketplace for on-demand ridesharing in the United States and Canada. It connects drivers with passengers through its mobile application.",
            "SPOT" to "Spotify Technology S.A. provides audio streaming subscription services. It is the world's largest music streaming platform with over 600 million monthly active users.",
            "COIN" to "Coinbase Global, Inc. provides financial infrastructure and technology for the cryptoeconomy. It operates the largest cryptocurrency exchange in the United States by trading volume."
        )
    }
}
