package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.ISdgsRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.SdgsRepository
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.SdgsService
import org.koin.dsl.module

val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    // SDGs Repository
    single<ISdgsRepository> {
        SdgsRepository()
    }

    // SDGs Service
    single {
        SdgsService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}