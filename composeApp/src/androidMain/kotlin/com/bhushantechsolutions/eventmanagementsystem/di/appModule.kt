package com.bhushantechsolutions.eventmanagementsystem.di

import com.bhushantechsolutions.eventmanagementsystem.data.network.EventRepository
import com.bhushantechsolutions.eventmanagementsystem.data.viewModel.EventsViewModel
import com.bhushantechsolutions.eventmanagementsystem.data.network.ktorClient
import org.koin.dsl.module

val appModule = module {

    // provide ktor client
    single { ktorClient }

    // provide repository
    single { EventRepository(get()) }

    // provide ViewModel
    single { EventsViewModel(get()) }
}
