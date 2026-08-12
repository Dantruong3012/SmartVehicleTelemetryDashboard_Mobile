package com.dantruong.smartvehicletelemetrydashboard_mobile.domain.usecase

import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.repository.AppShutdownRepository
import javax.inject.Inject

class ShutdownAppUseCase @Inject constructor(
    private val appShutdownRepository: AppShutdownRepository
) {
    operator fun invoke() {
        appShutdownRepository.shutdownServices()
    }
}
