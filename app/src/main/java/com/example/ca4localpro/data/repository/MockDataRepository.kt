package com.example.ca4localpro.data.repository

import com.example.ca4localpro.data.model.*

interface ServiceRepository {
    suspend fun getCategories(): Result<List<ServiceCategory>>
    suspend fun getNearbyProviders(): Result<List<ServiceProvider>>
    suspend fun getRecommendedProviders(): Result<List<ServiceProvider>>
    suspend fun getEmergencyProviders(): Result<List<ServiceProvider>>
    suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>>
    suspend fun getProviderById(id: String): Result<ServiceProvider?>
    suspend fun searchProviders(query: String): Result<List<ServiceProvider>>
}

class MockServiceRepository : ServiceRepository {

    private val categories = listOf(
        ServiceCategory("plumbing", "Plumbing", "Pipes, leaks, taps & drainage", "Plumbing", 18),
        ServiceCategory("electrical", "Electrical", "Wiring, switches, lights & panels", "ElectricalServices", 24),
        ServiceCategory("ac_repair", "AC Repair", "Cooling, water leak, gas refill & service", "AcUnit", 15),
        ServiceCategory("cleaning", "Cleaning", "Deep home, sofa, kitchen & office cleaning", "CleaningServices", 30),
        ServiceCategory("mechanic", "Mechanics", "Car & bike repair, battery & oil change", "Build", 12),
        ServiceCategory("tutoring", "Tutoring", "Math, Science, English & test prep", "School", 20),
        ServiceCategory("beauty", "Beauty & Spa", "Haircut, facial, makeup & massage", "Face", 25),
        ServiceCategory("photography", "Photography", "Events, portraits & wedding coverage", "PhotoCamera", 10)
    )

    private val providers = listOf(
        ServiceProvider(
            id = "p1",
            name = "Raj Plumbing & Sanitary Work",
            categoryId = "plumbing",
            categoryName = "Plumbing",
            rating = 4.8f,
            reviewCount = 142,
            distanceKm = 1.8,
            startingPrice = 299.0,
            isVerified = true,
            isEmergencyAvailable = true,
            yearsExperience = 8,
            bio = "Certified master plumber specializing in residential leak repairs, bathroom fittings, tap installations, and drainage solutions. 100% satisfaction guaranteed.",
            locationAddress = "Sector 14, Metro Area - 1.8 km away",
            phone = "+91 98765 43210",
            servicesOffered = listOf(
                ServiceItem("s101", "Leakage Repair", "Fixing pipe leaks & water seepage", 349.0, "45 mins"),
                ServiceItem("s102", "Tap & Mixer Installation", "Installation of kitchen or bathroom taps", 299.0, "30 mins"),
                ServiceItem("s103", "Full Drain Unclogging", "Clearing blocked pipes & sinks", 499.0, "60 mins")
            ),
            reviews = listOf(
                Review("r1", "Amit Sharma", 5.0f, "Yesterday", "Arrived within 20 mins! Fixed my kitchen pipe leak cleanly."),
                Review("r2", "Priya Verma", 4.5f, "3 days ago", "Very polite and professional. Fair pricing.")
            )
        ),
        ServiceProvider(
            id = "p2",
            name = "QuickCool AC Services",
            categoryId = "ac_repair",
            categoryName = "AC Repair",
            rating = 4.7f,
            reviewCount = 98,
            distanceKm = 2.4,
            startingPrice = 499.0,
            isVerified = true,
            isEmergencyAvailable = true,
            yearsExperience = 6,
            bio = "Expert AC technician for split & window ACs. Gas charging, jet wash, water leak fixes, and PCB repairs.",
            locationAddress = "Green Park Avenue - 2.4 km away",
            phone = "+91 98123 45678",
            servicesOffered = listOf(
                ServiceItem("s201", "AC Water Leak Repair", "Deep cleaning drain tray & pipe clearing", 499.0, "45 mins"),
                ServiceItem("s202", "Full Jet Servicing", "Indoor & outdoor foam jet wash", 699.0, "60 mins"),
                ServiceItem("s203", "Gas Refill (R32/R410)", "Complete refrigerant top-up with leak check", 1499.0, "60 mins")
            ),
            reviews = listOf(
                Review("r3", "Rohan Mehta", 5.0f, "4 days ago", "Fixed my leaking AC quickly during heatwave! Top notch."),
                Review("r4", "Sneha Kapoor", 4.5f, "1 week ago", "Cooling is back like brand new.")
            )
        ),
        ServiceProvider(
            id = "p3",
            name = "SparkFix Electrical Solutions",
            categoryId = "electrical",
            categoryName = "Electrical",
            rating = 4.9f,
            reviewCount = 210,
            distanceKm = 1.2,
            startingPrice = 199.0,
            isVerified = true,
            isEmergencyAvailable = true,
            yearsExperience = 10,
            bio = "Licensed electrical engineer. Circuit breaker replacement, short circuit fixing, fan installation, and whole house rewiring.",
            locationAddress = "Central Market Road - 1.2 km away",
            phone = "+91 99887 76655",
            servicesOffered = listOf(
                ServiceItem("s301", "Short Circuit Inspection", "Complete diagnostic & fuse replacement", 299.0, "30 mins"),
                ServiceItem("s302", "Ceiling Fan Fitting", "Assembly and mounting on ceiling", 199.0, "30 mins"),
                ServiceItem("s303", "Switchboard Upgrade", "Replacement with modular switches", 399.0, "45 mins")
            ),
            reviews = listOf(
                Review("r5", "Vikram Singh", 5.0f, "2 days ago", "Extremely skilled. Solved a complex wiring fault instantly.")
            )
        ),
        ServiceProvider(
            id = "p4",
            name = "CleanCare Home Deep Cleaning",
            categoryId = "cleaning",
            categoryName = "Cleaning",
            rating = 4.6f,
            reviewCount = 85,
            distanceKm = 3.1,
            startingPrice = 799.0,
            isVerified = true,
            isEmergencyAvailable = false,
            yearsExperience = 5,
            bio = "Professional eco-friendly cleaning team equipped with industrial vacuums & germ-kill steam sanitizers.",
            locationAddress = "Civil Lines - 3.1 km away",
            phone = "+91 97654 32109",
            servicesOffered = listOf(
                ServiceItem("s401", "Sofa Shampooing (3 Seater)", "Deep vacuuming & stain removal", 799.0, "90 mins"),
                ServiceItem("s402", "Kitchen Deep Clean", "Chimney, degreasing & cabinet cleaning", 1299.0, "120 mins")
            ),
            reviews = listOf(
                Review("r6", "Neha Gupta", 4.5f, "5 days ago", "House smells fresh and clean!")
            )
        ),
        ServiceProvider(
            id = "p5",
            name = "AutoPro Mobile Mechanic",
            categoryId = "mechanic",
            categoryName = "Mechanics",
            rating = 4.8f,
            reviewCount = 115,
            distanceKm = 2.9,
            startingPrice = 399.0,
            isVerified = true,
            isEmergencyAvailable = true,
            yearsExperience = 9,
            bio = "Doorstep car & bike breakdown support, battery jump-start, oil change, and brake servicing.",
            locationAddress = "Highway Bypass Hub - 2.9 km away",
            phone = "+91 95432 10987",
            servicesOffered = listOf(
                ServiceItem("s501", "Emergency Jump Start", "Battery boost & alternator check", 399.0, "20 mins"),
                ServiceItem("s502", "Full Synthetic Oil Change", "Oil filter replacement & engine check", 1199.0, "45 mins")
            ),
            reviews = listOf(
                Review("r7", "Karan Malhotra", 5.0f, "1 week ago", "Rescued me when my battery died on the road!")
            )
        ),
        ServiceProvider(
            id = "p6",
            name = "Aura Beauty & Home Salon",
            categoryId = "beauty",
            categoryName = "Beauty & Spa",
            rating = 4.9f,
            reviewCount = 175,
            distanceKm = 1.5,
            startingPrice = 499.0,
            isVerified = true,
            isEmergencyAvailable = false,
            yearsExperience = 7,
            bio = "Premium salon experience at your home. Organic facials, threading, waxing, hair styling & nail art.",
            locationAddress = "Model Town - 1.5 km away",
            phone = "+91 94321 09876",
            servicesOffered = listOf(
                ServiceItem("s601", "Glow Facial & De-tan", "Deep pore cleansing and hydration", 899.0, "60 mins"),
                ServiceItem("s602", "Hair Styling & Blow Dry", "Professional salon blow dry & setting", 499.0, "45 mins")
            ),
            reviews = listOf(
                Review("r8", "Ananya Roy", 5.0f, "3 days ago", "Super hygienic and professional services!")
            )
        )
    )

    override suspend fun getCategories(): Result<List<ServiceCategory>> = Result.success(categories)

    override suspend fun getNearbyProviders(): Result<List<ServiceProvider>> = Result.success(providers.sortedBy { it.distanceKm })

    override suspend fun getRecommendedProviders(): Result<List<ServiceProvider>> = Result.success(providers.sortedByDescending { it.rating })

    override suspend fun getEmergencyProviders(): Result<List<ServiceProvider>> = Result.success(providers.filter { it.isEmergencyAvailable })

    override suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>> {
        return Result.success(providers.filter { it.categoryId.equals(categoryId, ignoreCase = true) })
    }

    override suspend fun getProviderById(id: String): Result<ServiceProvider?> {
        return Result.success(providers.find { it.id == id })
    }

    override suspend fun searchProviders(query: String): Result<List<ServiceProvider>> {
        if (query.isBlank()) return Result.success(providers)
        val q = query.lowercase().trim()
        return Result.success(providers.filter {
            it.name.lowercase().contains(q) ||
            it.categoryName.lowercase().contains(q) ||
            it.bio.lowercase().contains(q) ||
            it.servicesOffered.any { s -> s.title.lowercase().contains(q) }
        })
    }
}


